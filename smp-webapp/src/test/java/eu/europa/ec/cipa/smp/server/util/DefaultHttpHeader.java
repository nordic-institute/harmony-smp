/*
 * Copyright 2017 European Commission | CEF eDelivery
 *
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by the European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 *
 * You may obtain a copy of the Licence attached in file: LICENCE-EUPL-v1.2.pdf
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the Licence is distributed on an "AS IS" basis,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the Licence for the specific language governing permissions and limitations under the Licence.
 */

package eu.europa.ec.cipa.smp.server.util;

import javax.ws.rs.core.Cookie;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import java.util.*;

/**
 * Created by rodrfla on 13/01/2017.
 */
public class DefaultHttpHeader implements HttpHeaders {

    private Map<String, List<String>> headerMap;
    private List<String> requestHeaders;

    {
        headerMap = new HashMap<>();
        requestHeaders = new ArrayList<>();
    }

    public void addRequestHeader(String headerParameter, List<String> values) {
        headerMap.put(headerParameter, values);
    }

    @Override
    public List<String> getRequestHeader(String headerParameter) {
        return headerMap.get(headerParameter);
    }

    @Override
    public String getHeaderString(String name) {
        return null;
    }

    @Override
    public MultivaluedMap<String, String> getRequestHeaders() {
        return null;
    }

    @Override
    public List<MediaType> getAcceptableMediaTypes() {
        return null;
    }

    @Override
    public List<Locale> getAcceptableLanguages() {
        return null;
    }

    @Override
    public MediaType getMediaType() {
        return null;
    }

    @Override
    public Locale getLanguage() {
        return null;
    }

    @Override
    public Map<String, Cookie> getCookies() {
        return null;
    }

    @Override
    public Date getDate() {
        return null;
    }

    @Override
    public int getLength() {
        return 0;
    }
}
