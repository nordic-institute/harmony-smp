package eu.europa.ec.edelivery.smp.config;

import org.apache.commons.lang3.StringUtils;
import org.springframework.context.annotation.Condition;
import org.springframework.context.annotation.ConditionContext;
import org.springframework.core.type.AnnotatedTypeMetadata;

public class JNDIDatasourceCondition implements Condition {

    @Override
    public boolean matches(ConditionContext context, AnnotatedTypeMetadata metadata) {
        // urls has precedence before jndi
        String jndiName = context.getEnvironment().getProperty("datasource.jndi");
        String url = context.getEnvironment().getProperty("jdbc.url");
        return StringUtils.isBlank(url) && !StringUtils.isBlank(jndiName);
    }
}
