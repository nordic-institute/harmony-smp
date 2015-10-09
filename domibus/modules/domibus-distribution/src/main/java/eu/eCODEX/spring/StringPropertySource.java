package eu.eCODEX.spring;

import java.util.Properties;

public class StringPropertySource extends org.springframework.core.env.PropertySource<Properties> {
    private final Properties props;

    StringPropertySource(Properties properties, String name) {
        super(name, properties);
        this.props = properties;
    }

    @Override
    public boolean containsProperty(String name) {
        return this.props.contains(name);
    }

    @Override
    public String getProperty(String s) {
        return this.props.getProperty(s);
    }
}
