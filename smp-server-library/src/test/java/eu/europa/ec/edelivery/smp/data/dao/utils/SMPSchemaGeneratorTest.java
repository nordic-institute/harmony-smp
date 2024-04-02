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
package eu.europa.ec.edelivery.smp.data.dao.utils;


import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class SMPSchemaGeneratorTest {

    private static final String DIALECT_ORACLE = "org.hibernate.dialect.Oracle10gDialect";
    private static final String DIALECT_MYSQL_INNO5 = "org.hibernate.dialect.MySQL5InnoDBDialect";

    protected static String ENTITY_PACKAGES = "eu.europa.ec.edelivery.smp.data.model," +
            "eu.europa.ec.edelivery.smp.data.model.user," +
            "eu.europa.ec.edelivery.smp.data.model.doc," +
            "eu.europa.ec.edelivery.smp.data.model.ext";

    private static Object[] dialectTestCases() {
        return new Object[][]{
                {DIALECT_MYSQL_INNO5, "eu.europa.ec.edelivery.smp.data.dao.utils.SMPMySQL5InnoDBDialect"},
                {DIALECT_ORACLE, DIALECT_ORACLE},
                {"org.hibernate.dialect.MySQLDialect", "org.hibernate.dialect.MySQLDialect"},
                {null, null},

        };
    }

    SMPSchemaGenerator testInstance = new SMPSchemaGenerator();

    @Test
    void createDDLScript() throws ClassNotFoundException, IOException {
        // given
        String folder = "target";
        String dialect = DIALECT_ORACLE;
        String version = "5.1-SNAPSHOT";
        List<String> lstPackages = Arrays.asList(ENTITY_PACKAGES.split(","));
        File f = new File("target/oracle10g.ddl");
        File fDrop = new File("target/oracle10g-drop.ddl");
        f.delete(); // delete if exists
        fDrop.delete(); // delete if exists
        assertFalse(f.exists());
        assertFalse(fDrop.exists());


        testInstance.createDDLScript(folder, dialect, lstPackages, version);

        assertTrue(f.exists());
        assertTrue(f.length() > 0);
        assertTrue(fDrop.exists());
        assertTrue(fDrop.length() > 0);
    }

    @Test
    void createFileNameOracleDialect() {
        String dialect = DIALECT_ORACLE;
        //when
        String filaName = testInstance.createFileName(dialect, SMPSchemaGenerator.filenameTemplate);
        // then
        assertEquals("oracle10g.ddl", filaName);
    }

    @Test
    void createFileNameMySQLDialect() {
        // given
        String dialect = DIALECT_MYSQL_INNO5;
        //when
        String fileName = testInstance.createFileName(dialect, SMPSchemaGenerator.filenameTemplate);
        // then
        assertEquals("mysql5innodb.ddl", fileName);
    }

    @Test
    void createDropFileNameOracleDialect() {
        String dialect = DIALECT_ORACLE;
        //when
        String fileName = testInstance.createFileName(dialect, SMPSchemaGenerator.filenameDropTemplate);
        // then
        assertEquals("oracle10g-drop.ddl", fileName);
    }

    @Test
    void createDropFileNameMySQLDialect() {
        // given
        String dialect = DIALECT_MYSQL_INNO5;
        //when
        String fileName = testInstance.createFileName(dialect, SMPSchemaGenerator.filenameDropTemplate);
        // then
        assertEquals("mysql5innodb-drop.ddl", fileName);
    }


    @ParameterizedTest
    @MethodSource("dialectTestCases")
    void getDialect(String input, String output) {

        //when
        String result = testInstance.getDialect(input);
        // then
        assertEquals(output, result);
    }

    @Test
    void getAllEntityClassesNotFound() {

        assertThrows(ClassNotFoundException.class, () -> testInstance.getAllEntityClasses("eu.not.exists"));
    }

    @Test
    void getAllEntityClasses() throws ClassNotFoundException {

        // given when
        List<Class> result = testInstance.getAllEntityClasses("eu.europa.ec.edelivery.smp.data.model");

        assertEquals(10, result.size());
    }
}
