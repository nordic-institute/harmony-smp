package utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Utils {
    private final static Logger LOG = LoggerFactory.getLogger(Utils.class);

    public static <T extends Enum<?>> T randomEnum(T[] values) {
        int x = new Random().nextInt(values.length);
        return values[x];
    }

    public static <T extends Enum<T>> T[] getAllEnumValues(Class<T> enumClass) {
        return enumClass.getEnumConstants();
    }

    public static String getAliasFromMessage(String message){
        String regex = "\\[(.*?)]";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(message);
        if (matcher.find()) {
            return matcher.group(1);

        }
        LOG.error("No alias found in the message: "+message);
        throw new NullPointerException("No alias found in the message: "+message);
    }
}
