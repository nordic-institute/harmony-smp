/*
 * Copyright 2017 European Commission | CEF eDelivery
 *
 * Licensed under the EUPL, Version 1.2 or - as soon they will be approved by the European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 *
 * You may obtain a copy of the Licence attached in file: LICENCE-EUPL-v1.2.pdf
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the Licence is distributed on an "AS IS" basis,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the Licence for the specific language governing permissions and limitations under the Licence.
 */

package eu.europa.ec.edelivery.smp.sml;

import eu.europa.ec.bdmsl.ws.soap.IManageParticipantIdentifierWS;
import eu.europa.ec.bdmsl.ws.soap.IManageServiceMetadataWS;
import eu.europa.ec.edelivery.smp.data.model.DBDomain;
import eu.europa.ec.edelivery.smp.exceptions.ErrorCode;
import eu.europa.ec.edelivery.smp.exceptions.SMPRuntimeException;
import eu.europa.ec.edelivery.smp.exceptions.SmlIntegrationException;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.busdox.servicemetadata.locator._1.PublisherEndpointType;
import org.busdox.servicemetadata.locator._1.ServiceMetadataPublisherServiceForParticipantType;
import org.busdox.servicemetadata.locator._1.ServiceMetadataPublisherServiceType;
import org.oasis_open.docs.bdxr.ns.smp._2016._05.ParticipantIdentifierType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

import static eu.europa.ec.edelivery.smp.conversion.SmlIdentifierConverter.toBusdoxParticipantId;
import static eu.europa.ec.smp.api.Identifiers.asString;

/**
 * Component responsible for building SOAP request and calling BDMSL.
 * It knows if SML integration is turned ON and only then makes a CREATE or DELETE participant call.
 * <p>
 * Created by gutowpa on 22/12/2017.
 */
@Component
public class SmlConnector implements ApplicationContextAware {

    private static final Logger log = LoggerFactory.getLogger(SmlConnector.class);

    @Value("${bdmsl.integration.enabled:false}")
    private boolean smlIntegrationEnabled;

    @Value("${bdmsl.integration.logical.address:}")
    private String smpLogicalAddress;

    @Value("${bdmsl.integration.physical.address:0.0.0.0}")
    private String smpPhysicalAddress;

    private ApplicationContext ctx;

    public boolean registerInDns(ParticipantIdentifierType normalizedParticipantId, DBDomain domain) {

        if (!smlIntegrationEnabled) {
            return false;
        }
        log.info("Registering new Participant in BDMSL: " + asString(normalizedParticipantId));
        try {
            ServiceMetadataPublisherServiceForParticipantType smlRequest = toBusdoxParticipantId(normalizedParticipantId, domain.getSmlSmpId());
            getClient(domain).create(smlRequest);
            return true;
        } catch (Exception e) {
            throw new SMPRuntimeException(ErrorCode.SML_INTEGRATION_EXCEPTION,e, ExceptionUtils.getRootCauseMessage(e));
        }
    }


    public boolean registerDomain(DBDomain domain) {

        if (!smlIntegrationEnabled) {
            return false;
        }
        log.info("Registering new Domain  toSML: (smpCode {} smp-smp-id {}) ", domain.getDomainCode(), domain.getSmlSmpId());
        try {
            ServiceMetadataPublisherServiceType smlSmpRequest = new ServiceMetadataPublisherServiceType();
            smlSmpRequest.setPublisherEndpoint(new PublisherEndpointType());
            smlSmpRequest.getPublisherEndpoint().setLogicalAddress(smpLogicalAddress);
            smlSmpRequest.getPublisherEndpoint().setPhysicalAddress(smpPhysicalAddress);
            smlSmpRequest.setServiceMetadataPublisherID(domain.getSmlSmpId());
            getSMPManagerClient(domain).create(smlSmpRequest);
            return true;
        } catch (Exception e) {
            throw new SMPRuntimeException(ErrorCode.SML_INTEGRATION_EXCEPTION,e, ExceptionUtils.getRootCauseMessage(e));
        }
    }


    public void unregisterFromDns(ParticipantIdentifierType normalizedParticipantId, DBDomain domain) {
        if (!smlIntegrationEnabled) {
            return;
        }
        log.info("Removing Participant from BDMSL: {} ", asString(normalizedParticipantId));
        try {
            ServiceMetadataPublisherServiceForParticipantType smlRequest = toBusdoxParticipantId(normalizedParticipantId, domain.getSmlSmpId());
            getClient(domain).delete(smlRequest);
        } catch (Exception e) {
            throw new SMPRuntimeException(ErrorCode.SML_INTEGRATION_EXCEPTION,e, ExceptionUtils.getRootCauseMessage(e));
        }
    }

    public boolean unregisterDomain(DBDomain domain) {
        if (!smlIntegrationEnabled) {
            return true;
        }
        log.info("Removing SMP id (Domain) from BDMSL: {} ", domain.getDomainCode());
        try {
            getSMPManagerClient(domain).delete(domain.getSmlSmpId());
            return true;
        } catch (Exception e) {
            throw new SMPRuntimeException(ErrorCode.SML_INTEGRATION_EXCEPTION,e, ExceptionUtils.getRootCauseMessage(e));
        }
    }

    private IManageParticipantIdentifierWS getClient(DBDomain domain) {

        String clientCertHttpHeader =domain.isSmlBlueCoatAuth()? domain.getSmlClientCertHeader():null;
        String clientCertAlias = domain.isSmlBlueCoatAuth()?null:domain.getSmlClientKeyAlias();
        return ctx.getBean(IManageParticipantIdentifierWS.class, clientCertAlias, clientCertHttpHeader);
    }

    private IManageServiceMetadataWS getSMPManagerClient(DBDomain domain) {

        String clientCertHttpHeader =domain.isSmlBlueCoatAuth()? domain.getSmlClientCertHeader():null;
        String clientCertAlias = domain.isSmlBlueCoatAuth()?null:domain.getSmlClientKeyAlias();
        return ctx.getBean(IManageServiceMetadataWS.class, clientCertAlias, clientCertHttpHeader);
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        ctx = applicationContext;
    }

    public boolean isSmlIntegrationEnabled() {
        return smlIntegrationEnabled;
    }
}
