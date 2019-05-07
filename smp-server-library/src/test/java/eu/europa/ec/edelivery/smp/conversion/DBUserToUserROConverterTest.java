package eu.europa.ec.edelivery.smp.conversion;

import eu.europa.ec.edelivery.smp.data.model.DBCertificate;
import eu.europa.ec.edelivery.smp.data.model.DBUser;
import eu.europa.ec.edelivery.smp.data.ui.UserRO;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.core.convert.ConversionService;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author Sebastian-Ion TINCU
 */

@RunWith(MockitoJUnitRunner.class)
public class DBUserToUserROConverterTest {

    private DBUser source;

    private UserRO target;

    @Mock
    private ConversionService conversionService;

    @InjectMocks
    private DBUserToUserROConverter converter = new DBUserToUserROConverter();

    @Test
    public void returnsThePasswordAsNotExpiredForCertificateOnlyUsers() {
        givenAnExistingCertificateOnlyUser();

        whenConvertingTheExistingUser();

        thenThePasswordIsNotMarkedAsExpired("The password should have not been marked as expired when the user has no password");
    }

    @Test
    public void returnsThePasswordAsExpiredWhenConvertingAnExistingUserThatHasAPasswordThatHasBeenRecentlyReset() {
        givenAnExistingUserHavingAPasswordThatHasJustBeenReset();

        whenConvertingTheExistingUser();

        thenThePasswordIsMarkedAsExpired("The passwords should be marked as expired when converting users having passwords that have been reset by SystemAdministrators");
    }

    @Test
    public void returnsThePasswordAsNotExpiredWhenConvertingAnExistingUserThatHasAPasswordChangedNoLongerThanThreeMonthsAgo() {
        givenAnExistingUserHavingAPasswordThatChangedNoLongerThanThreeMonthsAgo();

        whenConvertingTheExistingUser();

        thenThePasswordIsNotMarkedAsExpired("The passwords should not be marked as expired when converting users having password they have changed in the previous 3 months");
    }

    @Test
    public void returnsThePasswordAsExpiredWhenConvertingAnExistingUserThatHasAPasswordChangedMoreThanThreeMonthsAgo() {
        givenAnExistingUserHavingAPasswordThatChangedMoreThanThreeMonthsAgo();

        whenConvertingTheExistingUser();

        thenThePasswordIsMarkedAsExpired("The passwords should be marked as expired when converting users having password they have changed more than 3 months ago");
    }

    private void givenAnExistingCertificateOnlyUser() {
        givenAnExistingUser(null, null, new DBCertificate());
    }

    private void givenAnExistingUserHavingAPasswordThatHasJustBeenReset() {
        givenAnExistingUser("password", null, null);
    }

    private void givenAnExistingUserHavingAPasswordThatChangedNoLongerThanThreeMonthsAgo() {
        // some month has less than 29 days -therefore -28
        givenAnExistingUser("password", LocalDateTime.now().minusMonths(2).minusDays(28), null);
    }

    private void givenAnExistingUserHavingAPasswordThatChangedMoreThanThreeMonthsAgo() {
        givenAnExistingUser("password", LocalDateTime.now().minusMonths(3).minusDays(10), null);
    }

    private void givenAnExistingUser(String password, LocalDateTime passwordChange, DBCertificate certificate) {
        source = new DBUser();
        source.setCertificate(certificate);
        source.setPassword(password);
        source.setPasswordChanged(passwordChange);
    }

    private void whenConvertingTheExistingUser() {
        target = converter.convert(source);
    }

    private void thenThePasswordIsMarkedAsExpired(String failureDescription) {
        assertThat(target.isPasswordExpired())
                .describedAs(failureDescription)
                .isTrue();
    }

    private void thenThePasswordIsNotMarkedAsExpired(String failureDescription) {
        assertThat(target.isPasswordExpired())
                .describedAs(failureDescription)
                .isFalse();
    }
}