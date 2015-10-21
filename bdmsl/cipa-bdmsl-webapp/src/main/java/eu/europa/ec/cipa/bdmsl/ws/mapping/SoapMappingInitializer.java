package eu.europa.ec.cipa.bdmsl.ws.mapping;

import ec.services.wsdl.bdmsl.data._1.ListParticipantsType;
import ec.services.wsdl.bdmsl.data._1.PrepareChangeCertificateType;
import ec.services.wsdl.bdmsl.data._1.SMPAdvancedServiceForParticipantType;
import eu.europa.ec.cipa.bdmsl.common.bo.*;
import eu.europa.ec.cipa.bdmsl.dao.ICertificateDAO;
import eu.europa.ec.cipa.common.exception.TechnicalException;
import ma.glasnost.orika.CustomMapper;
import ma.glasnost.orika.MapperFactory;
import ma.glasnost.orika.MappingContext;
import org.busdox.servicemetadata.locator._1.*;
import org.busdox.transport.identifiers._1.ParticipantIdentifierType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

/**
 * In this class we register the mapping from BO to SOAP objects and vice-versa
 * <p/>
 * Created by feriaad on 15/06/2015.
 */

@Component
public class SoapMappingInitializer {

    @Autowired
    private MapperFactory mapperFactory;

    @Autowired
    private ICertificateDAO certificateDAO;

    @PostConstruct
    public void init() {
        // mapping from ServiceMetadataPublisherBO to ServiceMetadataPublisherServiceType
        mapperFactory.classMap(ServiceMetadataPublisherBO.class, ServiceMetadataPublisherServiceType.class)
                .field("smpId", "serviceMetadataPublisherID")
                .field("physicalAddress", "publisherEndpoint.physicalAddress")
                .field("logicalAddress", "publisherEndpoint.logicalAddress")
                .field("logicalAddress", "publisherEndpoint.logicalAddress")
                .customize(
                        new CustomMapper<ServiceMetadataPublisherBO, ServiceMetadataPublisherServiceType>() {
                            public void mapBtoA(ServiceMetadataPublisherServiceType a, ServiceMetadataPublisherBO b, MappingContext context) {
                                b.setCertificateId(SecurityContextHolder.getContext().getAuthentication().getName());
                            }
                        })
                .register();

        mapperFactory.classMap(ServiceMetadataPublisherServiceForParticipantType.class, ParticipantBO.class)
                .field("serviceMetadataPublisherID", "smpId")
                .field("participantIdentifier.value", "participantId")
                .field("participantIdentifier.scheme", "scheme")
                .customize(
                        new CustomMapper<ServiceMetadataPublisherServiceForParticipantType, ParticipantBO>() {
                            public void mapAtoB(ServiceMetadataPublisherServiceForParticipantType a, ParticipantBO b, MappingContext context) {
                                b.setType("Meta:SMP");
                            }
                        })
                .register();

        mapperFactory.classMap(ParticipantIdentifierType.class, ParticipantBO.class)
                .field("value", "participantId")
                .customize(
                        new CustomMapper<ParticipantIdentifierType, ParticipantBO>() {
                            public void mapAtoB(ParticipantIdentifierType a, ParticipantBO b, MappingContext context) {
                                b.setType("Meta:SMP");
                            }
                        })
                .byDefault()
                .register();

        mapperFactory.classMap(ParticipantIdentifierPageType.class, ParticipantListBO.class)
                .field("participantIdentifier{}", "participantBOList{}")
                .field("serviceMetadataPublisherID", "participantBOList{smpId}")
                .field("nextPageIdentifier", "nextPage")
                .register();

        mapperFactory.classMap(ListParticipantsType.class, ParticipantListBO.class)
                .field("participant{participantIdentifier.scheme}", "participantBOList{scheme}")
                .field("participant{participantIdentifier.value}", "participantBOList{participantId}")
                .field("participant{serviceMetadataPublisherID}", "participantBOList{smpId}")
                .register();

        mapperFactory.classMap(PageRequestType.class, PageRequestBO.class)
                .field("serviceMetadataPublisherID", "smpId")
                .field("nextPageIdentifier", "page")
                .register();

        mapperFactory.classMap(MigrationRecordType.class, MigrationRecordBO.class)
                .field("serviceMetadataPublisherID", "oldSmpId")
                .field("participantIdentifier.value", "participantId")
                .field("participantIdentifier.scheme", "scheme")
                .field("migrationKey", "migrationCode")
                .register();

        mapperFactory.classMap(SMPAdvancedServiceForParticipantType.class, ParticipantBO.class)
                .field("createParticipantIdentifier.serviceMetadataPublisherID", "smpId")
                .field("createParticipantIdentifier.participantIdentifier.value", "participantId")
                .field("createParticipantIdentifier.participantIdentifier.scheme", "scheme")
                .field("serviceName", "type")
                .register();
        mapperFactory.classMap(PrepareChangeCertificateType.class, PrepareChangeCertificateBO.class)
                .field("newCertificatePublicKey", "publicKey")
                .customize(
                        new CustomMapper<PrepareChangeCertificateType, PrepareChangeCertificateBO>() {
                            public void mapAtoB(PrepareChangeCertificateType a, PrepareChangeCertificateBO b, MappingContext context) {
                                try {
                                    CertificateBO certificateId = certificateDAO.findCertificateByCertificateId(SecurityContextHolder.getContext().getAuthentication().getName());
                                    if (certificateId == null) {
                                        throw new RuntimeException("The certificate " + SecurityContextHolder.getContext().getAuthentication().getName() + " couldn't be found");
                                    }
                                    b.setCurrentCertificate(certificateId);
                                } catch (TechnicalException exc) {
                                    throw new RuntimeException("The certificate " + SecurityContextHolder.getContext().getAuthentication().getName() + " couldn't be found", exc);
                                }
                            }
                        })
                .byDefault()
                .register();
    }
}
