/*-
 * #START_LICENSE#
 * smp-server-library
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
package eu.europa.ec.edelivery.smp.exceptions;

import eu.europa.ec.smp.spi.exceptions.TranslatedMessage;
import org.apache.commons.lang3.StringUtils;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

public class SMPRuntimeException extends RuntimeException implements TranslatedMessage {

    private final ErrorMessageType messageCode;

    private final Map<ErrorMessageArgument, Object> args = new HashMap<>();

    private String defaultTranslatedMessage;

    private final boolean translateMessageArgs;

    public SMPRuntimeException(ErrorMessageType messageCode) {
        this(messageCode, null, false);
    }

    public SMPRuntimeException(ErrorMessageType messageCode, Throwable th) {
        this(messageCode, th, false);
    }

    public SMPRuntimeException(ErrorMessageType messageCode, Throwable th, boolean translateMessageArgs) {
        super(th);
        this.messageCode = messageCode;
        this.translateMessageArgs = translateMessageArgs;
    }

    public SMPRuntimeException addParam(ErrorMessageArgument key, Object value) {
        this.args.put(key, value == null ? "" : value);
        return this;
    }

    public ErrorCode getErrorCode() {
        return messageCode.getErrorCode();
    }

    @Override
    public void setDefaultTranslatedMessage(String message) {
        this.defaultTranslatedMessage = message;
    }

    @Override
    public String getMessage() {
        if (StringUtils.isEmpty(defaultTranslatedMessage)) {
            return messageCode.getMessageTranslation(args);
        }
        return defaultTranslatedMessage;
    }

    @Override
    public String getMessageCode() {
        return messageCode.getMessageCode();
    }

    @Override
    public Map<String, Object> getMessageArgs() {
        // Convert keys to String
        return args.entrySet().stream()
                .collect(Collectors.toMap(e -> e.getKey().getArgumentName(), Map.Entry::getValue));

    }

    @Override
    public boolean getTranslateMessageArgs() {
        return translateMessageArgs;
    }
}
