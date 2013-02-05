package at.peppol.webgui.app.utils;

import com.vaadin.ui.AbstractTextField;
import com.vaadin.ui.Field;
import com.vaadin.ui.TextField;

public class MyField extends TextField {
	
	boolean requiredFlag = false;
	
	public MyField(String caption) {
		super(caption);
	}
	
	public void setRequiredFlag(boolean f) {
		requiredFlag = f;
	}
	
	public boolean getRequiredFlag() {
		return requiredFlag;
	}
}
