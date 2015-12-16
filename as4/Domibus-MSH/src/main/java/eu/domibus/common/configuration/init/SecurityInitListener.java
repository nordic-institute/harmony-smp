package eu.domibus.common.configuration.init;

import org.apache.log4j.Logger;
import org.apache.xml.security.algorithms.JCEMapper;
import org.apache.xml.security.algorithms.SignatureAlgorithm;
import org.apache.xml.security.c14n.Canonicalizer;
import org.apache.xml.security.exceptions.XMLSecurityException;
import org.apache.xml.security.keys.keyresolver.KeyResolver;
import org.apache.xml.security.transforms.Transform;
import org.apache.xml.security.utils.ElementProxy;
import org.apache.xml.security.utils.I18n;
import org.apache.xml.security.utils.resolver.ResourceResolver;
import org.bouncycastle.jce.provider.BouncyCastleProvider;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import java.security.AccessController;
import java.security.PrivilegedActionException;
import java.security.PrivilegedExceptionAction;
import java.security.Security;

public class SecurityInitListener implements ServletContextListener {

    private static final Logger logger = Logger.getLogger(SecurityInitListener.class);

    private static boolean alreadyInitialized;

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        if (!alreadyInitialized) {
            logger.info("Initializing security...");

            Security.addProvider(new BouncyCastleProvider());
            I18n.init("en", "US");

            if (logger.isDebugEnabled()) {
                logger.debug("Registering default algorithms");
            }
            try {
                AccessController.doPrivileged(new PrivilegedExceptionAction<Void>() {
                    @Override
                    public Void run() throws XMLSecurityException {
                        ElementProxy.registerDefaultPrefixes();
                        Transform.registerDefaultAlgorithms();
                        SignatureAlgorithm.registerDefaultAlgorithms();
                        JCEMapper.registerDefaultAlgorithms();
                        Canonicalizer.registerDefaultAlgorithms();
                        ResourceResolver.registerDefaultResolvers();
                        KeyResolver.registerDefaultResolvers();
                        alreadyInitialized = true;
                        return null;
                    }
                });
            } catch (PrivilegedActionException ex) {
                XMLSecurityException xse = (XMLSecurityException) ex.getException();
                logger.error(xse.getMessage(), xse);
            }
        }
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        // TODO Auto-generated method stub

    }

}
