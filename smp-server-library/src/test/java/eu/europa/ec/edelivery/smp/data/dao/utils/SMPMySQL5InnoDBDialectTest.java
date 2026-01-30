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


import jakarta.persistence.*;
import org.hamcrest.MatcherAssert;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.NaturalId;
import org.hibernate.boot.Metadata;
import org.hibernate.boot.MetadataSources;
import org.hibernate.boot.registry.StandardServiceRegistry;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.envers.Audited;
import org.hibernate.tool.hbm2ddl.SchemaExport;
import org.hibernate.tool.schema.TargetType;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Path;
import java.time.OffsetDateTime;
import java.util.EnumSet;
import java.util.UUID;

import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.not;


class SMPMySQL5InnoDBDialectTest {

    SMPMySQL5InnoDBDialect testInstance = new SMPMySQL5InnoDBDialect();

    @Test
    void getTableTypeString() {

        Assertions.assertEquals(" ENGINE=InnoDB DEFAULT CHARSET=utf8", testInstance.getTableTypeString());
    }

    @Test
    public void testGeneratedDDLForEntity() throws IOException {
        Path outputFilePath = Path.of("target", "test-ddl-" + UUID.randomUUID() + ".sql");
        // Set up the Hibernate service registry and metadata
        StandardServiceRegistry serviceRegistry = new StandardServiceRegistryBuilder()
                .applySetting("hibernate.dialect", SMPMySQL5InnoDBDialect.class.getName())
                .build();

        Metadata metadata = new MetadataSources(serviceRegistry)
                .addAnnotatedClass(TestEntity.class)
                .buildMetadata();

        // Generate the DDL
        SchemaExport schemaExport = new SchemaExport();

        schemaExport.setDelimiter(";");
        schemaExport.setFormat(true);
        schemaExport.setHaltOnError(true);
        schemaExport.setOutputFile(outputFilePath.toFile().getAbsolutePath());
        schemaExport.execute(EnumSet.of(TargetType.SCRIPT), SchemaExport.Action.CREATE, metadata);

        // read target/test-ddl.sql file to string
        String generatedDDL = new String(java.nio.file.Files.readAllBytes(outputFilePath));

        // Verify the generated DDL contains the expected LONGTEXT type for the description field
        System.out.println(generatedDDL);
        MatcherAssert.assertThat(generatedDDL, containsString("pem_encoding longtext"));
        MatcherAssert.assertThat(generatedDDL, containsString("certificate_id varchar(256)  CHARACTER SET utf8 COLLATE utf8_bin"));
        // audit had different type pem_encoding varchar(255)
        MatcherAssert.assertThat(generatedDDL, not(containsString("pem_encoding varchar")));

        MatcherAssert.assertThat(generatedDDL, containsString("created_on datetime"));

    }

    @Entity
    @Audited
    @Table(name = "DB_test")
    public static class TestEntity {

        // for 4.3 to 5.0 migration set increment_size to 1
        @Id
        @GenericGenerator(name = "db_certificate_seq", strategy = "native", parameters = {
                @org.hibernate.annotations.Parameter(name = "increment_size", value = "1")
        })
        @GeneratedValue(strategy = GenerationType.AUTO, generator = "db_certificate_seq")
        @Column(name = "id")
        private Long id;

        // length is dirty fix to make hibernate ddl generation for audit table
        // to correctly set the type longtext instead of varchar(255)
        // This is fix mysql dialect with hibernate 6.6.11.Final.Future version may not need this fix.!
        @Column(name = "pem_encoding", length = Integer.MAX_VALUE)
        @ColumnDescription(comment = "PEM encoding for the certificate")
        @Lob
        private String pemEncoding;


        @Column(name = "certificate_id", length = 256, unique = true, nullable = false)
        @ColumnDescription(comment = "The certificate_id is a key composed of the subject and the serial number of the certificate.")
        @NaturalId
        private String certificateId;

        @Column(name = "created_on", nullable = false)
        OffsetDateTime createdOn;

        public Long getId() {
            return id;
        }

        public void setId(Long id) {
            this.id = id;
        }

        public OffsetDateTime getCreatedOn() {
            return createdOn;
        }

        public void setCreatedOn(OffsetDateTime createdOn) {
            this.createdOn = createdOn;
        }
    }
}
