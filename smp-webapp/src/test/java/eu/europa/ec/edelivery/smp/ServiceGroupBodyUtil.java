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

package eu.europa.ec.edelivery.smp;

import static java.lang.String.format;

/**
 * Created by gutowpa on 26/09/2017.
 */
public class ServiceGroupBodyUtil {

    public static String getSampleServiceGroupBodyWithScheme(String scheme) {
        return format("<ServiceGroup xmlns=\"http://docs.oasis-open.org/bdxr/ns/SMP/2016/05\">\n" +
                "   <ParticipantIdentifier scheme=\"%s\">urn:poland:ncpb</ParticipantIdentifier>\n" +
                "   <ServiceMetadataReferenceCollection/>\n" +
                " </ServiceGroup>", scheme);
    }
}
