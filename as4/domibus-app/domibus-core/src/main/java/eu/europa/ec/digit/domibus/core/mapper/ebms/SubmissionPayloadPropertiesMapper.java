package eu.europa.ec.digit.domibus.core.mapper.ebms;

import java.util.HashSet;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import eu.europa.ec.digit.domibus.core.mapper.AbstractMapper;
import eu.europa.ec.digit.domibus.domain.domibus.PropertyBO;

@Component
public class SubmissionPayloadPropertiesMapper extends AbstractMapper<Set<PropertyBO>, Properties> {

    @Override
    public Properties mapTo(Set<PropertyBO> propertiesBO) {

        Properties submissionPayloadProperties = new Properties();

        if (!CollectionUtils.isEmpty(propertiesBO)) {
            for (PropertyBO propertyBO : propertiesBO) {
                submissionPayloadProperties.setProperty(propertyBO.getName(), propertyBO.getValue());
            }
        }
        return submissionPayloadProperties;
    }

    @Override
    public Set<PropertyBO> mapFrom(Properties submissionPayloadProperties) {
        Set<PropertyBO> propertiesBO = new HashSet<>();
        for (final Map.Entry<Object, Object> entry : submissionPayloadProperties.entrySet()) {
            propertiesBO.add(new PropertyBO(entry.getKey().toString(), entry.getValue().toString()));
        }
        return propertiesBO;
    }

}
