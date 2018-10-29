package eu.europa.ec.edelivery.smp.testutil;

import eu.europa.ec.edelivery.smp.data.model.DBDomain;
import eu.europa.ec.edelivery.smp.data.ui.ServiceGroupDomainRO;
import eu.europa.ec.edelivery.smp.data.ui.ServiceGroupValidationRO;
import eu.europa.ec.edelivery.smp.data.ui.ServiceGroupRO;
import eu.europa.ec.edelivery.smp.data.ui.ServiceMetadataRO;
import eu.europa.ec.edelivery.smp.data.ui.enums.EntityROStatus;

import java.io.IOException;
import java.util.Arrays;
import java.util.UUID;

import static eu.europa.ec.edelivery.smp.conversion.ExtensionConverterTest.RES_PATH;
import static eu.europa.ec.edelivery.smp.testutil.TestConstants.SIMPLE_DOCUMENT_XML;
import static eu.europa.ec.edelivery.smp.testutil.TestConstants.SIMPLE_EXTENSION_XML;

public class TestROUtils {


    public static ServiceMetadataRO createServiceMetadataDomain(DBDomain domain, ServiceGroupRO sgo, String docid, String docSch){
        ServiceMetadataRO sgdmd = new ServiceMetadataRO();
        sgdmd.setDomainCode(domain.getDomainCode());
        sgdmd.setSmlSubdomain(domain.getSmlSubdomain());
        sgdmd.setDocumentIdentifier(docid);
        sgdmd.setDocumentIdentifierScheme(docSch);
        sgdmd.setXmlContent(generateServiceMetadata(sgo.getParticipantIdentifier(), sgo.getParticipantScheme(), docid,docSch ));
        return sgdmd;
    }

    public static ServiceGroupDomainRO createServiceGroupDomain(DBDomain domain){

        ServiceGroupDomainRO sgd = new ServiceGroupDomainRO();
        sgd.setDomainId(domain.getId());
        sgd.setDomainCode(domain.getDomainCode());
        sgd.setSmlSubdomain(domain.getSmlSubdomain());
        return sgd;
    }

    public static ServiceGroupRO createROServiceGroup() {
        return createROServiceGroup(TestConstants.TEST_SG_ID_1, TestConstants.TEST_SG_SCHEMA_1);
    }

    public static ServiceGroupRO createROServiceGroupForDomains(DBDomain ... domains) {
        ServiceGroupRO sgo =  createROServiceGroup(TestConstants.TEST_SG_ID_1, TestConstants.TEST_SG_SCHEMA_1);
        Arrays.asList(domains).forEach(domain -> {
            ServiceGroupDomainRO sgd = createServiceGroupDomain(domain);
            sgo.getServiceGroupDomains().add(sgd);
        });
        return sgo;
    }

    public static ServiceGroupRO createROServiceGroup(String id, String sch) {
        return createROServiceGroup(id, sch, true);
    }

    public static ServiceGroupRO createROServiceGroup(String id, String sch, boolean withExtension) {
        ServiceGroupRO grp = new ServiceGroupRO();
        grp.setStatus(EntityROStatus.NEW.getStatusNumber());
        grp.setParticipantIdentifier(id);
        grp.setParticipantScheme(sch);
        if (withExtension) {
            grp.setExtensionStatus(EntityROStatus.NEW.getStatusNumber());
            grp.setExtension(generateExtension());
        }
        return grp;
    }

    public static String generateExtension(){
        return String.format(SIMPLE_EXTENSION_XML, UUID.randomUUID().toString());
    }

    public static String generateServiceMetadata(String partId, String partSch, String docId, String docSch){
        return String.format(SIMPLE_DOCUMENT_XML, partSch, partId,docSch, docId, UUID.randomUUID().toString()  );
    }

    public static ServiceGroupValidationRO getValidExtension() throws IOException {
        String inputDoc = XmlTestUtils.loadDocumentAsString(RES_PATH + "extensionValidOne.xml");
        return getExtensionRO(inputDoc);
    }


    public static ServiceGroupValidationRO getValidMultipleExtension() throws IOException {
        String inputDoc = XmlTestUtils.loadDocumentAsString(RES_PATH + "extensionValidMultiple.xml");
        return getExtensionRO(inputDoc);
    }

    public static ServiceGroupValidationRO getValidCustomText() throws IOException {
        String inputDoc = XmlTestUtils.loadDocumentAsString(RES_PATH + "extensionCustomText.xml");
        return getExtensionRO(inputDoc);
    }

    public static ServiceGroupValidationRO getInvalid() throws IOException {
        String inputDoc = XmlTestUtils.loadDocumentAsString(RES_PATH + "extensionInvalid.xml");
        return getExtensionRO(inputDoc);
    }

    public static ServiceGroupValidationRO getCustomExtension() throws IOException {
        String inputDoc = XmlTestUtils.loadDocumentAsString(RES_PATH + "extensionCustom.xml");
        return getExtensionRO(inputDoc);
    }


    public static ServiceGroupValidationRO getExtensionRO(String extension) {
        ServiceGroupValidationRO sg = new ServiceGroupValidationRO();
        sg.setServiceGroupId((long) 1);
        sg.setExtension(extension);
        return sg;
    }
}
