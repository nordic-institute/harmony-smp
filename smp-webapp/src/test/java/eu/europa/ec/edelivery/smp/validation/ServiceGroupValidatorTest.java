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

package eu.europa.ec.edelivery.smp.validation;

import eu.europa.ec.edelivery.smp.conversion.CaseSensitivityNormalizer;
import eu.europa.ec.edelivery.smp.error.exceptions.BadRequestException;
import eu.europa.ec.edelivery.smp.services.ConfigurationService;
import eu.europa.ec.smp.api.exceptions.MalformedIdentifierException;
import org.hamcrest.CoreMatchers;
import org.hamcrest.MatcherAssert;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.mockito.Mockito;
import org.oasis_open.docs.bdxr.ns.smp._2016._05.ParticipantIdentifierType;
import org.oasis_open.docs.bdxr.ns.smp._2016._05.ServiceGroup;

import java.util.Arrays;
import java.util.Collection;
import java.util.regex.Pattern;

import static eu.europa.ec.smp.api.Identifiers.asString;

/**
 * Created by gutowpa on 02/08/2017.
 */
@RunWith(Parameterized.class)
public class ServiceGroupValidatorTest {

    private static final String ALLOWED_SCHEME_REGEXP = "^$|^(?!^.{26})([a-z0-9]+-[a-z0-9]+-[a-z0-9]+)$|^urn:oasis:names:tc:ebcore:partyid-type:(iso6523|unregistered)(:.+)?$";

    private ServiceGroupValidator validator;
    ConfigurationService configurationService = Mockito.mock(ConfigurationService.class);
    CaseSensitivityNormalizer normalizer = new CaseSensitivityNormalizer(configurationService);

    @Parameterized.Parameters(name = "{index}: {0}")
    public static Collection<Object[]> data() {
        return Arrays.asList(new Object[][]{

                {"Good peppol schema", "good6-scheme4-ok", "urn:poland:ncpb", false, true, null, null},
                {"Allowed null schema", null, "urn:poland:ncpb", false, false, null, null},
                {"Length exceeded", "ength-exceeeeeedsTheCharacters-25chars", "urn:poland:ncpb", true, true, BadRequestException.class, "Service Group scheme does not match allowed pattern:"},
                {"Too many parts", "too-many-segments-inside", "urn:poland:ncpb", true, true, BadRequestException.class, "Service Group scheme does not match allowed pattern:"},
                {"Missing parts", "only-two", "urn:poland:ncpb", true, true, BadRequestException.class, "Service Group scheme does not match allowed pattern:"},
                {"Null not allowed", null, "urn:poland:ncpb", true, true, MalformedIdentifierException.class, "Malformed identifier, scheme and id should be delimited by double colon"},
                {"EBCorePartyId Oasis", "urn:oasis:names:tc:ebcore:partyid-type:iso6523:0088", "123456", false, true, null, null},
                {"EBCorePartyId eDelivery", null, "urn:oasis:names:tc:ebcore:partyid-type:iso6523:0088:123456", false, true, null, null},
        });
    }

    @Before
    public void init() {
        Mockito.doReturn(Pattern.compile(ALLOWED_SCHEME_REGEXP)).when(configurationService).getParticipantIdentifierSchemeRexExp();
        validator = new ServiceGroupValidator(configurationService, normalizer);
    }

    @Parameterized.Parameter
    public String caseName;
    @Parameterized.Parameter(1)
    public String schema;
    @Parameterized.Parameter(2)
    public String value;
    @Parameterized.Parameter(3)
    public boolean expectedThrowError;
    @Parameterized.Parameter(4)
    public boolean mandatoryScheme;
    @Parameterized.Parameter(5)
    public Class errorClass;
    @Parameterized.Parameter(6)
    public String errorMessage;


    @Test
    public void testServiceGroupIdentifier() {
        Mockito.doReturn(mandatoryScheme).when(configurationService).getParticipantSchemeMandatory();

        validateScheme(schema, value);
    }

    private void validateScheme(String scheme, String value) {
        ServiceGroup sg = new ServiceGroup();
        ParticipantIdentifierType id = new ParticipantIdentifierType(value, scheme);
        sg.setParticipantIdentifier(id);

        try {
            validator.validate(asString(id), sg);
            if (expectedThrowError) {
                Assert.fail();
            }
        } catch (RuntimeException exc) {
            if (!expectedThrowError) {
                Assert.fail();
            }
            Assert.assertEquals(errorClass, exc.getClass());
            MatcherAssert.assertThat(exc.getMessage(), CoreMatchers.startsWith(errorMessage));
        }
    }


}
