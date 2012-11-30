package at.peppol.webgui.app.validator.global;

import java.util.ArrayList;
import java.util.List;

import com.vaadin.ui.Component;

public class GlobalValidationsRegistry {
	
	private static List<BaseValidation> list = new ArrayList<BaseValidation>();
		
	public static void setMainComponent(Component c) {
		list.add(new InvoiceLinesNumberValidation(c));
	}
	
	public static List<String> runAll() {
		List<String> resList = new ArrayList<String>();
		for (int i=0;i<list.size();i++) {
			BaseValidation bv = list.get(i);
			String res = bv.run();
			if (res != null)
				resList.add(res);
		}
		
		return resList;
	}
}
