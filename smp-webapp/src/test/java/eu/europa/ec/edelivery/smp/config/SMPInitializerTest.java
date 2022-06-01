package eu.europa.ec.edelivery.smp.config;

import eu.europa.ec.edelivery.smp.utils.ExtLibraryClassLoader;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import org.slf4j.Logger;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;

import static org.junit.Assert.*;

public class SMPInitializerTest {

    SMPInitializer testInstance = new SMPInitializer();
    @Test
    public void onStartup() {
    }

    @Test
    public void logBuildProperties() {
        Logger log = Mockito.mock(Logger.class);
        testInstance.logBuildProperties(log, "/test-application.properties");

        ArgumentCaptor<String> template = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<String> name = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<String> version = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<String> date = ArgumentCaptor.forClass(String.class);

        Mockito.verify(log, Mockito.times(2)).info(Mockito.anyString());
        Mockito.verify(log, Mockito.times(1)).info(template.capture(),
                name.capture(),
                version.capture(),
                date.capture() );

        assertEquals(SMPInitializer.VERSION_LOG_TEMPLATE, template.getValue());
        assertEquals("eDelivery test SMP", name.getValue());
        assertEquals("1.0", version.getValue());
        assertEquals("2022-05-05T12:00:00Z", date.getValue());
    }

    @Test
    public void createLibraryClassLoaderNotExists() {
        ExtLibraryClassLoader loader= testInstance.createLibraryClassLoader(new File("FileNotExists"));
        assertNull(loader);
    }

    @Test
    public void createLibraryClassLoaderIsNotFolder() {
        ExtLibraryClassLoader loader= testInstance.createLibraryClassLoader(new File("./pom.xml"));
        assertNull(loader);
    }

    @Test
    public void createLibraryClassLoader() {
        // folder contains one library jar simple-extension.jar with the resource logback-test.xml
        Path path = Paths.get("src","test","resources", "test-libs");
        ExtLibraryClassLoader loader= testInstance.createLibraryClassLoader(path.toFile());
        assertNotNull(loader);
        assertNotNull(loader.getResource("logback-test.xml"));
    }
}