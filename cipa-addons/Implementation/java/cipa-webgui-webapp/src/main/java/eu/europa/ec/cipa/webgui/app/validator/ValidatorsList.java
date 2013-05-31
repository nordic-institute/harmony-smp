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
package eu.europa.ec.cipa.webgui.app.validator;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import com.vaadin.event.FieldEvents.BlurListener;
import com.vaadin.terminal.UserError;
import com.vaadin.ui.AbstractField;
import com.vaadin.ui.Component;
import com.vaadin.ui.Field;
import com.vaadin.ui.TabSheet;
import com.vaadin.ui.TabSheet.Tab;

public class ValidatorsList {
  static List <BlurListener> listenerList = new ArrayList <BlurListener> ();

  public static void addListener (final BlurListener b) {
    listenerList.add (b);
  }

  public static void addListeners (final Collection <BlurListener> col) {
    final Iterator <BlurListener> iter = col.iterator ();
    while (iter.hasNext ())
      listenerList.add (iter.next ());
  }

  public static void removeListeners (final Collection <BlurListener> col) {
    final Iterator <BlurListener> iter = col.iterator ();
    while (iter.hasNext ())
      listenerList.remove (iter.next ());
  }

  public static List <BlurListener> getListenersList () {
    return listenerList;
  }

  public static void validateListenersNotify () {
    for (int i = 0; i < listenerList.size (); i++) {
      final BlurListener b = listenerList.get (i);
      if (b instanceof RequiredFieldListener) {
        if (((RequiredFieldListener) b).isValid () == false) {
          // AbstractTextField tf = ((RequiredFieldListener)b).getTextField();
          final AbstractField tf = ((RequiredFieldListener) b).getField ();
          tf.setComponentError (new UserError (((RequiredFieldListener) b).getErrorMessage ()));

          final Tab tab = getParentTab (tf);
          if (tab != null)
            tab.setStyleName ("test111");
        }
        else {
          final AbstractField tf = ((RequiredFieldListener) b).getField ();
          final Tab tab = getParentTab (tf);
          if (tab != null)
            tab.setStyleName ("");
        }
      }
    }
  }

  public static void validateListenersNotify (final List <BlurListener> listenerList) {
    for (int i = 0; i < listenerList.size (); i++) {
      final BlurListener b = listenerList.get (i);
      if (b instanceof RequiredFieldListener) {
        if (((RequiredFieldListener) b).isValid () == false) {
          final AbstractField tf = ((RequiredFieldListener) b).getField ();
          tf.setComponentError (new UserError (((RequiredFieldListener) b).getErrorMessage ()));
        }
        else {
          final AbstractField tf = ((RequiredFieldListener) b).getField ();
          tf.setComponentError (null);
        }
      }
    }
  }

  public static Tab getParentTab (final Field f) {
    Component c = f.getParent ();
    Component prev_c = null;
    // while (!(c instanceof TabSheet)) {
    while (!(TabSheet.class.isInstance (c))) {
      if (c == null)
        return null;
      prev_c = c;
      c = c.getParent ();
    }
    final TabSheet tabSheet = (TabSheet) c;
    final Tab tab = tabSheet.getTab (prev_c);

    return tab;
  }

  public static boolean validateListeners () {
    for (int i = 0; i < listenerList.size (); i++) {
      final BlurListener b = listenerList.get (i);
      if (b instanceof RequiredFieldListener) {
        if (((RequiredFieldListener) b).isValid () == false)
          return false;
      }
    }

    return true;
  }

  public static int countListeners () {
    return listenerList.size ();
  }

}
