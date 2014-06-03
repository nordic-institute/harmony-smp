package eu.domibus.common.persistent;

import org.apache.log4j.Logger;
import eu.domibus.common.exceptions.ConfigurationException;
import eu.domibus.common.util.JNDIUtil;

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
        em = getEntityManager();
    }

    public static EntityManager getEntityManager() {
        synchronized (JpaUtil.class) {
            if (emf == null) {
                final String pUnit =
                        JNDIUtil.getStringEnvironmentParameter(eu.domibus.common.Constants.PERSISTENCE_UNIT);
                LOG.debug("Using PersistenceUnit : " + pUnit);
                final Properties hbmProps = new Properties();
                try {
                    hbmProps.load(new FileReader(JNDIUtil.getStringEnvironmentParameter(
                            eu.domibus.common.Constants.DOMIBUS_PERSISTENCE_PROPERTIES)));
                } catch (IOException e) {
                    throw new ConfigurationException("hibernate properties not found", e);
                }
                emf = Persistence.createEntityManagerFactory(pUnit, hbmProps);
            }
        }
        return emf.createEntityManager();
    }

    public void close() {
        emf.close();
    }
}