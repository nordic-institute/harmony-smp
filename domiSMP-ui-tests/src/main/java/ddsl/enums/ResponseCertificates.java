package ddsl.enums;

import java.util.Random;

public enum ResponseCertificates {
    SMP_DOMAIN_02("smp_domain_02 (CN=smp_domain_02,O=digit,C=eu:000000006443d987)"),
    SMP_EDDSA_448("smp_eddsa_448 (CN=smp_eddsa_448,O=digit,C=eu:000000006443fcba)"),
    SMP_ECDSA_NIST_B409("smp_ecdsa_nist-b409 (CN=smp_ecdsa_nist-b409,O=digit,C=eu:000000006443fd57)"),
    SMP_DOMAIN_01("smp_domain_01 (CN=smp_domain_01,O=digit,C=eu:000000006443d8a8)"),
    SAMPLE_KEY("sample_key (CN=demo-smp-signing-key,O=digit,C=eu:000000006443f9bc)"),
    SMP_EDDSA_25519("smp_eddsa_25519 (CN=smp_eddsa_25519,O=digit,C=eu:000000006443d95d)");

    public final String name;


    ResponseCertificates(String name) {
        this.name = name;
    }

    public static String getRandomCertificate() {
        ResponseCertificates[] certificates = values();
        int size = certificates.length;
        Random random = new Random();
        int index = random.nextInt(size);
        return certificates[index].name;
    }

    public static String getRandomCertificateWithSML() {
        ResponseCertificates[] certificates = {SMP_DOMAIN_01, SMP_DOMAIN_02};
        Random random = new Random();
        int index = random.nextInt(2);
        return certificates[index].name;
    }

}
