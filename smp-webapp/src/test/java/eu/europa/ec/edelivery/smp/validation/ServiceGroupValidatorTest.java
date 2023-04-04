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

import eu.europa.ec.edelivery.smp.conversion.IdentifierService;
import eu.europa.ec.edelivery.smp.identifiers.Identifier;
import eu.europa.ec.edelivery.smp.services.ConfigurationService;
import eu.europa.ec.smp.api.exceptions.MalformedIdentifierException;
import org.hamcrest.CoreMatchers;
import org.hamcrest.MatcherAssert;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.mockito.Mockito;
import org.oasis_open.docs.bdxr.ns.smp._2016._05.ParticipantIdentifierType;
import org.oasis_open.docs.bdxr.ns.smp._2016._05.ServiceGroup;

import java.util.Arrays;
import java.util.Collection;
import java.util.regex.Pattern;

/**
 * Created by gutowpa on 02/08/2017.
 */
@RunWith(Parameterized.class)
@Ignore
public class ServiceGroupValidatorTest {
/*
    private static final Pattern ALLOWED_SCHEME_PATTERN = Pattern.compile("^$|^(?!^.{26})([a-z0-9]+-[a-z0-9]+-[a-z0-9]+)$|^urn:oasis:names:tc:ebcore:partyid-type:(iso6523|unregistered)(:.+)?$");

    private ServiceGroupValidator validator;
    ConfigurationService configurationService = Mockito.mock(ConfigurationService.class);
    IdentifierService normalizer = new IdentifierService(configurationService);

    @Parameterized.Parameters(name = "{index}: {0}")
    public static Collection<Object[]> data() {
        return Arrays.asList(new Object[][]{

                {"Good peppol schema", "good6-scheme4-ok", "urn:poland:ncpb", false, true, ALLOWED_SCHEME_PATTERN, null, null},
                {"Allowed null schema", null, "urn:poland:ncpb", false, false, ALLOWED_SCHEME_PATTERN, null, null},
                {"Length exceeded", "ength-exceeeeeedsTheCharacters-25chars", "urn:poland:ncpb", true, true, ALLOWED_SCHEME_PATTERN, MalformedIdentifierException.class, "Scheme does not match pattern:"},
                {"Too many parts", "too-many-segments-inside", "urn:poland:ncpb", true, true, ALLOWED_SCHEME_PATTERN, MalformedIdentifierException.class, "Scheme does not match pattern:"},
                {"Missing parts", "only-two", "urn:poland:ncpb", true, true, ALLOWED_SCHEME_PATTERN, MalformedIdentifierException.class, "Scheme does not match pattern: "},
                {"Null not allowed", null, "urn:poland:ncpb", true, true, ALLOWED_SCHEME_PATTERN, IllegalArgumentException.class, "Invalid Identifier: "},
                {"EBCorePartyId Oasis", "urn:oasis:names:tc:ebcore:partyid-type:iso6523:0088", "123456", false, true, ALLOWED_SCHEME_PATTERN, null, null},
                {"EBCorePartyId eDelivery", null, "urn:oasis:names:tc:ebcore:partyid-type:iso6523:0088:123456", false, true, ALLOWED_SCHEME_PATTERN, null, null},
        });
    }

    @Before
    public void init() {
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
    public Pattern schemePattern;
    @Parameterized.Parameter(6)
    public Class errorClass;
    @Parameterized.Parameter(7)
    public String errorMessage;


    @Test
    public void testServiceGroupIdentifier() {
        normalizer.configureParticipantIdentifierFormatter(null, mandatoryScheme, schemePattern);

        validateScheme(schema, value);
    }

    private void validateScheme(String scheme, String value) {

        Identifier id = new Identifier(value, scheme);
        /*
        //ServiceGroup sg = new ServiceGroup();
        sg.setParticipantIdentifier(id);

        if (expectedThrowError) {
            Throwable throwable = Assert.assertThrows(errorClass, () -> validator.validate(normalizer.formatParticipant(id), sg));
            MatcherAssert.assertThat(throwable.getMessage(), CoreMatchers.containsString(errorMessage));
        } else {
            validator.validate(normalizer.formatParticipant(id), sg);
        }


    }
  */

}
