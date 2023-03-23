package eu.europa.ec.edelivery.smp.config.enums;

/**
 * DomiSMP environment properties definition
 *
 * @author Joze Rihtarsic
 * @since 5.0
 */
public enum SMPEnvPropertyEnum {
    CONFIGURATION_FILE("smp.configuration.file","smp.conf.properties","Configuration property file path."),
    SECURITY_FOLDER("smp.security.folder","smp","security folder for storing the keystore and the truststore"),
    INIT_CONFIGURATION_FILE("smp.init.configuration.file","smp.init.properties","Init configuration property file path."),
    LOG_CONFIGURATION_FILE("smp.log.configuration.file",null,"The path to custom logback logging configuration file  If configuration file path is blank, the default configuration is used."),
    LOG_FOLDER("smp.log.folder","logs","Configuration property file path."),
    LIBRARY_FOLDER("smp.libraries.folder","libs","Folder for deployment of the DomiSMP extensions."),

    DATABASE_JNDI("smp.datasource.jndi",null,"he JNDI name for datasource as example:" +
            "* weblogic datasource JNDI example " +
            "datasource.jndi=jdbc/eDeliverySmpDs " +
            "* tomcat datasource JNDI example" +
            "datasource.jndi=java:comp/env/jdbc/eDeliverySmpDs"),
    DATABASE_CREATE_DDL("smp.database.create-ddl","false","Auto create/update database objects. The property is effective only when smp.mode.development=true!"),

    DATABASE_SHOW_SQL("smp.database.show-sql","false","Print generated sql queries to logs. The property is effective only when smp.mode.development=true!"),
    HIBERNATE_DIALECT("smp.database.hibernate.dialect",null,"If for some reason it is not able to determine the proper DB dialect, you will need to set the hibernate dialect."),
    JDBC_DRIVER("smp.jdbc.driver",null," The jdbc driver as example: com.mysql.jdbc.Driver."),
    JDBC_USER("smp.jdbc.user",null," The jdbc connection username."),
    JDBC_PASSWORD("smp.jdbc.password",null,"The jdbc connection password."),
    JDBC_URL("smp.jdbc.url",null,"The jdbc URL as example: jdbc:mysql://localhost:3306/smp."),


    SMP_MODE_DEVELOPMENT("smp.mode.development","false","Set to true in test or development environment to make faster \"semi-random generation of secrets\"."),
    ;

    String property;
    String defValue;
    String desc;

    SMPEnvPropertyEnum(String property, String defValue, String desc) {
        this.property = property;
        this.defValue = defValue;
        this.desc = desc;
    }

    public String getProperty() {
        return property;
    }

    public String getDefValue() {
        return defValue;
    }

    public String getDesc() {
        return desc;
    }
}
