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

import java.util.HashMap;
import java.util.Map;

public class SMPRuntimeException extends RuntimeException implements TranslatedMessage {

    private final ErrorCode errorCode;

    private final String messageCode;

    private final Map<String, Object> args;

    private String defaultTranslatedMessage;

    private final boolean translateMessageArgs;

    public SMPRuntimeException(ErrorCode errorCode, String messageCode) {
        this(errorCode, messageCode, new HashMap<>());
    }

    public SMPRuntimeException(ErrorCode errorCode, String messageCode, Map<String, Object> args) {
        this(errorCode, messageCode, args, false);
    }

    public SMPRuntimeException(ErrorCode errorCode, String messageCode, Map<String, Object> args, boolean translateMessageArgs) {
        this.errorCode = errorCode;
        this.messageCode = messageCode;
        this.args = args;
        this.translateMessageArgs = translateMessageArgs;
    }

    public SMPRuntimeException(ErrorCode errorCode, String messageCode, Throwable th) {
        this(errorCode, messageCode, th, new HashMap<>());
    }

    public SMPRuntimeException(ErrorCode errorCode, String messageCode, Throwable th, Map<String, Object> args) {
        this(errorCode, messageCode, args, false);
    }

    public SMPRuntimeException(ErrorCode errorCode, String messageCode, Throwable th, Map<String, Object> args, boolean translateMessageArgs) {
        super(th);
        this.errorCode = errorCode;
        this.messageCode = messageCode;
        this.args = args;
        this.translateMessageArgs = translateMessageArgs;
    }

    public ErrorCode getErrorCode() {
        return errorCode;
    }

    @Override
    public void setDefaultTranslatedMessage(String message) {
        this.defaultTranslatedMessage = message;
    }

    @Override
    public String getMessage() {
        return defaultTranslatedMessage;
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
    public boolean getTranslateMessageArgs() {
        return translateMessageArgs;
    }
}
