package eu.europa.ec.cipa.dispatcher.endpoint_interface;

import java.security.cert.X509Certificate;
import java.sql.Connection;
import java.sql.SQLException;

import org.busdox.servicemetadata.publishing._1.EndpointType;

public interface IAS2EndpointDBInterface
{
	
	/** Checks if the partner passed as parameter is registered in our endpoint DB
	 * @return true if our endpoint knows about the partner
	 */
	public abstract boolean isPartnerKnown (String CN) throws SQLException;
	
	
	/** Retrieves the partner's url, or null if it doesn't exist
	 */
	public abstract String getPartnerUrl (String CN) throws SQLException;
	
	
	/** Retrieves an EndpointType object populated with the URL of the given partner and its certificate from the local keystore
	 */
	public EndpointType getPartnerData (String CN) throws SQLException;
	
	
	/** Creates a partner in the AS2 endpoint DB with all the given values, that can be null (depending if the endpointUrl field is given or not, we'll be able to send to the new partner or only to receive from it)
	 */
	public abstract void createNewPartner (String as2Id, String name, String endpointUrl, String mdnURL, X509Certificate cert) throws Exception;
	
	
	/** If the partner exists in the DB, modifies the partner's endpointUrl, mdnUrl and certificate in the AS2 endpoint DB. If any of the parameters is null, that field won't be deleted in the DB but just left as it was.
	 *  If the partner didn't exist yet, it'll create it with all the given values.
	 */
	public abstract void updatePartner (String as2Id, String name, String endpointUrl, String mdnURL, X509Certificate cert) throws Exception;
	
	
	/** Checks there's a local station configured in the Mendelson DB, and in negative case, configures a default one taking the certificate from the keystore specified on the config file.
	 */
	public abstract void configureLocalStationIfNeeded() throws Exception;

	public Connection getRuntimeConnection() throws SQLException;

	public Connection getConfigConnection() throws SQLException;
}
