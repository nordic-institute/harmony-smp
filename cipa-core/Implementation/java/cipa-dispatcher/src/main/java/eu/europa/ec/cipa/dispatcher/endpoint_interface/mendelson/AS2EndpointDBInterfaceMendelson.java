package eu.europa.ec.cipa.dispatcher.endpoint_interface.mendelson;

import java.io.StringWriter;
import java.security.MessageDigest;
import java.security.cert.X509Certificate;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import javax.xml.bind.DatatypeConverter;
import javax.xml.ws.wsaddressing.W3CEndpointReferenceBuilder;

import org.apache.commons.dbcp.BasicDataSource;
import org.bouncycastle.openssl.PEMWriter;
import org.busdox.servicemetadata.publishing._1.EndpointType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.europa.ec.cipa.dispatcher.endpoint_interface.IAS2EndpointDBInterface;
import eu.europa.ec.cipa.dispatcher.util.KeystoreUtil;
import eu.europa.ec.cipa.dispatcher.util.PropertiesUtil;

public class AS2EndpointDBInterfaceMendelson implements IAS2EndpointDBInterface
{
	private static final Logger s_aLogger = LoggerFactory.getLogger (AS2EndpointDBInterfaceMendelson.class);
	
	private BasicDataSource runtimeDataSource;
	private BasicDataSource configDataSource;
	protected Properties properties;
	
	public AS2EndpointDBInterfaceMendelson()
	{
		properties = PropertiesUtil.getProperties(null);
		String url = properties.getProperty(PropertiesUtil.DB_URL);
		if (!url.endsWith("/"))
			url += "/";
		
		runtimeDataSource = new BasicDataSource();
		runtimeDataSource.setDriverClassName(properties.getProperty(PropertiesUtil.DB_DRIVER_NAME));
		runtimeDataSource.setUrl(url + "runtime");
		runtimeDataSource.setUsername(properties.getProperty(PropertiesUtil.DB_USER));
		runtimeDataSource.setPassword(properties.getProperty(PropertiesUtil.DB_PASS));
		runtimeDataSource.setDefaultAutoCommit(false);
		runtimeDataSource.setDefaultReadOnly(false);
		runtimeDataSource.setPoolPreparedStatements(false);
		runtimeDataSource.setMaxActive(5);
		runtimeDataSource.setInitialSize(2);
		
		configDataSource = new BasicDataSource();
		configDataSource.setDriverClassName(properties.getProperty(PropertiesUtil.DB_DRIVER_NAME));
		configDataSource.setUrl(url + "config");
		configDataSource.setUsername(properties.getProperty(PropertiesUtil.DB_USER));
		configDataSource.setPassword(properties.getProperty(PropertiesUtil.DB_PASS));
		configDataSource.setDefaultAutoCommit(false);
		configDataSource.setDefaultReadOnly(false);
		configDataSource.setPoolPreparedStatements(false);
		configDataSource.setMaxActive(5);
		configDataSource.setInitialSize(2);
	}
	
	
	public Connection getRuntimeConnection() throws SQLException
	{
		Connection connection = null;
		
		int maxConnections = runtimeDataSource.getMaxActive();
        if (maxConnections < runtimeDataSource.getNumActive() + 1)
        {
        	runtimeDataSource.setMaxActive(maxConnections + 1);
        }
        
        connection = runtimeDataSource.getConnection();
		
		return connection;
	}
	
	
	public Connection getConfigConnection() throws SQLException
	{
		Connection connection = null;
		
		int maxConnections = configDataSource.getMaxActive();
        if (maxConnections < configDataSource.getNumActive() + 1)
        {
        	configDataSource.setMaxActive(maxConnections + 1);
        }
        
        connection = configDataSource.getConnection();
		
		return connection;
	}
	

	public boolean isPartnerKown(String CN) throws SQLException
	{
		if (CN == null || CN.isEmpty())
			return false;
		
		Connection con = getConfigConnection();
		PreparedStatement statement = con.prepareStatement("select 1 from PARTNER where AS2IDENT = ?");
		statement.setString(1, CN);
        ResultSet result = statement.executeQuery();
        
        if (result==null || result.next()==false)
        	return false;
        else
        	return true;
	}

	
	public String getPartnerUrl (String CN) throws SQLException
	{
		if (CN == null || CN.isEmpty())
			return null;
		
		Connection con = getConfigConnection();
		PreparedStatement statement = con.prepareStatement("select url from PARTNER where AS2IDENT = ?");
		statement.setString(1, CN);
        ResultSet result = statement.executeQuery();
        String url;
        
        if (result==null || result.next()==false)
        	return null;
        else
        {
        	url = result.getString(1);
        	if (url==null || url.isEmpty())
        		return null;
        }
        
        return url;
	}
	
	
	public EndpointType getPartnerData (String CN) throws SQLException
	{
		if (CN == null || CN.isEmpty())
			return null;
		
		EndpointType partner = new EndpointType();
		Connection con = getConfigConnection();
		PreparedStatement statement = con.prepareStatement("select url from PARTNER where AS2IDENT = ?");
		statement.setString(1, CN);
        ResultSet result = statement.executeQuery();
        
        if (result==null || result.next()==false)
        	return null;
        else
        {
        	try
        	{
        	String url = result.getString("url");
        	KeystoreUtil keystore = new KeystoreUtil();
        	X509Certificate cert = keystore.getCertificateByAlias(CN);
        	if (url==null || url.isEmpty() || cert==null)
        		return null;
        	W3CEndpointReferenceBuilder builder = new W3CEndpointReferenceBuilder();
        	builder.address(url);
        	partner.setEndpointReference(builder.build());
//        	partner.setRequireBusinessLevelSignature();
        	StringWriter stringWriter = new StringWriter();
        	PEMWriter pemWriter = new PEMWriter(stringWriter);
        	pemWriter.writeObject(keystore.getCertificateByAlias(CN));
        	pemWriter.flush();
        	partner.setCertificate(stringWriter.toString());
        	}
        	catch (Exception e)
        	{
        		s_aLogger.error("Error in AS2EndpointPartnerInterfaceMendelson.getPartnerData()", e);
        		return null;
        	}
        }
        partner.setTransportProfile("busdox-transport-as2-ver1p0");
        return partner;
	}
	
	
	public void createNewPartner(String as2Id, String name, String endpointUrl, String mdnUrl, X509Certificate cert) throws Exception
	{
		Connection con  = getConfigConnection();
		try
		{
			
			createPartner(con, as2Id, name, endpointUrl, mdnUrl, false, calculateHash(cert) , cert);
						
			con.commit();
		}
		catch (Exception e)
		{
			s_aLogger.error("Erro occure accessing AS2 Partner database", e);
			if (con!=null)
				con.rollback();
			throw new Exception (e);
		}
	}

	
	private void createPartner(Connection con, String as2Id, String name, String endpointUrl, String mdnUrl, boolean isLocal, String fingerprint , X509Certificate cert) throws Exception
	{
		//if demanded, we insert the partner's certificate in the AS2Endpoint keystore
		if(cert != null)
		{
			KeystoreUtil keyStoreAccess = new KeystoreUtil();
			keyStoreAccess.installNewPartnerCertificate(cert, as2Id);
		}
		
		//then we create the partner in the DB
		String insert = "insert into PARTNER (	AS2IDENT,	NAME,	ISLOCAL,	SIGN,	ENCRYPT,	URL,	MDNURL,	SUBJECT,		CONTENTTYPE,				SYNCMDN,	POLLIGNORELIST,	POLLINTERVAL,	COMPRESSION,	SIGNEDMDN,	USECOMMANDONRECEIPT,USEHTTPAUTH,USEHTTPAUTHASYNCMDN,KEEPORIGINALFILENAMEONRECEIPT,	NOTIFYSEND,	NOTIFYRECEIVE,	NOTIFYSENDRECEIVE,	NOTIFYSENDENABLED,	NOTIFYRECEIVEENABLED,	NOTIFYSENDRECEIVEENABLED,	USECOMMANDONSENDERROR,	USECOMMANDONSENDSUCCESS,CONTENTTRANSFERENCODING,HTTPVERSION,MAXPOLLFILES)" +
									" values (	?,			?,		?,			2,		1,			?,		?,		'AS2 message',	'application/EDI-Consent',	1,				null,			10,				1,				1,			0,					0,			0,					0,								0,			0,				0,					0,					0,						0,							0,						0,						1,						'1.1',		100);";
		PreparedStatement statement = con.prepareStatement(insert);
		statement.setString(1, as2Id);
		statement.setString(2, name);
		statement.setInt(3, isLocal? 1:0);
		statement.setString(4, endpointUrl);
		statement.setString(5, mdnUrl);
		statement.executeUpdate();
		
		if (fingerprint!=null)
		{	
			//we retrieve the ID of the partner just created
			statement = con.prepareStatement("select ID from PARTNER where AS2IDENT = ? and NAME = ?");
			statement.setString(1, as2Id);
			statement.setString(2, name);
			ResultSet result = statement.executeQuery();
			result.next();
			int newId = result.getInt("id");
			
			//we add the certificate linked to the partner
			statement = con.prepareStatement("insert into CERTIFICATES (PARTNERID, FINGERPRINTSHA1, CATEGORY, PRIO) values (?, ?, 2, 1)"); //category 2 means the cert is used for signature. category 1 is for encryption, but we dont use encryption.
			statement.setInt(1, newId);
			statement.setString(2, fingerprint);
			statement.executeUpdate();
		}
		
		return;
	}


	public void updatePartner(String as2Id, String name, String endpointUrl, String mdnUrl, X509Certificate cert) throws Exception
	{
		Connection con = getConfigConnection();
		
		String fingerprint = cert==null? null : calculateHash(cert);
		
		try
		{
			//first we retrieve the partner's data that we have
			PreparedStatement statement = con.prepareStatement("select id, url, mdnurl from PARTNER where AS2IDENT = ? and NAME = ?");
			statement.setString(1, as2Id);
			statement.setString(2, name);
			ResultSet result = statement.executeQuery();
			
			if (!result.next())
			{
				//if the partner doesn't exist yet, we create it
				createPartner(con, as2Id, name, endpointUrl, mdnUrl, false, fingerprint, cert);
			}
			else
			{
				int id;
				String url_finalValue;
				String mdnUrl_finalValue;
				
				id = result.getInt("id");
				url_finalValue = result.getString("url");
				mdnUrl_finalValue = result.getString("mdnurl");
				
				//we override the values if something was passed as parameter
				if (endpointUrl!= null && !endpointUrl.isEmpty())
					url_finalValue = endpointUrl;
				if (mdnUrl!=null && !mdnUrl.isEmpty())
					mdnUrl_finalValue = mdnUrl;
				
				//and we update in the DB
				String update = "update PARTNER set URL = ? , MDNURL = ? where AS2IDENT = ? and NAME = ?";
				statement = con.prepareStatement(update);
				statement.setString(1, url_finalValue);
				statement.setString(2, mdnUrl_finalValue);
				statement.setString(3, as2Id);
				statement.setString(4, name);
				statement.executeUpdate();
				
				//and finally we update the cert fingerprint
				if (fingerprint!=null)
				{
					statement = con.prepareStatement("update CERTIFICATES set FINGERPRINTSHA1 = ? where PARTNERID = ?");
					statement.setString(1, fingerprint);
					statement.setInt(2, id);
					statement.executeUpdate();
				}
			}
						
			//we finished
			con.commit();		
		}
		catch (Exception e)
		{
			if (con!=null)
				con.rollback();
			throw new Exception (e);
		}
	}
	
	
	
	public void configureLocalStationIfNeeded() throws Exception
	{
		//a local station needs to have NAME and AS2IDENT with the CommonName value of the AP certificate specified in the config file, and it must be linked to a certificate on the CERTIFICATES table.
		KeystoreUtil keystore = new KeystoreUtil();
		X509Certificate apCert = keystore.getApCertificate();
		String CN = KeystoreUtil.extractCN(apCert);
		String fingerprint = calculateHash(apCert);
		
		Connection con = getConfigConnection();
		PreparedStatement statement = con.prepareStatement("select p.ID from PARTNER p join CERTIFICATES c on p.ID = c.PARTNERID where p.ISLOCAL = 1 and p.AS2IDENT = ? and p.NAME = ? and c.FINGERPRINTSHA1 = ?");
		statement.setString(1, CN);
		statement.setString(2, CN);
		statement.setString(3, fingerprint);
        ResultSet result = statement.executeQuery();
        if (!result.next()) // if there's no such local station, we create one
        	createLocalStation(CN, fingerprint);
	}
	
	
	private void createLocalStation(String CN, String fingerprint) throws Exception
	{
		//delete any partner with the same CN already existing in the DB. Then create default local station.
		Connection con = getConfigConnection();
		try
		{
			PreparedStatement statement = con.prepareStatement("select ID from PARTNER where NAME = ? and AS2IDENT = ?");
			statement.setString(1, CN);
			statement.setString(2, CN);
			ResultSet result = statement.executeQuery();
			if (result.next())
			{
				int id = result.getInt("ID");
				statement = con.prepareStatement("delete from CERTIFICATES where PARTNERID = ?");
				statement.setInt(1, id);
				statement.executeUpdate();
				statement = con.prepareStatement("delete from PARTNERSYSTEM where PARTNERID = ?");
				statement.setInt(1, id);
				statement.executeUpdate();
				statement = con.prepareStatement("delete from PARTNER where ID = ?");
				statement.setInt(1, id);
				statement.executeUpdate();
				statement = con.prepareStatement("update PARTNER set islocal = 0");   //we set all the rest of partners to not local, so we'll only have ours as local.
			}
		
			createPartner(con, CN, CN, "", "foo@example.com", true, fingerprint , null);
			con.commit();
		}
		catch (Exception e)
		{
			s_aLogger.error("Erro occure accessing AS2 Partner database", e);
			if (con!=null)
				con.rollback();
			throw new Exception (e);
		}
	}
	
	
	
	public String getLatestMessageId() throws SQLException
	{
		String messageId = null;
		Connection con = getRuntimeConnection();
		Statement statement = con.createStatement();
		ResultSet result = statement.executeQuery("SELECT LIMIT 0 1 messageid FROM messages ORDER BY initdateutc DESC");
		if (result.next())
		{
			messageId = result.getString(1);
		}
		
        return messageId;
	}
	
	
	public Map<String,Integer> getMessageStatesByOriginalFilenames(Set<String> filenames) throws SQLException
	{
		Map<String,Integer> result = new HashMap<String,Integer>(); 
		Connection con = getRuntimeConnection();
		Statement statement = con.createStatement();
		String query = "select P.originalFileName , M.state from PAYLOAD P join MESSAGES M on P.messageid = M.messageid where ";
		for (String s : filenames)
		{
			query += "P.originalfilename = '" + s + "' or " ;
		}
		query = query.substring(0, query.length()-4);
		ResultSet resultSet = statement.executeQuery(query);
		while (resultSet.next())
		{
			result.put(resultSet.getString("originalFileName"), resultSet.getInt("state"));
		}
		
		return result;
	}
	
	
	public int getMessageState(String messageId) throws SQLException
	{
		Connection con = getRuntimeConnection();
		Statement statement = con.createStatement();
		ResultSet result = statement.executeQuery("SELECT state FROM messages WHERE messageid = '" + messageId + "'");
		if (result.next())
			return result.getInt(1);
		else
			return 0; 
	}

	
	
	private String calculateHash(X509Certificate cert) throws Exception
	{
		MessageDigest md = MessageDigest.getInstance("SHA1");
		md.update(cert.getEncoded());
		byte[] fingerprintBytes = md.digest();
		String fingerprint_aux = DatatypeConverter.printHexBinary(fingerprintBytes);
		StringBuffer fingerprint = new StringBuffer("");
		if (fingerprint_aux.length() > 2)
		{
			for (int i=0 ; i<fingerprint_aux.length() ; i=i+2) //sha-1 output's length is always an even number, so no risks here
			{
				fingerprint.append(fingerprint_aux.substring(i, i+2));
				fingerprint.append(':');
			}
		}
		
		return fingerprint.toString().substring(0, fingerprint.length()-1);
	}

}
