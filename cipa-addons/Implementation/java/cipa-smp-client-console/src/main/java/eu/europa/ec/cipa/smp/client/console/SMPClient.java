/**
 * Version: MPL 1.1/EUPL 1.1
 *
 * The contents of this file are subject to the Mozilla Public License Version
 * 1.1 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at:
 * http://www.mozilla.org/MPL/
 *
 * Software distributed under the License is distributed on an "AS IS" basis,
 * WITHOUT WARRANTY OF ANY KIND, either express or implied. See the License
 * for the specific language governing rights and limitations under the
 * License.
 *
 * The Original Code is Copyright The PEPPOL project (http://www.peppol.eu)
 *
 * Alternatively, the contents of this file may be used under the
 * terms of the EUPL, Version 1.1 or - as soon they will be approved
 * by the European Commission - subsequent versions of the EUPL
 * (the "Licence"); You may not use this work except in compliance
 * with the Licence.
 * You may obtain a copy of the Licence at:
 * http://joinup.ec.europa.eu/software/page/eupl/licence-eupl
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the Licence is distributed on an "AS IS" basis,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the Licence for the specific language governing permissions and
 * limitations under the Licence.
 *
 * If you wish to allow use of your version of this file only
 * under the terms of the EUPL License and not to allow others to use
 * your version of this file under the MPL, indicate your decision by
 * deleting the provisions above and replace them with the notice and
 * other provisions required by the EUPL License. If you do not delete
 * the provisions above, a recipient may use your version of this file
 * under either the MPL or the EUPL License.
 */
package eu.europa.ec.cipa.smp.client.console;

import java.io.File;
import java.io.PrintWriter;
import java.net.URI;
import java.util.Calendar;
import java.util.Date;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.xml.ws.wsaddressing.W3CEndpointReference;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.PosixParser;
import org.busdox.servicemetadata.publishing._1.EndpointType;
import org.busdox.servicemetadata.publishing._1.ObjectFactory;
import org.busdox.servicemetadata.publishing._1.ProcessListType;
import org.busdox.servicemetadata.publishing._1.ProcessType;
import org.busdox.servicemetadata.publishing._1.ServiceEndpointList;
import org.busdox.servicemetadata.publishing._1.ServiceGroupReferenceListType;
import org.busdox.servicemetadata.publishing._1.ServiceGroupReferenceType;
import org.busdox.servicemetadata.publishing._1.ServiceGroupType;
import org.busdox.servicemetadata.publishing._1.ServiceInformationType;
import org.busdox.servicemetadata.publishing._1.ServiceMetadataType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.phloc.commons.annotations.Nonempty;
import com.phloc.commons.charset.CCharset;
import com.phloc.commons.io.file.SimpleFileIO;
import com.phloc.commons.io.streams.NonBlockingStringWriter;
import com.phloc.commons.lang.CGStringHelper;
import com.phloc.commons.random.VerySecureRandom;
import com.phloc.commons.string.StringHelper;

import eu.europa.ec.cipa.peppol.identifier.doctype.SimpleDocumentTypeIdentifier;
import eu.europa.ec.cipa.peppol.identifier.participant.SimpleParticipantIdentifier;
import eu.europa.ec.cipa.peppol.identifier.process.SimpleProcessIdentifier;
import eu.europa.ec.cipa.peppol.security.DoNothingTrustManager;
import eu.europa.ec.cipa.peppol.utils.IReadonlyUsernamePWCredentials;
import eu.europa.ec.cipa.peppol.utils.ReadonlyUsernamePWCredentials;
import eu.europa.ec.cipa.peppol.wsaddr.W3CEndpointReferenceUtils;
import eu.europa.ec.cipa.smp.client.CSMPIdentifier;
import eu.europa.ec.cipa.smp.client.SMPServiceCaller;

/**
 * SMP commandline client
 * 
 * @author Itella
 * @author Philip Helger
 */
public final class SMPClient {
  private static enum ECommand {
    ADDGROUP,
    ADD,
    DELGROUP,
    DEL,
    LIST;

    @Nullable
    public static ECommand getFromNameOrNull (@Nullable final String sName) {
      if (StringHelper.hasText (sName))
        for (final ECommand eCommand : values ())
          if (sName.equalsIgnoreCase (eCommand.name ()))
            return eCommand;
      return null;
    }

    @Nonnull
    @Nonempty
    public static String getAllAsString () {
      final StringBuilder ret = new StringBuilder ();
      for (final ECommand eCommand : values ()) {
        if (ret.length () > 0)
          ret.append (',');
        ret.append (eCommand.name ());
      }
      return ret.toString ();
    }
  }

  private static final Logger s_aLogger = LoggerFactory.getLogger (SMPClient.class);

  private final URI m_aSMPAddress;
  private final String m_sSMPUsername;
  private final IReadonlyUsernamePWCredentials m_aSMPCredentials;
  private final String m_sAPAddress;
  private final String m_sCertificateContent;
  private final SimpleParticipantIdentifier m_aParticipantID;
  private final SimpleDocumentTypeIdentifier m_aDocumentType;
  private final SimpleProcessIdentifier m_aProcessID;

  public SMPClient (final URI aSMPAddress,
                    final String sSMPUsername,
                    final String sSMPPassword,
                    final String sParticipantID,
                    final String sDocumentType,
                    final String sProcessID,
                    final String sAPAddress,
                    final String sCertificateContent) {
    m_aSMPAddress = aSMPAddress;
    m_sSMPUsername = sSMPUsername;
    m_aSMPCredentials = new ReadonlyUsernamePWCredentials (sSMPUsername, sSMPPassword);
    m_sAPAddress = sAPAddress;
    m_sCertificateContent = sCertificateContent;
    m_aParticipantID = SimpleParticipantIdentifier.createWithDefaultScheme (sParticipantID);
    m_aDocumentType = SimpleDocumentTypeIdentifier.createWithDefaultScheme (sDocumentType);
    m_aProcessID = SimpleProcessIdentifier.createWithDefaultScheme (sProcessID);
  }

  public static void main (final String [] args) throws Exception {
    if (false) {
      // Enable this section in development mode, if you want to trust all HTTPS
      // certificates
      final SSLContext aSSLContext = SSLContext.getInstance ("SSL");
      aSSLContext.init (null, new TrustManager [] { new DoNothingTrustManager () }, VerySecureRandom.getInstance ());
      HttpsURLConnection.setDefaultSSLSocketFactory (aSSLContext.getSocketFactory ());
    }

    final SMPClientOptions aOptions = new SMPClientOptions ();
    final CommandLine cmd = new PosixParser ().parse (aOptions, args);

    ECommand eAction = null;
    boolean bGoodCmd = true;
    String cert = null;

    if (!cmd.hasOption ("h")) {
      s_aLogger.error ("No Host specified use -h to specify Host");
      bGoodCmd = false;
    }

    if (!cmd.hasOption ("u")) {
      s_aLogger.error ("No Username specified use -u to specify username");
      bGoodCmd = false;
    }

    if (!cmd.hasOption ("p")) {
      s_aLogger.error ("No Password specified use -p to specify password");
      bGoodCmd = false;
    }

    if (!cmd.hasOption ("c")) {
      s_aLogger.error ("No Action specified please use -c parameter to specify command(" +
                       ECommand.getAllAsString () +
                       ")");
      bGoodCmd = false;
    }
    else {
      final String sCommand = cmd.getOptionValue ("c");
      eAction = ECommand.getFromNameOrNull (sCommand);
      if (eAction == null) {
        s_aLogger.error ("Illegal Action specified:" +
                         sCommand +
                         " allowed commands(" +
                         ECommand.getAllAsString () +
                         ")");
        bGoodCmd = false;
      }
      else
        switch (eAction) {
          case ADDGROUP:
            if (!cmd.hasOption ("b")) {
              s_aLogger.error ("No Business/Participant ID specified use -b to specify Business/Participant ID");
              bGoodCmd = false;
            }
            break;
          case DELGROUP:
            if (!cmd.hasOption ("b")) {
              s_aLogger.error ("No Business/Participant ID specified use -b to specify Business/Participant ID");
              bGoodCmd = false;
            }
            break;
          case ADD:
            if (!cmd.hasOption ("a")) {
              s_aLogger.error ("No Accesspoint URL defined use -a to Specifify AP-URL");
              bGoodCmd = false;
            }
            if (!cmd.hasOption ("b")) {
              s_aLogger.error ("No Business/Participant ID specified use -b to specify Business/Participant ID");
              bGoodCmd = false;
            }
            if (!cmd.hasOption ("d")) {
              s_aLogger.error ("No DocumentType ID specified use -d to specify Document Type ID");
              bGoodCmd = false;
            }
            if (!cmd.hasOption ("r")) {
              s_aLogger.error ("No Process ID specified use -r to specify Process ID");
              bGoodCmd = false;
            }
            if (!cmd.hasOption ("e")) {
              s_aLogger.error ("No Certificate PEM file specified use -e to specify Certificate PEM file");
              bGoodCmd = false;
            }
            else {
              cert = SimpleFileIO.readFileAsString (new File (cmd.getOptionValue ('e')), CCharset.CHARSET_ISO_8859_1);
            }
            break;
          case DEL:
            if (!cmd.hasOption ("b")) {
              s_aLogger.error ("No Business/Participant ID specified use -b to specify Business/Participant ID");
              bGoodCmd = false;
            }
            if (!cmd.hasOption ("d")) {
              s_aLogger.error ("No Document Type ID specified use -d to specify Document Type ID");
              bGoodCmd = false;
            }
        }
    }

    if (!bGoodCmd) {
      final NonBlockingStringWriter aSW = new NonBlockingStringWriter ();
      new HelpFormatter ().printHelp (new PrintWriter (aSW),
                                      HelpFormatter.DEFAULT_WIDTH,
                                      CGStringHelper.getClassLocalName (SMPClient.class),
                                      null,
                                      aOptions,
                                      HelpFormatter.DEFAULT_LEFT_PAD,
                                      HelpFormatter.DEFAULT_DESC_PAD,
                                      null);
      s_aLogger.info (aSW.getAsString ());
      System.exit (-3);
    }

    final SMPClient client = new SMPClient (new URI (cmd.getOptionValue ('h')),
                                            cmd.getOptionValue ('u'),
                                            cmd.getOptionValue ('p'),
                                            cmd.getOptionValue ('b'),
                                            cmd.getOptionValue ('d'),
                                            cmd.getOptionValue ('r'),
                                            cmd.getOptionValue ('a'),
                                            cert);

    switch (eAction) {
      case ADDGROUP:
        client._createServiceGroup ();
        break;
      case DELGROUP:
        client._deleteServiceGroup ();
        break;
      case ADD:
        client._addDocument ();
        break;
      case DEL:
        client._deleteDocument ();
        break;
      case LIST:
        client._listDocuments ();
        break;
      default:
        throw new IllegalStateException ();
    }
  }

  private void _createServiceGroup () {
    final ServiceGroupType serviceGroup = new ObjectFactory ().createServiceGroupType ();
    serviceGroup.setParticipantIdentifier (m_aParticipantID);
    final SMPServiceCaller client = new SMPServiceCaller (m_aSMPAddress);
    try {
      client.saveServiceGroup (serviceGroup, m_aSMPCredentials);
    }
    catch (final Exception e) {
      s_aLogger.error ("Failed to create service group", e);
    }
  }

  private void _deleteServiceGroup () {
    final SMPServiceCaller client = new SMPServiceCaller (m_aSMPAddress);
    try {
      client.deleteServiceGroup (m_aParticipantID, m_aSMPCredentials);
    }
    catch (final Exception e) {
      s_aLogger.error ("Failed to delete service group", e);
    }
  }

  private void _listDocuments () {
    final SMPServiceCaller client = new SMPServiceCaller (m_aSMPAddress);
    try {
      final ServiceGroupReferenceListType list = client.getServiceGroupReferenceList (m_sSMPUsername, m_aSMPCredentials);
      for (final ServiceGroupReferenceType gr : list.getServiceGroupReference ())
        System.out.println (gr.getValue () + ":" + gr.getHref ());
    }
    catch (final Exception e) {
      s_aLogger.error ("Failed to list documents", e);
    }
  }

  private void _deleteDocument () {
    final SMPServiceCaller client = new SMPServiceCaller (m_aSMPAddress);
    try {
      client.deleteServiceRegistration (m_aParticipantID, m_aDocumentType, m_aSMPCredentials);
    }
    catch (final Exception e) {
      s_aLogger.error ("Failed to delete document", e);
    }
  }

  private void _addDocument () {
    final SMPServiceCaller client = new SMPServiceCaller (m_aSMPAddress);
    final W3CEndpointReference endpointReferenceType = W3CEndpointReferenceUtils.createEndpointReference (m_sAPAddress);

    final ObjectFactory aObjFactory = new ObjectFactory ();
    final ServiceMetadataType aServiceMetadata = aObjFactory.createServiceMetadataType ();

    {
      final ProcessListType aProcessList = aObjFactory.createProcessListType ();
      {
        final ProcessType aProcess = aObjFactory.createProcessType ();
        aProcess.setProcessIdentifier (m_aProcessID);
        {
          final ServiceEndpointList aServiceEndpointList = aObjFactory.createServiceEndpointList ();
          {
            final EndpointType aEndpoint = aObjFactory.createEndpointType ();
            aEndpoint.setEndpointReference (endpointReferenceType);
            aEndpoint.setTransportProfile (CSMPIdentifier.TRANSPORT_PROFILE_START);

            aEndpoint.setCertificate (m_sCertificateContent);
            aEndpoint.setServiceActivationDate (new Date (System.currentTimeMillis ()));
            aEndpoint.setServiceDescription ("Test service. For Interoperability test usage.");
            final Calendar exp = Calendar.getInstance ();
            exp.roll (Calendar.YEAR, 10);
            aEndpoint.setServiceExpirationDate (exp.getTime ());
            aEndpoint.setTechnicalContactUrl ("");
            aEndpoint.setMinimumAuthenticationLevel ("1");
            aEndpoint.setRequireBusinessLevelSignature (false);
            aServiceEndpointList.getEndpoint ().add (aEndpoint);
          }
          aProcess.setServiceEndpointList (aServiceEndpointList);
        }
        aProcessList.getProcess ().add (aProcess);
      }

      final ServiceInformationType aServiceInformation = aObjFactory.createServiceInformationType ();
      aServiceInformation.setDocumentIdentifier (m_aDocumentType);
      aServiceInformation.setParticipantIdentifier (m_aParticipantID);
      aServiceInformation.setProcessList (aProcessList);
      aServiceMetadata.setServiceInformation (aServiceInformation);
    }
    try {
      client.saveServiceRegistration (aServiceMetadata, m_aSMPCredentials);
    }
    catch (final Exception e) {
      s_aLogger.error ("Failed to add document", e);
    }
  }
}
