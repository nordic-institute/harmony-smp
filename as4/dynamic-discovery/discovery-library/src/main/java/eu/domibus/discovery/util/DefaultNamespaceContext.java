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
package eu.domibus.discovery.util;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.xml.XMLConstants;
import javax.xml.namespace.NamespaceContext;

/**
 * Maps prefixes to namespaces and vice versa.
 * 
 * <p>Java 6 does not provide a default implementation for this.</p>
 *
 * <p>Features:</p>
 * <ul>
 * 	<li>Users can add, query and remove custom prefixes/namespace assignments.</li>
 * 	<li>Users can modify and query the default XML namespace.</li>
 * 	<li>The default namespaces XML and XMLNS can be queried but neither modified nor removed.</li>
 * </ul>
 * 
 * @author Thorsten Niedzwetzki
 */
public class DefaultNamespaceContext implements NamespaceContext {

	/** Assigns each prefix to its namespace. */
	private final Map<String,String> prefixToNamespace = new HashMap<String,String>();
	
	/** Assigns each namespace to all prefixes that are assigned to this namespace */
	private final Map<String,List<String>> namespaceToPrefixes = new HashMap<String,List<String>>();

	/** These namespaces cannot be modified or removed */
	private static final List<String> PROTECTED_NAMESPACES = Arrays.asList(
			XMLConstants.XML_NS_URI, XMLConstants.XMLNS_ATTRIBUTE_NS_URI);

	/** These namespaces prefixes cannot be removed. */
	private static final List<String> PROTECTED_PREFIXES = Arrays.asList(
			XMLConstants.XML_NS_PREFIX, XMLConstants.XMLNS_ATTRIBUTE, XMLConstants.DEFAULT_NS_PREFIX);


	/**
	 * Assign default namespaces and prefixes.
	 */
	public DefaultNamespaceContext() {
		prefixToNamespace.put(XMLConstants.XML_NS_PREFIX, XMLConstants.XML_NS_URI);
		prefixToNamespace.put(XMLConstants.XMLNS_ATTRIBUTE, XMLConstants.XMLNS_ATTRIBUTE_NS_URI);
		prefixToNamespace.put(XMLConstants.DEFAULT_NS_PREFIX, XMLConstants.NULL_NS_URI);
		
		final List<String> xmlNamespaces = new LinkedList<String>();
		xmlNamespaces.add(XMLConstants.XML_NS_PREFIX);
		namespaceToPrefixes.put(XMLConstants.XML_NS_URI, xmlNamespaces);

		final List<String> xmlnsNamespaces = new LinkedList<String>();
		xmlnsNamespaces.add(XMLConstants.XMLNS_ATTRIBUTE);
		namespaceToPrefixes.put(XMLConstants.XMLNS_ATTRIBUTE_NS_URI, xmlnsNamespaces);

		final List<String> xmlDefaultNamespaces = new LinkedList<String>();
		xmlDefaultNamespaces.add(XMLConstants.DEFAULT_NS_PREFIX);
		namespaceToPrefixes.put(XMLConstants.NULL_NS_URI, xmlDefaultNamespaces);
	}


	@Override
	public String getNamespaceURI(final String prefix) {
		if (prefix == null) {
			throw new IllegalArgumentException();
		}
		final String namespaceURI = prefixToNamespace.get(prefix);
		return namespaceURI == null ? XMLConstants.NULL_NS_URI : namespaceURI;
	}


	@Override
	public String getPrefix(final String namespaceURI) {
		if (namespaceURI == null) {
			throw new IllegalArgumentException();
		}
		final List<String> prefixes = namespaceToPrefixes.get(namespaceURI);
		return prefixes == null ? null : prefixes.get(0);
	}


	@Override
	public Iterator<?> getPrefixes(final String namespaceURI) {
		if (namespaceURI == null) {
			throw new IllegalArgumentException();
		}
		final List<String> prefixes = namespaceToPrefixes.get(namespaceURI);
		return prefixes == null ? Collections.emptyList().iterator() : prefixes.iterator();
	}


	/**
	 * Assigns the prefix to a namespace URI.
	 * 
	 * <p>Any previous assignment of this prefix is replaced by the new one.</p> 
	 * 
	 * @param prefix prefix to assign to the given namespace URI
	 * @param namespaceURI namespace URI to assign to the given prefix
	 */
	public void addNamespace(final String prefix, final String namespaceURI) {
		prefixToNamespace.put(prefix, namespaceURI);
		List<String> prefixes = namespaceToPrefixes.get(namespaceURI);
		if (prefixes == null) {
			prefixes = new LinkedList<String>();
		}
		prefixes.add(prefix);
		namespaceToPrefixes.put(namespaceURI, prefixes);
	}


	/**
	 * Retrieves all namespaces that are assigned to the given prefix.
	 * 
	 * @param prefix the prefix to query for assigned namespaces
	 * @return non-empty list of assigned namespaces or {@code null} if this prefix is not assigned
	 */
	public String findNamespaceByPrefix(final String prefix) {
		return prefixToNamespace.get(prefix);
	}


	/**
	 * Retrieves a prefix that is assigned to the given namespace.
	 * 
	 * @param namespaceURI the namespace URI to query for an assigned prefix
	 * @return a prefix that is assigned to the given namespace URI or {@code null} if the namespace URI is not assigned
	 */
	public String findPrefixByNamespace(final String namespaceURI) {
		final List<String> prefixes = namespaceToPrefixes.get(namespaceURI);
		return prefixes != null ? prefixes.get(0) : null;
	}


	/**
	 * Retrieves al prefixes that are assigned to the given namespace.
	 * 
	 * @param namespaceURI the namespace URI to query for assigned prefixes
	 * @return all prefixes for the given namespace URI or an empty list if the namespace URI is unknown
	 */
	public List<String> findPrefixesByNamespace(final String namespaceURI) {
		final List<String> prefixes = namespaceToPrefixes.get(namespaceURI);
		return prefixes != null ? prefixes : Collections.<String>emptyList();
	}


	/**
	 * Removes all assignments of the given prefix.
	 * 
	 * <p>This also removes any namespaces that are assigned to this prefix only.</p>
	 * 
	 * @param prefix all assignments of this prefix will be removed
	 */
	public void removePrefix(final String prefix) {
		if (PROTECTED_PREFIXES.contains(prefix))
			throw new IllegalArgumentException("Cannot remove protected prefix " + prefix);		
		final String namespaceURI = prefixToNamespace.get(prefix);
		if (namespaceURI != null) {
			prefixToNamespace.remove(prefix);
			final List<String> prefixes = namespaceToPrefixes.get(namespaceURI);
			prefixes.remove(prefix);
			if (prefixes.isEmpty()) {
				namespaceToPrefixes.remove(namespaceURI);
			}
			if (getDefaultNamespace() == null) {
				setDefaultNamespace(null);
			}
		}
	}


	/**
	 * Removes all assignments of the given namespace URI.
	 * 
	 * <p>This also removes any prefixes that are assigned to this namespace URI.</p>
	 * 
	 * @param namespaceURI all assignments of this namespace URI will be removed
	 */
	public void removeNamespace(final String namespaceURI) {
		if (PROTECTED_NAMESPACES.contains(namespaceURI))
			throw new IllegalArgumentException("Cannot remove protected namespace URI " + namespaceURI);
		final List<String> prefixes = namespaceToPrefixes.get(namespaceURI);
		if (prefixes != null) {
			namespaceToPrefixes.remove(namespaceURI);
			for (final String prefix : prefixes) {
				prefixToNamespace.remove(prefix);
			}
			if (prefixes.contains(XMLConstants.DEFAULT_NS_PREFIX)) {
				setDefaultNamespace(null);
			}
		}
	}


	/**
	 * Sets the namespace URI for the default namespace (which has no prefix).
	 * 
	 * @param namespaceURI default namespace URI or {@code null} to reset the default namespace to its default value.
	 * @see #getDefaultNamespace()
	 */
	public void setDefaultNamespace(final String namespaceURI) {
		final String oldNamespaceURI = prefixToNamespace.remove(XMLConstants.DEFAULT_NS_PREFIX);
		if (oldNamespaceURI != null) {
			final List<String> prefixes = namespaceToPrefixes.get(oldNamespaceURI);
			if (prefixes.size() == 1) {
				namespaceToPrefixes.remove(oldNamespaceURI);
			} else {
				prefixes.remove(XMLConstants.DEFAULT_NS_PREFIX);
			}
		}
		addNamespace(XMLConstants.DEFAULT_NS_PREFIX,
				namespaceURI == null ? XMLConstants.NULL_NS_URI : namespaceURI);
	}


	/**
	 * Retrieves the default namespace URI.
	 * 
	 * @return the default namespace URI
	 * @see #setDefaultNamespace(String)
	 */
	public String getDefaultNamespace() {
		return prefixToNamespace.get(XMLConstants.DEFAULT_NS_PREFIX);
	}


	@Override
	public String toString() {
		return DefaultNamespaceContext.class.getSimpleName() +
				" [prefixToNamespace=" + prefixToNamespace +
				", namespaceToPrefixes=" + namespaceToPrefixes + "]";
	}

}
