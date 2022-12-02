package eu.europa.ec.smp.api.identifiers;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.oasis_open.docs.bdxr.ns.smp._2016._05.DocumentIdentifier;

import java.util.Arrays;
import java.util.Collection;

/**
 * @author Joze Rihtarsic
 * @since 5.0
 */
@RunWith(Parameterized.class)
public class DocumentIdentifierFormatterTest {

    @Parameterized.Parameters(name = "{index}: {0}")
    public static Collection documentIdentifierCases() {
        return Arrays.asList(new Object[][]{
                {"Basic example",
                        new DocumentIdentifier("b", "a"),
                        "a::b",
                        "a%3A%3Ab",
                        "a::b",
                        false},
                {"Double separator example",
                        new DocumentIdentifier("b::c", "a"),
                        "a::b::c",
                        "a%3A%3Ab%3A%3Ac",
                        "a::b::c",
                        false},
                {"Char : in scheme colon basic",
                        new DocumentIdentifier("c","a:b"),
                        "a:b::c",
                        "a%3Ab%3A%3Ac",
                        "a:b::c",
                        false},
                {"No Scheme Identifier ",
                        new DocumentIdentifier("NoSchemeIdentifier", null),
                        "::NoSchemeIdentifier",
                        "%3A%3ANoSchemeIdentifier",
                        "NoSchemeIdentifier",
                        false},
                {"No Scheme Identifier with ::",
                        new DocumentIdentifier("NoSchemeIdentifier01", null),
                        "::NoSchemeIdentifier01",
                        "%3A%3ANoSchemeIdentifier01",
                        "::NoSchemeIdentifier01",
                        false},
                {"No Scheme Identifier with no scheme and ::",
                        new DocumentIdentifier("NoSchemeIdentifier01::test", null),
                        "::NoSchemeIdentifier01::test",
                        "%3A%3ANoSchemeIdentifier01%3A%3Atest",
                        "::NoSchemeIdentifier01::test",
                        false},
                {"Example with ## and double colon",
                        new DocumentIdentifier("urn::epsos##services:extended:epsos::51", "ehealth-resid-qns"),
                        "ehealth-resid-qns::urn::epsos##services:extended:epsos::51",
                        "ehealth-resid-qns%3A%3Aurn%3A%3Aepsos%23%23services%3Aextended%3Aepsos%3A%3A51",
                        "ehealth-resid-qns::urn::epsos##services:extended:epsos::51",
                        false},
                {"Identifier with spaces -  formatted to uri with '%20",
                        new DocumentIdentifier("urn ncpb test", "ehealth-actorid-qns"),
                        "ehealth-actorid-qns::urn ncpb test",
                        "ehealth-actorid-qns%3A%3Aurn%20ncpb%20test",
                        "ehealth-actorid-qns::urn ncpb test",
                        false},
                {"Example 01 (parse spaces)",new DocumentIdentifier("urn:ehealth:pt:ncpb-idp","scheme"),
                        "scheme::urn:ehealth:pt:ncpb-idp",
                        "scheme%3A%3Aurn%3Aehealth%3Apt%3Ancpb-idp",
                        " scheme::urn:ehealth:pt:ncpb-idp",
                        false},
                {"Example 02 (parse spaces)",new DocumentIdentifier("urn:ehealth:be:ncpb-idp","otherscheme"),
                        "otherscheme::urn:ehealth:be:ncpb-idp",
                        "otherscheme%3A%3Aurn%3Aehealth%3Abe%3Ancpb-idp" ,
                        "otherscheme::urn:ehealth:be:ncpb-idp ",
                        false},
                {"Example 03 (parse spaces in argument)",new DocumentIdentifier("urn:ehealth:IdentityService::XCPD::CrossGatewayPatientDiscovery##ITI-55","ehealth-resid-qns"),
                        "ehealth-resid-qns::urn:ehealth:IdentityService::XCPD::CrossGatewayPatientDiscovery##ITI-55",
                        "ehealth-resid-qns%3A%3Aurn%3Aehealth%3AIdentityService%3A%3AXCPD%3A%3ACrossGatewayPatientDiscovery%23%23ITI-55",
                        "ehealth-resid-qns:: urn:ehealth:IdentityService::XCPD::CrossGatewayPatientDiscovery##ITI-55 ",
                        false},
                {"Example 04",new DocumentIdentifier( "urn:XCPD::CrossGatewayPatientDiscovery", "ehealth-resid-qns"),
                        "ehealth-resid-qns::urn:XCPD::CrossGatewayPatientDiscovery",
                        "ehealth-resid-qns%3A%3Aurn%3AXCPD%3A%3ACrossGatewayPatientDiscovery",
                        "ehealth-resid-qns::urn:XCPD::CrossGatewayPatientDiscovery",
                        false},
                {"Example 05",new DocumentIdentifier("urn:ehealth:PatientService::XCA::CrossGatewayQuery##ITI-38","ehealth-resid-qns"),
                        "ehealth-resid-qns::urn:ehealth:PatientService::XCA::CrossGatewayQuery##ITI-38",
                        "ehealth-resid-qns%3A%3Aurn%3Aehealth%3APatientService%3A%3AXCA%3A%3ACrossGatewayQuery%23%23ITI-38",
                        "ehealth-resid-qns::urn:ehealth:PatientService::XCA::CrossGatewayQuery##ITI-38",
                        false},
                {"Example 06",new DocumentIdentifier("urn:XCA::CrossGatewayQuery", "ehealth-resid-qns"),
                        "ehealth-resid-qns::urn:XCA::CrossGatewayQuery",
                        "ehealth-resid-qns%3A%3Aurn%3AXCA%3A%3ACrossGatewayQuery",
                        "ehealth-resid-qns::urn:XCA::CrossGatewayQuery",
                        false},
                {"Example 07",new DocumentIdentifier("urn:ehealth:OrderService::XCA::CrossGatewayQuery##ITI-38","ehealth-resid-qns"),
                        "ehealth-resid-qns::urn:ehealth:OrderService::XCA::CrossGatewayQuery##ITI-38",
                        "ehealth-resid-qns%3A%3Aurn%3Aehealth%3AOrderService%3A%3AXCA%3A%3ACrossGatewayQuery%23%23ITI-38",
                        "ehealth-resid-qns::urn:ehealth:OrderService::XCA::CrossGatewayQuery##ITI-38",
                        false},
                {"Example 08",new DocumentIdentifier("urn:ehealth:DispensationService:Initialize::XDR::ProvideandRegisterDocumentSet-b##ITI-41","ehealth-resid-qns"),
                        "ehealth-resid-qns::urn:ehealth:DispensationService:Initialize::XDR::ProvideandRegisterDocumentSet-b##ITI-41",
                        "ehealth-resid-qns%3A%3Aurn%3Aehealth%3ADispensationService%3AInitialize%3A%3AXDR%3A%3AProvideandRegisterDocumentSet-b%23%23ITI-41",
                        "ehealth-resid-qns::urn:ehealth:DispensationService:Initialize::XDR::ProvideandRegisterDocumentSet-b##ITI-41",
                        false},
                {"Example 09",new DocumentIdentifier("urn:XDR::ProvideandRegisterDocumentSet-b","ehealth-resid-qns"),
                        "ehealth-resid-qns::urn:XDR::ProvideandRegisterDocumentSet-b",
                        "ehealth-resid-qns%3A%3Aurn%3AXDR%3A%3AProvideandRegisterDocumentSet-b",
                        "ehealth-resid-qns::urn:XDR::ProvideandRegisterDocumentSet-b",
                        false},
                {"Example 10",new DocumentIdentifier("urn:ehealth:DispensationService:Discard::XDR::ProvideandRegisterDocumentSet-b##ITI-41","ehealth-resid-qns"),
                        "ehealth-resid-qns::urn:ehealth:DispensationService:Discard::XDR::ProvideandRegisterDocumentSet-b##ITI-41",
                        "ehealth-resid-qns%3A%3Aurn%3Aehealth%3ADispensationService%3ADiscard%3A%3AXDR%3A%3AProvideandRegisterDocumentSet-b%23%23ITI-41",
                        "ehealth-resid-qns::urn:ehealth:DispensationService:Discard::XDR::ProvideandRegisterDocumentSet-b##ITI-41",
                        false},

        });
    }

    // input parameters
    @Parameterized.Parameter
    public String name;
    @Parameterized.Parameter(1)
    public DocumentIdentifier identifierType;
    @Parameterized.Parameter(2)
    public String formattedIdentifier;
    @Parameterized.Parameter(3)
    public String uriFormattedIdentifier;
    @Parameterized.Parameter(4)
    public String identifierToParse;
    @Parameterized.Parameter(5)
    public boolean throwParseError;

    DocumentIdentifierFormatter testInstance = new DocumentIdentifierFormatter();

    @Test
    public void testFormat() {

        String result = testInstance.format(identifierType);
        String uriResult = testInstance.urlEncodedFormat(identifierType);

        Assert.assertEquals(formattedIdentifier, result);
        Assert.assertEquals(uriFormattedIdentifier, uriResult);
    }

    @Test
    public void testParse() {
        IllegalArgumentException exception = null;
        DocumentIdentifier result = null;
        if (throwParseError) {
            exception = Assert.assertThrows(IllegalArgumentException.class, () -> testInstance.parse(identifierToParse));
        } else {
            result = testInstance.parse(identifierToParse);
        }

        Assert.assertNotNull(throwParseError ? exception : result);
        if (!throwParseError) {
            Assert.assertEquals(identifierType.getScheme(), result.getScheme());
            Assert.assertEquals(identifierType.getValue(), result.getValue());
        }
    }
}