/*
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
package eu.peppol.start.util;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Jose Gorvenia Narvaez(jose@alfa1lab.com)
 */
public final class Configuration {

    /**
     * Logger to follow this class behavior.
     */
    private org.apache.log4j.Logger logger4J =
            org.apache.log4j.Logger.getLogger(Configuration.class);

    /**
     * Properties path
     */
    private static String PROPERTIES_PATH = "/configServer.properties";

    /**
     * Instance of the Configuration class.
     */
    private static Configuration instance;

    /**
     * Instance of the Properties class.
     */
    private static Properties prop;

    /**
     * Return the instance of the class.
     * @return Instance of Configuration.
     */
    public static synchronized Configuration getInstance() {
        if (instance == null) {
            instance = new Configuration();
        }
        return instance;
    }

    /**
     * Constructor Method.
     */
    private Configuration() {
        InputStream in = getClass().getResourceAsStream(PROPERTIES_PATH);

        logger4J.debug("Configuration loaded");

        prop =  new Properties();

        try {
            prop.load(in);
        } catch (IOException ex) {
            logger4J.error("No configuration file found (configServer.properties)", ex);
            Logger.getLogger(Configuration.class.getName()).log(Level.SEVERE, ex.getMessage());
            throw new
                    RuntimeException("No configuration file found (configServer.properties)");
        } finally {
            try {
                in.close();
            } catch (IOException ex) {
                Logger.getLogger(Configuration.class.getName()).log(Level.SEVERE, ex.getMessage());
            }
        }
    }

    /**
     * Return the value of a property.
     * @param key   name of the property.
     * @return Value of the key.
     */
    public String getProperty(String key) {
        return prop.getProperty(key);
    }
}