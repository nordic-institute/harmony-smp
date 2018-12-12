package eu.europa.ec.edelivery.smp.data.dao.utils;

import eu.europa.ec.edelivery.smp.exceptions.SMPRuntimeException;
import junitparams.JUnitParamsRunner;
import junitparams.Parameters;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;

import java.io.File;
import java.util.Collections;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

@RunWith(JUnitParamsRunner.class)
public class SMPSchemaGeneratorTest {

    private static final String DIALECT_ORACLE="org.hibernate.dialect.Oracle10gDialect";
    private static final String DIALECT_MYSQL_INNO5="org.hibernate.dialect.MySQL5InnoDBDialect";
    private static final String ENTITY_PACKAGE= "eu.europa.ec.edelivery.smp.data.model";

    private static final Object[] dialectTestCases() {
        return new Object[][]{
                {DIALECT_MYSQL_INNO5, "eu.europa.ec.edelivery.smp.data.dao.utils.SMPMySQL5InnoDBDialect"},
                {DIALECT_ORACLE, DIALECT_ORACLE},
                {"org.hibernate.dialect.MySQLDialect", "org.hibernate.dialect.MySQLDialect"},
                {null, null},

        };
    }

    @Rule
    public ExpectedException expectedEx = ExpectedException.none();


    SMPSchemaGenerator testInstance = new SMPSchemaGenerator();


    @Test
    public void createDDLScript() throws ClassNotFoundException {
        // given
        String folder = "target";
        String dialect = DIALECT_ORACLE;
        String version = "4.1.0-SNAPSHOT";
        List<String> lstPackages = Collections.singletonList(ENTITY_PACKAGE);
        File f = new File("target/oracle10g-4.1.0.ddl");
        f.delete(); // delete if exists
        assertTrue(!f.exists());


        testInstance.createDDLScript(folder, dialect, lstPackages, version);

        assertTrue(f.exists());
        assertTrue(f.length()>0);
    }

    @Test
    public void createFileNameOracleDialect() {
        // given
        String dialect = DIALECT_ORACLE;
        String version = "4.1.0";

        //when
        String filaName = testInstance.createFileName(dialect, version);
        // then
        assertEquals("oracle10g-4.1.0.ddl", filaName);
    }

    @Test
    public void createFileNameMySQLDialect() {
        // given
        String dialect = DIALECT_MYSQL_INNO5;
        String version = "4.1.0";

        //when
        String filaName = testInstance.createFileName(dialect, version);
        // then
        assertEquals("mysql5innodb-4.1.0.ddl", filaName);
    }

    @Test
    @Parameters(method = "dialectTestCases")
    public void getDialect(String input, String output) {

        //when
        String result = testInstance.getDialect(input);
        // then
        assertEquals(output, result);
    }

    @Test
    public void getAllEntityClassesNotFound() throws ClassNotFoundException {

        expectedEx.expect(ClassNotFoundException.class);

        testInstance.getAllEntityClasses("eu.not.exists");
    }
    @Test
    public void getAllEntityClasses() throws ClassNotFoundException {

        // given when
        List<Class> result =  testInstance.getAllEntityClasses(ENTITY_PACKAGE);

        assertEquals(14, result.size());
    }
}