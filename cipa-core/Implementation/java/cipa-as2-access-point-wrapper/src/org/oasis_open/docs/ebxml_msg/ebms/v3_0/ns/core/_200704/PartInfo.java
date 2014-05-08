
package org.oasis_open.docs.ebxml_msg.ebms.v3_0.ns.core._200704;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.adapters.CollapsedStringAdapter;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;


/**
 * <p>Java class for PartInfo complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="PartInfo">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="Schema" type="{http://docs.oasis-open.org/ebxml-msg/ebms/v3.0/ns/core/200704/}Schema" minOccurs="0"/>
 *         &lt;element name="Description" type="{http://docs.oasis-open.org/ebxml-msg/ebms/v3.0/ns/core/200704/}Description" minOccurs="0"/>
 *         &lt;element name="PartProperties" type="{http://docs.oasis-open.org/ebxml-msg/ebms/v3.0/ns/core/200704/}PartProperties" minOccurs="0"/>
 *       &lt;/sequence>
 *       &lt;attribute name="href" type="{http://www.w3.org/2001/XMLSchema}token" />
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "PartInfo", propOrder = {
    "schema",
    "description",
    "partProperties"
})
public class PartInfo {

    @XmlElement(name = "Schema")
    protected Schema schema;
    @XmlElement(name = "Description")
    protected Description description;
    @XmlElement(name = "PartProperties")
    protected PartProperties partProperties;
    @XmlAttribute(name = "href")
    @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
    @XmlSchemaType(name = "token")
    protected String href;

    /**
     * Gets the value of the schema property.
     * 
     * @return
     *     possible object is
     *     {@link Schema }
     *     
     */
    public Schema getSchema() {
        return schema;
    }

    /**
     * Sets the value of the schema property.
     * 
     * @param value
     *     allowed object is
     *     {@link Schema }
     *     
     */
    public void setSchema(Schema value) {
        this.schema = value;
    }

    /**
     * Gets the value of the description property.
     * 
     * @return
     *     possible object is
     *     {@link Description }
     *     
     */
    public Description getDescription() {
        return description;
    }

    /**
     * Sets the value of the description property.
     * 
     * @param value
     *     allowed object is
     *     {@link Description }
     *     
     */
    public void setDescription(Description value) {
        this.description = value;
    }

    /**
     * Gets the value of the partProperties property.
     * 
     * @return
     *     possible object is
     *     {@link PartProperties }
     *     
     */
    public PartProperties getPartProperties() {
        return partProperties;
    }

    /**
     * Sets the value of the partProperties property.
     * 
     * @param value
     *     allowed object is
     *     {@link PartProperties }
     *     
     */
    public void setPartProperties(PartProperties value) {
        this.partProperties = value;
    }

    /**
     * Gets the value of the href property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getHref() {
        return href;
    }

    /**
     * Sets the value of the href property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setHref(String value) {
        this.href = value;
    }

}
