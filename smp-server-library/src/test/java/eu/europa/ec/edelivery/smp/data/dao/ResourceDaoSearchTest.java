package eu.europa.ec.edelivery.smp.data.dao;


import eu.europa.ec.edelivery.smp.data.model.doc.DBResource;
import eu.europa.ec.edelivery.smp.data.model.doc.DBResourceFilter;
import org.hamcrest.CoreMatchers;
import org.hamcrest.MatcherAssert;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Purpose of class is to test all resource methods with database.
 *
 * @author Joze Rihtarsic
 * @since 5.0
 */

public class ResourceDaoSearchTest extends AbstractBaseDao {

    private static final Logger LOG = LoggerFactory.getLogger(ResourceDaoSearchTest.class);
    @Autowired
    ResourceDao testInstance;

    @Before
    public void prepareDatabase() {
        // setup initial data!
        testUtilsDao.clearData();
        testUtilsDao.createResourcesForSearch();
    }

    @Test
    @Transactional
    public void getAllPublicResources() {
        List<DBResource> allResources = testInstance.getResourcesForFilter(-1, -1, DBResourceFilter.createBuilder().build());
        Assert.assertEquals(8, allResources.size());

        // only one group is public -
        List<DBResource> result = testInstance.getPublicResourcesSearch(-1,-1,null, null, null);
        Assert.assertEquals(1, result.size());
        assertResources(result, "1-1-1::pubPubPub");

        // user1 (admin) and user2 (viewer) are members of all resources
        result = testInstance.getPublicResourcesSearch(-1,-1,testUtilsDao.getUser2(), null, null);
        Assert.assertEquals(8, result.size());

        result = testInstance.getPublicResourcesSearch(-1,-1,testUtilsDao.getUser1(), null, "pubPub");
        Assert.assertEquals(2, result.size());
        result.forEach(resource -> MatcherAssert.assertThat(resource.getIdentifierValue(), CoreMatchers.containsString("pubPub")));

        result = testInstance.getPublicResourcesSearch(-1,-1,testUtilsDao.getUser1(), "1-1",null);
        Assert.assertEquals(1, result.size());
        result.forEach(resource -> MatcherAssert.assertThat(resource.getIdentifierScheme(), CoreMatchers.containsString("1-1")));

        result = testInstance.getPublicResourcesSearch(-1,-1,testUtilsDao.getUser1(), "1-1","priv");
        Assert.assertEquals(0, result.size());

        result = testInstance.getPublicResourcesSearch(-1,-1,testUtilsDao.getUser2(), null, null);
        Assert.assertEquals(8, result.size());


        // user3 is direct member of private domain - can see only public resource on public groups
        result = testInstance.getPublicResourcesSearch(-1,-1,testUtilsDao.getUser3(), null, null);
        assertResources(result, "1-1-1::pubPubPub", "5-5-5::privPubPub");

        // user4 is direct member of private group in private domain
        result = testInstance.getPublicResourcesSearch(-1,-1,testUtilsDao.getUser4(), null, null);
        assertResources(result, "1-1-1::pubPubPub", "5-5-5::privPubPub", "7-7-7::privPrivPub");

        // user5 is direct member of private resource in  private group in private domain
        result = testInstance.getPublicResourcesSearch(-1,-1,testUtilsDao.getUser5(), null, null);
        assertResources(result, "1-1-1::pubPubPub", "5-5-5::privPubPub", "7-7-7::privPrivPub", "8-8-8::privPrivPriv");
    }

    public void assertResources(List<DBResource> result, String ... resourceIdentifiers) {
        List<String> resultIdentifiers = result.stream().map(val-> val.getIdentifierScheme()+"::"+val.getIdentifierValue() ).collect(Collectors.toList());
        System.out.println(resultIdentifiers);
        Assert.assertArrayEquals(resourceIdentifiers, resultIdentifiers.stream().toArray());
    }

    @Test
    public void getAllPublicResourcesCount() {
        List<DBResource> allResources = testInstance.getResourcesForFilter(-1, -1, DBResourceFilter.createBuilder().build());
        Assert.assertEquals(8, allResources.size());

        // only one group is public -
        Long result = testInstance.getPublicResourcesSearchCount(null, null, null);
        Assert.assertEquals(1, result.intValue());

        // user1 (admin) and user2 (viewer) are members of all resources
        result = testInstance.getPublicResourcesSearchCount(testUtilsDao.getUser1(), null, null);
        Assert.assertEquals(8, result.intValue());

        result = testInstance.getPublicResourcesSearchCount(testUtilsDao.getUser1(), null, "pubPub");
        Assert.assertEquals(2, result.intValue());

        result = testInstance.getPublicResourcesSearchCount(testUtilsDao.getUser1(), "1-1",null);
        Assert.assertEquals(1, result.intValue());

        result = testInstance.getPublicResourcesSearchCount(testUtilsDao.getUser1(), "1-1","priv");
        Assert.assertEquals(0, result.intValue());

        result = testInstance.getPublicResourcesSearchCount(testUtilsDao.getUser2(), null, null);
        Assert.assertEquals(8, result.intValue());

        // user3 is direct member of private domain - can see only public resource on public groups
        result = testInstance.getPublicResourcesSearchCount(testUtilsDao.getUser3(), null, null);
        Assert.assertEquals(2, result.intValue());

        // user4 is direct member of private group in private domain
        result = testInstance.getPublicResourcesSearchCount(testUtilsDao.getUser4(), null, null);
        Assert.assertEquals(3, result.intValue());

        // user5 is direct member of private resource in  private group in private domain
        result = testInstance.getPublicResourcesSearchCount(testUtilsDao.getUser5(), null, null);
        Assert.assertEquals(4, result.intValue());

    }

}
