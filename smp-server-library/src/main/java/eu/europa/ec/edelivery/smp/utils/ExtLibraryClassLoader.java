/*-
 * #%L
 * smp-server-library
 * %%
 * Copyright (C) 2017 - 2023 European Commission | eDelivery | DomiSMP
 * %%
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by the European Commission - subsequent
 * versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy of the Licence at:
 * 
 * [PROJECT_HOME]\license\eupl-1.2\license.txt or https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 * 
 * Unless required by applicable law or agreed to in writing, software distributed under the Licence is
 * distributed on an "AS IS" basis, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the Licence for the specific language governing permissions and limitations under the Licence.
 * #L%
 */
package eu.europa.ec.edelivery.smp.utils;

import eu.europa.ec.edelivery.smp.logging.SMPLogger;
import eu.europa.ec.edelivery.smp.logging.SMPLoggerFactory;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author Cosmin Baciu
 * @author Joze Rihtarsic
 * @since 4.2
 * <p>
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
