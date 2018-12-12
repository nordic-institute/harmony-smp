/*
 * Copyright 2017 European Commission | CEF eDelivery
 *
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by the European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 *
 * You may obtain a copy of the Licence attached in file: LICENCE-EUPL-v1.2.pdf
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the Licence is distributed on an "AS IS" basis,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the Licence for the specific language governing permissions and limitations under the Licence.
 */

package eu.europa.ec.smp.api;

import eu.europa.ec.smp.api.exceptions.MalformedIdentifierException;
import org.apache.commons.lang3.StringUtils;
import org.oasis_open.docs.bdxr.ns.smp._2016._05.DocumentIdentifier;
import org.oasis_open.docs.bdxr.ns.smp._2016._05.ParticipantIdentifierType;
import org.oasis_open.docs.bdxr.ns.smp._2016._05.ProcessIdentifier;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static java.nio.charset.StandardCharsets.UTF_8;

/**
 * Factory and utility methods for API classes generated from OASIS XSD.
 * <p>
 * Created by gutowpa on 12/01/2017.
 */
public class Identifiers {

    private static final Pattern IDENTIFIER_PATTERN = Pattern.compile("^(?<scheme>.+?)::(?<value>.+)$");


    public static ParticipantIdentifierType asParticipantId(String doubleColonDelimitedId) {
        String scheme = extract(doubleColonDelimitedId, "scheme");
        String value = extract(doubleColonDelimitedId, "value");
        return new ParticipantIdentifierType(value, scheme);
    }

    public static DocumentIdentifier asDocumentId(String doubleColonDelimitedId) {
        String[] res = splitIdentifier(doubleColonDelimitedId);
        return new DocumentIdentifier(res[1], res[0]);
    }

    public static ProcessIdentifier asProcessId(String doubleColonDelimitedId) {
        String scheme = extract(doubleColonDelimitedId, "scheme");
        String value = extract(doubleColonDelimitedId, "value");
        return new ProcessIdentifier(value, scheme);
    }

    public static String asString(ParticipantIdentifierType participantId) {
        return String.format("%s::%s", participantId.getScheme(), participantId.getValue());
    }

    public static String asString(DocumentIdentifier docId) {
        return String.format("%s::%s", docId.getScheme()!=null?docId.getScheme():"", docId.getValue());
    }

    public static String asUrlEncodedString(ParticipantIdentifierType participantId) {
        return urlEncode(asString(participantId));
    }

    public static String asUrlEncodedString(DocumentIdentifier docId) {
        return urlEncode(asString(docId));
    }

    private static String urlEncode(String s) {
        try {
            return URLEncoder.encode(s, UTF_8.name());
        } catch (UnsupportedEncodingException e) {
            throw new IllegalStateException(e);
        }
    }

    private static String extract(String doubleColonDelimitedId, String groupName) {
        try {
            Matcher m = IDENTIFIER_PATTERN.matcher(doubleColonDelimitedId);
            m.matches();
            return m.group(groupName);
        } catch (Exception e) {
            throw new MalformedIdentifierException(doubleColonDelimitedId, e);
        }
    }

    /**
     * Method splits identifier at first occurance of double colon :: and returns array size of 2. The first value is
     * schema and the second is identifier. If identifier is blank or with missing :: MalformedIdentifierException is thrown
     * @param doubleColonDelimitedId
     * @return array with two elements. First is schema and second is id
     */
    private static String[] splitIdentifier(String doubleColonDelimitedId) {
        String[] idResult = new String[2];
        if (StringUtils.isBlank(doubleColonDelimitedId)){
            throw new MalformedIdentifierException(doubleColonDelimitedId, null);
        }
        int delimiterIndex = doubleColonDelimitedId.indexOf("::");
        if (delimiterIndex<0){
            throw new MalformedIdentifierException(doubleColonDelimitedId, null);
        }
        idResult[0] = delimiterIndex==0?null:doubleColonDelimitedId.substring(0,delimiterIndex);
        idResult[1] = doubleColonDelimitedId.substring(delimiterIndex+2);

        if (StringUtils.isBlank(idResult[1])){
            throw new MalformedIdentifierException(doubleColonDelimitedId, null);
        }

        return idResult;

    }


}
