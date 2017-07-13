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

import com.helger.commons.random.VerySecureRandom;
import com.helger.commons.state.ESuccess;
import eu.europa.ec.bdmsl.ws.soap.IManageParticipantIdentifierWS;
import eu.europa.ec.bdmsl.ws.soap.ManageBusinessIdentifierService;
import eu.europa.ec.bdmsl.ws.soap.NotFoundFault;
import eu.europa.ec.bdmsl.ws.soap.UnauthorizedFault;

import eu.europa.ec.cipa.smp.server.security.DoNothingTrustManager;
import eu.europa.ec.cipa.smp.server.security.HostnameVerifierAlwaysTrue;
import eu.europa.ec.cipa.smp.server.util.ConfigFile;
import eu.europa.ec.cipa.smp.server.security.KeyStoreUtils;
import org.busdox.servicemetadata.locator._1.ServiceMetadataPublisherServiceForParticipantType;
import org.oasis_open.docs.bdxr.ns.smp._2016._05.ParticipantIdentifierType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Conditional;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.stereotype.Component;
import sun.security.krb5.Config;

import javax.annotation.concurrent.NotThreadSafe;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.xml.ws.BindingProvider;
import javax.xml.ws.handler.MessageContext;
import java.net.MalformedURLException;
import java.net.URL;
import java.security.KeyStore;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * An implementation of the RegistrationHook that informs the SML of updates to
 * this SMP's identifiers.<br>
 * The design of this hook is very bogus! It relies on the postUpdate always
 * being called in order in this Thread.
 *
 * @author PEPPOL.AT, BRZ, Philip Helger
 */
@NotThreadSafe
@Component
@Conditional(SMLHookConditionOn.class)
public final class RegistrationServiceRegistrationHook extends AbstractRegistrationHook {

  private static final String CLIENT_CERT_HEADER_KEY = "Client-Cert";
  private static final String CONFIG_HOOK_REG_LOCATOR_URL = "regServiceRegistrationHook.regLocatorUrl";
  private static final String CONFIG_HOOK_ID = "regServiceRegistrationHook.id";
  private static final String CONFIG_HOOK_KEYSTORE_CLASSPATH = "regServiceRegistrationHook.keystore.classpath";
  private static final String CONFIG_HOOK_KEYSTORE_PASSWORD = "regServiceRegistrationHook.keystore.password";
  private static final String CONFIG_HOOK_CLIENT_CERT = "regServiceRegistrationHook.clientCert";

  private static final Logger s_aLogger = LoggerFactory.getLogger (RegistrationServiceRegistrationHook.class);
  private URL s_aSMLEndpointURL;
  private String s_sSMPID;
  private String s_sSMPClientCertificate;

  private ConfigFile configFile;

  @Autowired
  public void setConfigFile(ConfigFile configFile){
    /* TODO : This is a quick and dirty hack to allow the use of a configuration file with an other name if it's
        in the classpath (like smp.config.properties or sml.config.properties).
        If the configuration file defined in applicationContext.xml couldn't be found, then the config.properties inside the war is used as a fallback.
        Needs to be properly refactored
    ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext(new String[] {"classpath:applicationContext.xml"});
    configFile = (ConfigFile) context.getBean("configFile");
    */

    // SML endpoint (incl. the service name)
    final String sURL = configFile.getString (CONFIG_HOOK_REG_LOCATOR_URL);
    try {
      s_aSMLEndpointURL = new URL (sURL);
    }
    catch (final MalformedURLException ex) {
      throw new IllegalStateException ("Failed to init SML endpoint URL from '" + sURL + "'", ex);
    }

    // SMP ID
    s_sSMPID = configFile.getString (CONFIG_HOOK_ID);
    s_sSMPClientCertificate = configFile.getString(CONFIG_HOOK_CLIENT_CERT);

    s_aLogger.info ("Using the following SML address: " + s_aSMLEndpointURL);
    s_aLogger.info ("This SMP has the ID: " + s_sSMPID);
  }

  private enum EAction {
    CREATE,
    DELETE
  }

  //TODO: Get rid of these two stinky and not thread-safe guys (even if there is a ThreadLocal queue-something implemented, this is super stinky) Will cause problems after we switch to Spring:
  private ParticipantIdentifierType m_aBusinessIdentifier;
  private EAction m_eAction;

  public RegistrationServiceRegistrationHook () {
    resetQueue ();
  }


  protected IManageParticipantIdentifierWS getSmlCaller () {
    Map<String, List<String>> customHeaders = new HashMap<>();
    customHeaders.put(CLIENT_CERT_HEADER_KEY, Arrays.asList(s_sSMPClientCertificate));

    final ManageBusinessIdentifierService aService = new ManageBusinessIdentifierService((URL) null);
    final IManageParticipantIdentifierWS aPort = aService.getManageBusinessIdentifierServicePort ();
    Map<String, Object> requestContext = ((BindingProvider) aPort).getRequestContext();
    requestContext.put(BindingProvider.ENDPOINT_ADDRESS_PROPERTY, s_aSMLEndpointURL.toString());
    requestContext.put(MessageContext.HTTP_REQUEST_HEADERS, customHeaders);
    return aPort;
  }

  private void _setupSSLSocketFactory () {
    // Keystore for SML access:
    try {
      final String sKeystorePath = configFile.getString (CONFIG_HOOK_KEYSTORE_CLASSPATH);
      final String sKeystorePassword = configFile.getString (CONFIG_HOOK_KEYSTORE_PASSWORD);
      final String sRegLocatorUrl = configFile.getString (CONFIG_HOOK_REG_LOCATOR_URL);

      // Main key storage
      final KeyStore aKeyStore = KeyStoreUtils.loadKeyStore (sKeystorePath, sKeystorePassword);

      // Key manager
      final KeyManagerFactory aKeyManagerFactory = KeyManagerFactory.getInstance ("SunX509");
      aKeyManagerFactory.init (aKeyStore, sKeystorePassword.toCharArray ());

      // Trust manager

      // Assign key manager and empty trust manager to SSL context
      final SSLContext aSSLCtx = SSLContext.getInstance ("TLS");
      aSSLCtx.init (aKeyManagerFactory.getKeyManagers (),
                    new TrustManager [] { new DoNothingTrustManager() },
                    VerySecureRandom.getInstance ());
      HttpsURLConnection.setDefaultSSLSocketFactory (aSSLCtx.getSocketFactory ());
      if (sRegLocatorUrl.contains ("localhost")) {
        HttpsURLConnection.setDefaultHostnameVerifier (new HostnameVerifierAlwaysTrue());
      }
    }
    catch (final Exception ex) {
      throw new IllegalStateException ("Failed to init keyStore for SML access", ex);
    }
  }

  public void create (final ParticipantIdentifierType participantId) throws HookException {
    m_aBusinessIdentifier = participantId; // TODO: Oh my God, this is not thread safe !!! (Will not be when we migrate to Spring)
    m_eAction = EAction.CREATE; //TODO: and this as well !!!

    s_aLogger.info ("Trying to create business " + participantId + " in Business Identifier Manager Service");

    try {
      _setupSSLSocketFactory ();
      final IManageParticipantIdentifierWS aSMLCaller = getSmlCaller();
      aSMLCaller.create (toBusdoxParticipantId(participantId));
      s_aLogger.info ("Succeeded in creating business " +toString(participantId) + " using Business Identifier Manager Service");
      getQueueInstance ().set (this);
    }
    catch (final UnauthorizedFault ex) {
      final String sMsg = "Seems like this SMP is not registered to the SML, or you're providing invalid credentials!";
      s_aLogger.warn (sMsg);
      throw new HookException (sMsg, ex);
    }
    catch (final Throwable t) {
      final String sMsg = "Could not create business " +toString(participantId) + " in Business Identifier Manager Service";
      s_aLogger.warn (sMsg, t);
      throw new HookException (sMsg, t);
    }
  }

  private ServiceMetadataPublisherServiceForParticipantType toBusdoxParticipantId(ParticipantIdentifierType aPI) {
    ServiceMetadataPublisherServiceForParticipantType busdoxIdentifier = new ServiceMetadataPublisherServiceForParticipantType();
    busdoxIdentifier.setServiceMetadataPublisherID(s_sSMPID);
    org.busdox.transport.identifiers._1.ParticipantIdentifierType parId = new org.busdox.transport.identifiers._1.ParticipantIdentifierType();
    parId.setScheme(aPI.getScheme());
    parId.setValue(aPI.getValue());
    busdoxIdentifier.setParticipantIdentifier(parId);
    return busdoxIdentifier;
  }

  public void delete (final ParticipantIdentifierType participantId) throws HookException {
    m_aBusinessIdentifier = participantId; // TODO: Oh my God, this is not thread safe !!! (Will not be when we migrate to Spring)
    m_eAction = EAction.DELETE; //TODO: and this as well !!!

    s_aLogger.info ("Trying to delete business " + toString(participantId) + " in Business Identifier Manager Service");

    try {
      _setupSSLSocketFactory ();
      final IManageParticipantIdentifierWS aSMLCaller = getSmlCaller();
      aSMLCaller.delete (toBusdoxParticipantId(participantId));
      s_aLogger.info ("Succeded in deleting business " + toString(participantId) + " using Business Identifier Manager Service");
      getQueueInstance ().set (this);
    }
    catch (final NotFoundFault e) {
      s_aLogger.warn ("The business " + toString(participantId) + " was not present in the SML. Just ignore.");
    }
    catch (final Throwable t) {
      final String sMsg = "Could not delete business " + toString(participantId) + " in Business Identifier Manager Service";
      s_aLogger.warn (sMsg, t);
      throw new HookException (sMsg, t);
    }
  }

  public void postUpdate (final ESuccess eSuccess) throws HookException {
    if (eSuccess.isFailure ())
      try {
        _setupSSLSocketFactory ();
        final IManageParticipantIdentifierWS aSMLCaller = getSmlCaller();

        switch (m_eAction) {
          case CREATE:
            // Undo create
            s_aLogger.warn ("CREATE failed in database, so deleting " + toString(m_aBusinessIdentifier) + " from SML.");
            aSMLCaller.delete (toBusdoxParticipantId(m_aBusinessIdentifier));
            break;
          case DELETE:
            // Undo delete
            s_aLogger.warn ("DELETE failed in database, so creating " +
                    m_aBusinessIdentifier.getScheme() + toString(m_aBusinessIdentifier) + " in SML.");
            aSMLCaller.create (toBusdoxParticipantId(m_aBusinessIdentifier));
            break;
        }
      }
      catch (final Throwable t) {
        throw new HookException ("Unable to rollback update business " + toString(m_aBusinessIdentifier), t);
      }
  }

  private String toString(ParticipantIdentifierType id){
    return id.getScheme()+"::"+id.getValue();
  }

}
