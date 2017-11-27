/*
 * Copyright 2017 European Commission | CEF eDelivery
 *
 * Licensed under the EUPL, Version 1.1 or – as soon they will be approved by the European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 *
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/software/page/eupl
 * or file: LICENCE-EUPL-v1.1.pdf
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the Licence is distributed on an "AS IS" basis,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the Licence for the specific language governing permissions and limitations under the Licence.
 */

package eu.europa.ec.edelivery.smp.data.model;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Table(name = "smp_service_metadata")
public class DBServiceMetadata implements Serializable {

    private DBServiceMetadataId serviceMetadataId;
    private DBServiceGroup serviceGroup;
    private String xmlContent;

    public DBServiceMetadata() {  }

    public DBServiceMetadata(DBServiceMetadataId serviceMetadataId, DBServiceGroup serviceGroup) {
        this(serviceMetadataId, serviceGroup, null);
    }

    public DBServiceMetadata(DBServiceMetadataId serviceMetadataId,
                             DBServiceGroup serviceGroup,
                             String xmlContent) {
        this.serviceMetadataId = serviceMetadataId;
        this.serviceGroup = serviceGroup;
        this.xmlContent = xmlContent;
    }

    @EmbeddedId
    public DBServiceMetadataId getId() {
        return serviceMetadataId;
    }

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumns({@JoinColumn(name = "businessIdentifier",
            referencedColumnName = "businessIdentifier",
            nullable = false,
            insertable = false,
            updatable = false),
            @JoinColumn(name = "businessIdentifierScheme",
                    referencedColumnName = "businessIdentifierScheme",
                    nullable = false,
                    insertable = false,
                    updatable = false)})
    public DBServiceGroup getServiceGroup() {
        return serviceGroup;
    }

    @Lob
    @Column(name = "xmlcontent")
    public String getXmlContent() {
        return xmlContent;
    }

    public void setId(final DBServiceMetadataId serviceMetadataId) {
        this.serviceMetadataId = serviceMetadataId;
    }

    public void setServiceGroup(final DBServiceGroup serviceGroup) {
        this.serviceGroup = serviceGroup;
    }

    public void setXmlContent(String xmlContent) {
        this.xmlContent = xmlContent;
    }

}
