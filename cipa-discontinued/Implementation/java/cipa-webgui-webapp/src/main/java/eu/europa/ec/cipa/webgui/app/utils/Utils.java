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

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;


import com.vaadin.data.Validator.InvalidValueException;
import com.vaadin.event.FieldEvents.BlurEvent;
import com.vaadin.event.FieldEvents.BlurListener;
import com.vaadin.ui.AbstractField;
import com.vaadin.ui.Field;
import com.vaadin.ui.Form;
import com.vaadin.ui.Label;

import eu.europa.ec.cipa.webgui.app.validator.ValidatorsList;

/*import java.nio.file.FileSystems; //jdk1.7
 import java.nio.file.Path; //jdk1.7
 import java.nio.file.Paths; //jdk1.7
 import java.nio.file.WatchEvent; //jdk1.7
 import java.nio.file.WatchKey; //jdk1.7
 import java.nio.file.WatchService; //jdk1.7
 import java.nio.file.StandardWatchEventKinds.*; //jdk1.7 */

public class Utils {

  public static XMLGregorianCalendar DateToGregorian (final Date date) {
    final GregorianCalendar greg = new GregorianCalendar ();
    greg.setTime (date);

    XMLGregorianCalendar XMLDate = null;
    try {
      XMLDate = DatatypeFactory.newInstance ().newXMLGregorianCalendar ();
      XMLDate.setYear (greg.get (Calendar.YEAR));
      XMLDate.setMonth (greg.get (Calendar.MONTH) + 1);
      XMLDate.setDay (greg.get (Calendar.DATE));
    }
    catch (final DatatypeConfigurationException e) {
      Logger.getLogger (Utils.class.getName ()).log (Level.SEVERE, null, e);
    }

    return XMLDate;
  }

  public static Label requiredLabel (final String text) {
    return new Label ("<span>" + text + " <span style=\"color: red;\">*</span></span>", Label.CONTENT_XHTML);
  }

  public static void validateFormFields (final Form form) throws InvalidValueException {
    final Collection <String> props = (Collection <String>) form.getItemPropertyIds ();
    final List <Field> fields = new ArrayList <Field> ();
    for (final String property : props) {
      fields.add (form.getField (property));
    }
    final List <BlurListener> listeners = new ArrayList <BlurListener> ();
    for (final Field f : fields) {
      if (f instanceof AbstractField) {
        final AbstractField ff = (AbstractField) f;
        listeners.addAll ((Collection <BlurListener>) ff.getListeners (BlurEvent.class));
      }
    }
    ValidatorsList.validateListenersNotify (listeners);
    form.validate ();
  }

  public static Collection <BlurListener> getFieldListeners (final Form form) {
    final Collection <?> propertyIds = form.getItemPropertyIds ();
    final List <BlurListener> listeners = new ArrayList <BlurListener> (propertyIds.size ());
    for (final Object itemPropertyId : propertyIds) {
      final Field f = form.getField (itemPropertyId);
      if (f instanceof AbstractField) {
        final AbstractField field = (AbstractField) f;
        final Collection <?> c = field.getListeners (BlurEvent.class);
        for (final Object l : c) {
          listeners.add ((BlurListener) l);
        }
      }

    }

    return listeners;
  }

  /*
   * public static void registerWatcher(Path dir, Button button) { try {
   * WatchService watcher = FileSystems.getDefault().newWatchService(); for (;;)
   * { // wait for key to be signaled WatchKey key = dir.register(watcher,
   * java.nio.file.StandardWatchEventKinds.ENTRY_CREATE,
   * java.nio.file.StandardWatchEventKinds.ENTRY_DELETE,
   * java.nio.file.StandardWatchEventKinds.ENTRY_MODIFY); try { key =
   * watcher.take(); } catch (InterruptedException x) { return; } for
   * (WatchEvent<?> event: key.pollEvents()) { WatchEvent.Kind<?> kind =
   * event.kind(); if (kind == java.nio.file.StandardWatchEventKinds.OVERFLOW) {
   * continue; } WatchEvent<Path> ev = (WatchEvent<Path>)event; Path filename =
   * ev.context(); Path child = dir.resolve(filename);
   * System.out.println("Modified filename: "+filename);
   * System.out.println("Child: "+child); button.click(); } boolean valid =
   * key.reset(); if (!valid) { break; } } }catch (Exception e) {
   * e.printStackTrace(); } }
   */
}
