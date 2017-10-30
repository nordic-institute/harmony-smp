package eu.europa.ec.cipa.smp.server.util;

import java.util.List;
import java.util.ListIterator;

public class CommonUtil {

    //TODO UNIT TESTS
    public static void toLowerCaseStringList(List<String> strings) {
        ListIterator<String> iterator = strings.listIterator();
        while (iterator.hasNext()) {
            iterator.set(iterator.next().toLowerCase().trim());
        }
    }
}
