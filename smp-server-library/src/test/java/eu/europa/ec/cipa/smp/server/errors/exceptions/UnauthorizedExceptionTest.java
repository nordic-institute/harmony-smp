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

package eu.europa.ec.cipa.smp.server.errors.exceptions;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

/**
 * Created by migueti on 16/01/2017.
 */
public class UnauthorizedExceptionTest {

    @Test
    public void testUnauthorizedExceptionThrown() {
        // given

        // when
        try {
            throwUnauthorizedException();
        } catch(UnauthorizedException ex) {
            // then
            assertEquals("Exception thrown", ex.getMessage());
            return;
        }
        fail();
    }

    private void throwUnauthorizedException() {
        throw new UnauthorizedException("Exception thrown");
    }
}
