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
import org.oasis_open.docs.bdxr.ns.smp._2016._05.DocumentIdentifier;
import org.oasis_open.docs.bdxr.ns.smp._2016._05.ParticipantIdentifierType;
import org.oasis_open.docs.bdxr.ns.smp._2016._05.ProcessIdentifier;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Factory and utility methods for API classes generated from OASIS XSD.
 *
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
        String scheme = extract(doubleColonDelimitedId, "scheme");
        String value = extract(doubleColonDelimitedId, "value");
        return new DocumentIdentifier(value, scheme);
    }

    public static ProcessIdentifier asProcessId(String doubleColonDelimitedId) {
        String scheme = extract(doubleColonDelimitedId, "scheme");
        String value = extract(doubleColonDelimitedId, "value");
        return new ProcessIdentifier(value, scheme);
    }

    public static String asString(ParticipantIdentifierType participantId){
        return String.format("%s::%s", participantId.getScheme(), participantId.getValue());
    }

    public static String asString(DocumentIdentifier docId){
        return String.format("%s::%s", docId.getScheme(), docId.getValue());
    }


    private static String extract(String doubleColonDelimitedId, String groupName) {
        try {
            Matcher m = IDENTIFIER_PATTERN.matcher(doubleColonDelimitedId);
            m.matches();
            return m.group(groupName);
        }catch(Exception e){
            throw new MalformedIdentifierException(doubleColonDelimitedId, e);
        }
    }
}
