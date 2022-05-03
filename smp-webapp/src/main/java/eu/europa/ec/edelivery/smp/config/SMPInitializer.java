package eu.europa.ec.edelivery.smp.config;

import eu.europa.ec.edelivery.smp.auth.URLCsrfIgnoreMatcher;
import eu.europa.ec.edelivery.smp.exceptions.ErrorCode;
import eu.europa.ec.edelivery.smp.exceptions.SMPRuntimeException;
import eu.europa.ec.edelivery.smp.logging.SMPLoggerFactory;
import eu.europa.ec.edelivery.smp.utils.ExtLibraryClassLoader;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.apache.commons.text.StringSubstitutor;
import org.slf4j.Logger;
import org.springframework.web.WebApplicationInitializer;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.util.Properties;

import static eu.europa.ec.edelivery.smp.data.ui.enums.SMPPropertyEnum.CONFIGURATION_DIR;


public class SMPInitializer implements WebApplicationInitializer {

    private static final String FILE_APPLICATION_PROPERTIES = "/application.properties";

    private static final String PROP_BUILD_NAME = "smp.artifact.name";
    private static final String PROP_BUILD_VERSION = "smp.artifact.version";
    private static final String PROP_BUILD_TIME = "smp.artifact.build.time";


    private static final Logger LOG = SMPLoggerFactory.getLogger(
            SMPInitializer.class);
    @Override
    public void onStartup(ServletContext servletContext) throws ServletException {
        Properties fileProperties = FileProperty.getFileProperties();
        logBuildProperties();

        // load external libraries
        String libraryFolderPath = StringUtils.trim(fileProperties.getProperty(FileProperty.PROPERTY_LIB_FOLDER, null));
        if (StringUtils.isNotBlank(libraryFolderPath)){
            File fLibraryDir = new File(libraryFolderPath);
            ExtLibraryClassLoader pluginClassLoader = createLibraryClassLoader(fLibraryDir);
            if (pluginClassLoader!=null){
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
    protected void logBuildProperties() {
        InputStream is = PropertyInitialization.class.getResourceAsStream(FILE_APPLICATION_PROPERTIES);
        if (is != null) {
            Properties applProp = new Properties();
            try {
                applProp.load(is);
                LOG.info("*****************************************************************************************");
                LOG.info("Start application: name: {}, version: {}, build time: {}.", applProp.getProperty(PROP_BUILD_NAME)
                        , applProp.getProperty(PROP_BUILD_VERSION)
                        , applProp.getProperty(PROP_BUILD_TIME));
                LOG.info("*****************************************************************************************");
            } catch (IOException e) {
                LOG.error("Error occurred  while reading application properties. Is file " + FILE_APPLICATION_PROPERTIES + " included in war!", e);
            }
        } else {
            LOG.error("Not found application build properties: {}!", FILE_APPLICATION_PROPERTIES);
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
        if (!smpLibraryFolder.exists() ) {
            LOG.warn("Library folder [{}] does not exist! No libraries are loaded!", smpLibraryFolder.getAbsolutePath());
            return null;
        }
        if (!smpLibraryFolder.isDirectory() ) {
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
