package eu.europa.ec.edelivery.smp.conversion;

import eu.europa.ec.edelivery.smp.data.enums.CredentialType;
import eu.europa.ec.edelivery.smp.data.model.user.DBCertificate;
import eu.europa.ec.edelivery.smp.data.model.user.DBCredential;
import eu.europa.ec.edelivery.smp.data.model.user.DBUser;
import eu.europa.ec.edelivery.smp.data.ui.UserRO;
import eu.europa.ec.edelivery.smp.services.ConfigurationService;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.platform.commons.util.StringUtils;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.core.convert.ConversionService;

import java.time.OffsetDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author Sebastian-Ion TINCU
 * @since 4.1
 */

@RunWith(MockitoJUnitRunner.class)
@Ignore
public class DBUserToUserROConverterTest {

    private DBUser source;

    private UserRO target;


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

        thenThePasswordIsMarkedAsExpired("The passwords should be marked as expired when converting users" +
                " having passwords that have been reset by SystemAdministrators");
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
        // some month has less than 29 days -therefore -27
        givenAnExistingUser("password", OffsetDateTime.now().minusMonths(2).minusDays(27), null);
    }

    private void givenAnExistingUserHavingAPasswordThatChangedMoreThanThreeMonthsAgo() {
        givenAnExistingUser("password", OffsetDateTime.now().minusMonths(3).minusDays(10), null);
    }

    private void givenAnExistingUser(String password, OffsetDateTime passwordChange, DBCertificate certificate) {
        source = new DBUser();
        /*
        Optional<DBCredential> optUserPassCred = source.getCredentials().stream().filter(credential -> credential.getCredentialType() == CredentialType.USERNAME_PASSWORD).findFirst();
        Optional<DBCredential> optCertCred = source.getCredentials().stream().filter(credential -> credential.getCredentialType() == CredentialType.CERTIFICATE).findFirst();

        if (StringUtils.isNotBlank(password)) {
            DBCredential credential =optUserPassCred.orElse(new DBCredential());
            if (credential.getUser()==null){
                credential.setUser(source);
                credential.setCredentialType(CredentialType.USERNAME_PASSWORD);
                source.addCredentials(credential);
            }
            credential.setValue(password);
            credential.setChangedOn(passwordChange);
            credential.setExpireOn(passwordChange != null ? passwordChange.plusMonths(3) : null);
        } else if (optUserPassCred.isPresent()) {
            source.removeCredentials(optUserPassCred.get());
        }

        if (certificate!=null) {
            DBCredential credential =optCertCred.orElse(new DBCredential());
            if (credential.getUser()==null){
                credential.setUser(source);
                credential.setCredentialType(CredentialType.CERTIFICATE);
                source.addCredentials(credential);
            }
            credential.setCertificate(certificate);
            credential.setValue(certificate.getCertificateId());
            credential.setChangedOn(passwordChange);
            credential.setExpireOn(certificate.getValidTo());
            credential.setExpireOn(certificate.getValidFrom());
        } else if (optCertCred.isPresent()) {
            source.removeCredentials(optCertCred.get());
        }

         */
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
