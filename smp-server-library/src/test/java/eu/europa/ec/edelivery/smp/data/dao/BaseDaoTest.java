/*-
 * #START_LICENSE#
 * smp-server-library
 * %%
 * Copyright (C) 2017 - 2024 European Commission | eDelivery | DomiSMP
 * %%
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by the European Commission - subsequent
 * versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy of the Licence at:
 *
 * [PROJECT_HOME]\license\eupl-1.2\license.txt or https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the Licence is
 * distributed on an "AS IS" basis, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the Licence for the specific language governing permissions and limitations under the Licence.
 * #END_LICENSE#
 */
package eu.europa.ec.edelivery.smp.data.dao;

import eu.europa.ec.edelivery.smp.data.model.DBDomain;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;


/**
 * Purpose of class is to test implemented methods of BaseDao on DomainDao instance.
 *
 * @author Joze Rihtarsic
 * @since 4.1
 */
class BaseDaoTest extends AbstractBaseDao {


    @Autowired
    DomainDao testInstance;

    @PersistenceContext
    protected EntityManager memEManager;

    @Test
    void testSelectAndCountResult() {
        // given
        TestFilter filter = null;
        Class cls = DBDomain.class;
        // when
        CriteriaQuery res = testInstance.createSearchCriteria(filter, cls, false, null, null);
        //Then
        assertNotNull(res);
        assertNull(res.getSelection());

        // when
        res = testInstance.createSearchCriteria(filter, cls, true, null, null);
        //Then
        assertNotNull(res);
        assertNotNull(res.getSelection());
        assertEquals(java.lang.Long.class, res.getSelection().getJavaType());
    }

    @Test
    void testFilterEmpty() {
        // given
        TestFilter filter = new TestFilter();
        Class cls = DBDomain.class;
        // when
        CriteriaQuery res = testInstance.createSearchCriteria(filter, cls, false, null, null);
        //Then
        assertNotNull(res);
        assertEquals(0, res.getParameters().size());
    }

    @Test
    void testPredicatesStringValue() {
        // given
        TestFilter filter = new TestFilter();
        String filterValue = "TestValue";
        filter.setDomainCode("TestValue");

        CriteriaBuilder cb = memEManager.getCriteriaBuilder();
        CriteriaQuery<DBDomain> cq = cb.createQuery(DBDomain.class);
        Root<DBDomain> om = cq.from(DBDomain.class);
        Predicate expected = cb.equal(testInstance.getPath(om, "DomainCode"), filterValue);

        // when
        List<Predicate> lst = testInstance.createPredicates(filter, om, cb);

        //Then
        assertNotNull(lst);
        assertEquals(1, lst.size());
    }

    @Test
    void testPredicatesStringListValue() {
        // given
        TestFilter filter = new TestFilter();
        List<String> filterValue = Collections.singletonList("TestValue");
        filter.setDomainCodeList(filterValue);

        CriteriaBuilder cb = memEManager.getCriteriaBuilder();
        CriteriaQuery<DBDomain> cq = cb.createQuery(DBDomain.class);
        Root<DBDomain> om = cq.from(DBDomain.class);
        Predicate expected = cb.equal(testInstance.getPath(om, "DomainCodeList", "List"), filterValue);

        // when
        List<Predicate> lst = testInstance.createPredicates(filter, om, cb);

        //Then
        assertNotNull(lst);
        assertEquals(1, lst.size());
    }

    @Test
    void testPredicatesStringLikeValue() {
        // given
        TestFilter filter = new TestFilter();
        String filterValue = "TestValue";
        filter.setDomainCodeLike(filterValue);

        CriteriaBuilder cb = memEManager.getCriteriaBuilder();
        CriteriaQuery<DBDomain> cq = cb.createQuery(DBDomain.class);
        Root<DBDomain> om = cq.from(DBDomain.class);
        Predicate expected = cb.equal(testInstance.getPath(om, "DomainCodeLike", "Like"), filterValue);

        // when
        List<Predicate> lst = testInstance.createPredicates(filter, om, cb);

        //Then
        assertNotNull(lst);
        assertEquals(1, lst.size());
    }

    @Test
    void testPredicatesLong() {
        // given
        TestFilter filter = new TestFilter();
        Long filterValue = (long) 10;
        filter.setId(filterValue);

        CriteriaBuilder cb = memEManager.getCriteriaBuilder();
        CriteriaQuery<DBDomain> cq = cb.createQuery(DBDomain.class);
        Root<DBDomain> om = cq.from(DBDomain.class);
        Predicate expected = cb.equal(testInstance.getPath(om, "Id"), filterValue);

        // when
        List<Predicate> lst = testInstance.createPredicates(filter, om, cb);

        //Then
        assertNotNull(lst);
        assertEquals(1, lst.size());
    }


}

class TestFilter {
    String domainCode;
    String domainCodeLike;
    Long id;
    Long idFrom;
    Long idTo;
    List<String> domainCodeList;

    public String getDomainCode() {
        return domainCode;
    }

    public void setDomainCode(String domainCode) {
        this.domainCode = domainCode;
    }

    public String getDomainCodeLike() {
        return domainCodeLike;
    }

    public void setDomainCodeLike(String domainCodeLike) {
        this.domainCodeLike = domainCodeLike;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getIdFrom() {
        return idFrom;
    }

    public void setIdFrom(Long idFrom) {
        this.idFrom = idFrom;
    }

    public Long getIdTo() {
        return idTo;
    }

    public void setIdTo(Long idTo) {
        this.idTo = idTo;
    }

    public List<String> getDomainCodeList() {
        return domainCodeList;
    }

    public void setDomainCodeList(List<String> domainCodeList) {
        this.domainCodeList = domainCodeList;
    }
}
