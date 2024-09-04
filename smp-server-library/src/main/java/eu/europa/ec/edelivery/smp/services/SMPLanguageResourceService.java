package eu.europa.ec.edelivery.smp.services;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import eu.europa.ec.edelivery.smp.config.enums.SMPEnvPropertyEnum;
import eu.europa.ec.edelivery.smp.logging.SMPLogger;
import eu.europa.ec.edelivery.smp.logging.SMPLoggerFactory;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.stereotype.Service;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Properties;
import java.util.TimeZone;
import java.util.concurrent.atomic.AtomicBoolean;

import static org.apache.commons.lang3.StringUtils.lowerCase;
import static org.apache.commons.lang3.StringUtils.trimToEmpty;

/**
 * Service providing operations for managing language resources (e.g. updating language files on the disk).
 *
 * @author Sebastian-Ion TINCU
 * @since 5.1
 */
@Service
public class SMPLanguageResourceService {

    public static final String LANGUAGE_FILENAME_UI_PREFIX = "ui_";
    public static final String LANGUAGE_RESOURCE_UI_FOLDER = "/META-INF/resources/ui/assets/i18n/";
    public static final String LANGUAGE_RESOURCE_UI_DEFAULT = LANGUAGE_RESOURCE_UI_FOLDER + "en.json";

    public static final String LANGUAGE_FILENAME_MAIL_PREFIX = "mail-messages_";
    public static final String LANGUAGE_RESOURCE_MAIL_FOLDER = "/mail-messages/";
    public static final String LANGUAGE_RESOURCE_MAIL_DEFAULT = LANGUAGE_RESOURCE_MAIL_FOLDER + "en.json";

    private static final SMPLogger LOG = SMPLoggerFactory.getLogger(SMPLanguageResourceService.class);

    private final ConfigurationService configurationService;
    private final ResourcePatternResolver resourceResolver;

    public SMPLanguageResourceService(ConfigurationService configurationService,
                                      ResourcePatternResolver resourceResolver) {
        this.configurationService = configurationService;
        this.resourceResolver = resourceResolver;
    }


    /**
     * Method for getting the language file for a specific ISO 639 language code.
     *
     * @param langCode       the ISO 639 language code (e.g. "en" for English, "fr" for French,
     *                       "de" for German, etc.) or null for the default language
     * @param filenamePrefix the prefix of the language file (e.g. "ui_" for "ui_en.json")
     * @return the path to the language file
     */
    public Path getLanguageFile(String filenamePrefix, String langCode) {
        File localeFolder = configurationService.getLocaleFolder();
        String languageFileName = getLanguageFilename(filenamePrefix, langCode);

        if (localeFolder != null && !localeFolder.exists() && !localeFolder.mkdirs()) {
            LOG.error("Failed to create locale folder [{}]", localeFolder);
            return null;
        }
        return new File(localeFolder, languageFileName).toPath().toAbsolutePath();
    }

    @Cacheable("mail-templates-translations")
    public Properties getMailProperties(String langCode) {
        Resource langRes = getTranslationResourceFile(LANGUAGE_FILENAME_MAIL_PREFIX, langCode, LANGUAGE_RESOURCE_MAIL_DEFAULT);
        ObjectMapper mapper = jsonObjectMapper();
        try (InputStream target = langRes.getInputStream()) {
            // Read JSON nodes from input streams
            JsonNode jsonTranslation = mapper.readTree(target);
            Properties properties = new Properties();
            jsonTranslation.fieldNames().forEachRemaining(fieldName ->
                properties.setProperty(fieldName, jsonTranslation.get(fieldName).asText());
            );
            return properties;
        } catch (IOException e) {
            LOG.error("Error occurred while merging the translation files", e);
            return new Properties();
        }
    }

    public Resource getTranslationResourceFile(String prefix, String code, String defaultResourceFile) {

        Path langResourcePath = getLanguageFile(prefix, code);
        if (langResourcePath != null && langResourcePath.toFile().exists()) {
            LOG.debug("Returning local mail translation file [{}]", langResourcePath.toAbsolutePath());
            return new FileSystemResource(langResourcePath);
        } else {
            LOG.warn("Local translation file [{}] does not exist. Return default translation [{}]!", code, defaultResourceFile);
            ClassPathResource defResource = new ClassPathResource(defaultResourceFile);
            if (defResource.exists()) {
                return defResource;
            } else {
                LOG.error("Default locale file [{}] does not exist in classpath!", defaultResourceFile);
                return null;
            }
        }
    }

    /**
     * Method for updating the language files on the disk. The method will copy the language files from the classpath
     * to the locale folder. If the locale folder does not exist, the method will try to create it.
     * The method will not overwrite existing properties in the translation files.  If a property is missing in the
     * existing translation file, the method will add it from the classpath translation file.
     */
    public void updateLocalesOnDisk() {
        updateLocalesOnDisk(LANGUAGE_FILENAME_UI_PREFIX, LANGUAGE_RESOURCE_UI_FOLDER + "*.json");
        updateLocalesOnDisk(LANGUAGE_FILENAME_MAIL_PREFIX, LANGUAGE_RESOURCE_MAIL_FOLDER + "*.json");
    }

    public void updateLocalesOnDisk(String filenamePrefix, String resourcePathPattern) {
        // Get all the language files from the classpath
        Resource[] resources;
        try {
            resources = resourceResolver.getResources("classpath:" + resourcePathPattern);
        } catch (IOException e) {
            LOG.error("An error occurred while reading the language files from the classpath", e);
            return;
        }

        // Copy the language files to the locale folder
        for (Resource resource : resources) {
            String filename = resource.getFilename();
            if (filename == null) {
                LOG.warn("Resource [{}] does not have a filename!", resource);
                continue;
            }
            String langCode = filename.substring(0, filename.indexOf('.'));
            Path localeFile = getLanguageFile(filenamePrefix, langCode);
            if (localeFile == null) {
                LOG.warn("Can not generate 'locale/language' file [{}]! Check if local folder defined in property [{}] " +
                        "has writing permissions and the folder exists", localeFile, SMPEnvPropertyEnum.LOCALE_FOLDER.getProperty());
                continue;
            }
            copyOrUpdateTranslation(localeFile, resource);
        }
    }

    /**
     * Method for copying a resource to a locale folder. If the IOException occurs, the method will silently log an error.
     * The error can be caused by the lack of writing permissions or the absence of the locale folder or if the file already exists.
     *
     * @param resource   the resource to copy
     * @param localeFile the path to the locale file
     */
    protected static void copyResourceToLocaleFolder(Resource resource, Path localeFile) {
        try (InputStream inputStream = resource.getInputStream()) {
            Files.copy(inputStream, localeFile);
        } catch (IOException e) {
            LOG.error("An error occurred while updating locale file [{}]", localeFile, e);
        }
    }

    /**
     * Method creates the language filename for a specific ISO 639 language code
     * and a prefix. If the prefix is null, it will be ignored. The prefix and the language code
     * will be normalized (lower case and trimmed).
     *
     * @param langCode the ISO 639 language code
     * @return the filename for the language file
     */
    private String getLanguageFilename(String prefix, String langCode) {
        return normalize(prefix) + normalize(langCode) + ".json";
    }

    /**
     * Method returns normalized token (lower case and trimmed).
     *
     * @param token token to normalize
     * @return normalized token
     */
    private String normalize(String token) {
        return trimToEmpty(lowerCase(token));
    }

    /**
     * The method will merge the existing translation file with the classpath translation file. The merge will add
     * missing properties from the classpath translation file into the existing translation file, but it will not
     * overwrite existing properties.
     *
     * @param localFilePath       the path to the local translation file
     * @param resourceTranslation
     */
    public static void copyOrUpdateTranslation(Path localFilePath, Resource resourceTranslation) {
        File localFile = localFilePath.toFile();
        LOG.info("Updating translation file [{}]", localFile);
        if (!localFile.exists()) {
            LOG.debug("The local translation file [{}] does not exist!", localFile);
            copyResourceToLocaleFolder(resourceTranslation, localFilePath);
            return;
        }
        // update file
        ObjectMapper mapper = jsonObjectMapper();
        JsonNode mergedJson = null;
        boolean changed = false;
        try (InputStream target = new FileInputStream(localFilePath.toFile());
             InputStream classpathTranslation = resourceTranslation.getInputStream()) {
            // Read JSON nodes from input streams
            JsonNode jsonReference = mapper.readTree(classpathTranslation);
            mergedJson = mapper.readTree(target);
            // Merge the JSON nodes
            changed = mergeTranslationJson(mergedJson, jsonReference);
        } catch (IOException e) {
            LOG.error("Error occurred while merging the translation files", e);
            return;
        }

        if (!changed) {
            LOG.info("No changes were made to the translation file [{}]", localFile);
            return;
        }

        // Write the merged JSON to the output file
        try (OutputStream os = new FileOutputStream(localFilePath.toFile())) {
            // Write the merged JSON to the output file
            mapper.writerWithDefaultPrettyPrinter().writeValue(os, mergedJson);
        } catch (IOException e) {
            LOG.error("Error occurred while writing the merged translation file", e);
        }
    }

    /**
     * Merge two JSON nodes recursively. Method will add missing properties from
     * the reference node into the target node, but it will not overwrite existing
     * properties. If any changes were made to the target node, the method returns
     * true else false.
     * <p>
     * The merge is used to add new translations to the existing language
     * files.
     *
     * @param targetNode    the main node to be updates
     * @param referenceNode the reference node
     * @return true if any changes were made to the target node else false.
     */
    protected static boolean mergeTranslationJson(JsonNode targetNode, JsonNode referenceNode) {
        AtomicBoolean changed = new AtomicBoolean(false);
        referenceNode.fieldNames().forEachRemaining(fieldName -> {
            JsonNode jsonNode = targetNode.get(fieldName);
            // If the node is an object, recursively merge
            boolean mergeChanged = changed.get();
            if (jsonNode != null && jsonNode.isObject()) {
                mergeChanged |= mergeTranslationJson(jsonNode, referenceNode.get(fieldName));
            } else if (jsonNode == null && targetNode instanceof ObjectNode) {
                // add new field
                ((ObjectNode) targetNode).set(fieldName, referenceNode.get(fieldName));
                mergeChanged = true;
            }
            changed.set(mergeChanged);
        });
        return changed.get();
    }


    /**
     * Method for creating a new instance of the {@link ObjectMapper} with the default configuration.
     *
     * @return the new instance of the {@link ObjectMapper}
     */
    public static ObjectMapper jsonObjectMapper() {
        ObjectMapper objectMapper = JsonMapper.builder()
                .findAndAddModules()
                .build();
        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ");
        dateFormat.setTimeZone(TimeZone.getDefault());
        objectMapper.setDateFormat(dateFormat);
        return objectMapper;
    }

}
