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
package eu.europa.ec.edelivery.smp.data.dao.utils;

import eu.europa.ec.edelivery.smp.logging.SMPLogger;
import eu.europa.ec.edelivery.smp.logging.SMPLoggerFactory;
import jakarta.persistence.Entity;
import org.apache.commons.lang3.StringUtils;
import org.hibernate.boot.MetadataSources;
import org.hibernate.boot.model.naming.Identifier;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.boot.spi.MetadataImplementor;
import org.hibernate.mapping.Column;
import org.hibernate.mapping.PersistentClass;
import org.hibernate.tool.hbm2ddl.SchemaExport;
import org.hibernate.tool.schema.TargetType;

import java.io.*;
import java.lang.reflect.Field;
import java.net.URL;
import java.nio.file.Files;
import java.util.*;

import static org.apache.commons.lang3.StringUtils.split;
import static org.apache.commons.lang3.StringUtils.trim;

/**
 * Class generates DDL script for SMP. Purpose of script is to manually run SQL script to create database. And to
 * give more Database Administrators opportunity to enhance script before executing on the database.
 *
 * @author Joze Rihtarsic
 * @since 4.1
 */
public class SMPSchemaGenerator {
    protected static final SMPLogger LOG = SMPLoggerFactory.getLogger(SMPSchemaGenerator.class);
    private static final String DATABASE_ACTION_CREATE = "create";
    protected static String filenameTemplate = "%s.ddl";
    protected static String filenameDropTemplate = "%s-drop.ddl";
    protected static String entityPackageName = "eu.europa.ec.edelivery.smp.data.model," +
            "eu.europa.ec.edelivery.smp.data.model.user," +
            "eu.europa.ec.edelivery.smp.data.model.doc," +
            "eu.europa.ec.edelivery.smp.data.model.ext";
    private static final String FILE_COMMENT =  "-- This is [%s] database script for DomiSML version: [%s].%n" +
            "-- This file was generated using hibernate version [%s] with dialect [%s].%n" +
            "-- For more information, refer to the Hibernate dialect documentation.%n";

    public static void main(String[] args) throws IOException, ClassNotFoundException {


        String strDialects = args[0]; // comma separated dialects
        String strVersion = args.length > 1 ? args[1] : "";  // version
        String exportFolder = args.length > 2 ? args[2] : ""; // export folder
        String hibernateVersion = args.length > 3 ? args[3] : ""; // hibernate version
        SMPSchemaGenerator sg = new SMPSchemaGenerator();

        String[] generateDataSets = strDialects.split(",");
        // execute
        for (String generateData : generateDataSets) {
            String[] generateDetails = split(generateData, ':');
            String dialect = trim(generateDetails[0]);
            String targetFilename = null;
            if (generateDetails.length > 1) {
                targetFilename = trim(generateDetails[1]);
            }

            sg.createDDLScript(exportFolder, dialect.trim(), Arrays.asList(entityPackageName.split(",")), strVersion, hibernateVersion, targetFilename);
        }

        System.exit(0);
    }

    /**
     * Create and export DDL script for hibernate dialects.
     *
     * @param exportFolder - folder where to export the script
     * @param hibernateDialect - hibernate dialect
     * @param packageNames - list of package names where to search for entity classes
     * @param version - version of DomiSML
     * @param hibernateVersion - hibernate version
     */
    public void createDDLScript(String exportFolder,
                                String hibernateDialect,
                                List<String> packageNames,
                                String version,
                                String hibernateVersion,
                                String targetFilename) throws ClassNotFoundException, IOException {
        // create export file
        System.setProperty("spring.jpa.hibernate.ddl-auto", DATABASE_ACTION_CREATE);
        System.setProperty("hibernate.hbm2ddl.auto", DATABASE_ACTION_CREATE);
        // add upper case for _AUD suffix - for mysql
        System.setProperty("org.hibernate.envers.audit_table_suffix", "_AUD");

        String targetFilenamePrivate = StringUtils.isBlank(targetFilename)?
                getFileNameFromDialect(hibernateDialect) :
                trim(targetFilename);
        String filename = String.format(filenameTemplate, targetFilenamePrivate);
        String filenameDrop = String.format(filenameDropTemplate, targetFilenamePrivate);

        String dialect = getDialect(hibernateDialect);

        // metadata source
        MetadataSources metadata = new MetadataSources(
                new StandardServiceRegistryBuilder()
                        .applySetting("hibernate.dialect", dialect)
                        .applySetting("hibernate.hbm2ddl.auto", DATABASE_ACTION_CREATE)
                        .build());

        // add annotated classes
        for (String pckName : packageNames) {
            // metadata.addPackage did not work...
            List<Class<?>> clsList = getAllEntityClasses(pckName);
            for (Class<?> clazz : clsList) {
                metadata.addAnnotatedClass(clazz);
            }
        }
        // build metadata implementor
        MetadataImplementor metadataImplementor = (MetadataImplementor) metadata.buildMetadata();

        // add column description
        metadata.getAnnotatedClasses().forEach(clzz -> {
            for (Field fld : clzz.getDeclaredFields()) {

                updateColumnComment(clzz.getName(), fld, metadataImplementor);
            }
        });

        // create schema exporter
        SchemaExport export = new SchemaExport();


        File file = new File(exportFolder, filename);
        if (Files.deleteIfExists(file.toPath())) { // delete if exists
            LOG.debug("File [{}] deleted - new will be generated!", file.getAbsolutePath());
        }

        File fileDrop = new File(exportFolder, filenameDrop);
        if (Files.deleteIfExists(fileDrop.toPath())) { // delete if exists
            LOG.debug("File [{}] deleted - new will be generated!", fileDrop.getAbsolutePath());
        }

        LOG.info("Export DDL script with dialect: [{}] to file: [{}]", dialect, file.getAbsolutePath());
        export.setOutputFile(file.getAbsolutePath());
        export.setFormat(true);
        export.setDelimiter(";");

        //chan change the output here
        EnumSet<TargetType> enumSet = EnumSet.of(TargetType.SCRIPT);
        export.execute(enumSet, SchemaExport.Action.CREATE, metadataImplementor);
        // prepend comment to file with wersion
        prependComment(file, String.format(FILE_COMMENT, "CREATE", version, hibernateVersion, hibernateDialect));

        // create drop script
        export.setOutputFile(fileDrop.getAbsolutePath());
        export.execute(enumSet, SchemaExport.Action.DROP, metadataImplementor);
        prependComment(fileDrop, String.format(FILE_COMMENT, "DROP", version, hibernateVersion, hibernateDialect));
    }


    private void updateColumnComment(String entityName, Field field, MetadataImplementor metadataImplementor) {
        jakarta.persistence.Column column = field.getAnnotation(jakarta.persistence.Column.class);
        ColumnDescription columnDesc = field.getAnnotation(ColumnDescription.class);
        if (column != null && columnDesc != null && !StringUtils.isBlank(columnDesc.comment())) {
            LOG.info("Get table for entity [{}] column name: [{}].", entityName, column.name());
            PersistentClass persistentClass = metadataImplementor.getEntityBinding(entityName);
            if (persistentClass != null) {
                Column c = persistentClass.getTable().getColumn(Identifier.toIdentifier(column.name(), false));
                // remove quotations from description
                c.setComment(columnDesc.comment().replace("'", ""));
            }
        }
    }


    /**
     * Method creates filename based on dialect
     *
     * @param dialect - hibernate dialect
     * @return file name derived from dialect
     */
    public String getFileNameFromDialect(String dialect) {
        return dialect.substring(dialect.lastIndexOf('.') + 1, dialect.lastIndexOf("Dialect")).toLowerCase();

    }

    /**
     * Some dialect are customized in order to generate better SQL DDL script. Method check the dialect and returns
     * the upgraded dialect
     *
     * @param dialect - original hibernate dialect
     * @return return the customized dialect or the dialects itself if not customization
     */
    public String getDialect(String dialect) {
        if (!StringUtils.isBlank(dialect)
                && dialect.equalsIgnoreCase("org.hibernate.dialect.MySQL5InnoDBDialect")) {
            return "eu.europa.ec.edelivery.smp.data.dao.utils.SMPMySQL5InnoDBDialect";
        } else {
            return dialect;
        }
    }

    /***
     * Returns list of classes with entity annotation in package and subpackages.
     * @param packageName the package name where to search for entity classes
     * @return list of classes with entity annotation
     * @throws ClassNotFoundException if the class is not found
     * @throws IOException if the package is not found
     * <p />
     * Method was heavily inspired by:
     * <a href="https://dzone.com/articles/get-all-classes-within-package">dzone.com</a>
     */


    public List<Class<?>> getAllEntityClasses(String packageName)
            throws ClassNotFoundException, IOException {
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        assert classLoader != null;
        String path = packageName.replace('.', '/');
        Enumeration<URL> resources = classLoader.getResources(path);
        List<File> dirs = new ArrayList<>();
        while (resources.hasMoreElements()) {
            URL resource = resources.nextElement();
            dirs.add(new File(resource.getFile()));
        }
        if (dirs.isEmpty()) {
            throw new ClassNotFoundException("Package: " + packageName + " not exist.");
        }
        ArrayList<Class<?>> classes = new ArrayList<>();
        for (File directory : dirs) {
            classes.addAll(findClasses(directory, packageName));
        }
        return classes;
    }

    private List<Class<?>> findClasses(File directory, String packageName) throws ClassNotFoundException {
        List<Class<?>> classes = new ArrayList<>();
        if (!directory.exists()) {
            return classes;
        }
        File[] files = Objects.requireNonNull(directory.listFiles());
        for (File file : files) {
            if (file.isDirectory()) {
                assert !file.getName().contains(".");
                classes.addAll(findClasses(file, packageName + "." + file.getName()));
            } else if (file.getName().endsWith(".class")) {
                Class<?> clazz = Class.forName(packageName + '.' + file.getName().substring(0, file.getName().length() - 6));
                if (clazz.isAnnotationPresent(Entity.class)) {
                    classes.add(clazz);
                }
            }
        }
        return classes;
    }


    public static void prependComment(File input, String prefix) throws IOException {
        try (RandomAccessFile file = new RandomAccessFile(input, "rws")) {
            byte[] text = new byte[(int) file.length()];
            file.readFully(text);
            file.seek(0);
            file.writeBytes(prefix);
            file.write(text);
        }
    }
}
