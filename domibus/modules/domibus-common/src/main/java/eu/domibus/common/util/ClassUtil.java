package eu.domibus.common.util;

import org.apache.log4j.Logger;

import java.lang.reflect.Constructor;

public class ClassUtil {

    private static final Logger log = Logger.getLogger(ClassUtil.class);

    public static Object createInstance(final String className) {
        Object instance = null;
        try {
            final int dollarPos = className.indexOf("$");
            if (dollarPos < 0) {
                final Class instanceClass = Class.forName(className);
                instance = instanceClass.newInstance();
            } else {
                final String containerClassName = className.substring(0, dollarPos);
                final Class containerClass = Class.forName(containerClassName);
                final Class innerClass = Class.forName(className);
                final Object containerInstance = containerClass.newInstance();
                final Constructor innerConstructor = innerClass.getDeclaredConstructor(new Class[]{containerClass});
                instance = innerConstructor.newInstance(containerInstance);
            }
        } catch (Exception e) {
            log.error("Error during createInstance", e);
        }
        return instance;
    }

}
