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
package eu.europa.ec.cipa.smp.server.util;

import com.helger.commons.ValueEnforcer;
import com.helger.commons.annotations.Nonempty;
import com.helger.commons.annotations.ReturnsMutableCopy;
import com.helger.commons.collections.CollectionHelper;
import com.helger.commons.io.resource.ClassPathResource;
import com.helger.commons.io.resource.FileSystemResource;
import com.helger.commons.io.streams.StreamUtils;
import com.helger.commons.state.ESuccess;
import com.helger.commons.string.StringHelper;
import com.helger.commons.string.StringParser;
import com.helger.commons.string.ToStringGenerator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.concurrent.Immutable;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;

/**
 * Used for accessing configuration files based on properties. By default first
 * the private (not checked-in) version of the config file called
 * <code>private-config.properties</code> is accessed. If no such file is
 * present, the default config file (which is also in the SCM) is accessed by
 * the name<code>config.properties</code>.<br>
 * Additionally you can create a new instance with a custom file path.
 *
 * @author PEPPOL.AT, BRZ, Philip Helger
 */
@Immutable
@Deprecated
public class ConfigFile {

  public static final String LIST_SEPARATOR = "\\|";
  public static final String EMPTY_STRING = "";

  private static final class SingletonHolder {
    static final ConfigFile s_aInstance = new ConfigFile ();
  }

  /** Default file name for the private config file */
  public static final String DEFAULT_PRIVATE_CONFIG_PROPERTIES = "private-config.properties";

  /** Default file name for the regular config file */
  public static final String DEFAULT_CONFIG_PROPERTIES = "config.properties";

  private static final Logger s_aLogger = LoggerFactory.getLogger (ConfigFile.class);

  private final boolean m_bRead;
  private final Properties m_aProps = new Properties ();

  /**
   * Default constructor for the default file paths (private-config.properties
   * and afterwards config.properties)
   */
  private ConfigFile() {
    this (DEFAULT_PRIVATE_CONFIG_PROPERTIES, DEFAULT_CONFIG_PROPERTIES);
  }

  /**
   * Constructor for explicitly specifying a file path to read.
   *
   * @param aConfigPaths
   *        The array of paths to the config files to be read. Must be
   *        classpath-relative. The first file that could be read will be taken
   */
  public ConfigFile(@Nonnull @Nonempty final String... aConfigPaths) {
    ValueEnforcer.notEmptyNoNullValue (aConfigPaths, "ConfigPaths");

    boolean bRead = false;
    for (final String sConfigPath : aConfigPaths)
      if (_readConfigFile (sConfigPath).isSuccess ()) {
        bRead = true;
        break;
      }

    if (!bRead) {
      // No config file found at all
      s_aLogger.warn ("Failed to resolve config file paths: " + CollectionHelper.newList (aConfigPaths).toString ());
    }
    m_bRead = bRead;
  }

  public ConfigFile(final String configFilePath) {
    this (configFilePath, DEFAULT_PRIVATE_CONFIG_PROPERTIES, DEFAULT_CONFIG_PROPERTIES);
  }

  @Nonnull
  private ESuccess _readConfigFile (@Nonnull final String sPath) {
    // Try to get the input stream for the passed property file name
    InputStream aIS = ClassPathResource.getInputStream (sPath);
    if (aIS == null) {
      // Fallback to file system - maybe this helps...
      aIS = new FileSystemResource (sPath).getInputStream ();
    }
    if (aIS != null) {
      try {
        // Does not close the input stream!
        m_aProps.load (aIS);
        if (s_aLogger.isDebugEnabled ())
          s_aLogger.debug ("Loaded configuration from '" + sPath + "': " + Collections.list (m_aProps.keys ()));
        return ESuccess.SUCCESS;
      }
      catch (final IOException ex) {
        s_aLogger.error ("Failed to read config file '" + sPath + "'", ex);
      }
      finally {
        // Manually close the input stream!
        StreamUtils.close (aIS);
      }
    }
    return ESuccess.FAILURE;
  }

  /**
   * @return The default configuration file denoted by the file names
   *         {@value #DEFAULT_PRIVATE_CONFIG_PROPERTIES} and
   *         {@value #DEFAULT_CONFIG_PROPERTIES}.
   */
  @Nonnull
  public static ConfigFile getInstance () {
    return SingletonHolder.s_aInstance;
  }

  /**
   * @return <code>true</code> if reading succeeded, <code>false</code> if
   *         reading failed (warning was already logged)
   */
  public boolean isRead () {
    return m_bRead;
  }

  /**
   * Get the string from the configuration files
   *
   * @param sKey
   *        The key to search
   * @return <code>null</code> if no such value is in the configuration file.
   */
  @Nullable
  public final String getString (@Nonnull final String sKey) {
    return getString (sKey, null);
  }

  /**
   * Get the string from the configuration files
   *
   * @param sKey
   *        The key to search
   * @param sDefault
   *        The default value to be returned if the value was not found. May be
   *        <code>null</code>.
   * @return the passed default value if no such value is in the configuration
   *         file.
   */
  @Nullable
  public final String getString (@Nonnull final String sKey, @Nullable final String sDefault) {
    final String sValue = m_aProps.getProperty (sKey);
    return sValue != null ? StringHelper.trim (sValue) : sDefault;
  }

  @Nullable
  public final char [] getCharArray (@Nonnull final String sKey) {
    return getCharArray (sKey, null);
  }

  @Nullable
  public final char [] getCharArray (@Nonnull final String sKey, final char [] aDefault) {
    final String ret = getString (sKey, null);
    return ret == null ? aDefault : ret.toCharArray ();
  }

  public final boolean getBoolean (@Nonnull final String sKey, final boolean bDefault) {
    return StringParser.parseBool (getString (sKey), bDefault);
  }

  public final int getInt (@Nonnull final String sKey, final int nDefault) {
    return StringParser.parseInt (getString (sKey), nDefault);
  }

  public List<String> getStringList(@Nonnull final String sKey) {
    List<String> stringsList = Arrays.asList(getString(sKey, EMPTY_STRING).split(LIST_SEPARATOR));
    if (stringsList.size() == 1 && stringsList.contains(EMPTY_STRING)) {
      return Collections.emptyList(); //configured empty value
    } else {
      return stringsList;
    }
  }

  /**
   * @return A {@link Set} with all keys contained in the configuration file
   */
  @Nonnull
  @ReturnsMutableCopy
  public final Set <String> getAllKeys () {
    // Convert from Set<Object> to Set<String>
    final Set <String> ret = new HashSet <String> ();
    for (final Object o : m_aProps.keySet ())
      ret.add ((String) o);
    return ret;
  }

  @Override
  public String toString () {
    return new ToStringGenerator (this).append ("read", m_bRead).append ("props", m_aProps).toString ();
  }
}
