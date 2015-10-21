package eu.europa.ec.cipa.bdmsl.service.dns;

import eu.europa.ec.cipa.common.exception.TechnicalException;
import org.xbill.DNS.Message;

/**
 * Created by feriaad on 12/06/2015.
 */
public interface IDnsMessageSenderService {
    Message sendMessage(Message message) throws TechnicalException;
}
