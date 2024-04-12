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

/**
 * @author Joze Rihtarsic
 * @since 5.0
 *
 * The external validation library throws the exception if the payload validation does not pass.
 */
public class SignatureException extends Exception {
    public enum ErrorCode {
        CONFIGURATION_ERROR,
        SIGNATURE_ERROR,
        INVALID_PARAMETERS,
        INTERNAL_ERROR,
    }

    ErrorCode errorCode;
    public SignatureException(ErrorCode code, String message) {
        super(message);
        this.errorCode = code;
    }

    public SignatureException(ErrorCode code, String message, Throwable cause) {
        super(message, cause);
        this.errorCode = code;
    }


    public ErrorCode getErrorCode() {
        return errorCode;
    }
}
