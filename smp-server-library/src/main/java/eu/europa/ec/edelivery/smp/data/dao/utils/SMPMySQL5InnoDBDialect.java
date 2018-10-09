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
