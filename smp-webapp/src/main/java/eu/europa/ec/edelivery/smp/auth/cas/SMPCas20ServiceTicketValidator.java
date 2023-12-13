/*-
 * #%L
 * smp-webapp
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
 * #L%
 */
package eu.europa.ec.edelivery.smp.auth.cas;

import org.apache.commons.lang3.StringUtils;
import org.jasig.cas.client.validation.Assertion;
import org.jasig.cas.client.validation.Cas20ServiceTicketValidator;
import org.jasig.cas.client.validation.TicketValidationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * EU-Login (ECAS) service is based on the Central Authentication Service (CAS) version 2 developed at Yale University.
 * This class is an extension of the Cas 2.0 ticket validator. The SMPCas20ServiceTicketValidator enables to set ticket validation endpoints "urlSuffix" according to ECAS security policy. For options of the URL, ECAS suffix see the
 * ECAS Client Installation and Configuration Guide - Basic. If the URL suffix is not given, the default value is used: "serviceValidate".
 *
 * @author Joze Rihtarsic
 * @since 4.2
 */
public class SMPCas20ServiceTicketValidator extends Cas20ServiceTicketValidator {
    private static final Logger LOG = LoggerFactory.getLogger(Cas20ServiceTicketValidator.class);

    private final String urlSuffix;

    public SMPCas20ServiceTicketValidator(String casServerUrl, String urlSuffix) {
        super(casServerUrl);
        this.urlSuffix = urlSuffix;
    }

    @Override
    protected String getUrlSuffix() {
        if (StringUtils.isBlank(urlSuffix)){
            LOG.warn("Cas20 ServiceTicketValidator url suffix is not configured. Use default value: [{}]", super.getUrlSuffix());
            return super.getUrlSuffix();
        }
        return urlSuffix;
    }

    @Override
    protected void customParseResponse(final String response, final Assertion assertion)
            throws TicketValidationException {
        LOG.debug("Got CAS response: [{}] and test it with assertion [{}]",response,assertion );
    }
}
