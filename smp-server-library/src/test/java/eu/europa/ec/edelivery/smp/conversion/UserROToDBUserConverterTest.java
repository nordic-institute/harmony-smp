/*-
 * #%L
 * smp-server-library
 * %%
 * Copyright (C) 2017 - 2023 European Commission | eDelivery | DomiSMP
 * %%
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by the European Commission - subsequent
 * versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy of the Licence at:
 * 
 * [PROJECT_HOME]\license\eupl-1.2\license.txt or https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 * 
 * Unless required by applicable law or agreed to in writing, software distributed under the Licence is
 * distributed on an "AS IS" basis, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the Licence for the specific language governing permissions and limitations under the Licence.
 * #L%
 */
package eu.europa.ec.edelivery.smp.conversion;

import eu.europa.ec.edelivery.smp.data.model.user.DBUser;
import eu.europa.ec.edelivery.smp.data.ui.UserRO;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.core.convert.ConversionService;

/**
 * @author Sebastian-Ion TINCU
 */
@RunWith(MockitoJUnitRunner.class)
public class UserROToDBUserConverterTest {

    private UserRO source;

    private DBUser target;

    @Mock
    private ConversionService conversionService;

    @InjectMocks
    private UserROToDBUserConverter converter = new UserROToDBUserConverter();

    @Test
    public void doesNotSetPasswordChangedWhenConvertingUser() {
        givenUser();

        whenConvertingTheUserRoSource();

        thenThePasswordChangeTimeIsNotSet();
    }

    private void givenUser() {
        source = new UserRO();
    }

    private void whenConvertingTheUserRoSource() {
        target = converter.convert(source);
    }

    private void thenThePasswordChangeTimeIsNotSet() {
        /* TODO
        assertThat(target.getPasswordChanged())
                .describedAs("The last time the password changed should not be set by the converter as it is controlled when the user details are updated " +
                        "and it depends if it's done by the SystemAdministrators or by the users themselves")
                .isNull();

         */
    }
}
