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
package eu.europa.ec.cipa.webgui.app.components;

import com.vaadin.ui.ComboBox;

import eu.europa.ec.cipa.peppol.identifier.issuingagency.EPredefinedIdentifierIssuingAgency;

/**
 * @author Jerouris
 */
public class PartyAgencyIDSelect extends ComboBox {

  public PartyAgencyIDSelect (final String caption) {
    super (caption);
    setWidth (13, UNITS_EM);
    initData ();
  }

  private void initData () {
    for (final EPredefinedIdentifierIssuingAgency a : EPredefinedIdentifierIssuingAgency.values ()) {
      if (!a.isDeprecated ()) {
        addItem (a.getSchemeID ());
        setItemCaption (a.getSchemeID (), a.getSchemeID () + " (" + a.getSchemeAgency () + ")");
      }
    }
  }

  public String getSelectedAgencyName () {
    final String id = getSelectedAgencyID ();
    for (final EPredefinedIdentifierIssuingAgency a : EPredefinedIdentifierIssuingAgency.values ()) {
      if (!a.isDeprecated ()) {
        if (a.getSchemeID ().equals (id))
          return a.getSchemeAgency ();
      }
    }

    return getItemCaption (getValue ());

  }

  public String getSelectedAgencyID () {
    return (String) getValue ();
  }

}
