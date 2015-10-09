package eu.domibus.security.handlers;

import eu.domibus.common.util.JNDIUtil;
import org.apache.log4j.Logger;
import org.apache.ws.security.WSPasswordCallback;

import javax.security.auth.callback.Callback;
import javax.security.auth.callback.CallbackHandler;
import javax.security.auth.callback.UnsupportedCallbackException;
import java.io.IOException;

import static eu.domibus.security.module.Constants.SECURITY_PRIVATEKEY_PASSWORD_PARAMETER;

public class PWCBHandler implements CallbackHandler {
    private static final Logger LOG = Logger.getLogger(PWCBHandler.class);

    public void handle(final Callback[] callbacks) throws IOException, UnsupportedCallbackException {

        final WSPasswordCallback pwcb = (WSPasswordCallback) callbacks[0];
        final String id = pwcb.getIdentifier();
        PWCBHandler.LOG.trace("Returning password defined in domibus.xml " + SECURITY_PRIVATEKEY_PASSWORD_PARAMETER +
                              " for alias:" +
                              id);

        pwcb.setPassword(JNDIUtil.getStringEnvironmentParameter(SECURITY_PRIVATEKEY_PASSWORD_PARAMETER));
    }
}