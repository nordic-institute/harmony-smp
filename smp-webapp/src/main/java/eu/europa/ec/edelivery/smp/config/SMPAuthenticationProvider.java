package eu.europa.ec.edelivery.smp.config;

import eu.europa.ec.edelivery.smp.data.dao.UserDao;
import eu.europa.ec.edelivery.smp.data.model.DBUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCrypt;

import java.util.Collections;
import java.util.List;

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

        DBUser usr = mUserDao.find(username);
        System.out.println("GOT user " +username + " username " + usr );
        if (usr == null){
            //https://www.owasp.org/index.php/Authentication_Cheat_Sheet
            // Do not reveal the status of an existing account. Not to use UsernameNotFoundException
            throw new BadCredentialsException("Login failed; Invalid userID or password");
        }
        System.out.println("Check print");
        if (!BCrypt.checkpw(password,  usr.getPassword())) {
            throw new BadCredentialsException("Login failed; Invalid userID or password");
        }
        System.out.println("get roles");
        List<GrantedAuthority> roles ;
        try {
            roles = mUserDao.getUserRoles(username);

        }catch (Exception ex) {
            ex.printStackTrace(System.out);
            return null;
        }

        System.out.println("Got roles: " + roles.size() + " " + roles);
        return new UsernamePasswordAuthenticationToken(username, password,roles);

    }



    @Override
    public boolean supports(Class<?> auth) {
        return auth.equals(UsernamePasswordAuthenticationToken.class);
    }
}
