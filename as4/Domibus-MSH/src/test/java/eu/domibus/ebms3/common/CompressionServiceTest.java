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

import eu.domibus.common.configuration.model.LegConfiguration;
import eu.domibus.common.exception.EbMS3Exception;
import eu.domibus.common.exception.ErrorCode;
import eu.domibus.common.model.org.oasis_open.docs.ebxml_msg.ebms.v3_0.ns.core._200704.*;
import org.apache.cxf.helpers.IOUtils;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.junit.Assert.*;
import static org.mockito.Mockito.when;

public class CompressionServiceTest {

    private static final String COMPRESSION_PROPERTY_KEY = "CompressionType";
    private static final String COMPRESSION_PROPERTY_VALUE = "application/gzip";
    private static final String UNCOMPRESSED_FILE_PATH = "compression/payload.xml";
    private static final String COMPRESSED_FILE_PATH = "compression/payload.xml.gz";


    @InjectMocks
    CompressionService compressionService;

    @Mock
    private CompressionMimeTypeBlacklist blacklist;

    @Mock
    private UserMessage userMessage;

    @Mock
    private LegConfiguration legConfiguration;

    @Before
    public void setUp() throws Exception {

        MockitoAnnotations.initMocks(this);

        when(this.legConfiguration.isCompressPayloads()).thenReturn(true);
        when(this.blacklist.getEntries()).thenReturn(Collections.<String>emptyList());
    }

    @Test
    public void testHandleCompression_positive() throws Exception {

        final PayloadInfo payloadInfoUncompressed;

        final Property mimeProperty = new Property();
        mimeProperty.setName(Property.MIME_TYPE);
        mimeProperty.setValue("text/xml");

        final PartProperties partProperties = new PartProperties();
        partProperties.getProperties().add(mimeProperty);

        final PartInfo partInfo = new PartInfo();
        partInfo.setInBody(false);
        partInfo.setPartProperties(partProperties);
        partInfo.setBinaryData(IOUtils.readBytesFromStream(this.getClass().getClassLoader().getResourceAsStream(CompressionServiceTest.UNCOMPRESSED_FILE_PATH)));

        final List<PartInfo> partInfoList = new ArrayList<>();
        partInfoList.add(partInfo);

        payloadInfoUncompressed = new PayloadInfo();
        payloadInfoUncompressed.getPartInfo().addAll(partInfoList);

        final byte[] payloadDataBeforeCompressionFirstAttachment = payloadInfoUncompressed.getPartInfo().get(0).getBinaryData();

        when(this.userMessage.getPayloadInfo()).thenReturn(payloadInfoUncompressed);

        assertTrue(this.compressionService.handleCompression(this.userMessage, this.legConfiguration));

        final byte[] compressedPayloadResult = this.userMessage.getPayloadInfo().getPartInfo().get(0).getBinaryData();
        assertNotEquals(payloadDataBeforeCompressionFirstAttachment.length, compressedPayloadResult.length);
        assertEquals(this.userMessage.getPayloadInfo().getPartInfo().get(0).getPartProperties().getProperties().size(), 2);

        final Property compressionProperty = new Property();
        compressionProperty.setName(CompressionServiceTest.COMPRESSION_PROPERTY_KEY);
        compressionProperty.setValue(CompressionServiceTest.COMPRESSION_PROPERTY_VALUE);
        assertTrue(this.userMessage.getPayloadInfo().getPartInfo().get(0).getPartProperties().getProperties().contains(compressionProperty));
    }

    @Test
    public void testHandleCompression_negative_mimetypeValueMissing() throws IOException {
        final PayloadInfo payloadInfoUncompressed;
        final Property mimeProperty = new Property();
        mimeProperty.setName(Property.MIME_TYPE);

        final PartProperties partProperties = new PartProperties();
        partProperties.getProperties().add(mimeProperty);

        final PartInfo partInfo = new PartInfo();
        partInfo.setInBody(false);
        partInfo.setPartProperties(partProperties);
        partInfo.setBinaryData(IOUtils.readBytesFromStream(this.getClass().getClassLoader().getResourceAsStream(CompressionServiceTest.UNCOMPRESSED_FILE_PATH)));

        final List<PartInfo> partInfoList = new ArrayList<>();
        partInfoList.add(partInfo);

        payloadInfoUncompressed = new PayloadInfo();
        payloadInfoUncompressed.getPartInfo().addAll(partInfoList);

        when(this.userMessage.getPayloadInfo()).thenReturn(payloadInfoUncompressed);

        try {
            this.compressionService.handleCompression(this.userMessage, this.legConfiguration);
            fail("Empty mimetype is not allowed and therefore an exception is expected to be thrown");
        } catch (final EbMS3Exception e) {
            assertEquals(ErrorCode.EBMS_0007.getErrorCodeName(), e.getErrorCodeObject().getErrorCodeName());
        }
    }

    @Test
    public void testHandleDecompression_positive() throws Exception {

        final PayloadInfo payloadInfoCompressed;

        final Property mimeProperty = new Property();
        mimeProperty.setName(Property.MIME_TYPE);
        mimeProperty.setValue("text/xml");

        final Property compressionProperty = new Property();
        compressionProperty.setName(CompressionServiceTest.COMPRESSION_PROPERTY_KEY);
        compressionProperty.setValue(CompressionServiceTest.COMPRESSION_PROPERTY_VALUE);

        final PartProperties partProperties = new PartProperties();
        partProperties.getProperties().add(mimeProperty);
        partProperties.getProperties().add(compressionProperty);

        final PartInfo partInfo = new PartInfo();
        partInfo.setInBody(false);
        partInfo.setPartProperties(partProperties);
        partInfo.setBinaryData(IOUtils.readBytesFromStream(this.getClass().getClassLoader().getResourceAsStream(CompressionServiceTest.COMPRESSED_FILE_PATH)));

        final List<PartInfo> partInfoList = new ArrayList<>();
        partInfoList.add(partInfo);

        payloadInfoCompressed = new PayloadInfo();
        payloadInfoCompressed.getPartInfo().addAll(partInfoList);


        when(this.userMessage.getPayloadInfo()).thenReturn(payloadInfoCompressed);

        this.compressionService.handleDecompression(this.userMessage, this.legConfiguration);

        assertEquals(payloadInfoCompressed.getPartInfo().size(), 1);
    }


}