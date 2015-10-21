package eu.europa.ec.cipa.bdmsl.service.dns.impl;

import eu.europa.ec.cipa.bdmsl.common.exception.DNSClientException;
import eu.europa.ec.cipa.bdmsl.service.dns.IDnsMessageSenderService;
import eu.europa.ec.cipa.common.exception.TechnicalException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.xbill.DNS.Message;
import org.xbill.DNS.SimpleResolver;

import java.io.IOException;
import java.net.UnknownHostException;

/**
 * Created by feriaad on 24/06/2015.
 */
@Service
public class DnsMessageSenderServiceImpl implements IDnsMessageSenderService {

    @Value("${dnsClient.server}")
    private String dnsServer;

    @Override
    public Message sendMessage(Message message) throws TechnicalException {
        SimpleResolver res = null;
        try {
            res = new SimpleResolver(dnsServer);
        } catch (UnknownHostException exc) {
            throw new DNSClientException("Invalid DNS server : '" + dnsServer + "'", exc);
        }
        res.setTCP(true);
       try {
            return res.send(message);
        } catch (IOException exc) {
            throw new DNSClientException("Couldn't send the message to the DNS server", exc);
        }
    }
}
