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
package eu.europa.ec.cipa.smp.server.data;

import com.helger.commons.annotations.Nonempty;
import com.helger.commons.lang.GenericReflection;
import eu.europa.ec.cipa.smp.server.conversion.CaseSensitivityNormalizer;
import eu.europa.ec.cipa.smp.server.data.dbms.DBMSDataManager;
import eu.europa.ec.cipa.smp.server.util.ConfigFile;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import javax.annotation.Nonnull;
import javax.annotation.concurrent.Immutable;

/**
 * Factory for creating new DataManagers. This implementation retrieves the name
 * of the data manager from a configuration file.
 *
 * @author PEPPOL.AT, BRZ, Philip Helger
 */
@Immutable
@Deprecated
public final class DataManagerFactory {
  private static final Logger s_aLogger = LoggerFactory.getLogger (DataManagerFactory.class);
  private static final String CONFIG_DATA_MANAGER_CLASS = "dataManager.class";
  private static final String s_sDataManagerClassName;
  private static final IDataManager s_aInstance;

  static {
       /* TODO : This is a quick and dirty hack to allow the use of a configuration file with an other name if it's
        in the classpath (like smp.config.properties or sml.config.properties).
        If the configuration file defined in applicationContext.xml couldn't be found, then the config.properties inside the war is used as a fallback.
        Needs to be properly refactored */
      ConfigFile configFile = null;
      ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext(new String[]{"classpath:applicationContext.xml"});
      configFile = (ConfigFile) context.getBean("configFile");
    s_sDataManagerClassName = configFile.getString (CONFIG_DATA_MANAGER_CLASS);
    final IDataManager ret = GenericReflection.newInstance (s_sDataManagerClassName, IDataManager.class);
    if (ret == null)
      throw new IllegalStateException ("Failed to instantiate IDataManager class '" + s_sDataManagerClassName + "'");
    s_aLogger.info ("IDataManager class '" + s_sDataManagerClassName + "' instantiated");

    if(ret instanceof DBMSDataManager){
      CaseSensitivityNormalizer caseSensitivityNormalizer = context.getBean(CaseSensitivityNormalizer.class);
      ((DBMSDataManager)ret).setCaseSensitivityNormalizer(caseSensitivityNormalizer);
    }

    s_aInstance = ret;
  }

  private DataManagerFactory () {}

  /**
   * @return The name of the class used to instantiate the data manager. Neither
   *         <code>null</code> nor empty.
   */
  @Nonnull
  @Nonempty
  public static String getDataManagerClassName () {
    return s_sDataManagerClassName;
  }

  /**
   * @return The same instance over and over again. Never <code>null</code>.
   */
  @Nonnull
  public static IDataManager getInstance () {
    return s_aInstance;
  }
}
