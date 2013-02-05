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
/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package at.peppol.webgui.app.components;

import java.util.Collection;

import oasis.names.specification.ubl.schema.xsd.commonaggregatecomponents_2.AddressType;
import oasis.names.specification.ubl.schema.xsd.commonaggregatecomponents_2.CountryType;
import oasis.names.specification.ubl.schema.xsd.commonaggregatecomponents_2.PartyIdentificationType;
import oasis.names.specification.ubl.schema.xsd.commonaggregatecomponents_2.PartyLegalEntityType;
import oasis.names.specification.ubl.schema.xsd.commonaggregatecomponents_2.PartyNameType;
import oasis.names.specification.ubl.schema.xsd.commonaggregatecomponents_2.PartyTaxSchemeType;
import oasis.names.specification.ubl.schema.xsd.commonaggregatecomponents_2.PartyType;
import oasis.names.specification.ubl.schema.xsd.commonaggregatecomponents_2.TaxSchemeType;
import oasis.names.specification.ubl.schema.xsd.commonbasiccomponents_2.CityNameType;
import oasis.names.specification.ubl.schema.xsd.commonbasiccomponents_2.CompanyIDType;
import oasis.names.specification.ubl.schema.xsd.commonbasiccomponents_2.CountrySubentityType;
import oasis.names.specification.ubl.schema.xsd.commonbasiccomponents_2.EndpointIDType;
import oasis.names.specification.ubl.schema.xsd.commonbasiccomponents_2.IDType;
import oasis.names.specification.ubl.schema.xsd.commonbasiccomponents_2.IdentificationCodeType;
import oasis.names.specification.ubl.schema.xsd.commonbasiccomponents_2.NameType;
import oasis.names.specification.ubl.schema.xsd.commonbasiccomponents_2.RegistrationNameType;
import at.peppol.webgui.app.utils.Utils;
import at.peppol.webgui.app.validator.RequiredFieldListener;
import at.peppol.webgui.app.validator.ValidatorsList;

import com.vaadin.data.Item;
import com.vaadin.data.Property;
import com.vaadin.data.util.NestedMethodProperty;
import com.vaadin.data.util.PropertysetItem;
import com.vaadin.event.FieldEvents.BlurEvent;
import com.vaadin.event.FieldEvents.BlurListener;
import com.vaadin.ui.AbstractTextField;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Component;
import com.vaadin.ui.DefaultFieldFactory;
import com.vaadin.ui.Field;
import com.vaadin.ui.Form;
import com.vaadin.ui.FormFieldFactory;
import com.vaadin.ui.Panel;
import com.vaadin.ui.VerticalLayout;

/**
 * @author Jerouris
 */
@SuppressWarnings ("serial")
public class PartyDetailForm extends Panel {

  private final PartyType partyBean;
  private final String party;

  private VerticalLayout hiddenContent;
  Form legalEntityForm;

  public static String taxSchemeCompanyID = "Tax Scheme Company ID";
  public static String taxSchemeID = "Tax Scheme";

  public PartyDetailForm (final String partyType, final PartyType partyBean) {
    this.party = partyType;
    this.partyBean = partyBean;

    initElements ();

  }

  private void initElements () {

    setCaption (party + " Party");
    // setStyleName("light");

    final VerticalLayout outerLayout = new VerticalLayout ();
    outerLayout.setSpacing (true);
    outerLayout.setMargin (true);

    hiddenContent = new VerticalLayout ();
    hiddenContent.setSpacing (true);
    hiddenContent.setMargin (true);

    final PropertysetItem partyItemSet = new PropertysetItem ();

    PartyIdentificationType supplierPartyID;
    if (partyBean.getPartyIdentification ().size () == 0) {
      supplierPartyID = new PartyIdentificationType ();
      supplierPartyID.setID (new IDType ());
      partyBean.getPartyIdentification ().add (supplierPartyID);
    }
    else {
      supplierPartyID = partyBean.getPartyIdentification ().get (0);
    }

    partyItemSet.addItemProperty ("Party ID", new NestedMethodProperty (supplierPartyID, "ID.value"));

    EndpointIDType endPointID;
    if (partyBean.getEndpointID () == null) {
      endPointID = new EndpointIDType ();
      partyBean.setEndpointID (endPointID);
    }
    else {
      endPointID = partyBean.getEndpointID ();
    }
    partyItemSet.addItemProperty ("Endpoint ID", new NestedMethodProperty (endPointID, "SchemeAgencyID"));

    PartyNameType partyName;
    if (partyBean.getPartyName ().size () == 0) {
      partyName = new PartyNameType ();
      partyName.setName (new NameType ());
      partyBean.getPartyName ().add (partyName);
    }
    else {
      partyName = partyBean.getPartyName ().get (0);
    }
    partyItemSet.addItemProperty ("Party Name", new NestedMethodProperty (partyName, "name.value"));

    /*
     * partyItemSet.addItemProperty("Agency Name", new
     * NestedMethodProperty(supplierPartyID, "ID.SchemeAgencyID") );
     */
    /*
     * final AddressType partyrAddress = new AddressType();
     * partyBean.setPostalAddress(partyrAddress);
     * partyrAddress.setStreetName(new StreetNameType());
     * partyrAddress.setCityName(new CityNameType());
     * partyrAddress.setPostalZone(new PostalZoneType());
     * partyrAddress.setCountry(new CountryType());
     * partyrAddress.getCountry().setIdentificationCode(new
     * IdentificationCodeType()); partyItemSet.addItemProperty("Street Name",
     * new NestedMethodProperty(partyrAddress, "streetName.value"));
     * partyItemSet.addItemProperty("City", new
     * NestedMethodProperty(partyrAddress, "cityName.value"));
     * partyItemSet.addItemProperty("Postal Zone", new
     * NestedMethodProperty(partyrAddress, "postalZone.value"));
     * partyItemSet.addItemProperty("Country", new
     * NestedMethodProperty(partyrAddress, "country.identificationCode.value"));
     */
    AddressDetailForm partyAddressForm;
    AddressType address;
    if (partyBean.getPostalAddress () == null) {
      address = new AddressType ();
    }
    else {
      address = partyBean.getPostalAddress ();
    }
    partyAddressForm = new AddressDetailForm (party, address);
    partyBean.setPostalAddress (address);

    PartyTaxSchemeType taxScheme;
    if (partyBean.getPartyTaxScheme ().size () == 0) {
      taxScheme = new PartyTaxSchemeType ();
      taxScheme.setCompanyID (new CompanyIDType ());

      // partyItemSet.addItemProperty(taxSchemeCompanyID,
      // new NestedMethodProperty(taxScheme.getCompanyID(),"value"));

      // TODO: Hardcoded ShemeID etc for TaxScheme. Should be from a codelist?
      taxScheme.setTaxScheme (new TaxSchemeType ());
      taxScheme.getTaxScheme ().setID (new IDType ());
      taxScheme.getTaxScheme ().getID ().setValue ("VAT");
      taxScheme.getTaxScheme ().getID ().setSchemeID ("UN/ECE 5153");
      taxScheme.getTaxScheme ().getID ().setSchemeAgencyID ("6");

      partyBean.getPartyTaxScheme ().add (taxScheme);
    }
    else {
      taxScheme = partyBean.getPartyTaxScheme ().get (0);
    }

    partyItemSet.addItemProperty (taxSchemeCompanyID, new NestedMethodProperty (taxScheme.getCompanyID (), "value"));

    partyItemSet.addItemProperty (taxSchemeID, new NestedMethodProperty (taxScheme.getTaxScheme ().getID (), "value"));

    final Form partyForm = new Form ();
    partyForm.setFormFieldFactory (new PartyFieldFactory ());
    partyForm.setItemDataSource (partyItemSet);
    partyForm.setImmediate (true);

    final Button addLegalEntityBtn = new Button ("Add Legal Entity");
    final Button removeLegalEntityBtn = new Button ("Remove Legal Entity");
    // removeLegalEntityBtn.setVisible(false);

    addLegalEntityBtn.addListener (new Button.ClickListener () {
      @Override
      public void buttonClick (final ClickEvent event) {
        // add the legal entity component
        final Panel panel = createLegalEntityPanel (removeLegalEntityBtn);
        outerLayout.addComponent (panel);
        panel.setWidth ("90%");
        addLegalEntityBtn.setVisible (false);
        // removeLegalEntityBtn.setVisible(true);
        // outerLayout.replaceComponent(removeLegalEntityBtn, panel);
      }
    });

    removeLegalEntityBtn.addListener (new Button.ClickListener () {
      @Override
      public void buttonClick (final ClickEvent event) {
        // remove the legal entity component
        for (int i = 0; i < outerLayout.getComponentCount (); i++) {
          final Component c = outerLayout.getComponent (i);
          if (c instanceof Panel) {
            if (c.getCaption ().equals ("Legal Entity")) {
              outerLayout.removeComponent (c);
              if (partyBean.getPartyLegalEntity ().size () > 0) {
                partyBean.getPartyLegalEntity ().clear ();
                ValidatorsList.removeListeners (Utils.getFieldListeners (legalEntityForm));
              }
            }
          }
        }
        // removeLegalEntityBtn.setVisible(false);
        addLegalEntityBtn.setVisible (true);
      }
    });

    outerLayout.addComponent (partyForm);
    partyForm.setWidth ("90%");
    outerLayout.addComponent (partyAddressForm);
    partyAddressForm.setWidth ("90%");
    outerLayout.addComponent (addLegalEntityBtn);
    if (partyBean.getPartyLegalEntity ().size () > 0)
      addLegalEntityBtn.click ();
    // outerLayout.addComponent(removeLegalEntityBtn);
    // outerLayout.addComponent(createLegalEntityPanel());

    setContent (outerLayout);
  }

  private Panel createLegalEntityPanel (final Button removeButton) {
    final Panel legalEntityPanel = new Panel ("Legal Entity");
    legalEntityPanel.setStyleName ("light");
    legalEntityPanel.setSizeFull ();

    final PropertysetItem legalEntityItemSet = new PropertysetItem ();

    PartyLegalEntityType legalEntity;
    if (partyBean.getPartyLegalEntity ().size () == 0) {
      legalEntity = new PartyLegalEntityType ();
      legalEntity.setRegistrationName (new RegistrationNameType ());
      legalEntity.setCompanyID (new CompanyIDType ());
      legalEntity.setRegistrationAddress (new AddressType ());
      legalEntity.getRegistrationAddress ().setCityName (new CityNameType ());
      legalEntity.getRegistrationAddress ().setCountrySubentity (new CountrySubentityType ());
      legalEntity.getRegistrationAddress ().setCountry (new CountryType ());
      legalEntity.getRegistrationAddress ().getCountry ().setIdentificationCode (new IdentificationCodeType ());
    }
    else {
      legalEntity = partyBean.getPartyLegalEntity ().get (0);
    }
    // make fields
    legalEntityItemSet.addItemProperty ("Registration Name", new NestedMethodProperty (legalEntity,
                                                                                       "registrationName.value"));
    legalEntityItemSet.addItemProperty ("Company ID", new NestedMethodProperty (legalEntity, "companyID.value"));
    legalEntityItemSet.addItemProperty ("City Name", new NestedMethodProperty (legalEntity.getRegistrationAddress (),
                                                                               "cityName.value"));
    legalEntityItemSet.addItemProperty ("Country Subentity",
                                        new NestedMethodProperty (legalEntity.getRegistrationAddress (),
                                                                  "countrySubentity.value"));
    legalEntityItemSet.addItemProperty ("Country ID", new NestedMethodProperty (legalEntity.getRegistrationAddress (),
                                                                                "country.identificationCode.value"));

    legalEntityForm = new Form ();
    legalEntityForm.setFormFieldFactory (new PartyFieldFactory ());
    legalEntityForm.setItemDataSource (legalEntityItemSet);
    legalEntityForm.setImmediate (true);
    legalEntityPanel.addComponent (legalEntityForm);
    legalEntityPanel.addComponent (removeButton);

    // add the legal entity
    partyBean.getPartyLegalEntity ().add (legalEntity);

    return legalEntityPanel;
  }

  @SuppressWarnings ("serial")
  class PartyFieldFactory implements FormFieldFactory {

    @Override
    public Field createField (final Item item, final Object propertyId, final Component uiContext) {
      // Identify the fields by their Property ID.
      final String pid = (String) propertyId;

      if ("Party ID".equals (pid)) {
        final PartyAgencyIDSelect select = new PartyAgencyIDSelect (pid);
        select.addListener (new Property.ValueChangeListener () {

          @Override
          public void valueChange (final com.vaadin.data.Property.ValueChangeEvent event) {
            partyBean.getPartyIdentification ().get (0).getID ().setSchemeAgencyName (select.getSelectedAgencyName ());
          }
        });
        return select;
      }

      if ("Endpoint ID".equals (pid)) {
        final PartyAgencyIDSelect select = new PartyAgencyIDSelect (pid);
        select.addListener (new Property.ValueChangeListener () {

          @Override
          public void valueChange (final com.vaadin.data.Property.ValueChangeEvent event) {
            partyBean.getEndpointID ().setSchemeAgencyName (select.getSelectedAgencyName ());
          }
        });
        return select;
      }

      if (taxSchemeID.equals (pid)) {
        final TaxSchemeSelect taxSchemeSelect = new TaxSchemeSelect (pid);
        taxSchemeSelect.setRequired (true);
        return taxSchemeSelect;
      }

      final Field field = DefaultFieldFactory.get ().createField (item, propertyId, uiContext);
      if (field instanceof AbstractTextField) {
        ((AbstractTextField) field).setNullRepresentation ("");
        final AbstractTextField tf = (AbstractTextField) field;
        if ("Party Name".equals (pid)) {
          tf.setRequired (true);
          // Validator v = new RequiredFieldValidator();
          // tf.addValidator(v);
          tf.addListener (new RequiredFieldListener (tf, pid));
          ValidatorsList.addListeners ((Collection <BlurListener>) tf.getListeners (BlurEvent.class));
        }
        else
          if ("Company ID".equals (pid)) {
            tf.setRequired (true);
            tf.addListener (new RequiredFieldListener (tf, pid));
            ValidatorsList.addListeners ((Collection <BlurListener>) tf.getListeners (BlurEvent.class));
          }
          else
            if ("Country ID".equals (pid)) {
              tf.setRequired (true);
              tf.addListener (new RequiredFieldListener (tf, pid));
              ValidatorsList.addListeners ((Collection <BlurListener>) tf.getListeners (BlurEvent.class));
            }
            else
              if ("Tax ID".equals (pid)) {
                tf.setRequired (true);
                tf.addListener (new RequiredFieldListener (tf, pid));
                ValidatorsList.addListeners ((Collection <BlurListener>) tf.getListeners (BlurEvent.class));
              }
      }

      return field;
    }
  }
}
