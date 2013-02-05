/**
 * Version: MPL 1.1/EUPL 1.1
 *
 * The contents of this file are subject to the Mozilla Public License Version
 * 1.1 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at:
 * http://www.mozilla.org/MPL/
 *
 * Software distributed under the License is distributed on an "AS IS" basis,
 * WITHOUT WARRANTY OF ANY KIND, either express or implied. See the License
 * for the specific language governing rights and limitations under the
 * License.
 *
 * The Original Code is Copyright The PEPPOL project (http://www.peppol.eu)
 *
 * Alternatively, the contents of this file may be used under the
 * terms of the EUPL, Version 1.1 or - as soon they will be approved
 * by the European Commission - subsequent versions of the EUPL
 * (the "Licence"); You may not use this work except in compliance
 * with the Licence.
 * You may obtain a copy of the Licence at:
 * http://joinup.ec.europa.eu/software/page/eupl/licence-eupl
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the Licence is distributed on an "AS IS" basis,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the Licence for the specific language governing permissions and
 * limitations under the Licence.
 *
 * If you wish to allow use of your version of this file only
 * under the terms of the EUPL License and not to allow others to use
 * your version of this file under the MPL, indicate your decision by
 * deleting the provisions above and replace them with the notice and
 * other provisions required by the EUPL License. If you do not delete
 * the provisions above, a recipient may use your version of this file
 * under either the MPL or the EUPL License.
 */
package at.peppol.webgui.app.components;

import java.util.Collection;

import oasis.names.specification.ubl.schema.xsd.commonaggregatecomponents_2.AddressType;
import oasis.names.specification.ubl.schema.xsd.commonaggregatecomponents_2.CountryType;
import oasis.names.specification.ubl.schema.xsd.commonbasiccomponents_2.AdditionalStreetNameType;
import oasis.names.specification.ubl.schema.xsd.commonbasiccomponents_2.BuildingNumberType;
import oasis.names.specification.ubl.schema.xsd.commonbasiccomponents_2.CityNameType;
import oasis.names.specification.ubl.schema.xsd.commonbasiccomponents_2.CountrySubentityType;
import oasis.names.specification.ubl.schema.xsd.commonbasiccomponents_2.DepartmentType;
import oasis.names.specification.ubl.schema.xsd.commonbasiccomponents_2.IdentificationCodeType;
import oasis.names.specification.ubl.schema.xsd.commonbasiccomponents_2.PostalZoneType;
import oasis.names.specification.ubl.schema.xsd.commonbasiccomponents_2.StreetNameType;

import at.peppol.webgui.app.validator.RequiredFieldListener;
import at.peppol.webgui.app.validator.ValidatorsList;

import com.vaadin.data.Item;
import com.vaadin.data.util.NestedMethodProperty;
import com.vaadin.data.util.PropertysetItem;
import com.vaadin.event.FieldEvents.BlurEvent;
import com.vaadin.event.FieldEvents.BlurListener;
import com.vaadin.ui.AbstractTextField;
import com.vaadin.ui.Component;
import com.vaadin.ui.DefaultFieldFactory;
import com.vaadin.ui.Field;
import com.vaadin.ui.Form;
import com.vaadin.ui.FormFieldFactory;
import com.vaadin.ui.Panel;

@SuppressWarnings ("serial")
public class AddressDetailForm extends Panel {
  
  private final AddressType address;
  private final String addressPrefix;
  
  public AddressDetailForm(String addressPrefix, AddressType addressBean) {
      this.addressPrefix = addressPrefix;
      this.address = addressBean;
      
      initElements();
  }  

  private void initElements() {
    setCaption(addressPrefix + " Address");
    setStyleName("light");
    
    PropertysetItem addressItemSet = new PropertysetItem();
    
    //initialize
    //AddressType address = new AddressType();
    if (address.getStreetName() == null)
    	address.setStreetName(new StreetNameType());
    if (address.getAdditionalStreetName() == null)
    	address.setAdditionalStreetName (new AdditionalStreetNameType());
    if (address.getBuildingNumber() == null)
    	address.setBuildingNumber (new BuildingNumberType ());
    if (address.getDepartment() == null)
    	address.setDepartment (new DepartmentType ());
    if (address.getCityName() == null)
    	address.setCityName(new CityNameType());
    if (address.getPostalZone() == null)
    	address.setPostalZone(new PostalZoneType());
    if (address.getCountrySubentity() == null)
    	address.setCountrySubentity (new CountrySubentityType ());
    
    if (address.getCountry() == null) {
    	address.setCountry(new CountryType());
    	if (address.getCountry().getIdentificationCode() == null)
    		address.getCountry().setIdentificationCode(new IdentificationCodeType());
    }
    
    //make fields
    addressItemSet.addItemProperty("Street Name", new NestedMethodProperty(address, "streetName.value"));
    addressItemSet.addItemProperty ("Additional Street Name", new NestedMethodProperty(address, "additionalStreetName.value") );
    addressItemSet.addItemProperty ("Department", new NestedMethodProperty(address, "department.value") );
    addressItemSet.addItemProperty("City Name", new NestedMethodProperty(address, "cityName.value"));
    addressItemSet.addItemProperty("Postal Zone", new NestedMethodProperty(address, "postalZone.value"));
    addressItemSet.addItemProperty ("Country Subentity", new NestedMethodProperty(address, "countrySubentity.value") );
    addressItemSet.addItemProperty("Country ID", new NestedMethodProperty(address, "country.identificationCode.value"));
    
    //make form
    final Form addressForm = new Form();
    addressForm.setFormFieldFactory(new AddressFieldFactory());
    addressForm.setItemDataSource(addressItemSet);
    addressForm.setImmediate(true);
    addressForm.setWriteThrough(true);
    
    addComponent(addressForm);
  }  
  
  class AddressFieldFactory implements FormFieldFactory {

     @Override
     public Field createField(Item item, Object propertyId, Component uiContext) {
       // Identify the fields by their Property ID.
       String pid = (String) propertyId;

       if ("Country ID".equals(pid)) {
           final CountrySelect countrySelect = new CountrySelect("Country ID");
           countrySelect.setRequired(true);
           return countrySelect;
       }
       
       Field field = DefaultFieldFactory.get().createField(item,propertyId, uiContext);
       if (field instanceof AbstractTextField){
           ((AbstractTextField) field).setNullRepresentation("");
           final AbstractTextField tf = (AbstractTextField) field;
           if ("Street Name".equals(pid) || "City Name".equals(pid) || "Postal Zone".equals(pid) || "Country ID".equals(pid)) {
        	   tf.setRequired(true);
        	   tf.addListener(new RequiredFieldListener(tf,pid));
        	   ValidatorsList.addListeners((Collection<BlurListener>) tf.getListeners(BlurEvent.class));
           }
       }
       
       return field;
     }
  }  
 
}
