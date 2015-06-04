package eu.europa.ec.cipa.sml.server.dns;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.slf4j.LoggerFactory;
import org.xbill.DNS.CNAMERecord;
import org.xbill.DNS.DClass;
import org.xbill.DNS.Name;
import org.xbill.DNS.Record;
import org.xbill.DNS.TextParseException;
import org.xbill.DNS.Type;
import org.xbill.DNS.ZoneTransferException;

public class RunIt {

	static final org.slf4j.Logger logger = LoggerFactory.getLogger(RunIt.class);

	private List<DNSEntery> lSMPHosts = new ArrayList<DNSEntery>();
	private List<DNSEntery> lIdentifierHosts = new ArrayList<DNSEntery>();
	private static final int MAX_RETRY = 10;
	private String smlZoneSuffix = "";
	private String identifierZoneSuffix = "";
	private IDNSClient client = null;

	private IDNSClient getDnsClient() {
		if (client == null) {
			client = DNSClientFactory.getSimpleInstace();
		}
		return client;
	}
	
	public RunIt() {
		// TODO Auto-generated constructor stub
	}

	public static void main(String[] args) {
		RunIt run = new RunIt();
		if (args.length < 1) {
			logger.info("Please provide the full path to the file with the Dns-records file");
		} else {
			File f = new File(args[0]);
			if (!f.exists()) {
				URL url = RunIt.class.getClassLoader().getResource(args[0]);
				if (url != null) {
					try {
						f = new File(url.toURI());
					} catch (URISyntaxException e) {
						logger.error("The file : " + args[0] + " couldn't be found", e);
					}
				} else {
					logger.error("The file : " + args[0] + " couldn't be found");
				}
			}
			List<String> l = run.getLinesOfTheFile(f);
			run.parseLines(l);
			run.PutRecordsInDNS();
			run.check(l);
		}
	}

	private void check(List<String> listEntries) {
		logger.info("Checking that all the entries have been successfully registered. This could take few minutes. Please wait...");
		List<String> errors = new ArrayList<>();
		int entriesCount = 0;
		int i = 0;
		for (String entry : listEntries) {
			i++;
			String dnsName = null;
			String[] lineArray = entry.split("\tCNAME\t");
			if (lineArray != null && lineArray.length > 1) {
				if (entry.contains("publisher.")) {
					// normal host
					dnsName = lineArray[0].trim() + "." + identifierZoneSuffix + "." + DNSClientConfiguration.getZone() + ".";
				} else {
					// smp host
					dnsName = lineArray[0].trim() + "." + DNSClientConfiguration.getSMLZoneName();
				}
				entriesCount++;
				try {
					String rec = getDnsClient().lookupDNSRecord(dnsName);
					if (rec == null) {
						errors.add(dnsName);
					}
				} catch (IOException e) {
					errors.add(dnsName);
				}
			}
			if (i % 100 == 0) {
				logger.info("Checking in progress: " + i + "/≈" + listEntries.size() + ". Please wait...");
			}
		}
		logger.info("----------------------------------------------");
		logger.info("-----------         REPORT       -------------");
		if (!errors.isEmpty()) {
			logger.error("There was some errors in the registration inside the DNS. Some entries coulnd't be registered:");
			for (String error : errors) {
				logger.error(error);
			}
		} else {
			logger.info("All " + entriesCount + " entries have been successfully registered in the dns");
		}
		logger.info("----------------------------------------------");
	}

	private void printDNS() {
		try {
			List<Record> recs = getDnsClient().getAllRecords();
			for (Record rec : recs) {
				logger.info(rec.toString());
			}
		} catch (IOException | ZoneTransferException e) { // TODO Auto-generated
															// catch block
			logger.error(e.getMessage(), e);
		}
	}

	private void deleteAllCNames() {
		try {
			List<Record> recs = getDnsClient().getAllRecords();
			logger.info("Number of records on the list: " + recs.size());
			List<Record> out = new ArrayList<>();
			int i = 0;
			for (Record rec : recs) {
				if (rec.getType() == Type.CNAME || rec.getName().toString().contains(DNSClientConfiguration.getSMLZoneName())) {
					out.add(rec);
					i++;
					if (i == 400) {
						deleteRecordsWithRetry(out);
						out.clear();
						i = 0;
					}
				}
			}
			if (!out.isEmpty())
			{
				deleteRecordsWithRetry(out);
			}
		} catch (IOException | ZoneTransferException e) {
			// TOD!O Auto-generated catch block
			logger.error(e.getMessage(), e);
		}
	}

	private void addSMPHosts() {
		List<Record> delete = new ArrayList<>();
		List<Record> add = new ArrayList<>();
		for (DNSEntery ent : lSMPHosts) {
			try {
				Record[] rec = getDnsClient().getRecordFromName(ent.getName());
				if (rec != null && rec.length > 0) {
					for (Record r : rec) {
						delete.add(r);
					}
				}
				add.add(new CNAMERecord(Name.fromString(ent.getName() + "." + DNSClientConfiguration.getSMLZoneName()), DClass.IN, client.getTTLSecs(),
						new Name(ent.getHost())));
			} catch (TextParseException e) {
				logger.error(e.getMessage(), e);
			}
		}

		deleteRecordsWithRetry(delete);
		addRecordsWithRetry(add);
	}

	private void addRecordsWithRetry(List<Record> recordList){
		addRecordsWithRetry(recordList, 0);
	}

	private void deleteRecordsWithRetry(List<Record> recordList){
		deleteRecordsWithRetry(recordList, 0);
	}

	private void deleteRecordsWithRetry(List<Record> recordList, int retry){
		if (retry > 0) {
			logger.error("Retrying for the " + retry + " time");
		}
		if (retry < MAX_RETRY) {
			try {
				getDnsClient().deleteList(recordList);
			} catch(Throwable exc) {
				if (retry > 5) {
					try {
						wait(2000);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
				logger.warn("An error occurred while deleting records from the DNS.", exc);
				deleteRecordsWithRetry(recordList, retry + 1);
			}
		} else {
			logger.error("FATAL: There was a fatal error. Impossible to delete the records from the DNS after " + retry + " tentatives");
			System.exit(-1);
		}
	}

	private void addRecordsWithRetry(List<Record> recordList, int retry){
		if (retry > 0) {
			logger.error("Retrying for the " + retry + " time");
		}
		if (retry < MAX_RETRY) {
			try {
				getDnsClient().addRecords(recordList);
			} catch(Throwable exc) {
				logger.warn("An error occurred while adding records to the DNS.", exc);
				if (retry > 5) {
					try {
						wait(2000);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
				addRecordsWithRetry(recordList, retry + 1);
			}
		} else {
			logger.error("FATAL: There was a fatal error. Impossible to add the records to the DNS after " + retry + " tentatives");
			System.exit(-1);
		}
	}

	private void addEndpoints() {
		List<Record> updateList = new ArrayList<>();
		int i = 0;
		for (DNSEntery ent : lIdentifierHosts) {
			try {
				Name n = Name.fromString(ent.getName(), Name.fromString(identifierZoneSuffix + "." + DNSClientConfiguration.getZone() + "."));
				updateList.add(new CNAMERecord(n, DClass.IN, client.getTTLSecs(), Name.fromString(ent.getHost())));
				i++;
				if (i == 400) {
					i = 0;
					addRecordsWithRetry(updateList);
					updateList.clear();
				}
			} catch (TextParseException e) {
				logger.error(e.getMessage(), e);
			}
		}
		if (!updateList.isEmpty()) {
			addRecordsWithRetry(updateList);
		}
	}

	private void PutRecordsInDNS() {
		deleteAllCNames();
		addSMPHosts();
		addEndpoints();
		printDNS();

	}

	private void parseLines(List<String> lines) {
		for (String line : lines) {
			if (line.contains("CNAME")) {
				if (!line.contains(".publisher.")) {
					DNSEntery pub = new DNSEntery();
					String s[] = line.split("\tCNAME\t");
					pub.setName(s[0]);
					pub.setHost(s[1]);
					lSMPHosts.add(pub);
				} else {
					DNSEntery pub = new DNSEntery();
					String s[] = line.split("\tCNAME\t");
					pub.setName(s[0]);
					String temp = s[1].replace("publisher.sml.peppolcentral.org.", DNSClientConfiguration.getSMLZoneName());
					pub.setHost(temp);
					lIdentifierHosts.add(pub);
				}
			} else {
				if (line.contains("$ORIGIN")) {
					if (line.contains("publisher.")) {
						String s[] = line.split(" ");
						smlZoneSuffix = s[1];
					}
					if (line.contains("-actorid-")) {
						String s[] = line.split(" ");
						identifierZoneSuffix = s[1];
						String t[] = identifierZoneSuffix.split("\\.");
						identifierZoneSuffix = t[0];
					}
				}
			}
		}
	}

	private List<String> getLinesOfTheFile(File f) {
		InputStream ins = null;
		Reader r = null;
		BufferedReader bf = null;
		List<String> out = new ArrayList<String>();
		String s = null;
		try {
			ins = new FileInputStream(f);
			r = new InputStreamReader(ins, "UTF-8");
			bf = new BufferedReader(r);
			while ((s = bf.readLine()) != null) {
				out.add(s);
			}
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			logger.error(e.getMessage(), e);
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			logger.error(e.getMessage(), e);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			logger.error(e.getMessage(), e);
		} finally {
			if (bf != null) {
				try {
					bf.close();
				} catch (Throwable t) { /* ensure close happens */
					logger.error(t.getMessage(), t);
				}
			}
			if (r != null) {
				try {
					r.close();
				} catch (Throwable t) { /* ensure close happens */
					logger.error(t.getMessage(), t);
				}
			}
			if (ins != null) {
				try {
					ins.close();
				} catch (Throwable t) { /* ensure close happens */
					logger.error(t.getMessage(), t);
				}
			}
		}
		return out;
	}
}
