package eu.europa.ec.edelivery.smp.config;

import eu.europa.ec.edelivery.smp.config.enums.SMPEnvPropertyEnum;
import eu.europa.ec.edelivery.smp.logging.SMPLoggerFactory;
import eu.europa.ec.edelivery.smp.utils.ExtLibraryClassLoader;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.slf4j.Logger;

import javax.servlet.ServletContext;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.util.Properties;

import static eu.europa.ec.edelivery.smp.config.enums.SMPEnvPropertyEnum.SMP_MODE_DEVELOPMENT;

/**
 * WebApplicationInitializer implementation class loads the external libraries and configures the logging
 *
 * @author Joze Rihtarsic
 * @since 4.2
 */
public class SMPWebApplicationInitializer implements org.springframework.web.WebApplicationInitializer {

    private static final String FILE_APPLICATION_PROPERTIES = "/application.properties";

    protected static final String PROP_BUILD_NAME = "smp.artifact.name";
    protected static final String PROP_BUILD_VERSION = "smp.artifact.version";
    protected static final String PROP_BUILD_TIME = "smp.artifact.build.time";
    protected static final String VERSION_LOG_TEMPLATE = "Start application: name: [{}], version: [{}], build time: [{}].";

    private static final Logger LOG = SMPLoggerFactory.getLogger(SMPWebApplicationInitializer.class);

    @Override
    public void onStartup(ServletContext servletContext) {
        SMPEnvironmentProperties smpEnvironmentProperties = SMPEnvironmentProperties.getInstance();
        // print out the  application data
        logBuildProperties(LOG, FILE_APPLICATION_PROPERTIES);
        // get environment properties
        boolean devMode = Boolean.parseBoolean(smpEnvironmentProperties.getEnvPropertyValue(SMP_MODE_DEVELOPMENT));
        if (devMode) {
            LOG.warn("***********************************************************************");
            LOG.warn("WARNING: The SMP is started in DEVELOPMENT mode!");
            LOG.warn("***********************************************************************");
        }

        Properties fileProperties = smpEnvironmentProperties.getEnvProperties();
        // load external libraries
        String libraryFolderPath = StringUtils.trim(fileProperties.getProperty(SMPEnvPropertyEnum.LIBRARY_FOLDER.getProperty(), null));
        if (StringUtils.isNotBlank(libraryFolderPath)) {
            File fLibraryDir = new File(libraryFolderPath);
            ExtLibraryClassLoader pluginClassLoader = createLibraryClassLoader(fLibraryDir);
            if (pluginClassLoader != null) {
                LOG.debug("Add libraries from the folder: [{}]!", libraryFolderPath);
                Thread.currentThread().setContextClassLoader(pluginClassLoader);
            }
        } else {
            LOG.info("Library folder is not set!  No libraries are loaded!");
        }
    }

    /**
     * Method reads internal 'application.properties' file and prints the build version to the logs
     */
    protected void logBuildProperties(Logger log, String versionResource) {
        InputStream is = SMPWebApplicationInitializer.class.getResourceAsStream(versionResource);
        if (is != null) {
            Properties applProp = new Properties();
            try {
                applProp.load(is);
                log.info("*****************************************************************************************");
                log.info(VERSION_LOG_TEMPLATE, applProp.getProperty(PROP_BUILD_NAME)
                        , applProp.getProperty(PROP_BUILD_VERSION)
                        , applProp.getProperty(PROP_BUILD_TIME));
                log.info("*****************************************************************************************");
            } catch (IOException e) {
                log.error("Error occurred  while reading application properties. Is file " + FILE_APPLICATION_PROPERTIES + " included in war!", e);
            }
        } else {
            log.error("Not found application build properties: [{}]!", FILE_APPLICATION_PROPERTIES);
        }
    }

    /**
     * Method creates the ClassLoader for the external libraries places into the smpLibrary folder.
     *
     * @param smpLibraryFolder is the SMP library folder
     * @return
     */
    protected ExtLibraryClassLoader createLibraryClassLoader(File smpLibraryFolder) {
        LOG.info("Load libraries from location [{}]", smpLibraryFolder);
        if (!smpLibraryFolder.exists()) {
            LOG.warn("Library folder [{}] does not exist! No libraries are loaded!", smpLibraryFolder.getAbsolutePath());
            return null;
        }
        if (!smpLibraryFolder.isDirectory()) {
            LOG.warn("Library folder [{}] is not a folder! No libraries are loaded!", smpLibraryFolder.getAbsolutePath());
            return null;
        }

        ExtLibraryClassLoader pluginClassLoader = null;
        try {
            pluginClassLoader = new ExtLibraryClassLoader(smpLibraryFolder, Thread.currentThread().getContextClassLoader());
        } catch (MalformedURLException e) {
            LOG.error("MalformedURLException occurred while loading the external libraries from folder [{}] with error cause [{}]! No libraries are loaded!",
                    smpLibraryFolder.getAbsolutePath(), ExceptionUtils.getRootCauseMessage(e));
        }
        return pluginClassLoader;
    }
}
