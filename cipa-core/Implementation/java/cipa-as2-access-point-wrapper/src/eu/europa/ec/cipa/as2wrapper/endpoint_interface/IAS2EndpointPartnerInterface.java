package eu.europa.ec.cipa.as2wrapper.endpoint_interface;

import java.security.cert.X509Certificate;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Properties;

import org.apache.commons.dbcp.BasicDataSource;

import eu.europa.ec.cipa.as2wrapper.util.PropertiesUtil;

public interface IAS2EndpointPartnerInterface
{
	
	/** Checks if the partner passed as parameter is registered in our endpoint DB
	 * @return true if our endpoint knows about the partner
	 */
	public abstract boolean isPartnerKown (String CN) throws SQLException;
	
	
	/** Retrieves the partner's url, or null if it doesn't exist
	 */
	public abstract String getPartnerUrl (String CN) throws SQLException;
	
	
	/** Creates a partner in the AS2 endpoint DB with all the given values, that can be null (depending if the endpointUrl field is given or not, we'll be able to send to the new partner or only to receive from it)
	 */
	public abstract void createNewPartner (String as2Id, String name, String endpointUrl, String mdnURL, X509Certificate cert) throws Exception;
	
	
	/** If the partner exists in the DB, modifies the partner's endpointUrl, mdnUrl and certificate in the AS2 endpoint DB. If any of the parameters is null, that field won't be deleted in the DB but just left as it was.
	 *  If the partner didn't exist yet, it'll create it with all the given values.
	 */
	public abstract void updatePartner (String as2Id, String name, String endpointUrl, String mdnURL, X509Certificate cert) throws Exception;
		
}
