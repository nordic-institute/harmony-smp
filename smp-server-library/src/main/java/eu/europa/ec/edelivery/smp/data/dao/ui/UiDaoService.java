package eu.europa.ec.edelivery.smp.data.dao.ui;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;


@Service
public class UiDaoService {

    @PersistenceContext
    protected EntityManager memEManager;
    private static final Logger LOG = LoggerFactory.getLogger(UiDaoService.class);

    /**
     *
     * @param <T>
     * @param type
     * @param searchParams
     * @param forCount
     * @param sortField
     * @param sortOrder
     * @return
     */
    protected <T, D> CriteriaQuery createSearchCriteria(Class<T> type,
                                                        Object searchParams, Class<D> filterType,
                                                        boolean forCount, String sortField, String sortOrder) {
        CriteriaBuilder cb = memEManager.getCriteriaBuilder();
        CriteriaQuery cq = forCount ? cb.createQuery(Long.class
        ) : cb.createQuery(
                type);
        Root<T> om = cq.from(filterType == null ? type : filterType);
        if (forCount) {
            cq.select(cb.count(om));
        } else if (sortField != null) {
            if (sortOrder != null && sortOrder.equalsIgnoreCase("desc")) {
                cq.orderBy(cb.asc(om.get(sortField)));
            } else {
                cq.orderBy(cb.desc(om.get(sortField)));
            }
        } else {
           // cq.orderBy(cb.desc(om.get("Id")));
        }
        List<Predicate> lstPredicate = new ArrayList<>();
        // set order by
        if (searchParams != null) {
            Class cls = searchParams.getClass();
            Method[] methodList = cls.getMethods();
            for (Method m : methodList) {
                // only getters (public, starts with get, no arguments)
                String mName = m.getName();
                if (Modifier.isPublic(m.getModifiers()) && m.getParameterCount() == 0
                        && !m.getReturnType().equals(Void.TYPE)
                        && (mName.startsWith("get") || mName.startsWith("is"))) {
                    String fieldName = mName.substring(mName.startsWith("get") ? 3 : 2);
                    // get returm parameter
                    Object searchValue;
                    try {
                        searchValue = m.invoke(searchParams, new Object[]{});
                    } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException ex) {
                       // LOG.error(l, ex);
                        continue;
                    }

                    if (searchValue == null) {
                        continue;
                    }

                    if (fieldName.endsWith("List") && searchValue instanceof List) {
                        String property = fieldName.substring(0, fieldName.lastIndexOf(
                                "List"));
                        if (!((List) searchValue).isEmpty()) {
                            lstPredicate.add(om.get(property).in(
                                    ((List) searchValue).toArray()));
                        } else {
                            lstPredicate.add(om.get(property).isNull());
                        }
                    } else {
                        try {
                            cls.getMethod("set" + fieldName, new Class[]{m.getReturnType()});
                        } catch (NoSuchMethodException | SecurityException ex) {
                            // method does not have setter // ignore other methods
                            continue;
                        }

                        if (fieldName.endsWith("From") && searchValue instanceof Comparable) {
                            lstPredicate.add(cb.greaterThanOrEqualTo(
                                    om.get(fieldName.substring(0, fieldName.
                                            lastIndexOf("From"))),
                                    (Comparable) searchValue));
                        } else if (fieldName.endsWith("To") && searchValue instanceof Comparable) {
                            lstPredicate.add(cb.lessThan(
                                    om.
                                            get(fieldName.substring(0, fieldName.lastIndexOf(
                                                    "To"))),
                                    (Comparable) searchValue));
                        } else if (searchValue instanceof String) {
                            if (!((String) searchValue).isEmpty()) {
                                lstPredicate.add(cb.equal(om.get(fieldName), searchValue));
                            }
                        } else if (searchValue instanceof BigInteger) {
                            lstPredicate.add(cb.equal(om.get(fieldName), searchValue));
                        } else {
                            LOG.warn("Unknown search value type %s for method %s! "
                                            + "Parameter is ignored!",
                                    searchValue, fieldName);
                        }
                    }

                }
            }
            if (!lstPredicate.isEmpty()) {
                Predicate[] tblPredicate = lstPredicate.stream().toArray(
                        Predicate[]::new);
                cq.where(cb.and(tblPredicate));
            }
        }
        return cq;
    }


    public <T> List<T> getDataList(Class<T> type, String hql,
                                   Map<String, Object> params) {
        TypedQuery<T> q = memEManager.createQuery(hql, type);
        params.forEach((param, value) -> {
            q.setParameter(param, value);
        });
        return q.getResultList();
    }

    /**
     *
     * @param <T>
     * @param type
     * @param startingAt
     * @param maxResultCnt
     * @param sortField
     * @param sortOrder
     * @param filters
     * @return
     */

    public <T> List<T> getDataList(Class<T> type, int startingAt, int maxResultCnt,
                                   String sortField,
                                   String sortOrder, Object filters) {

        return getDataList(type, startingAt, maxResultCnt, sortField, sortOrder,
                filters, null);
    }

    /**
     *
     * @param <T>
     * @param resultType
     * @param startingAt
     * @param maxResultCnt
     * @param sortField
     * @param sortOrder
     * @param filters
     * @param filterType
     * @return
     */
    public <T, D> List<T> getDataList(Class<T> resultType, int startingAt,
                                      int maxResultCnt,
                                      String sortField,
                                      String sortOrder, Object filters, Class<D> filterType) {

        List<T> lstResult;
        try {
            CriteriaQuery<T> cq = createSearchCriteria(resultType, filters, filterType,
                    false, sortField,
                    sortOrder);
            TypedQuery<T> q = memEManager.createQuery(cq);
            if (maxResultCnt > 0) {
                q.setMaxResults(maxResultCnt);
            }
            if (startingAt > 0) {
                q.setFirstResult(startingAt);
            }
            lstResult = q.getResultList();
        } catch (NoResultException ex) {
            lstResult = new ArrayList<>();
        }

        return lstResult;
    }

    /**
     *
     * @param <T>
     * @param type
     * @param filters
     * @return
     */
    public <T> long getDataListCount(Class<T> type, Object filters) {
        CriteriaQuery<Long> cqCount = createSearchCriteria(type, filters, null, true,
                null,
                null);
        Long res = memEManager.createQuery(cqCount).getSingleResult();
        return res;
    }

    public <T> void persist(T entity){
        memEManager.persist(entity);
    }

    public <T> void update(T entity){
        memEManager.merge(entity);
    }

    public <T> void remove(T entity){
        memEManager.remove(entity);
    }
}
