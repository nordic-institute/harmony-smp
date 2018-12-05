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

import eu.europa.ec.bdmsl.ws.soap.BadRequestFault;
import eu.europa.ec.bdmsl.ws.soap.IManageParticipantIdentifierWS;
import eu.europa.ec.bdmsl.ws.soap.IManageServiceMetadataWS;
import eu.europa.ec.bdmsl.ws.soap.NotFoundFault;
import eu.europa.ec.edelivery.smp.data.model.DBDomain;
import eu.europa.ec.edelivery.smp.exceptions.ErrorCode;
import eu.europa.ec.edelivery.smp.exceptions.SMPRuntimeException;
import eu.europa.ec.edelivery.smp.logging.SMPLogger;
import eu.europa.ec.edelivery.smp.logging.SMPLoggerFactory;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.busdox.servicemetadata.locator._1.PublisherEndpointType;
import org.busdox.servicemetadata.locator._1.ServiceMetadataPublisherServiceForParticipantType;
import org.busdox.servicemetadata.locator._1.ServiceMetadataPublisherServiceType;
import org.oasis_open.docs.bdxr.ns.smp._2016._05.ParticipantIdentifierType;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

import static eu.europa.ec.edelivery.smp.conversion.SmlIdentifierConverter.toBusdoxParticipantId;
import static eu.europa.ec.edelivery.smp.sml.SMLErrorMessages.ERR_DOMAIN_ALREADY_EXISTS;
import static eu.europa.ec.edelivery.smp.sml.SMLErrorMessages.ERR_DOMAIN_NOT_EXISTS;
import static eu.europa.ec.edelivery.smp.sml.SMLErrorMessages.ERR_PARTICIPANT_ALREADY_EXISTS;
import static eu.europa.ec.smp.api.Identifiers.asString;

/**
 * Component responsible for building SOAP request and calling BDMSL.
 * It knows if SML integration is turned ON and only then makes a CREATE or DELETE participant call.
 * <p>
 * Created by gutowpa on 22/12/2017.
 */
@Component
public class SmlConnector implements ApplicationContextAware {

    private static final SMPLogger LOG = SMPLoggerFactory.getLogger(SmlConnector.class);

    @Value("${bdmsl.integration.enabled:false}")
    private boolean smlIntegrationEnabled;

    @Value("${bdmsl.participant.multidomain.enabled:false}")
    private boolean smlParticipantMultidomainEnabled;


    @Value("${bdmsl.integration.logical.address:}")
    private String smpLogicalAddress;

    @Value("${bdmsl.integration.physical.address:0.0.0.0}")
    private String smpPhysicalAddress;

    private ApplicationContext ctx;

    public boolean registerInDns(ParticipantIdentifierType normalizedParticipantId, DBDomain domain) {

        if (!smlIntegrationEnabled) {
            return false;
        }
        LOG.info("Registering new Participant in BDMSL: " + asString(normalizedParticipantId));
        try {
            ServiceMetadataPublisherServiceForParticipantType smlRequest = toBusdoxParticipantId(normalizedParticipantId, domain.getSmlSmpId());
            getClient(domain).create(smlRequest);
            return true;
        } catch (BadRequestFault e){
            return processSMLErrorMessage(e, normalizedParticipantId);
        } catch (NotFoundFault e){
            return processSMLErrorMessage(e, normalizedParticipantId);
        }  catch (Exception e) {
            LOG.error(e.getClass().getName() + "" + e.getMessage(), e);
            throw new SMPRuntimeException(ErrorCode.SML_INTEGRATION_EXCEPTION,e, ExceptionUtils.getRootCauseMessage(e));
        }
    }

    protected boolean processSMLErrorMessage(BadRequestFault e, ParticipantIdentifierType participantIdentifierType){
        if(!isOkMessage(participantIdentifierType, e.getMessage())){
            LOG.error( e.getMessage(), e);
            throw new SMPRuntimeException(ErrorCode.SML_INTEGRATION_EXCEPTION,e, ExceptionUtils.getRootCauseMessage(e));
        }
        LOG.warn( e.getMessage(), e);
        return true;
    }

    protected boolean processSMLErrorMessage(NotFoundFault e, ParticipantIdentifierType participantIdentifierType){
        if(!isOkMessage(participantIdentifierType, e.getMessage())){
            LOG.error( e.getMessage(), e);
            throw new SMPRuntimeException(ErrorCode.SML_INTEGRATION_EXCEPTION,e, ExceptionUtils.getRootCauseMessage(e));
        }
        LOG.warn( e.getMessage(), e);
        return true;
    }

    /**
     * Ignore messages if already exists
     * @param patId
     * @param errorMessage
     * @return
     */
    protected boolean isOkMessage(ParticipantIdentifierType patId, String errorMessage){
        if (errorMessage ==null){
            return false;
        }
        String exp = String.format(ERR_PARTICIPANT_ALREADY_EXISTS, patId.getValue(), patId.getScheme());
        return errorMessage.startsWith(exp);
    }


    /**
     *
     * @param domain
     * @return
     */
    public boolean registerDomain(DBDomain domain) {

        if (!smlIntegrationEnabled) {
            return false;
        }
        LOG.info("Registering new Domain  toSML: (smpCode {} smp-smp-id {}) ", domain.getDomainCode(), domain.getSmlSmpId());
        try {
            ServiceMetadataPublisherServiceType smlSmpRequest = new ServiceMetadataPublisherServiceType();
            smlSmpRequest.setPublisherEndpoint(new PublisherEndpointType());
            smlSmpRequest.getPublisherEndpoint().setLogicalAddress(smpLogicalAddress);
            smlSmpRequest.getPublisherEndpoint().setPhysicalAddress(smpPhysicalAddress);
            smlSmpRequest.setServiceMetadataPublisherID(domain.getSmlSmpId());
            getSMPManagerClient(domain).create(smlSmpRequest);
            return true;
        } catch (BadRequestFault e){
            return processSMLErrorMessage(e, domain);
        }
        catch (Exception e) {
            LOG.error(e.getClass().getName() + "" + e.getMessage(), e);
            throw new SMPRuntimeException(ErrorCode.SML_INTEGRATION_EXCEPTION,e, ExceptionUtils.getRootCauseMessage(e));
        }
    }

    private boolean processSMLErrorMessage(BadRequestFault e, DBDomain domain){
        if(!isOkMessage(domain, e.getMessage())){
            LOG.error( e.getMessage(), e);
            throw new SMPRuntimeException(ErrorCode.SML_INTEGRATION_EXCEPTION,e, ExceptionUtils.getRootCauseMessage(e));
        }
        LOG.warn( e.getMessage(), e);
        return true;
    }

    private boolean processSMLErrorMessage(NotFoundFault e, DBDomain domain){
        if(!isOkMessage(domain, e.getMessage())){
            LOG.error( e.getMessage(), e);
            throw new SMPRuntimeException(ErrorCode.SML_INTEGRATION_EXCEPTION,e, ExceptionUtils.getRootCauseMessage(e));
        }
        LOG.warn( e.getMessage(), e);
        return true;
    }

    /**
     * Ignore messages if already exists
     * @param domain
     * @param errorMessage
     * @return
     */
    protected boolean isOkMessage( DBDomain domain, String errorMessage){
        LOG.info("Validate SML error message for domain {} {}", errorMessage,domain.getDomainCode() );
        if (errorMessage == null){
            return false;
        }
        String exp = String.format(ERR_DOMAIN_ALREADY_EXISTS, domain.getSmlSmpId());
        String exp2 = String.format(ERR_DOMAIN_NOT_EXISTS, domain.getSmlSmpId());
        return errorMessage.startsWith(exp)|| errorMessage.startsWith(exp2);
    }


    public boolean unregisterFromDns(ParticipantIdentifierType normalizedParticipantId, DBDomain domain) {
        if (!smlIntegrationEnabled) {
            return false;
        }
        LOG.info("Removing Participant from BDMSL: {} ", asString(normalizedParticipantId));
        try {
            ServiceMetadataPublisherServiceForParticipantType smlRequest = toBusdoxParticipantId(normalizedParticipantId, domain.getSmlSmpId());
            getClient(domain).delete(smlRequest);
            return true;
        }catch (BadRequestFault e){
            return processSMLErrorMessage(e, normalizedParticipantId);
        }catch (NotFoundFault e){
            return processSMLErrorMessage(e, normalizedParticipantId);
        } catch (Exception e) {
            LOG.error(e.getClass().getName() + "" + e.getMessage(), e);
            throw new SMPRuntimeException(ErrorCode.SML_INTEGRATION_EXCEPTION,e, ExceptionUtils.getRootCauseMessage(e));
        }
    }

    public boolean unregisterDomain(DBDomain domain) {
        if (!smlIntegrationEnabled) {
            return true;
        }
        LOG.info("Removing SMP id (Domain) from BDMSL: {} ", domain.getDomainCode());
        try {
            getSMPManagerClient(domain).delete(domain.getSmlSmpId());
            return true;
        }catch (BadRequestFault e){
            return processSMLErrorMessage(e, domain);
        } catch (NotFoundFault e){
            return processSMLErrorMessage(e, domain);
        } catch (Exception e) {
            LOG.error(e.getClass().getName() + "" + e.getMessage(), e);
            throw new SMPRuntimeException(ErrorCode.SML_INTEGRATION_EXCEPTION,e, ExceptionUtils.getRootCauseMessage(e));
        }
    }

    private IManageParticipantIdentifierWS getClient(DBDomain domain) {
;
        return ctx.getBean(IManageParticipantIdentifierWS.class, domain.getSmlClientKeyAlias(),
                domain.getSmlClientCertHeader(), domain.isSmlBlueCoatAuth());
    }

    private IManageServiceMetadataWS getSMPManagerClient(DBDomain domain) {


        return ctx.getBean(IManageServiceMetadataWS.class,
                domain.getSmlClientKeyAlias(), domain.getSmlClientCertHeader(), domain.isSmlBlueCoatAuth());
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        ctx = applicationContext;
    }

    public boolean isSmlIntegrationEnabled() {
        return smlIntegrationEnabled;
    }
    public boolean isSmlMultidomainEnabled() {
        return smlParticipantMultidomainEnabled;
    }
}
