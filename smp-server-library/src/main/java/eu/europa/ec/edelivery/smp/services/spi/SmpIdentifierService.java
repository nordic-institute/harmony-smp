package eu.europa.ec.edelivery.smp.services.spi;

import eu.europa.ec.edelivery.smp.conversion.IdentifierService;
import eu.europa.ec.edelivery.smp.identifiers.Identifier;
import eu.europa.ec.edelivery.smp.logging.SMPLogger;
import eu.europa.ec.edelivery.smp.logging.SMPLoggerFactory;
import eu.europa.ec.edelivery.smp.services.ConfigurationService;
import eu.europa.ec.smp.spi.api.SmpIdentifierServiceApi;
import eu.europa.ec.smp.spi.api.model.ResourceIdentifier;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.util.regex.Pattern;

/**
 * Implementation of the class provides the identifier services for the SPI implementation. The identifier formatting
 * is DomiSMP configuration specific!
 *
 * @author Joze Rihtarsic
 * @since 5.0
 */
@Service
public class SmpIdentifierService implements SmpIdentifierServiceApi {
    private static final SMPLogger LOG = SMPLoggerFactory.getLogger(SmpIdentifierService.class);

    final IdentifierService identifierService;
    final ConfigurationService configurationService;

    public SmpIdentifierService(IdentifierService identifierService, ConfigurationService configurationService) {
        this.identifierService = identifierService;
        this.configurationService = configurationService;
    }

    @Override
    public ResourceIdentifier normalizeResourceIdentifier(String value, String scheme) {
        return toUrlIdentifier(identifierService.normalizeParticipant(scheme, value));
    }

    @Override
    public ResourceIdentifier normalizeSubresourceIdentifier(String value, String scheme) {
        return toUrlIdentifier(identifierService.normalizeDocument(scheme, value));
    }

    @Override
    public String formatResourceIdentifier(ResourceIdentifier identifier) {
        if (identifier == null) {
            return null;
        }
        Identifier id = toIdentifier(identifier);
        return identifierService.formatParticipant(id);
    }

    @Override
    public String formatSubresourceIdentifier(ResourceIdentifier identifier) {
        if (identifier == null) {
            return null;
        }
        Identifier id = toIdentifier(identifier);
        return identifierService.formatDocument(id);
    }

    @Override
    public String getURLEncodedResourceIdentifier(ResourceIdentifier identifier) {
        if (identifier == null) {
            return null;
        }
        Identifier id = toIdentifier(identifier);
        return identifierService.urlEncodedFormatParticipant(id);
    }

    @Override
    public String getURLEncodedSubresourceIdentifier(ResourceIdentifier identifier) {
        if (identifier == null) {
            return null;
        }
        Identifier id = toIdentifier(identifier);
        return identifierService.urlEncodedFormatDocument(id);
    }

    @Override
    public boolean concatenateResourceIdentifier(ResourceIdentifier identifier) {
        Pattern concatenatePartyId = configurationService.getParticipantIdentifierUrnValidationRexExp();
        if (identifier == null || StringUtils.isBlank(identifier.getScheme())) {
            LOG.debug("Return false for null or empty scheme identifier!");
            return false;
        }

        return concatenatePartyId != null
                && concatenatePartyId.matcher(identifier.getScheme())
                .matches();
    }

    private Identifier toIdentifier(ResourceIdentifier identifier) {
        return identifier == null ? null :
                new Identifier(identifier.getValue(), identifier.getScheme());
    }

    private ResourceIdentifier toUrlIdentifier(Identifier identifier) {
        return identifier == null ? null :
                new ResourceIdentifier(identifier.getValue(), identifier.getScheme());
    }

}
