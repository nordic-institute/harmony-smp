/*-
 * #START_LICENSE#
 * smp-webapp
 * %%
 * Copyright (C) 2017 - 2024 European Commission | eDelivery | DomiSMP
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
package eu.europa.ec.edelivery.smp.services;

import eu.europa.ec.edelivery.smp.exceptions.ErrorMessageArgument;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Map;
import java.util.Properties;

import static org.mockito.ArgumentMatchers.anyString;

/**
 * @author Sebastian-Ion TINCU
 * @since 5.2
 */
@ExtendWith(MockitoExtension.class)
class SMPExceptionLanguageServiceTest {

    final Properties messageCodes = new Properties();

    @Mock
    SMPLanguageResourceService smpLanguageResourceService;

    @InjectMocks
    SMPExceptionLanguageService smpExceptionLanguageService;


    @BeforeEach
    public void setup() {
        Mockito.when(smpLanguageResourceService.getErroProperties(anyString())).thenReturn(messageCodes);
    }

    @AfterEach
    public void teardown() {
        messageCodes.clear();
    }

    @Test
    public void translateMessageCode() {
        messageCodes.put("message.code", "Message [{{property}}]");

        String translation = smpExceptionLanguageService.getMessageTranslation("message.code",
                Map.of("property", "value"));

        Assertions.assertEquals("Message [value]", translation);
    }

    @Test
    public void translateMessageCode_ReturnsMessageCodeAsTranslationWhenNotFound() {
        String translation = smpExceptionLanguageService.getMessageTranslation("non.existent.message.code");

        Assertions.assertEquals("non.existent.message.code", translation);
    }

    @Test
    public void translateInnerMessageCode() {
        String outerMessageCode = "message.code.outer";
        String innerMessageCode = "message.code.inner";
        messageCodes.put(outerMessageCode, "Outer message [{{errorMessageCode}}]"); // inner
        messageCodes.put(innerMessageCode, "Inner message [{{property}}]");
        String translation = smpExceptionLanguageService.getMessageTranslation(outerMessageCode,
                Map.of(ErrorMessageArgument.ERROR_MESSAGE_CODE.getArgumentName(), innerMessageCode,
                        "property", "value"));
        Assertions.assertEquals("Outer message [Inner message [value]]", translation);
    }
}