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
import eu.europa.ec.bdmsl.ws.soap.ManageBusinessIdentifierService;
import org.apache.cxf.configuration.jsse.TLSClientParameters;
import org.apache.cxf.configuration.security.ProxyAuthorizationPolicy;
import org.apache.cxf.endpoint.Client;
import org.apache.cxf.ext.logging.LoggingFeature;
import org.apache.cxf.frontend.ClientProxy;
import org.apache.cxf.transport.http.HTTPConduit;
import org.apache.cxf.transports.http.configuration.ProxyServerType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.net.ssl.KeyManager;
import javax.net.ssl.KeyManagerFactory;
import javax.xml.ws.BindingProvider;
import javax.xml.ws.handler.MessageContext;
import java.io.FileInputStream;
import java.net.URL;
import java.security.KeyStore;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static java.lang.String.format;
import static java.util.Arrays.asList;
import static org.apache.commons.lang3.StringUtils.isBlank;
import static org.apache.commons.lang3.StringUtils.isNotBlank;

/**
 * Created by gutowpa on 14/12/2017.
 */
@Component
public class SmlClientFactory {

    private static final Logger log = LoggerFactory.getLogger(SmlClientFactory.class);

    private static final String CLIENT_CERT_HEADER_KEY = "Client-Cert";

    @Value("${bdmsl.integration.url}")
    private URL smlUrl;

    @Value("${bdmsl.integration.keystore.path}")
    private String smlClientKeyStorePath;

    @Value("${bdmsl.integration.keystore.password}")
    private String smlClientKeyStorePassword;

    @Value("${bdmsl.integration.keystore.alias}")
    private String smlClientKeyAlias;

    @Value("${bdmsl.integration.http.header.client.cert}")
    private String smlClientCertHttpHeader;

    private KeyManager[] keyManagers;


    @Value("${bdmsl.integration.proxy.server}")
    private String proxyServer;

    @Value("${bdmsl.integration.proxy.port}")
    private Optional<Integer> proxyPort;

    @Value("${bdmsl.integration.proxy.user}")
    private String proxyUser;

    @Value("${bdmsl.integration.proxy.password}")
    private String proxyPassword;


    @Bean()
    @Scope("prototype")
    public IManageParticipantIdentifierWS create() {
        ManageBusinessIdentifierService smlService = new ManageBusinessIdentifierService((URL) null);
        IManageParticipantIdentifierWS smlPort = smlService.getManageBusinessIdentifierServicePort();
        Client client = ClientProxy.getClient(smlPort);

        HTTPConduit httpConduit = (HTTPConduit) client.getConduit();
        Map<String, Object> requestContext = ((BindingProvider) smlPort).getRequestContext();
        requestContext.put(BindingProvider.ENDPOINT_ADDRESS_PROPERTY, smlUrl.toString());

        configureFaultHandling(requestContext);
        configureProxy(httpConduit);
        configurePayloadLogging(client);
        configureClientAuthentication(httpConduit, requestContext);

        return smlPort;
    }

    @PostConstruct
    public void init() {
        if (isNotBlank(smlClientKeyStorePath)) {
            loadKeyStore();
        }
    }

    private void loadKeyStore() {
        try {
            FileInputStream fileStream = new FileInputStream(smlClientKeyStorePath);
            KeyStore keyStore = KeyStore.getInstance("JKS");
            keyStore.load(fileStream, smlClientKeyStorePassword.toCharArray());
            KeyManagerFactory kmf = KeyManagerFactory.getInstance("SunX509");
            kmf.init(keyStore, smlClientKeyStorePassword.toCharArray());
            keyManagers = kmf.getKeyManagers();
        } catch (Exception e) {
            throw new IllegalStateException("Could not load keystore for SML integration: " + smlClientKeyStorePath, e);
        }
    }

    private void configureClientAuthentication(HTTPConduit httpConduit, Map<String, Object> requestContext) {
        if (isNotBlank(smlClientKeyStorePath) && isNotBlank(smlClientCertHttpHeader)) {
            throw new IllegalStateException("SML integration is wrongly configured, cannot use both authentication ways at the same time: 2-way-SSL and Client-Cert header");
        }

        if (isNotBlank(smlClientKeyStorePath)) {
            TLSClientParameters tlsParams = new TLSClientParameters();
            tlsParams.setCertAlias(smlClientKeyAlias);
            tlsParams.setKeyManagers(keyManagers);
            httpConduit.setTlsClientParameters(tlsParams);
        } else if (isNotBlank(smlClientCertHttpHeader)) {
            Map<String, List<String>> customHeaders = new HashMap<>();
            customHeaders.put(CLIENT_CERT_HEADER_KEY, asList(smlClientCertHttpHeader));
            requestContext.put(MessageContext.HTTP_REQUEST_HEADERS, customHeaders);
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

    private void configureProxy(HTTPConduit httpConduit) {
        if (isBlank(proxyServer)) {
            return;
        }

        log.info(format("Configuring proxy for BDMSL integration client: %s:%s@%s:%d", proxyUser, "########", proxyServer, proxyPort.get()));
        httpConduit.getClient().setProxyServerType(ProxyServerType.HTTP);
        httpConduit.getClient().setProxyServer(proxyServer);
        httpConduit.getClient().setProxyServerPort(proxyPort.get());
        ProxyAuthorizationPolicy proxyAuth = new ProxyAuthorizationPolicy();
        proxyAuth.setUserName(proxyUser);
        proxyAuth.setPassword(proxyPassword);
        httpConduit.setProxyAuthorization(proxyAuth);
    }
}
