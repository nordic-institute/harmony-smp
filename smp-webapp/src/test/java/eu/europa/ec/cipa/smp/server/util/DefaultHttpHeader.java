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
