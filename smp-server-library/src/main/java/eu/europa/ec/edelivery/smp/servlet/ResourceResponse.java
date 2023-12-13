/*-
 * #%L
 * smp-server-library
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
 * #L%
 */
package eu.europa.ec.edelivery.smp.servlet;

import eu.europa.ec.edelivery.smp.exceptions.SMPRuntimeException;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.OutputStream;

import static eu.europa.ec.edelivery.smp.exceptions.ErrorCode.INVALID_REQUEST;

public class ResourceResponse {

    HttpServletResponse response;

    public ResourceResponse(HttpServletResponse response) {
        this.response = response;
    }

    public int getHttpStatus() {
        return response.getStatus();
    }

    public void setHttpStatus(int httpStatus) {
        response.setStatus(httpStatus);
    }

    public String getMimeType() {
        return response.getContentType();
    }

    public void setContentType(String mimeType) {
        response.setContentType(mimeType);
    }

    public String getHttpHeader(String name) {
        return response.getHeader(name);
    }

    public void setHttpHeader(String name, String value) {
        response.setHeader(name, value);
    }

    public OutputStream getOutputStream() {
        try {
            return response.getOutputStream();
        } catch (IOException e) {
            throw new SMPRuntimeException(INVALID_REQUEST, "Can not open output stream for response!", e);
        }
    }

}
