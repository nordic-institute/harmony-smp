/*
 * Copyright 2015 e-CODEX Project
 *
 * Licensed under the EUPL, Version 1.1 or – as soon they
 * will be approved by the European Commission - subsequent
 * versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the
 * Licence.
 * You may obtain a copy of the Licence at:
 * http://ec.europa.eu/idabc/eupl5
 * Unless required by applicable law or agreed to in
 * writing, software distributed under the Licence is
 * distributed on an "AS IS" basis,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either
 * express or implied.
 * See the Licence for the specific language governing
 * permissions and limitations under the Licence.
 */

package eu.domibus.common.configuration.model;

import eu.domibus.common.model.AbstractBaseEntity;

import javax.persistence.*;
import javax.xml.bind.annotation.*;
import java.util.ArrayList;
import java.util.List;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
        "rolesXml",
        "partiesXml",
        "mepsXml",
        "propertiesXml",
        "payloadProfilesXml",
        "errorHandlingsXml",
        "securitiesXml",
        "agreementsXml",
        "servicesXml",
        "actionsXml",
        "as4Xml",
        "legConfigurationsXml",
        "processes"
})
@Entity
@Table(name = "TB_BUSINESS_PROCESS")
public class BusinessProcesses extends AbstractBaseEntity {

    @XmlElement(required = true, name = "roles")
    @Transient
    protected Roles rolesXml;
    @XmlElement(required = true, name = "parties")
    @Transient
    protected Parties partiesXml;
    @XmlElement(required = true, name = "meps")
    @Transient
    protected Meps mepsXml;
    @XmlElement(name = "properties")
    @Transient
    protected Properties propertiesXml;
    @XmlElement(required = true, name = "payloadProfiles")
    @Transient
    protected PayloadProfiles payloadProfilesXml;
    @XmlElement(required = true, name = "errorHandlings")
    @Transient
    protected ErrorHandlings errorHandlingsXml;
    @XmlElement(required = true, name = "agreements")
    @Transient
    protected Agreements agreementsXml;
    @XmlElement(required = true, name = "services")
    @Transient
    protected Services servicesXml;
    @XmlElement(required = true, name = "actions")
    @Transient
    protected Actions actionsXml;
    @XmlElement(required = true, name = "as4")
    @Transient
    protected As4 as4Xml;
    @XmlElement(required = true, name = "securities")
    @Transient
    protected Securities securitiesXml;
    @XmlElement(required = true, name = "legConfigurations")
    @Transient
    protected LegConfigurations legConfigurationsXml;

    @XmlElement(required = true, name = "process")
    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinColumn(name = "FK_BUSINESSPROCESS")
    protected List<Process> processes;


    @XmlTransient
    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinColumn(name = "FK_BUSINESSPROCESS")
    private List<Role> roles;
    @XmlTransient
    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinColumn(name = "FK_BUSINESSPROCESS")
    private List<PartyIdType> partyIdTypes;
    @XmlTransient
    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinColumn(name = "FK_BUSINESSPROCESS")
    private List<Party> parties;
    @XmlTransient
    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinColumn(name = "FK_BUSINESSPROCESS")
    private List<Binding> mepBindings;
    @XmlTransient
    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinColumn(name = "FK_BUSINESSPROCESS")
    private List<Mep> meps;
    @XmlTransient
    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinColumn(name = "FK_BUSINESSPROCESS")
    private List<Property> properties;
    @XmlTransient
    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinColumn(name = "FK_BUSINESSPROCESS")
    private List<PropertySet> propertySets;
    @XmlTransient
    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinColumn(name = "FK_BUSINESSPROCESS")
    private List<PayloadProfile> payloadProfiles;
    @XmlTransient
    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinColumn(name = "FK_BUSINESSPROCESS")
    private List<Payload> payloads;
    @XmlTransient
    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinColumn(name = "FK_BUSINESSPROCESS")
    private List<ErrorHandling> errorHandlings;
    @XmlTransient
    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinColumn(name = "FK_BUSINESSPROCESS")
    private List<Agreement> agreements;
    @XmlTransient
    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinColumn(name = "FK_BUSINESSPROCESS")
    private List<Service> services;
    @XmlTransient
    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinColumn(name = "FK_BUSINESSPROCESS")
    private List<Action> actions;
    @XmlTransient
    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinColumn(name = "FK_BUSINESSPROCESS")
    private List<ReceptionAwareness> as4ConfigReceptionAwareness;
    @XmlTransient
    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinColumn(name = "FK_BUSINESSPROCESS")
    private List<Reliability> as4Reliability;
    @XmlTransient
    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinColumn(name = "FK_BUSINESSPROCESS")
    private List<LegConfiguration> legConfigurations;
    @XmlTransient
    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinColumn(name = "FK_BUSINESSPROCESS")
    private List<Security> securities;

    public List<PartyIdType> getPartyIdTypes() {
        return this.partyIdTypes;
    }

    public void setPartyIdTypes(final List<PartyIdType> partyIdTypes) {
        this.partyIdTypes = partyIdTypes;
    }

    public List<Reliability> getAs4Reliability() {
        return this.as4Reliability;
    }

    public void setAs4Reliability(final List<Reliability> as4Reliability) {
        this.as4Reliability = as4Reliability;
    }

    public List<Binding> getMepBindings() {
        return this.mepBindings;
    }

    public void setMepBindings(final List<Binding> mepBindings) {
        this.mepBindings = mepBindings;
    }

    public List<Mep> getMeps() {
        return this.meps;
    }

    public void setMeps(final List<Mep> meps) {
        this.meps = meps;
    }

    public List<PropertySet> getPropertySets() {
        return this.propertySets;
    }

    public void setPropertySets(final List<PropertySet> propertySets) {
        this.propertySets = propertySets;
    }

    public List<Payload> getPayloads() {
        return this.payloads;
    }

    public void setPayloads(final List<Payload> payloads) {
        this.payloads = payloads;
    }

    public List<ReceptionAwareness> getAs4ConfigReceptionAwareness() {
        return this.as4ConfigReceptionAwareness;
    }

    public void setAs4ConfigReceptionAwareness(final List<ReceptionAwareness> as4ConfigReceptionAwareness) {
        this.as4ConfigReceptionAwareness = as4ConfigReceptionAwareness;
    }

    public List<Security> getSecurities() {
        return this.securities;
    }

    public void setSecurities(final List<Security> securities) {
        this.securities = securities;
    }

    void init(final Configuration configuration) {
        for (final Role role : this.rolesXml.getRole()) {
            role.init(configuration);
        }
        this.roles = this.rolesXml.getRole();
        for (final PartyIdType partyIdType : this.partiesXml.getPartyIdTypes().getPartyIdType()) {
            partyIdType.init(configuration);
        }
        this.partyIdTypes = this.partiesXml.getPartyIdTypes().getPartyIdType();
        for (final Party party : this.partiesXml.getParty()) {
            party.init(configuration);
        }
        this.parties = this.partiesXml.getParty();
        for (final Binding binding : this.mepsXml.getBinding()) {
            binding.init(configuration);
        }
        this.mepBindings = this.mepsXml.getBinding();
        for (final Mep mep : this.mepsXml.getMep()) {
            mep.init(configuration);
        }
        this.meps = this.mepsXml.getMep();
        for (final Property property : this.propertiesXml.getProperty()) {
            property.init(configuration);
        }
        this.properties = this.propertiesXml.getProperty();
        for (final PropertySet propertySet : this.propertiesXml.getPropertySet()) {
            propertySet.init(configuration);
        }
        this.propertySets = this.propertiesXml.getPropertySet();
        for (final Payload payload : this.payloadProfilesXml.getPayload()) {
            payload.init(configuration);
        }
        this.payloads = this.payloadProfilesXml.getPayload();
        for (final PayloadProfile payloadProfile : this.payloadProfilesXml.getPayloadProfile()) {
            payloadProfile.init(configuration);
        }
        this.payloadProfiles = this.payloadProfilesXml.getPayloadProfile();
        for (final ErrorHandling errorHandling : this.errorHandlingsXml.getErrorHandling()) {
            errorHandling.init(configuration);
        }
        this.errorHandlings = this.errorHandlingsXml.getErrorHandling();
        for (final Agreement agreement : this.agreementsXml.agreement) {
            agreement.init(configuration);
        }
        this.agreements = this.agreementsXml.getAgreement();
        for (final Service service : this.servicesXml.getService()) {
            service.init(configuration);
        }
        this.services = this.servicesXml.getService();
        for (final Action action : this.actionsXml.getAction()) {
            action.init(configuration);
        }
        this.actions = this.actionsXml.getAction();
        for (final ReceptionAwareness receptionAwareness : this.as4Xml.getReceptionAwareness()) {
            receptionAwareness.init(configuration);
        }
        this.as4ConfigReceptionAwareness = this.as4Xml.getReceptionAwareness();
        for (final Reliability reliability : this.as4Xml.getReliability()) {
            reliability.init(configuration);
        }
        this.as4Reliability = this.as4Xml.getReliability();

        for (final Security security : this.securitiesXml.getSecurity()) {
            security.init(configuration);
        }
        this.securities = this.securitiesXml.getSecurity();

        for (final LegConfiguration legConfiguration : this.legConfigurationsXml.getLegConfiguration()) {
            legConfiguration.init(configuration);
        }
        this.legConfigurations = this.legConfigurationsXml.getLegConfiguration();

        for (final Process process : this.processes) {
            process.init(configuration);
        }
    }

    public List<Role> getRoles() {
        return this.roles;
    }

    public void setRoles(final List<Role> roles) {
        this.roles = roles;
    }

    public List<Party> getParties() {
        return this.parties;
    }

    public void setParties(final List<Party> parties) {
        this.parties = parties;
    }

    public List<Property> getProperties() {
        return this.properties;
    }

    public void setProperties(final List<Property> properties) {
        this.properties = properties;
    }

    public List<PayloadProfile> getPayloadProfiles() {
        return this.payloadProfiles;
    }

    public void setPayloadProfiles(final List<PayloadProfile> payloadProfiles) {
        this.payloadProfiles = payloadProfiles;
    }

    public List<ErrorHandling> getErrorHandlings() {
        return this.errorHandlings;
    }

    public void setErrorHandlings(final List<ErrorHandling> errorHandlings) {
        this.errorHandlings = errorHandlings;
    }

    public List<Agreement> getAgreements() {
        return this.agreements;
    }

    public void setAgreements(final List<Agreement> agreements) {
        this.agreements = agreements;
    }

    public List<Service> getServices() {
        return this.services;
    }

    public void setServices(final List<Service> services) {
        this.services = services;
    }

    public List<Action> getActions() {
        return this.actions;
    }

    public void setActions(final List<Action> actions) {
        this.actions = actions;
    }


    public List<LegConfiguration> getLegConfigurations() {
        return this.legConfigurations;
    }

    public void setLegConfigurations(final List<LegConfiguration> legConfigurations) {
        this.legConfigurations = legConfigurations;
    }

    public List<Process> getProcesses() {
        if (this.processes == null) {
            this.processes = new ArrayList<>();
        }
        return this.processes;
    }

}
