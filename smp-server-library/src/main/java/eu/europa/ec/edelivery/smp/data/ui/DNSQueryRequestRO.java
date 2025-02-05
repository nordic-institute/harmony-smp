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

import java.io.Serializable;
import java.util.StringJoiner;

/**
 * DNS query request object with DNS type and query string
 *
 * @author Joze Rihtarsic
 * @since 5.1
 */
public class DNSQueryRequestRO implements Serializable {

    private static final long serialVersionUID = 9008583888835630034L;

    String identifierValue;
    String identifierScheme;
    String domainCode;
    String topDnsDomain;

    public String getIdentifierValue() {
        return identifierValue;
    }

    public void setIdentifierValue(String identifierValue) {
        this.identifierValue = identifierValue;
    }

    public String getIdentifierScheme() {
        return identifierScheme;
    }

    public void setIdentifierScheme(String identifierScheme) {
        this.identifierScheme = identifierScheme;
    }

    public String getDomainCode() {
        return domainCode;
    }

    public void setDomainCode(String domainCode) {
        this.domainCode = domainCode;
    }

    public String getTopDnsDomain() {
        return topDnsDomain;
    }

    public void setTopDnsDomain(String topDnsDomain) {
        this.topDnsDomain = topDnsDomain;
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", DNSQueryRequestRO.class.getSimpleName() + "[", "]")
                .add("identifierValue='" + identifierValue + "'")
                .add("identifierScheme='" + identifierScheme + "'")
                .add("domainCode='" + domainCode + "'")
                .add("topDnsDomain='" + topDnsDomain + "'")
                .toString();
    }
}

