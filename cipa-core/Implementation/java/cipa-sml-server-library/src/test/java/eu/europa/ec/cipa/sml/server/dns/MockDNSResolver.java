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
import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nonnull;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xbill.DNS.Cache;
import org.xbill.DNS.Credibility;
import org.xbill.DNS.Flags;
import org.xbill.DNS.Message;
import org.xbill.DNS.Name;
import org.xbill.DNS.Opcode;
import org.xbill.DNS.RRset;
import org.xbill.DNS.Rcode;
import org.xbill.DNS.Record;
import org.xbill.DNS.Resolver;
import org.xbill.DNS.ResolverListener;
import org.xbill.DNS.Section;
import org.xbill.DNS.TSIG;
import org.xbill.DNS.TextParseException;
import org.xbill.DNS.Type;
import org.xbill.DNS.Update;

import com.helger.commons.annotations.ReturnsMutableObject;

/**
 * @author PEPPOL.AT, BRZ, Philip Helger
 */
public final class MockDNSResolver implements Resolver {
  private static final Logger s_aLogger = LoggerFactory.getLogger (MockDNSResolver.class);

  private final Name m_aZone;
  private final Cache m_aCache = new Cache ();
  private final List <Record> m_aRecordList = new ArrayList <Record> ();

  public MockDNSResolver (final String z) {
    s_aLogger.debug ("Create DNS Resolver for zone: " + z);
    try {
      m_aZone = new Name (z);
    }
    catch (final TextParseException ex) {
      throw new IllegalArgumentException ("Error parsing '" + z + "'", ex);
    }
  }

  @Nonnull
  @ReturnsMutableObject (reason = "Because it is a mock object only")
  public List <Record> getAllRecords () {
    return m_aRecordList;
  }

  @Override
  public Message send (final Message message) throws IOException {
    final int opcode = message.getHeader ().getOpcode ();
    s_aLogger.debug ("send Message : " +
                     message.getClass ().getCanonicalName () +
                     " : " +
                     opcode +
                     " : " +
                     Opcode.string (opcode));

    final Message response = new Message ();
    response.getHeader ().setOpcode (message.getHeader ().getOpcode ());

    if (message instanceof Update) {
      s_aLogger.debug ("This Is Update!");

      final Update update = (Update) message;

      final Record [] list = update.getSectionArray (Section.UPDATE);
      for (final Record r : list) {
        if (r.getName ().subdomain (m_aZone)) {
          s_aLogger.debug ("Record is in Zone!");
        }
        else {
          s_aLogger.debug ("Record is NOT in Zone! " + r.getName () + " - zone : " + m_aZone);
          response.getHeader ().setRcode (Rcode.NOTAUTH);
          return response;
        }

        if (r.getTTL () == 0 && r.getType () == Type.ANY) {
          s_aLogger.debug ("This is Delete");
          // DELETE
          final RRset [] rrset = m_aCache.findAnyRecords (r.getName (), Type.ANY);
          if (rrset == null) {
            s_aLogger.debug ("NO Record to Delete ");
          }
          else {
            s_aLogger.debug ("Deleting Record " + m_aCache.getSize () + " : " + m_aRecordList.size ());
            m_aCache.flushName (r.getName ());
            m_aRecordList.remove (rrset[0].first ());
            s_aLogger.debug ("Deleting Record " + m_aCache.getSize () + " : " + m_aRecordList.size ());
          }
        }
        else {
          s_aLogger.debug ("Inserting Record " + m_aCache.getSize () + " : " + m_aRecordList.size ());
          m_aCache.addRecord (r, Credibility.NORMAL, message);
          m_aRecordList.add (r);
          s_aLogger.debug ("Inserting Record " + m_aCache.getSize () + " : " + m_aRecordList.size ());
        }
      }

    }
    else {
      if (opcode == Opcode.QUERY) {
        s_aLogger.debug ("Query " +
                         message.getClass ().getCanonicalName () +
                         " : " +
                         opcode +
                         " : " +
                         Opcode.string (opcode));
        final Record [] list = message.getSectionArray (Section.QUESTION);
        if (list.length > 0) {
          final RRset [] rrset = m_aCache.findAnyRecords (list[0].getName (), Type.ANY);

          response.addRecord (list[0], Section.QUESTION);

          if (rrset == null) {
            return response;
          }
          if (rrset.length > 0) {
            response.addRecord (rrset[0].first (), Section.ANSWER);
          }
          response.getHeader ().setFlag (Flags.QR);
          response.getHeader ().setFlag (Flags.AA);
          response.getHeader ().setFlag (Flags.RD);
        }
      }
      else {
        s_aLogger.debug ("UNKNOWN " +
                         message.getClass ().getCanonicalName () +
                         " : " +
                         opcode +
                         " : " +
                         Opcode.string (opcode));
      }

    }

    return response;
  }

  @Override
  public Object sendAsync (final Message message, final ResolverListener arg1) {
    s_aLogger.debug ("### sendAsync : " + message + " : " + arg1);
    return null;
  }

  @Override
  public void setEDNS (final int arg0) {
    s_aLogger.debug ("### setEDNS : " + arg0);
  }

  @Override
  public void setEDNS (final int arg0, final int arg1, final int arg2, @SuppressWarnings ("rawtypes") final List arg3) {
    s_aLogger.debug ("### setEDNS : " + arg0 + " : " + arg1 + " : " + arg2 + " : " + arg3);
  }

  @Override
  public void setIgnoreTruncation (final boolean arg0) {
    s_aLogger.debug ("### setIgnoreTruncation : " + arg0);
  }

  @Override
  public void setPort (final int arg0) {
    s_aLogger.debug ("### setPort : " + arg0);
  }

  @Override
  public void setTCP (final boolean arg0) {
    s_aLogger.debug ("### setTCP : " + arg0);
  }

  @Override
  public void setTSIGKey (final TSIG arg0) {
    s_aLogger.debug ("### setTSIGKey : " + arg0);
  }

  @Override
  public void setTimeout (final int arg0) {
    s_aLogger.debug ("### setTimeout : " + arg0);
  }

  @Override
  public void setTimeout (final int arg0, final int arg1) {
    s_aLogger.debug ("### setTimeout : " + arg0 + " : " + arg1);
  }
}
