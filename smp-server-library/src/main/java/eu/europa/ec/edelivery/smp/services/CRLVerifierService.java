package eu.europa.ec.edelivery.smp.services;

/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements. See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership. The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at
 * <p/>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p/>
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */


import eu.europa.ec.edelivery.smp.exceptions.ErrorCode;
import eu.europa.ec.edelivery.smp.exceptions.SMPRuntimeException;
import eu.europa.ec.edelivery.smp.logging.SMPLogger;
import eu.europa.ec.edelivery.smp.logging.SMPLoggerFactory;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.apache.cxf.helpers.IOUtils;
import org.apache.http.HttpHost;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStream;
import java.math.BigInteger;
import java.net.URL;
import java.security.cert.*;
import java.util.*;


@Service
public class CRLVerifierService {

    private static final SMPLogger LOG = SMPLoggerFactory.getLogger(CRLVerifierService.class);

    Map<String, X509CRL> crlCacheMap = new HashMap<>();
    Map<String, Long> crlCacheNextRefreshMap = new HashMap<>();
    public static long REFRESH_CRL_INTERVAL = 1000 * 60 * 60 * 24;
    public static Long NULL_LONG = new Long(-1);


    @Autowired
    ConfigurationService configurationService;


    public void verifyCertificateCRLs(String certificateId, String serial, List<String> crlDistPoints) throws CertificateRevokedException {

        if (crlDistPoints.isEmpty()) {
            LOG.warn("The certificate: [" + certificateId + "] has no CRL Lists");
            return;
        }

        for (String crlDP : crlDistPoints) {
            verifyCertificateCRLs(serial, crlDP);
        }

    }


    public void verifyCertificateCRLs(String serial, String crlDistributionPointURL) throws CertificateRevokedException {
        LOG.info("Download CRL " + crlDistributionPointURL);
        X509CRL crl = getCRLByURL(crlDistributionPointURL);


        if (crl == null) {
            throw new SMPRuntimeException(ErrorCode.CERTIFICATE_ERROR, "Can not verify CRL '" + crlDistributionPointURL
                    + "' for certificate of serial number : " + serial);
        }
        LOG.info("GOT  CRL " + crlDistributionPointURL + " CRL " + crl);

        if (crl.getRevokedCertificates() != null) {
            BigInteger bi = new BigInteger(serial.trim().replaceAll("\\s", ""), 16);

            X509CRLEntry entry = crl.getRevokedCertificate(bi);
            if (entry != null) {
                Map<String, Extension> map = new HashMap<>();
                throw new CertificateRevokedException(entry.getRevocationDate(), entry.getRevocationReason(), entry.getCertificateIssuer(), map);

            } else {
                LOG.debug("The CRL '" + crlDistributionPointURL + "' is null/empty for the given certificate");
            }
        }
    }

    public X509CRL getCRLByURL(String crlURL) {
        X509CRL x509CRL = null;
        if (StringUtils.isBlank(crlURL)) {
            return x509CRL;
        }
        Date currentDate = Calendar.getInstance().getTime();
        String url = crlURL.trim();
        if (crlCacheMap.containsKey(url)) {
            X509CRL crlTmp = crlCacheMap.get(url);
            Long nextRefresh = crlCacheNextRefreshMap.getOrDefault(url, NULL_LONG);
            ;
            if (nextRefresh > currentDate.getTime()) {
                x509CRL = crlTmp;
            }
        }
        if (x509CRL == null) {
            x509CRL = getCRLByURL(crlURL);
            Long nextRefresh = x509CRL.getNextUpdate() != null ? x509CRL.getNextUpdate().getTime()
                    : currentDate.getTime() + REFRESH_CRL_INTERVAL;

            crlCacheMap.put(crlURL, x509CRL);
            crlCacheNextRefreshMap.put(crlURL, nextRefresh);
        }
        return x509CRL;
    }


    /**
     * Downloads CRL from given URL. Supports http, https, ftp based
     * URLs.
     */
    public X509CRL downloadCRL(String crlURL) throws IOException,
            CertificateException, CRLException {

        InputStream crlStream;
        try {
            crlStream = downloadURL(crlURL);
        } catch (SMPRuntimeException e) {
            LOG.warn("Can not download CRL from certificate "
                    + "distribution point: " + crlURL, e);
            return null;
        }

        if (crlStream == null) {
            LOG.warn("Can not download CRL from certificate "
                    + "distribution point: " + crlURL);
            return null;
        }

        CertificateFactory cf = CertificateFactory.getInstance("X.509");
        X509CRL crl = (X509CRL) cf.generateCRL(crlStream);
        crlStream.close();
        return crl;
    }

    public InputStream downloadURL(String crlURL) {
        try {

            InputStream inputStream = null;
            if (!StringUtils.isEmpty(crlURL) && (crlURL.startsWith("http://") || crlURL.startsWith("https://")
                    || crlURL.startsWith("ftp://") || crlURL.startsWith("file:/"))) {

                URL targetUrl = new URL(crlURL);

                if (useProxy() && !doesTargetMatchNonProxy(targetUrl.getHost(), configurationService.getHttpNoProxyHosts())
                ) {
                    LOG.debug("Using proxy for downloading URL " + crlURL);
                    String decryptedPassword = configurationService.getDecryptedHttpPassword();
                    inputStream = downloadURLViaProxy(crlURL, configurationService.getHttpProxyHost(), configurationService.getHttpProxyPort(),
                            configurationService.getHttpUsername(), decryptedPassword);
                } else {
                    inputStream = downloadURLDirect(crlURL);
                }
            }
            return inputStream;
        } catch (Exception exc) {
            throw new SMPRuntimeException(ErrorCode.CONFIGURATION_ERROR, "Error occurred while downloading CRL:'" + crlURL + "'. Error " + ExceptionUtils.getRootCauseMessage(exc));
        }
    }

    public InputStream downloadURLViaProxy(String url, String proxyHost, Integer proxyPort, String proxyUser,
                                           String proxyPassword) throws IOException {

        CredentialsProvider credentialsProvider = null;
        if (isValidParameter(proxyUser, proxyPassword)) {
            credentialsProvider = new BasicCredentialsProvider();
            credentialsProvider.setCredentials(new AuthScope(proxyHost, proxyPort),
                    new UsernamePasswordCredentials(proxyUser, proxyPassword));
        }


        try (CloseableHttpClient httpclient = credentialsProvider == null ? HttpClients.custom().build() :
                HttpClients.custom().setDefaultCredentialsProvider(credentialsProvider).build()) {

            RequestConfig config = RequestConfig.custom().setProxy(new HttpHost(proxyHost, proxyPort)).build();
            HttpGet httpget = new HttpGet(url);
            httpget.setConfig(config);
            LOG.debug("Executing request " + url + " via proxy " + proxyHost +
                    (credentialsProvider == null ? " with no authentication." : "with username: " + proxyHost + "."));

            return execute(httpclient, httpget);
        }
    }

    public InputStream downloadURLDirect(String url) throws IOException {
        return execute(HttpClients.createDefault(), new HttpGet(url));
    }

    private InputStream execute(CloseableHttpClient httpclient, HttpGet httpget) throws IOException {
        try (CloseableHttpResponse response = httpclient.execute(httpget)) {
            return IOUtils.loadIntoBAIS(response.getEntity().getContent());
        }
    }


    private boolean useProxy() {
        if (!configurationService.isProxyEnabled()) {
            LOG.debug("Proxy not required. The property useProxy is not configured");
            return false;
        }
        return true;
    }

    private boolean isValidParameter(String... parameters) {
        if (parameters == null || parameters.length == 0) {
            return false;
        }

        for (String parameter : Arrays.asList(parameters)) {

            if (StringUtils.isEmpty(parameter)) {
                return false;
            }
        }
        return true;
    }

    /**
     * Method validates if host match non proxy list
     *
     * @param uriHost          target host
     * @param nonProxyHostList non proxy ist
     * @return true if host match nonProxy list else return false.
     */
    public boolean doesTargetMatchNonProxy(String uriHost, String nonProxyHostList) {
        String[] nonProxyHosts = StringUtils.isBlank(nonProxyHostList) ? null : nonProxyHostList.split("\\|");

        int nphLength = nonProxyHosts != null ? nonProxyHosts.length : 0;
        if (nonProxyHosts == null || nphLength < 1) {
            LOG.debug("host:'" + uriHost + "' : DEFAULT (0 non proxy host)");
            return false;
        }


        for (String nonProxyHost : nonProxyHosts) {
            String mathcRegExp = (nonProxyHost.startsWith("*") ? "." : "") + nonProxyHost;
            if (uriHost.matches(mathcRegExp)) {
                LOG.debug(" host:'" + uriHost + "' matches nonProxyHost '" + mathcRegExp + "' : NO PROXY");
                return true;
            }
        }
        LOG.debug(" host:'" + uriHost + "' : DEFAULT  (no match of " + Arrays.toString(nonProxyHosts) + " non proxy host)");
        return false;
    }


}
