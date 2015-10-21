package eu.europa.ec.cipa.bdmsl.service.dns.impl;

import com.google.common.base.Strings;
import eu.europa.ec.cipa.bdmsl.common.exception.SIG0Exception;
import eu.europa.ec.cipa.bdmsl.service.dns.ISIG0KeyProviderService;
import eu.europa.ec.cipa.common.exception.TechnicalException;
import eu.europa.ec.cipa.common.logging.ILoggingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.xbill.DNS.*;
import org.xbill.DNS.utils.base64;

import java.io.*;
import java.math.BigInteger;
import java.net.UnknownHostException;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.spec.DSAPrivateKeySpec;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;

/**
 * Created by feriaad on 24/06/2015.
 */
@Service
public class SIG0KeyProviderServiceImpl implements ISIG0KeyProviderService {

    @Autowired
    private ILoggingService loggingService;

    private PrivateKey privateKey = null;

    private static String[] lineStarts = new String[]{"Subprime(q)", "Prime(p)", "Base(g)", "Private_value(x)", "Public_value(y)"};

    @Value("${dnsClient.SIG0KeyFileName}")
    private String dnsClientSIG0KeyFileName;

    @Value("${dnsClient.SIG0PublicKeyName}")
    private String sig0PublicKeyName;

    @Value("${dnsClient.server}")
    private String dnsServer;

    @Value("${configurationDir}")
    private String configurationDir;

    private KEYRecord sig0Record;

    @Override
    public PrivateKey getPrivateSIG0Key() throws TechnicalException {
        if (privateKey == null) {
            KeyFactory keyFactory;
            try {
                keyFactory = KeyFactory.getInstance("DSA");
            } catch (NoSuchAlgorithmException e) {
                throw new SIG0Exception("Can not initiate the key factory with the DSA algorithm", e);
            }
            KeySpec privateKeySpec;

            if (!configurationDir.endsWith("/")) {
                configurationDir += "/";
            }

            InputStream stream;
            try {
                stream = new FileInputStream(configurationDir + dnsClientSIG0KeyFileName);
            } catch (FileNotFoundException exc) {

                stream = this.getClass().getResourceAsStream(dnsClientSIG0KeyFileName);
                if (stream == null) {
                    stream = ClassLoader.getSystemClassLoader().getResourceAsStream(dnsClientSIG0KeyFileName);
                    if (stream == null) {
                        stream = Thread.currentThread().getContextClassLoader().getResourceAsStream(dnsClientSIG0KeyFileName);
                    }
                }
            }
            if (stream == null){
                throw new SIG0Exception("Can not get the SIG0 private key");
            }
            privateKeySpec = readDSAPrivateKey(stream);
            try {
                privateKey = keyFactory.generatePrivate(privateKeySpec);
            } catch (InvalidKeySpecException exc) {
                throw new SIG0Exception("Can not get the SIG0 private key", exc);
            }
        }
        return privateKey;
    }

    private DSAPrivateKeySpec readDSAPrivateKey(final InputStream inputs) throws TechnicalException {
        final BigInteger[] values = new BigInteger[6];

        try {
            final BufferedReader in = new BufferedReader(new InputStreamReader(inputs));
            in.readLine(); // skip header
            String[] parts;
            for (int i = 0; i < values.length; ++i) {
                parts = in.readLine().split(": ");
                for (final String s : lineStarts) {
                    if (parts[0].equals(s)) {
                        try {
                            final byte[] data = base64.fromString(parts[1].trim().replace("\t", ""));
                            values[i] = new BigInteger(1, data);
                        } catch (final NumberFormatException ex) {
                            loggingService.warn(ex.getLocalizedMessage());
                            values[i] = new BigInteger("0");
                        } catch (final IndexOutOfBoundsException e) {
                            loggingService.warn(e.getLocalizedMessage());
                            values[i] = new BigInteger("0");
                        }
                    }
                }
            }
        } catch (final IOException ioe) {
            throw new SIG0Exception("I/O exception when trying to read the private key", ioe);
        }
        return new DSAPrivateKeySpec(values[4], values[1], values[2], values[3]);
    }

    public KEYRecord getSIG0Record() throws TechnicalException {
        if (sig0Record == null) {
            if (!Strings.isNullOrEmpty(sig0PublicKeyName)) {
                Lookup aLookup;
                try {
                    aLookup = new Lookup(sig0PublicKeyName, Type.KEY);
                } catch (TextParseException e) {
                    throw new SIG0Exception("Can not get the SIG0 record from the SIG0 key " + sig0PublicKeyName, e);
                }
                SimpleResolver res;
                try {
                    res = new SimpleResolver(dnsServer);
                } catch (UnknownHostException e) {
                    throw new SIG0Exception("Can not instantiate the resolver for dns server " + dnsServer, e);
                }
                res.setTCP(true);
                aLookup.setResolver(res);
                aLookup.setCache(null);
                Record[] aRecords = aLookup.run();
                for (Record rec : aRecords) {
                    if (rec.getType() == Type.KEY) {
                        sig0Record = (KEYRecord) rec;
                    }
                }
            }
        }
        return sig0Record;
    }
}
