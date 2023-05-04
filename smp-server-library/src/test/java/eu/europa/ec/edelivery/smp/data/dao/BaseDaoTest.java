package eu.europa.ec.edelivery.smp.data.dao;

import eu.europa.ec.edelivery.smp.data.model.DBDomain;
import eu.europa.ec.edelivery.smp.exceptions.ErrorCode;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;

import javax.persistence.EntityManager;
import javax.persistence.Parameter;
import javax.persistence.PersistenceContext;
import javax.persistence.criteria.*;
import java.util.Collections;
import java.util.List;


/**
 *  Purpose of class is to test implemented methods of BaseDao on DomainDao instance.
 *
 * @author Joze Rihtarsic
 * @since 4.1
 */
public class BaseDaoTest extends AbstractBaseDao {


    @Autowired
    DomainDao testInstance;

    @PersistenceContext
    protected EntityManager memEManager;

    @Test
    public void testSelectAndCountResult() {
        // given
        TestFilter filter = null;
        Class cls = DBDomain.class;
        // when
        CriteriaQuery res = testInstance.createSearchCriteria(filter, cls, false, null, null);
        //Then
        Assert.assertNotNull(res);
        Assert.assertNull(res.getSelection());

        // when
        res = testInstance.createSearchCriteria(filter, cls, true, null, null);
        //Then
        Assert.assertNotNull(res);
        Assert.assertNotNull(res.getSelection());
        Assert.assertEquals(java.lang.Long.class, res.getSelection().getJavaType());
    }

    @Test
    public void testFilterEmpty() {
        // given
        TestFilter filter = new TestFilter();
        Class cls = DBDomain.class;
        // when
        CriteriaQuery res = testInstance.createSearchCriteria(filter, cls, false, null, null);
        //Then
        Assert.assertNotNull(res);
        Assert.assertEquals(0,res.getParameters().size() );
    }

    @Test
    public void testPredicatesStringValue() {
        // given
        TestFilter filter = new TestFilter();
        String filterValue = "TestValue";
        filter.setDomainCode("TestValue");

        CriteriaBuilder cb = memEManager.getCriteriaBuilder();
        CriteriaQuery<DBDomain> cq = cb.createQuery(DBDomain.class);
        Root<DBDomain> om = cq.from(DBDomain.class);
        Predicate expected = cb.equal(testInstance.getPath(om, "DomainCode"), filterValue);

        // when
        List<Predicate> lst = testInstance.createPredicates(filter,om,cb );

        //Then
        Assert.assertNotNull(lst);
        Assert.assertEquals(1,lst.size() );
    }

    @Test
    public void testPredicatesStringListValue() {
        // given
        TestFilter filter = new TestFilter();
        List<String > filterValue  = Collections.singletonList("TestValue");
        filter.setDomainCodeList(filterValue);

        CriteriaBuilder cb = memEManager.getCriteriaBuilder();
        CriteriaQuery<DBDomain> cq = cb.createQuery(DBDomain.class);
        Root<DBDomain> om = cq.from(DBDomain.class);
        Predicate expected = cb.equal(testInstance.getPath(om, "DomainCodeList", "List"), filterValue);

        // when
        List<Predicate> lst = testInstance.createPredicates(filter,om,cb );

        //Then
        Assert.assertNotNull(lst);
        Assert.assertEquals(1,lst.size() );
    }

    @Test
    public void testPredicatesStringLikeValue() {
        // given
        TestFilter filter = new TestFilter();
        String filterValue = "TestValue";
        filter.setDomainCodeLike(filterValue);

        CriteriaBuilder cb = memEManager.getCriteriaBuilder();
        CriteriaQuery<DBDomain> cq = cb.createQuery(DBDomain.class);
        Root<DBDomain> om = cq.from(DBDomain.class);
        Predicate expected = cb.equal(testInstance.getPath(om, "DomainCodeLike", "Like"), filterValue);

        // when
        List<Predicate> lst = testInstance.createPredicates(filter,om,cb );

        //Then
        Assert.assertNotNull(lst);
        Assert.assertEquals(1,lst.size() );
    }

    @Test
    public void testPredicatesLong() {
        // given
        TestFilter filter = new TestFilter();
        Long filterValue = (long)10;
        filter.setId(filterValue);

        CriteriaBuilder cb = memEManager.getCriteriaBuilder();
        CriteriaQuery<DBDomain> cq = cb.createQuery(DBDomain.class);
        Root<DBDomain> om = cq.from(DBDomain.class);
        Predicate expected = cb.equal(testInstance.getPath(om, "Id"), filterValue);

        // when
        List<Predicate> lst = testInstance.createPredicates(filter,om,cb );

        //Then
        Assert.assertNotNull(lst);
        Assert.assertEquals(1,lst.size() );
    }




}
class TestFilter {
    String domainCode;
    String domainCodeLike;
    Long  id;
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
