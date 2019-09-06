/*
 * Copyright 2017 European Commission | CEF eDelivery
 *
 * Licensed under the EUPL, Version 1.1 or – as soon they will be approved by the European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 *
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/software/page/eupl
 * or file: LICENCE-EUPL-v1.1.pdf
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the Licence is distributed on an "AS IS" basis,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the Licence for the specific language governing permissions and limitations under the Licence.
 */

package eu.europa.ec.edelivery.smp.sml;

import eu.europa.ec.bdmsl.ws.soap.IManageParticipantIdentifierWS;
import eu.europa.ec.bdmsl.ws.soap.IManageServiceMetadataWS;
import eu.europa.ec.bdmsl.ws.soap.ManageBusinessIdentifierService;
import eu.europa.ec.bdmsl.ws.soap.ManageServiceMetadataService;
import eu.europa.ec.edelivery.smp.logging.SMPLogger;
import eu.europa.ec.edelivery.smp.logging.SMPLoggerFactory;
import eu.europa.ec.edelivery.smp.services.ConfigurationService;
import eu.europa.ec.edelivery.smp.services.ui.UIKeystoreService;
import eu.europa.ec.edelivery.smp.utils.HttpUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.cxf.configuration.jsse.TLSClientParameters;
import org.apache.cxf.configuration.security.ProxyAuthorizationPolicy;
import org.apache.cxf.endpoint.Client;
import org.apache.cxf.ext.logging.LoggingFeature;
import org.apache.cxf.frontend.ClientProxy;
import org.apache.cxf.transport.http.HTTPConduit;
import org.apache.cxf.transports.http.configuration.ProxyServerType;
import org.apache.http.client.utils.URIBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import javax.xml.ws.BindingProvider;
import javax.xml.ws.handler.MessageContext;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static eu.europa.ec.edelivery.smp.data.ui.enums.SMPPropertyEnum.SML_URL;
import static java.util.Arrays.asList;
import static org.apache.commons.lang3.StringUtils.isNotBlank;

/**
 * Factory creating CXF client that access BDMSL via SOAP interface.
 * Produced client has already configured all transport and authentication parameters like URL, keystore, proxy etc...
 * <p>
 * Created by gutowpa on 14/12/2017.
 */
@Component
public class SmlClientFactory {

    private static final SMPLogger LOG = SMPLoggerFactory.getLogger(SmlClientFactory.class);

    private static final String CLIENT_CERT_HEADER_KEY = "Client-Cert";

    @Autowired
    ConfigurationService configurationService;

    @Autowired
    UIKeystoreService keystoreService;

    @Bean
    @Scope("prototype")
    public IManageParticipantIdentifierWS create(String clientKeyAlias, String clientCertHttpHeader, boolean blueCoatAuthentication) {
        LOG.info("create IManageParticipantIdentifierWS with alias {} http-header {}", clientKeyAlias, clientCertHttpHeader);
        ManageBusinessIdentifierService smlService = new ManageBusinessIdentifierService((URL) null);
        IManageParticipantIdentifierWS smlPort = smlService.getManageBusinessIdentifierServicePort();

        submitRequest("manageparticipantidentifier", smlPort, clientKeyAlias, clientCertHttpHeader, blueCoatAuthentication);

        return smlPort;
    }

    @Bean
    @Scope("prototype")
    public IManageServiceMetadataWS createSmp(String clientKeyAlias, String clientCertHttpHeader, boolean blueCoatAuthentication) {
        LOG.info("create IManageServiceMetadataWS with alias {} http-header {}", clientKeyAlias, clientCertHttpHeader);

        ManageServiceMetadataService smlService = new ManageServiceMetadataService((URL) null);
        IManageServiceMetadataWS smlPort = smlService.getManageServiceMetadataServicePort();
        submitRequest("manageservicemetadata", smlPort, clientKeyAlias, clientCertHttpHeader, blueCoatAuthentication);

        return smlPort;
    }

    public void submitRequest(String serviceEndpoint, Object smlPort, String clientKeyAlias, String clientCertHttpHeader, boolean blueCoatAuthentication) {

        Client client = ClientProxy.getClient(smlPort);

        URL url = configurationService.getSMLIntegrationUrl();
        if (url ==null) {
            throw new IllegalStateException("Empty or null SML url. Check the configuration and set property: " + SML_URL.getProperty());
        }
        URL urlSMPManagment;
        try {
            urlSMPManagment = new URL(StringUtils.appendIfMissing(url.toString(),"/")+serviceEndpoint);

        } catch (MalformedURLException e) {
            throw new IllegalStateException("Malformed SML URL: " + url, e);
        }

        HTTPConduit httpConduit = (HTTPConduit) client.getConduit();
        Map<String, Object> requestContext = ((BindingProvider) smlPort).getRequestContext();
        requestContext.put(BindingProvider.ENDPOINT_ADDRESS_PROPERTY, urlSMPManagment.toString());

        configureFaultHandling(requestContext);
        configureProxy(httpConduit, urlSMPManagment);
        configurePayloadLogging(client);
        configureClientAuthentication(httpConduit, requestContext,
                blueCoatAuthentication ? null : clientKeyAlias,
                blueCoatAuthentication ? clientCertHttpHeader : null);
    }


    public void configureClientAuthentication(HTTPConduit httpConduit, Map<String, Object> requestContext, String smlClientKeyAlias, String smlClientCertHttpHeader) {
        LOG.info("Connect to SML (alias: {} http-header: {})", smlClientKeyAlias, smlClientCertHttpHeader);
        if (isNotBlank(smlClientKeyAlias) && isNotBlank(smlClientCertHttpHeader)) {
            throw new IllegalStateException("SML integration is wrongly configured, cannot use both authentication ways at the same time: 2-way-SSL and Client-Cert header");
        }

        if (isNotBlank(smlClientKeyAlias)) {
            TLSClientParameters tlsParams = new TLSClientParameters();
            tlsParams.setCertAlias(smlClientKeyAlias);
            tlsParams.setKeyManagers(keystoreService.getKeyManagers());
            httpConduit.setTlsClientParameters(tlsParams);
        } else if (isNotBlank(smlClientCertHttpHeader)) {
            Map<String, List<String>> customHeaders = new HashMap<>();
            customHeaders.put(CLIENT_CERT_HEADER_KEY, asList(smlClientCertHttpHeader));
            requestContext.put(MessageContext.HTTP_REQUEST_HEADERS, customHeaders);
        } else {
            throw new IllegalStateException("SML integration is wrongly configured, at least one authentication option is required: 2-way-SSL or Client-Cert header");
        }
    }

    private void configureFaultHandling(Map<String, Object> requestContext) {
        // CXF by default stops processing response in a few cases, ie.: when server returned HTTP 400 (SOAP Fault)
        // We want to finish processing such messages - otherwise we would not be able to log fault's reason.
        requestContext.put(HTTPConduit.NO_IO_EXCEPTIONS, true);
        requestContext.put(HTTPConduit.PROCESS_FAULT_ON_HTTP_400, true);
    }

    private void configurePayloadLogging(Client client) {
        client.getBus().setFeatures(asList(new LoggingFeature()));
    }

    private void configureProxy(HTTPConduit httpConduit, URL targetUrl) {

        String proxyNoHosts = configurationService.getHttpNoProxyHosts();
        if (configurationService.isProxyEnabled() &&
                !HttpUtils.doesTargetMatchNonProxy(targetUrl.getHost(), configurationService.getHttpNoProxyHosts())) {
            return;
        }
        String proxyServer = configurationService.getHttpProxyHost();
        Optional<Integer> proxyPort = configurationService.getHttpProxyPort();
        String proxyUser = configurationService.getProxyUsername();
        String proxyPassword = configurationService.getProxyCredentialToken();


        LOG.info("Configuring proxy for BDMSL integration client: {}:{}@{}:{}", proxyUser, "########", proxyServer, proxyPort.isPresent() ? proxyPort.get() : "");
        httpConduit.getClient().setProxyServerType(ProxyServerType.HTTP);
        httpConduit.getClient().setProxyServer(proxyServer);
        if (proxyPort.isPresent()) {
            httpConduit.getClient().setProxyServerPort(proxyPort.get());
        }
        ProxyAuthorizationPolicy proxyAuth = new ProxyAuthorizationPolicy();
        proxyAuth.setUserName(proxyUser);
        proxyAuth.setPassword(proxyPassword);
        httpConduit.setProxyAuthorization(proxyAuth);
    }
}
