/*-
 * #START_LICENSE#
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
 * #END_LICENSE#
 */
package eu.europa.ec.edelivery.smp.config.properties;

import eu.europa.ec.edelivery.smp.config.PropertyUpdateListener;
import eu.europa.ec.edelivery.smp.config.enums.SMPPropertyEnum;
import eu.europa.ec.edelivery.smp.conversion.IdentifierService;
import eu.europa.ec.edelivery.smp.logging.SMPLogger;
import eu.europa.ec.edelivery.smp.logging.SMPLoggerFactory;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import static eu.europa.ec.edelivery.smp.config.enums.SMPPropertyEnum.*;

/**
 * It is used to update identifier configuration if properties are changed.
 * It listens for changes in the following properties:
 * <ul>
 *     <li>{@link SMPPropertyEnum#PARTC_SCH_SPLIT_REGEXP}</li>
 *     <li>{@link SMPPropertyEnum#PARTC_SCH_VALIDATION_REGEXP}</li>
 *     <li>{@link SMPPropertyEnum#PARTC_SCH_MANDATORY}</li>
 *     <li>{@link SMPPropertyEnum#CS_PARTICIPANTS}</li>
 *     <li>{@link SMPPropertyEnum#CS_DOCUMENTS}</li>
 * </ul>
 *
 * @author Joze Rihtarsic
 * @since 5.0
 */
@Component
public class SMPIdentifierServicePropertyUpdateListener implements PropertyUpdateListener {
    private static final SMPLogger LOG = SMPLoggerFactory.getLogger(SMPIdentifierServicePropertyUpdateListener.class);

    IdentifierService identifierService;

    public SMPIdentifierServicePropertyUpdateListener(IdentifierService identifierService) {
        this.identifierService = identifierService;
    }

    @Override
    public void updateProperties(Map<SMPPropertyEnum, Object> properties) {
        if (identifierService == null) {
            LOG.warn("No IdentifierService found, Skip IdentifierService configuration!");
            return;
        }
        Boolean partcSchemeMandatory = (Boolean) properties.get(PARTC_SCH_MANDATORY);
        Pattern partcSchemePattern = (Pattern) properties.get(PARTC_SCH_VALIDATION_REGEXP);
        List<String> partcCaseSensitiveSchemes = (List<String>) properties.get(CS_PARTICIPANTS);
        List<String> docCaseSensitiveSchemes = (List<String>) properties.get(CS_DOCUMENTS);

        identifierService.configureParticipantIdentifierFormatter(partcCaseSensitiveSchemes, partcSchemeMandatory, partcSchemePattern);
        identifierService.configureDocumentIdentifierFormatter(docCaseSensitiveSchemes);
    }

    @Override
    public List<SMPPropertyEnum> handledProperties() {
        return Arrays.asList(
                PARTC_SCH_SPLIT_REGEXP,
                PARTC_SCH_VALIDATION_REGEXP,
                PARTC_SCH_MANDATORY,
                CS_PARTICIPANTS,
                CS_DOCUMENTS
        );
    }
}
