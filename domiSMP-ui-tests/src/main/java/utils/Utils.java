package utils;

import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Utils {
    public static <T extends Enum<?>> T randomEnum(T[] values) {
        int x = new Random().nextInt(values.length);
        return values[x];
    }

    public static String getAliasFromMessage(String message){
        Pattern pattern = Pattern.compile("(?<= \\[)(.*?)(?=\\])");
        Matcher matcher = pattern.matcher(message);
        if (matcher.find()) {
            String result = matcher.group(1);
            return result;
        }
        return null;
    }
}
