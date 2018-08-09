package eu.europa.ec.edelivery.smp.testutil;

import eu.europa.ec.edelivery.smp.data.model.*;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.UUID;

public class AuditUtils {
    public static final String REVISION_USER ="REVISION_USER";
    public static final String REVISION_DOMAIN ="REVISION_DOMAIN";

    public static final String REVISION_BUSSINESS_ID ="REVISION_BUSSINESS_ID";
    public static final String REVISION_BUSSINESS_SCH ="REVISION_BUSSINESS_SCH";

    public static final String REVISION_DOCUMENT_ID ="REVISION_DOCUMENT_ID";
    public static final String REVISION_DOCUMENT_SCH ="REVISION_DOCUMENT_SCH";

    public static void reflectionSetFiled(Class clazz, Object entity, String filedName, Object newValue ) {
        try {
            Method method = clazz.getMethod("set"+filedName, newValue.getClass());
            method.invoke(entity, newValue);
        } catch ( NoSuchMethodException | IllegalAccessException | InvocationTargetException ex) {
            throw new RuntimeException(ex);
        }
    }

    public static DBUser createDBUser(){
        DBUser dbuser = new DBUser();
        dbuser.setUsername(REVISION_USER);
        dbuser.setAdmin(true);
        dbuser.setPassword(UUID.randomUUID().toString());
        return dbuser;
    }

    public static DBDomain createDBDomain(){
        DBDomain domain = new DBDomain();
        domain.setId(REVISION_DOMAIN);
        domain.setBdmslClientCertAlias(UUID.randomUUID().toString());
        domain.setBdmslSmpId(UUID.randomUUID().toString());
        return domain;
    }

    public static DBServiceGroup createDBServiceGroup(){
        DBServiceGroupId grpID = new DBServiceGroupId();
        grpID.setBusinessIdentifier(REVISION_BUSSINESS_ID);
        grpID.setBusinessIdentifierScheme(REVISION_BUSSINESS_SCH);

        DBDomain dbDomain = createDBDomain();

        DBServiceGroup grp = new DBServiceGroup();
        grp.setId(grpID);
        grp.setDomain(dbDomain);
        grp.setExtension(UUID.randomUUID().toString());
        return grp;
    }

    public static DBOwnership createDBOwnership(){
        DBServiceGroup grp = createDBServiceGroup();

        DBUser dbuser = createDBUser();

        DBOwnershipId ownID = new DBOwnershipId();
        ownID.setBusinessIdentifier(grp.getId().getBusinessIdentifier());
        ownID.setBusinessIdentifierScheme(grp.getId().getBusinessIdentifierScheme());
        ownID.setUsername(dbuser.getUsername());

        DBOwnership own = new DBOwnership();
        own.setId(ownID);
        own.setServiceGroup(grp);
        own.setUser(dbuser);
        return own;
    }

    public static DBServiceMetadata createDBServiceMetadata(){
        DBServiceGroup grp = createDBServiceGroup();


        DBServiceMetadataId smdId = new DBServiceMetadataId();
        smdId.setBusinessIdentifier(grp.getId().getBusinessIdentifier());
        smdId.setBusinessIdentifierScheme(grp.getId().getBusinessIdentifierScheme());
        smdId.setDocumentIdentifier(REVISION_DOCUMENT_ID);
        smdId.setDocumentIdentifierScheme(REVISION_DOCUMENT_SCH);

        DBServiceMetadata smd = new DBServiceMetadata();
        smd.setId(smdId);
        smd.setServiceGroup(grp);
        smd.setXmlContent(UUID.randomUUID().toString());


        return smd;
    }
}
