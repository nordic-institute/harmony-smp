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
