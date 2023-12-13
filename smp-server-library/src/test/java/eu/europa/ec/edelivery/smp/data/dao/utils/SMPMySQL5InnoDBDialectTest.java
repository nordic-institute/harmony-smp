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

import org.junit.Test;

import java.sql.Types;

import static org.junit.Assert.assertEquals;

public class SMPMySQL5InnoDBDialectTest {

    SMPMySQL5InnoDBDialect testInstance = new SMPMySQL5InnoDBDialect();

    @Test
    public void getTableTypeString() {

        assertEquals(" ENGINE=InnoDB DEFAULT CHARSET=utf8", testInstance.getTableTypeString());
    }

    @Test
    public void registerVarcharTypes() {

        assertEquals("longtext", testInstance.getTypeName(Types.VARCHAR));
        assertEquals("longtext", testInstance.getTypeName(Types.LONGVARCHAR));
        assertEquals("varchar(65535)  CHARACTER SET utf8 COLLATE utf8_bin", testInstance.getTypeName(Types.VARCHAR, 65535,0,0));

    }
}
