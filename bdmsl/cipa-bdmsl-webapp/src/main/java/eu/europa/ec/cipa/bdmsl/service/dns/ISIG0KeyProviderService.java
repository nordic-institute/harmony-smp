package eu.europa.ec.cipa.bdmsl.service.dns;

import eu.europa.ec.cipa.common.exception.TechnicalException;
import org.xbill.DNS.KEYRecord;

import java.security.PrivateKey;

/**
 * Created by feriaad on 12/06/2015.
 */
public interface ISIG0KeyProviderService {
    PrivateKey getPrivateSIG0Key () throws TechnicalException;

    KEYRecord getSIG0Record() throws TechnicalException;
}
