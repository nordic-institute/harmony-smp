package eu.europa.ec.cipa.as2wrapper.endpoint_interface;

import java.security.cert.X509Certificate;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Properties;

import org.apache.commons.dbcp.BasicDataSource;

import eu.europa.ec.cipa.as2wrapper.util.PropertiesUtil;

public abstract class AS2EndpointPartnerInterface
{
	

	private BasicDataSource dataSource;
	protected Properties properties;

	
	/** Checks if the partner passed as parameter is registered in our endpoint DB
	 * @return true if our endpoint knows about the partner
	 */
	public abstract boolean isPartnerKown (String CN) throws SQLException;
	
	
	/** Because there can be partners without the endpointUrl field filled in the DB, we check here if we know the endpointUrl for the given partner. 
	 */
	public abstract boolean isPartnerUrlKnown (String CN) throws SQLException;
	
	
	/** Creates a partner in the AS2 endpoint DB (depending if the endpointUrl field is given or not, we'll be able to send to the new partner or only to receive from it)
	 * @return true if creation successful
	 */
	public abstract void createNewPartner (String as2Id, String name, String endpointUrl, String mdnURL, X509Certificate cert) throws Exception;
	
	
	/** Modifies the partner's endpointUrl, mdnUrl and/or certificate fingerprint in the AS2 endpoint DB
	 * @return true if modification successful
	 */
	public abstract void updatePartner (String as2Id, String name, String endpointUrl, String mdnURL, X509Certificate cert) throws Exception;
	

	
	public AS2EndpointPartnerInterface()
	{
		properties = PropertiesUtil.getProperties();
		
		dataSource = new BasicDataSource();
		dataSource.setDriverClassName(properties.getProperty(PropertiesUtil.DB_DRIVER_NAME));
		dataSource.setUrl(properties.getProperty(PropertiesUtil.DB_URL));
		dataSource.setUsername(properties.getProperty(PropertiesUtil.DB_USER));
		dataSource.setPassword(properties.getProperty(PropertiesUtil.DB_PASS));
		dataSource.setDefaultAutoCommit(false);
		dataSource.setDefaultReadOnly(false);
		dataSource.setPoolPreparedStatements(false);
		dataSource.setMaxActive(5);
		dataSource.setInitialSize(2);
	}
	
	
	public Connection getConnection() throws SQLException
	{
		Connection connection = null;
		
		int maxConnections = dataSource.getMaxActive();
        if (maxConnections < dataSource.getNumActive() + 1)
        {
            dataSource.setMaxActive(maxConnections + 1);
        }
        
        connection = dataSource.getConnection();
		
		return connection;
	}
		
}
