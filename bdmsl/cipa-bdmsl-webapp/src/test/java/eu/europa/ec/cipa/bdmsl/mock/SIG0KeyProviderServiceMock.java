package eu.europa.ec.cipa.bdmsl.mock;

import eu.europa.ec.cipa.bdmsl.common.exception.GenericTechnicalException;
import eu.europa.ec.cipa.bdmsl.service.dns.ISIG0KeyProviderService;
import eu.europa.ec.cipa.common.exception.TechnicalException;
import org.springframework.stereotype.Component;
import org.xbill.DNS.DClass;
import org.xbill.DNS.DNSSEC;
import org.xbill.DNS.KEYRecord;
import org.xbill.DNS.Name;

import java.security.*;

/**
 * Created by feriaad on 30/06/2015.
 */
@Component
public class SIG0KeyProviderServiceMock implements ISIG0KeyProviderService {

    private PrivateKey privateKey;
    private PublicKey publicKey;

    public SIG0KeyProviderServiceMock() throws NoSuchAlgorithmException {
        KeyPairGenerator keyGen = KeyPairGenerator.getInstance("RSA");
        keyGen.initialize(1024);
        KeyPair key = keyGen.generateKeyPair();
        privateKey = key.getPrivate();
        publicKey = key.getPublic();
    }

    @Override
    public PrivateKey getPrivateSIG0Key() throws TechnicalException {
        return privateKey;
    }

    @Override
    public KEYRecord getSIG0Record() throws TechnicalException {
        try {
            return new KEYRecord(Name.root, DClass.ANY, 0, 0, 10, DNSSEC.Algorithm.RSAMD5, publicKey);
        } catch(Exception exc) {
            throw new GenericTechnicalException("Problem in SIG0KeyProviderServiceMock", exc);
        }
    }
}
