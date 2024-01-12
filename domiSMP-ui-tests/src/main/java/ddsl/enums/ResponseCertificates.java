package ddsl.enums;

/**
 * Enumeration of data for provided certificates in the test truststure.
 * This is used to select the correct certificate in the UI.
 */
public enum ResponseCertificates {

    SAMPLE_KEY("sample_key", "sample_key (CN=demo-smp-signing-key,O=digit,C=eu:000000006443f9bc)"),
    SMP_DOMAIN_01("smp_domain_01", "smp_domain_01 (CN=smp_domain_01,O=digit,C=eu:000000006443d8a8)"),
    SMP_DOMAIN_02("smp_domain_02", "smp_domain_02 (CN=smp_domain_02,O=digit,C=eu:000000006443d987)"),
    SMP_ECDSA_NIST_B409("smp_ecdsa_nist-b409", "smp_ecdsa_nist-b409 (CN=smp_ecdsa_nist-b409,O=digit,C=eu:000000006443fd57)"),
    SMP_EDDSA_448("smp_eddsa_448", "smp_eddsa_448 (CN=smp_eddsa_448,O=digit,C=eu:000000006443fcba)"),
    SMP_EDDSA_25519("smp_eddsa_25519", "smp_eddsa_25519 (CN=smp_eddsa_25519,O=digit,C=eu:000000006443d95d)");


    private final String text;
    private final String alias;

    ResponseCertificates(String alias, String text) {
        this.alias = alias;
        this.text = text;
    }

    public String getText() {
        return text;
    }

    public String getAlias() {
        return alias;
    }

    public static ResponseCertificates getByAlias(String alias) {
        for (ResponseCertificates e : values()) {
            if (e.getAlias().equals(alias)) {
                return e;
            }
        }
        return null;
    }

    public static String getTextForAlias(String alias) {
        ResponseCertificates certificates = getByAlias(alias);

        return certificates != null ? certificates.getText() : null;
    }
}
