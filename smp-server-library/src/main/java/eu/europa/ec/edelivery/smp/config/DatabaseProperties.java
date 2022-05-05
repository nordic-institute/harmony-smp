package eu.europa.ec.edelivery.smp.config;

import eu.europa.ec.edelivery.smp.data.model.DBConfiguration;
import eu.europa.ec.edelivery.smp.data.ui.enums.SMPPropertyEnum;
import eu.europa.ec.edelivery.smp.logging.SMPLogger;
import eu.europa.ec.edelivery.smp.logging.SMPLoggerFactory;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Properties;

import static org.apache.commons.lang3.StringUtils.trim;

public class DatabaseProperties extends Properties {
    SMPLogger LOG = SMPLoggerFactory.getLogger(DatabaseProperties.class);
    private static final long serialVersionUID = 1L;

    private OffsetDateTime lastUpdate;

    public DatabaseProperties(EntityManager em) {
        super();
        TypedQuery<DBConfiguration> tq = em.createNamedQuery("DBConfiguration.getAll", DBConfiguration.class);
        List<DBConfiguration> lst = tq.getResultList();
        for (DBConfiguration dc : lst) {
            if (dc.getValue() != null) {
                LOG.info("Database property: [{}] value: [{}]", dc.getProperty(),
                        isSensitiveData(dc.getProperty()) ? "******" : dc.getValue());
                setProperty(trim(dc.getProperty()), trim(dc.getValue()));
            }
            lastUpdate = (lastUpdate == null || lastUpdate.isBefore(dc.getLastUpdatedOn())) ? dc.getLastUpdatedOn() : lastUpdate;
        }
    }

    /**
     * Return true for properties which must not be logged!
     *
     * @param property - value to validate if contains sensitive data
     * @return true if data is sensitive, else return false
     */
    public boolean isSensitiveData(String property) {
        Optional<SMPPropertyEnum> propOpt = SMPPropertyEnum.getByProperty(property);
        if (propOpt.isPresent()) {
            return propOpt.get().isEncrypted() || property.toLowerCase().contains(".password.decrypted");
        }
        LOG.warn("Database property [{}] is not recognized by the SMP!", property);
        return false;
    }

    public OffsetDateTime getLastUpdate() {
        return lastUpdate;
    }
}
