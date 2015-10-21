package eu.europa.ec.cipa.bdmsl.dao;

import eu.europa.ec.cipa.bdmsl.dao.entity.AbstractEntity;
import eu.europa.ec.cipa.common.logging.ILoggingService;
import ma.glasnost.orika.MapperFactory;
import org.springframework.beans.factory.annotation.Autowired;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.Calendar;

/**
 * Created by feriaad on 15/06/2015.
 */
public class AbstractDAOImpl {

    @Autowired
    protected ILoggingService loggingService;

    @Autowired
    protected MapperFactory mapperFactory;

    @PersistenceContext
    private EntityManager entityManager;

    public EntityManager getEntityManager() {
        return entityManager;
    }

    public void persist(AbstractEntity entity) {
        Calendar now = Calendar.getInstance();
        entity.setCreationDate(now);
        entity.setLastUpdateDate(now);
        getEntityManager().persist(entity);
    }

    public void merge(AbstractEntity entity) {
        entity.setLastUpdateDate(Calendar.getInstance());
        getEntityManager().merge(entity);
    }
}
