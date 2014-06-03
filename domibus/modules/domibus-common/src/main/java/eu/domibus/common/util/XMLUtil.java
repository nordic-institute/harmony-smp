package eu.domibus.common.util;

import org.apache.axiom.om.*;
import org.apache.axiom.om.impl.builder.StAXOMBuilder;
import org.apache.axiom.om.xpath.AXIOMXPath;
import org.apache.axiom.soap.SOAP11Constants;
import org.apache.axiom.soap.SOAP12Constants;
import org.apache.axiom.soap.SOAPEnvelope;
import org.apache.axiom.soap.SOAPFactory;
import org.apache.axis2.context.MessageContext;
import org.apache.axis2.util.XMLPrettyPrinter;
import org.apache.log4j.Logger;
import org.jaxen.JaxenException;
import org.jdom.JDOMException;

import javax.xml.stream.*;
import javax.xml.transform.*;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import java.io.*;
import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class XMLUtil {

    private static OMFactory factory = OMAbstractFactory.getOMFactory();

    private static final Logger log = Logger.getLogger(XMLUtil.class);


    public static OMElement newElement(final String localName, final String uri, final String prefix) {
        final OMNamespace ns = factory.createOMNamespace(uri, prefix);
        return factory.createOMElement(localName, ns);
    }

    public static String toString(final OMElement element) {
        if (element == null) {
            return null;
        }
        final StringWriter sw = new StringWriter();
        writeTo(element, sw);
        return sw.toString();
    }

    public static void writeTo(final OMElement element, final Writer writer) {
        try {
            final XMLOutputFactory xof = XMLOutputFactory.newInstance();
            final XMLStreamWriter w = xof.createXMLStreamWriter(writer);
            element.serialize(w);
            writer.flush();
        } catch (XMLStreamException ex) {
            log.error("An unexpected error during the xml processing occured", ex);
        } catch (IOException e) {
            log.error("Error while flushing writer", e);
        }
    }

    public static void prettyPrint(final OMElement element, final String fileName) {
        if (element == null || fileName == null || fileName.trim().equals("")) {
            return;
        }
        try {
            final FileOutputStream fos = new FileOutputStream(fileName);
            XMLPrettyPrinter.prettify(element, fos);
            fos.flush();
            fos.close();
        } catch (IOException ex) {
            log.error("Error on FileOutStream operation", ex);
        } catch (Exception e) {
            log.error("Error while calling XMLPrettyPrinter on element");
        }
    }

    public static String prettyToString(final OMElement element) {
        if (element == null) {
            return null;
        }
        try {
            final java.io.ByteArrayOutputStream bos = new java.io.ByteArrayOutputStream();
            XMLPrettyPrinter.prettify(element, bos);
            return bos.toString();
        } catch (Exception ex) {
            log.error("Error while calling XMLPrettyPrinter on element");
        }
        return element.toString();
    }

    public static void debug(final Logger log, final OMElement element) {
        if (log == null || element == null) {
            return;
        }
        log.debug(prettyToString(element));
    }

    public static void debug(final Logger log, final String message, final OMElement element) {
        if (log == null || element == null) {
            return;
        }
        log.debug(message + " " + prettyToString(element));
    }

    public static void info(final Logger log, final OMElement element) {
        if (log == null || element == null) {
            return;
        }
        log.info(prettyToString(element));
    }

    public static void info(final Logger log, final String message, final OMElement element) {
        if (log == null || element == null) {
            return;
        }
        log.info(message + " " + prettyToString(element));
    }

    public static void error(final Logger log, final OMElement element) {
        if (log == null || element == null) {
            return;
        }
        log.error(prettyToString(element));
    }

    public static void error(final Logger log, final String message, final OMElement element) {
        if (log == null || element == null) {
            return;
        }
        log.error(message + " " + prettyToString(element));
    }

    public static OMElement toOMElement(final String xml) {
        if (xml == null || xml.trim().equals("")) {
            return null;
        }
        final OMElement element = null;
        try {
            final StringReader sr = new StringReader(xml);
            final XMLInputFactory xif = XMLInputFactory.newInstance();
            final XMLStreamReader reader = xif.createXMLStreamReader(sr);
            final StAXOMBuilder builder = new StAXOMBuilder(reader);
            return builder.getDocumentElement();
        } catch (FactoryConfigurationError e) {
            log.error("FactoryConfigurationError during toElement", e);
        } catch (XMLStreamException e) {
            log.error("Problem while XML Stream processing", e);
        }
        return element;
    }

    public static OMElement rootElement(final File xmlFile) {
        if (xmlFile == null || !xmlFile.exists()) {
            return null;
        }
        try {
            final FileInputStream fis = new FileInputStream(xmlFile);
            final XMLInputFactory xif = XMLInputFactory.newInstance();
            final XMLStreamReader reader = xif.createXMLStreamReader(fis);
            final StAXOMBuilder builder = new StAXOMBuilder(reader);
            return builder.getDocumentElement();
        } catch (FactoryConfigurationError e) {
            log.error("FactoryConfigurationError during toElement", e);
        } catch (XMLStreamException e) {
            log.error("Problem while XML Stream processing", e);
        } catch (FileNotFoundException e) {
            log.error("Error while reading XMLFile. File not found", e);
        }
        return null;
    }

    public static OMElement rootElement(final InputStream in) {
        if (in == null) {
            return null;
        }
        try {
            final XMLInputFactory xif = XMLInputFactory.newInstance();
            final XMLStreamReader reader = xif.createXMLStreamReader(in);
            final StAXOMBuilder builder = new StAXOMBuilder(reader);
            return builder.getDocumentElement();
        } catch (FactoryConfigurationError e) {
            log.error("FactoryConfigurationError during toElement", e);
        } catch (XMLStreamException e) {
            log.error("Problem while XML Stream processing", e);
        }
        return null;
    }

    public static OMAttribute getAttribute(final OMElement element, final String attLocalName) {
        if (element == null || attLocalName == null ||
            attLocalName.trim().equals("")) {
            return null;
        }
        final Iterator it = element.getAllAttributes();
        OMAttribute att = null;
        while (it != null && it.hasNext()) {
            att = (OMAttribute) it.next();
            if (att != null && att.getLocalName().equals(attLocalName)) {
                return att;
            }
        }
        return null;
    }

    public static String getAttributeValue(final OMElement element, final String attLocalName) {
        final OMAttribute att = getAttribute(element, attLocalName);
        if (att != null) {
            return att.getAttributeValue();
        } else {
            return null;
        }
    }

    public static void addAttributeTo(final OMElement element, final String attName, final String attValue) {
        if (element == null || (attName == null || attName.trim().equals(""))) {
            return;
        }
        final OMAttribute att = element.getOMFactory().createOMAttribute(attName, null, attValue);
        element.addAttribute(att);
    }

    public static void addAttributeTo(final OMElement element, final String attName, final String uri,
                                      final String prefix, final String attValue) {
        if (element == null || (attName == null || attName.trim().equals(""))) {
            return;
        }
        OMNamespace ns = null;
        if (uri != null || prefix != null) {
            if (uri == null) {
                ns = element.findNamespaceURI(prefix);
            }
            if (ns == null) {
                ns = element.getOMFactory().createOMNamespace(uri, prefix);
            }
        }
        final OMAttribute att = element.getOMFactory().createOMAttribute(attName, ns, attValue);
        element.addAttribute(att);
    }

    public static OMElement getFirstChildWithName(final OMElement parent, final String childName) {
        if (parent == null || childName == null || childName.trim().equals("")) {
            return null;
        }
        final Iterator it = parent.getChildElements();
        if (it == null || !it.hasNext()) {
            return null;
        }
        Object obj = null;
        OMElement child = null;
        while (it.hasNext()) {
            obj = it.next();
            if (obj instanceof OMElement) {
                child = (OMElement) obj;
                if (child.getLocalName().equals(childName)) {
                    return child;
                }
            }
        }
        return null;
    }

    public static OMElement getFirstChildWithNameNS(final OMElement root, final String localName,
                                                    final String namespaceURI) {
        if (localName == null || localName.trim().equals("") || root == null) {
            return null;
        }
        final Iterator<?> it = root.getChildren();
        while (it != null && it.hasNext()) {
            final Object object = it.next();
            if (object instanceof OMElement) {
                final OMElement element = (OMElement) object;
                if (element.getLocalName().equals(localName) && hasNamespaceURI(element, namespaceURI)) {
                    return element;
                }
            }
        }
        return null;
    }

    public static OMElement getFirstGrandChildWithName(final OMElement root, final String localName) {
        if (root == null || localName == null || localName.trim().equals("")) {
            return null;
        }
        final Iterator it = root.getChildElements();
        if (it == null || !it.hasNext()) {
            return null;
        }
        OMElement result = null;
        result = getFirstChildWithName(root, localName);
        if (result != null) {
            return result;
        }

        Object obj = null;
        OMElement child = null;
        while (it.hasNext()) {
            obj = it.next();
            if (obj instanceof OMElement) {
                child = (OMElement) obj;
                result = getFirstGrandChildWithName(child, localName);
                if (result != null) {
                    return result;
                }
            }
        }

        return result;
    }

    // will be used by modules
    public static String getGrandChildAttributeValue(final OMElement root, final String localName,
                                                     final String attributeName) {
        final OMElement gc = getFirstGrandChildWithName(root, localName);
        if (gc == null) {
            return null;
        }
        return getAttributeValue(gc, attributeName);
    }

    // will be used by modules
    public static String getGrandChildValue(final OMElement root, final String localName) {
        final OMElement gc = getFirstGrandChildWithName(root, localName);
        if (gc == null) {
            return null;
        }
        return gc.getText();
    }

    // will be used by modules
    public static OMElement getGrandChildNameNS(final OMElement root, final String _localName,
                                                final String namespaceURI) {
        if (_localName == null || _localName.trim().equals("") || root == null) {
            return null;
        }
        final Iterator it = root.getChildElements();
        OMElement e = null;
        while (it != null && it.hasNext()) {
            e = (OMElement) it.next();
            if (e.getLocalName().equals(_localName) && hasNamespaceURI(e, namespaceURI)) {
                return e;
            }
            final OMElement temp = getGrandChildNameNS(e, _localName, namespaceURI);
            if (temp != null) {
                return temp;
            }
        }
        return null;
    }

    public static List<OMElement> getGrandChildrenNameNS(final OMElement root, final String _localName,
                                                         final String namespaceURI) {
        if (_localName == null || _localName.trim().equals("") || root == null) {
            return null;
        }
        final List<OMElement> result = new ArrayList<OMElement>();
        final Iterator it = root.getChildElements();
        OMElement e = null;
        while (it != null && it.hasNext()) {
            e = (OMElement) it.next();
            if (e.getLocalName().equals(_localName) && hasNamespaceURI(e, namespaceURI)) {
                result.add(e);
            }
            final List<OMElement> tmp = getGrandChildrenNameNS(e, _localName, namespaceURI);
            if (tmp != null && tmp.size() > 0) {
                result.addAll(tmp);
            }
        }
        return result;
    }

    public static List<OMElement> getGrandChildrenName(final OMElement root, final String _localName) {
        if (_localName == null || _localName.trim().equals("") || root == null) {
            return null;
        }
        final List<OMElement> result = new ArrayList<OMElement>();
        final Iterator it = root.getChildElements();
        OMElement e = null;
        while (it != null && it.hasNext()) {
            e = (OMElement) it.next();
            if (e.getLocalName().equals(_localName)) {
                result.add(e);
            }
            final List<OMElement> tmp = getGrandChildrenName(e, _localName);
            if (tmp != null && tmp.size() > 0) {
                result.addAll(tmp);
            }
        }
        return result;
    }

    private static boolean hasNamespaceURI(final OMElement e, final String uri) {
        if (e == null) {
            return false;
        }
        if (e.getNamespace() != null &&
            e.getNamespace().getNamespaceURI() != null &&
            uri != null && e.getNamespace().getNamespaceURI().equals(uri)) {
            return true;
        }
        return e.getNamespace() == null && (uri == null || uri.trim().equals(""));
    }

    public static org.jdom.Element toJdomElement(final OMElement omElement) {
        if (omElement == null) {
            return null;
        }
        omElement.build();
        final StringWriter sw = new StringWriter();
        writeTo(omElement, sw);
        final StringReader sr = new StringReader(sw.toString());
        try {
            final org.jdom.input.SAXBuilder builder = new org.jdom.input.SAXBuilder();
            return builder.build(sr).getRootElement();
        } catch (JDOMException ex) {
            log.error("Error while parsing XML", ex);
        } catch (IOException e) {
            log.error("I/O exception prevents document from being fully parsed", e);
        } finally {
            return null;
        }
    }

    public static OMElement toOMElement(final org.jdom.Element element) {
        if (element == null) {
            return null;
        }
        final String xml = toXML(element);
        final ByteArrayOutputStream out = new ByteArrayOutputStream();
        final PrintWriter pw = new PrintWriter(out);
        pw.write(xml);
        pw.close();
        final ByteArrayInputStream in = new ByteArrayInputStream(out.toByteArray());
        return rootElement(in);
    }

    public static String toXML(final org.jdom.Element element) {
        String xml = null;
        try {
            final org.jdom.output.XMLOutputter xo = new org.jdom.output.XMLOutputter();
            xo.setFormat(org.jdom.output.Format.getPrettyFormat());
            final StringWriter st = new StringWriter();
            xo.output(element, st);
            xml = st.toString();
        } catch (IOException e) {
            log.error("I/O exception prevents document from being fully parsed", e);
        }
        return xml;
    }

    public static SOAPEnvelope createEnvelope(final double soapVersion) {
        SOAPFactory omFactory = null;
        if (soapVersion < 1.2) {
            omFactory = OMAbstractFactory.getSOAP11Factory();
        } else {
            omFactory = OMAbstractFactory.getSOAP12Factory();
        }

        final SOAPEnvelope envelope = omFactory.getDefaultEnvelope();
        envelope.declareNamespace("http://www.w3.org/1999/XMLSchema-instance/", "xsi");
        envelope.declareNamespace("http://www.w3.org/1999/XMLSchema", "xsd");
        return envelope;
    }

    public static OMElement performXLST(final InputStream styleSheet, final OMElement source) {
        if (styleSheet == null || source == null) {
            return source;
        }
        try {
            final Transformer transformer =
                    TransformerFactory.newInstance().newTransformer(new StreamSource(styleSheet));
            final ByteArrayOutputStream baosForSource = new ByteArrayOutputStream();
            final XMLStreamWriter xsWriterForSource =
                    XMLOutputFactory.newInstance().createXMLStreamWriter(baosForSource);
            source.serialize(xsWriterForSource);
            final Source transformSrc = new StreamSource(new ByteArrayInputStream(baosForSource.toByteArray()));
            final ByteArrayOutputStream baosForTarget = new ByteArrayOutputStream();
            final StreamResult transformTgt = new StreamResult(baosForTarget);
            transformer.transform(transformSrc, transformTgt);
            final StAXOMBuilder builder = new StAXOMBuilder(new ByteArrayInputStream(baosForTarget.toByteArray()));
            return builder.getDocumentElement();
        } catch (XMLStreamException e) {
            log.error("Error while reading XML file");
        } catch (TransformerConfigurationException e) {
            log.error("XMl Transformer was not configured sufficiently");
        } catch (TransformerException e) {
            log.error("Error occured during XML Transformation", e);
        }
        return source;
    }

    public static org.jdom.Element performXLST(final InputStream stylesheetFile, final org.jdom.Element sourceDoc) {
        try {
            final TransformerFactory transformerFactory = TransformerFactory.newInstance();
            final Templates stylesheet = transformerFactory.newTemplates(new StreamSource(stylesheetFile));
            final Transformer processor = stylesheet.newTransformer();
            // Use I/O streams for source files
            final PipedInputStream sourceIn = new PipedInputStream();
            final PipedOutputStream sourceOut = new PipedOutputStream(sourceIn);
            final StreamSource source = new StreamSource(sourceIn);
            // Use I/O streams for output files
            final PipedInputStream resultIn = new PipedInputStream();
            final PipedOutputStream resultOut = new PipedOutputStream(resultIn);
            // Convert the output target for use in Xalan-J 2
            final StreamResult result = new StreamResult(resultOut);
            // Get a means for output of the JDOM Document
            final org.jdom.output.XMLOutputter xmlOutputter = new org.jdom.output.XMLOutputter();
            // Output to the I/O stream
            xmlOutputter.output(sourceDoc, sourceOut);
            sourceOut.close();
            // Feed the resultant I/O stream into the XSLT processor
            processor.transform(source, result);
            resultOut.close();
            // Convert the resultant transformed document back to JDOM
            final org.jdom.input.SAXBuilder builder = new org.jdom.input.SAXBuilder();
            final org.jdom.Document resultDoc = builder.build(resultIn);
            return resultDoc.getRootElement();
        } catch (TransformerConfigurationException ex) {
            log.error("XMl Transformer was not configured sufficiently");
        } catch (JDOMException e) {
            log.error("Error while parsing XML", e);
        } catch (TransformerException e) {
            log.error("Error occured during XML Transformation", e);
        } catch (IOException e) {
            log.error("I/O exception prevents document from being fully parsed", e);
        } finally {
            return null;
        }
    }

    public static OMElement performXLST(final String xsltFile, final OMElement source) {
        if (xsltFile == null || xsltFile.trim().equals("") || source == null) {
            return source;
        }
        final File xsl = new File(xsltFile);
        if (!xsl.exists()) {
            log.error("xslt file: " + xsltFile + " does not exist");
            return source;
        }
        try {
            return performXLST(new FileInputStream(xsltFile), source);
        } catch (Exception e) {
           log.error("Error while XSLT processing", e);
        }
        return source;
    }

    public static OMNode evaluate(final String xpathExpression, final MessageContext context) {
        try {
            final AXIOMXPath sourceXPath = new AXIOMXPath(xpathExpression);
            sourceXPath.addNamespace("SOAP-ENV", context.isSOAP11() ? SOAP11Constants.SOAP_ENVELOPE_NAMESPACE_URI :
                                                 SOAP12Constants.SOAP_ENVELOPE_NAMESPACE_URI);
            log.debug("Transformation against source element evaluated by : " + sourceXPath);
            final Object o = sourceXPath.evaluate(context.getEnvelope());
            if (o instanceof OMNode) {
                return (OMNode) o;
            } else if (o instanceof List && !((List) o).isEmpty()) {
                return (OMNode) ((List) o).get(0);  // Always fetches *only* the first
            }
        } catch (JaxenException e) {
            log.error("Syntax error while parsing xpathexpression", e);
        }
        return null;
    }

    public static Object evaluate(final String xpathExpression, final OMNode source) {
        if (source == null) {
            return null;
        }
        if (xpathExpression == null || xpathExpression.trim().equals("")) {
            return null;
        }
        try {
            final AXIOMXPath sourceXPath = new AXIOMXPath(xpathExpression);
      /*
      Object o = sourceXPath.evaluate(source);
      if (o instanceof OMNode) return (OMNode) o;
      if (o instanceof List && !((List) o).isEmpty())
         return (OMNode) ((List) o).get(0);
      if ( o == null ) return null;
      */
            log.debug(">>>>====== evaluating xpath " + xpathExpression);
            final List result = sourceXPath.selectNodes(source);
            if (result == null || result.size() == 0) {
                return null;
            }

            // Debugging:
            if (result.get(0) == null) {
                log.debug("node is null");
            } else {
                log.debug("node is not null");
            }
            log.debug("node's type is " + result.get(0).getClass().getName());
            if (result.get(0) instanceof OMAttribute) {
                log.debug("node is an instance of OMAttribute");
            } else {
                log.debug("node is not an instance of OMAttribute");
            }

            return result.get(0);
        } catch (JaxenException e) {
            log.error("Syntax error while parsing xpathexpression", e);
        }
        return null;
    }

    public static List<OMNode> evaluateAll(final String xpathExpression, final OMNode source) {
        if (source == null) {
            return null;
        }
        if (xpathExpression == null || xpathExpression.trim().equals("")) {
            return null;
        }
        final List<OMNode> result = new ArrayList<OMNode>();
        try {
            final AXIOMXPath sourceXPath = new AXIOMXPath(xpathExpression);
            final Object o = sourceXPath.evaluate(source);
            if (o instanceof OMNode) {
                result.add((OMNode) o);
                return result;
            }
            if (o instanceof List && !((List) o).isEmpty()) {
                return ((List<OMNode>) o);
            }

            if (o == null) {
                return null;
            }
        } catch (JaxenException e) {
            log.error("Syntax error while parsing xpathexpression", e);
        }
        return null;
    }

    public static OMElement performXLST(final URL xsltUrl, final String xpathExpression, final MessageContext context) {
        try {
            final OMNode sourceNode = evaluate(xpathExpression, context);
            if (sourceNode == null) {
                return null;
            }
            // create a transformer
            final Transformer transformer =
                    TransformerFactory.newInstance().newTransformer(new StreamSource(xsltUrl.openStream()));

            // create a byte array output stream and serialize the source node into it
            final ByteArrayOutputStream baosForSource = new ByteArrayOutputStream();
            final XMLStreamWriter xsWriterForSource =
                    XMLOutputFactory.newInstance().createXMLStreamWriter(baosForSource);

            log.debug("Transformation source : " + sourceNode);
            sourceNode.serialize(xsWriterForSource);
            final Source transformSrc = new StreamSource(new ByteArrayInputStream(baosForSource.toByteArray()));

            // create a new Stream result over a new BAOS..
            final ByteArrayOutputStream baosForTarget = new ByteArrayOutputStream();
            final StreamResult transformTgt = new StreamResult(baosForTarget);

            // perform transformation
            transformer.transform(transformSrc, transformTgt);
            final StAXOMBuilder builder = new StAXOMBuilder(new ByteArrayInputStream(baosForTarget.toByteArray()));
            return builder.getDocumentElement();
        } catch (TransformerConfigurationException ex) {
            log.error("XMl Transformer was not configured sufficiently");
        } catch (XMLStreamException e) {
            log.error("Error while reading XML file");
        } catch (TransformerException e) {
            log.error("Error occured during XML Transformation", e);
        } catch (IOException e) {
            log.error("I/O exception prevents document from being fully parsed", e);
        }
        return null;
    }

}
