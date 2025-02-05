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
package eu.europa.ec.edelivery.smp.services.spi.data;

import eu.europa.ec.smp.spi.api.model.ResponseData;

import java.io.ByteArrayOutputStream;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;

/**
 *  The resource metadata.
 *
 * @author Joze Rihtarsic
 * @since 5.0
 */
public class SpiResponseData implements ResponseData {

    OutputStream outputStream;
    Map<String, String> httpHeaders = new HashMap();
    String contentType;
    Integer responseCode;

    public SpiResponseData(OutputStream outputStream) {
        this.outputStream= outputStream;
    }

    public OutputStream getOutputStream() {
        if (outputStream==null) {
            outputStream = new ByteArrayOutputStream();
        }
        return outputStream;
    }

    public void addHttpHeader(String name, String value) {
        httpHeaders.put(name, value);
    }


    public Map<String, String> getHttpHeaders() {
        return httpHeaders;
    }

    public String getContentType() {
        return contentType;
    }

    public void setContentType(String contentType) {
        this.contentType = contentType;
    }

    public Integer getResponseCode() {
        return responseCode;
    }

    public void setResponseCode(Integer responseCode) {
        this.responseCode = responseCode;
    }
}
