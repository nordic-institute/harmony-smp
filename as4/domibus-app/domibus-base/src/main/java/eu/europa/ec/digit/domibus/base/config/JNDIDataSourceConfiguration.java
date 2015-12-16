package eu.europa.ec.digit.domibus.base.config;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.core.env.Environment;

/**
 * @author Vincent Dijkstra
 *
 */
@Configuration
@Profile("production")
public class JNDIDataSourceConfiguration implements DataSourceConfiguration {

    /* ---- Instance Variables ---- */

    @Autowired
    private Environment environment;

    /* ---- Configuration Beans ---- */

    @Bean (destroyMethod="")
    public DataSource dataSource() {
        DataSource dataSource;
        try {
            Context ctx = new InitialContext();
            dataSource = (DataSource) ctx.lookup(this.environment.getProperty("datasource.jndi"));
        } catch (Exception e) {
            throw new IllegalStateException("Failed to create a dataSource", e);
        }
        return dataSource;
    }

    /* ---- Getters and Setters ---- */

	public Environment getEnvironment() {
		return environment;
	}

	public void setEnvironment(Environment environment) {
		this.environment = environment;
	}

}
