package eu.domibus.common.soap;

import org.apache.axiom.om.*;
import org.apache.axiom.om.impl.builder.StAXOMBuilder;
import org.apache.log4j.Logger;

import javax.xml.namespace.QName;
import javax.xml.stream.*;
import java.io.*;
import java.util.Iterator;

/**
 * @author Hamid Ben Malek
 */
public class Element implements Serializable {
    public static final long serialVersionUID = 7825985226389871436L;

    private static final Logger log = Logger.getLogger(Element.class);

    protected OMElement element;
    protected String localName;
    protected OMNamespace namespace;
    protected String text;

    public Element() {
    }

    public Element(final String name) {
        final OMFactory factory = OMAbstractFactory.getOMFactory();
        this.element = factory.createOMElement(name, null);
    }

    public Element(final String localName, final String uri, final String prefix) {
        final OMFactory factory = OMAbstractFactory.getOMFactory();
        final OMNamespace ns = factory.createOMNamespace(uri, prefix);
        this.element = factory.createOMElement(localName, ns);
    }

    public Element(final OMElement omElement) {
        this.fromOMElement(omElement);
    }

    public Element(final File xmlFile) {
        if (xmlFile == null) {
            return;
        }
        try {
            final XMLStreamReader parser =
                    XMLInputFactory.newInstance().createXMLStreamReader(new FileInputStream(xmlFile));
            final StAXOMBuilder builder = new StAXOMBuilder(parser);
            this.element = builder.getDocumentElement();
        } catch (FactoryConfigurationError ex) {
            Element.log.error("Problem while getting instance of XMLInputFactory", ex);
        } catch (FileNotFoundException e) {
            Element.log.error("XMLFile not found", e);
        } catch (XMLStreamException e) {
            Element.log.error("An unexpected error during the xml processing occured", e);
        }
    }

    public OMElement getElement() {
        if (this.element != null) {
            return this.element;
        }
        this.element = this.getOMFactory().createOMElement(this.localName, this.namespace);
        if (this.text != null) {
            this.element.setText(this.text);
        }
        return this.element;
    }

    public String getLocalName() {
        return this.localName;
    }

    public void setLocalName(final String localName) {
        this.localName = localName;
        if (this.element != null) {
            this.element.setLocalName(localName);
        } else {
            this.element = this.getOMFactory().createOMElement(localName, this.namespace);
        }
    }

    public OMNamespace getNamespace() {
        return this.namespace;
    }

    public void setNamespace(final OMNamespace namespace) {
        this.namespace = namespace;
        if (this.element != null) {
            this.element.setNamespace(namespace);
        } else {
            this.element = this.getOMFactory().createOMElement(this.localName, namespace);
        }
    }

    public String getText() {
        return this.text;
    }

    public void setText(final String text) {
        this.text = text;
        if (this.element != null) {
            this.element.setText(text);
        } else {
            this.element = this.getOMFactory().createOMElement(this.localName, this.namespace);
            this.element.setText(text);
        }
    }

    private void fromOMElement(final OMElement omElement) {
        if (omElement == null) {
            return;
        }
        this.element = omElement.cloneOMElement();
        this.text = this.element.getText();
        this.localName = this.element.getLocalName();
        this.namespace = this.element.getNamespace();
    }

    public String getAttributeValue(final String name, final String uri, final String prefix) {
        final QName qname = new QName(uri, name, prefix);
        return this.getElement().getAttributeValue(qname);
    }

    public void addAttribute(final String name, final String uri, final String prefix, final String value) {
        OMNamespace ns = null;
        if (((uri != null) && !"".equals(uri.trim())) || ((prefix != null) && !"".equals(prefix.trim()))) {
            ns = this.getOMFactory().createOMNamespace(uri, prefix);
        }
        final OMAttribute att = this.getOMFactory().createOMAttribute(name, ns, value);
        this.getElement().addAttribute(att);
    }

    public void addAttribute(final String name, final String value) {
        final OMAttribute att = this.getOMFactory().createOMAttribute(name, null, value);
        this.getElement().addAttribute(att);
    }

    public void setAttribute(final String attLocalName, final String value) {
        if ((attLocalName == null) || "".equals(attLocalName.trim())) {
            return;
        }
        final OMAttribute att = this.getAttribute(attLocalName);
        if (att != null) {
            att.setAttributeValue(value);
        } else {
            this.addAttribute(attLocalName, value);
        }
    }

    public void setAttribute(final String attLocalName, final String prefix, final String value) {
        if ((attLocalName == null) || "".equals(attLocalName.trim())) {
            return;
        }
        if ((prefix == null) || "".equals(prefix.trim())) {
            this.setAttribute(attLocalName, value);
        } else {
            final OMNamespace ns = this.getElement().findNamespaceURI(prefix);
            OMAttribute att;
            if (ns != null) {
                att = this.getAttribute(ns.getNamespaceURI(), attLocalName, prefix);
                if (att != null) {
                    att.setAttributeValue(value);
                } else {
                    att = this.getOMFactory().createOMAttribute(attLocalName, ns, value);
                    this.getElement().addAttribute(att);
                }
            } else {
                this.getOMFactory().createOMAttribute(attLocalName, ns, value);
            }
        }
    }

    public OMAttribute getAttribute(final String attLocalName) {
        if ((attLocalName == null) || "".equals(attLocalName.trim())) {
            return null;
        }
        final Iterator it = this.getElement().getAllAttributes();
        OMAttribute att;
        while ((it != null) && it.hasNext()) {
            att = (OMAttribute) it.next();
            if ((att != null) && att.getLocalName().equals(attLocalName)) {
                return att;
            }
        }
        return null;
    }

    public OMAttribute getAttribute(final String uri, final String name, final String prefix) {
        OMAttribute att = null;
        if ((uri != null) && (prefix != null)) {
            final QName qname = new QName(uri, name, prefix);
            att = this.getElement().getAttribute(qname);
        }
        if (att != null) {
            return att;
        } else {
            return this.getAttribute(name);
        }
    }

    public String getAttributeValue(final String attLocalName) {
        final OMAttribute att = this.getAttribute(attLocalName);
        if (att != null) {
            return att.getAttributeValue();
        } else {
            return null;
        }
    }

    public Element addElement(final String localName, final String prefix) {
        if ((localName == null) || "".equals(localName.trim())) {
            return null;
        }
        OMNamespace ns = null;
        if ((prefix != null) && !"".equals(prefix.trim())) {
            ns = this.getElement().findNamespaceURI(prefix);
        }
        final Element child = new Element();
        child.setLocalName(localName);
        child.setNamespace(ns);
        this.getElement().addChild(child.getElement());
        return child;
    }

    public OMElement getChild(final String localName, final String prefix) {
        if ((localName == null) || "".equals(localName.trim())) {
            return null;
        }
        final Iterator it = this.getElement().getChildElements();
        while ((it != null) && it.hasNext()) {
            final OMElement e = (OMElement) it.next();
            if ((e != null) && e.getLocalName().equals(localName) &&
                (e.getNamespace() != null) && (e.getNamespace().getPrefix() != null) &&
                e.getNamespace().getPrefix().equals(prefix)) {
                return e;
            }
        }
        return null;
    }

    public OMElement getFirstGrandChildWithName(final String _localName) {
        return this.getFirstGrandChildWithName(this.element, _localName);
    }

    public String getGrandChildValue(final String _localName) {
        final OMElement gc = this.getFirstGrandChildWithName(_localName);
        if (gc != null) {
            return gc.getText();
        } else {
            return null;
        }
    }

    private OMElement getFirstGrandChildWithName(final OMElement root, final String _localName) {
        if ((_localName == null) || "".equals(_localName.trim()) || (root == null)) {
            return null;
        }
        if (root.getLocalName().equals(_localName)) {
            return root;
        }

        final OMElement om = root.getFirstElement();
        if (om != null) {
            if (om.getLocalName().equals(_localName)) {
                return om;
            }
            OMElement temp = this.getFirstGrandChildWithName(om, _localName);
            if (temp != null) {
                return temp;
            }

            for (OMElement tmp = (OMElement) om.getNextOMSibling(); tmp != null; ) {
                temp = this.getFirstGrandChildWithName(tmp, _localName);
                if (temp != null) {
                    return temp;
                }
                tmp = (OMElement) tmp.getNextOMSibling();     //FIXME: assignment to for-loop parameter
            }
        }
        return null;
    }

    public void writeTo(final Writer writer) {
        try {
            final XMLOutputFactory xof = XMLOutputFactory.newInstance();
            final XMLStreamWriter w = xof.createXMLStreamWriter(writer);
            this.getElement().serialize(w);
            writer.flush();
        } catch (FactoryConfigurationError ex) {
            Element.log.error("Problem while getting instance of XMLInputFactory", ex);
        } catch (IOException e) {
            Element.log.error("I/O exception occured during flush()", e);
        } catch (XMLStreamException e) {
            Element.log.error("An unexpected error during the xml processing occured", e);
        }
    }

    public String toXML() {
        final StringWriter sw = new StringWriter();
        this.writeTo(sw);
        return sw.toString();
    }

    public void addChild(final OMNode omNode) {
        if (omNode == null) {
            return;
        }
        this.getElement().addChild(omNode);
    }

    public void addChild(final Element node) {
        if (node == null) {
            return;
        }
        this.getElement().addChild(node.getElement());
    }

    public Iterator getChildElements() {
        return this.getElement().getChildElements();
    }

    private OMFactory getOMFactory() {
        return OMAbstractFactory.getOMFactory();
    }
}