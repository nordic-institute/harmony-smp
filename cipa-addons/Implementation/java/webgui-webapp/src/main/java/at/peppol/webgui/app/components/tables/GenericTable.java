package at.peppol.webgui.app.components.tables;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import oasis.names.specification.ubl.schema.xsd.commonaggregatecomponents_2.PaymentMeansType;

import at.peppol.webgui.app.components.adapters.Adapter;
import at.peppol.webgui.app.components.adapters.InvoiceItemPropertyAdapter;
import at.peppol.webgui.app.components.adapters.PaymentMeansAdapter;

import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.ui.Table;
import com.vaadin.ui.Window.Notification;

@SuppressWarnings("serial")
public class GenericTable<Ttype,Tadapter extends Adapter> extends Table {
	protected List<Ttype> linesFromInvoice;
	protected BeanItemContainer<Tadapter> tableLines;
	private final List<String> visibleHeaderNames = new ArrayList<String>();
	
	public GenericTable(){};
	
	public void addPropertyWithHeader(String property, String headerName) {
	    tableLines.addNestedContainerProperty(property);
	    setColumnHeader(property, headerName);
	    visibleHeaderNames.add(property);
	  }
	
	public BeanItemContainer<Tadapter> getTableLines() {
		return tableLines;
	}

	  public void setDefinedPropertiesAsVisible() {
	    setVisibleColumns(visibleHeaderNames.toArray());
	  }
	  
	  public void addLine(Tadapter pms) {
		  linesFromInvoice.add((Ttype)pms);
	      tableLines.addBean(pms);
		  //System.out.println(pms.hashCode());
	  }
	  
	  public void setLineItem(String lineID, Tadapter pms) {
		  /*Tadapter originalItem = getItemWithID(lineID);
		  if (originalItem != null) {
			  int index = linesFromInvoice.indexOf(originalItem);
			  System.out.println("Index is "+index);
			  if (index > -1) {
				  linesFromInvoice.set(index, (Ttype)pms);
				  int tableIndex = tableLines.indexOfId(originalItem);
				  System.out.println("Table index is "+tableIndex);
				  tableLines.removeItem(originalItem);
				  tableLines.addItemAt(tableIndex, pms);
			  }
		  }*/
	  }
	  
	  public void setLine(String lineID, Tadapter pms) {
		  //use for editing....
		  int index = getIndexFromID(lineID);
		  if (index > -1) {
			  linesFromInvoice.set(index, (Ttype)pms);
			  //Tadapter tableItem = tableLines.getIdByIndex(index);
			  //tableLines.get
			  
			  tableLines.removeAllItems ();
			  Iterator<Ttype> iterator = linesFromInvoice.iterator();
			  while (iterator.hasNext()) {
				  Object ac = iterator.next();
				  tableLines.addBean((Tadapter) ac);
			  }
		  }
	  }
	  
/*	  public void removeLine(String lineID) {
	    Iterator<Ttype> iterator = linesFromInvoice.iterator ();
	    while (iterator.hasNext()) {
	    	Adapter ac = (Adapter) iterator.next();
	      if (ac.getIDAdapter().equals (lineID)) {
	        tableLines.removeItem (ac);
	        linesFromInvoice.remove (ac);
	        break;
	        
	      }
	    }
	  }
*/	  
	  public void removeLine(String lineID) {
		    String id="";
		    int index=0;
		    
		    for (int i=0;i<linesFromInvoice.size();i++) {
		    	Tadapter ac = (Tadapter)linesFromInvoice.get(i);	
		    	if (ac.getIDAdapter().equals (lineID)) {
		    		linesFromInvoice.remove (ac);
		    		tableLines.removeItem (ac);
		    		index = i;
		    		id = ac.getIDAdapter();
		    		break;
		    	}
		    }
		    if (!id.equals(""))
			    for (int i=index;i<linesFromInvoice.size();i++) {
			    	Tadapter ac = (Tadapter)linesFromInvoice.get(i); 
					linesFromInvoice.remove(ac);
					tableLines.removeItem(ac);
					ac.setIDAdapter(id);
					linesFromInvoice.add(i,(Ttype)ac);
					tableLines.addBean(ac);
					id = String.valueOf((Integer.valueOf(id).intValue() + 1));
					//tableLines.getItem(ac).getBean().setTableLineID(String.valueOf(count));
			    }

	  }
	  
	  public Tadapter getItemWithID(String id) {
		  Collection<Tadapter> collection = tableLines.getItemIds();
		  for (Tadapter ac : collection) {
			  if (ac.getIDAdapter().equals (id)) {
				  return ac;
			  }
		  }    
		  return null;
	  }
	  
	  public int getIndexFromID(String lineID) {
		  Collection<Tadapter> collection = tableLines.getItemIds();
		  for (Tadapter ac : collection) {
			  if (ac.getIDAdapter().equals (lineID)) {
			        int index = linesFromInvoice.indexOf ((Ttype)ac);
			        return index;
			      }
		  }
  	      return -1;
	    /*Iterator <Ttype> iterator = linesFromInvoice.iterator ();
	    while (iterator.hasNext()) {
	    	Tadapter ac = (Tadapter) iterator.next();
	      if (ac.getIDAdapter().equals (lineID)) {
	        int index = linesFromInvoice.indexOf (ac);
	        return index;
	      }
	    }    
	    return -1;*/
	  }    
	  
/*	  public Adapter getEntryFromID(String lineID) {
	    Iterator <Ttype> iterator = linesFromInvoice.iterator ();
	    while (iterator.hasNext()) {
	    	Adapter ac = (Adapter) iterator.next();
	    	if (ac.getIDAdapter().equals (lineID)) {
	    		return ac;
	    	}
	    }    
	    return null;
	  }
*/	
	  public Tadapter getInstanceOfTadapter(Class<Tadapter> aClass) {
		  try {
			  return aClass.newInstance();
	      } catch (InstantiationException e) {
	    	  e.printStackTrace();
	      } catch (IllegalAccessException e) {
	    	  e.printStackTrace();
	      }
	      return null;
	  } 
}
