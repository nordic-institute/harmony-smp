package eu.europa.ec.cipa.smp.server.security;

import org.springframework.security.crypto.bcrypt.BCrypt;

/**
 * Created by gutowpa on 22/02/2017.
 */
public class BCryptPasswordHash {

    public static String hashPassword(String pass){
        return BCrypt.hashpw(pass, BCrypt.gensalt());
    }

    public static void main(String [] args){
        for(String pass : args) {
            System.out.println(hashPassword(pass));
        }
    }
}
