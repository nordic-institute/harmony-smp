package eu.europa.ec.edelivery.smp.config;

import eu.europa.ec.edelivery.smp.data.model.DBConfiguration;
import eu.europa.ec.edelivery.smp.logging.SMPLogger;
import eu.europa.ec.edelivery.smp.logging.SMPLoggerFactory;
import eu.europa.ec.edelivery.smp.utils.PropertyUtils;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import java.time.OffsetDateTime;
import java.util.List;
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
                String prop =trim(dc.getProperty());
                String value =trim(dc.getValue());
                setProperty(prop, value);
                LOG.info("Database property: [{}] value: [{}]", prop,PropertyUtils.getMaskedData(prop, value) );
            }
            lastUpdate = (lastUpdate == null || lastUpdate.isBefore(dc.getLastUpdatedOn())) ? dc.getLastUpdatedOn() : lastUpdate;
        }
    }
    public OffsetDateTime getLastUpdate() {
        return lastUpdate;
    }
}
