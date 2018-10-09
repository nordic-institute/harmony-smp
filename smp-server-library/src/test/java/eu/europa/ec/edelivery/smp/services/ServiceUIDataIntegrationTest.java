package eu.europa.ec.edelivery.smp.services;

import eu.europa.ec.edelivery.smp.config.H2JPATestConfiguration;
import eu.europa.ec.edelivery.smp.data.ui.*;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.Assert.*;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = {
        H2JPATestConfiguration.class})
@Transactional
public class ServiceUIDataIntegrationTest {

    @Autowired
    private ServiceUIData serviceUIData;

    private  DomainRO testDomain = null;
    private  ServiceGroupRO testSG = null;

    @Before
    public void initDatabase() {


        testDomain = new DomainRO();
        testDomain.setDomainId("test");
        testDomain.setBdmslSmpId("test");
        serviceUIData.persistDomain(testDomain);

        testSG = new ServiceGroupRO();
        testSG.setServiceGroupROId(new ServiceGroupRO.ServiceGroupROId());
        testSG.getServiceGroupROId().setParticipantId("testParticipantId");
        testSG.getServiceGroupROId().setParticipantSchema("testParticipantSchema");
        testSG.setDomain(testDomain.getDomainId());
        serviceUIData.persistServiceGroup(testSG);

    }


    @Test
    public void getServiceGroupList() {
        for (int i = 0; i < 20; i++) {
            ServiceGroupRO sg = new ServiceGroupRO();
            sg.setServiceGroupROId(new ServiceGroupRO.ServiceGroupROId());
            sg.getServiceGroupROId().setParticipantId("ParticipantId" + i);
            sg.getServiceGroupROId().setParticipantSchema("ParticipantId");
            sg.setDomain(testDomain.getDomainId());
            serviceUIData.persistServiceGroup(sg);
        }

        ServiceResult<ServiceGroupRO> lst = serviceUIData.getServiceGroupList(0, 10, null, null);
        assertEquals(10, lst.getServiceEntities().size());
    }

    @Test
    public void getUserList() {

        for (int i = 0; i < 20; i++) {
            UserRO ent = new UserRO();
            ent.setAdmin(false);
            ent.setUserName("Username" + i);
            ent.setPassword("Password");
            serviceUIData.persistUser(ent);
        }

        ServiceResult<UserRO> lst = serviceUIData.getUserList(0, 10, null, null);
        assertEquals(10, lst.getServiceEntities().size());
    }

    @Test
    public void getDomainList() {
        for (int i = 0; i < 20; i++) {
            DomainRO ent = new DomainRO();
            ent.setDomainId("DomainId" + i);
            ent.setBdmslClientCertAlias("dmslClientCertAlias");
            ent.setBdmslClientCertHeader("dmslClientCertHeader");
            ent.setBdmslSmpId("BdmslSmpId");
            ent.setSignatureCertAlias("SignatureCertAlias");
            serviceUIData.persistDomain(ent);
        }

        ServiceResult<DomainRO> lst = serviceUIData.getDomainList(0, 10, null, null);
        assertEquals(10, lst.getServiceEntities().size());
    }

    @Test
    public void getServiceMetadataList() {
        for (int i = 0; i < 20; i++) {
            ServiceMetadataRO ent = new ServiceMetadataRO();
            ent.setServiceMetadataROId(new ServiceMetadataRO.ServiceMetadataROId());
            ent.getServiceMetadataROId().setDocumentIdScheme("DocumentIdScheme");
            ent.getServiceMetadataROId().setDocumentIdValue("DocumentIdValue" +i);
            ent.getServiceMetadataROId().setParticipantId(testSG.getServiceGroupROId().getParticipantId());
            ent.getServiceMetadataROId().setParticipantSchema(testSG.getServiceGroupROId().getParticipantSchema());
            long cnt = serviceUIData.getServiceMetadataList(0, 10, null, null).getCount();
            serviceUIData.persistMetaData(ent);
        }

        ServiceResult<ServiceMetadataRO> lst = serviceUIData.getServiceMetadataList(0, 10, null, null);
        assertEquals(10, lst.getServiceEntities().size());
    }


    @Test
    public void persistServiceGroup() {

        ServiceGroupRO sg = new ServiceGroupRO();
        sg.setServiceGroupROId(new ServiceGroupRO.ServiceGroupROId());
        sg.getServiceGroupROId().setParticipantId("ParticipantId");
        sg.getServiceGroupROId().setParticipantSchema("ParticipantId");
        sg.setDomain(testDomain.getDomainId());
        long cnt = serviceUIData.getServiceGroupList(0, 10, null, null).getCount();

        serviceUIData.persistServiceGroup(sg);

        ServiceResult<ServiceGroupRO> lst = serviceUIData.getServiceGroupList(0, 10, null, null);
        assertEquals(cnt + 1, lst.getCount().longValue());
    }

    @Test
    public void persistUser() {

        UserRO ent = new UserRO();
        ent.setAdmin(false);
        ent.setUserName("Username");
        ent.setPassword("Password");

        long cnt = serviceUIData.getUserList(0, 10, null, null).getCount();
        serviceUIData.persistUser(ent);

        ServiceResult<UserRO> lst = serviceUIData.getUserList(0, 10, null, null);
        assertEquals(cnt + 1, lst.getCount().longValue());
    }

    @Test
    public void persistDomain() {

        DomainRO ent = new DomainRO();
        ent.setDomainId("DomainId");
        ent.setBdmslClientCertAlias("dmslClientCertAlias");
        ent.setBdmslClientCertHeader("dmslClientCertHeader");
        ent.setBdmslSmpId("BdmslSmpId");
        ent.setSignatureCertAlias("SignatureCertAlias");
        long cnt = serviceUIData.getDomainList(0, 10, null, null).getCount();

        serviceUIData.persistDomain(ent);

        ServiceResult<DomainRO> lst = serviceUIData.getDomainList(0, 10, null, null);
        assertEquals(cnt + 1, lst.getCount().longValue());
    }

    @Test
    public void persistMetaData() {
        ServiceMetadataRO ent = new ServiceMetadataRO();
        ent.setServiceMetadataROId(new ServiceMetadataRO.ServiceMetadataROId());
        ent.getServiceMetadataROId().setDocumentIdScheme("DocumentIdScheme");
        ent.getServiceMetadataROId().setDocumentIdValue("DocumentIdValue");
        ent.getServiceMetadataROId().setParticipantId(testSG.getServiceGroupROId().getParticipantId());
        ent.getServiceMetadataROId().setParticipantSchema(testSG.getServiceGroupROId().getParticipantSchema());
        long cnt = serviceUIData.getServiceMetadataList(0, 10, null, null).getCount();
        serviceUIData.persistMetaData(ent);

        ServiceResult<ServiceMetadataRO> lst = serviceUIData.getServiceMetadataList(0, 10, null, null);
        assertEquals(cnt + 1, lst.getCount().longValue());
    }


}