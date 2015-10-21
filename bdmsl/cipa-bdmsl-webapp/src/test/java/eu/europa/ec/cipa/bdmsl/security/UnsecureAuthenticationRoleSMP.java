package eu.europa.ec.cipa.bdmsl.security;

import eu.europa.ec.cipa.common.exception.TechnicalException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.Arrays;
import java.util.Collection;

/**
 * Created by feriaad on 09/07/2015.
 */
public class UnsecureAuthenticationRoleSMP extends UnsecureAuthentication {
    public UnsecureAuthenticationRoleSMP() throws TechnicalException {
        super();
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return Arrays.asList(new SimpleGrantedAuthority[]{new SimpleGrantedAuthority(CustomAuthenticationProvider.SMP_ROLE)});
    }
}
