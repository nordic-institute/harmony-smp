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

import org.hibernate.boot.model.TypeContributions;
import org.hibernate.dialect.MySQLDialect;
import org.hibernate.service.ServiceRegistry;
import org.hibernate.type.descriptor.sql.internal.CapacityDependentDdlType;
import org.hibernate.type.descriptor.sql.internal.DdlTypeImpl;
import org.hibernate.type.descriptor.sql.spi.DdlTypeRegistry;

import java.sql.Types;

import static org.hibernate.type.SqlTypes.*;
import static org.hibernate.type.SqlTypes.CHAR;

/**
 *  Update the MySQL5InnoDBDialect to add CHARSET=utf8 to varchar columns and tables!
 *
 * @author Joze Rihtarsic
 * @since 4.1
 */
public class SMPMySQL5InnoDBDialect extends MySQLDialect {


    @Override
    public String getTableTypeString() {
        return " ENGINE=InnoDB DEFAULT CHARSET=utf8";
    }


    @Override
    protected void registerColumnTypes(TypeContributions typeContributions, ServiceRegistry serviceRegistry) {
        super.registerColumnTypes(typeContributions, serviceRegistry);

        final DdlTypeRegistry ddlTypeRegistry = typeContributions.getTypeConfiguration().getDdlTypeRegistry();
        //ddlTypeRegistry.addDescriptor(new DdlTypeImpl(Types.VARCHAR, "varchar($l)  CHARACTER SET utf8 COLLATE utf8_bin", this));
        // this is the default ddl generation for clob
        ddlTypeRegistry.addDescriptor(new DdlTypeImpl(Types.CLOB, "longtext", this));
        ddlTypeRegistry.addDescriptor(new DdlTypeImpl(Types.LONGVARCHAR, "longtext", this));
        // this is the default ddl generation for timestamp
        ddlTypeRegistry.addDescriptor(new DdlTypeImpl(Types.TIMESTAMP, "datetime", this));

        // this is added for audit table with clob otherwise it will be varchar(255)
        final CapacityDependentDdlType.Builder varcharBuilder =
                CapacityDependentDdlType.builder(
                                VARCHAR,
                                CapacityDependentDdlType.LobKind.BIGGEST_LOB,
                                columnType(CLOB),
                                columnType(CHAR),
                                castType(CHAR),
                                this
                        )
                        .withTypeCapacity(getMaxVarcharLength(), "varchar($l)  CHARACTER SET utf8 COLLATE utf8_bin")
                        .withTypeCapacity(Integer.MAX_VALUE, "longtext");
        ddlTypeRegistry.addDescriptor(varcharBuilder.build());


    }


}