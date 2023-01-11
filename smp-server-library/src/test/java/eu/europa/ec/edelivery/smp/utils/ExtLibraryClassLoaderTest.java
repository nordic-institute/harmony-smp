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