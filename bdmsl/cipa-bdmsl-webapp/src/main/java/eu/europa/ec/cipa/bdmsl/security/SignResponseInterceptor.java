package eu.europa.ec.cipa.bdmsl.security;

import org.apache.cxf.binding.soap.SoapMessage;
import org.apache.cxf.interceptor.Fault;
import org.apache.cxf.ws.security.wss4j.WSS4JOutInterceptor;
import org.apache.wss4j.common.ext.WSPasswordCallback;
import org.apache.wss4j.dom.handler.WSHandlerConstants;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.security.auth.callback.Callback;
import javax.security.auth.callback.CallbackHandler;
import javax.security.auth.callback.UnsupportedCallbackException;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

/**
 * Created by feriaad on 10/07/2015.
 */
@Component(value = "signResponseInterceptor")
public class SignResponseInterceptor extends WSS4JOutInterceptor {

    @Value("${keystoreAlias}")
    private String keystoreAlias;

    @Value("${keystoreFileName}")
    private String keystoreFileName;

    @Value("${keystorePassword}")
    private String keystorePassword;

    @Value("${signResponse}")
    private String signResponse;

    @Value("${configurationDir}")
    private String configurationDir;

    @PostConstruct
    public void init() {

        if (!configurationDir.endsWith("/")) {
            configurationDir += "/";
        }

        Map<String, Object> props = new HashMap<>();
            props.put(WSHandlerConstants.ACTION, "Signature");
            props.put(WSHandlerConstants.SIGNATURE_PARTS, "{Element}{http://schemas.xmlsoap.org/soap/envelope/}Body");
            props.put(WSHandlerConstants.SIGNATURE_USER, keystoreAlias);
            Properties sigProps = new Properties();
            sigProps.put("org.apache.ws.security.crypto.provider", "org.apache.ws.security.components.crypto.Merlin");
            sigProps.put("org.apache.ws.security.crypto.merlin.keystore.provider", "");
            sigProps.put("org.apache.ws.security.crypto.merlin.keystore.type", "jks");
            sigProps.put("org.apache.ws.security.crypto.merlin.keystore.password", keystorePassword);
            sigProps.put("org.apache.ws.security.crypto.merlin.keystore.alias", keystoreAlias);
            sigProps.put("org.apache.ws.security.crypto.merlin.file", configurationDir + keystoreFileName);

            props.put(WSHandlerConstants.SIG_PROP_REF_ID, "sigProps");
            props.put(WSHandlerConstants.MUST_UNDERSTAND, "false");
            props.put("sigProps", sigProps);
            props.put(WSHandlerConstants.PW_CALLBACK_REF, new CallbackHandler() {
                @Override
                public void handle(Callback[] callbacks) throws IOException, UnsupportedCallbackException {
                    WSPasswordCallback pc = (WSPasswordCallback) callbacks[0];
                    pc.setPassword(keystorePassword);
                }
            });

        setProperties(props);
    }

    @Override
    public void handleMessage(SoapMessage mc) throws Fault {
        if (Boolean.parseBoolean(signResponse)) {
            super.handleMessage(mc);
        }
    }
}
