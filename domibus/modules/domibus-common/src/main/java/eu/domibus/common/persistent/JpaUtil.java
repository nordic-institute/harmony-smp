package eu.domibus.common.persistent;

import eu.domibus.common.Constants;
import eu.domibus.common.exceptions.ConfigurationException;
import eu.domibus.common.util.JNDIUtil;
import org.apache.log4j.Logger;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import java.io.FileReader;
import java.io.IOException;
import java.util.Properties;

/**
 * Basic JPA helper class, handles database operations.
 * <p>
 * JpaUtil.setPersistenceUnit() method must be called first prior to using this
 * utility.
 * </p>
 *
 * @author Hamid Ben Malek
 */
public class JpaUtil

{
    private static final Logger LOG = Logger.getLogger(JpaUtil.class);
    private static EntityManagerFactory emf;
    protected EntityManager em;


    public JpaUtil() {
        this.em = JpaUtil.getEntityManager();
    }

    public static EntityManager getEntityManager() {
        synchronized (JpaUtil.class) {
            if (JpaUtil.emf == null) {
                final String pUnit = JNDIUtil.getStringEnvironmentParameter(Constants.PERSISTENCE_UNIT);
                JpaUtil.LOG.debug("Using PersistenceUnit : " + pUnit);
                final Properties hbmProps = new Properties();
                try {
                    hbmProps.load(new FileReader(JNDIUtil.getStringEnvironmentParameter(
                            eu.domibus.common.Constants.DOMIBUS_PERSISTENCE_PROPERTIES)));
                } catch (IOException e) {
                    throw new ConfigurationException("hibernate properties not found", e);
                }
                JpaUtil.emf = Persistence.createEntityManagerFactory(pUnit, hbmProps);
            }
        }
        return JpaUtil.emf.createEntityManager();
    }

    public void close() {
        JpaUtil.emf.close();
    }
}