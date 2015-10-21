/*
 * Copyright 2015 e-CODEX Project
 *
 * Licensed under the EUPL, Version 1.1 or – as soon they
 * will be approved by the European Commission - subsequent
 * versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the
 * Licence.
 * You may obtain a copy of the Licence at:
 * http://ec.europa.eu/idabc/eupl5
 * Unless required by applicable law or agreed to in
 * writing, software distributed under the Licence is
 * distributed on an "AS IS" basis,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either
 * express or implied.
 * See the Licence for the specific language governing
 * permissions and limitations under the Licence.
 */

package eu.domibus.common.validators;

import eu.domibus.common.configuration.model.Property;
import eu.domibus.common.configuration.model.PropertySet;
import eu.domibus.common.exception.EbMS3Exception;
import eu.domibus.common.model.org.oasis_open.docs.ebxml_msg.ebms.v3_0.ns.core._200704.Messaging;
import eu.domibus.ebms3.common.dao.PModeProvider;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Christian Koch
 * @version 3.0
 * @since 3.0
 */

@Service
public class PropertyProfileValidator {
    private static final Log LOG = LogFactory.getLog(PropertyProfileValidator.class);

    @Autowired
    private PModeProvider pModeProvider;

    public void validate(final Messaging messaging, final String pmodeKey) throws EbMS3Exception {
        final List<Property> modifiablePropertyList = new ArrayList<>();
        final PropertySet propSet = this.pModeProvider.getLegConfiguration(pmodeKey).getPropertySet();
        if (propSet == null) {
            // no profile means everything is valid
            return;
        }

        final List<Property> profile = propSet.getProperties();

        modifiablePropertyList.addAll(profile);

        for (final eu.domibus.common.model.org.oasis_open.docs.ebxml_msg.ebms.v3_0.ns.core._200704.Property property : messaging.getUserMessage().getMessageProperties().getProperty()) {
            Property profiled = null;
            for (final Property profiledProperty : modifiablePropertyList) {
                if (profiledProperty.getKey().equals(property.getName())) {
                    profiled = profiledProperty;
                }
            }
            modifiablePropertyList.remove(profiled);
            if (profiled == null) {
                throw new EbMS3Exception(EbMS3Exception.EbMS3ErrorCode.EBMS_0010, "Property profiling for this exchange does not include a property named: " + property.getName(), null, null);
            }

            switch (profiled.getDatatype().toLowerCase()) {
                case "string":
                    break;
                case "int":
                    try {
                        Integer.parseInt(property.getValue());
                        break;
                    } catch (final NumberFormatException e) {
                        throw new EbMS3Exception(EbMS3Exception.EbMS3ErrorCode.EBMS_0010, "Property profiling for this exchange requires a INTEGER datatype for property named: " + property.getName() + ", but got " + property.getValue(), null, null);
                    }
                case "boolean":
                    if (property.getValue().equalsIgnoreCase("false") || property.getValue().equalsIgnoreCase("true")) {
                        break;
                    }
                    throw new EbMS3Exception(EbMS3Exception.EbMS3ErrorCode.EBMS_0010, "Property profiling for this exchange requires a BOOLEAN datatype for property named: " + property.getName() + ", but got " + property.getValue(), null, null);
                default:
                    PropertyProfileValidator.LOG.warn("Validation for Datatype " + profiled.getDatatype() + " not possible. This type is not known by the validator. The value will be accepted unchecked");
            }


        }
        for (final Property property : modifiablePropertyList) {
            if (property.isRequired()) {
                throw new EbMS3Exception(EbMS3Exception.EbMS3ErrorCode.EBMS_0010, "Required property missing: " + property, null, null);

            }
        }


    }
}
