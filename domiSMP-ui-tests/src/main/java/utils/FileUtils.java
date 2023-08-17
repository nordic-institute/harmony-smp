package utils;

import java.io.File;

public class FileUtils {
    public static String getAbsolutePath(String relativePath) {
        return new File(relativePath).getAbsolutePath();
    }

}
