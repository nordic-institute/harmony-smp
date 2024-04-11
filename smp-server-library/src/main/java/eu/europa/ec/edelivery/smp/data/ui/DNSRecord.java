/*-
 * #START_LICENSE#
 * smp-server-library
 * %%
 * Copyright (C) 2017 - 2023 European Commission | eDelivery | DomiSMP
 * %%
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by the European Commission - subsequent
 * versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy of the Licence at:
 *
 * [PROJECT_HOME]\license\eupl-1.2\license.txt or https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the Licence is
 * distributed on an "AS IS" basis, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the Licence for the specific language governing permissions and limitations under the Licence.
 * #END_LICENSE#
 */
package eu.europa.ec.edelivery.smp.data.ui;

import eu.europa.ec.dynamicdiscovery.enums.DNSLookupType;

import java.io.Serializable;
import java.util.StringJoiner;

/**
 * DNS Record  with DNS type, raw  result string
 *
 * @author Joze Rihtarsic
 * @since 5.1
 */
public class DNSRecord implements Serializable {

    private static final long serialVersionUID = 9008583888835630033L;

    DNSLookupType dnsType;
    String dnsDomain;
    String rawRecordResult;
    String naptrService;
    String value;

    public DNSRecord() {
    }

    public DNSRecord(String dnsDomain, DNSLookupType dnsType, String rawRecordResult, String value) {
        this(dnsDomain, dnsType, rawRecordResult, null, value);
    }

    public DNSRecord(String dnsDomain, DNSLookupType dnsType, String rawRecordResult, String naptrService, String value) {
        this.dnsDomain = dnsDomain;
        this.dnsType = dnsType;
        this.rawRecordResult = rawRecordResult;
        this.naptrService = naptrService;
        this.value = value;
    }

    public String getNaptrService() {
        return naptrService;
    }

    public void setNaptrService(String naptrService) {
        this.naptrService = naptrService;
    }

    public DNSLookupType getDnsType() {
        return dnsType;
    }

    public void setDnsType(DNSLookupType dnsType) {
        this.dnsType = dnsType;
    }

    public String getRawRecordResult() {
        return rawRecordResult;
    }

    public void setRawRecordResult(String rawRecordResult) {
        this.rawRecordResult = rawRecordResult;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", DNSRecord.class.getSimpleName() + "[", "]")
                .add("dnsType=" + dnsType)
                .add("rawRecordResult='" + rawRecordResult + "'")
                .add("value='" + value + "'")
                .toString();
    }
}
