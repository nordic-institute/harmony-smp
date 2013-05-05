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

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.concurrent.Immutable;

import org.busdox.transport.identifiers._1.ParticipantIdentifierType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.phloc.commons.annotations.PresentForCodeCoverage;
import com.phloc.commons.regex.RegExHelper;

import eu.europa.ec.cipa.peppol.identifier.CIdentifier;
import eu.europa.ec.cipa.peppol.identifier.IdentifierUtils;
import eu.europa.ec.cipa.peppol.identifier.participant.SimpleParticipantIdentifier;
import eu.europa.ec.cipa.sml.server.exceptions.IllegalHostnameException;

/**
 * @author PEPPOL.AT, BRZ, Philip Helger
 */
@Immutable
public final class DNSUtils {
  private static final Logger s_aLogger = LoggerFactory.getLogger (DNSUtils.class);
  private static final String DOMAIN_IDENTIFIER = "((\\p{Alnum})([-]|(\\p{Alnum}))*(\\p{Alnum}))|(\\p{Alnum})";
  private static final String DOMAIN_NAME_RULE = "(" + DOMAIN_IDENTIFIER + ")((\\.)(" + DOMAIN_IDENTIFIER + "))*";

  @PresentForCodeCoverage
  @SuppressWarnings ("unused")
  private static final DNSUtils s_aInstance = new DNSUtils ();

  private DNSUtils () {}

  /**
   * Utility methods which extracts Identifier Hash Value from DNS record.
   * Additionally it is checked whether the passed DNS name belongs to the given
   * SML zone.
   * 
   * @param sDnsName
   *        The DNS service name.
   * @param sSmlZoneName
   *        The SML zone name to which the DNS name must belong
   * @return the hash value from the DNS name or <code>null</code> if parsing
   *         failed
   */
  @Nullable
  public static String getIdentifierHashValueFromDnsName (@Nonnull final String sDnsName,
                                                          @Nonnull final String sSmlZoneName) {
    final ParticipantIdentifierType aPI = getIdentiferFromDnsName (sDnsName, sSmlZoneName);
    return aPI == null ? null : aPI.getValue ();
  }

  /**
   * Utility methods which extracts Identifier Hash Value from DNS record.
   * 
   * @param sDnsName
   * @return the hash value from the DNS name or <code>null</code> if parsing
   *         failed
   */
  @Nullable
  public static String getIdentifierHashValueFromDnsName (@Nullable final String sDnsName) {
    final ParticipantIdentifierType aPI = getIdentiferFromDnsName (sDnsName);
    return aPI == null ? null : aPI.getValue ();
  }

  /**
   * Utility methods which extracts ParticipantIdentifier from DNS record.
   * 
   * @param sDnsName
   * @param sSmlZoneName
   * @return ParticipantIdentifier or <code>null</code> if parsing failed
   */
  @Nullable
  public static ParticipantIdentifierType getIdentiferFromDnsName (@Nonnull final String sDnsName,
                                                                   @Nonnull final String sSmlZoneName) {
    if (!sDnsName.endsWith ("." + sSmlZoneName)) {
      s_aLogger.warn ("wrong DNS zone : " + sDnsName + " not in : " + sSmlZoneName);
      return null;
    }

    // Remove trailing SML zone name
    final String sIdentifierPart = sDnsName.substring (0, sDnsName.length () - (1 + sSmlZoneName.length ()));
    return getIdentiferFromDnsName (sIdentifierPart);
  }

  /**
   * Utility methods which extracts ParticipantIdentifier from DNS record.
   * 
   * @param sDNSName
   * @return ParticipantIdentifier or <code>null</code> if parsing failed
   */
  @Nullable
  public static ParticipantIdentifierType getIdentiferFromDnsName (@Nullable final String sDNSName) {
    // Split in hash, scheme and rest
    final String [] parts = RegExHelper.getSplitToArray (sDNSName, "\\.", 3);
    if (parts.length < 2) {
      s_aLogger.warn ("wrong syntax of identifier - must contain at least on separator : " + sDNSName);
      return null;
    }

    // Check scheme
    final String sSchemeID = parts[1];
    if (!IdentifierUtils.isValidParticipantIdentifierScheme (sSchemeID)) {
      s_aLogger.warn ("wrong syntax of identifier - scheme is invalid : " + sSchemeID);
      return null;
    }

    // check hash
    String sHash = parts[0];
    if (!sHash.startsWith (CIdentifier.DNS_HASHED_IDENTIFIER_PREFIX)) {
      // must start with "B-"
      s_aLogger.warn ("wrong syntax of identifier - must start with : " + CIdentifier.DNS_HASHED_IDENTIFIER_PREFIX);
      return null;
    }

    sHash = sHash.substring (CIdentifier.DNS_HASHED_IDENTIFIER_PREFIX.length ());

    // is it a valid MD5 hash?
    if (sHash.length () != 32) {
      s_aLogger.warn ("id part : " + sHash + " is not hashed; length=" + sHash.length ());
      return null;
    }

    if (!RegExHelper.stringMatchesPattern ("[0-9a-f]{32}", sHash)) {
      s_aLogger.warn ("id part : " + sHash + " contains illegal characters");
      return null;
    }

    // OK, all checks done!
    return new SimpleParticipantIdentifier (parts[1], sHash);
  }

  /**
   * Get the SMP ID from the passed DNS name. SMP DNS names are identified by
   * the ".publisher." identifier in the name.
   * 
   * @param sDnsName
   * @param sSmlZoneName
   * @return <code>null</code> if the passed DNS name is not an SMP DNS name
   */
  @Nullable
  public static String getPublisherAnchorFromDnsName (@Nonnull final String sDnsName, @Nonnull final String sSmlZoneName) {
    if (s_aLogger.isDebugEnabled ())
      s_aLogger.debug ("Get PublisherAnchroFromDnsName : " + sDnsName);

    if (!isHandledZone (sDnsName, sSmlZoneName)) {
      s_aLogger.error ("This is not correct zone : " + sDnsName + " not in : " + sSmlZoneName);
      return null;
    }

    final String sDnsNameLC = sDnsName.toLowerCase ();
    final String sSuffix = ".publisher." + sSmlZoneName;
    if (sDnsNameLC.endsWith (sSuffix))
      return sDnsName.substring (0, sDnsName.length () - sSuffix.length ());

    if (s_aLogger.isDebugEnabled ())
      s_aLogger.debug ("This is not Publisher Anchor " + sDnsName);
    return null;
  }

  public static boolean isHandledZone (@Nonnull final String sDnsName, @Nonnull final String sSmlZoneName) {
    if (s_aLogger.isDebugEnabled ())
      s_aLogger.debug ("isHandledZone : " + sDnsName + " : " + sSmlZoneName);

    return sDnsName.toLowerCase ().endsWith ("." + sSmlZoneName);
  }

  public static void verifyHostname (@Nonnull final String sHostname) throws IllegalHostnameException {
    if (s_aLogger.isDebugEnabled ())
      s_aLogger.debug ("verifyHostname : " + sHostname);

    if (sHostname == null)
      throw new IllegalHostnameException ("Hostname cannot be 'null'");

    if (sHostname.length () > 253)
      throw new IllegalHostnameException ("Hostname total length > 253 : " + sHostname + " : " + sHostname.length ());

    if (!RegExHelper.stringMatchesPattern (DOMAIN_NAME_RULE, sHostname))
      throw new IllegalHostnameException ("Hostname not legal : " + sHostname);

    final String [] aParts = RegExHelper.getSplitToArray (sHostname, "\\.");
    for (final String sPart : aParts)
      if (sPart.length () > 63)
        throw new IllegalHostnameException ("Hostname part length > 63 : " + sHostname);
  }

  public static boolean isValidHostname (final String sHostname) {
    try {
      verifyHostname (sHostname);
      return true;
    }
    catch (final IllegalHostnameException ex) {
      return false;
    }
  }
}
