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
package eu.europa.ec.edelivery.smp.exceptions;

import org.apache.commons.lang3.StringUtils;
import org.springframework.security.authentication.BadCredentialsException;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;
/**
 * Exception thrown when authentication fails due to bad credentials.
 *
 * @since 5.2
 * @author Joze Rihtarsic
 */
public class SMPBadCredentialsException extends BadCredentialsException implements I18NException {
    private String defaultTranslatedMessage;

    private final ErrorMessageType messageCode;

    private final Map<ErrorMessageArgument, Object> args = new HashMap<>();

    public SMPBadCredentialsException(ErrorMessageType messageCode) {
        this(messageCode, null);
    }

    public SMPBadCredentialsException(ErrorMessageType messageCode, Throwable th) {
        super(messageCode.getTemplate(), th);
        this.messageCode = messageCode;
    }

    public SMPBadCredentialsException addParam(ErrorMessageArgument key, Object value) {
        this.args.put(key, value == null ? "" : value);
        return this;
    }

    @Override
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
                .collect(Collectors.toMap(e ->
                        e.getKey().getArgumentName(), Map.Entry::getValue));
    }
}
