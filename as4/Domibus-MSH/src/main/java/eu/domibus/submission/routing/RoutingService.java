package eu.domibus.submission.routing;

import eu.domibus.common.model.org.oasis_open.docs.ebxml_msg.ebms.v3_0.ns.core._200704.UserMessage;
import eu.domibus.submission.BackendConnector;
import eu.domibus.submission.routing.dao.BackendFilterDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by walcz01 on 04.08.2015.
 */
@Service
public class RoutingService {

    @Autowired
    private BackendFilterDao backendFilterDao;

    @Resource(name = "backends")
    private List<BackendConnector> backends;

    @Cacheable(value = "backendFilterCache")
    public List<BackendFilter> getBackendFilters() {
        List<BackendFilter> filters = backendFilterDao.findAll();
        List<BackendConnector> backendsTemp = new ArrayList<>(backends);
        for (BackendFilter filter : filters) {
            boolean filterExists = false;
            for (BackendConnector backend : backendsTemp) {
                if (filter.getBackendName().equals(backend.getName())) {
                    filterExists = true;
                    backendsTemp.remove(backend);
                    break;
                }
            }
            if (!filterExists) {
                filters.remove(filter);
            }
        }
        for (BackendConnector backend : backendsTemp) {
            BackendFilter filter = new BackendFilter();
            filter.setBackendName(backend.getName());
            filters.add(filter);
        }
        return filters;
    }

    @CacheEvict(value = "backendFilterCache", allEntries = true)
    public void updateBackendFilters(List<BackendFilter> filters) {
        backendFilterDao.update(filters);
    }

    public BackendConnector findResponsibleBackend(UserMessage message) {
        for (BackendFilter filter : getBackendFilters()) {
            for (AbstractRoutingCriteria routingCriteria : filter.getRoutingCriterias()) {
                if (routingCriteria.matches(message)) {
                    for (BackendConnector backend : backends) {
                        if (backend.getName().equals(filter.getBackendName())) {
                            return backend;
                        }
                    }
                }
            }
        }
        return null;
    }

}
