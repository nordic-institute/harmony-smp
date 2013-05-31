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
package eu.europa.ec.cipa.webgui.app.utils;

import com.vaadin.ui.Alignment;
import com.vaadin.ui.Field;
import com.vaadin.ui.Form;
import com.vaadin.ui.GridLayout;
import com.vaadin.ui.Label;

@SuppressWarnings ("serial")
public class GridForm extends Form {
  GridLayout layout;
  int counter;
  int split;

  public GridForm (final int rows) {
    this.split = rows;
    layout = new GridLayout (2, split);
    layout.setSpacing (true);
    layout.setMargin (true);
    setLayout (layout);
    counter = 0;
    // setHeight("100%");
    // layout.setHeight("100%");
  }

  @Override
  protected void attachField (final Object propertyId, final Field field) {
    if (counter % split == 0 && counter != 0) {
      layout.setColumns (layout.getColumns () + 2);
    }

    final int col = counter / split;
    Label fieldLabel;
    if (field instanceof MyField) {
      if (((MyField) field).getRequiredFlag ())
        fieldLabel = Utils.requiredLabel (field.getCaption ());
      else
        fieldLabel = new Label (field.getCaption ());
    }
    else {
      fieldLabel = new Label (field.getCaption ());
    }

    field.setCaption (null);
    layout.addComponent (fieldLabel, 2 * col, counter % split);
    layout.addComponent (field, 2 * col + 1, counter % split);
    layout.setComponentAlignment (fieldLabel, Alignment.MIDDLE_RIGHT);
    layout.setComponentAlignment (field, Alignment.MIDDLE_LEFT);
    counter++;
  }

}
