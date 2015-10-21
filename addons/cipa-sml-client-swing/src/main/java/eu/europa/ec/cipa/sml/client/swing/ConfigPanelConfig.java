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
package eu.europa.ec.cipa.sml.client.swing;

import java.util.Vector;

import javax.swing.BorderFactory;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import net.miginfocom.layout.AC;
import net.miginfocom.layout.LC;
import net.miginfocom.swing.MigLayout;

import com.helger.commons.collections.ContainerHelper;

import eu.europa.ec.cipa.peppol.sml.ESML;
import eu.europa.ec.cipa.peppol.sml.ISMLInfo;
import eu.europa.ec.cipa.sml.client.swing.utils.SMLInfoNameComparator;
import eu.europa.ec.cipa.sml.client.swing.utils.WrappedSMLInfo;

/**
 * Configuration Panel
 * 
 * @author PEPPOL.AT, BRZ, Jakob Frohnwieser
 */
final class ConfigPanelConfig extends JPanel {
  private final JComboBox m_aCBSMLHost;
  private final JTextField m_aTFSMPID;

  public ConfigPanelConfig () {
    setLayout (new MigLayout (new LC ().fill (), new AC ().size ("label").gap ().align ("left"), new AC ()));
    // setPreferredSize (new Dimension (450, 100));
    setBorder (BorderFactory.createTitledBorder ("Client Configuration"));

    final Vector <ISMLInfo> aSMLHosts = new Vector <ISMLInfo> ();
    for (final ESML eSml : ContainerHelper.getSorted (ESML.values (), new SMLInfoNameComparator ()))
      aSMLHosts.add (new WrappedSMLInfo (eSml));

    final JLabel aLabelHost = new JLabel ("SML Hostname: ");
    m_aCBSMLHost = new JComboBox (aSMLHosts);

    final JLabel aLabelSMPID = new JLabel ("SMP ID: ");
    m_aTFSMPID = new JTextField (15);

    add (aLabelHost);
    add (m_aCBSMLHost, "width 100%,wrap");
    add (aLabelSMPID);
    add (m_aTFSMPID, "width 100%,wrap");

    initData ();
  }

  public void initData () {
    m_aCBSMLHost.setSelectedItem (AppProperties.getInstance ().getSMLInfo ());
    m_aTFSMPID.setText (AppProperties.getInstance ().getSMPID ());
  }

  public void saveData () {
    AppProperties.getInstance ().setSMLInfo ((ISMLInfo) m_aCBSMLHost.getSelectedItem ());
    AppProperties.getInstance ().setSMPID (m_aTFSMPID.getText ());
  }
}
