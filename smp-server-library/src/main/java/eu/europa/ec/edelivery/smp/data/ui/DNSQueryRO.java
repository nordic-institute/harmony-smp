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
import java.util.ArrayList;
import java.util.List;
import java.util.StringJoiner;

/**
 * DNS query request object with DNS type and query string
 *
 * @since 5.1
 */
public class DNSQueryRO implements Serializable {

    private static final long serialVersionUID = 9008583888835630032L;

    private String dnsQuery;
    private DNSLookupType dnsType;
    private List<DNSRecord> dnsRecordEntries = new ArrayList<>();
    private List<String> dnsErrors = new ArrayList<>();

    public DNSQueryRO() {
    }

    public DNSQueryRO(String dnsQuery, DNSLookupType dnsType) {
        this.dnsQuery = dnsQuery;
        this.dnsType = dnsType;
    }

    public String getDnsQuery() {
        return dnsQuery;
    }

    public void setDnsQuery(String dnsQuery) {
        this.dnsQuery = dnsQuery;
    }

    public DNSLookupType getDnsType() {
        return dnsType;
    }

    public void setDnsType(DNSLookupType dnsType) {
        this.dnsType = dnsType;
    }

    public void addDnsRecordEntry(DNSRecord dnsRecord) {
        dnsRecordEntries.add(dnsRecord);
    }

    public void addDnsRecordEntry(String domain, DNSLookupType type, String rawResult, String value) {
        dnsRecordEntries.add(new DNSRecord(domain, type, rawResult, value));
    }

    public List<DNSRecord> getDnsEntries() {
        return dnsRecordEntries;
    }

    public void addDnsError(String dnsError) {
        dnsErrors.add(dnsError);
    }

    public List<String> getDnsErrors() {
        return dnsErrors;
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", DNSQueryRO.class.getSimpleName() + "[", "]")
                .add("dnsQuery='" + dnsQuery + "'")
                .add("dnsType=" + dnsType)
                .toString();
    }
}
