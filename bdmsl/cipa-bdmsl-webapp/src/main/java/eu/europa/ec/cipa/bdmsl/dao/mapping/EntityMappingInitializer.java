package eu.europa.ec.cipa.bdmsl.dao.mapping;

import eu.europa.ec.cipa.bdmsl.common.bo.*;
import eu.europa.ec.cipa.bdmsl.dao.entity.*;
import ma.glasnost.orika.MapperFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

/**
 * This class contains all the mapping from JPA entities to Business Objects and vice-versa.
 * <p/>
 * Created by feriaad on 15/06/2015.
 */

@Component
public class EntityMappingInitializer {

    @Autowired
    private MapperFactory mapperFactory;

    @PostConstruct
    public void init() {
        mapperFactory.classMap(SmpEntity.class, ServiceMetadataPublisherBO.class)
                .field("endpointLogicalAddress", "logicalAddress")
                .field("endpointPhysicalAddress", "physicalAddress")
                .field("certificate.certificateId", "certificateId")
                .byDefault()
                .register();

        mapperFactory.classMap(CertificateDomainEntity.class, CertificateDomainBO.class)
                .byDefault()
                .register();

        mapperFactory.classMap(CertificateBO.class, CertificateEntity.class)
                .field("newCertificateId", "newCertificate.id")
                .field("migrationDate", "newCertificateChangeDate")
                .byDefault()
                .register();

        mapperFactory.classMap(ParticipantBO.class, ParticipantIdentifierEntity.class)
                .field("smpId", "smp.smpId")
                .byDefault()
                .register();

        mapperFactory.classMap(MigrateEntity.class, MigrationRecordBO.class)
                .field("migrationKey", "migrationCode")
                .byDefault()
                .register();

        mapperFactory.classMap(AllowedWildcardEntity.class, WildcardBO.class)
                .field("certificate.certificateId", "certificateId")
                .byDefault()
                .register();
    }
}

