package eu.domibus.submission.routing.dao;

import eu.domibus.common.dao.BasicDao;
import eu.domibus.submission.routing.BackendFilter;
import org.springframework.stereotype.Service;

import javax.persistence.NoResultException;
import javax.persistence.TypedQuery;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by walcz01 on 04.08.2015.
 */
@Service
public class BackendFilterDao extends BasicDao<BackendFilter> {

    public BackendFilterDao() {
        super(BackendFilter.class);
    }

    public void create(List<BackendFilter> filters) {
        for (int i = 0; i < filters.size(); i++) {
            BackendFilter f = filters.get(i);
            f.setIndex(i);
            super.create(f);
        }
    }

    public void update(List<BackendFilter> filters) {
        for (int i = 0; i < filters.size(); i++) {
            BackendFilter f = filters.get(i);
            f.setIndex(i);
            super.update(f);
        }
    }

    public List<BackendFilter> findAll() {
        final TypedQuery<BackendFilter> query = em.createNamedQuery("BackendFilter.findEntries", BackendFilter.class);
        try {
            return query.getResultList();
        } catch (NoResultException e) {
            return new ArrayList<>();
        }
    }

}
