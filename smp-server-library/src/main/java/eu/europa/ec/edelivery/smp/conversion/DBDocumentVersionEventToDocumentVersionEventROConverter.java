package eu.europa.ec.edelivery.smp.conversion;


import eu.europa.ec.edelivery.smp.data.model.DBDomain;
import eu.europa.ec.edelivery.smp.data.model.doc.DBDocumentVersionEvent;
import eu.europa.ec.edelivery.smp.data.ui.DocumentVersionEventRO;
import eu.europa.ec.edelivery.smp.logging.SMPLogger;
import eu.europa.ec.edelivery.smp.logging.SMPLoggerFactory;
import org.apache.commons.beanutils.BeanUtils;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

import java.lang.reflect.InvocationTargetException;

/**
 * Converter for domain DAO entity {@link DBDomain} to
 * enriched webservice object {@link eu.europa.ec.edelivery.smp.data.ui.DomainPublicRO}.
 *
 * @author Joze Rihtarsic
 * @since 5.1
 */
@Component
public class DBDocumentVersionEventToDocumentVersionEventROConverter
        implements Converter<DBDocumentVersionEvent, DocumentVersionEventRO> {
    private static final SMPLogger LOG = SMPLoggerFactory.getLogger(DBDocumentVersionEventToDocumentVersionEventROConverter.class);

    @Override
    public DocumentVersionEventRO convert(DBDocumentVersionEvent source) {

        if (source == null) {
            return null;
        }
        DocumentVersionEventRO target = new DocumentVersionEventRO();
        try {
            BeanUtils.copyProperties(target, source);
            target.setDocumentVersionStatus(source.getStatus());
        } catch (IllegalAccessException | InvocationTargetException e) {
            LOG.error("Error occurred while converting DBDomain", e);
            return null;
        }
        return target;
    }

}
