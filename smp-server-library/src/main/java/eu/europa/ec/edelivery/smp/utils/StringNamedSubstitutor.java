/*-
 * #START_LICENSE#
 * smp-server-library
 * %%
 * Copyright (C) 2017 - 2023 European Commission | eDelivery | DomiSMP
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

import org.apache.commons.lang3.StringUtils;

import java.io.*;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * This class is used to substitute named variables in the string with key value pairs from the map.
 * The variables are in the form of ${name} and are case-insensitive and can contain only letters, digits, and chars
 * '_' and '.'.
 *
 * @author Joze Rihtarsic
 * @since 4.2
 */
public class StringNamedSubstitutor {
    private static final String START_NAME = "${";
    private static final char END_NAME = '}';

    /**
     * Substitute named variables in the string with key value pairs from the map.
     * The variables are in the form of ${name} and are case-insensitive and can contain only letters, digits, _ and .
     *
     * @param templateIS the InputStream to resolve
     * @param config     the config to use
     * @return the resolved string
     */
    public static String resolve(InputStream templateIS, Map<String, Object> config) throws IOException {
        Map<String, Object> lowerCaseMap = config.entrySet().stream()
                .collect(Collectors.toMap(e -> StringUtils.lowerCase(e.getKey()), Map.Entry::getValue));
        StringBuilder builder = new StringBuilder();

        BufferedReader template = new BufferedReader(new InputStreamReader(templateIS));
        int read;
        while ((read = template.read()) != -1) {
            if (read == START_NAME.charAt(0) && isStartSequence(template)) {
                template.skip(START_NAME.length() - 1);
                String name = readName(template, END_NAME);
                if (name == null) {
                    builder.append(START_NAME);
                } else {
                    String key = StringUtils.lowerCase(name);
                    Object objValue = lowerCaseMap.get(key);
                    String value  = objValue!=null? String.valueOf(lowerCaseMap.get(key)): null;

                    if (value != null) {
                        builder.append(value);
                    } else {
                        builder.append(START_NAME).append(name).append(END_NAME);
                    }
                }
            } else {
                builder.append((char) read);
            }
        }

        return builder.toString();
    }

    public static boolean isStartSequence(BufferedReader reader) throws IOException {
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

    /**
     * Substitute named variables in the string with key value pairs from the map.
     * The variables are in the form of ${name} and are case-insensitive and can contain only letters, digits, _ and .
     *
     * @param string the string to resolve
     * @param config the config to use
     * @return the resolved string
     */
    public static String resolve(String string, Map<String, Object> config) {
        try {
            return resolve(new ByteArrayInputStream(string.getBytes()), config);
        } catch (IOException e) {
            throw new IllegalArgumentException(e);
        }
    }
}
