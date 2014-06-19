
package org.oasis_open.docs.ebxml_msg.ebms.v3_0.ns.core._200704;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAnyAttribute;
import javax.xml.bind.annotation.XmlAnyElement;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlID;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.adapters.CollapsedStringAdapter;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import javax.xml.namespace.QName;
import backend.ecodex.org._1_1.runtime.ZeroOneBooleanAdapter;
import org.w3c.dom.Element;


/**
 *  
 * 	The eb:Messaging element is the top element of ebMS-3 headers, and it is 
 * 	placed within the SOAP Header element (either SOAP 1.1 or SOAP 1.2). The 
 * 	eb:Messaging element may contain several instances of eb:SignalMessage 
 * 	and eb:UserMessage elements. However in the core part of the ebMS-3
 * 	specification, only one instance of either eb:UserMessage or eb:SignalMessage 
 * 	must be present. The second part of ebMS-3 specification may need to include 
 * 	multiple instances of either eb:SignalMessage, eb:UserMessage or both. 
 * 	Therefore, this schema is allowing multiple instances of eb:SignalMessage 
 * 	and eb:UserMessage elements for part 2 of the ebMS-3 specification. Note
 * 	that the eb:Messaging element cannot be empty (at least one of 
 * 	eb:SignalMessage or eb:UserMessage element must present).
 * 			
 * 
 * <p>Java class for Messaging complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="Messaging">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="SignalMessage" type="{http://docs.oasis-open.org/ebxml-msg/ebms/v3.0/ns/core/200704/}SignalMessage" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element name="UserMessage" type="{http://docs.oasis-open.org/ebxml-msg/ebms/v3.0/ns/core/200704/}UserMessage" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;any processContents='lax' namespace='##other' maxOccurs="unbounded" minOccurs="0"/>
 *       &lt;/sequence>
 *       &lt;attGroup ref="{http://docs.oasis-open.org/ebxml-msg/ebms/v3.0/ns/core/200704/}headerExtension"/>
 *       &lt;anyAttribute processContents='lax' namespace='##other'/>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "Messaging", propOrder = {
    "signalMessage",
    "userMessage",
    "any"
})
public class Messaging {

    @XmlElement(name = "SignalMessage")
    protected List<SignalMessage> signalMessage;
    @XmlElement(name = "UserMessage")
    protected List<UserMessage> userMessage;
    @XmlAnyElement(lax = true)
    protected List<Object> any;
    @XmlAttribute(name = "id")
    @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
    @XmlID
    @XmlSchemaType(name = "ID")
    protected String id;
    @XmlAttribute(name = "mustUnderstandS11", namespace = "http://schemas.xmlsoap.org/soap/envelope/")
    @XmlJavaTypeAdapter(ZeroOneBooleanAdapter.class)
    protected Boolean mustUnderstandS11;
    @XmlAttribute(name = "mustUnderstand", namespace = "http://www.w3.org/2003/05/soap-envelope")
    protected Boolean mustUnderstand;
    @XmlAnyAttribute
    private Map<QName, String> otherAttributes = new HashMap<QName, String>();

    /**
     * Gets the value of the signalMessage property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the signalMessage property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getSignalMessage().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link SignalMessage }
     * 
     * 
     */
    public List<SignalMessage> getSignalMessage() {
        if (signalMessage == null) {
            signalMessage = new ArrayList<SignalMessage>();
        }
        return this.signalMessage;
    }

    /**
     * Gets the value of the userMessage property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the userMessage property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getUserMessage().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link UserMessage }
     * 
     * 
     */
    public List<UserMessage> getUserMessage() {
        if (userMessage == null) {
            userMessage = new ArrayList<UserMessage>();
        }
        return this.userMessage;
    }

    /**
     * Gets the value of the any property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the any property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getAny().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link Object }
     * {@link Element }
     * 
     * 
     */
    public List<Object> getAny() {
        if (any == null) {
            any = new ArrayList<Object>();
        }
        return this.any;
    }

    /**
     * Gets the value of the id property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getId() {
        return id;
    }

    /**
     * Sets the value of the id property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setId(String value) {
        this.id = value;
    }

    /**
     * 
     * 	    if SOAP 1.1 is being used, this attribute is required
     * 			  
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public Boolean isMustUnderstandS11() {
        return mustUnderstandS11;
    }

    /**
     * Sets the value of the mustUnderstandS11 property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setMustUnderstandS11(Boolean value) {
        this.mustUnderstandS11 = value;
    }

    /**
     * 
     * 	    if SOAP 1.2 is being used, this attribute is required
     * 			  
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public boolean isMustUnderstand() {
        if (mustUnderstand == null) {
            return false;
        } else {
            return mustUnderstand;
        }
    }

    /**
     * Sets the value of the mustUnderstand property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setMustUnderstand(Boolean value) {
        this.mustUnderstand = value;
    }

    /**
     * Gets a map that contains attributes that aren't bound to any typed property on this class.
     * 
     * <p>
     * the map is keyed by the name of the attribute and 
     * the value is the string value of the attribute.
     * 
     * the map returned by this method is live, and you can add new attribute
     * by updating the map directly. Because of this design, there's no setter.
     * 
     * 
     * @return
     *     always non-null
     */
    public Map<QName, String> getOtherAttributes() {
        return otherAttributes;
    }

}
