package at.peppol.webgui.app.utils;

import com.vaadin.ui.Alignment;
import com.vaadin.ui.Field;
import com.vaadin.ui.Form;
import com.vaadin.ui.GridLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.TextField;

@SuppressWarnings("serial")
public class GridForm extends Form {
	GridLayout layout;
	int counter;
	int split;
	
	public GridForm(int rows) {
		this.split = rows;
		layout = new GridLayout(2,split);
		layout.setSpacing(true);
		layout.setMargin(true);
		setLayout(layout);
		counter = 0;
		//setHeight("100%");
		//layout.setHeight("100%");
	}
	  
	@Override
	protected void attachField(Object propertyId, Field field) {
		if (counter%split == 0 && counter!=0) {
			layout.setColumns(layout.getColumns()+2);
	    }
		
		int col = counter/split;
		Label fieldLabel;
		if (field instanceof MyField) {
			if (((MyField)field).getRequiredFlag())
				fieldLabel = Utils.requiredLabel(field.getCaption());
			else
				fieldLabel = new Label(field.getCaption());
		}
		else {
			fieldLabel = new Label(field.getCaption());
		}
		
		field.setCaption(null);
		layout.addComponent(fieldLabel, 2*col, counter%split);
		layout.addComponent(field, 2*col+1, counter%split);
		layout.setComponentAlignment(fieldLabel, Alignment.MIDDLE_RIGHT);
		layout.setComponentAlignment(field, Alignment.MIDDLE_LEFT);
		counter++;
	}  
 
}
