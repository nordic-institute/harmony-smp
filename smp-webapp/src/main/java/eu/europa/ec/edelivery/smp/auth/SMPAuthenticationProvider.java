package eu.europa.ec.edelivery.smp.auth;

import eu.europa.ec.edelivery.smp.data.dao.UserDao;
import eu.europa.ec.edelivery.smp.data.model.DBUser;
import eu.europa.ec.edelivery.smp.logging.SMPLogger;
import eu.europa.ec.edelivery.smp.logging.SMPLoggerFactory;
import eu.europa.ec.edelivery.smp.logging.SMPMessageCode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.bcrypt.BCrypt;

import java.util.Collections;
import java.util.Optional;

public class SMPAuthenticationProvider implements AuthenticationProvider {

    private static final SMPLogger LOG = SMPLoggerFactory.getLogger(AuthenticationProvider.class);

    @Autowired
    UserDao mUserDao;

    @Override
    public Authentication authenticate(Authentication auth)
            throws AuthenticationException {

        // get user
        // test credentials
        // get and return  user roles.
        String username = auth.getName();
        String password = auth.getCredentials().toString();

        Optional<DBUser> oUsr = mUserDao.findUserByIdentifier(username);
        if (!oUsr.isPresent()){
            LOG.securityWarn(SMPMessageCode.SEC_USER_NOT_EXISTS, username);
            //https://www.owasp.org/index.php/Authentication_Cheat_Sheet
            // Do not reveal the status of an existing account. Not to use UsernameNotFoundException
            throw new BadCredentialsException("Login failed; Invalid userID or password");
        }
        DBUser usr = oUsr.get();
        String role = usr.getRole();
        try {
            if (!BCrypt.checkpw(password, usr.getPassword())) {
                LOG.securityWarn(SMPMessageCode.SEC_INVALID_PASSWORD, username);
                throw new BadCredentialsException("Login failed; Invalid userID or password");
            }

        }catch (java.lang.IllegalArgumentException ex){
            // password is not hashed;
            LOG.securityWarn(SMPMessageCode.SEC_INVALID_PASSWORD,ex, username);
            throw new BadCredentialsException("Login failed; Invalid userID or password");
        }
        LOG.securityInfo(SMPMessageCode.SEC_USER_AUTHENTICATED, username, role);
        return new UsernamePasswordAuthenticationToken(username, password,Collections.singletonList(new SMPAuthority(role)));

    }

    @Override
    public boolean supports(Class<?> auth) {
        return auth.equals(UsernamePasswordAuthenticationToken.class);
    }
}
