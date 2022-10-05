package eu.europa.ec.smp.api;

import org.junit.Test;
import org.oasis_open.docs.bdxr.ns.smp._2016._05.DocumentIdentifier;
import org.oasis_open.docs.bdxr.ns.smp._2016._05.ParticipantIdentifierType;
import org.oasis_open.docs.bdxr.ns.smp._2016._05.ProcessIdentifier;
import org.oasis_open.docs.bdxr.ns.smp._2016._05.ServiceGroup;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import java.io.InputStream;

import static org.junit.Assert.*;

/**
 * Created by gutowpa on 17/01/2017.
 */
public class GeneratedCodeTest {

    private static final String SCHEME = "The sample Identifier Scheme";
    private static final String VALUE = "The sample Identifier Value";

    @Test
    public void testValueConstructorWasGeneratedForParticipantId() {
        //when
        ParticipantIdentifierType id = new ParticipantIdentifierType(VALUE, SCHEME);

        //then
        assertEquals(SCHEME, id.getScheme());
        assertEquals(VALUE, id.getValue());
    }

    @Test
    public void testValueConstructorWasGeneratedForDocumentId() {
        //when
        DocumentIdentifier id = new DocumentIdentifier(VALUE, SCHEME);

        //then
        assertEquals(SCHEME, id.getScheme());
        assertEquals(VALUE, id.getValue());
    }

    @Test
    public void testValueConstructorWasGeneratedForProcessId() {
        //when
        ProcessIdentifier id = new ProcessIdentifier(VALUE, SCHEME);

        //then
        assertEquals(SCHEME, id.getScheme());
        assertEquals(VALUE, id.getValue());
    }

    @Test
    public void testGeneratedEqualsIsNotAwareOfWhitespaceAndCommentChanges() throws JAXBException {
        //given
        ServiceGroup serviceGroupA = loadServiceGroup("/ServiceGroupA.xml");
        ServiceGroup serviceGroupB = loadServiceGroup("/ServiceGroupB.xml");

        //when-then
        assertTrue(serviceGroupA.equals(serviceGroupB));
    }

    @Test
    public void testGeneratedEqualsIsAwareOfContentChanges() throws JAXBException {
        //given
        ServiceGroup serviceGroupA = loadServiceGroup("/ServiceGroupA.xml");
        ServiceGroup serviceGroupC = loadServiceGroup("/ServiceGroupC.xml");

        //when-then
        assertFalse(serviceGroupA.equals(serviceGroupC));
    }

    public static ServiceGroup loadServiceGroup(String path) throws JAXBException {
        InputStream inputStream = IdentifiersTest.class.getResourceAsStream(path);
        JAXBContext jaxbContext = JAXBContext.newInstance(ServiceGroup.class);
        Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();
        Object serviceGroup = unmarshaller.unmarshal(inputStream);
        return (ServiceGroup) serviceGroup;
    }

}
