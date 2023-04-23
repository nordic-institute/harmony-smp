package eu.europa.ec.edelivery.smp.data.dao;


import eu.europa.ec.edelivery.smp.data.enums.MembershipRoleType;
import eu.europa.ec.edelivery.smp.data.model.DBDomain;
import eu.europa.ec.edelivery.smp.data.model.DBGroup;
import eu.europa.ec.edelivery.smp.data.model.doc.DBDocument;
import eu.europa.ec.edelivery.smp.data.model.doc.DBResource;
import eu.europa.ec.edelivery.smp.data.model.doc.DBResourceFilter;
import eu.europa.ec.edelivery.smp.data.model.ext.DBResourceDef;
import eu.europa.ec.edelivery.smp.data.model.user.DBUser;
import eu.europa.ec.edelivery.smp.testutil.TestDBUtils;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import javax.transaction.Transactional;
import java.util.List;
import java.util.Optional;

import static eu.europa.ec.edelivery.smp.testutil.TestConstants.*;

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
        testUtilsDao.createResourcePrivateInternalMemberships();


    }

    @Test
    public void getAllPublicResources() {
        List<DBResource> result = testInstance.getPublicResourcesSearch(-1,-1,null, null, null);
        //System.out.println(result.get(0));
        Assert.assertEquals(2, result.size());


       result = testInstance.getPublicResourcesSearch(-1,-1,testUtilsDao.getUser1(), null, null);
        //System.out.println(result.get(0));
        Assert.assertEquals(3, result.size());

    }

    @Test
    public void getAllPublicResourcesCount() {
        Long result = testInstance.getPublicResourcesSearchCount(null, null, null);
        //System.out.println(result.get(0));
        Assert.assertEquals(2, result.intValue());


        result = testInstance.getPublicResourcesSearchCount(testUtilsDao.getUser1(), null, null);
        //System.out.println(result.get(0));
        Assert.assertEquals(3, result.intValue());

    }

}
