//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, vJAXB 2.1.10 in JDK 6 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2013.09.09 at 11:30:57 AM MESZ 
//


package eu.domibus.security.config.generated;

import javax.xml.bind.annotation.*;
import javax.xml.bind.annotation.adapters.CollapsedStringAdapter;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;


/**
 * <p>Java class for anonymous complex type.
 * <p/>
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p/>
 * <pre>
 * &lt;complexType>
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element ref="{http://www.e-codex.eu/domibus/securityconfig/0.1}remoteAlias"/>
 *         &lt;element ref="{http://www.e-codex.eu/domibus/securityconfig/0.1}policyFile"/>
 *       &lt;/sequence>
 *       &lt;attribute name="name" use="required" type="{http://www.w3.org/2001/XMLSchema}NCName" />
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {"remoteAlias", "policyFile"})
@XmlRootElement(name = "security")
public class Security {

    @XmlElement(required = true)
    @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
    @XmlSchemaType(name = "NCName")
    protected String remoteAlias;
    @XmlElement(required = true)
    @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
    @XmlSchemaType(name = "NCName")
    protected String policyFile;
    @XmlAttribute(required = true)
    @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
    @XmlSchemaType(name = "NCName")
    protected String name;

    /**
     * Gets the value of the remoteAlias property.
     *
     * @return possible object is
     * {@link String }
     */
    public String getRemoteAlias() {
        return this.remoteAlias;
    }

    /**
     * Sets the value of the remoteAlias property.
     *
     * @param value allowed object is
     *              {@link String }
     */
    public void setRemoteAlias(final String value) {
        this.remoteAlias = value;
    }

    /**
     * Gets the value of the policyFile property.
     *
     * @return possible object is
     * {@link String }
     */
    public String getPolicyFile() {
        return this.policyFile;
    }

    /**
     * Sets the value of the policyFile property.
     *
     * @param value allowed object is
     *              {@link String }
     */
    public void setPolicyFile(final String value) {
        this.policyFile = value;
    }

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

}