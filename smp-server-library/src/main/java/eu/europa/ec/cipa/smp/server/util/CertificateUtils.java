/*
 * Copyright 2017 European Commission | CEF eDelivery
 *
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by the European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 *
 * You may obtain a copy of the Licence attached in file: LICENCE-EUPL-v1.2.pdf
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the Licence is distributed on an "AS IS" basis,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the Licence for the specific language governing permissions and limitations under the Licence.
 */

package eu.europa.ec.cipa.smp.server.util;

import eu.europa.ec.edelivery.smp.exceptions.CertificateAuthenticationException;
import eu.europa.ec.cipa.smp.server.security.CertificateDetails;
import org.apache.commons.lang3.StringEscapeUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateUtils;

import javax.naming.InvalidNameException;
import javax.naming.ldap.LdapName;
import javax.naming.ldap.Rdn;
import java.math.BigInteger;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

/**
 * Class with some utils used in Certificates
 * Created by migueti on 06/12/2016.
 */
public class CertificateUtils {

    /**
     * Left pads a String with 0's
     *
     * @param str String to be padded
     * @return String padded
     */
    private static String convertStringtoPaddedString(String str) {
        return StringUtils.leftPad(str, 16, "0");
    }

    /**
     * Converts number into an Hexadecimal String
     *
     * @param number BigInteger number
     * @return Hexadecimal string value of number
     */
    public static String convertBigIntToHexString(BigInteger number) {
        return convertStringtoPaddedString(number.toString(16));
    }

    /**
     * Returns Certificate Id
     *
     * @param subjectName  Subject Name
     * @param serialNumber Serial Number
     * @return CertificateId composed by SubjectName + ":" + SerialNumber HexString
     */
    private static String prepareCertificateId(String subjectName, String serialNumber) {
        StringBuilder strBuilder = new StringBuilder();
        strBuilder.append(subjectName);
        strBuilder.append(":");
        serialNumber = removeHexHeader(serialNumber);
        strBuilder.append(convertStringtoPaddedString(serialNumber));
        return String.valueOf(strBuilder);
    }

    /**
     * Returns Certificate Id
     *
     * @param subjectName  Subject Name
     * @param serialNumber Serial Number
     * @return CertificateId composed by SubjectName + ":" + SerialNumber HexString
     */
    public static String returnCertificateId(String subjectName, BigInteger serialNumber) {
        return prepareCertificateId(subjectName, convertBigIntToHexString(serialNumber));
    }

    /**
     * Returns Certificate Id
     *
     * @param subjectName  Subject Name
     * @param serialNumber Serial Number
     * @return CertificateId composed by SubjectName + ":" + SerialNumber HexString
     */
    public static String returnCertificateId(String subjectName, String serialNumber) {
        return prepareCertificateId(subjectName, serialNumber);
    }

    /**
     * Removes hexa header (0x) from string, if it contains or itself if not
     *
     * @param hexString Hexadecimal String
     * @return String without 0x
     */
    public static String removeHexHeader(String hexString) {
        return hexString.replaceFirst("0x", "");
    }

    /**
     * This method calculates the certificate id from the certificate's header
     *
     * @param certHeaderValue Certificate's header
     * @return Certificate Id
     * @throws CertificateAuthenticationException Certificate Authentication Exception
     */
    public final static CertificateDetails getCommonNameFromCalculateHeaderCertificateId(final String certHeaderValue) throws CertificateAuthenticationException {
        CertificateDetails certificateDetails = calculateCertificateIdFromHeader(certHeaderValue);
        certificateDetails.setCertificateId(returnCertificateId(certificateDetails.getSubject(), certificateDetails.getSerial()));
        return certificateDetails;
    }

    /**
     * This method calculates the certificate id from the certificate's header
     *
     * @param certHeaderValue Certificate's header
     * @return Certificate Id
     * @throws CertificateAuthenticationException Certificate Authentication Exception
     */
    public final static CertificateDetails calculateCertificateIdFromHeader(final String certHeaderValue) throws CertificateAuthenticationException {
        String clientCertHeaderDecoded = null;
        synchronized (CertificateUtils.class) {
            try {
                CertificateDetails certificate = new CertificateDetails();
                clientCertHeaderDecoded = URLDecoder.decode(certHeaderValue, StandardCharsets.UTF_8.name());
                clientCertHeaderDecoded = StringEscapeUtils.unescapeHtml4(clientCertHeaderDecoded);

                certificate = parseClientCertHeader(certificate, clientCertHeaderDecoded);
                certificate.setSerial(certificate.getSerial().replaceAll(":", ""));
                // in the database, the subject is stored in a different way than the one in the
                //client cert header and without spaces we thus need to rebuild it
                String subject = certificate.getSubject();

                LdapName ldapName;
                try {
                    ldapName = new LdapName(subject);
                } catch (InvalidNameException exc) {
                    throw new CertificateAuthenticationException("Impossible to identify authorities for certificate " + subject, exc);
                }
                // Make a map from type to name
                final Map<String, Rdn> parts = new HashMap<>();
                for (final Rdn rdn : ldapName.getRdns()) {
                    parts.put(rdn.getType(), rdn);
                }

                final String subjectName = parts.get("CN").toString() + "," + parts.get("O").toString() + "," + parts.get("C").toString();
                certificate.setSubject(subjectName);

                return certificate;
            } catch (final Exception exc) {
                throw new CertificateAuthenticationException(String.format("Impossible to determine the certificate identifier from encoded = %s and decoded = %s", certHeaderValue, clientCertHeaderDecoded), exc);
            }
        }
    }

    /**
     * Parses Client Certification Header
     *
     * @param clientCertHeaderDecoded Client Certification's Header
     * @throws CertificateAuthenticationException Certificate Authentication Exception
     */
    private static CertificateDetails parseClientCertHeader(CertificateDetails certificate, String clientCertHeaderDecoded) throws CertificateAuthenticationException {
        final String HEADER_ATTRIBUTE_SEPARATOR = "&";
        final String[] HEADER_ATTRIBUTE_SUBJECT = {"subject"};
        final String[] HEADER_ATTRIBUTE_SERIAL = {"serial", "sno"};
        final String[] HEADER_ATTRIBUTE_VALID_FROM = {"validFrom"};
        final String[] HEADER_ATTRIBUTE_VALID_TO = {"validTo"};
        final String[] HEADER_ATTRIBUTE_ISSUER = {"issuer"};
        final String[] HEADER_ATTRIBUTE_POLICY_OIDS = {"policy_oids"};

        String[] split = clientCertHeaderDecoded.split(HEADER_ATTRIBUTE_SEPARATOR);

        if (split.length < 5) {
            throw new CertificateAuthenticationException(String.format("Invalid BlueCoat Client Certificate Header Received [%s] ", Arrays.toString(split)));
        }
        DateFormat df = new SimpleDateFormat("MMM d hh:mm:ss yyyy zzz", Locale.US);
        for (final String attribute : split) {
            if (isIn(attribute, HEADER_ATTRIBUTE_ISSUER)) {
                certificate.setIssuer(attribute.substring(attribute.indexOf('=') + 1));
            } else if (isIn(attribute, HEADER_ATTRIBUTE_SERIAL)) {
                certificate.setSerial(attribute.substring(attribute.indexOf('=') + 1));
            } else if (isIn(attribute, HEADER_ATTRIBUTE_SUBJECT)) {
                certificate.setSubject(attribute.substring(attribute.indexOf('=') + 1));
            } else if (isIn(attribute, HEADER_ATTRIBUTE_POLICY_OIDS)) {
                certificate.setPolicyOids(attribute.substring(attribute.indexOf('=') + 1));
            } else if (isIn(attribute, HEADER_ATTRIBUTE_VALID_FROM)) {
                try {
                    certificate.setValidFrom(DateUtils.toCalendar(df.parse(attribute.substring(attribute.indexOf('=') + 1))));
                } catch (ParseException e) {
                    throw new CertificateAuthenticationException(
                            "Invalid BlueCoat Client Certificate Header Received (Unparsable Date for " + HEADER_ATTRIBUTE_VALID_FROM + ") ");
                }
            } else if (isIn(attribute, HEADER_ATTRIBUTE_VALID_TO)) {
                try {
                    certificate.setValidTo(DateUtils.toCalendar(df.parse(attribute.substring(attribute.indexOf('=') + 1))));
                } catch (ParseException e) {
                    throw new CertificateAuthenticationException(
                            "Invalid BlueCoat Client Certificate Header Received (Unparsable Date for " + HEADER_ATTRIBUTE_VALID_TO + ") ");
                }
            } else {
                throw new CertificateAuthenticationException(
                        "Unknown BlueCoat Client Certificate Header Received: " + attribute);
            }
        }
        certificate.setRootCertificateDN(certificate.getIssuer());

        return certificate;
    }

    /**
     * Checks if an attribute is part of the array of attributes
     *
     * @param attribute        Attribute to check for
     * @param headerAttributes Array of attributes
     * @return True, if attribute is in headerAttributes; false, otherwise
     */
    private static boolean isIn(String attribute, String[] headerAttributes) {
        for (String headerAttribute : headerAttributes) {
            if (attribute.toLowerCase(Locale.US).startsWith(headerAttribute.toLowerCase(Locale.US))) {
                return true;
            }
        }
        return false;
    }

    public static String orderSubjectByDefaultMetadata(String subject) throws CertificateAuthenticationException {
        try {
            LdapName ldapName = new LdapName(subject);

            // Make a map from type to name
            final Map<String, Rdn> parts = new HashMap<>();
            for (final Rdn rdn : ldapName.getRdns()) {
                parts.put(rdn.getType(), rdn);
            }

            return parts.get("CN").toString() + "," + parts.get("O").toString() + "," + parts.get("C").toString();
        } catch (Exception exc) {
            throw new CertificateAuthenticationException("Impossible to identify authorities for certificate " + subject, exc);
        }
    }
}
