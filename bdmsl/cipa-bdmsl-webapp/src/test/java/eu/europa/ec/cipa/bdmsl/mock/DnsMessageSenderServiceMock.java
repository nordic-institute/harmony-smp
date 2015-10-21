package eu.europa.ec.cipa.bdmsl.mock;

import eu.europa.ec.cipa.bdmsl.service.dns.IDnsMessageSenderService;
import eu.europa.ec.cipa.common.exception.TechnicalException;
import org.springframework.stereotype.Service;
import org.xbill.DNS.Header;
import org.xbill.DNS.Message;
import org.xbill.DNS.Rcode;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by feriaad on 22/06/2015.
 */
@Service
public class DnsMessageSenderServiceMock implements IDnsMessageSenderService {

    private List<Message> messages = new ArrayList<>();

    @Override
    public Message sendMessage(Message message) throws TechnicalException {
        Message response = new Message();
        Header header = new Header();
        header.setRcode(Rcode.NOERROR);
        messages.add(message);
        return response;
    }

    public void reset() throws TechnicalException {
        messages.clear();
    }

    public String getMessages() {
        String result = "";
       for (Message message : messages) {
           result += message + "\n";
       }
        return result;
    }
}
