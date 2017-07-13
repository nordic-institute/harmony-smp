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
package eu.europa.ec.cipa.smp.server.hook;

import eu.europa.ec.cipa.smp.server.util.to_be_removed.ESuccess;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nonnull;
import javax.servlet.*;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletResponseWrapper;
import java.io.IOException;

/**
 * Filter which handles post-registration hooks. If a registration was started
 * in <code>AbstractRegistrationHook</code>, this filter will make sure the
 * registration is ended by calling
 * <code>AbstractRegistrationHook.postUpdate(ESuccess)</code>.
 *
 * @author PEPPOL.AT, BRZ, Philip Helger
 */
public final class PostRegistrationFilter implements Filter {
  /**
   * This response wrapper simply captures the status of the response in an
   * easily accessible way.
   *
   * @author Ravnholdt
   */
  private static final class HttpServletResponseWrapperWithStatus extends HttpServletResponseWrapper {
    private int m_nStatus = HttpServletResponse.SC_OK;

    public HttpServletResponseWrapperWithStatus (final HttpServletResponse aHttpResponse) {
      super (aHttpResponse);
    }

    @Override
    public void sendError (final int nStatusCode) throws IOException {
      super.sendError (nStatusCode);
      m_nStatus = nStatusCode;
    }

    @Override
    public void sendError (final int nStatusCode, final String msg) throws IOException {
      super.sendError (nStatusCode, msg);
      m_nStatus = nStatusCode;
    }

    @Override
    public void setStatus (final int nStatusCode) {
      super.setStatus (nStatusCode);
      m_nStatus = nStatusCode;
    }

    @Override
    public void setStatus (final int nStatusCode, final String sStatusMessage) {
      super.setStatus (nStatusCode, sStatusMessage);
      m_nStatus = nStatusCode;
    }

    public int getStatus () {
      return m_nStatus;
    }
  }

  private static final Logger s_aLogger = LoggerFactory.getLogger (PostRegistrationFilter.class);

  public void init (final FilterConfig arg0) {}

  private static void _notifyRegistrationHook (@Nonnull final ESuccess eSuccess) throws ServletException {
    final AbstractRegistrationHook aCallback = AbstractRegistrationHook.getQueue ();
    if (aCallback != null) {
      try {
        aCallback.postUpdate (eSuccess);
      }
      catch (final HookException e) {
        throw new ServletException (e);
      }
      finally {
        // Ensure that no memory leak resides in the ThreadLocal
        AbstractRegistrationHook.resetQueue ();
      }
    }
  }

  public void doFilter (final ServletRequest aRequest, final ServletResponse aResponse, final FilterChain aFilterChain) throws IOException,
                                                                                                                       ServletException {
    // Wrap the response
    final HttpServletResponseWrapperWithStatus aResponseWrapper = new HttpServletResponseWrapperWithStatus ((HttpServletResponse) aResponse);
    try {
      aFilterChain.doFilter (aRequest, aResponseWrapper);

      // Success or failure?
      if (aResponseWrapper.getStatus () >= 400) {
        if (s_aLogger.isDebugEnabled ())
          s_aLogger.debug ("Operation failed, status: " + aResponseWrapper.getStatus ());
        _notifyRegistrationHook (ESuccess.FAILURE);
      }
      else {
        if (s_aLogger.isDebugEnabled ())
          s_aLogger.debug ("Operation ok, status: " + aResponseWrapper.getStatus ());
        _notifyRegistrationHook (ESuccess.SUCCESS);
      }
    }
    catch (final IOException e) {
      s_aLogger.warn ("Got IOException " + e.getMessage ());
      _notifyRegistrationHook (ESuccess.FAILURE);
      throw e;
    }
    catch (final ServletException e) {
      s_aLogger.warn ("Got ServletException " + e.getMessage ());
      _notifyRegistrationHook (ESuccess.FAILURE);
      throw e;
    }
    catch (final RuntimeException e) {
      s_aLogger.warn ("Got RuntimeException " + e.getMessage ());
      _notifyRegistrationHook (ESuccess.FAILURE);
      throw e;
    }
  }

  public void destroy () {
    // empty
  }
}
