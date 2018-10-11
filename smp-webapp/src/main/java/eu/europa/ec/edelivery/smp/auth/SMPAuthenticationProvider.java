package eu.europa.ec.edelivery.smp.auth;

import eu.europa.ec.edelivery.smp.data.dao.UserDao;
import eu.europa.ec.edelivery.smp.data.model.DBUser;
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
            //https://www.owasp.org/index.php/Authentication_Cheat_Sheet
            // Do not reveal the status of an existing account. Not to use UsernameNotFoundException
            throw new BadCredentialsException("Login failed; Invalid userID or password");
        }
        DBUser usr = oUsr.get();
        try {
            if (!BCrypt.checkpw(password, usr.getPassword())) {
                throw new BadCredentialsException("Login failed; Invalid userID or password");
            }
        }catch (java.lang.IllegalArgumentException ex){
            // password is not hashed
            throw new BadCredentialsException("Login failed; Invalid userID or password");
        }
        return new UsernamePasswordAuthenticationToken(username, password,Collections.singletonList(new SMPAuthority(usr.getRole())));

    }



    @Override
    public boolean supports(Class<?> auth) {
        return auth.equals(UsernamePasswordAuthenticationToken.class);
    }
}
