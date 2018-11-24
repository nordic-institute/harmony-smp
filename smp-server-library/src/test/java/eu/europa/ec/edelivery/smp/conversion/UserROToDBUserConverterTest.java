package eu.europa.ec.edelivery.smp.conversion;

import eu.europa.ec.edelivery.smp.data.model.DBUser;
import eu.europa.ec.edelivery.smp.data.ui.UserRO;
import org.assertj.core.api.Assertions;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.core.convert.ConversionService;

import static org.assertj.core.api.Assertions.assertThat;

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
        assertThat(target.getPasswordChanged())
                .describedAs("The last time the password changed should not be set by the converter as it is controlled when the user details are updated " +
                        "and it depends if it's done by the SystemAdministrators or by the users themselves")
                .isNull();
    }
}