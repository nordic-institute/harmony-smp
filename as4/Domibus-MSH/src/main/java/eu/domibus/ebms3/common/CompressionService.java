/*
 * Copyright 2015 e-CODEX Project
 *
 * Licensed under the EUPL, Version 1.1 or – as soon they
 * will be approved by the European Commission - subsequent
 * versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the
 * Licence.
 * You may obtain a copy of the Licence at:
 * http://ec.europa.eu/idabc/eupl5
 * Unless required by applicable law or agreed to in
 * writing, software distributed under the Licence is
 * distributed on an "AS IS" basis,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either
 * express or implied.
 * See the Licence for the specific language governing
 * permissions and limitations under the Licence.
 */

package eu.domibus.ebms3.common;

import eu.domibus.common.MSHRole;
import eu.domibus.common.configuration.model.LegConfiguration;
import eu.domibus.common.exception.EbMS3Exception;
import eu.domibus.common.model.org.oasis_open.docs.ebxml_msg.ebms.v3_0.ns.core._200704.PartInfo;
import eu.domibus.common.model.org.oasis_open.docs.ebxml_msg.ebms.v3_0.ns.core._200704.Property;
import eu.domibus.common.model.org.oasis_open.docs.ebxml_msg.ebms.v3_0.ns.core._200704.UserMessage;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.*;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

/**
 * This class is responsible for compression handling of incoming and outgoing ebMS3 messages.
 *
 * @author Stefan Müller
 * @since 3.0
 */
@Service
public class CompressionService {

    public static final String COMPRESSION_PROPERTY_KEY = "CompressionType";
    public static final String COMPRESSION_PROPERTY_VALUE = "application/gzip";
    private static final Log LOG = LogFactory.getLog(CompressionService.class);
    @Autowired
    private CompressionMimeTypeBlacklist blacklist;


    /**
     * This method is responsible for compression of payloads in a ebMS3 AS4 comformant way in case of {@link eu.domibus.common.MSHRole#SENDING}
     *
     * @param ebmsMessage         the sending {@link eu.domibus.common.model.org.oasis_open.docs.ebxml_msg.ebms.v3_0.ns.core._200704.UserMessage} with all payloads
     * @param legConfigForMessage legconfiguration for this message
     * @return {@code true} if compression was applied properly and {@code false} if compression was not enabled in the corresponding pmode
     * @throws EbMS3Exception if an problem occurs during the compression or the mimetype was missing
     */
    public boolean handleCompression(UserMessage ebmsMessage, LegConfiguration legConfigForMessage) throws EbMS3Exception {
        //if compression is not necessary return false
        if (!legConfigForMessage.isCompressPayloads()) {
            return false;
        }

        for (PartInfo partInfo : ebmsMessage.getPayloadInfo().getPartInfo()) {
            if (partInfo.isInBody()) {
                continue;
            }

            String mimeType = null;
            for (Property property : partInfo.getPartProperties().getProperties()) {
                if (Property.MIME_TYPE.equals(property.getName())) {
                    mimeType = property.getValue();
                    break;
                }
            }

            if (mimeType == null || mimeType.isEmpty()) {
                throw new EbMS3Exception(EbMS3Exception.EbMS3ErrorCode.EBMS_0007, "No mime type found for payload with cid:" + partInfo.getHref(), null, MSHRole.SENDING);
            }

            //if mimetype of payload is not considered to be compressed, skip
            if (this.blacklist.getEntries().contains(mimeType)) {
                continue;
            }

            try {
                partInfo.setBinaryData(this.compress(partInfo.getBinaryData()));
            } catch (IOException e) {
                throw new EbMS3Exception(EbMS3Exception.EbMS3ErrorCode.EBMS_0004, "Problem during compression of payload occured", e, MSHRole.SENDING);
            }

            Property compressionProperty = new Property();
            compressionProperty.setName(CompressionService.COMPRESSION_PROPERTY_KEY);
            compressionProperty.setValue(CompressionService.COMPRESSION_PROPERTY_VALUE);
            partInfo.getPartProperties().getProperties().add(compressionProperty);
            CompressionService.LOG.debug("Payload with cid: " + partInfo.getHref() + " and mime type: " + mimeType + " was compressed");
        }

        return true;
    }

    /**
     * This method handles decompression of payloads for messages in case of {@link eu.domibus.common.MSHRole#RECEIVING}
     *
     * @param ebmsMessage the receving {@link eu.domibus.common.model.org.oasis_open.docs.ebxml_msg.ebms.v3_0.ns.core._200704.UserMessage} with all payloads
     * @return {@code true} if everything was decompressed without problems, {@code false} in case of disabled compression via pmode
     * @throws EbMS3Exception if an problem occurs during the de compression or the mimetype of a compressed payload was missing
     */
    public boolean handleDecompression(UserMessage ebmsMessage, LegConfiguration legConfigForMessage) throws EbMS3Exception {
        //if compression is not necessary return false
        if (!legConfigForMessage.isCompressPayloads()) {
            return false;
        }

        for (PartInfo partInfo : ebmsMessage.getPayloadInfo().getPartInfo()) {
            if (partInfo.isInBody()) {
                continue;
            }

            String mimeType = null;
            boolean payloadCompressed = false;

            for (Property property : partInfo.getPartProperties().getProperties()) {
                if (Property.MIME_TYPE.equals(property.getName())) {
                    mimeType = property.getValue();
                }
                if (CompressionService.COMPRESSION_PROPERTY_KEY.equals(property.getName()) && CompressionService.COMPRESSION_PROPERTY_VALUE.equals(property.getValue())) {
                    payloadCompressed = true;
                }
            }

            if (!payloadCompressed) {
                continue;
            }

            Property compressionProperty = new Property();
            compressionProperty.setName(CompressionService.COMPRESSION_PROPERTY_KEY);
            compressionProperty.setValue(CompressionService.COMPRESSION_PROPERTY_VALUE);
            partInfo.getPartProperties().getProperties().remove(compressionProperty);

            if (mimeType == null) {
                throw new EbMS3Exception(EbMS3Exception.EbMS3ErrorCode.EBMS_0007, "No mime type found for payload with cid:" + partInfo.getHref(), null, MSHRole.RECEIVING);
            }

            try {
                partInfo.setBinaryData(this.decompress(partInfo.getBinaryData()));
            } catch (IOException e) {
                throw new EbMS3Exception(EbMS3Exception.EbMS3ErrorCode.EBMS_0303, "Problem during decompression of payload occured", e, MSHRole.RECEIVING);
            }

            CompressionService.LOG.debug("Payload with cid: " + partInfo.getHref() + " and mime type: " + mimeType + " was decompressed");
        }

        return true;
    }


    /**
     * Compress given Stream via GZIP [RFC1952].
     *
     * @param sourceStream Stream of uncompressed data
     * @param targetStream Stream of compressed data
     * @throws IOException
     */
    private void compress(final InputStream sourceStream, final GZIPOutputStream targetStream) throws IOException {
        final byte[] buffer = new byte[1024];

        try {
            int i;
            while ((i = sourceStream.read(buffer)) > 0) {
                targetStream.write(buffer, 0, i);
            }

            sourceStream.close();

            targetStream.finish();
            targetStream.close();

        } catch (IOException e) {
            CompressionService.LOG.error("I/O exception during gzip compression. method: compress(Inputstream, GZIPOutputStream)", e);
            throw e;
        }
    }

    /**
     * Decompress given GZIP Stream. Separated from {@link eu.domibus.ebms3.common.CompressionService#compress(java.io.InputStream, java.util.zip.GZIPOutputStream)}
     * just for a better overview even though they share the same logic (except finish() ).
     *
     * @param sourceStream Stream of compressed data
     * @param targetStream Stream of uncompressed data
     * @throws IOException
     */
    private void decompress(final GZIPInputStream sourceStream, final OutputStream targetStream)
            throws IOException {

        final byte[] buffer = new byte[1024];

        try {
            int i;
            while ((i = sourceStream.read(buffer)) > 0) {
                targetStream.write(buffer, 0, i);
            }

            sourceStream.close();
            targetStream.close();

        } catch (IOException e) {
            CompressionService.LOG.error("I/O exception during gzip compression. method: doDecompress(GZIPInputStream, OutputStream");
            throw e;
        }
    }

    /**
     * Compresses the given byte[].
     *
     * @param uncompressed the byte[] to compress
     * @return the compressed byte[]
     * @throws java.lang.NullPointerException if the payload has no content a {@link java.lang.NullPointerException}
     *                                        is thrown
     * @throws java.io.IOException            if problem during gzip compression occurs a {@link java.io.IOException} is thrown
     */
    private byte[] compress(byte[] uncompressed) throws IOException, NullPointerException {
        if (uncompressed == null) {
            throw new NullPointerException("Payload was null");
        }

        ByteArrayOutputStream compressedContent = new ByteArrayOutputStream();

        try {
            this.compress(new ByteArrayInputStream(uncompressed), new GZIPOutputStream(compressedContent));
        } catch (IOException e) {
            CompressionService.LOG.error("", e);
            throw e;
        }

        return compressedContent.toByteArray();
    }

    /**
     * Decompresses the given byte[].
     *
     * @param compressed the byte[] to decompress
     * @return the decompressed byte[]
     * @throws java.lang.NullPointerException if the payload has no content a {@link java.lang.NullPointerException}
     *                                        is thrown
     * @throws java.io.IOException            if problem during gzip compression occurs a {@link java.io.IOException} is thrown
     */
    private byte[] decompress(byte[] compressed) throws IOException, NullPointerException {
        if (compressed == null) {
            throw new NullPointerException("Payload was null");
        }

        ByteArrayInputStream compressedContent = new ByteArrayInputStream(compressed);
        ByteArrayOutputStream decompressedContent = new ByteArrayOutputStream();

        try {
            this.decompress(new GZIPInputStream(compressedContent), decompressedContent);
        } catch (IOException e) {
            CompressionService.LOG.error("", e);
            throw e;
        }

        return decompressedContent.toByteArray();
    }


}
