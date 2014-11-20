package eu.domibus.security.config.model;

import eu.domibus.security.config.generated.PublicKeystore;
import eu.domibus.security.config.generated.Security;
import org.apache.neethi.Policy;

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
        return this.security;
    }

    public PublicKeystore getPublicKeystore() {
        return this.publicKeystore;
    }

    public Policy getPolicy() {
        return this.policy;
    }

}
