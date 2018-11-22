package eu.europa.ec.edelivery.smp.data.dao.utils;

import eu.europa.ec.edelivery.smp.logging.SMPLogger;
import eu.europa.ec.edelivery.smp.logging.SMPLoggerFactory;
import org.apache.commons.lang3.StringUtils;
import org.hibernate.boot.MetadataSources;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.boot.spi.MetadataImplementor;
import org.hibernate.tool.hbm2ddl.SchemaExport;
import org.hibernate.tool.schema.TargetType;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.EnumSet;
import java.util.List;

/**
 * Class generates DDL script for SMP. Purpose of script is to manually run SQL script to create database. And to
 * give more Database Administrators opportunity to enhance script before executing on the database.
 *
 * @author Joze Rihtarsic
 * @since 4.1
 */
public class SMPSchemaGenerator {
    private static String filenameTemplate = "%s-%s.ddl";
    private static String smpEntityPackageName = "eu.europa.ec.edelivery.smp.data.model";
    private static final SMPLogger LOG = SMPLoggerFactory.getLogger(SMPSchemaGenerator.class);

    public static void main(String[] args) throws IOException, ClassNotFoundException {


        String strDialects = args[0]; // comma separated dialects
        String strVersion = args.length > 1 ? args[1] : "";  // version
        String exportFolder = args.length > 2 ? args[2] : ""; // export folder
        SMPSchemaGenerator sg = new SMPSchemaGenerator();

        String[] dialects = strDialects.split(",");
        // execute
        for (String dialect : dialects) {
            sg.createDDLScript(exportFolder, dialect.trim(), Arrays.asList(smpEntityPackageName.split(",")), strVersion);
        }

        System.exit(0);
    }

    /**
     * Create and export DDL script for hibernate dialects.
     *
     * @param exportFolder
     * @param hibernateDialect
     * @param packageNames
     * @param version
     */
    public void createDDLScript(String exportFolder, String hibernateDialect, List<String> packageNames, String version) throws ClassNotFoundException {
        // create export file
        String sqlVer = version;
        int idx = version.indexOf("-SNAPSHOT");
        if (idx > 0) {
            sqlVer = version.substring(0, idx);
        }
        String filename = createFileName(hibernateDialect, sqlVer);

        String dialect = getDialect(hibernateDialect);

        // metadata source
        MetadataSources metadata = new MetadataSources(
                new StandardServiceRegistryBuilder()
                        .applySetting("hibernate.dialect", dialect)
                        .applySetting("hibernate.hbm2ddl.auto", "create")
                        .build());

        // add annonated classes
        for (String pckName : packageNames) {
            // metadata.addPackage did not work...
            List<Class> clsList = getAllEntityClasses(pckName);
            for (Class clazz : clsList) {

                metadata.addAnnotatedClass(clazz);
            }
        }

        MetadataImplementor metadataImplementor = (MetadataImplementor) metadata.buildMetadata();
        // create schema exporter
        SchemaExport export = new SchemaExport();
        File file = new File(exportFolder, filename);
        if (file.delete()) { // delete if exists
            LOG.info("File {} deleted!", file.getAbsolutePath());
        } ;
        export.setOutputFile(file.getAbsolutePath());
        export.setFormat(true);
        export.setDelimiter(";");

        //can change the output here
        EnumSet<TargetType> enumSet = EnumSet.of(TargetType.SCRIPT);
        export.execute(enumSet, SchemaExport.Action.CREATE, metadataImplementor);


    }

    /**
     * Method creates filename based on dialect and version
     *
     * @param dialect
     * @param version
     * @return file name.
     */
    public String createFileName(String dialect, String version) {
        String dbName = dialect.substring(dialect.lastIndexOf('.') + 1, dialect.lastIndexOf("Dialect")).toLowerCase();
        return String.format(filenameTemplate, dbName, version);
    }

    /**
     * Some dialect are customized in order to generate better SQL DDL script. Method check the dialect and returns
     * the upgrated dialect
     *
     * @param dialect - original hibernate dialect
     * @return return the customized dialect or the dialects itself if not costumization
     */
    public String getDialect(String dialect) {
        if (!StringUtils.isBlank(dialect) && dialect.equalsIgnoreCase("org.hibernate.dialect.MySQL5InnoDBDialect")) {
            return "eu.europa.ec.edelivery.smp.data.dao.utils.SMPMySQL5InnoDBDialect";
        } else {
            return dialect;
        }
    }

    /***
     * Returns list of classes in package.
     * @param pckgname
     * @return
     */
    public List<Class> getAllEntityClasses(String pckgname) throws ClassNotFoundException {
        ArrayList classes = new ArrayList();

        // Get a File object for the package
        File directory = null;
        try {
            directory = new File(Thread.currentThread().getContextClassLoader().getResource(pckgname.replace('.', '/')).getFile());
        } catch (NullPointerException x) {
            throw new ClassNotFoundException(pckgname + " does not appear to be a valid package");
        }
        if (directory.exists()) {
            // Get the list of the files contained in the package
            String[] files = directory.list();
            for (int i = 0; i < files.length; i++) {
                if (files[i].endsWith(".class")) {
                    // removes the .class extension
                    classes.add(Class.forName(pckgname + '.' + files[i].substring(0, files[i].length() - 6)));
                }
            }
        } else {
            throw new ClassNotFoundException("Package: " + pckgname + " does not eixsts!");
        }

        return classes;
    }

}
