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

package eu.europa.ec.edelivery.smp;

import static java.lang.String.format;

/**
 * Created by gutowpa on 26/09/2017.
 */
public class ServiceGroupBodyUtil {

    public static final String SIMPLE_DOCUMENT_XML ="<ServiceMetadata xmlns=\"http://docs.oasis-open.org/bdxr/ns/SMP/2016/05\"><ServiceInformation><ParticipantIdentifier scheme=\"%s\">%s</ParticipantIdentifier><DocumentIdentifier scheme=\"%s\">%s</DocumentIdentifier><ProcessList><Process><ProcessIdentifier scheme=\"cenbii-procid-ubl\">urn:www.cenbii.eu:profile:bii04:ver1.0</ProcessIdentifier><ServiceEndpointList><Endpoint transportProfile=\"bdxr-transport-ebms3-as4-v1p0\"><EndpointURI>http://localhost:8080/domibus-weblogic/services/msh</EndpointURI><RequireBusinessLevelSignature>true</RequireBusinessLevelSignature><ServiceActivationDate>2003-01-01T00:00:00</ServiceActivationDate><ServiceExpirationDate>2030-05-01T00:00:00</ServiceExpirationDate><Certificate>dGVzdHdvcmRz</Certificate><ServiceDescription>Sample description of %s</ServiceDescription><TechnicalContactUrl>https://example.com</TechnicalContactUrl></Endpoint></ServiceEndpointList></Process></ProcessList></ServiceInformation></ServiceMetadata>";


    public static String getSampleServiceGroupBodyWithScheme(String scheme) {
        return getSampleServiceGroupBody(scheme, "urn:poland:ncpb");
    }

    public static String getSampleServiceGroupBody(String scheme, String identifier) {
        return format("<ServiceGroup xmlns=\"http://docs.oasis-open.org/bdxr/ns/SMP/2016/05\">\n" +
                "   <ParticipantIdentifier scheme=\"%s\">%s</ParticipantIdentifier>\n" +
                "   <ServiceMetadataReferenceCollection/>\n" +
                " </ServiceGroup>", scheme, identifier);
    }

    public static  String generateServiceMetadata(String partcId, String partcSch, String docId, String docSch, String desc){
        return String.format(SIMPLE_DOCUMENT_XML,partcSch, partcId,docSch, docId, desc);
    }
}
