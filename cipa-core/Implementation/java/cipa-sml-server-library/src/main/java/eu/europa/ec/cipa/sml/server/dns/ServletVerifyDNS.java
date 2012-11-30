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

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Date;
import java.util.List;

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
  private static final Logger log = LoggerFactory.getLogger (ServletVerifyDNS.class);

  @Override
  protected void doGet (final HttpServletRequest req, final HttpServletResponse resp) throws ServletException,
                                                                                     IOException {
    _handleRequest (req, resp);
  }

  @Override
  protected void doPost (final HttpServletRequest req, final HttpServletResponse resp) throws ServletException,
                                                                                      IOException {
    _handleRequest (req, resp);
  }

  /**
   * Handle both get/post request.
   * 
   * @param req
   * @param resp
   * @throws IOException
   */
  private static void _handleRequest (final HttpServletRequest req, final HttpServletResponse resp) throws IOException {
    log.info ("Check DNS Records : " + new Date ());
    final ServletOutputStream os = resp.getOutputStream ();
    try {
      _writeLog (os, "Check DNS Records : " + new Date ());

      doit (os);
    }
    catch (final Exception e) {
      log.error ("Failed to verify DNS vs Database", e);
      try {
        _writeLog (os, "Failed to verify DNS vs Database : " + e);
        _writeLog (os, e);
      }
      catch (final Exception ignore) {}
    }
  }

  /**
   * Write log line to servlet output stream.
   * 
   * @param os
   * @param msg
   * @throws Exception
   */
  private static void _writeLog (final OutputStream os, final String msg) throws Exception {
    os.write (msg.getBytes ());
    os.write ("\n".getBytes ());
    os.flush ();
  }

  /**
   * Write exception to servlet output stream.
   * 
   * @param os
   * @param e
   * @throws Exception
   */
  private static void _writeLog (final OutputStream os, final Exception e) throws Exception {
    // Write exception + stacktrace to print writer
    final StringWriter aSW = new StringWriter ();
    final PrintWriter pw = new PrintWriter (aSW);
    e.printStackTrace (pw);
    pw.flush ();
    pw.close ();
    aSW.append ('\n');

    // write all to the output stream
    os.write (aSW.toString ().getBytes ());
  }

  private static boolean verifyRunning = false;

  /**
   * Verify DNS Records.
   * 
   * @param os
   * @throws Exception
   */
  public static void doit (final OutputStream os) throws Exception {
    synchronized (ServletVerifyDNS.class) {
      if (verifyRunning) {
        log.info ("DNS Verify is already running...");
        _writeLog (os, "DNS Verify is already running..");
        return;
      }
      verifyRunning = true;
    }

    try {
      final IDNSClient aDNSClient = DNSClientFactory.getInstance ();
      final IGenericDataHandler aGenericHandler = DataHandlerFactory.getGenericDataHandler ();

      log.info ("DNSClient is : " + aDNSClient);
      _writeLog (os, "DNSClient is : " + aDNSClient);

      log.info ("DNSServer is : " +
                aDNSClient.getServer () +
                " - handling DNS Zone : " +
                aDNSClient.getDNSZoneName () +
                " - SML Zone : " +
                aDNSClient.getSMLZoneName () +
                " - TTL : " +
                DNSClientConfiguration.getTTL ());
      _writeLog (os,
                 "DNSServer is : " +
                     aDNSClient.getServer () +
                     " - handling DNS Zone : " +
                     aDNSClient.getDNSZoneName () +
                     " - SML Zone : " +
                     aDNSClient.getSMLZoneName () +
                     " - TTL : " +
                     DNSClientConfiguration.getTTL ());
      _writeLog (os, "");

      log.info ("DATAHandler is : " + aGenericHandler);
      _writeLog (os, "DATAHandler is : " + aGenericHandler);
      _writeLog (os, "");

      log.info ("Run DNS Verify..." + aDNSClient.getDNSZoneName () + " : " + aDNSClient.getServer ());
      _writeLog (os, "=== Verify Records in DNS ===");
      try {
        final List <Record> records = aDNSClient.getAllRecords ();

        log.info (" - retrieved # of records : " + records.size ());

        // Loop through all DNS Records
        for (final Record record : records) {
          final String name = record.getName ().toString ();
          //
          _writeLog (os, name);

          String referedName = null;
          switch (record.getType ()) {
            case Type.CNAME:
              if (!aDNSClient.isHandledZone (name)) {
                _writeLog (os, " Not in Zone...\n");
                continue;
              }
              referedName = ((CNAMERecord) record).getAlias ().toString ();
              break;
            case Type.A:
              if (!aDNSClient.isHandledZone (name)) {
                _writeLog (os, " Not in Zone...\n");
                continue;
              }
              referedName = ((ARecord) record).getAddress ().getHostAddress ();
              if (referedName.endsWith (".")) {
                referedName = referedName.substring (0, referedName.length () - 1);
                _writeLog (os, "REFERED NAME [" + referedName + "]");
              }
              if (aDNSClient.getPublisherAnchorFromDnsName (name) == null) {
                _writeLog (os, " Skip A...\n");
                continue;
              }
              break;
            case Type.NS:
            case Type.SOA:
              _writeLog (os, " Skip NS, SOA...\n");
              continue;
            default:
              log.info (" - UNKNOWN : " + Type.string (record.getType ()));
              continue;
          }

          // Check if DNS Record is ParticipantIdentifier

          final ParticipantIdentifierType pi = aDNSClient.getIdentifierFromDnsName (name);

          if (pi != null) {
            // NO VERIFY BEFORE LOOKUP BY HASH
            // verifyDNSParticipantIdentifier(os, dnsClient, handler, name,
            // referedName, pi);
            // if(true) break;
            continue;
          }

          // Check if DNS Record is PublisherAnchor

          _writeLog (os, " Test for PublisherAnchor! " + name);
          //
          final String dnsAnchorId = aDNSClient.getPublisherAnchorFromDnsName (name);
          if (dnsAnchorId != null) {
            //
            _verifyDNSPublisherAnchor (os, aDNSClient, aGenericHandler, referedName, dnsAnchorId);
            continue;
          }

          // log.info(" - Unknown Record : " + r);
          // writeLog(os, " - NOT Handled : " + r);
          _writeLog (os, "");

        }
      }
      catch (final Exception e) {
        log.info ("DNS Verify failed.", e);
        _writeLog (os, "DNS Verify failed.");
        _writeLog (os, e);
      }

      _writeLog (os, "=== Verify Records in Database ===");
      // find all smp's
      try {
        final List <String> ids = aGenericHandler.getAllSMPIDs ();
        log.info ("Find All Publisher # = " + ids.size ());
        _writeLog (os, "Find All Publisher # = " + ids.size ());

        // Loop through all Publishers in Database
        for (final String smpId : ids) {
          _writeLog (os, "");
          _writeLog (os, "Validate : " + smpId);
          final String dnsPublisherEndpoint = aDNSClient.lookupPeppolPublisherById (smpId);
          _writeLog (os, " - DNS Publisher endpoint : " + dnsPublisherEndpoint);

          final PublisherEndpointType dbPublisherEndpointType = aGenericHandler.getSMPEndpointAddressOfSMPID (smpId);
          final String dbPublisherEndpoint = dbPublisherEndpointType.getPhysicalAddress ();

          _writeLog (os, " - DB  Publisher endpoint : " + dbPublisherEndpoint);
          final String dbPublisherEndpointHost = _getHost (dbPublisherEndpoint);

          if (dnsPublisherEndpoint == null || !dbPublisherEndpointHost.equalsIgnoreCase (dnsPublisherEndpoint)) {
            // create new anchor!
            _writeLog (os, " - Creating new Anchor : " + smpId + " -> " + dbPublisherEndpointHost);
            aDNSClient.createPublisherAnchor (smpId, dbPublisherEndpointHost);
          }

          String nextPageIdentifier = "";

          // Loop through all ParticipantIdentifiers for Publisher

          do {
            final ParticipantIdentifierPageType participantIdentifiers = aGenericHandler.listParticipantIdentifiers (nextPageIdentifier,
                                                                                                                     smpId);
            _writeLog (os, " - # of identifiers for id : " +
                           smpId +
                           " == " +
                           participantIdentifiers.getParticipantIdentifier ().size ());
            _writeLog (os, "");
            for (final ParticipantIdentifierType pi : participantIdentifiers.getParticipantIdentifier ()) {

              //
              _verifyDBParticipantIdentifier (os, aDNSClient, smpId, pi);

            }
            nextPageIdentifier = participantIdentifiers.getNextPageIdentifier ();
          } while (nextPageIdentifier != null && nextPageIdentifier.length () > 0);

          _writeLog (os, "");

        }
      }
      catch (final Exception e) {
        log.error ("Failed Verifying Dabtabase : " + e, e);
        _writeLog (os, "Failed Verifying Dabtabase : " + e);
        _writeLog (os, e);
      }

      log.info ("Check Done!");
      _writeLog (os, "Check Done");

      os.close ();
    }
    catch (final Exception e) {
      log.error ("Failed to init DNSChecker : " + e, e);
      try {
        _writeLog (os, "Failed to init DNSChecker : " + e);
        _writeLog (os, e);
      }
      catch (final Exception ignore) {}
    }
    finally {
      verifyRunning = false;
    }
  }

  /**
   * Verifies that ParticipantIdentifier
   * 
   * @param aOS
   * @param aDNSClient
   * @param sSMPID
   * @param aPI
   * @throws Exception
   * @throws IOException
   */
  private static void _verifyDBParticipantIdentifier (final OutputStream aOS,
                                                      final IDNSClient aDNSClient,
                                                      final String sSMPID,
                                                      final ParticipantIdentifierType aPI) throws Exception,
                                                                                          IOException {
    final String piDnsName = aDNSClient.getDNSNameOfParticipant (aPI);
    _writeLog (aOS, aPI.getScheme () + " : " + aPI.getValue ());

    if (piDnsName == null) {
      _writeLog (aOS, " - Unknown ParticipantIdentifier Scheme : " + aPI.getScheme ());
      return;
    }

    final String piDnsAnchor = aDNSClient.lookupDNSRecord (piDnsName);
    if (piDnsAnchor == null) {
      // ParticipantIdentifier not in DNS!
      // piDnsName does not resolve to an PublisherAnchor
      _writeLog (aOS, "  - Create ParticipantIdentifier : " + piDnsName + " -> " + sSMPID);
      aDNSClient.createIdentifier (aPI, sSMPID);
    }
    else {
      // identifier is allready in DNS - verify that it points to the correct
      final String checkAnchorId = aDNSClient.getPublisherAnchorFromDnsName (piDnsAnchor);

      if (checkAnchorId == null) {
        // IS THIS ERROR ???
        _writeLog (aOS, " - Could not get PublisherAnchorId from : " +
                        piDnsName +
                        " : " +
                        piDnsAnchor +
                        " : " +
                        checkAnchorId);
      }
      else {
        if (checkAnchorId.equals (sSMPID)) {
          _writeLog (aOS, " - Anchor OK!");
        }
        else {
          // SMP id has changed
          _writeLog (aOS, " - PublisherAnchor Does not match smpId : " +
                          piDnsName +
                          " - " +
                          checkAnchorId +
                          " != " +
                          sSMPID);
          // create identifier!
          _writeLog (aOS, "  - Create ParticipantIdentifier : " + piDnsName + " -> " + sSMPID);
          aDNSClient.createIdentifier (aPI, sSMPID);

        }
      }
    }

    _writeLog (aOS, "");
  }

  /**
   * Verify that Publisher/Anchor. - checks if Publisher/Anchor exists in DB. --
   * If NOT : Delete Publisher/Anchor in DBS.
   * 
   * @param aOS
   * @param aDNSClient
   * @param aGenericHandler
   * @param referedName
   * @param dnsAnchorId
   * @throws Exception
   * @throws InternalErrorException
   * @throws IOException
   */
  private static void _verifyDNSPublisherAnchor (final OutputStream aOS,
                                                 final IDNSClient aDNSClient,
                                                 final IGenericDataHandler aGenericHandler,
                                                 final String referedName,
                                                 final String dnsAnchorId) throws Exception,
                                                                          InternalErrorException,
                                                                          IOException {
    log.info (" - This is Anchor : " + dnsAnchorId);
    _writeLog (aOS, " - Is Anchor : " + dnsAnchorId);

    final PublisherEndpointType dbPublisherEndpointType = aGenericHandler.getSMPEndpointAddressOfSMPID (dnsAnchorId);
    if (dbPublisherEndpointType == null) {
      // delete anchor
      log.error ("No ServiceMetadataPublisher with id : " + dnsAnchorId);
      _writeLog (aOS, " - No ServiceMetadataPublisher for AnchorID: " + dnsAnchorId + " - Deleting");
      aDNSClient.deletePublisherAnchor (dnsAnchorId);
    }
    else {
      final String dbPublisherEndpoint = dbPublisherEndpointType.getPhysicalAddress ();

      if (dbPublisherEndpoint == null) {
        // delete anchor
        log.error ("No ServiceMetadataPublisher with id : " + dnsAnchorId);
        _writeLog (aOS, " - No ServiceMetadataPublisher for AnchorID: " + dnsAnchorId + " - Deleting");
        aDNSClient.deletePublisherAnchor (dnsAnchorId);
      }
      else {
        log.info ("Found ServiceMetadataPubliser with id : " + dnsAnchorId + " : " + dbPublisherEndpoint);
        _writeLog (aOS, " - OK : " + dbPublisherEndpoint + " -> " + referedName);

      }
    }

    _writeLog (aOS, "");
  }

  /**
   * Verify ParticipantIdentifier. - checks if CNAME is a valid Publisher
   * Anchor. - checks if Publisher Anchor/CNAME existe in DNS. - checks if
   * MetadatPublisher exists for ParticipantIdentifier. -- If NOT : Deletes
   * ParticipantIdentifier in DNS - checks if hostname found in DNS matches
   * hostname found in MetadataPublisher. -- If NOT : hostname is updated for
   * Publisher/Anchor.
   * 
   * @param os
   * @param dnsClient
   * @param handler
   * @param name
   * @param referedName
   * @param pi
   * @throws Exception
   * @throws IOException
   */
  @SuppressWarnings ("unused")
  private static void _verifyDNSParticipantIdentifier (final OutputStream os,
                                                       final IDNSClient dnsClient,
                                                       final IGenericDataHandler handler,
                                                       final String name,
                                                       final String referedName,
                                                       final ParticipantIdentifierType pi) throws Exception,
                                                                                          IOException {
    log.info ("");
    log.info (" - ServiceMetadataPublisherServiceType - lookup : " + pi.getScheme () + " : " + pi.getValue ());
    log.info (" - smpAnchor : " + referedName);

    //

    final String dnsAnchorId = dnsClient.getPublisherAnchorFromDnsName (referedName);
    if (dnsAnchorId == null) {
      log.error ("Identifier does not have valid anchor : " + referedName);
      _writeLog (os, "Identifier does not have valid anchor : " + referedName);
    }

    try {

      String dnsPublisher = dnsClient.lookupPeppolPublisherById (dnsAnchorId);
      if (dnsPublisher == null) {
        log.info (" - dnsAnchor does not specify ServiceMetada publisher : " + dnsAnchorId);
        _writeLog (os, " Identifier has Anchor that does not resolve: " + dnsAnchorId);
      }
      else {
        if (dnsPublisher.endsWith (".")) {
          dnsPublisher = dnsPublisher.substring (0, dnsPublisher.length () - 1);
        }
        log.info (" - " + name + " -> " + dnsPublisher);
        _writeLog (os, " Identifier -> " + referedName + " -> " + dnsPublisher);
      }

      final ServiceMetadataPublisherServiceType metadataPublisher = handler.getSMPDataOfParticipant (pi);

      final PublisherEndpointType dbEndpointType = metadataPublisher.getPublisherEndpoint ();
      final String dbEndpoint = dbEndpointType.getPhysicalAddress ();
      final String dbEndpointHost = _getHost (dbEndpoint);

      log.info (" - endpoint host : " + dbEndpointHost);
      if (dbEndpointHost.equalsIgnoreCase (dnsPublisher)) {
        // Endpoint is OK!!
        log.info (" - " + dnsPublisher + " == " + dbEndpointHost);
        // writeLog(os, " " + dnsPublisher + " == "
        // + dbEndpointUrl.getHost());
        _writeLog (os, " - OK");
      }
      else {
        if (dnsPublisher != null) {
          log.info (" - " + dnsPublisher + " NOT == " + dbEndpointHost);
          _writeLog (os, " - ERROR : " + dnsPublisher + " NOT == " + dbEndpointHost);

          //
          if (dnsAnchorId != null) {
            log.info (" - creating new anchor : " + dnsAnchorId + " -> " + dbEndpointHost);
            _writeLog (os, " - creating new anchor : " + dnsAnchorId + " -> " + dbEndpointHost);
            dnsClient.createPublisherAnchor (dnsAnchorId, dbEndpointHost);
          }
        }
      }
      _writeLog (os, "");
    }
    catch (final NotFoundException noService) {
      // TOTO: Is this only lookup exception - and not
      // database errors ???
      log.error ("ParticipantIdentifier in DNS - but not in Database " + name, noService);
      _writeLog (os, " in DNS - but not in Database");
      // TODO: How do we clean up

      dnsClient.deleteIdentifier (pi);
      log.info (" - Deleted");
      _writeLog (os, " deleted");
    }
    catch (final Exception e) {
      log.error ("Error verifying DNS Record : ", e);
      _writeLog (os, "Error verifying DNS Record : " + e);
      _writeLog (os, e);
    }
  }

  /**
   * Utility method to get host name.
   * 
   * @param endpoint
   * @return the host name to use
   * @throws MalformedURLException
   */
  private static String _getHost (final String endpoint) throws MalformedURLException {
    final String sEndpointLC = endpoint.toLowerCase ();
    if (endpoint.startsWith ("http:")) {
      final URL endpointUrl = new URL (sEndpointLC);
      return endpointUrl.getHost ();
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
        log.debug ("SOA / NS : " + name);
        continue;
      }
      final ParticipantIdentifierType pi = dnsClient.getIdentifierFromDnsName (name);
      if (pi != null) {
        log.debug ("PI : " + name);
        // dnsClient.deleteZoneRecord (name);
      }
      else
        if (dnsClient.getPublisherAnchorFromDnsName (name) != null) {
          log.debug ("ANCHOR : " + name);
          final String anchor = dnsClient.getPublisherAnchorFromDnsName (name);
          dnsClient.deletePublisherAnchor (anchor);
        }
        else {
          log.debug ("OTHER RECORD : " + name);
          // dnsClient.deleteZoneRecord (name);
        }
    }
  }

  @SuppressWarnings ("unused")
  private static void _listAllDNSRecords () throws IOException, ZoneTransferException {
    System.out.println ("TTL : " + DNSClientConfiguration.getTTL ());
    final IDNSClient dnsClient = DNSClientFactory.getInstance ();
    final List <Record> records = dnsClient.getAllRecords ();
    for (final Record record : records) {
      System.out.println (record.toString ());
    }
  }

  public static void main (final String [] args) throws Exception {
    final ByteArrayOutputStream baos = new ByteArrayOutputStream ();
    ServletVerifyDNS.doit (baos);

    System.out.println ("=================================================");
    System.out.write (baos.toByteArray ());
    baos.close ();
    baos.flush ();

    // listAllDNSRecords();
  }
}
