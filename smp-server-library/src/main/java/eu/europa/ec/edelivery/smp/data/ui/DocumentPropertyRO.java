/*-
 * #START_LICENSE#
 * smp-server-library
 * %%
 * Copyright (C) 2017 - 2024 European Commission | eDelivery | DomiSMP
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
package eu.europa.ec.edelivery.smp.data.ui;


import eu.europa.ec.edelivery.smp.config.enums.SMPPropertyTypeEnum;

/**
 * Document property contains values for updating the document variables. The
 * properties are used with document templates such are ${my.property.name}.
 *
 * @author Joze Rihtarsic
 * @since 5.1
 */
public class DocumentPropertyRO extends BaseRO {

    private static final long serialVersionUID = 9008583888835630036L;
    private String property;
    private String value;
    private String desc;
    private SMPPropertyTypeEnum type = SMPPropertyTypeEnum.STRING;
    // the property is readonly and can not be changed. Example of readonly
    // property is resource identifier
    private boolean readonly;

    public DocumentPropertyRO() {
    }

    public DocumentPropertyRO(String property, String value, String desc, boolean readonly) {
        this.property = property;
        this.value = value;
        this.desc = desc;
        this.readonly = readonly;
    }

    public String getProperty() {
        return property;
    }

    public void setProperty(String property) {
        this.property = property;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public SMPPropertyTypeEnum getType() {
        return type;
    }

    public void setType(SMPPropertyTypeEnum type) {
        this.type = type;
    }

    public boolean isReadonly() {
        return readonly;
    }

    public void setReadonly(boolean readonly) {
        this.readonly = readonly;
    }


}
