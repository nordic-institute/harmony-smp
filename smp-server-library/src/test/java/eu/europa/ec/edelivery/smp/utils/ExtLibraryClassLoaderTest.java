/*-
 * #START_LICENSE#
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
 * #END_LICENSE#
 */
package eu.europa.ec.edelivery.smp.utils;

import org.junit.Test;

import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;

import static org.junit.Assert.*;

public class ExtLibraryClassLoaderTest {

    @Test
    public void discoverLibraries() throws MalformedURLException {
        Path path = Paths.get("src", "test","resources","test-libs");
        URL[] result = ExtLibraryClassLoader.discoverLibraries(path.toFile());
        assertNotNull(result);
        assertEquals(2, result.length);
    }

    @Test
    public void getLibraryFolder() throws MalformedURLException {
        Path path = Paths.get("src", "test","resources","test-libs");
        ExtLibraryClassLoader  loader = new ExtLibraryClassLoader(path.toFile(), ClassLoader.getSystemClassLoader());
        URL url = loader.getResource("test-load-library.xml");
        URL urlNotExists = loader.getResource("test-load-library-notExists.xml");
        assertEquals(path.toFile(), loader.getLibraryFolder());
        assertNotNull(url);
        assertNull(urlNotExists);
    }
}
