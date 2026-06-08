package pages.administration.editResourcesPage.editResourceDocumentPage;

import utils.XMLUtils;

import javax.xml.parsers.ParserConfigurationException;

public class ResourceDocumentEditor extends XMLUtils {
    public ResourceDocumentEditor(String xmlStr) throws ParserConfigurationException {
        super(xmlStr);
    }

    public void addExtensionTag() {
        addNewNode("ServiceGroup", "Extension", "");
        setAttributeValueForNode("Extension", "xmlns", "http://docs.oasis-open.org/bdxr/ns/SMP/2016/05");
    }

    public void addNewExtesionCode(String nodeName, String attributXmlnsValue, String nodeValue) {

        addNewNode("Extension", "ext:" + nodeName, nodeValue);
        setAttributeValueForNode("ext:" + nodeName, "xmlns:ext", attributXmlnsValue);

    }
}
