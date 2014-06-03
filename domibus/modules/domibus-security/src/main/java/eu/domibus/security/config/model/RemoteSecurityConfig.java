package eu.domibus.security.config.model;

import org.apache.neethi.Policy;
import eu.domibus.security.config.generated.PublicKeystore;
import eu.domibus.security.config.generated.Security;

public class RemoteSecurityConfig {
    private final Security security;
    private final PublicKeystore publicKeystore;
    private final Policy policy;

    public RemoteSecurityConfig(final Security security, final PublicKeystore publicKeystore, final Policy policy) {
        super();
        this.security = security;
        this.publicKeystore = publicKeystore;
        this.policy = policy;
    }

    public Security getSecurity() {
        return security;
    }

    public PublicKeystore getPublicKeystore() {
        return publicKeystore;
    }

    public Policy getPolicy() {
        return policy;
    }

}
