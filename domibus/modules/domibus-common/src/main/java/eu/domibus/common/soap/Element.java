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
public class Element implements java.io.Serializable {
    public static final long serialVersionUID = 7825985226389871436L;

    private static final Logger log = Logger.getLogger(Element.class);

    protected OMElement element = null;
    protected String localName = null;
    protected OMNamespace namespace = null;
    protected String text = null;

    public Element() {
    }

    public Element(final String name) {
        final OMFactory factory = OMAbstractFactory.getOMFactory();
        element = factory.createOMElement(name, null);
    }

    public Element(final String localName, final String uri, final String prefix) {
        final OMFactory factory = OMAbstractFactory.getOMFactory();
        final OMNamespace ns = factory.createOMNamespace(uri, prefix);
        element = factory.createOMElement(localName, ns);
    }

    public Element(final OMElement omElement) {
        fromOMElement(omElement);
    }

    public Element(final File xmlFile) {
        if (xmlFile == null) {
            return;
        }
        try {
            final XMLStreamReader parser =
                    XMLInputFactory.newInstance().createXMLStreamReader(new FileInputStream(xmlFile));
            final StAXOMBuilder builder = new StAXOMBuilder(parser);
            element = builder.getDocumentElement();
        } catch (FactoryConfigurationError ex) {
            log.error("Problem while getting instance of XMLInputFactory", ex);
        } catch (FileNotFoundException e) {
            log.error("XMLFile not found", e);
        } catch (XMLStreamException e) {
            log.error("An unexpected error during the xml processing occured", e);
        }
    }

    public OMElement getElement() {
        if (element != null) {
            return element;
        }
        element = getOMFactory().createOMElement(localName, namespace);
        if (text != null) {
            element.setText(text);
        }
        return element;
    }

    public String getLocalName() {
        return localName;
    }

    public void setLocalName(final String localName) {
        this.localName = localName;
        if (element != null) {
            element.setLocalName(localName);
        } else {
            element = getOMFactory().createOMElement(localName, namespace);
        }
    }

    public OMNamespace getNamespace() {
        return namespace;
    }

    public void setNamespace(final OMNamespace namespace) {
        this.namespace = namespace;
        if (element != null) {
            element.setNamespace(namespace);
        } else {
            element = getOMFactory().createOMElement(localName, namespace);
        }
    }

    public String getText() {
        return text;
    }

    public void setText(final String text) {
        this.text = text;
        if (element != null) {
            element.setText(text);
        } else {
            element = getOMFactory().createOMElement(localName, namespace);
            element.setText(text);
        }
    }

    private void fromOMElement(final OMElement omElement) {
        if (omElement == null) {
            return;
        }
        element = omElement.cloneOMElement();
        text = element.getText();
        localName = element.getLocalName();
        namespace = element.getNamespace();
    }

    public String getAttributeValue(final String name, final String uri, final String prefix) {
        final QName qname = new QName(uri, name, prefix);
        return getElement().getAttributeValue(qname);
    }

    public void addAttribute(final String name, final String uri, final String prefix, final String value) {
        OMNamespace ns = null;
        if ((uri != null && !uri.trim().equals("")) || (prefix != null && !prefix.trim().equals(""))) {
            ns = getOMFactory().createOMNamespace(uri, prefix);
        }
        final OMAttribute att = getOMFactory().createOMAttribute(name, ns, value);
        getElement().addAttribute(att);
    }

    public void addAttribute(final String name, final String value) {
        final OMAttribute att = getOMFactory().createOMAttribute(name, null, value);
        getElement().addAttribute(att);
    }

    public void setAttribute(final String attLocalName, final String value) {
        if (attLocalName == null || attLocalName.trim().equals("")) {
            return;
        }
        final OMAttribute att = getAttribute(attLocalName);
        if (att != null) {
            att.setAttributeValue(value);
        } else {
            addAttribute(attLocalName, value);
        }
    }

    public void setAttribute(final String attLocalName, final String prefix, final String value) {
        if (attLocalName == null || attLocalName.trim().equals("")) {
            return;
        }
        if (prefix == null || prefix.trim().equals("")) {
            setAttribute(attLocalName, value);
        } else {
            final OMNamespace ns = getElement().findNamespaceURI(prefix);
            OMAttribute att;
            if (ns != null) {
                att = getAttribute(ns.getNamespaceURI(), attLocalName, prefix);
                if (att != null) {
                    att.setAttributeValue(value);
                } else {
                    att = getOMFactory().createOMAttribute(attLocalName, ns, value);
                    getElement().addAttribute(att);
                }
            } else {
                getOMFactory().createOMAttribute(attLocalName, ns, value);
            }
        }
    }

    public OMAttribute getAttribute(final String attLocalName) {
        if (attLocalName == null || attLocalName.trim().equals("")) {
            return null;
        }
        final Iterator it = getElement().getAllAttributes();
        OMAttribute att;
        while (it != null && it.hasNext()) {
            att = (OMAttribute) it.next();
            if (att != null && att.getLocalName().equals(attLocalName)) {
                return att;
            }
        }
        return null;
    }

    public OMAttribute getAttribute(final String uri, final String name, final String prefix) {
        OMAttribute att = null;
        if (uri != null && prefix != null) {
            final QName qname = new QName(uri, name, prefix);
            att = getElement().getAttribute(qname);
        }
        if (att != null) {
            return att;
        } else {
            return getAttribute(name);
        }
    }

    public String getAttributeValue(final String attLocalName) {
        final OMAttribute att = getAttribute(attLocalName);
        if (att != null) {
            return att.getAttributeValue();
        } else {
            return null;
        }
    }

    public Element addElement(final String localName, final String prefix) {
        if (localName == null || localName.trim().equals("")) {
            return null;
        }
        OMNamespace ns = null;
        if (prefix != null && !prefix.trim().equals("")) {
            ns = getElement().findNamespaceURI(prefix);
        }
        final Element child = new Element();
        child.setLocalName(localName);
        child.setNamespace(ns);
        getElement().addChild(child.getElement());
        return child;
    }

    public OMElement getChild(final String localName, final String prefix) {
        if (localName == null || localName.trim().equals("")) {
            return null;
        }
        final Iterator it = getElement().getChildElements();
        while (it != null && it.hasNext()) {
            final OMElement e = (OMElement) it.next();
            if (e != null && e.getLocalName().equals(localName) &&
                e.getNamespace() != null && e.getNamespace().getPrefix() != null &&
                e.getNamespace().getPrefix().equals(prefix)) {
                return e;
            }
        }
        return null;
    }

    public OMElement getFirstGrandChildWithName(final String _localName) {
        return getFirstGrandChildWithName(element, _localName);
    }

    public String getGrandChildValue(final String _localName) {
        final OMElement gc = getFirstGrandChildWithName(_localName);
        if (gc != null) {
            return gc.getText();
        } else {
            return null;
        }
    }

    private OMElement getFirstGrandChildWithName(final OMElement root, final String _localName) {
        if (_localName == null || _localName.trim().equals("") || root == null) {
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
            OMElement temp = getFirstGrandChildWithName(om, _localName);
            if (temp != null) {
                return temp;
            }

            for (OMElement tmp = (OMElement) om.getNextOMSibling(); tmp != null; ) {
                temp = getFirstGrandChildWithName(tmp, _localName);
                if (temp != null) {
                    return temp;
                }
                tmp = (OMElement) tmp.getNextOMSibling();
            }
        }
        return null;
    }

    public void writeTo(final Writer writer) {
        try {
            final XMLOutputFactory xof = XMLOutputFactory.newInstance();
            final XMLStreamWriter w = xof.createXMLStreamWriter(writer);
            getElement().serialize(w);
            writer.flush();
        } catch (FactoryConfigurationError ex) {
            log.error("Problem while getting instance of XMLInputFactory", ex);
        } catch (IOException e) {
            log.error("I/O exception occured during flush()", e);
        } catch (XMLStreamException e) {
            log.error("An unexpected error during the xml processing occured", e);
        }
    }

    public String toXML() {
        final StringWriter sw = new StringWriter();
        writeTo(sw);
        return sw.toString();
    }

    public void addChild(final OMNode omNode) {
        if (omNode == null) {
            return;
        }
        getElement().addChild(omNode);
    }

    public void addChild(final Element node) {
        if (node == null) {
            return;
        }
        getElement().addChild(node.getElement());
    }

    public Iterator getChildElements() {
        return getElement().getChildElements();
    }

    private OMFactory getOMFactory() {
        return OMAbstractFactory.getOMFactory();
    }
}