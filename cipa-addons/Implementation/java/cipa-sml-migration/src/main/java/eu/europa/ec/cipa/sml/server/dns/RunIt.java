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

import org.xbill.DNS.Record;
import org.xbill.DNS.TextParseException;
import org.xbill.DNS.Type;
import org.xbill.DNS.ZoneTransferException;

public class RunIt {

	private List<DNSEntery> lSMPHosts = new ArrayList<DNSEntery>();
	private List<DNSEntery> lIdentifierHosts = new ArrayList<DNSEntery>();
	private String smlZoneSuffix = "";
	private String identieferZoneSuffix = "";

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
			run.PutInDNS();
		}
	}

	private void PutInDNS() {
		IDNSClient client = DNSClientFactory.getSimpleInstace();

		try {
			List<Record> recs = client.getAllRecords();
			System.out.println("Number of records on the list: " + recs.size());
			for (Record rec : recs) {
				if (rec.getType() == Type.CNAME) {
					client.deleteRecord(rec);
					Thread.sleep(200);
				}
			}
		} catch (IOException | ZoneTransferException | InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		try {
			List<Record> recs = client.getAllRecords();
			System.out.println("Number of records on the list: " + recs.size());
		} catch (IOException | ZoneTransferException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		for (DNSEntery ent : lSMPHosts) {
			try {
				client.addpublisherRecord(ent.getName() + "." + DNSClientConfiguration.getSMLZoneName(), ent.getHost());
				Thread.sleep(200);
			} catch (TextParseException | InterruptedException e) {
				// TODO // Auto-generated catch block
				e.printStackTrace();
			}
		}

		for (DNSEntery ent : lIdentifierHosts) {
			try {

				client.addIdentifierRecord(ent.getName() + "." + identieferZoneSuffix + "." + DNSClientConfiguration.getZone(), ent.getHost());
				Thread.sleep(200);
			} catch (TextParseException | InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		try {
			List<Record> recs = client.getAllRecords();
			for (Record rec : recs) {
				System.out.println(rec);
			}
		} catch (IOException | ZoneTransferException e) { // TODO Auto-generated
															// catch block
			e.printStackTrace();
		}

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
