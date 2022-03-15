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
import eu.europa.ec.edelivery.smp.utils.HttpUtils;
import eu.europa.ec.edelivery.smp.utils.X509CertificateUtils;
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

import javax.security.auth.x500.X500Principal;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigInteger;
import java.net.URL;
import java.security.cert.*;
import java.util.*;


@Service
public class CRLVerifierService {

    private static final SMPLogger LOG = SMPLoggerFactory.getLogger(CRLVerifierService.class);

    public static final int DEF_PROXY_PORT = 80;

    Map<String, X509CRL> crlCacheMap = new HashMap<>();
    Map<String, Long> crlCacheNextRefreshMap = new HashMap<>();
    public static final long REFRESH_CRL_INTERVAL = 1000L * 60 * 60;
    public static final Long NULL_LONG = Long.valueOf(-1);

    private static final X500Principal NULL_ISSUER = new X500Principal("");
    private static final CRLReason NULL_CRL_REASON = CRLReason.UNSPECIFIED;

    @Autowired
    ConfigurationService configurationService;


    public void verifyCertificateCRLs(X509Certificate cert) throws CertificateRevokedException {

        List<String> crlDistPoints = X509CertificateUtils.getCrlDistributionPoints(cert);
        if (crlDistPoints.isEmpty()) {
            LOG.warn("The certificate: '{}' has no CRL Lists.", cert.getSubjectX500Principal());
            return;
        }
        String crlUrl = X509CertificateUtils.extractHttpCrlDistributionPoint(crlDistPoints);
        BigInteger serNumber = cert.getSerialNumber();
        verifyCertificateCRLs(serNumber, crlUrl);
    }

    public void verifyCertificateCRLs(String serial, String crlDistributionPointURL) throws CertificateRevokedException {
        // remove
        String cleanSerial = serial.trim().replaceAll("\\s", "");
        BigInteger biSerial = new BigInteger(cleanSerial, 16);
        verifyCertificateCRLs(biSerial, crlDistributionPointURL);
    }


    public void verifyCertificateCRLs(BigInteger serial, String crlDistributionPointURL) throws CertificateRevokedException {
        LOG.info("Download CRL {}.", crlDistributionPointURL);
        X509CRL crl = getCRLByURL(crlDistributionPointURL);
        if (crl != null && crl.getRevokedCertificates() != null) {
            validateCertificateCRL(crl, serial);
        }
    }

    public X509CRL getCRLByURL(String crlURL) {
        X509CRL x509CRL = null;
        if (StringUtils.isBlank(crlURL)) {
            return x509CRL;
        }
        Date currentDate = Calendar.getInstance().getTime();
        String url = crlURL.trim();
        x509CRL = getCachedCRLByURL(url, currentDate);

        if (x509CRL == null) {
            // if CRL is null try to get one
            boolean mandatoryCrlValidation = configurationService.forceCRLValidation();
            x509CRL = downloadCRL(crlURL, mandatoryCrlValidation);
            // calculate next update in milliseconds...
            Long nextRefresh = x509CRL != null && x509CRL.getNextUpdate() != null ? x509CRL.getNextUpdate().getTime()
                    : currentDate.getTime() + REFRESH_CRL_INTERVAL;

            // set /replace data
            crlCacheMap.put(crlURL, x509CRL);
            crlCacheNextRefreshMap.put(crlURL, nextRefresh);
        }
        return x509CRL;
    }

    protected X509CRL getCachedCRLByURL(String crlURL, Date currentDate) {
        X509CRL x509CRL = null;
        if (StringUtils.isBlank(crlURL)) {
            return x509CRL;
        }
        String url = crlURL.trim();
        if (crlCacheMap.containsKey(url)) {
            X509CRL crlTmp = crlCacheMap.get(url);
            Long nextRefresh = crlCacheNextRefreshMap.getOrDefault(url, NULL_LONG);
            if (nextRefresh > currentDate.getTime()) {
                x509CRL = crlTmp;
            }
        }
        return x509CRL;
    }


    /**
     * Downloads CRL from given URL. Supports http, https, ftp based
     * URLs.
     */
    public X509CRL downloadCRL(String crlURL, boolean mandatoryCRLValidation) {

        X509CRL crl = null;
        SMPRuntimeException exception = null;
        try (InputStream crlStream = downloadURL(crlURL)) {
            if (crlStream != null) {
                CertificateFactory cf = CertificateFactory.getInstance("X.509");
                crl = (X509CRL) cf.generateCRL(crlStream);
            }
        } catch (IOException e) {
            exception = new SMPRuntimeException(ErrorCode.CERTIFICATE_ERROR, "Can not download CRL '" + crlURL
                    , ExceptionUtils.getRootCauseMessage(e), e);
        } catch (CertificateException e) {
            exception = new SMPRuntimeException(ErrorCode.CERTIFICATE_ERROR, "CRL list is not supported '" + crlURL
                    , ExceptionUtils.getRootCauseMessage(e), e);
        } catch (CRLException e) {
            exception = new SMPRuntimeException(ErrorCode.CERTIFICATE_ERROR, "CRL can not be read: '" + crlURL
                    , ExceptionUtils.getRootCauseMessage(e), e);
        } catch (SMPRuntimeException exc) {
            exception = exc;
        }
        // if exception occurred
        if (exception != null) {
            if (mandatoryCRLValidation) {
                throw exception;
            } else {
                LOG.warn(SMPLogger.SECURITY_MARKER, exception.getMessage(), exception);
            }
        }
        return crl;
    }

    public InputStream downloadURL(String crlURL) {
        try {

            InputStream inputStream = null;
            if (!StringUtils.isEmpty(crlURL) && (crlURL.startsWith("http://") || crlURL.startsWith("https://")
                    || crlURL.startsWith("ftp://") || crlURL.startsWith("file:/"))) {

                URL targetUrl = new URL(crlURL);

                if (useProxy() && !HttpUtils.doesTargetMatchNonProxy(targetUrl.getHost(), configurationService.getHttpNoProxyHosts())
                ) {
                    LOG.debug("Using proxy for downloading URL: {}.", crlURL);
                    String decryptedPassword = configurationService.getProxyCredentialToken();
                    Optional<Integer> proxyPort = configurationService.getHttpProxyPort();
                    inputStream = downloadURLViaProxy(crlURL, configurationService.getHttpProxyHost(),
                            proxyPort.isPresent() ? proxyPort.get() : DEF_PROXY_PORT,
                            configurationService.getProxyUsername(), decryptedPassword);
                } else {
                    inputStream = downloadURLDirect(crlURL);
                }
            }
            return inputStream;
        } catch (Exception exc) {
            throw new SMPRuntimeException(ErrorCode.CERTIFICATE_ERROR, "Error occurred while downloading CRL:'" + crlURL, ExceptionUtils.getRootCauseMessage(exc));
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
            // log username
            String logUserName = credentialsProvider == null ? "None" : proxyUser;
            LOG.debug("Executing request '{}' via proxy '{}' with user: '{}'.", url, proxyHost,
                    logUserName);

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

    private void validateCertificateCRL(X509CRL x509CRL, BigInteger bi) throws CertificateRevokedException {
        X509CRLEntry entry = x509CRL.getRevokedCertificate(bi);
        if (entry != null) {
            Map<String, Extension> map = new HashMap<>();

            throw new CertificateRevokedException(entry.getRevocationDate(),
                    entry.getRevocationReason() == null ? NULL_CRL_REASON : entry.getRevocationReason(),
                    entry.getCertificateIssuer() == null ? NULL_ISSUER : entry.getCertificateIssuer(), map);

        }
    }

}
