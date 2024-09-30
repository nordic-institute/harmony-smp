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
package eu.europa.ec.edelivery.smp.error.exceptions;

import eu.europa.ec.edelivery.smp.exceptions.ErrorBusinessCode;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

/**
 * Smp ResponseStatusException extension to hold also smp business error code. Exception is used for REST API "Fault" responses
 *
 * @author Joze Rihtarsic
 * @since 4.2
 */
public class SMPResponseStatusException extends ResponseStatusException {
    private final ErrorBusinessCode errorBusinessCode;

    public SMPResponseStatusException(ErrorBusinessCode errorBusinessCode, HttpStatus httpStatus, String sMsg) {
        super(httpStatus, sMsg);
        this.errorBusinessCode = errorBusinessCode;
    }

    public ErrorBusinessCode getErrorBusinessCode() {
        return errorBusinessCode;
    }

}
