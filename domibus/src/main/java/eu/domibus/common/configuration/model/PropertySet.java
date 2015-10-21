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
@XmlType(name = "", propOrder = "propertyRef")
@Entity
@Table(name = "TB_MESSAGE_PROPERTY_SET")
public class PropertySet extends AbstractBaseEntity {

    @XmlAttribute(name = "name")
    @Column(name = "NAME")
    protected String name;

    @XmlElement(required = true, name = "propertyRef")
    @Transient
    protected List<PropertyRef> propertyRef;


    @XmlTransient
    @ManyToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinTable(name = "TB_JOIN_PROPERTY_SET", joinColumns = @JoinColumn(name = "PROPERTY_FK"), inverseJoinColumns = @JoinColumn(name = "SET_FK"))
    private List<Property> properties;

    /**
     * Gets the value of the name property.
     *
     * @return possible object is
     * {@link String }
     */
    public String getName() {
        return this.name;
    }

    /**
     * Sets the value of the name property.
     *
     * @param value allowed object is
     *              {@link String }
     */
    public void setName(final String value) {
        this.name = value;
    }

    public void init(final Configuration configuration) {
        this.properties = new ArrayList<>();
        for (final PropertyRef ref : this.propertyRef) {
            for (final Property property : configuration.getBusinessProcesses().getProperties()) {
                if (ref.getProperty().equals(property.getName())) {
                    this.properties.add(property);
                    break;
                }
            }
        }
    }

    public List<Property> getProperties() {
        return this.properties;
    }
}
