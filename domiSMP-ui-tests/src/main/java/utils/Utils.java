package utils;

import java.util.Random;

public class Utils {
    public static <T extends Enum<?>> T randomEnum(T[] values) {
        int x = new Random().nextInt(values.length);
        return values[x];
    }
}
