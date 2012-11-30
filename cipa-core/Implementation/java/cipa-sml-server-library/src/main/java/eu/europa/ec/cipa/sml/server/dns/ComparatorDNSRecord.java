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

import org.xbill.DNS.NSRecord;
import org.xbill.DNS.Record;
import org.xbill.DNS.SOARecord;

import com.phloc.commons.compare.AbstractComparator;

import eu.europa.ec.cipa.peppol.identifier.CIdentifier;

/**
 * Comparator for sorting DNS records alphabetically.
 * 
 * @author PEPPOL.AT, BRZ, Philip Helger
 */
public final class ComparatorDNSRecord extends AbstractComparator <Record> {
  @Override
  protected int mainCompare (final Record r1, final Record r2) {
    // SOA records first
    final boolean bSOA1 = r1 instanceof SOARecord;
    final boolean bSOA2 = r2 instanceof SOARecord;
    if (bSOA1 && !bSOA2)
      return -1;
    if (!bSOA1 && bSOA2)
      return 1;

    // Next come NS records
    final boolean bNS1 = r1 instanceof NSRecord;
    final boolean bNS2 = r2 instanceof NSRecord;
    if (bNS1 && !bNS2)
      return -1;
    if (!bNS1 && bNS2)
      return 1;

    // Next come address and CNAME records but the participant IDs come last
    // Participant records are identified by the leading "B-"
    final String sName1 = r1.getName ().toString ();
    final String sName2 = r2.getName ().toString ();
    final boolean bParticipant1 = sName1.startsWith (CIdentifier.DNS_HASHED_IDENTIFIER_PREFIX);
    final boolean bParticipant2 = sName2.startsWith (CIdentifier.DNS_HASHED_IDENTIFIER_PREFIX);
    if (bParticipant1 && !bParticipant2)
      return 1;
    if (!bParticipant1 && bParticipant2)
      return -1;

    // If all are equal so far, sort by name alphabetically
    return sName1.compareTo (sName2);
  }
}
