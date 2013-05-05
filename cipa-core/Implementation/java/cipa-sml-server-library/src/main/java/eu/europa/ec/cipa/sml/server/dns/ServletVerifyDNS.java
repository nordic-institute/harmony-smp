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
package eu.europa.ec.cipa.sml.server.dns;

import java.io.IOException;
import java.io.OutputStream;
import java.net.URL;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.atomic.AtomicBoolean;

import javax.annotation.Nonnull;
import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.busdox.servicemetadata.locator._1.ParticipantIdentifierPageType;
import org.busdox.servicemetadata.locator._1.PublisherEndpointType;
import org.busdox.servicemetadata.locator._1.ServiceMetadataPublisherServiceType;
import org.busdox.transport.identifiers._1.ParticipantIdentifierType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xbill.DNS.ARecord;
import org.xbill.DNS.CNAMERecord;
import org.xbill.DNS.Record;
import org.xbill.DNS.Type;
import org.xbill.DNS.ZoneTransferException;

import com.phloc.commons.lang.StackTraceHelper;
import com.phloc.commons.url.URLUtils;

import eu.europa.ec.cipa.sml.server.IGenericDataHandler;
import eu.europa.ec.cipa.sml.server.exceptions.InternalErrorException;
import eu.europa.ec.cipa.sml.server.exceptions.NotFoundException;
import eu.europa.ec.cipa.sml.server.management.DataHandlerFactory;

/**
 * Utility servlet that verifies consistency between registration in DNS and
 * Locator Database. First loops through DNS - and verifies against DB. Then
 * loops through DB and verifies against DNS.
 * 
 * @author PEPPOL.AT, BRZ, Philip Helger
 */
public class ServletVerifyDNS extends HttpServlet {
  private static final Logger s_aLogger = LoggerFactory.getLogger (ServletVerifyDNS.class);
  private static final boolean EXECUTE_CONSISTENCY_OPERATIONS = false;

  private static volatile AtomicBoolean s_aVerifyRunning = new AtomicBoolean (false);

  private static void _infoLog (@Nonnull final OutputStream aOS, @Nonnull final String sMessage) throws IOException {
    final String sText = sMessage + "\n";
    aOS.write (sText.getBytes ());
    aOS.flush ();
    if (sMessage.length () > 0)
      s_aLogger.info (sMessage);
  }

  private static void _errorLog (@Nonnull final OutputStream aOS, @Nonnull final String sMessage) throws IOException {
    final String sText = sMessage + "\n";
    aOS.write (sText.getBytes ());
    aOS.flush ();
    s_aLogger.error (sMessage);
  }

  private static void _errorLog (@Nonnull final OutputStream aOS,
                                 @Nonnull final String sMessage,
                                 @Nonnull final Throwable t) throws IOException {
    final String sText = sMessage + "\n" + StackTraceHelper.getStackAsString (t) + "\n";
    aOS.write (sText.getBytes ());
    aOS.flush ();
    s_aLogger.error (sMessage, t);
  }

  /**
   * Verify ParticipantIdentifier. - checks if CNAME is a valid Publisher
   * Anchor. - checks if Publisher Anchor/CNAME existe in DNS. - checks if
   * MetadatPublisher exists for ParticipantIdentifier. -- If NOT : Deletes
   * ParticipantIdentifier in DNS - checks if hostname found in DNS matches
   * hostname found in MetadataPublisher. -- If NOT : hostname is updated for
   * Publisher/Anchor.
   * 
   * @param aOS
   * @param aDNSClient
   * @param aGenericHandler
   * @param sRecordName
   * @param sReferedName
   * @param aParticipantID
   * @throws Exception
   * @throws IOException
   */
  private static void _verifyDNSParticipantIdentifier (@Nonnull final OutputStream aOS,
                                                       @Nonnull final IDNSClient aDNSClient,
                                                       @Nonnull final IGenericDataHandler aGenericHandler,
                                                       @Nonnull final String sRecordName,
                                                       @Nonnull final String sReferedName,
                                                       @Nonnull final ParticipantIdentifierType aParticipantID) throws Exception,
                                                                                                               IOException {
    s_aLogger.info (" - Participant ID " +
                    aParticipantID.getScheme () +
                    "::" +
                    aParticipantID.getValue () +
                    " with " +
                    sReferedName);

    final String sDNSPublisherAnchorID = aDNSClient.getPublisherAnchorFromDnsName (sReferedName);
    if (sDNSPublisherAnchorID == null) {
      _errorLog (aOS, " -  Identifier does not have valid anchor: " + sReferedName);
    }
    else {
      String sDNSPublisher = aDNSClient.lookupPeppolPublisherById (sDNSPublisherAnchorID);
      if (sDNSPublisher == null) {
        _infoLog (aOS, " -  Identifier has Anchor that does not resolve: " + sDNSPublisherAnchorID);
      }
      else {
        if (sDNSPublisher.endsWith ("."))
          sDNSPublisher = sDNSPublisher.substring (0, sDNSPublisher.length () - 1);
        _infoLog (aOS, "   " + sReferedName + " -> " + sDNSPublisher);
      }

      try {
        final ServiceMetadataPublisherServiceType aDBSMP = aGenericHandler.getSMPDataOfParticipant (aParticipantID);
        final String sDBPhysicalAddress = aDBSMP.getPublisherEndpoint ().getPhysicalAddress ();
        final String sDBPhysicalHost = _getHost (sDBPhysicalAddress);

        if (sDBPhysicalHost.equalsIgnoreCase (sDNSPublisher)) {
          // Endpoint is OK!!
          _infoLog (aOS, " -   endpoint host " + sDBPhysicalHost + " is OK");
        }
        else {
          if (sDNSPublisher != null) {
            _infoLog (aOS, " -   endpoint error: " + sDNSPublisher + " != " + sDBPhysicalHost + "; creating");
            if (EXECUTE_CONSISTENCY_OPERATIONS)
              aDNSClient.createPublisherAnchor (sDNSPublisherAnchorID, sDBPhysicalHost);
          }
        }
      }
      catch (final NotFoundException ex) {
        // TOTO: Is this only lookup exception - and not
        // database errors ???
        s_aLogger.error ("ParticipantIdentifier in DNS - but not in Database " + sRecordName, ex);
        _errorLog (aOS, " -   in DNS - but not in Database - deleting in DNS");

        // TODO: How do we clean up
        if (EXECUTE_CONSISTENCY_OPERATIONS)
          aDNSClient.deleteIdentifier (aParticipantID);
      }
      catch (final Throwable t) {
        _errorLog (aOS, " -   Error verifying DNS Record", t);
      }
    }
    _infoLog (aOS, "");
  }

  /**
   * Verify that Publisher/Anchor. Checks if Publisher/Anchor exists in DB and
   * if it has a physical address. If NOT : Delete Publisher/Anchor in DB.
   * 
   * @param aOS
   * @param aDNSClient
   * @param aGenericHandler
   * @param sReferredName
   * @param sPublisherAnchorID
   * @throws Exception
   * @throws InternalErrorException
   * @throws IOException
   */
  private static void _verifyDNSPublisherAnchor (final OutputStream aOS,
                                                 final IDNSClient aDNSClient,
                                                 final IGenericDataHandler aGenericHandler,
                                                 final String sReferredName,
                                                 final String sPublisherAnchorID) throws Throwable {
    _infoLog (aOS, " - Publisher anchor ID: " + sPublisherAnchorID);

    final PublisherEndpointType aPublisherEndpoint = aGenericHandler.getSMPEndpointAddressOfSMPID (sPublisherAnchorID);
    if (aPublisherEndpoint == null) {
      // delete anchor
      _infoLog (aOS, " - No ServiceMetadataPublisher for publisher anchorID - Deleting");
      if (EXECUTE_CONSISTENCY_OPERATIONS)
        aDNSClient.deletePublisherAnchor (sPublisherAnchorID);
    }
    else {
      final String sPhysicalAddress = aPublisherEndpoint.getPhysicalAddress ();
      if (sPhysicalAddress == null) {
        // delete anchor
        _infoLog (aOS, " - ServiceMetadataPublisher is missing physical address - Deleting");
        if (EXECUTE_CONSISTENCY_OPERATIONS)
          aDNSClient.deletePublisherAnchor (sPublisherAnchorID);
      }
      else {
        _infoLog (aOS, " - OK : " + sPhysicalAddress + " -> " + sReferredName);
      }
    }

    _infoLog (aOS, "");
  }

  /**
   * Verify DNS Records.
   * 
   * @param aOS
   *        OutputStream to use. May not be <code>null</code>.
   * @throws Exception
   */
  public static void verifyDNS (@Nonnull final OutputStream aOS) throws Exception {
    if (s_aVerifyRunning.getAndSet (true)) {
      _infoLog (aOS, "DNS Verify is already running...");
      return;
    }

    try {
      final IDNSClient aDNSClient = DNSClientFactory.getInstance ();
      final IGenericDataHandler aGenericHandler = DataHandlerFactory.getGenericDataHandler ();

      _infoLog (aOS, "DNSClient is: " + aDNSClient);
      _infoLog (aOS,
                "DNSServer is: " +
                    aDNSClient.getServer () +
                    " - handling DNS Zone: " +
                    aDNSClient.getDNSZoneName () +
                    " - SML Zone: " +
                    aDNSClient.getSMLZoneName () +
                    " - TTL: " +
                    DNSClientConfiguration.getTTL () +
                    " secs");
      _infoLog (aOS, "Generic DataHandler is: " + aGenericHandler);
      _infoLog (aOS, "=== Verify Records in DNS ===");
      try {
        final List <Record> aRecords = aDNSClient.getAllRecords ();
        _infoLog (aOS, " - retrieved # of records : " + aRecords.size ());

        // Loop through all DNS Records
        for (final Record aRecord : aRecords) {
          final String sRecordName = aRecord.getName ().toString ();
          _infoLog (aOS, sRecordName);

          String sReferredName = null;
          switch (aRecord.getType ()) {
            case Type.CNAME:
              if (!aDNSClient.isHandledZone (sRecordName)) {
                _infoLog (aOS, "  CName entry not in zone");
                continue;
              }
              sReferredName = ((CNAMERecord) aRecord).getAlias ().toString ();
              break;
            case Type.A:
              if (!aDNSClient.isHandledZone (sRecordName)) {
                _infoLog (aOS, "  A entry not in zone");
                continue;
              }
              sReferredName = ((ARecord) aRecord).getAddress ().getHostAddress ();
              if (sReferredName.endsWith (".")) {
                sReferredName = sReferredName.substring (0, sReferredName.length () - 1);
                _infoLog (aOS, "  REFERRED NAME [" + sReferredName + "]");
              }
              if (aDNSClient.getPublisherAnchorFromDnsName (sRecordName) == null) {
                _infoLog (aOS, "  Skipping A entry");
                continue;
              }
              break;
            case Type.NS:
              _infoLog (aOS, "  Skipping NS entry");
              continue;
            case Type.SOA:
              _infoLog (aOS, "  Skipping SOA entry");
              continue;
            default:
              s_aLogger.info ("  Skipping " + Type.string (aRecord.getType ()) + " entry");
              continue;
          }

          // Check if DNS Record is ParticipantIdentifier
          final ParticipantIdentifierType aParticipantID = aDNSClient.getIdentifierFromDnsName (sRecordName);
          if (aParticipantID != null) {
            // NO VERIFY BEFORE LOOKUP BY HASH
            _verifyDNSParticipantIdentifier (aOS,
                                             aDNSClient,
                                             aGenericHandler,
                                             sRecordName,
                                             sReferredName,
                                             aParticipantID);
            // if(true) break;
            continue;
          }

          // Check if DNS Record is PublisherAnchor
          _infoLog (aOS, "Test for PublisherAnchor " + sRecordName);
          final String sPublisherAnchorID = aDNSClient.getPublisherAnchorFromDnsName (sRecordName);
          if (sPublisherAnchorID != null) {
            _verifyDNSPublisherAnchor (aOS, aDNSClient, aGenericHandler, sReferredName, sPublisherAnchorID);
            continue;
          }

          _infoLog (aOS, "");
        }
      }
      catch (final Throwable e) {
        _errorLog (aOS, "DNS Verify failed.", e);
      }

      _infoLog (aOS, "=== Verify Records in Database ===");
      // find all smp's
      try {
        final List <String> aAllSMPIDs = aGenericHandler.getAllSMPIDs ();
        _infoLog (aOS, "Find All Publisher # = " + aAllSMPIDs.size ());

        // Loop through all Publishers in Database
        for (final String sSMPID : aAllSMPIDs) {
          _infoLog (aOS, "");
          _infoLog (aOS, "Validate : " + sSMPID);
          final String dnsPublisherEndpoint = aDNSClient.lookupPeppolPublisherById (sSMPID);
          _infoLog (aOS, " - DNS Publisher endpoint : " + dnsPublisherEndpoint);

          final PublisherEndpointType dbPublisherEndpointType = aGenericHandler.getSMPEndpointAddressOfSMPID (sSMPID);
          final String dbPublisherEndpoint = dbPublisherEndpointType.getPhysicalAddress ();

          _infoLog (aOS, " - DB  Publisher endpoint : " + dbPublisherEndpoint);
          final String dbPublisherEndpointHost = _getHost (dbPublisherEndpoint);

          if (dnsPublisherEndpoint == null || !dbPublisherEndpointHost.equalsIgnoreCase (dnsPublisherEndpoint)) {
            // create new anchor!
            _infoLog (aOS, " - Creating new Anchor : " + sSMPID + " -> " + dbPublisherEndpointHost);
            aDNSClient.createPublisherAnchor (sSMPID, dbPublisherEndpointHost);
          }

          String nextPageIdentifier = "";

          // Loop through all ParticipantIdentifiers for Publisher

          do {
            final ParticipantIdentifierPageType participantIdentifiers = aGenericHandler.listParticipantIdentifiers (nextPageIdentifier,
                                                                                                                     sSMPID);
            _infoLog (aOS, " - # of identifiers for id : " +
                           sSMPID +
                           " == " +
                           participantIdentifiers.getParticipantIdentifier ().size ());
            _infoLog (aOS, "");
            for (final ParticipantIdentifierType pi : participantIdentifiers.getParticipantIdentifier ()) {

              //
              _verifyDBParticipantIdentifier (aOS, aDNSClient, sSMPID, pi);

            }
            nextPageIdentifier = participantIdentifiers.getNextPageIdentifier ();
          } while (nextPageIdentifier != null && nextPageIdentifier.length () > 0);

          _infoLog (aOS, "");

        }
      }
      catch (final Throwable e) {
        _errorLog (aOS, "Failed Verifying Database", e);
      }

      _infoLog (aOS, "Check Done");

      aOS.close ();
    }
    catch (final Exception e) {
      _errorLog (aOS, "Failed to init DNSChecker", e);
    }
    finally {
      s_aVerifyRunning.set (false);
    }
  }

  /**
   * Verifies that ParticipantIdentifier
   * 
   * @param aOS
   * @param aDNSClient
   * @param sSMPID
   * @param aParticipantID
   * @throws Exception
   * @throws IOException
   */
  private static void _verifyDBParticipantIdentifier (@Nonnull final OutputStream aOS,
                                                      @Nonnull final IDNSClient aDNSClient,
                                                      @Nonnull final String sSMPID,
                                                      @Nonnull final ParticipantIdentifierType aParticipantID) throws Exception,
                                                                                                              IOException {
    _infoLog (aOS, " - " + aParticipantID.getScheme () + "::" + aParticipantID.getValue ());

    final String sParticipantDNSName = aDNSClient.getDNSNameOfParticipant (aParticipantID);
    if (sParticipantDNSName == null) {
      _infoLog (aOS, " -   Failed to resolve DNS name");
    }
    else {
      _infoLog (aOS, " -   " + sParticipantDNSName);
      final String sDNSHostName = aDNSClient.lookupDNSRecord (sParticipantDNSName);
      if (sDNSHostName == null) {
        // ParticipantIdentifier not in DNS!
        // sParticipantDNSName does not resolve to an PublisherAnchor
        _infoLog (aOS, "  -   Create ParticipantIdentifier: " + sParticipantDNSName + " -> " + sSMPID);
        if (EXECUTE_CONSISTENCY_OPERATIONS)
          aDNSClient.createIdentifier (aParticipantID, sSMPID);
      }
      else {
        // identifier is already in DNS - verify that it points to the correct
        final String sPublisherAnchorID = aDNSClient.getPublisherAnchorFromDnsName (sDNSHostName);
        if (sPublisherAnchorID == null) {
          // IS THIS ERROR ???
          _infoLog (aOS, " -   Could not get PublisherAnchorId from : " +
                         sParticipantDNSName +
                         " : " +
                         sDNSHostName +
                         " : " +
                         sPublisherAnchorID);
        }
        else {
          if (sPublisherAnchorID.equals (sSMPID)) {
            _infoLog (aOS, " -   Anchor OK!");
          }
          else {
            // SMP id has changed
            _infoLog (aOS, " -   PublisherAnchorID does not match SMP ID: " + sPublisherAnchorID + " != " + sSMPID);
            // create identifier!
            _infoLog (aOS, "  - Create ParticipantIdentifier: " + sParticipantDNSName + " -> " + sSMPID);
            if (EXECUTE_CONSISTENCY_OPERATIONS)
              aDNSClient.createIdentifier (aParticipantID, sSMPID);
          }
        }
      }
    }
    _infoLog (aOS, "");
  }

  /**
   * Utility method to get host name.
   * 
   * @param sEndpoint
   * @return the host name to use
   */
  @Nonnull
  private static String _getHost (@Nonnull final String sEndpoint) {
    final String sEndpointLC = sEndpoint.toLowerCase (Locale.US);
    if (sEndpointLC.startsWith ("http:")) {
      final URL aEndpointUrl = URLUtils.getAsURL (sEndpointLC);
      if (aEndpointUrl != null)
        return aEndpointUrl.getHost ();
    }
    return sEndpointLC;
  }

  // ===== Test Methods

  @SuppressWarnings ("unused")
  private static void _deleteAllDNSRecords () throws IOException, ZoneTransferException {
    final IDNSClient dnsClient = DNSClientFactory.getInstance ();

    final List <Record> records = dnsClient.getAllRecords ();
    for (final Record record : records) {
      final String name = record.getName ().toString ();
      if (record.getType () == Type.NS || record.getType () == Type.SOA) {
        s_aLogger.debug ("SOA / NS : " + name);
        continue;
      }
      final ParticipantIdentifierType pi = dnsClient.getIdentifierFromDnsName (name);
      if (pi != null) {
        s_aLogger.debug ("PI : " + name);
        // dnsClient.deleteZoneRecord (name);
      }
      else
        if (dnsClient.getPublisherAnchorFromDnsName (name) != null) {
          s_aLogger.debug ("ANCHOR : " + name);
          final String anchor = dnsClient.getPublisherAnchorFromDnsName (name);
          dnsClient.deletePublisherAnchor (anchor);
        }
        else {
          s_aLogger.debug ("OTHER RECORD : " + name);
          // dnsClient.deleteZoneRecord (name);
        }
    }
  }

  /**
   * Handle both get/post request.
   * 
   * @param aHttpRequest
   * @param aHttpResponse
   * @throws IOException
   */
  private static void _handleRequest (@Nonnull final HttpServletRequest aHttpRequest,
                                      @Nonnull final HttpServletResponse aHttpResponse) throws IOException {
    final ServletOutputStream aOS = aHttpResponse.getOutputStream ();
    try {
      _infoLog (aOS, "Check DNS Records. Start time " + new Date ());
      verifyDNS (aOS);
    }
    catch (final Exception e) {
      _errorLog (aOS, "Failed to verify DNS vs Database", e);
    }
    finally {
      _infoLog (aOS, "Check DNS Records. End time " + new Date ());
    }
  }

  @Override
  protected void doGet (@Nonnull final HttpServletRequest aHttpRequest, @Nonnull final HttpServletResponse aHttpResponse) throws ServletException,
                                                                                                                         IOException {
    _handleRequest (aHttpRequest, aHttpResponse);
  }

  @Override
  protected void doPost (@Nonnull final HttpServletRequest aHttpRequest,
                         @Nonnull final HttpServletResponse aHttpResponse) throws ServletException, IOException {
    _handleRequest (aHttpRequest, aHttpResponse);
  }
}
