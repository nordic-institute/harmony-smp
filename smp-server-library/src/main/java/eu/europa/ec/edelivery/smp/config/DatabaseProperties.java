package eu.europa.ec.edelivery.smp.config;

import eu.europa.ec.edelivery.smp.data.model.DBConfiguration;
import eu.europa.ec.edelivery.smp.logging.SMPLogger;
import eu.europa.ec.edelivery.smp.logging.SMPLoggerFactory;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Properties;

public class DatabaseProperties extends Properties {
    SMPLogger LOG = SMPLoggerFactory.getLogger(DatabaseProperties.class);
    private static final long serialVersionUID = 1L;

    private LocalDateTime lastUpdate;

    public DatabaseProperties(EntityManager em) {
        super();
        TypedQuery<DBConfiguration> tq = em.createNamedQuery("DBConfiguration.getAll", DBConfiguration.class);
        List<DBConfiguration> lst = tq.getResultList();
        for (DBConfiguration dc : lst) {
            if(dc.getValue()!=null) {
                LOG.info("Database property: '{}' value: '{}'",dc.getProperty(),
                        dc.getProperty().toLowerCase().contains("password")?"******": dc.getValue());
                setProperty(dc.getProperty(), dc.getValue());
            }
            lastUpdate = (lastUpdate==null || lastUpdate.isBefore(dc.getLastUpdatedOn()) )? dc.getLastUpdatedOn() :lastUpdate;
        }

    }

    public LocalDateTime getLastUpdate() {
        return lastUpdate;
    }
}
