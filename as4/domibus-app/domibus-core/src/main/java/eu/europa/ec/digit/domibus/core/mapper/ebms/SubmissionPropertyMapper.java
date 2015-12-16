package eu.europa.ec.digit.domibus.core.mapper.ebms;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.springframework.stereotype.Component;

import eu.domibus.submission.Submission;
import eu.europa.ec.digit.domibus.domain.domibus.PropertyBO;

@Component
public class SubmissionPropertyMapper {

    /* ---- Constants ---- */

    /* ---- Instance Variables ---- */

    /* ---- Constructors ---- */

    /* ---- Business Methods ---- */

    public Submission mapTo(Set<PropertyBO> propertiesBO, Submission submission) {

        for (PropertyBO propertyBO : propertiesBO) {
            submission.addMessageProperty(propertyBO.getName(), propertyBO.getValue());
        }
        return submission;
    }

    public Set<PropertyBO> mapFrom(Submission submission) {

        Set<PropertyBO> propertiesBO = new HashSet<>();
        for (final Map.Entry<Object, Object> submissionPropertyEntry : submission.getMessageProperties().entrySet()) {
            final PropertyBO propertyBO = new PropertyBO();
            propertyBO.setName(submissionPropertyEntry.getKey().toString());
            propertyBO.setValue(submissionPropertyEntry.getValue().toString());
            propertiesBO.add(propertyBO);
        }
        return propertiesBO;
    }

}
