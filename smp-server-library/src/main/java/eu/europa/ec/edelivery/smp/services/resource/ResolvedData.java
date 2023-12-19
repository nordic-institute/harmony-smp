/*-
 * #START_LICENSE#
 * smp-server-library
 * %%
 * Copyright (C) 2017 - 2023 European Commission | eDelivery | DomiSMP
 * %%
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by the European Commission - subsequent
 * versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy of the Licence at:
 * 
 * [PROJECT_HOME]\license\eupl-1.2\license.txt or https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 * 
 * Unless required by applicable law or agreed to in writing, software distributed under the Licence is
 * distributed on an "AS IS" basis, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the Licence for the specific language governing permissions and limitations under the Licence.
 * #END_LICENSE#
 */
package eu.europa.ec.edelivery.smp.services.resource;

import eu.europa.ec.edelivery.smp.data.model.DBDomain;
import eu.europa.ec.edelivery.smp.data.model.DBGroup;
import eu.europa.ec.edelivery.smp.data.model.doc.DBResource;
import eu.europa.ec.edelivery.smp.data.model.doc.DBSubresource;
import eu.europa.ec.edelivery.smp.data.model.ext.DBResourceDef;
import eu.europa.ec.edelivery.smp.data.model.ext.DBSubresourceDef;

public class ResolvedData {
    boolean resolved;
    DBDomain domain;
    DBGroup group;
    DBResourceDef resourceDef;
    DBSubresourceDef subResourceDef;
    DBResource resource;
    DBSubresource subresource;

    public DBDomain getDomain() {
        return domain;
    }

    public void setDomain(DBDomain domain) {
        this.domain = domain;
    }

    public DBResourceDef getResourceDef() {
        return resourceDef;
    }

    public void setResourceDef(DBResourceDef resourceDef) {
        this.resourceDef = resourceDef;
    }

    public DBSubresourceDef getSubResourceDef() {
        return subResourceDef;
    }

    public DBResource getResource() {
        return resource;
    }

    public void setResource(DBResource resource) {
        this.resource = resource;
    }

    public DBSubresource getSubresource() {
        return subresource;
    }

    public void setSubResourceDef(DBSubresourceDef subResourceDef) {
        this.subResourceDef = subResourceDef;
    }

    public void setSubresource(DBSubresource subresource) {
        this.subresource = subresource;
    }

    public boolean isResolved() {
        return resolved;
    }

    public void setResolved(boolean resolved) {
        this.resolved = resolved;
    }

    public DBGroup getGroup() {
        return group;
    }

    public void setGroup(DBGroup group) {
        this.group = group;
    }
}
