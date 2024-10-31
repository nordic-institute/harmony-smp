package utils;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;

public class FileUtils {

    private static final Path keystoreFolder = Paths.get("src", "main", "resources", "keystore");
    private static final Path trustStoreFolder = Paths.get("src", "main", "resources", "truststore");


    public static String getAbsolutePath(String relativePath) {
        return new File(relativePath).getAbsolutePath();
    }

    /**
     * Returns the absolute path of the keystore file in the keystore folder
     *
     * @param keystoreFileName the name of the keystore file
     * @return the absolute path of the keystore file
     */
    public static String getAbsoluteKeystorePath(String keystoreFileName) {
        return keystoreFolder.resolve(keystoreFileName).toAbsolutePath().toString();
    }

    public static String getAbsoluteTruststorePath(String truststoreFileName) {
        return trustStoreFolder.resolve(truststoreFileName).toAbsolutePath().toString();
    }

}
