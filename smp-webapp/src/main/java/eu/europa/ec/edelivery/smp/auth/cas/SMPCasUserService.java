package eu.europa.ec.edelivery.smp.auth.cas;

import eu.europa.ec.edelivery.smp.data.model.DBUser;
import eu.europa.ec.edelivery.smp.data.ui.UserRO;
import eu.europa.ec.edelivery.smp.data.ui.auth.SMPAuthority;
import eu.europa.ec.edelivery.smp.exceptions.SMPRuntimeException;
import eu.europa.ec.edelivery.smp.services.ui.UIUserService;
import org.jasig.cas.client.authentication.AttributePrincipal;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.cas.authentication.CasAssertionAuthenticationToken;
import org.springframework.security.core.userdetails.AuthenticationUserDetailsService;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.Map;


/**
 * The purpose of the class is to retrieve Spring Security UserDetails object for the CAS ticket validation request (CasAssertionAuthenticationToken).
 * The User object is mapped to local authorization object via AttributePrincipal name value.
 *
 * @author Joze Rihtarsic
 * @since 4.2
 */
@Component
public class SMPCasUserService implements AuthenticationUserDetailsService<CasAssertionAuthenticationToken> {

	private static final Logger LOG = LoggerFactory.getLogger(SMPCasUserService.class);

	final UIUserService uiUserService;

	@Autowired
	public SMPCasUserService(UIUserService uiUserService) {
		this.uiUserService = uiUserService;
	}

	/**
	 * @param token The pre-authenticated authentication token from the cas SMPCas20ServiceTicketValidator
	 * @return UserDetails for the given authentication token, never null.
	 * @throws UsernameNotFoundException if no user details can be found for the given authentication token
	 */
	@Override
	public UserDetails loadUserDetails(CasAssertionAuthenticationToken token) throws UsernameNotFoundException {
		
		AttributePrincipal principal = token.getAssertion().getPrincipal();
		String username = principal.getName();
		LOG.info("Principal name:  "+username);
		LOG.info("Principal:  "+principal);
		Map<String, Object> attributes = principal.getAttributes();
		for(Map.Entry<String, Object> attribute : attributes.entrySet()) {
			LOG.info("Principal attribute "+attribute.getKey()+"="+attribute.getValue());
		}
		DBUser dbuser;
		try {
			dbuser = uiUserService.findUserByUsername(username);
		} catch (SMPRuntimeException ex) {
			throw new UsernameNotFoundException("User with the username ["+username+"] is not registered in SMP", ex);
		}
		UserRO userRo = uiUserService.convertToRo(dbuser);
		userRo.setCasAuthenticated(true);
		userRo.setPassword(null);
		userRo.setAuthorities(Collections.singletonList(new SMPAuthority(userRo.getRole())));
		return userRo;
	}
}