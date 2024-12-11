package domiSMPTests.ui;

import ddsl.DomiSMPPage;
import ddsl.enums.Pages;
import ddsl.enums.ResourceTypes;
import domiSMPTests.SeleniumTest;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import org.testng.asserts.SoftAssert;
import pages.LoginPage;
import pages.administration.editResourcesPage.EditResourcePage;
import pages.search.ResourcesPage;
import rest.models.*;
import utils.TestRunData;
import utils.XMLUtils;

import java.util.Arrays;
import java.util.List;

public class SearchResourcesOgTests extends SeleniumTest {
    DomiSMPPage homePage;
    LoginPage loginPage;
    EditResourcePage editResourcePage;
    UserModel adminUser;
    DomainModel domainModel;
    GroupModel groupModel;
    SoftAssert soft;
    MemberModel adminMember;
    ResourcesPage resourcesPage;

    @BeforeMethod(alwaysRun = true)
    public void beforeTest() throws Exception {
        soft = new SoftAssert();
        domainModel = DomainModel.generatePublicDomainModelWithSML();
        adminUser = UserModel.generateUserWithADMINrole();
        groupModel = GroupModel.generatePublicGroup();

        adminMember = new MemberModel() {
        };
        adminMember.setUsername(adminUser.getUsername());
        adminMember.setRoleType("ADMIN");
        adminMember.setHasPermissionReview(true);

        MemberModel superMember = new MemberModel();
        superMember.setUsername(TestRunData.getInstance().getAdminUsername());
        superMember.setRoleType("ADMIN");

        //create user
        rest.users().createUser(adminUser).getString("userId");

        //create domain
        domainModel = rest.domains().createDomain(domainModel);

        //add users to domain
        rest.domains().addMembersToDomain(domainModel, adminMember);
        rest.domains().addMembersToDomain(domainModel, superMember);

        //add resources to domain
        List<ResourceTypes> resourcesToBeAdded = Arrays.asList(ResourceTypes.OASIS1, ResourceTypes.OASIS3, ResourceTypes.OASIS2);
        domainModel = rest.domains().addResourcesToDomain(domainModel, resourcesToBeAdded);

        //create group for domain
        groupModel = rest.domains().createGroupForDomain(domainModel, groupModel);

        //add users to groups
        rest.groups().addMembersToGroup(domainModel, groupModel, adminMember);


        homePage = new DomiSMPPage(driver);
        loginPage = homePage.goToLoginPage();
        loginPage.login(adminUser.getUsername(), TestRunData.getInstance().getNewPassword());
        resourcesPage = homePage.getSidebar().navigateTo(Pages.SEARCH_RESOURCES);
    }

    @Test(description = "SRCRES-09 User is able to Open URL for resource with OASIS 1.0")
    public void userIsAbleToOpenURLForResourceWithOASIS1() throws Exception {
        ResourceModel resourceModelOasis1 = ResourceModel.generatePublicResourceUnregisteredToSML();
        resourceModelOasis1.setResourceTypeIdentifier(ResourceTypes.OASIS1.getName());

        resourceModelOasis1 = rest.resources().createResourceForGroup(domainModel, groupModel, resourceModelOasis1);
        rest.resources().addMembersToResource(domainModel, groupModel, resourceModelOasis1, adminMember);

        XMLUtils documentXML = resourcesPage.openURLResouceDocument(resourceModelOasis1.getIdentifierValue(), resourceModelOasis1.getIdentifierScheme());

        soft.assertEquals(documentXML.getNodeValue("ParticipantIdentifier"), resourceModelOasis1.getIdentifierValue(), "ParticipantIdentifier value is wrong");
        soft.assertAll();
    }

    @Test(description = "SRCRES-09 User is able to Open URL for resource with OASIS 1.0")
    public void userIsAbleToOpenURLForResourceWithOASIS2() throws Exception {
        ResourceModel resourceModelOasis2 = ResourceModel.generatePublicResourceUnregisteredToSML();
        resourceModelOasis2.setResourceTypeIdentifier(ResourceTypes.OASIS2.getName());

        resourceModelOasis2 = rest.resources().createResourceForGroup(domainModel, groupModel, resourceModelOasis2);
        rest.resources().addMembersToResource(domainModel, groupModel, resourceModelOasis2, adminMember);

        XMLUtils documentXML = resourcesPage.openURLResouceDocument(resourceModelOasis2.getIdentifierValue(), resourceModelOasis2.getIdentifierScheme());

        soft.assertEquals(documentXML.getNodeValue("ParticipantID"), resourceModelOasis2.getIdentifierValue(), "ParticipantIdentifier value is wrong");
        soft.assertAll();
    }

    @Test(description = "SRCRES-09 User is able to Open URL for resource with OASIS 1.0")
    public void userIsAbleToOpenURLForResourceWithOASIS3() throws Exception {
        ResourceModel resourceModelOasis3 = ResourceModel.generatePublicResourceUnregisteredToSML();
        resourceModelOasis3.setResourceTypeIdentifier(ResourceTypes.OASIS3.getName());

        resourceModelOasis3 = rest.resources().createResourceForGroup(domainModel, groupModel, resourceModelOasis3);
        rest.resources().addMembersToResource(domainModel, groupModel, resourceModelOasis3, adminMember);

        XMLUtils documentXML = resourcesPage.openURLResouceDocument(resourceModelOasis3.getIdentifierValue(), resourceModelOasis3.getIdentifierScheme());

        soft.assertEquals(documentXML.getNodeValue("PartyName"), resourceModelOasis3.getIdentifierValue(), "ParticipantIdentifier value is wrong");
        soft.assertAll();
    }

}