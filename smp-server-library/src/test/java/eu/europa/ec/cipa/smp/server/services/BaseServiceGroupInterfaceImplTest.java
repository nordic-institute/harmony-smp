package eu.europa.ec.cipa.smp.server.services;

import com.helger.commons.codec.URLCodec;
import com.helger.commons.scopes.mgr.ScopeManager;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.oasis_open.docs.bdxr.ns.smp._2016._05.ServiceGroup;
import org.oasis_open.docs.bdxr.ns.smp._2016._05.ServiceMetadataReferenceType;

import javax.annotation.Nonnull;
import javax.ws.rs.Path;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.UriBuilder;
import javax.ws.rs.core.UriInfo;
import java.net.URI;
import java.util.List;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

/**
 * Created by gutowpa on 27/03/2017.
 */
public class BaseServiceGroupInterfaceImplTest {

    @Before
    public void setUp(){
        ScopeManager.onGlobalBegin("a");
        ScopeManager.onRequestBegin("x", "y", "z");
    }

    @After
    public void tearDown(){
        ScopeManager.onRequestEnd();
        ScopeManager.onGlobalEnd();
    }

    @Test
    public void testCaseSensitivityOfReferencesReturned() throws Throwable {
        //given
        String PARTICIPANT_ID = "iso6523-actorid-upis::0010:599900000000B";
        String PARTICIPANT_ID_URL_ENCODED = new URLCodec().encodeText (PARTICIPANT_ID);
        String PARTICIPANT_ID_TO_LOWERCASE_URL_ENCODED = new URLCodec().encodeText (PARTICIPANT_ID.toLowerCase());
        UriInfo uriInfoMock = Mockito.mock(UriInfo.class);
        when(uriInfoMock.getBaseUriBuilder()).thenReturn(UriBuilder.fromResource(DummyInterfaceClass.class));
        HttpHeaders httpHeadersMock = Mockito.mock(HttpHeaders.class);
        
        //when
        ServiceGroup serviceGroup = BaseServiceGroupInterfaceImpl.getServiceGroup(uriInfoMock, httpHeadersMock, PARTICIPANT_ID, DummyInterfaceClass.class);

        //then
        List<ServiceMetadataReferenceType> serviceMetadataReferences = serviceGroup.getServiceMetadataReferenceCollection().getServiceMetadataReferences();
        Assert.assertEquals(1, serviceMetadataReferences.size());
        ServiceMetadataReferenceType reference = serviceMetadataReferences.get(0);
        assertFalse(reference.getHref().contains(PARTICIPANT_ID_URL_ENCODED));
        assertTrue(reference.getHref().contains(PARTICIPANT_ID_TO_LOWERCASE_URL_ENCODED));
    }

    @Path ("/{ServiceGroupId}/services/{DocumentTypeId}")
    private static final class DummyInterfaceClass{}
}
