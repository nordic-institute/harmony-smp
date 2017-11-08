/*
 * Copyright 2017 European Commission | CEF eDelivery
 *
 * Licensed under the EUPL, Version 1.1 or – as soon they will be approved by the European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 *
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/software/page/eupl
 * or file: LICENCE-EUPL-v1.1.pdf
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the Licence is distributed on an "AS IS" basis,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the Licence for the specific language governing permissions and limitations under the Licence.
 */

/*
 * Copyright 2017 European Commission | CEF eDelivery
 *
 * Licensed under the EUPL, Version 1.1 or – as soon they will be approved by the European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 *
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/software/page/eupl
 * or file: LICENCE-EUPL-v1.1.pdf
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the Licence is distributed on an "AS IS" basis,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the Licence for the specific language governing permissions and limitations under the Licence.
 */

package eu.europa.ec.edelivery.smp.error;

import ec.services.smp._1.ErrorResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;

import static eu.europa.ec.edelivery.smp.error.ErrorBusinessCode.TECHNICAL;
import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;

/**
 * Created by migueti on 05/01/2017.
 */
public class ErrorResponseBuilder {

    private static final Logger log = LoggerFactory.getLogger(ErrorResponseBuilder.class);

    private HttpStatus status = INTERNAL_SERVER_ERROR;
    private ErrorBusinessCode errorBusinessCode = TECHNICAL;
    private String strErrorDescription = "Unexpected technical error occurred.";

    private static final SimpleDateFormat TIMESTAMP_FORMAT = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSz");

    private static String getErrorUniqueId() {
        StringBuilder errId = new StringBuilder();
        errId.append(TIMESTAMP_FORMAT.format(new Date()))
                .append(":")
                .append(UUID.randomUUID());
        return String.valueOf(errId);
    }

    public ErrorResponseBuilder(){}

    private ErrorResponseBuilder(HttpStatus status) {
        this.status = status;
    }

    public static ErrorResponseBuilder status(HttpStatus status) {
        return new ErrorResponseBuilder(status);
    }

    private ErrorResponse buildBody() {
        ErrorResponse err = new ErrorResponse();
        err.setBusinessCode(errorBusinessCode.name());
        err.setErrorDescription(strErrorDescription);
        err.setErrorUniqueId(getErrorUniqueId());

        return err;
    }

    public ErrorResponseBuilder businessCode(ErrorBusinessCode newErrorBusinessCode) {
        this.errorBusinessCode = newErrorBusinessCode;
        return this;
    }

    public ErrorResponseBuilder errorDescription(String newErrorDescription) {
        this.strErrorDescription = newErrorDescription;
        return this;
    }

    public ResponseEntity build() {
        return ResponseEntity.status(this.status)
                .contentType(MediaType.TEXT_XML)
                .body(this.buildBody());
    }

}
