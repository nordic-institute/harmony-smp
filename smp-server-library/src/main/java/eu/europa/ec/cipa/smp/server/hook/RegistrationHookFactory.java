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

import com.helger.commons.lang.GenericReflection;
import eu.europa.ec.cipa.smp.server.util.ConfigFile;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import javax.annotation.Nonnull;
import javax.annotation.concurrent.Immutable;

/**
 * @author PEPPOL.AT, BRZ, Philip Helger
 */
@Immutable
public final class RegistrationHookFactory {
  private static final Logger s_aLogger = LoggerFactory.getLogger (RegistrationHookFactory.class);
  private static final String CONFIG_REGISTRATION_HOOK_CLASS = "registrationHook.class";

  private static ConfigFile configFile;

  static {
      /* TODO : This is a quick and dirty hack to allow the use of a configuration file with an other name if it's
        in the classpath (like smp.config.properties or sml.config.properties).
        If the configuration file defined in applicationContext.xml couldn't be found, then the config.properties inside the war is used as a fallback.
        Needs to be properly refactored */
    ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext(new String[]{"classpath:applicationContext.xml"});
    configFile = (ConfigFile) context.getBean("configFile");
  }

  private RegistrationHookFactory () {}

  /**
   * Create a new instance every time this method is invoked.
   *
   * @return A new instance of {@link IRegistrationHook} according to the
   *         configuration file.
   * @throws IllegalStateException
   *         If the class could not be instantiated
   */
  @Nonnull
  public static IRegistrationHook createInstance () {
    final String sRegHookName = configFile.getString (CONFIG_REGISTRATION_HOOK_CLASS);
    final IRegistrationHook aHook = GenericReflection.newInstance (sRegHookName, IRegistrationHook.class);
    if (aHook == null)
      throw new IllegalStateException ("Failed to create registration hook instance from class '" + sRegHookName + "'");
    s_aLogger.info ("Registration hook class '" + sRegHookName + "' instantiated");
    return aHook;
  }
}
