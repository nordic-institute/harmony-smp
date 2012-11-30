package eu.europa.ec.cipa.transport.start.client.console;

import java.io.File;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.net.URI;
import java.util.UUID;
import java.util.logging.FileHandler;
import java.util.logging.Level;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.PosixParser;
import org.busdox.transport.identifiers._1.DocumentIdentifierType;
import org.busdox.transport.identifiers._1.ParticipantIdentifierType;
import org.busdox.transport.identifiers._1.ProcessIdentifierType;
import org.busdox.transport.start.cert.ServerConfigFile;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;

import com.phloc.commons.SystemProperties;
import com.phloc.commons.charset.CCharset;
import com.phloc.commons.io.streams.NonBlockingStringWriter;
import com.phloc.commons.io.streams.StringInputStream;
import com.phloc.commons.lang.CGStringHelper;

import eu.europa.ec.cipa.busdox.CBusDox;
import eu.europa.ec.cipa.peppol.identifier.doctype.EPredefinedDocumentTypeIdentifier;
import eu.europa.ec.cipa.peppol.identifier.participant.SimpleParticipantIdentifier;
import eu.europa.ec.cipa.peppol.identifier.process.EPredefinedProcessIdentifier;
import eu.europa.ec.cipa.peppol.sml.ESML;
import eu.europa.ec.cipa.peppol.utils.ConfigFile;
import eu.europa.ec.cipa.smp.client.SMPServiceCaller;
import eu.europa.ec.cipa.transport.IMessageMetadata;
import eu.europa.ec.cipa.transport.MessageMetadata;
import eu.europa.ec.cipa.transport.PingMessageHelper;
import eu.europa.ec.cipa.transport.start.client.AccessPointClient;

public class StartClientConsole {
	
	 private static final Logger log = LoggerFactory.getLogger (StartClientConsole.class);
	 
	private static enum EClientMode { 
		DIRECT_AP, DIRECT_SMP, FULL;
	}
	
	private static void enableProxy(){
		ConfigFile proxyConf = new ConfigFile("configProxy.properties");
		if (proxyConf.getAllKeys() == null ||proxyConf.getAllKeys().isEmpty()){
			log.error("No configProxy.properties file provide proxy will not be configured ");
		}else{
		    System.setProperty ("http.proxyHost", proxyConf.getString("http.proxyHost"));
		    System.setProperty ("http.proxyPort", proxyConf.getString("http.proxyPort"));
		    System.setProperty ("https.proxyHost", proxyConf.getString("https.proxyHost"));
		    System.setProperty ("https.proxyPort", proxyConf.getString("https.proxyPort"));
		}
		
	}
	@Nullable
	private static String _getAccessPointUrl(@Nonnull URI smpAddress,
			@Nonnull final IMessageMetadata aMetadata) throws Exception {
		// SMP client
		final SMPServiceCaller aServiceCaller = new SMPServiceCaller(smpAddress);
		// get service info
		return aServiceCaller.getEndpointAddress(aMetadata.getRecipientID(),
				aMetadata.getDocumentTypeID(), aMetadata.getProcessID());

	}

	@Nullable
	private static String _getAccessPointUrl(
			@Nonnull final IMessageMetadata aMetadata) throws Exception {
		// SMP client
		final SMPServiceCaller aServiceCaller = new SMPServiceCaller(
				aMetadata.getRecipientID(), ESML.PRODUCTION);
		// get service info
		return aServiceCaller.getEndpointAddress(aMetadata.getRecipientID(),
				aMetadata.getDocumentTypeID(), aMetadata.getProcessID());

	}

	@Nonnull
	private static IMessageMetadata _createPingMetadata() {
		final ParticipantIdentifierType aSender = PingMessageHelper.PING_SENDER;
		final ParticipantIdentifierType aRecipient = PingMessageHelper.PING_RECIPIENT;
		final DocumentIdentifierType aDocumentType = PingMessageHelper.PING_DOCUMENT_TYPE;
		final ProcessIdentifierType aProcessIdentifier = PingMessageHelper.PING_PROCESS;
		final String sMessageID = "uuid:" + UUID.randomUUID().toString();
		return new MessageMetadata(sMessageID, "ping-channel", aSender,
				aRecipient, aDocumentType, aProcessIdentifier);
	}

	private static void enableDebug() throws Exception {
		CBusDox.setMetroDebugSystemProperties(true);
		//FileOutputStream fos = new FileOutputStream();
		
		
		// Debug logging
		SystemProperties.setPropertyValue(
				"com.sun.xml.ws.transport.http.client.HttpTransportPipe.dump",
				Boolean.toString(false));
		SystemProperties.setPropertyValue(
				"com.sun.xml.ws.rx.rm.runtime.ClientTube.dump", "true");
		// Metro uses java.util.logging
		java.util.logging.LogManager
				.getLogManager()
				.readConfiguration(
						new StringInputStream(
								"handlers=java.util.logging.ConsoleHandler\r\n"
										+ "java.util.logging.ConsoleHandler.level=FINEST",
								CCharset.CHARSET_ISO_8859_1));
		FileHandler fh = new FileHandler("metroOut.log");
		fh.setLevel(Level.FINEST);
		
		java.util.logging.Logger.getLogger("com.sun.metro.rx").setLevel(
				java.util.logging.Level.FINER);
		java.util.logging.Logger.getLogger("com.sun.metro.rx").addHandler(fh);
		java.util.logging.Logger.getLogger("com.sun.xml.ws").addHandler(fh);
		java.util.logging.Logger.getLogger("com.sun.xml.wss").addHandler(fh);
		
		
		// Metro debugging
		SystemProperties.setPropertyValue(
				"com.sun.xml.ws.rx.mc.runtime.McTubeFactory.dump.client.after",
				"true");
		SystemProperties
				.setPropertyValue(
						"com.sun.xml.ws.rx.mc.runtime.McTubeFactory.dump.endpoint.before",
						"true");
		SystemProperties
				.setPropertyValue(
						"com.sun.xml.wss.provider.wsit.SecurityTubeFactory.dump.client.after",
						"true");
		SystemProperties
				.setPropertyValue(
						"com.sun.xml.wss.provider.wsit.SecurityTubeFactory.dump.endpoint.before",
						"true");
		
		
	
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) throws Exception {
		final StartClientOptions aOptions = new StartClientOptions();
		final CommandLine cmd = new PosixParser().parse(aOptions, args);
		EClientMode mode = null;
		boolean bGoodCmd = true;
		System.setProperty ("java.net.useSystemProxies", "true"); 
		
		if (cmd.hasOption("debug")&& Boolean.valueOf(cmd.getOptionValue("debug"))) enableDebug();
		
		if (cmd.hasOption("proxy")&& Boolean.valueOf(cmd.getOptionValue("proxy"))) enableProxy();
		
		if (!cmd.hasOption("s") || !cmd.hasOption("r") || !cmd.hasOption("p")
				|| !cmd.hasOption("d") || !cmd.hasOption("dpath")) {
			System.out
					.println("You did not specify all the mandatory parameters please check the help");
			bGoodCmd = false;
		}

		if (!cmd.hasOption("m")) {
			bGoodCmd = false;
		} else {
			mode = EClientMode.valueOf(cmd.getOptionValue("m"));
			switch (mode) {
			case DIRECT_AP:
				if (!cmd.hasOption("ap")) {
					System.out.println("AP url required in DIRECT_AP mode ");
					bGoodCmd = false;
				}
				break;
			case DIRECT_SMP:
				if (!cmd.hasOption("smp")) {
					System.out.println("SMP url required in DIRECT_AP mode ");
					bGoodCmd = false;
				}
				break;
			case FULL:
				break;
			default:
				break;
			}
			;
		}
		if (!bGoodCmd) {
			final NonBlockingStringWriter aSW = new NonBlockingStringWriter();
			new HelpFormatter().printHelp(new PrintWriter(aSW),
					HelpFormatter.DEFAULT_WIDTH,
					CGStringHelper.getClassLocalName(StartClientConsole.class),
					null, aOptions, HelpFormatter.DEFAULT_LEFT_PAD,
					HelpFormatter.DEFAULT_DESC_PAD, null);
			System.out.println(aSW);
			System.exit(-3);
		}
		IMessageMetadata md = null;

		if (cmd.hasOption("ping")
				&& Boolean.valueOf(cmd.getOptionValue("ping"))) {
			System.out.println("Sending Ping Messsage");
			md = _createPingMetadata();
		} else {
			final ParticipantIdentifierType aSender = SimpleParticipantIdentifier
					.createWithDefaultScheme(cmd.getOptionValue('s'));
			final ParticipantIdentifierType aRecipient = SimpleParticipantIdentifier
					.createWithDefaultScheme(cmd.getOptionValue('r'));
			final DocumentIdentifierType aDocumentType = EPredefinedDocumentTypeIdentifier
					.valueOf(cmd.getOptionValue('d'))
					.getAsDocumentTypeIdentifier();
			final ProcessIdentifierType aProcessIdentifier = EPredefinedProcessIdentifier
					.valueOf(cmd.getOptionValue('p')).getAsProcessIdentifier();
			String sMessageID = null;
			if (cmd.hasOption("muid")) {
				sMessageID = cmd.getOptionValue("muid");
			} else {
				sMessageID = "uuid:" + UUID.randomUUID().toString();
			}

			md = new MessageMetadata(sMessageID, "test-channel", aSender,
					aRecipient, aDocumentType, aProcessIdentifier);

		}

		File f = new File(cmd.getOptionValue("dpath"));
		DocumentBuilder builder = DocumentBuilderFactory.newInstance()
				.newDocumentBuilder();
		Document d = builder.parse(f);
		String apURL = null;
		switch (mode) {
		case DIRECT_AP:
			apURL = cmd.getOptionValue("ap");
			AccessPointClient.send(apURL, md, d);
			break;
		case DIRECT_SMP:
			apURL = _getAccessPointUrl(new URI(cmd.getOptionValue("smp")), md);
			AccessPointClient.send(apURL, md, d);
			break;
		case FULL:
			apURL = _getAccessPointUrl(md);
			AccessPointClient.send(apURL, md, d);
			break;
		default:
			break;
		}

	}

}
