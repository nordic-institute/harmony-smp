package eu.europa.ec.edelivery.smp.auth.jwt;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.interfaces.EdECPublicKey;
import java.security.spec.InvalidKeySpecException;

import static org.junit.jupiter.api.Assertions.*;

class SMPJwtConfigTest {

    @ParameterizedTest
    @CsvSource({
            "Key alg: RSA,MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAuOBK4kDLk2OY+wh0ezvF2O+34g6vT2vwtjFMBw/nz/m3QOrU3Kg25itVAyS7kaszbHtdGAxP4xsT9Alq/Z+/GSIbbvCrMTpDgajQ/fC0Vj6UzytVkomWhGLgzG3BxT9CDLCTGWSc28Z909ZdDc/f4X0mYE8GRvHjcLmybP2Q24beC6LGSOGrVtZacbajFX67cbtcG/DJuX5iodAV2r2AmEzgT3Z7SXVBd9qmzk6hKRFgm06h2gMIaPJofd/iWlhyI/r/iCE7G7puJB13eiDkGUcWG270Zh71HtdImCl8SWGmsZjWwKXdR52FDniwDHKwp19RUuLMjY2oFWqVNow+5QIDAQAB,RSA, rs256",
            "Key alg: EC,MFkwEwYHKoZIzj0CAQYIKoZIzj0DAQcDQgAEeujq1i6QztHeJTVllFmR+uChnVwxUhAQv7ehZvShsPg//Mk9Rgtr+rr3ELtcvJZd/mgmYiQlgMTwud6Vd4/UkQ==, EC, ES256",
            "Key alg: Ed25519, MCowBQYDK2VwAyEAJQUoAZs7F8INql8oKpyrfBntIamUqRBdutFuRB7zc1M=,EdDSA, EdDSA",
            "Key alg: Ed448, MEMwBQYDK2VxAzoAItQA6ZhjqddqzTcoMBVL9+HZQGvF0o2yePQEfXFKjN+qoWcJyXG7nCuFFMbX5BrdvQEBi3W5oGWA,EdDSA, EdDSA"
    })
    void testGetPublicKey(String testDesc, String pemKey, String keyAlgorithm, String  jwtSigAlg) throws NoSuchAlgorithmException, InvalidKeySpecException {
        System.out.println(testDesc);
        PublicKey key = SMPJwtConfig. loadPublicKey(pemKey, jwtSigAlg);

        assertNotNull(key);
        assertEquals(keyAlgorithm, key.getAlgorithm());
    }
}