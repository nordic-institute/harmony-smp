/*
 * Copyright 2017 European Commission | CEF eDelivery
 *
 * Licensed under the EUPL, Version 1.1 or – as soon they will be approved by the European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 *
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/software/page/eupl
 * or file: LICENCE-EUPL-v1.1.pdf
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the Licence is distributed on an "AS IS" basis,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the Licence for the specific language governing permissions and limitations under the Licence.
 */
package eu.europa.ec.cipa.smp.server.util;

import javax.annotation.Nullable;
import javax.annotation.concurrent.Immutable;

import com.helger.commons.annotations.PresentForCodeCoverage;
import com.helger.commons.microdom.IMicroNode;
import com.helger.commons.microdom.serialize.MicroWriter;
import com.helger.commons.typeconvert.TypeConverter;
import com.helger.commons.typeconvert.TypeConverterException;
import com.helger.commons.xml.serialize.*;
import org.oasis_open.docs.bdxr.ns.smp._2016._05.ExtensionType;

import com.helger.commons.string.StringHelper;


import org.oasis_open.docs.bdxr.ns.smp._2016._05.ObjectFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.xml.sax.SAXException;

/**
 * This class is used inside the DB component and contains several utility
 * methods.
 *
 * @author PEPPOL.AT, BRZ, Philip Helger
 */
@Immutable
@Deprecated
public final class SMPDBUtils {

    private static final Logger s_aLogger = LoggerFactory.getLogger(SMPDBUtils.class);
    private static final ObjectFactory s_aOF = new ObjectFactory();
    private static final XMLWriterSettings s_aXWS = new XMLWriterSettings().setSerializeDocType(EXMLSerializeDocType.IGNORE).setIndent(EXMLSerializeIndent.NONE);

    private SMPDBUtils() {
    }

    /**
     * This class is used for converting between a String representation of the
     * extension element and the "ExtensionType" complex type.
     *
     * @author PEPPOL.AT, BRZ, Philip Helger
     */

    @Nullable
    @Deprecated
    public static ExtensionType getAsExtensionSafe(@Nullable final String sXML) {
        try {
            if (StringHelper.hasText(sXML)) {

                // Try to interpret as XML
                final Document aDoc = DOMReader.readXMLDOM(sXML);
                if (aDoc != null) {
                    final ExtensionType aExtension = s_aOF.createExtensionType();
                    aExtension.setAny(aDoc.getDocumentElement());
                    return aExtension;
                }
            }
        } catch (SAXException | IllegalArgumentException ex) {
            s_aLogger.warn("Error in parsing extension XML '" + sXML + "'", ex);
        }
        return null;
    }

    /**
     * Convert the passed extension type to a string representation.
     *
     * @param aExtension
     *        The extension to be converted. May be <code>null</code>.
     * @return <code>null</code> if no extension was passed - the XML
     *         representation of the extension otherwise.
     * @throws IllegalArgumentException
     *         If the Extension cannot be converted to a String
     */
    @Nullable
    @Deprecated
    public static String convert (@Nullable final ExtensionType aExtension) {
        // If there is no extension present, nothing to convert
        if (aExtension == null)
            return null;

        // Get the extension content
        final Object aExtensionElement = aExtension.getAny ();
        if (aExtensionElement == null)
            return null;

        // Handle DOM nodes directly
        if (aExtensionElement instanceof Node)
            return XMLWriter.getNodeAsString ((Node) aExtensionElement, s_aXWS);

        // Handle Micro nodes also directly
        if (aExtensionElement instanceof IMicroNode)
            return MicroWriter.getNodeAsString ((IMicroNode) aExtensionElement, s_aXWS);

        try {
            // Call the global type converter - maybe it helps :)
            return TypeConverter.convertIfNecessary (aExtensionElement, String.class);
        }
        catch (final TypeConverterException ex) {
            // FIXME the extension may contain multiple elements (e.g. lists)
            throw new IllegalArgumentException ("Don't know how to convert the extension element of type " +
                    aExtension.getClass ().getName ());
        }
    }

    /**
     * The certificate string needs to be emitted in portions of 64 characters. If
     * characters are left, than &lt;CR>&lt;LF> ("\r\n") must be added to the
     * string so that the next characters start on a new line. After the last
     * part, no &lt;CR>&lt;LF> is needed. Respective RFC parts are 1421 4.3.2.2
     * and 4.3.2.4
     *
     * @param sCertificate
     *        Original certificate string as stored in the DB
     * @return The RFC 1421 compliant string
     */
    @Nullable
    public static String getRFC1421CompliantStringWithoutCarriageReturnCharacters(@Nullable final String sCertificate) {
        if (StringHelper.hasNoText(sCertificate))
            return sCertificate;

        // Remove all existing whitespace characters
        String sPlainString = StringHelper.getWithoutAnySpaces(sCertificate);

        // Start building the result
        final int nMaxLineLength = 64;
        final String sLF = "\n"; //Originally RFC suggests CRLF instead of LF
        final StringBuilder aSB = new StringBuilder();
        while (sPlainString.length() > nMaxLineLength) {
            // Append line + LF
            aSB.append(sPlainString, 0, nMaxLineLength).append(sLF);

            // Remove the start of the string
            sPlainString = sPlainString.substring(nMaxLineLength);
        }

        // Append the rest
        aSB.append(sPlainString);

        return aSB.toString();
    }
}
