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
import eu.europa.ec.cipa.smp.server.errors.ParentExceptionTest;
import eu.europa.ec.smp.api.exceptions.MalformedIdentifierException;
import org.junit.Test;
import org.xml.sax.SAXException;

import javax.ws.rs.core.Response;
import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;

import static eu.europa.ec.cipa.smp.server.errors.ErrorBusinessCode.FORMAT_ERROR;
import static javax.ws.rs.core.Response.Status.BAD_REQUEST;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

/**
 * Created by migueti on 19/01/2017.
 */
public class MalformedIdentifierExceptionMapperTest extends ParentExceptionTest {

    @Test
    public void testToResponse() throws IOException, SAXException, ParserConfigurationException {
        // given
        MalformedIdentifierExceptionMapper mapper = new MalformedIdentifierExceptionMapper();
        Exception exception = new Exception();
        MalformedIdentifierException malformedException = new MalformedIdentifierException("TEST_ID", exception);

        // when
        Response response = mapper.toResponse(malformedException);

        // then
        ErrorResponse entity = (ErrorResponse) response.getEntity();
        assertEquals(BAD_REQUEST.getStatusCode(), response.getStatus());

        String errorUniqueId = checkXmlError(entity, FORMAT_ERROR, "Malformed identifier, scheme and id should be delimited by double colon: TEST_ID");
        assertNotNull(errorUniqueId);
    }
}
