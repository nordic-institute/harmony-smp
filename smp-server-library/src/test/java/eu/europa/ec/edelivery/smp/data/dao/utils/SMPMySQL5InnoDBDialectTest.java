package eu.europa.ec.edelivery.smp.data.dao.utils;

import org.junit.Test;

import java.sql.Types;

import static org.junit.Assert.*;
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