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
package eu.europa.ec.cipa.smp.server.services.common;

import com.helger.commons.exceptions.InitializationException;

import javax.annotation.Nonnull;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.net.URI;
import java.net.URISyntaxException;

/**
 * REST Web Service for redirection
 * 
 * @author Jerry Dimitriou
 */
@Path ("/")
public final class RedirectInterface {
  private static final URI INDEX_HTML;

  static {
    try {
      INDEX_HTML = new URI ("web/index.html");
    }
    catch (final URISyntaxException e) {
      throw new InitializationException ("Failed to build index URI");
    }
  }

  public RedirectInterface () {}

  @GET
  @Produces (MediaType.TEXT_HTML)
  @Nonnull
  public Response displayHomeURI () {
    return Response.seeOther (INDEX_HTML).build ();
  }
}
