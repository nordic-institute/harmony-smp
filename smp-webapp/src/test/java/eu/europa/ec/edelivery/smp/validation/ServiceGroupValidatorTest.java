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

package eu.europa.ec.edelivery.smp.validation;

import eu.europa.ec.edelivery.smp.error.exceptions.BadRequestException;
import org.junit.Before;
import org.junit.Test;
import org.oasis_open.docs.bdxr.ns.smp._2016._05.ParticipantIdentifierType;
import org.oasis_open.docs.bdxr.ns.smp._2016._05.ServiceGroup;
import org.springframework.beans.factory.annotation.Autowired;

import static eu.europa.ec.smp.api.Identifiers.asString;

/**
 * Created by gutowpa on 02/08/2017.
 */
public class ServiceGroupValidatorTest {

    private static final String ALLOWED_SCHEME_REGEXP = "^(?!^.{26})([a-z0-9]+-[a-z0-9]+-[a-z0-9]+)";

    @Autowired
    private ServiceGroupValidator validator;

    @Before
    public void init() {
        validator = new ServiceGroupValidator();
        validator.setRegexPattern(ALLOWED_SCHEME_REGEXP);
    }

    @Test
    public void testPositiveGoodScheme() throws Throwable {
        validateBadScheme("good6-scheme4-ok");
    }

    @Test(expected = BadRequestException.class)
    public void testServiceGroupIdentifierSchemeValidationTooLong() throws Throwable {
        validateBadScheme("length-exceeeeeeds-25chars");
    }

    @Test(expected = BadRequestException.class)
    public void testServiceGroupIdentifierSchemeValidationNotBuiltWithThreeSegments() throws Throwable {
        validateBadScheme("too-many-segments-inside");
    }

    @Test(expected = BadRequestException.class)
    public void testServiceGroupIdentifierSchemeValidationTooLittleSegments() throws Throwable {
        validateBadScheme("only-two");
    }

    @Test(expected = BadRequestException.class)
    public void testServiceGroupIdentifierSchemeValidationIllegalChar() throws Throwable {
        validateBadScheme("illegal-char-here:");
    }

    private void validateBadScheme(String scheme) throws Throwable {
        ServiceGroup sg = new ServiceGroup();
        ParticipantIdentifierType id = new ParticipantIdentifierType("urn:poland:ncpb", scheme);
        sg.setParticipantIdentifier(id);

        validator.validate(asString(id), sg);
    }
}
