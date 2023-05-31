package domiSMPTests.ui;

import ddsl.ApplicationRoles;
import domiSMPTests.SeleniumTest;
import org.testng.Reporter;
import org.testng.annotations.Test;
import org.testng.asserts.SoftAssert;
import utils.Generator;


public class LoginPgTests extends SeleniumTest {

    /**
     * Methods creates new  user with role given as parameter "role". Then the newly created users tries to login to application.
     *
     * @param role
     * @param soft
     * @throws Exception
     */
    private void checkUserLogin(String role, SoftAssert soft) throws Exception {

        String username = Generator.randomAlphaNumeric(10);
        String email = Generator.randomAlphaNumeric(5) + "@automatedTesint.com";

        rest.users().createUser(username, role, email);
        Reporter.log(String.format("Created user %s with role %s", username, role));
        LOG.info("Created user [{}] with role [{}]", username, role);

        Reporter.log(String.format("Login %s with role %s", username, role));
        LOG.info(String.format("Login %s with role %s", username, role));
        //login(username, data.defaultPass());

    }

    @Test(description = "LGN-1")
    public void validLogin() throws Exception {
        Reporter.log("Testing valid login with every type of user");
        LOG.info("Testing valid login with every type of user");
        SoftAssert soft = new SoftAssert();

        checkUserLogin(ApplicationRoles.SYSTEM_ADMIN, soft);
        checkUserLogin(ApplicationRoles.USER, soft);

        soft.assertAll();
    }
}

