package eu.europa.ec.edelivery.smp.servlet;

import eu.europa.ec.edelivery.smp.data.model.DBDomain;
import eu.europa.ec.edelivery.smp.services.resource.ResolvedData;

import java.io.InputStream;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static org.apache.commons.lang3.StringUtils.lowerCase;
import static org.apache.commons.lang3.StringUtils.trim;

public class ResourceRequest {

    ResourceAction action;

    Map<String, String> httpHeaders;

    List<String> urlPathParameters;
    InputStream getInputStream;

    DBDomain authorizedDomain;

    ResolvedData resolvedData;


    public ResourceRequest(ResourceAction action, Map<String, String> httpHeaders, List<String> urlPathParameters, InputStream getInputStream) {
        this.action = action;
        this.httpHeaders = httpHeaders;
        this.urlPathParameters = urlPathParameters;
        this.getInputStream = getInputStream;
    }

    public ResourceAction getAction() {
        return action;
    }

    public void setAction(ResourceAction action) {
        this.action = action;
    }

    public String getOwnerHttpParameter() {
        String owner =  getHeader(WebConstants.HTTP_PARAM_OWNER);

        return owner;
    }

    public String getDomainHttpParameter() {
        return getHeader(WebConstants.HTTP_PARAM_DOMAIN);
    }

    public String getResourceTypeHttpParameter() {
        return getHeader(WebConstants.HTTP_PARAM_RESOURCE_TYPE);
    }

    public List<String> getUrlPathParameters() {
        return urlPathParameters;
    }

    public DBDomain getAuthorizedDomain() {
        return authorizedDomain;
    }

    public void setAuthorizedDomain(DBDomain authorizedDomain) {
        this.authorizedDomain = authorizedDomain;
    }

    public InputStream getInputStream() {
        return getInputStream;
    }

    public ResolvedData getResolvedData() {
        return resolvedData;
    }

    public void setResolvedData(ResolvedData resolvedData) {
        this.resolvedData = resolvedData;
    }

    /**
     * Returns the value of the request header. If header does not exist this method returns <code>null</code>.
     * If there are multiple headers with the same name, this method
     * returns the first header in the request.
     * The header name is case insensitive. You can use
     * this method with any request header.
     *
     * @param name a <code>String</code> specifying the
     *             header name
     * @return a <code>String</code> containing the
     * value of the requested
     * header, or <code>null</code>
     * if the request does not
     * have a header of that name
     */
    public String getHeader(String name) {
        String key = lowerCase(trim(name));
        return httpHeaders != null && httpHeaders.containsKey(key) ? httpHeaders.get(key) : null;
    }

    public String getUrlPathParameter(int pathParameter) {
        return urlPathParameters != null && urlPathParameters.size() > pathParameter ? urlPathParameters.get(pathParameter) : null;

    }

    @Override
    public String toString() {
        return "ResourceRequest{" +
                "action=" + action +
                ", httpHeaders=" + headersToString() +
                ", urlPathParameters=" + pathParameterToString() +
                ", authorizedDomain=" + authorizedDomain +
                ", resolvedData=" + resolvedData +
                '}';
    }

    private String headersToString(){
        return  httpHeaders == null? null:
                httpHeaders.keySet().stream()
                .map(key -> key + "=" + httpHeaders.get(key))
                .collect(Collectors.joining(", ", "{", "}"));
    }

    private String pathParameterToString(){
        return  urlPathParameters == null? null:
                urlPathParameters.stream()
                        .collect(Collectors.joining(", ", "{", "}"));
    }
}
