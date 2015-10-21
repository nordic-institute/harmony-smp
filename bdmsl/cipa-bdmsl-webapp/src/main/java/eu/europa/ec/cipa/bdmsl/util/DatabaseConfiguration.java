package eu.europa.ec.cipa.bdmsl.util;

/**
 * Created by feriaad on 08/07/2015.
 */

import org.apache.commons.configuration.ConversionException;

import javax.sql.DataSource;
import java.io.IOException;
import java.io.Reader;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.sql.Clob;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Configuration stored in a database.
 *
 */
public class DatabaseConfiguration extends org.apache.commons.configuration.DatabaseConfiguration {
    public DatabaseConfiguration(DataSource datasource, String table, String nameColumn, String keyColumn, String valueColumn, String name) {
        super(datasource, table, nameColumn, keyColumn, valueColumn, name);
    }

    /**
     * Build a configuration from a table.-
     *
     * @param datasource  the datasource to connect to the database
     * @param table       the name of the table containing the configurations
     * @param keyColumn   the column containing the keys of the configuration
     * @param valueColumn the column containing the values of the configuration
     */
    public DatabaseConfiguration(DataSource datasource, String table, String keyColumn, String valueColumn) {
        this(datasource, table, null, keyColumn, valueColumn, null);
    }

    @Override
    @SuppressWarnings("unchecked")
    public List getList(String key, List defaultValue) {
        Object value = getProperty(key);
        List<Object> list;

        if (value instanceof String) {
            list = new ArrayList<>(1);
            list.add(interpolate((String) value));
        } else if (value instanceof List) {
            list = new ArrayList<>();
            List l = (List) value;

            // add the interpolated elements in the new list
            Iterator it = l.iterator();
            while (it.hasNext()) {
                list.add(interpolate(it.next()));
            }

        } else if (value == null) {
            list = (List<Object>) defaultValue;
        } else if ("weblogic.jdbc.wrapper.Clob_oracle_sql_CLOB".equals(value.getClass().getName())) {
            try {
                Method method = value.getClass().getMethod("getVendorObj", new Class[]{});
                Clob clob = (java.sql.Clob) method.invoke(value);
                Reader reader = clob.getCharacterStream();
                StringBuilder buffer = new StringBuilder();
                int numCharsRead;
                char[] arr = new char[8 * 1024];
                while ((numCharsRead = reader.read(arr, 0, arr.length)) != -1) {
                    buffer.append(arr, 0, numCharsRead);
                }
                reader.close();
                list = new ArrayList<>(1);
                list.add(interpolate(buffer.toString()));
            } catch (InvocationTargetException exc) {
                throw new ConversionException('\'' + key + "' doesn't map to a List object: " + value + ", a "
                        + value.getClass().getName(), exc);
            } catch (NoSuchMethodException exc) {
                throw new ConversionException('\'' + key + "' doesn't map to a List object: " + value + ", a "
                        + value.getClass().getName(), exc);
            } catch (IllegalAccessException exc) {
                throw new ConversionException('\'' + key + "' doesn't map to a List object: " + value + ", a "
                        + value.getClass().getName(), exc);
            } catch (SQLException exc) {
                throw new ConversionException('\'' + key + "' doesn't map to a List object: " + value + ", a "
                        + value.getClass().getName(), exc);
            } catch (IOException exc) {
                throw new ConversionException('\'' + key + "' doesn't map to a List object: " + value + ", a "
                        + value.getClass().getName(), exc);
            }
        }
        else {
            throw new ConversionException('\'' + key + "' doesn't map to a List object: " + value + ", a "
                    + value.getClass().getName());
        }
        return list;
    }
}
