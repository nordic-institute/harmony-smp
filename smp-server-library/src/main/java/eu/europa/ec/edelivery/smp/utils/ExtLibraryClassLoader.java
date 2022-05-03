package eu.europa.ec.edelivery.smp.utils;

import com.google.common.collect.Lists;
import eu.europa.ec.edelivery.smp.logging.SMPLogger;
import eu.europa.ec.edelivery.smp.logging.SMPLoggerFactory;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author Cosmin Baciu
 * @author Joze Rihtarsic
 * @since 4.2
 *
 * ExtLibraryClassLoader extends URLClassLoader for loading the SMP's SPI extensions.
 * The class loader implementation is heavily inspired by the Domibus PluginClassLoader.
 */
public class ExtLibraryClassLoader extends URLClassLoader {

    private static final SMPLogger LOG = SMPLoggerFactory.getLogger(ExtLibraryClassLoader.class);

    protected File libraryFolder;

    public ExtLibraryClassLoader(File libraryFolder, ClassLoader parent) throws MalformedURLException {
        super(discoverLibraries(libraryFolder), parent);
        this.libraryFolder = libraryFolder;
    }

    /**
     * discovery libraries in the folder to extract the jar files url.
     *
     * @param libraryDirectory with the SMP library extensions.
     * @return the urls of the jar files.
     * @throws MalformedURLException
     */
    protected static URL[] discoverLibraries(File libraryDirectory) throws MalformedURLException {

        final List<URI> jarUris = Arrays.asList(
                libraryDirectory.listFiles((dir, name) -> name.toLowerCase().endsWith(".jar")))
                .stream()
                .map(File::toURI)
                .collect(Collectors.toList());

        final URL[] urls = new URL[jarUris.size()];
        for (int i = 0; i < jarUris.size(); i++) {
            urls[i] = jarUris.get(i).toURL();
            LOG.info("Adding the following library to the classpath:[{}] ", urls[i]);
        }
        return urls;
    }

    public File getLibraryFolder() {
        return libraryFolder;
    }
}
