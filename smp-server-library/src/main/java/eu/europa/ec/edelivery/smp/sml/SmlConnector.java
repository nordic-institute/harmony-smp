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
import eu.europa.ec.edelivery.smp.data.ui.CertificateRO;
import eu.europa.ec.edelivery.smp.data.ui.enums.SMPPropertyEnum;
import eu.europa.ec.edelivery.smp.exceptions.ErrorCode;
import eu.europa.ec.edelivery.smp.exceptions.SMPRuntimeException;
import eu.europa.ec.edelivery.smp.logging.SMPLogger;
import eu.europa.ec.edelivery.smp.logging.SMPLoggerFactory;
import eu.europa.ec.edelivery.smp.services.ConfigurationService;
import eu.europa.ec.edelivery.smp.services.ui.UIKeystoreService;
import eu.europa.ec.edelivery.smp.services.ui.UITruststoreService;
import eu.europa.ec.edelivery.smp.utils.HttpUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.apache.cxf.configuration.jsse.TLSClientParameters;
import org.apache.cxf.configuration.security.CertificateConstraintsType;
import org.apache.cxf.configuration.security.CombinatorType;
import org.apache.cxf.configuration.security.DNConstraintsType;
import org.apache.cxf.configuration.security.ProxyAuthorizationPolicy;
import org.apache.cxf.endpoint.Client;
import org.apache.cxf.ext.logging.LoggingFeature;
import org.apache.cxf.frontend.ClientProxy;
import org.apache.cxf.transport.http.HTTPConduit;
import org.apache.cxf.transports.http.configuration.ProxyServerType;
import org.busdox.servicemetadata.locator._1.PublisherEndpointType;
import org.busdox.servicemetadata.locator._1.ServiceMetadataPublisherServiceForParticipantType;
import org.busdox.servicemetadata.locator._1.ServiceMetadataPublisherServiceType;
import org.oasis_open.docs.bdxr.ns.smp._2016._05.ParticipantIdentifierType;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.xml.ws.BindingProvider;
import javax.xml.ws.handler.MessageContext;
import java.net.MalformedURLException;
import java.net.URL;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.util.*;

import static eu.europa.ec.edelivery.smp.conversion.SmlIdentifierConverter.toBusdoxParticipantId;
import static eu.europa.ec.edelivery.smp.exceptions.SMLErrorMessages.*;
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

    private static final String SERVICE_METADATA_CONTEXT = "manageservicemetadata";
    private static final String PARTICIPANT_IDENTIFIER_CONTEXT = "manageparticipantidentifier";

    private static final String CLIENT_CERT_HEADER_KEY = "Client-Cert";

    @Autowired
    ConfigurationService configurationService;

    @Autowired
    UIKeystoreService keystoreService;

    @Autowired
    UITruststoreService truststoreService;

    private ApplicationContext ctx;

    public boolean registerInDns(ParticipantIdentifierType normalizedParticipantId, DBDomain domain) {


        if (!configurationService.isSMLIntegrationEnabled()) {
            return false;
        }
        String normalizedParticipantString = asString(normalizedParticipantId);
        if (!domain.isSmlRegistered()) {
            LOG.info("Participant {} is not registered to SML because domain {} is not registered!",
                    normalizedParticipantString, domain.getDomainCode());
            return false;
        }

        LOG.debug("Registering new Participant: {} to domain: {}.", normalizedParticipantString, domain.getDomainCode());
        try {
            ServiceMetadataPublisherServiceForParticipantType smlRequest = toBusdoxParticipantId(normalizedParticipantId, domain.getSmlSmpId());
            getParticipantWSClient(domain).create(smlRequest);
            LOG.info("Participant: {} registered to domain: {}.", normalizedParticipantString, domain.getDomainCode());
            return true;
        } catch (BadRequestFault e) {
            return processSMLErrorMessage(e, normalizedParticipantId);
        } catch (NotFoundFault e) {
            return processSMLErrorMessage(e, normalizedParticipantId);
        } catch (Exception e) {
            LOG.error(e.getClass().getName() + "" + e.getMessage(), e);
            throw new SMPRuntimeException(ErrorCode.SML_INTEGRATION_EXCEPTION, e, ExceptionUtils.getRootCauseMessage(e));
        }
    }

    protected boolean processSMLErrorMessage(BadRequestFault e, ParticipantIdentifierType participantIdentifierType) {
        if (!isOkMessage(participantIdentifierType, e.getMessage())) {
            LOG.error(e.getMessage(), e);
            throw new SMPRuntimeException(ErrorCode.SML_INTEGRATION_EXCEPTION, e, ExceptionUtils.getRootCauseMessage(e));
        }
        LOG.warn(e.getMessage(), e);
        return true;
    }

    protected boolean processSMLErrorMessage(NotFoundFault e, ParticipantIdentifierType participantIdentifierType) {
        if (!isOkMessage(participantIdentifierType, e.getMessage())) {
            LOG.error(e.getMessage(), e);
            throw new SMPRuntimeException(ErrorCode.SML_INTEGRATION_EXCEPTION, e, ExceptionUtils.getRootCauseMessage(e));
        }
        LOG.warn(e.getMessage(), e);
        return true;
    }

    /**
     * Ignore messages if already exists
     *
     * @param patId
     * @param errorMessage
     * @return
     */
    protected boolean isOkMessage(ParticipantIdentifierType patId, String errorMessage) {
        if (errorMessage == null) {
            return false;
        }
        String exp = String.format(ERR_PARTICIPANT_ALREADY_EXISTS, patId.getValue(), patId.getScheme());
        String exp2 = String.format(ERR_PARTICIPANT_NOT_EXISTS, patId.getValue(), patId.getScheme());
        return errorMessage.startsWith(exp) || errorMessage.startsWith(exp2);
    }


    /**
     * @param domain
     * @return
     */
    public boolean registerDomain(DBDomain domain) {

        if (!configurationService.isSMLIntegrationEnabled()) {
            return false;
        }
        String smpLogicalAddress = configurationService.getSMLIntegrationSMPLogicalAddress();
        String smpPhysicalAddress = configurationService.getSMLIntegrationSMPPhysicalAddress();
        LOG.info("Registering new Domain  to SML: (smpCode {} smp-smp-id {}) ", domain.getDomainCode(), domain.getSmlSmpId());
        try {
            ServiceMetadataPublisherServiceType smlSmpRequest = new ServiceMetadataPublisherServiceType();
            smlSmpRequest.setPublisherEndpoint(new PublisherEndpointType());
            smlSmpRequest.getPublisherEndpoint().setLogicalAddress(smpLogicalAddress);
            smlSmpRequest.getPublisherEndpoint().setPhysicalAddress(smpPhysicalAddress);
            smlSmpRequest.setServiceMetadataPublisherID(domain.getSmlSmpId());
            getSMPManagerWSClient(domain).create(smlSmpRequest);
            return true;
        } catch (BadRequestFault e) {
            return processSMLErrorMessage(e, domain);
        } catch (Exception e) {
            LOG.error(e.getClass().getName() + "" + e.getMessage(), e);
            throw new SMPRuntimeException(ErrorCode.SML_INTEGRATION_EXCEPTION, e, ExceptionUtils.getRootCauseMessage(e));
        }
    }

    private boolean processSMLErrorMessage(BadRequestFault e, DBDomain domain) {
        if (!isOkMessage(domain, e.getMessage())) {
            LOG.error(e.getMessage(), e);
            throw new SMPRuntimeException(ErrorCode.SML_INTEGRATION_EXCEPTION, e, ExceptionUtils.getRootCauseMessage(e));
        }
        LOG.warn(e.getMessage(), e);
        return true;
    }

    private boolean processSMLErrorMessage(NotFoundFault e, DBDomain domain) {
        if (!isOkMessage(domain, e.getMessage())) {
            LOG.error(e.getMessage(), e);
            throw new SMPRuntimeException(ErrorCode.SML_INTEGRATION_EXCEPTION, e, ExceptionUtils.getRootCauseMessage(e));
        }
        LOG.warn(e.getMessage(), e);
        return true;
    }

    /**
     * Ignore messages if already exists
     *
     * @param domain
     * @param errorMessage
     * @return
     */
    protected boolean isOkMessage(DBDomain domain, String errorMessage) {
        LOG.info("Validate SML error message for domain {} {}", errorMessage, domain.getDomainCode());
        if (errorMessage == null) {
            return false;
        }
        String exp = String.format(ERR_DOMAIN_ALREADY_EXISTS, domain.getSmlSmpId());
        String exp2 = String.format(ERR_DOMAIN_NOT_EXISTS, domain.getSmlSmpId());
        return errorMessage.startsWith(exp) || errorMessage.startsWith(exp2);
    }


    public boolean unregisterFromDns(ParticipantIdentifierType normalizedParticipantId, DBDomain domain) {
        if (!configurationService.isSMLIntegrationEnabled()) {
            return false;
        }
        String normalizedParticipantString = asString(normalizedParticipantId);
        if (!domain.isSmlRegistered()) {
            LOG.info("Participant {} is not unregistered from SML because domain {} is not registered!",
                    normalizedParticipantString, domain.getDomainCode());
            return false;
        }

        LOG.debug("Removing Participant: {} from domain: {}.", normalizedParticipantString, domain.getDomainCode());
        try {
            ServiceMetadataPublisherServiceForParticipantType smlRequest = toBusdoxParticipantId(normalizedParticipantId, domain.getSmlSmpId());
            getParticipantWSClient(domain).delete(smlRequest);
            LOG.info("Participant: {} removed domain: {}.", normalizedParticipantString, domain.getDomainCode());
            return true;
        } catch (BadRequestFault e) {
            return processSMLErrorMessage(e, normalizedParticipantId);
        } catch (NotFoundFault e) {
            return processSMLErrorMessage(e, normalizedParticipantId);
        } catch (Exception e) {
            LOG.error(e.getClass().getName() + "" + e.getMessage(), e);
            throw new SMPRuntimeException(ErrorCode.SML_INTEGRATION_EXCEPTION, e, ExceptionUtils.getRootCauseMessage(e));
        }
    }

    public boolean unregisterDomain(DBDomain domain) {
        if (!configurationService.isSMLIntegrationEnabled()) {
            return true;
        }
        LOG.info("Removing SMP id (Domain) from BDMSL: {} ", domain.getDomainCode());
        try {
            getSMPManagerWSClient(domain).delete(domain.getSmlSmpId());
            return true;
        } catch (BadRequestFault e) {
            return processSMLErrorMessage(e, domain);
        } catch (NotFoundFault e) {
            return processSMLErrorMessage(e, domain);
        } catch (Exception e) {
            LOG.error(e.getClass().getName() + "" + e.getMessage(), e);
            throw new SMPRuntimeException(ErrorCode.SML_INTEGRATION_EXCEPTION, e, ExceptionUtils.getRootCauseMessage(e));
        }
    }

    private IManageParticipantIdentifierWS getParticipantWSClient(DBDomain domain) {

        IManageParticipantIdentifierWS iManageServiceMetadataWS = ctx.getBean(IManageParticipantIdentifierWS.class, getSmlClientKeyAliasForDomain(domain),
                domain.getSmlClientCertHeader(), domain.isSmlBlueCoatAuth());
        // configure connection
        configureClient(PARTICIPANT_IDENTIFIER_CONTEXT, iManageServiceMetadataWS, domain);

        return iManageServiceMetadataWS;
    }

    private IManageServiceMetadataWS getSMPManagerWSClient(DBDomain domain) {


        IManageServiceMetadataWS iManageServiceMetadataWS = ctx.getBean(IManageServiceMetadataWS.class,
                getSmlClientKeyAliasForDomain(domain), domain.getSmlClientCertHeader(), domain.isSmlBlueCoatAuth());
        // configure value connection
        configureClient(SERVICE_METADATA_CONTEXT, iManageServiceMetadataWS, domain);


        return iManageServiceMetadataWS;
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        ctx = applicationContext;
    }

    protected String getSmlClientKeyAliasForDomain(DBDomain domain) {
        String alias = domain.getSmlClientKeyAlias();
        if (!domain.isSmlBlueCoatAuth() && StringUtils.isBlank(alias)) {
            List<CertificateRO> list = keystoreService.getKeystoreEntriesList();
            // if there is only one certificate than choose the one
            if (list.size() == 1) {
                alias = list.get(0).getAlias();
            }
        }
        return alias;
    }


    public void configureClient(String serviceEndpoint, Object smlPort, DBDomain domain) {

        String clientKeyAlias = getSmlClientKeyAliasForDomain(domain);
        String clientCertHttpHeader = domain.getSmlClientCertHeader();
        boolean blueCoatAuthentication = domain.isSmlBlueCoatAuth();

        Client client = ClientProxy.getClient(smlPort);
        URL url = configurationService.getSMLIntegrationUrl();
        if (url == null) {
            throw new IllegalArgumentException("Empty or null SML url. Check the configuration and set property: " + SMPPropertyEnum.SML_URL.getProperty());
        }
        URL urlSMPManagment;
        try {
            urlSMPManagment = new URL(StringUtils.appendIfMissing(url.toString(), "/") + serviceEndpoint);

        } catch (MalformedURLException e) {
            throw new IllegalArgumentException("Malformed SML URL: " + url, e);
        }
        boolean useTLS = urlSMPManagment.getProtocol().equalsIgnoreCase("https");
        Map<String, Object> requestContext = ((BindingProvider) smlPort).getRequestContext();
        requestContext.put(BindingProvider.ENDPOINT_ADDRESS_PROPERTY, urlSMPManagment.toString());

        // check if there is only one cert in  keystore
        if (!blueCoatAuthentication && StringUtils.isBlank(clientKeyAlias)) {
            List<CertificateRO> list = keystoreService.getKeystoreEntriesList();
            if (list.size() == 1) {
                // set the default alias
                clientKeyAlias = list.get(0).getAlias();
            } else if (list.isEmpty()) {
                throw new IllegalStateException("Empty keystore! Import Key for SML authentication to keystore!");
            } else {
                throw new IllegalStateException("More than one key in Keystore! Define alias for the domain SML authentication!");
            }
        }

        if (!blueCoatAuthentication && !useTLS) {
            LOG.warn("SML integration is wrongly configured. Uses 2-way-SSL HTTPS but URL is not HTTPS! Url: {}.", urlSMPManagment.toString());
        }

        HTTPConduit httpConduit = (HTTPConduit) client.getConduit();

        configureClientAuthentication(httpConduit, requestContext,
                blueCoatAuthentication ? clientCertHttpHeader : clientKeyAlias,
                blueCoatAuthentication, useTLS);
        configureFaultHandling(requestContext);
        configureProxy(httpConduit, urlSMPManagment);
        configurePayloadLogging(client);
    }


    public void configureClientAuthentication(HTTPConduit httpConduit, Map<String, Object> requestContext, String smlClientAuthentication, boolean blueCoatAuthentication, boolean useTLS) {
        LOG.info("Connect to SML (smlClientAuthentication: {} use Client-CertHeader: {})", smlClientAuthentication, blueCoatAuthentication);
        if (StringUtils.isBlank(smlClientAuthentication)) {
            throw new IllegalStateException("SML integration is wrongly configured, at least one authentication option is required: 2-way-SSL or Client-Cert header");
        }

        // set truststore...
        TLSClientParameters tlsParams = new TLSClientParameters();
        tlsParams.setUseHttpsURLConnectionDefaultSslSocketFactory(false);
        tlsParams.setUseHttpsURLConnectionDefaultHostnameVerifier(false);
        tlsParams.setCertConstraints(createCertConstraint(configurationService.getSMLIntegrationServerCertSubjectRegExp()));
        tlsParams.setDisableCNCheck(configurationService.smlDisableCNCheck());

        if (!blueCoatAuthentication) {
            LOG.info("SML X509 certificate authentication with alias  {}.", smlClientAuthentication);
            tlsParams.setCertAlias(smlClientAuthentication);
            tlsParams.setKeyManagers(keystoreService.getKeyManagers());
            LOG.info("SET KEY MANAGERS!  {}.", keystoreService.getKeyManagers());
        } else {
            Map<String, List<String>> customHeaders = new HashMap<>();
            customHeaders.put(CLIENT_CERT_HEADER_KEY, Arrays.asList(smlClientAuthentication));
            requestContext.put(MessageContext.HTTP_REQUEST_HEADERS, customHeaders);
        }
        if (useTLS) {
            httpConduit.setTlsClientParameters(tlsParams);
        }
    }


    public CertificateConstraintsType createCertConstraint(String regExp) {
        if (StringUtils.isBlank(regExp)) {
            return null;
        }

        CertificateConstraintsType ct = new CertificateConstraintsType();
        DNConstraintsType dnConstraintsType = new DNConstraintsType();
        dnConstraintsType.setCombinator(CombinatorType.fromValue("ALL"));
        dnConstraintsType.getRegularExpression().add(regExp);
        ct.setSubjectDNConstraints(dnConstraintsType);

        return ct;
    }

    private void configureFaultHandling(Map<String, Object> requestContext) {
        // CXF by default stops processing response in a few cases, ie.: when server returned HTTP 400 (SOAP Fault)
        // We want to finish processing such messages - otherwise we would not be able to log fault's reason.
        requestContext.put(HTTPConduit.NO_IO_EXCEPTIONS, true);
        requestContext.put(HTTPConduit.PROCESS_FAULT_ON_HTTP_400, true);
    }

    private void configurePayloadLogging(Client client) {
        client.getBus().setFeatures(Arrays.asList(new LoggingFeature()));
    }

    private void configureProxy(HTTPConduit httpConduit, URL targetUrl) {

        if (!configurationService.isProxyEnabled()) {
            return;
        }

        String noProxyHosts = configurationService.getHttpNoProxyHosts();
        if (HttpUtils.doesTargetMatchNonProxy(targetUrl.getHost(), configurationService.getHttpNoProxyHosts())) {
            LOG.info("Target host {} is match noProxy hosts {}!", targetUrl.getHost(), noProxyHosts);
            return;
        }
        String proxyServer = configurationService.getHttpProxyHost();
        Optional<Integer> proxyPort = configurationService.getHttpProxyPort();
        String proxyUser = configurationService.getProxyUsername();
        String proxyPassword = configurationService.getProxyCredentialToken();


        LOG.info("Configuring proxy for BDMSL integration client: {}:{}@{}:{}", proxyUser, "******", proxyServer, proxyPort.isPresent() ? proxyPort.get() : "");
        httpConduit.getClient().setProxyServerType(ProxyServerType.HTTP);
        httpConduit.getClient().setProxyServer(proxyServer);
        if (proxyPort.isPresent()) {
            httpConduit.getClient().setProxyServerPort(proxyPort.get());
        }

        if (!StringUtils.isBlank(proxyUser)) {
            ProxyAuthorizationPolicy proxyAuth = new ProxyAuthorizationPolicy();
            proxyAuth.setAuthorizationType("Basic");
            LOG.debug("Set proxy authentication {}", proxyUser);
            proxyAuth.setUserName(proxyUser);
            proxyAuth.setPassword(proxyPassword);
            httpConduit.setProxyAuthorization(proxyAuth);
        }

    }

}
