package at.peppol.webgui.app.components.tables;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import at.peppol.webgui.app.components.adapters.Adapter;
import at.peppol.webgui.app.components.adapters.InvoiceItemPropertyAdapter;

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
	  }
	  
	  public void setLine(String lineID, Tadapter pms) {
		  //use for editing....
		  if(getIndexFromID(lineID) > -1){
			  linesFromInvoice.set(getIndexFromID(lineID), (Ttype)pms);
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
		    		tableLines.removeItem (ac);
		    		linesFromInvoice.remove (ac);
		    		index = i;
		    		id = ac.getIDAdapter();
		    		break;
		    	}
		    }
		    if (!id.equals(""))
			    for (int i=index;i<linesFromInvoice.size();i++) {
			    	Tadapter ac = (Tadapter)linesFromInvoice.get(i); 
					tableLines.removeItem(ac);
					linesFromInvoice.remove(ac);
					ac.setIDAdapter(id);
					tableLines.addBean(ac);
					linesFromInvoice.add(i,(Ttype)ac);
					id = String.valueOf((Integer.valueOf(id).intValue() + 1));
					//tableLines.getItem(ac).getBean().setTableLineID(String.valueOf(count));
			    }

	  }
	  
	  public int getIndexFromID(String lineID) {
	    Iterator <Ttype> iterator = linesFromInvoice.iterator ();
	    while (iterator.hasNext()) {
	    	Adapter ac = (Adapter) iterator.next();
	      if (ac.getIDAdapter().equals (lineID)) {
	        int index = linesFromInvoice.indexOf (ac);
	        return index;
	      }
	    }    
	    return -1;
	  }    
	  
	  public Adapter getEntryFromID(String lineID) {
	    Iterator <Ttype> iterator = linesFromInvoice.iterator ();
	    while (iterator.hasNext()) {
	    	Adapter ac = (Adapter) iterator.next();
	    	if (ac.getIDAdapter().equals (lineID)) {
	    		return ac;
	    	}
	    }    
	    return null;
	  }
	
	
}
