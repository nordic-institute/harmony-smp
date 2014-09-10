package eu.europa.ec.cipa.sml.server.dns;

import java.io.IOException;
import java.util.List;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.xbill.DNS.Record;
import org.xbill.DNS.TextParseException;
import org.xbill.DNS.ZoneTransferException;

public interface IDNSClient {
	
	/**
	   * Administrative Zone configured for DNSClient.
	   * 
	   * @return zonename
	   */
	  @Nullable
	  String getDNSZoneName ();

	  /**
	   * SML Zone name configured used by Identifiers for DNSClient. Will be
	   * prefixed on Zone
	   * 
	   * @return zonename
	   */
	  @Nullable
	  String getSMLZoneName ();

	  /**
	   * DNS Server handling Publisher hosts.
	   * 
	   * @return server
	   */
	  @Nullable
	  String getServer ();

	  @Nullable
	  String lookupDNSRecord (@Nonnull String dnsName) throws IOException;

	  /**
	   * Run Zone Transfer and list all records.
	   * 
	   * @return List<org.xbill.DNS.Record>
	   * @throws IOException
	   * @throws ZoneTransferException
	   */
	  @Nullable
	  List <Record> getAllRecords () throws IOException, ZoneTransferException;
	  
	  /**
	   * Checks if name is handled.
	   * 
	   * @param name
	   * @return true or false
	   */
	  boolean isHandledZone (String name);
	  
	  public void addIdentifierRecord(String participant, String publisher) throws TextParseException;
	  
	  public void addpublisherRecord(String publisherHost, String endpoint) throws TextParseException;
	  
	  public void deleteRecord (Record rec);
}
