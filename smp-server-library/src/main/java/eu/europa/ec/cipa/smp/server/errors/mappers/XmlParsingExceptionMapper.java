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

package eu.europa.ec.cipa.smp.server.errors.mappers;

import ec.services.smp._1.ErrorResponse;
import eu.europa.ec.cipa.smp.server.errors.ErrorResponseBuilder;
import eu.europa.ec.cipa.smp.server.errors.exceptions.XmlParsingException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

import static eu.europa.ec.cipa.smp.server.errors.ErrorBusinessCode.XSD_INVALID;
import static javax.ws.rs.core.Response.Status.BAD_REQUEST;

/**
 * Created by migueti on 19/01/2017.
 */
@Provider
public class XmlParsingExceptionMapper implements ExceptionMapper<XmlParsingException>{
    private static final Logger s_aLogger = LoggerFactory.getLogger (XmlParsingExceptionMapper.class);

    @Override
    public Response toResponse(XmlParsingException e) {
        Response response = ErrorResponseBuilder.status(BAD_REQUEST)
                .businessCode(XSD_INVALID)
                .errorDescription(e.getMessage())
                .build();
        ErrorResponse errorResponse = (ErrorResponse) response.getEntity();
        s_aLogger.warn (String.format("%s : %s", errorResponse.getErrorUniqueId(), e.getMessage()));
        s_aLogger.warn ("exception: ", e);
        return response;
    }
}
