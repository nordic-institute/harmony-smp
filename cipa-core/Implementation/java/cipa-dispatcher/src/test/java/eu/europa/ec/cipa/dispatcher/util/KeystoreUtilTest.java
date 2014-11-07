package eu.europa.ec.cipa.dispatcher.util;

import static org.junit.Assert.*;

import java.util.Properties;

import org.junit.Test;

public class KeystoreUtilTest {
	
	private static Properties properties = PropertiesUtil.getProperties(null);

	@Test
	public void test() {
		try {
			KeystoreUtil util = new KeystoreUtil(properties.getProperty(PropertiesUtil.KEYSTORE_PATH), properties.getProperty(PropertiesUtil.KEYSTORE_PASS));
			assertTrue(true);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			fail("Not yet implemented");
		}
		
	}

}
