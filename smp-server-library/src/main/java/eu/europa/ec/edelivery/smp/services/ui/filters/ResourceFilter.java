package eu.europa.ec.edelivery.smp.services.ui.filters;

import eu.europa.ec.edelivery.smp.data.model.DBDomain;
import eu.europa.ec.edelivery.smp.data.model.user.DBUser;

public class ResourceFilter {
    private String identifierValue;
    private String identifierScheme;

    private DBUser owner;
    private DBDomain domain;

    public String getIdentifierValueLike() {
        return identifierValue;
    }

    public void setIdentifierValueLike(String participantIdentifier) {
        this.identifierValue = participantIdentifier;
    }

    public String getIdentifierSchemeLike() {
        return identifierScheme;
    }

    public void setIdentifierSchemeLike(String participantScheme) {
        this.identifierScheme = participantScheme;
    }

    public DBUser getOwner() {
        return owner;
    }

    public void setOwner(DBUser owner) {
        this.owner = owner;
    }

    public DBDomain getDomain() {
        return domain;
    }

    public void setDomain(DBDomain domain) {
        this.domain = domain;
    }
}
