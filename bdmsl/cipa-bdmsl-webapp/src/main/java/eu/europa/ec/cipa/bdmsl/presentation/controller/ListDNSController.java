package eu.europa.ec.cipa.bdmsl.presentation.controller;

import eu.europa.ec.cipa.bdmsl.common.bo.CertificateDomainBO;
import eu.europa.ec.cipa.bdmsl.service.ICipaService;
import eu.europa.ec.cipa.bdmsl.service.dns.IDnsClientService;
import eu.europa.ec.cipa.common.exception.TechnicalException;
import eu.europa.ec.cipa.common.logging.ILoggingService;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.Predicate;
import org.apache.commons.collections.Transformer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.xbill.DNS.ARecord;
import org.xbill.DNS.CNAMERecord;
import org.xbill.DNS.NAPTRRecord;
import org.xbill.DNS.Record;

import java.util.*;

/**
 * Created by feriaad on 16/07/2015.
 */
@Controller
public class ListDNSController {

    @Autowired
    private IDnsClientService dnsClientService;

    @Autowired
    private ICipaService cipaService;

    @Autowired
    private ILoggingService loggingService;

    @Value("${dnsClient.enabled}")
    private String dnsEnabled;

    @Value("${dnsClient.server}")
    private String dnsServer;

    @RequestMapping("/listDNS")
    public String listDNS(Model model) {
        loggingService.debug("Calling listDNS...");
        Map<String, Collection<Record>> recordMap = new HashMap<>();
        boolean dnsClientEnabled = Boolean.parseBoolean(dnsEnabled);
        int numberOfRecords = 0;
        if (dnsClientEnabled) {
            model.addAttribute("dnsServer", dnsServer);
            try {
                Set<String> domainSet = new HashSet<>(CollectionUtils.collect(cipaService.findAll(), new Transformer() {
                    @Override
                    public Object transform(Object o) {
                        CertificateDomainBO domain = (CertificateDomainBO) o;
                        return domain.getDomain();
                    }
                }));

                for (String domain : domainSet) {
                    Collection records = CollectionUtils.select(dnsClientService.getAllRecords(domain), new Predicate() {
                        @Override
                        public boolean evaluate(Object record) {
                            return record instanceof ARecord || record instanceof CNAMERecord || record instanceof NAPTRRecord;
                        }
                    });
                    numberOfRecords += records.size();
                    recordMap.put(domain, records);
                }

            } catch (TechnicalException exc) {
                loggingService.error("Error during the ListDNS call", exc);
            }
        }
        model.addAttribute("dnsEnabled", dnsClientEnabled);
        model.addAttribute("recordMap", recordMap);
        model.addAttribute("numberOfRecords", numberOfRecords);
        loggingService.debug("listDNS Done!");
        return "listDNS";
    }
}

