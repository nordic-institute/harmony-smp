/*
 * Licensed under the EUPL, Version 1.1 or - as soon they will be approved by the
 * European Commission - subsequent versions of the EUPL (the "Licence");
 * 
 * You may not use this work except in compliance with the Licence.
 * 
 * You may obtain a copy of the Licence at:
 * http://ec.europa.eu/idabc/eupl
 * 
 * Unless required by applicable law or agreed to in writing, software distributed
 * under the Licence is distributed on an "AS IS" basis, 
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * 
 * See the Licence for the specific language governing permissions and limitations
 * under the Licence.
 */
package eu.domibus.discovery.handlers;

import java.text.MessageFormat;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import eu.domibus.discovery.DiscoveryException;
import eu.domibus.discovery.locatorClient.LocatorClient;
import eu.domibus.discovery.util.DefaultNamespaceContext;

/**
 * This default implementation for a metadata handlers simplifies building custom metadata handlers.
 * 
 * <p>It provides a default implementation for configuration and provides convenience methods
 * for XPath evaluation.</p>
 *
 * @author Thorsten Niedzwetzki
 */
public abstract class DefaultMetadataHandler implements MetadataHandler {

	protected LocatorClient locatorClient;
	protected DocumentBuilder documentBuilder;
	protected int maxRecursions;
	private XPath xpath;
	private DefaultNamespaceContext namespaces;


	@Override
	public void configure(
			final LocatorClient locatorClient,
			final DocumentBuilder documentBuilder,
			final XPathFactory xpathFactory,
			final int maxRecursions) {
		this.locatorClient = locatorClient;
		this.documentBuilder = documentBuilder;
		this.xpath = xpathFactory.newXPath();
		this.namespaces = new DefaultNamespaceContext();
		configureNamespaces(namespaces);
		xpath.setNamespaceContext(namespaces);
		this.maxRecursions = maxRecursions;
	}
	
	
	/**
	 * Add your namespaces to the given namespace context
	 */
	public void configureNamespaces(final DefaultNamespaceContext namespaces) {
		// No namespaces needed per default.
	}


	/**
	 * Evaluates an XPath expression.
	 * Throws an exception if the expression cannot be evaluated or if there is no result.
	 * 
	 * <p>The XPath expression can include placeholders {0}, {1} and so forth.</p>
	 * 
	 * <p>Include the values to populate the placeholders as additional arguments.</p>
	 * 
	 * @param context the node which to apply the XPath expression to (e. g. document root node)
	 * @param expression the XPath expression to evaluate, optionally including placeholders
	 * @param arguments values to populate the placesholders with
	 * @return the first Node that could be found using the given XPath expression
	 * @throws XPathExpressionException on any syntax error in the XPath expression
	 * @throws XPathNullResultException if the XPath expression yields no result.
	 */
	protected Node safeXPath(final Node context, final String expression, final Object... arguments)
			throws XPathExpressionException, XPathNullResultException {
		final String formattedExpression = MessageFormat.format(expression, arguments);
		final Node node = (Node) xpath.evaluate(formattedExpression, context, XPathConstants.NODE);
		if (node == null) {
			throw new XPathNullResultException("No result for expression " + formattedExpression);
		}
		return node;
	}

	/**
	 * Evaluates an XPath expression.
	 * Throws an exception if the expression cannot be evaluated or if there is no result.
	 * 
	 * <p>The XPath expression can include placeholders {0}, {1} and so forth.</p>
	 * 
	 * <p>Include the values to populate the placeholders as additional arguments.</p>
	 * 
	 * @param context the node which to apply the XPath expression to (e. g. document root node)
	 * @param expression the XPath expression to evaluate, optionally including placeholders
	 * @param arguments values to populate the placesholders with
	 * @return the complete NodeList that could be found using the given XPath expression
	 * @throws XPathExpressionException on any syntax error in the XPath expression
	 * @throws XPathNullResultException if the XPath expression yields no result.
	 */
	protected NodeList safeXPathNodeList(final Node context, final String expression, final Object... arguments)
			throws XPathExpressionException, XPathNullResultException {
		final String formattedExpression = MessageFormat.format(expression, arguments);
		final NodeList nodelist = (NodeList) xpath.evaluate(formattedExpression, context, XPathConstants.NODESET);
		if (nodelist == null) {
			throw new XPathNullResultException("No result for expression " + formattedExpression);
		}
		return nodelist;
	}
	

	/**
	 * Evaluates an XPath expression.
	 * 
	 * Throws an exception if the expression cannot be evaluated.
	 * Returns {@code null} if there is no result.
	 * 
	 * <p>The XPath expression can include placeholders {0}, {1} and so forth.</p>
	 * 
	 * <p>Include the values to populate the placeholders as additional arguments.</p>
	 * 
	 * @param context the node which to apply the XPath expression to (e. g. document root node)
	 * @param expression the XPath expression to evaluate, optionally including placeholders
	 * @param arguments values to populate the placesholders with
	 * @return the first Node that could be found using the given XPath expression or {@code null}
	 * @throws XPathExpressionException on any syntax error in the XPath expression
	 */
	protected Node unsafeXPath(final Node context, final String expression, final Object... arguments)
			throws XPathExpressionException {
		final String formattedExpression = MessageFormat.format(expression, arguments);
		return (Node) xpath.evaluate(formattedExpression, context, XPathConstants.NODE);
	}


	/**
	 * The {@link DefaultMetadataHandler#safeXPath(Node, String, Object...)} method throws
	 * this exception if an XPath expression yields no result.
	 *
	 * @author Thorsten Niedzwetzki
	 */
	public static class XPathNullResultException extends DiscoveryException {
		private static final long serialVersionUID = -2636850907501526044L;
		public XPathNullResultException(final String message) { super(message); }
	}

}
