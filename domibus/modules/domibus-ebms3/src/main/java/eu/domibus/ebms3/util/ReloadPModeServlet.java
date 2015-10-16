package eu.domibus.ebms3.util;
import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;



import eu.domibus.ebms3.config.PModePool;
import eu.domibus.ebms3.module.Configuration;
import org.apache.log4j.Logger;


public class ReloadPModeServlet extends HttpServlet {

	private static final Logger log = Logger.getLogger(ReloadPModeServlet.class);

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		// TODO Auto-generated method stub
		String pmodefile= req.getParameter("pmodefile");
		log.info("the Pmode file to be reloaded is : " + pmodefile);
//		String directory = Configuration.getPModesDir();
//		String fullFilePath = directory.concat("/").concat(pmodefileName);
		final PModePool pool = PModePool.load(pmodefile);
	        if (pool != null) {
	            Configuration.addPModePool(pool);
	        }
		
	}

}
