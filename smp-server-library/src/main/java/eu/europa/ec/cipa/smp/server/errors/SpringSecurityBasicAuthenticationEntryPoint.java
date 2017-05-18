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

package eu.europa.ec.cipa.smp.server.errors;

import ec.services.smp._1.ErrorResponse;
import eu.europa.ec.cipa.smp.server.errors.exceptions.UnauthorizedException;
import eu.europa.ec.cipa.smp.server.errors.mappers.UnauthorizedExceptionMapper;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.www.BasicAuthenticationEntryPoint;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.core.Response;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import java.io.IOException;
import java.io.StringWriter;

/**
 * Created by gutowpa on 27/01/2017.
 */

/*
TODO This is an ugly glue-code to make use Jersey-error-mappings in the Spring-security error handling beans
This class should be removed once we migrate to Spring
*/
@Deprecated
public class SpringSecurityBasicAuthenticationEntryPoint extends BasicAuthenticationEntryPoint {

    public SpringSecurityBasicAuthenticationEntryPoint() {
        this.setRealmName("any realm name");
    }

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response,
                         AuthenticationException authException) throws IOException, ServletException {

        Response jerseyResponse = new UnauthorizedExceptionMapper().toResponse(new UnauthorizedException(authException.getMessage()));
        String errorResponse = marshall((ErrorResponse) jerseyResponse.getEntity());
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType("text/xml");
        response.getOutputStream().print(errorResponse);
    }

    private static String marshall(ErrorResponse errorResponse) {
        try {
            StringWriter sw = new StringWriter();
            JAXBContext jaxbContext = JAXBContext.newInstance(ErrorResponse.class);
            Marshaller jaxbMarshaller = jaxbContext.createMarshaller();
            jaxbMarshaller.marshal(errorResponse, sw);
            return sw.toString();
        } catch (JAXBException e) {
            return e.getMessage();
        }
    }
}
