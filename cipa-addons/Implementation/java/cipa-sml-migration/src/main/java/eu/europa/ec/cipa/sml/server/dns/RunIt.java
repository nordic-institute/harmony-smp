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
import java.util.ArrayList;
import java.util.List;

import org.xbill.DNS.CNAMERecord;
import org.xbill.DNS.DClass;
import org.xbill.DNS.Name;
import org.xbill.DNS.Record;
import org.xbill.DNS.TextParseException;
import org.xbill.DNS.Type;
import org.xbill.DNS.ZoneTransferException;

public class RunIt {

	private List<DNSEntery> lSMPHosts = new ArrayList<DNSEntery>();
	private List<DNSEntery> lIdentifierHosts = new ArrayList<DNSEntery>();
	private String smlZoneSuffix = "";
	private String identieferZoneSuffix = "";
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
			System.out.println("Please provide the full path to the file with the Dns-records file");
		} else {
			File f = new File(args[0]);
			List<String> l = run.getLinesOfTheFile(f);
			run.parseLines(l);
			run.PutRecordsInDNS();
		}
	}

	private void printDNS() {
		try {
			List<Record> recs = getDnsClient().getAllRecords();
			for (Record rec : recs) {
				System.out.println(rec);
			}
		} catch (IOException | ZoneTransferException e) { // TODO Auto-generated
															// catch block
			e.printStackTrace();
		}
	}

	private void deleteAllCNames() {
		try {
			List<Record> recs = getDnsClient().getAllRecords();
			System.out.println("Number of records on the list: " + recs.size());
			List<Record> out = new ArrayList<Record>();
			int i = 0;
			for (Record rec : recs) {
				if (rec.getType() == Type.CNAME || rec.getName().toString().contains(DNSClientConfiguration.getSMLZoneName())) {
					out.add(rec);
					i++;
					if (i == 400) {
						getDnsClient().deleteList(out);
						out.clear();
						i = 0;
					}
				}
			}
			if (!out.isEmpty())
			{
				getDnsClient().deleteList(out);
			}
		} catch (IOException | ZoneTransferException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private void printNumberOfRecords() {
		try {
			List<Record> recs = getDnsClient().getAllRecords();
			System.out.println("Number of records on the list: " + recs.size());
		} catch (IOException | ZoneTransferException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private void addSMLHosts() {
		List<Record> delete = new ArrayList<Record>();
		List<Record> add = new ArrayList<Record>();
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
				e.printStackTrace();
			}
		}

		getDnsClient().deleteList(delete);
		getDnsClient().addRecords(add);
	}

	private void addEndpoints() {
		List<Record> updateList = new ArrayList<>();
		int i = 0;
		for (DNSEntery ent : lIdentifierHosts) {
			try {
				Name n = Name.fromString(ent.getName(), Name.fromString(identieferZoneSuffix + "." + DNSClientConfiguration.getZone() + "."));
				updateList.add(new CNAMERecord(n, DClass.IN, client.getTTLSecs(), Name.fromString(ent.getHost())));
				i++;
				if (i == 400) {
					i = 0;
					getDnsClient().addRecords(updateList);
					updateList.clear();
				}
			} catch (TextParseException e) {
				e.printStackTrace();
			}
		}
		if (!updateList.isEmpty()) {
			getDnsClient().addRecords(updateList);
		}
	}

	private void PutRecordsInDNS() {
		deleteAllCNames();
		addSMLHosts();
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
						identieferZoneSuffix = s[1];
						String t[] = identieferZoneSuffix.split("\\.");
						identieferZoneSuffix = t[0];
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
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			if (bf != null) {
				try {
					bf.close();
				} catch (Throwable t) { /* ensure close happens */
				}
			}
			if (r != null) {
				try {
					r.close();
				} catch (Throwable t) { /* ensure close happens */
				}
			}
			if (ins != null) {
				try {
					ins.close();
				} catch (Throwable t) { /* ensure close happens */
				}
			}
		}
		return out;
	}
}
