/*-
 * #START_LICENSE#
 * smp-server-library
 * %%
 * Copyright (C) 2017 - 2024 European Commission | eDelivery | DomiSMP
 * %%
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by the European Commission - subsequent
 * versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy of the Licence at:
 *
 * [PROJECT_HOME]\license\eupl-1.2\license.txt or https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the Licence is
 * distributed on an "AS IS" basis, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the Licence for the specific language governing permissions and limitations under the Licence.
 * #END_LICENSE#
 */
package eu.europa.ec.edelivery.smp.utils;

import org.slf4j.Logger;

import java.io.*;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Map;

import static org.apache.commons.lang3.StringUtils.lowerCase;
import static org.apache.commons.lang3.StringUtils.trim;

/**
 * This class is used to substitute named variables in the string with key value pairs from the map.
 * The variables are in the form of ${name} and are case-insensitive and can contain only letters, digits, and chars
 * '_' and '.'.
 *
 * @author Joze Rihtarsic
 * @since 4.2
 */
public class StringNamedSubstitutor {
    private static final Logger LOG = org.slf4j.LoggerFactory.getLogger(StringNamedSubstitutor.class);
    private static final String START_NAME = "${";
    private static final char END_NAME = '}';

    private StringNamedSubstitutor() {
        // private constructor
    }

    /**
     * Substitute named variables in the string with key value pairs from the map.
     * The variables are in the form of ${name} and are case-insensitive and can contain only letters, digits, _ and .
     *
     * @param string the string to resolve
     * @param config the config to use
     * @return the resolved string
     */
    public static String resolve(String string, Map<String, Object> config) {
        String charset = Charset.defaultCharset().name();
        LOG.debug("Using default charset: [{}]", charset);
        return resolve(string, config, charset);
    }

    /**
     * Substitute named variables in the string with key value pairs from the map.
     * The variables are in the form of ${name} and are case-insensitive and can contain only letters, digits, _ and .
     *
     * @param string  the string to resolve
     * @param config  the config to use
     * @param charset the character of the input stream
     * @return the resolved string
     */
    public static String resolve(String string, Map<String, Object> config, String charset) {
        try {
            return resolve(new ByteArrayInputStream(string.getBytes()), config, charset);
        } catch (IOException e) {
            throw new IllegalArgumentException(e);
        }
    }

    /**
     * Substitute named variables in the string with key value pairs from the map.
     * The variables are in the form of ${name} and are case-insensitive and can contain only letters, digits, _ and .
     * The input stream is ready with default charset.
     *
     * @param templateIS the InputStream to resolve
     * @param config     the map of property names and its values
     * @return the resolved string
     * @throws IOException if an I/O error occurs
     */
    public static String resolve(InputStream templateIS, Map<String, Object> config) throws IOException {
        String charset = Charset.defaultCharset().name();
        LOG.debug("Using default charset: [{}]", charset);
        return resolve(templateIS, config, charset);
    }

    /**
     * Substitute named variables in the string with key value pairs from the map.
     * The variables are in the form of ${name} and are case-insensitive and can contain only letters, digits, _ and .
     *
     * @param templateIS the InputStream to resolve
     * @param config     the config to use
     * @param charset    the character of the input stream
     * @return the resolved string
     */
    public static String resolve(InputStream templateIS, Map<String, Object> config, String charset) throws IOException {

        try (ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream()) {
            resolve(templateIS, config, byteArrayOutputStream, charset);
            return byteArrayOutputStream.toString();
        }
    }

    /**
     * Substitute named variables in the string with key value pairs from the map.
     * The variables are in the form of ${name} and are case-insensitive and can contain only letters, digits, _ and .
     *
     * @param templateIS   the InputStream to resolve
     * @param config       the config to use
     * @param outputStream the output stream to write the resolved string
     * @param charset      the charset to use
     * @throws IOException if an I/O error occurs
     */
    public static void resolve(InputStream templateIS, Map<String, Object> config,
                               OutputStream outputStream, String charset) throws IOException {
        Map<String, Object> lowerCaseMap = normalizeData(config);
        try (BufferedReader template = new BufferedReader(new InputStreamReader(templateIS, charset));
             Writer writer = new OutputStreamWriter(outputStream, charset)) {
            int read;
            while ((read = template.read()) != -1) {
                if (read != START_NAME.charAt(0) || !isStartSequence(template)) {
                    writer.write((char) read);
                    continue;
                }

                template.skip(1L);
                String name = readName(template, END_NAME);
                if (name == null) {
                    writer.write(START_NAME);
                } else {
                    String key = lowerCase(name);
                    Object objValue = lowerCaseMap.get(key);
                    String value = objValue != null ? String.valueOf(lowerCaseMap.get(key)) : null;

                    if (value != null) {
                        writer.write(value);
                    } else {
                        writer.write(START_NAME);
                        writer.write(name);
                        writer.write(END_NAME);
                    }
                }
            }
        }
    }

    /**
     * Normalize the data model to trim and lower case the keys.
     *
     * @param dataModel the data model
     * @return the normalized data model
     */
    private static Map<String, Object> normalizeData(Map<String, Object> dataModel) {
        Map<String, Object> lowerCaseMap = new HashMap<>();
        // Note: do not use stream with Collectors.toMap because it throws NPE if value is null
        dataModel.forEach((key, value) ->
                lowerCaseMap.put(lowerCase(trim(key)), value));
        return lowerCaseMap;
    }

    private static boolean isStartSequence(BufferedReader reader) throws IOException {
        reader.mark(START_NAME.length());
        int read = reader.read();
        if (read == -1) {
            return false;
        }
        for (int i = 1; i < START_NAME.length(); i++) {
            if (read != START_NAME.charAt(i)) {
                reader.reset();
                return false;
            }
            read = reader.read();
            if (read == -1) {
                return false;
            }
        }
        reader.reset();
        return true;
    }

    /**
     * Method reads name until it finds end name char or until the first not letter/digit or
     * _ or . character is found.
     *
     * @param inputStream the string to search index of the character
     * @param searchChar  the character
     * @return the index of the character or -1 if not found
     */
    private static String readName(BufferedReader inputStream, char searchChar) throws IOException {
        StringBuilder builder = new StringBuilder();
        int read;
        while ((read = inputStream.read()) != -1) {
            char currChar = (char) read;
            if (currChar == searchChar) {
                return builder.toString();
            } else if (!Character.isLetterOrDigit(currChar)
                    && currChar != '_'
                    && currChar != '.') {
                builder.append(currChar);
                return builder.toString();
            }
            builder.append(currChar);
        }
        return null;
    }
}
