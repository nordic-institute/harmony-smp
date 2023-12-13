/*-
 * #%L
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
 * #L%
 */
package eu.europa.ec.edelivery.smp.data.dao.utils;

import org.hibernate.dialect.MySQL5InnoDBDialect;

import java.sql.Types;

/**
 *  Update the MySQL5InnoDBDialect to add CHARSET=utf8 to varchar columns and tables!
 *
 * @author Joze Rihtarsic
 * @since 4.1
 */
public class SMPMySQL5InnoDBDialect extends MySQL5InnoDBDialect {

    @Override
        public String getTableTypeString() {
            return " ENGINE=InnoDB DEFAULT CHARSET=utf8";
        }

    @Override
    protected void registerVarcharTypes() {
        registerColumnType( Types.VARCHAR, "longtext" );
        // TO  SET CHARACTER SET utf8 COLLATE utf8_bin
        registerColumnType( Types.VARCHAR, 65535, "varchar($l)  CHARACTER SET utf8 COLLATE utf8_bin" );
        registerColumnType( Types.LONGVARCHAR, "longtext" );
    }
}
