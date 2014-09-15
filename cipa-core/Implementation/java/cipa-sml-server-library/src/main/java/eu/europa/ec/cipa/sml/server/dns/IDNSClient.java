package eu.europa.ec.cipa.sml.server.dns;

import java.io.IOException;
import java.util.List;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.xbill.DNS.Record;
import org.xbill.DNS.ZoneTransferException;

public interface IDNSClient {
	
	/**
	   * Administrative Zone configured for DNSClient.
	   * 
	   * @return zonename
	   */
	  @Nullable
	  public String getDNSZoneName ();

	  /**
	   * SML Zone name configured used by Identifiers for DNSClient. Will be
	   * prefixed on Zone
	   * 
	   * @return zonename
	   */
	  @Nullable
	  public String getSMLZoneName ();

	  /**
	   * DNS Server handling Publisher hosts.
	   * 
	   * @return server
	   */
	  @Nullable
	  public String getServer ();
	  
	  public int getTTLSecs();

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
	 
	  public void addRecords(List<Record> records);

	  public void deleteList(List<Record> records);
	  
	  public Record[] getRecordFromName(String name);
}
