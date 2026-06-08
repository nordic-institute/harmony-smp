/*-
 * #START_LICENSE#
 * smp-spi
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
package eu.europa.ec.smp.spi.exceptions;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Sebastian-Ion TINCU
 * @since 5.2
 */
public class SMPException extends Exception implements TranslatedMessage {

    private final String messageCode;

    private final Map<String, Object> args;


    private String defaultTranslatedMessage = "";

    public SMPException(String messageCode) {
        this(messageCode, new HashMap<>());
    }

    public SMPException(String messageCode, Map<String, Object> args) {
        this.messageCode = messageCode;
        this.args = args;

    }

    public SMPException(String messageCode, Throwable cause) {
        this(messageCode, cause, new HashMap<>());
    }

    public SMPException(String messageCode, Throwable cause, Map<String, Object> args) {
        super(cause);
        this.messageCode = messageCode;
        this.args = args;
    }

    @Override
    public String getMessageCode() {
        return messageCode;
    }

    @Override
    public Map<String, Object> getMessageArgs() {
        return args;
    }

    @Override
    public void setDefaultTranslatedMessage(String message) {
        this.defaultTranslatedMessage = message;
    }

    @Override
    public String getMessage() {
        return defaultTranslatedMessage;
    }
}
