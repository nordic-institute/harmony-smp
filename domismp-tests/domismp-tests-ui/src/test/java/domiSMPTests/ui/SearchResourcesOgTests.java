package domiSMPTests.ui;

import ddsl.DomiSMPPage;
import ddsl.enums.Pages;
import ddsl.enums.ResourceTypes;
import domiSMPTests.SeleniumTest;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import org.testng.asserts.SoftAssert;
import pages.LoginPage;
import pages.search.ResourcesPage;
import rest.models.*;
import utils.TestRunData;
import utils.XMLUtils;

import java.util.Arrays;
import java.util.List;

public class SearchResourcesOgTests extends SeleniumTest {
    DomiSMPPage homePage;
    LoginPage loginPage;
    UserModel userAdmin;
    DomainModel domainModel;
    GroupModel groupModel;
    SoftAssert soft;
    MemberModel memberAdmin;
    ResourcesPage resourcesPage;
    MemberModel memberSuper;
    @BeforeMethod(alwaysRun = true)
    public void beforeTest() throws Exception {
        soft = new SoftAssert();
        domainModel = DomainModel.generatePublicDomainModelWithSML();
        userAdmin = UserModel.generateUserWithADMINrole();
        groupModel = GroupModel.generatePublicGroup();

        memberAdmin = new MemberModel() {
        };
        memberAdmin.setUsername(userAdmin.getUsername());
        memberAdmin.setRoleType("ADMIN");
        memberAdmin.setHasPermissionReview(true);

        memberSuper = new MemberModel();
        memberSuper.setUsername(TestRunData.getInstance().getAdminUsername());
        memberSuper.setRoleType("ADMIN");

        //create user
        rest.users().createUser(userAdmin).getString("userId");

        //create domain
        domainModel = rest.domains().createDomain(domainModel);

        //add users to domain
        rest.domains().addMembersToDomain(domainModel, memberAdmin);
        rest.domains().addMembersToDomain(domainModel, memberSuper);

        //add resources to domain
        List<ResourceTypes> resourcesTypesToBeAdded = Arrays.asList(ResourceTypes.OASIS1, ResourceTypes.OASIS3, ResourceTypes.OASIS2);
        domainModel = rest.domains().addResourcesToDomain(domainModel, resourcesTypesToBeAdded);

        //create group for domain
        groupModel = rest.domains().createGroupForDomain(domainModel, groupModel);

        //add users to groups
        rest.groups().addMembersToGroup(domainModel, groupModel, memberAdmin);


        homePage = new DomiSMPPage(driver);
        loginPage = homePage.goToLoginPage();
        loginPage.login(userAdmin.getUsername(), TestRunData.getInstance().getNewPassword());
        resourcesPage = homePage.getSidebar().navigateTo(Pages.SEARCH_RESOURCES);
    }

    @Test(description = "SRCRES-01 Private resource can be seen only by members of the resource")
    public void privateResourceCanBeSeenOnlyByMembersOfResource() throws Exception {

        DomainModel domainPublic = DomainModel.generatePublicDomainModelWithSML();
        DomainModel domainPrivate = DomainModel.generatePrivateDomainModelWithoutSML();

        GroupModel groupPublic = GroupModel.generatePublicGroup();
        GroupModel groupPrivate = GroupModel.generatePublicGroup();

        //Generating resources for the 4 scenarios Public domain-Public group, Public Domain-private group, Private domain-public group, Private domain-private group
        ResourceModel resource_for_publicDomain_publicGroup = ResourceModel.generatePrivateResource(ResourceTypes.OASIS1);
        ResourceModel resource_for_publicDomain_privateGroup = ResourceModel.generatePrivateResource(ResourceTypes.OASIS1);
        ResourceModel resource_for_privateDomain_publicGroup = ResourceModel.generatePrivateResource(ResourceTypes.OASIS1);
        ResourceModel resource_for_privateDomain_privateGroup = ResourceModel.generatePrivateResource(ResourceTypes.OASIS1);

        //generating  users
        UserModel userDomain = UserModel.generateUserWithADMINrole();
        UserModel userGroup = UserModel.generateUserWithADMINrole();
        UserModel userResource = UserModel.generateUserWithADMINrole();

        //generating member models
        MemberModel memberDomain = new MemberModel();
        memberDomain.setUsername(userDomain.getUsername());
        memberDomain.setRoleType("ADMIN");

        MemberModel memberGroup = new MemberModel();
        memberGroup.setUsername(userGroup.getUsername());
        memberGroup.setRoleType("ADMIN");

        MemberModel memberResource = new MemberModel();
        memberResource.setUsername(userResource.getUsername());
        memberResource.setRoleType("ADMIN");

        //create user
        rest.users().createUser(userDomain);
        rest.users().createUser(userGroup);
        rest.users().createUser(userResource);

        //create domains public and private
        domainPublic = rest.domains().createDomain(domainPublic);
        domainPrivate = rest.domains().createDomain(domainPrivate);

        //add users to domains
        rest.domains().addMembersToDomain(domainPublic, memberDomain);
        rest.domains().addMembersToDomain(domainPublic, memberSuper);

        rest.domains().addMembersToDomain(domainPrivate, memberDomain);
        rest.domains().addMembersToDomain(domainPrivate, memberSuper);

        //add resource types to domains
        List<ResourceTypes> resourcesTypesToBeAdded = Arrays.asList(ResourceTypes.OASIS1, ResourceTypes.OASIS3, ResourceTypes.OASIS2);
        domainPublic = rest.domains().addResourcesToDomain(domainPublic, resourcesTypesToBeAdded);
        domainPrivate = rest.domains().addResourcesToDomain(domainPrivate, resourcesTypesToBeAdded);

        //create groups for domains
        GroupModel groupPublic_for_publicDomain = rest.domains().createGroupForDomain(domainPublic, groupPublic);
        GroupModel groupPrivate_for_publicDomain = rest.domains().createGroupForDomain(domainPublic, groupPrivate);

        GroupModel groupPublic_for_privateDomain = rest.domains().createGroupForDomain(domainPrivate, groupPublic);
        GroupModel groupPrivate_for_privateDomain = rest.domains().createGroupForDomain(domainPrivate, groupPrivate);

        //add users to groups
        rest.groups().addMembersToGroup(domainPublic, groupPublic_for_publicDomain, memberGroup);
        rest.groups().addMembersToGroup(domainPublic, groupPrivate_for_publicDomain, memberGroup);

        rest.groups().addMembersToGroup(domainPrivate, groupPublic_for_privateDomain, memberGroup);
        rest.groups().addMembersToGroup(domainPrivate, groupPrivate_for_privateDomain, memberGroup);

        //add resources to groups
        resource_for_publicDomain_publicGroup = rest.resources().createResourceForGroup(domainPublic, groupPublic_for_publicDomain, resource_for_publicDomain_publicGroup);
        resource_for_publicDomain_privateGroup = rest.resources().createResourceForGroup(domainPublic, groupPrivate_for_publicDomain, resource_for_publicDomain_privateGroup);
        resource_for_privateDomain_publicGroup = rest.resources().createResourceForGroup(domainPrivate, groupPublic_for_privateDomain, resource_for_privateDomain_publicGroup);
        resource_for_privateDomain_privateGroup = rest.resources().createResourceForGroup(domainPrivate, groupPrivate_for_privateDomain, resource_for_privateDomain_privateGroup);

        //Add resources member to resource
        rest.resources().addMembersToResource(domainPublic, groupPublic_for_publicDomain, resource_for_publicDomain_publicGroup, memberResource);
        rest.resources().addMembersToResource(domainPublic, groupPrivate_for_publicDomain, resource_for_publicDomain_privateGroup, memberResource);
        rest.resources().addMembersToResource(domainPrivate, groupPublic_for_privateDomain, resource_for_privateDomain_publicGroup, memberResource);
        rest.resources().addMembersToResource(domainPrivate, groupPrivate_for_privateDomain, resource_for_privateDomain_privateGroup, memberResource);

        //Check is private resource is available WITHOUT login
        resourcesPage.logout();
        soft.assertFalse(resourcesPage.isResourcePresent(resource_for_publicDomain_publicGroup.getIdentifierValue(), resource_for_publicDomain_publicGroup.getIdentifierScheme())
                , "Private resource of Public Domain and Public Group is accessible for not logged in users.");
        soft.assertFalse(resourcesPage.isResourcePresent(resource_for_publicDomain_privateGroup.getIdentifierValue(), resource_for_publicDomain_privateGroup.getIdentifierScheme())
                , "Private resource of Public Domain and Private Group is accessible for not logged in users.");
        soft.assertFalse(resourcesPage.isResourcePresent(resource_for_privateDomain_publicGroup.getIdentifierValue(), resource_for_privateDomain_publicGroup.getIdentifierScheme())
                , "Private resource of Private Domain and Public Group is accessible for not logged in users.");
        soft.assertFalse(resourcesPage.isResourcePresent(resource_for_privateDomain_privateGroup.getIdentifierValue(), resource_for_privateDomain_privateGroup.getIdentifierScheme())
                , "Private resource of Private Domain and Public Group is accessible for not logged in users.");


        //Check is private resource is available for resource admin for the 4 scenarios
        loginPage = homePage.goToLoginPage();
        loginPage.login(userResource.getUsername(), TestRunData.getInstance().getNewPassword());
        resourcesPage = homePage.getSidebar().navigateTo(Pages.SEARCH_RESOURCES);
        soft.assertTrue(resourcesPage.isResourcePresent(resource_for_publicDomain_publicGroup.getIdentifierValue(), resource_for_publicDomain_publicGroup.getIdentifierScheme())
                , "Private resource of Public Domain and Public groups is not available for resource user.");
        soft.assertTrue(resourcesPage.isResourcePresent(resource_for_publicDomain_privateGroup.getIdentifierValue(), resource_for_publicDomain_privateGroup.getIdentifierScheme())
                , "Private resource of Public Domain and Private groups is not available for resource user");
        soft.assertTrue(resourcesPage.isResourcePresent(resource_for_privateDomain_publicGroup.getIdentifierValue(), resource_for_privateDomain_publicGroup.getIdentifierScheme())
                , "Private resource of Private Domain and Public groups is not available for resource user");
        soft.assertTrue(resourcesPage.isResourcePresent(resource_for_privateDomain_privateGroup.getIdentifierValue(), resource_for_privateDomain_privateGroup.getIdentifierScheme())
                , "Private resource of Private Domain and Private groups is not available for resource user");


        //Check is private resource is available for group admin for the 4 scenarios
        resourcesPage.logout();
        loginPage = homePage.goToLoginPage();
        loginPage.login(userGroup.getUsername(), TestRunData.getInstance().getNewPassword());
        resourcesPage = homePage.getSidebar().navigateTo(Pages.SEARCH_RESOURCES);
        soft.assertFalse(resourcesPage.isResourcePresent(resource_for_publicDomain_publicGroup.getIdentifierValue(), resource_for_publicDomain_publicGroup.getIdentifierScheme())
                , "Private resource of Public Domain and Public groups is not available for domain user.");
        soft.assertFalse(resourcesPage.isResourcePresent(resource_for_publicDomain_privateGroup.getIdentifierValue(), resource_for_publicDomain_privateGroup.getIdentifierScheme())
                , "Private resource of Public Domain and Private groups is not available for domain user");
        soft.assertFalse(resourcesPage.isResourcePresent(resource_for_privateDomain_publicGroup.getIdentifierValue(), resource_for_privateDomain_publicGroup.getIdentifierScheme())
                , "Private resource of Private Domain and Public groups is not available for domain user");
        soft.assertFalse(resourcesPage.isResourcePresent(resource_for_privateDomain_privateGroup.getIdentifierValue(), resource_for_privateDomain_privateGroup.getIdentifierScheme())
                , "Private resource of Private Domain and Private groups is not available for domain user");

        //Check is private resource is available for domain admin for the 4 scenarios
        resourcesPage.logout();
        loginPage = homePage.goToLoginPage();
        loginPage.login(userDomain.getUsername(), TestRunData.getInstance().getNewPassword());
        resourcesPage = homePage.getSidebar().navigateTo(Pages.SEARCH_RESOURCES);
        soft.assertFalse(resourcesPage.isResourcePresent(resource_for_publicDomain_publicGroup.getIdentifierValue(), resource_for_publicDomain_publicGroup.getIdentifierScheme())
                , "Private resource of Public Domain and Public groups is not available for group user.");
        soft.assertFalse(resourcesPage.isResourcePresent(resource_for_publicDomain_privateGroup.getIdentifierValue(), resource_for_publicDomain_privateGroup.getIdentifierScheme())
                , "Private resource of Public Domain and Private groups is not available for group user");
        soft.assertFalse(resourcesPage.isResourcePresent(resource_for_privateDomain_publicGroup.getIdentifierValue(), resource_for_privateDomain_publicGroup.getIdentifierScheme())
                , "Private resource of Private Domain and Public groups is not available for group user");
        soft.assertFalse(resourcesPage.isResourcePresent(resource_for_privateDomain_privateGroup.getIdentifierValue(), resource_for_privateDomain_privateGroup.getIdentifierScheme())
                , "Private resource of Private Domain and Private groups is not available for group user");
        soft.assertAll();
    }

    @Test(description = "SRCRES-02 Public resources from public domain and public group can bee seen by anyone")
    public void publicResourcesOfPublicDomainAndPublicGroupCanBeSeenByAnyone() throws Exception {

        DomainModel domainPublic = DomainModel.generatePublicDomainModelWithoutSML();

        GroupModel groupPublic = GroupModel.generatePublicGroup();
        GroupModel groupPublic2 = GroupModel.generatePublicGroup();

        //Generating resources for the 4 scenarios Public domain-Public group, Public Domain-private group, Private domain-public group, Private domain-private group
        ResourceModel resourcePublic = ResourceModel.generatePublicResource(ResourceTypes.OASIS1);
        ResourceModel resourcePublic2 = ResourceModel.generatePublicResource(ResourceTypes.OASIS1);

        //generating  users
        UserModel userDomainAdmin = UserModel.generateUserWithADMINrole();
        UserModel userGroupAdmin = UserModel.generateUserWithADMINrole();
        UserModel userResourceAdmin = UserModel.generateUserWithADMINrole();

        UserModel userDomainViewer = UserModel.generateUserWithUSERrole();
        UserModel userGroupViewer = UserModel.generateUserWithUSERrole();
        UserModel userResourceViewer = UserModel.generateUserWithUSERrole();

        UserModel userDomainAdminDomain2 = UserModel.generateUserWithUSERrole();
        UserModel userGroupAdminGroup2 = UserModel.generateUserWithUSERrole();
        UserModel userResourceAdminResource2 = UserModel.generateUserWithUSERrole();


        //generating member models
        MemberModel memberDomainAdmin = new MemberModel();
        memberDomainAdmin.setUsername(userDomainAdmin.getUsername());
        memberDomainAdmin.setRoleType("ADMIN");

        MemberModel memberGroupAdmin = new MemberModel();
        memberGroupAdmin.setUsername(userGroupAdmin.getUsername());
        memberGroupAdmin.setRoleType("ADMIN");

        MemberModel memberResourceAdmin = new MemberModel();
        memberResourceAdmin.setUsername(userResourceAdmin.getUsername());
        memberResourceAdmin.setRoleType("ADMIN");

        MemberModel memberDomainViewer = new MemberModel();
        memberDomainViewer.setUsername(userDomainViewer.getUsername());
        memberDomainViewer.setRoleType("VIEWER");

        MemberModel memberGroupViewer = new MemberModel();
        memberGroupViewer.setUsername(userGroupViewer.getUsername());
        memberGroupViewer.setRoleType("VIEWER");

        MemberModel memberResourceViewer = new MemberModel();
        memberResourceViewer.setUsername(userResourceViewer.getUsername());
        memberResourceViewer.setRoleType("VIEWER");

        MemberModel memberDomainAdminForDomain2 = new MemberModel();
        memberDomainAdminForDomain2.setUsername(userDomainAdminDomain2.getUsername());
        memberDomainAdminForDomain2.setRoleType("ADMIN");

        MemberModel memberGroupAdminforGroup2 = new MemberModel();
        memberGroupAdminforGroup2.setUsername(userGroupAdminGroup2.getUsername());
        memberGroupAdminforGroup2.setRoleType("ADMIN");

        MemberModel memberResourceAdminForResource2 = new MemberModel();
        memberResourceAdminForResource2.setUsername(userResourceAdminResource2.getUsername());
        memberResourceAdminForResource2.setRoleType("ADMIN");


        //create user
        rest.users().createUser(userDomainAdmin);
        rest.users().createUser(userGroupAdmin);
        rest.users().createUser(userResourceAdmin);
        rest.users().createUser(userDomainViewer);
        rest.users().createUser(userGroupViewer);
        rest.users().createUser(userResourceViewer);
        rest.users().createUser(userDomainAdminDomain2);
        rest.users().createUser(userGroupAdminGroup2);
        rest.users().createUser(userResourceAdminResource2);

        //create domain
        domainPublic = rest.domains().createDomain(domainPublic);

        //add users to domains
        rest.domains().addMembersToDomain(domainPublic, memberDomainAdmin);
        rest.domains().addMembersToDomain(domainPublic, memberDomainViewer);
        rest.domains().addMembersToDomain(domainPublic, memberSuper);

        rest.domains().addMembersToDomain(domainPublic, memberDomainAdminForDomain2);


        //add resource types to domains
        List<ResourceTypes> resourcesTypesToBeAdded = Arrays.asList(ResourceTypes.OASIS1, ResourceTypes.OASIS3, ResourceTypes.OASIS2);
        domainPublic = rest.domains().addResourcesToDomain(domainPublic, resourcesTypesToBeAdded);

        //create groups for domains
        groupPublic = rest.domains().createGroupForDomain(domainPublic, groupPublic);
        groupPublic2 = rest.domains().createGroupForDomain(domainPublic, groupPublic2);

        //add users to groups
        rest.groups().addMembersToGroup(domainPublic, groupPublic, memberGroupAdmin);
        rest.groups().addMembersToGroup(domainPublic, groupPublic, memberGroupViewer);

        rest.groups().addMembersToGroup(domainPublic, groupPublic2, memberGroupAdminforGroup2);

        //add resources to groups
        resourcePublic = rest.resources().createResourceForGroup(domainPublic, groupPublic, resourcePublic);
        resourcePublic2 = rest.resources().createResourceForGroup(domainPublic, groupPublic2, resourcePublic2);


        //Add resources member to resource
        rest.resources().addMembersToResource(domainPublic, groupPublic, resourcePublic, memberResourceAdmin);
        rest.resources().addMembersToResource(domainPublic, groupPublic, resourcePublic, memberResourceViewer);
        rest.resources().addMembersToResource(domainPublic, groupPublic2, resourcePublic2, memberResourceAdminForResource2);

        //Check is Public resource of Public domain and Public group is available WITHOUT login
        resourcesPage.logout();
        soft.assertTrue(resourcesPage.isResourcePresent(resourcePublic.getIdentifierValue(), resourcePublic.getIdentifierScheme())
                , "Public resource of Public domain and Public group is not accessible for not logged in users.");


        //Check if Public resource of Public domain and Public group can be seen by Resource Admin
        loginPage = homePage.goToLoginPage();
        loginPage.login(userResourceAdmin.getUsername(), TestRunData.getInstance().getNewPassword());
        resourcesPage = homePage.getSidebar().navigateTo(Pages.SEARCH_RESOURCES);
        soft.assertTrue(resourcesPage.isResourcePresent(resourcePublic.getIdentifierValue(), resourcePublic.getIdentifierScheme())
                , "Public resource of Public Domain and Public groups is not available for resource admin");

        //Check if Public resource of Public domain and Public group can be seen by Resource viewer
        resourcesPage.logout();
        loginPage = homePage.goToLoginPage();
        loginPage.login(userResourceViewer.getUsername(), TestRunData.getInstance().getNewPassword());
        resourcesPage = homePage.getSidebar().navigateTo(Pages.SEARCH_RESOURCES);
        soft.assertTrue(resourcesPage.isResourcePresent(resourcePublic.getIdentifierValue(), resourcePublic.getIdentifierScheme())
                , "Public resource of Public Domain and Public groups is not available for resource viewer");

        //Check if Public resource of Public domain and Public group can be seen by Group Admin
        resourcesPage.logout();
        loginPage = homePage.goToLoginPage();
        loginPage.login(userGroupAdmin.getUsername(), TestRunData.getInstance().getNewPassword());
        resourcesPage = homePage.getSidebar().navigateTo(Pages.SEARCH_RESOURCES);
        soft.assertTrue(resourcesPage.isResourcePresent(resourcePublic.getIdentifierValue(), resourcePublic.getIdentifierScheme())
                , "Public resource of Public Domain and Public groups is not available for group admin");

        //Check if Public resource of Public domain and Public group can be seen by Group viewer
        resourcesPage.logout();
        loginPage = homePage.goToLoginPage();
        loginPage.login(userGroupViewer.getUsername(), TestRunData.getInstance().getNewPassword());
        resourcesPage = homePage.getSidebar().navigateTo(Pages.SEARCH_RESOURCES);
        soft.assertTrue(resourcesPage.isResourcePresent(resourcePublic.getIdentifierValue(), resourcePublic.getIdentifierScheme())
                , "Public resource of Public Domain and Public groups is not available for group viewer!");

        //Check if Public resource of Public domain and Public group can be seen by Domain Admin
        resourcesPage.logout();
        loginPage = homePage.goToLoginPage();
        loginPage.login(userDomainAdmin.getUsername(), TestRunData.getInstance().getNewPassword());
        resourcesPage = homePage.getSidebar().navigateTo(Pages.SEARCH_RESOURCES);
        soft.assertTrue(resourcesPage.isResourcePresent(resourcePublic.getIdentifierValue(), resourcePublic.getIdentifierScheme())
                , "Public resource of Public Domain and Public groups is not available for domain admin!");

        //Check if Public resource of Public domain and Public group can be seen by Domain viewer
        resourcesPage.logout();
        loginPage = homePage.goToLoginPage();
        loginPage.login(memberDomainViewer.getUsername(), TestRunData.getInstance().getNewPassword());
        resourcesPage = homePage.getSidebar().navigateTo(Pages.SEARCH_RESOURCES);
        soft.assertTrue(resourcesPage.isResourcePresent(resourcePublic.getIdentifierValue(), resourcePublic.getIdentifierScheme())
                , "Public resource of Public Domain and Public groups is not available for domain viewer!");

        //Check if Public resource of Public domain and Public group can be seen by Domain admin of other domain
        resourcesPage.logout();
        loginPage = homePage.goToLoginPage();
        loginPage.login(userDomainAdminDomain2.getUsername(), TestRunData.getInstance().getNewPassword());
        resourcesPage = homePage.getSidebar().navigateTo(Pages.SEARCH_RESOURCES);
        soft.assertTrue(resourcesPage.isResourcePresent(resourcePublic.getIdentifierValue(), resourcePublic.getIdentifierScheme())
                , "Public resource of Public Domain and Public groups is not available for domain admin of other domain!");

        //Check if Public resource of Public domain and Public group can be seen by group admin of other group
        resourcesPage.logout();
        loginPage = homePage.goToLoginPage();
        loginPage.login(userGroupAdminGroup2.getUsername(), TestRunData.getInstance().getNewPassword());
        resourcesPage = homePage.getSidebar().navigateTo(Pages.SEARCH_RESOURCES);
        soft.assertTrue(resourcesPage.isResourcePresent(resourcePublic.getIdentifierValue(), resourcePublic.getIdentifierScheme())
                , "Public resource of Public Domain and Public groups is not available for group admin of other group!");

        //Check if Public resource of Public domain and Public group can be seen by group admin of other group
        resourcesPage.logout();
        loginPage = homePage.goToLoginPage();
        loginPage.login(userResourceAdminResource2.getUsername(), TestRunData.getInstance().getNewPassword());
        resourcesPage = homePage.getSidebar().navigateTo(Pages.SEARCH_RESOURCES);
        soft.assertTrue(resourcesPage.isResourcePresent(resourcePublic.getIdentifierValue(), resourcePublic.getIdentifierScheme())
                , "Public resource of Public Domain and Public groups is not available for resource admin of other resource!");

        soft.assertAll();
    }


    @Test(description = "SRCRES-03 Public resources of Private groups can be seen by Group members and resource members")
    public void publicResourcesOfPrivateGroupsCanBeSeenByGroupMembersAndResourceMembers() throws Exception {


        DomainModel domainPublic = DomainModel.generatePublicDomainModelWithoutSML();

        GroupModel groupPrivate = GroupModel.generatePrivateGroup();
        GroupModel groupPrivateSameDomain = GroupModel.generatePrivateGroup();

        //Generating resources for Public resource Private group Public Domain
        ResourceModel resource_for_publicDomain_privateGroup = ResourceModel.generatePublicResource(ResourceTypes.OASIS1);

        //generating  users
        UserModel domainAdminUserUnderTest = UserModel.generateUserWithADMINrole();
        UserModel groupAdminUserUnderTest = UserModel.generateUserWithADMINrole();
        UserModel resourceAdminUserUnderTest = UserModel.generateUserWithADMINrole();

        UserModel domainViewerUserUnderTest = UserModel.generateUserWithUSERrole();
        UserModel groupViewerUserUnderTest = UserModel.generateUserWithUSERrole();
        UserModel resourceViewerUserUnderTest = UserModel.generateUserWithUSERrole();

        UserModel groupAdminUserForGroup2 = UserModel.generateUserWithUSERrole();


        //generating member models
        MemberModel superMember = new MemberModel();
        superMember.setUsername(TestRunData.getInstance().getAdminUsername());
        superMember.setRoleType("ADMIN");

        MemberModel domainAdminMember = new MemberModel();
        domainAdminMember.setUsername(domainAdminUserUnderTest.getUsername());
        domainAdminMember.setRoleType("ADMIN");

        MemberModel groupAdminMember = new MemberModel();
        groupAdminMember.setUsername(groupAdminUserUnderTest.getUsername());
        groupAdminMember.setRoleType("ADMIN");

        MemberModel resourceAdminMember = new MemberModel();
        resourceAdminMember.setUsername(resourceAdminUserUnderTest.getUsername());
        resourceAdminMember.setRoleType("ADMIN");


        MemberModel domainViewerMember = new MemberModel();
        domainViewerMember.setUsername(domainViewerUserUnderTest.getUsername());
        domainViewerMember.setRoleType("VIEWER");

        MemberModel groupViewerMember = new MemberModel();
        groupViewerMember.setUsername(groupViewerUserUnderTest.getUsername());
        groupViewerMember.setRoleType("VIEWER");

        MemberModel resourceViewerMember = new MemberModel();
        resourceViewerMember.setUsername(resourceViewerUserUnderTest.getUsername());
        resourceViewerMember.setRoleType("VIEWER");

        MemberModel groupAdminForGroup2SameDomainMember = new MemberModel();
        groupAdminForGroup2SameDomainMember.setUsername(groupAdminUserForGroup2.getUsername());
        groupAdminForGroup2SameDomainMember.setRoleType("ADMIN");

        //create user
        rest.users().createUser(domainAdminUserUnderTest);
        rest.users().createUser(groupAdminUserUnderTest);
        rest.users().createUser(resourceAdminUserUnderTest);
        rest.users().createUser(domainViewerUserUnderTest);
        rest.users().createUser(groupViewerUserUnderTest);
        rest.users().createUser(resourceViewerUserUnderTest);
        rest.users().createUser(groupAdminUserForGroup2);

        //create domain
        domainPublic = rest.domains().createDomain(domainPublic);


        //add users to domains
        rest.domains().addMembersToDomain(domainPublic, domainAdminMember);
        rest.domains().addMembersToDomain(domainPublic, domainViewerMember);
        rest.domains().addMembersToDomain(domainPublic, superMember);

        //add resource types to domains
        List<ResourceTypes> resourcesTypesToBeAdded = Arrays.asList(ResourceTypes.OASIS1, ResourceTypes.OASIS3, ResourceTypes.OASIS2);
        domainPublic = rest.domains().addResourcesToDomain(domainPublic, resourcesTypesToBeAdded);

        //create groups for domains
        GroupModel groupPrivate_for_publicDomain = rest.domains().createGroupForDomain(domainPublic, groupPrivate);
        GroupModel groupPrivate2_for_publicDomain = rest.domains().createGroupForDomain(domainPublic, groupPrivateSameDomain);

        //add users to groups
        rest.groups().addMembersToGroup(domainPublic, groupPrivate_for_publicDomain, groupAdminMember);
        rest.groups().addMembersToGroup(domainPublic, groupPrivate_for_publicDomain, groupViewerMember);

        rest.groups().addMembersToGroup(domainPublic, groupPrivate2_for_publicDomain, groupAdminForGroup2SameDomainMember);


        //add resources to groups
        resource_for_publicDomain_privateGroup = rest.resources().createResourceForGroup(domainPublic, groupPrivate_for_publicDomain, resource_for_publicDomain_privateGroup);

        //Add resources member to resource
        rest.resources().addMembersToResource(domainPublic, groupPrivate_for_publicDomain, resource_for_publicDomain_privateGroup, resourceAdminMember);
        rest.resources().addMembersToResource(domainPublic, groupPrivate_for_publicDomain, resource_for_publicDomain_privateGroup, resourceViewerMember);


        //Check is Public resource of Private domain is not available WITHOUT login
        resourcesPage.logout();
        soft.assertFalse(resourcesPage.isResourcePresent(resource_for_publicDomain_privateGroup.getIdentifierValue(), resource_for_publicDomain_privateGroup.getIdentifierScheme())
                , "Public resource of Public domain and Private group is accessible for not logged in users!");


        //Check if Public resource of Private group can be seen by Resource Admin
        loginPage = homePage.goToLoginPage();
        loginPage.login(resourceAdminUserUnderTest.getUsername(), TestRunData.getInstance().getNewPassword());
        resourcesPage = homePage.getSidebar().navigateTo(Pages.SEARCH_RESOURCES);
        soft.assertTrue(resourcesPage.isResourcePresent(resource_for_publicDomain_privateGroup.getIdentifierValue(), resource_for_publicDomain_privateGroup.getIdentifierScheme())
                , "Public resource of Public Domain and private groups is not available for resource admin!");

        //Check if Public resource of Private group can be seen by Resource viewer
        resourcesPage.logout();
        loginPage = homePage.goToLoginPage();
        loginPage.login(resourceViewerUserUnderTest.getUsername(), TestRunData.getInstance().getNewPassword());
        resourcesPage = homePage.getSidebar().navigateTo(Pages.SEARCH_RESOURCES);
        soft.assertTrue(resourcesPage.isResourcePresent(resource_for_publicDomain_privateGroup.getIdentifierValue(), resource_for_publicDomain_privateGroup.getIdentifierScheme())
                , "Public resource of Public Domain and Private groups is not available for resource viewer");

        //Check if Public resource of Private group can be seen by Group Admin
        resourcesPage.logout();
        loginPage = homePage.goToLoginPage();
        loginPage.login(groupAdminUserUnderTest.getUsername(), TestRunData.getInstance().getNewPassword());
        resourcesPage = homePage.getSidebar().navigateTo(Pages.SEARCH_RESOURCES);
        soft.assertTrue(resourcesPage.isResourcePresent(resource_for_publicDomain_privateGroup.getIdentifierValue(), resource_for_publicDomain_privateGroup.getIdentifierScheme())
                , "Public resource of Public Domain and Private groups is not available for group admin");

        //Check if Public resource of Private group can be seen by Group viewer
        resourcesPage.logout();
        loginPage = homePage.goToLoginPage();
        loginPage.login(groupViewerUserUnderTest.getUsername(), TestRunData.getInstance().getNewPassword());
        resourcesPage = homePage.getSidebar().navigateTo(Pages.SEARCH_RESOURCES);
        soft.assertTrue(resourcesPage.isResourcePresent(resource_for_publicDomain_privateGroup.getIdentifierValue(), resource_for_publicDomain_privateGroup.getIdentifierScheme())
                , "Public resource of Public Domain and Private groups is not available for group viewer!");

        //Check if Public resource of Private group can be seen by Domain Admin
        resourcesPage.logout();
        loginPage = homePage.goToLoginPage();
        loginPage.login(domainAdminUserUnderTest.getUsername(), TestRunData.getInstance().getNewPassword());
        resourcesPage = homePage.getSidebar().navigateTo(Pages.SEARCH_RESOURCES);
        soft.assertFalse(resourcesPage.isResourcePresent(resource_for_publicDomain_privateGroup.getIdentifierValue(), resource_for_publicDomain_privateGroup.getIdentifierScheme())
                , "Public resource of Public Domain and Private groups is not available for domain admin!");


        //Check if Public resource of Private group can be seen by Domain viewer
        resourcesPage.logout();
        loginPage = homePage.goToLoginPage();
        loginPage.login(domainViewerMember.getUsername(), TestRunData.getInstance().getNewPassword());
        resourcesPage = homePage.getSidebar().navigateTo(Pages.SEARCH_RESOURCES);
        soft.assertFalse(resourcesPage.isResourcePresent(resource_for_publicDomain_privateGroup.getIdentifierValue(), resource_for_publicDomain_privateGroup.getIdentifierScheme())
                , "Public resource of Public Domain and Private groups is not available for domain viewer!");


        //Check if Public resource of Private group can be seen by Domain admin of other group of same domain
        resourcesPage.logout();
        loginPage = homePage.goToLoginPage();
        loginPage.login(groupAdminForGroup2SameDomainMember.getUsername(), TestRunData.getInstance().getNewPassword());
        resourcesPage = homePage.getSidebar().navigateTo(Pages.SEARCH_RESOURCES);
        soft.assertFalse(resourcesPage.isResourcePresent(resource_for_publicDomain_privateGroup.getIdentifierValue(), resource_for_publicDomain_privateGroup.getIdentifierScheme())
                , "Private resource of Public Domain and Private groups is available for domain admin of other domain!");

        soft.assertAll();
    }

    @Test(description = "SRCRES-04 Public resources of Private DOMAIN can be seen by Domain member, Group member and Resource member")
    public void publicResourcesOfPrivateDomainCanBeSeenByDomainMemberGroupMemberAndResourceMember() throws Exception {

        DomainModel domainPrivate = DomainModel.generatePrivateDomainModelWithoutSML();
        DomainModel domain2 = DomainModel.generatePrivateDomainModelWithoutSML();

        GroupModel groupPublic = GroupModel.generatePublicGroup();
        GroupModel groupPrivate = GroupModel.generatePublicGroup();

        //Generating resources
        ResourceModel resource_for_privateDomain_publicGroup = ResourceModel.generatePublicResource(ResourceTypes.OASIS1);
        ResourceModel resource_for_privateDomain_privateGroup = ResourceModel.generatePublicResource(ResourceTypes.OASIS1);

        //generating users
        UserModel userDomainAdmin = UserModel.generateUserWithADMINrole();
        UserModel userGroupAdmin = UserModel.generateUserWithADMINrole();
        UserModel userResourceAdmin = UserModel.generateUserWithADMINrole();

        UserModel userDomainViewer = UserModel.generateUserWithUSERrole();
        UserModel userGroupViewer = UserModel.generateUserWithUSERrole();
        UserModel userResourceViewer = UserModel.generateUserWithUSERrole();

        UserModel userDomainAdminDomain2 = UserModel.generateUserWithUSERrole();

        //generating member models
        MemberModel memberDomainAdmin = new MemberModel();
        memberDomainAdmin.setUsername(userDomainAdmin.getUsername());
        memberDomainAdmin.setRoleType("ADMIN");

        MemberModel memberGroupAdmin = new MemberModel();
        memberGroupAdmin.setUsername(userGroupAdmin.getUsername());
        memberGroupAdmin.setRoleType("ADMIN");

        MemberModel memberResourceAdmin = new MemberModel();
        memberResourceAdmin.setUsername(userResourceAdmin.getUsername());
        memberResourceAdmin.setRoleType("ADMIN");

        MemberModel memberDomainViewer = new MemberModel();
        memberDomainViewer.setUsername(userDomainViewer.getUsername());
        memberDomainViewer.setRoleType("VIEWER");

        MemberModel memberGroupViewer = new MemberModel();
        memberGroupViewer.setUsername(userGroupViewer.getUsername());
        memberGroupViewer.setRoleType("VIEWER");

        MemberModel memberResourceViewer = new MemberModel();
        memberResourceViewer.setUsername(userResourceViewer.getUsername());
        memberResourceViewer.setRoleType("VIEWER");

        MemberModel memberDomainAdminDomain2 = new MemberModel();
        memberDomainAdminDomain2.setUsername(userDomainAdminDomain2.getUsername());
        memberDomainAdminDomain2.setRoleType("ADMIN");

        //create user
        rest.users().createUser(userDomainAdmin);
        rest.users().createUser(userGroupAdmin);
        rest.users().createUser(userResourceAdmin);
        rest.users().createUser(userDomainViewer);
        rest.users().createUser(userGroupViewer);
        rest.users().createUser(userResourceViewer);
        rest.users().createUser(userDomainAdminDomain2);

        //create domain
        domainPrivate = rest.domains().createDomain(domainPrivate);
        domain2 = rest.domains().createDomain(domain2);

        //add users to domains
        rest.domains().addMembersToDomain(domainPrivate, memberDomainAdmin);
        rest.domains().addMembersToDomain(domainPrivate, memberDomainViewer);
        rest.domains().addMembersToDomain(domainPrivate, memberSuper);

        rest.domains().addMembersToDomain(domain2, memberDomainAdminDomain2);
        rest.domains().addMembersToDomain(domain2, memberSuper);

        //add resource types to domains
        List<ResourceTypes> resourcesTypesToBeAdded = Arrays.asList(ResourceTypes.OASIS1, ResourceTypes.OASIS3, ResourceTypes.OASIS2);
        domainPrivate = rest.domains().addResourcesToDomain(domainPrivate, resourcesTypesToBeAdded);

        //create groups for domains
        GroupModel groupPublic_for_privateDomain = rest.domains().createGroupForDomain(domainPrivate, groupPublic);
        GroupModel groupPrivate_for_privateDomain = rest.domains().createGroupForDomain(domainPrivate, groupPrivate);

        //add users to groups
        rest.groups().addMembersToGroup(domainPrivate, groupPublic_for_privateDomain, memberGroupAdmin);
        rest.groups().addMembersToGroup(domainPrivate, groupPrivate_for_privateDomain, memberGroupAdmin);
        rest.groups().addMembersToGroup(domainPrivate, groupPublic_for_privateDomain, memberGroupViewer);
        rest.groups().addMembersToGroup(domainPrivate, groupPrivate_for_privateDomain, memberGroupViewer);

        //add resources to groups
        resource_for_privateDomain_publicGroup = rest.resources().createResourceForGroup(domainPrivate, groupPublic_for_privateDomain, resource_for_privateDomain_publicGroup);
        resource_for_privateDomain_privateGroup = rest.resources().createResourceForGroup(domainPrivate, groupPrivate_for_privateDomain, resource_for_privateDomain_privateGroup);

        //Add resources member to resource
        rest.resources().addMembersToResource(domainPrivate, groupPublic_for_privateDomain, resource_for_privateDomain_publicGroup, memberResourceAdmin);
        rest.resources().addMembersToResource(domainPrivate, groupPrivate_for_privateDomain, resource_for_privateDomain_privateGroup, memberResourceAdmin);

        rest.resources().addMembersToResource(domainPrivate, groupPublic_for_privateDomain, resource_for_privateDomain_publicGroup, memberResourceViewer);
        rest.resources().addMembersToResource(domainPrivate, groupPrivate_for_privateDomain, resource_for_privateDomain_privateGroup, memberResourceViewer);

        //Check is Public resource of Public domain is not available WITHOUT login
        resourcesPage.logout();
        soft.assertFalse(resourcesPage.isResourcePresent(resource_for_privateDomain_publicGroup.getIdentifierValue(), resource_for_privateDomain_publicGroup.getIdentifierScheme())
                , "Public resource of private domain and public group is not accessible for not logged in users.");
        soft.assertFalse(resourcesPage.isResourcePresent(resource_for_privateDomain_privateGroup.getIdentifierValue(), resource_for_privateDomain_privateGroup.getIdentifierScheme())
                , "Public resource of private domain and private group is not accessible for not logged in users.");

        //Check if Public resource of Private domain can be seen by Resource Admin
        loginPage = homePage.goToLoginPage();
        loginPage.login(userResourceAdmin.getUsername(), TestRunData.getInstance().getNewPassword());
        resourcesPage = homePage.getSidebar().navigateTo(Pages.SEARCH_RESOURCES);
        soft.assertTrue(resourcesPage.isResourcePresent(resource_for_privateDomain_publicGroup.getIdentifierValue(), resource_for_privateDomain_publicGroup.getIdentifierScheme())
                , "Public resource of Private Domain and Public groups is not available for resource admin");
        soft.assertTrue(resourcesPage.isResourcePresent(resource_for_privateDomain_privateGroup.getIdentifierValue(), resource_for_privateDomain_privateGroup.getIdentifierScheme())
                , "Public resource of Private Domain and Private groups is not available for resource admin");

        //Check if Public resource of Private domain can be seen by Resource viewer
        resourcesPage.logout();
        loginPage = homePage.goToLoginPage();
        loginPage.login(userResourceViewer.getUsername(), TestRunData.getInstance().getNewPassword());
        resourcesPage = homePage.getSidebar().navigateTo(Pages.SEARCH_RESOURCES);
        soft.assertTrue(resourcesPage.isResourcePresent(resource_for_privateDomain_publicGroup.getIdentifierValue(), resource_for_privateDomain_publicGroup.getIdentifierScheme())
                , "Public resource of Private Domain and Public groups is not available for resource viewer");
        soft.assertTrue(resourcesPage.isResourcePresent(resource_for_privateDomain_privateGroup.getIdentifierValue(), resource_for_privateDomain_privateGroup.getIdentifierScheme())
                , "Public resource of Private Domain and Private groups is not available for resource viewer");

        //Check if Public resource of Private domain can be seen by Group Admin
        resourcesPage.logout();
        loginPage = homePage.goToLoginPage();
        loginPage.login(userGroupAdmin.getUsername(), TestRunData.getInstance().getNewPassword());
        resourcesPage = homePage.getSidebar().navigateTo(Pages.SEARCH_RESOURCES);
        soft.assertTrue(resourcesPage.isResourcePresent(resource_for_privateDomain_publicGroup.getIdentifierValue(), resource_for_privateDomain_publicGroup.getIdentifierScheme())
                , "Public resource of Private Domain and Public groups is not available for group admin");
        soft.assertTrue(resourcesPage.isResourcePresent(resource_for_privateDomain_privateGroup.getIdentifierValue(), resource_for_privateDomain_privateGroup.getIdentifierScheme())
                , "Public resource of Private Domain and Private groups is not available for group admin");

        //Check if Public resource of Private domain can be seen by Group viewer
        resourcesPage.logout();
        loginPage = homePage.goToLoginPage();
        loginPage.login(userGroupViewer.getUsername(), TestRunData.getInstance().getNewPassword());
        resourcesPage = homePage.getSidebar().navigateTo(Pages.SEARCH_RESOURCES);
        soft.assertTrue(resourcesPage.isResourcePresent(resource_for_privateDomain_publicGroup.getIdentifierValue(), resource_for_privateDomain_publicGroup.getIdentifierScheme())
                , "Public resource of Private Domain and Public groups is not available for group viewer!");
        soft.assertTrue(resourcesPage.isResourcePresent(resource_for_privateDomain_privateGroup.getIdentifierValue(), resource_for_privateDomain_privateGroup.getIdentifierScheme())
                , "Public resource of Private Domain and Private groups is not available for group viewer!");

        //Check if Public resource of Private domain can be seen by Domain Admin
        resourcesPage.logout();
        loginPage = homePage.goToLoginPage();
        loginPage.login(userDomainAdmin.getUsername(), TestRunData.getInstance().getNewPassword());
        resourcesPage = homePage.getSidebar().navigateTo(Pages.SEARCH_RESOURCES);
        soft.assertTrue(resourcesPage.isResourcePresent(resource_for_privateDomain_publicGroup.getIdentifierValue(), resource_for_privateDomain_publicGroup.getIdentifierScheme())
                , "Public resource of Private Domain and Public groups is not available for domain admin!");
        soft.assertTrue(resourcesPage.isResourcePresent(resource_for_privateDomain_privateGroup.getIdentifierValue(), resource_for_privateDomain_privateGroup.getIdentifierScheme())
                , "Public resource of Private Domain and Private groups is not available for domain admin!");

        //Check if Public resource of Private domain can be seen by Domain viewer
        resourcesPage.logout();
        loginPage = homePage.goToLoginPage();
        loginPage.login(memberDomainViewer.getUsername(), TestRunData.getInstance().getNewPassword());
        resourcesPage = homePage.getSidebar().navigateTo(Pages.SEARCH_RESOURCES);
        soft.assertTrue(resourcesPage.isResourcePresent(resource_for_privateDomain_publicGroup.getIdentifierValue(), resource_for_privateDomain_publicGroup.getIdentifierScheme())
                , "Public resource of Private Domain and Public groups is not available for domain viewer!");
        soft.assertTrue(resourcesPage.isResourcePresent(resource_for_privateDomain_privateGroup.getIdentifierValue(), resource_for_privateDomain_privateGroup.getIdentifierScheme())
                , "Public resource of Private Domain and Private groups is not available for domain viewer!");

        //Check if Public resource of Private domain can be seen by Domain admin of other domain
        resourcesPage.logout();
        loginPage = homePage.goToLoginPage();
        loginPage.login(memberDomainAdminDomain2.getUsername(), TestRunData.getInstance().getNewPassword());
        resourcesPage = homePage.getSidebar().navigateTo(Pages.SEARCH_RESOURCES);
        soft.assertFalse(resourcesPage.isResourcePresent(resource_for_privateDomain_publicGroup.getIdentifierValue(), resource_for_privateDomain_publicGroup.getIdentifierScheme())
                , "Private resource of Private Domain and Public groups is available for domain admin of other domain!");
        soft.assertFalse(resourcesPage.isResourcePresent(resource_for_privateDomain_privateGroup.getIdentifierValue(), resource_for_privateDomain_privateGroup.getIdentifierScheme())
                , "Private resource of Private Domain and Private groups is  available for domain admin of other domain!");
        soft.assertAll();
    }
    @Test(description = "SRCRES-09 User is able to Open URL for resource with OASIS 1.0")
    public void userIsAbleToOpenURLForResourceWithOASIS1() throws Exception {
        ResourceModel resourceModelOasis1 = ResourceModel.generatePublicResource(ResourceTypes.OASIS1);
        resourceModelOasis1 = rest.resources().createResourceForGroup(domainModel, groupModel, resourceModelOasis1);
        rest.resources().addMembersToResource(domainModel, groupModel, resourceModelOasis1, memberAdmin);

        XMLUtils documentXML = resourcesPage.openURLResouceDocument(resourceModelOasis1.getIdentifierValue(), resourceModelOasis1.getIdentifierScheme());

        soft.assertEquals(documentXML.getNodeValue("ParticipantIdentifier"), resourceModelOasis1.getIdentifierValue(), "ParticipantIdentifier value is wrong");
        soft.assertAll();
    }

    @Test(description = "SRCRES-09 User is able to Open URL for resource with OASIS 2.0")
    public void userIsAbleToOpenURLForResourceWithOASIS2() throws Exception {
        ResourceModel resourceModelOasis2 = ResourceModel.generatePublicResource(ResourceTypes.OASIS2);
        resourceModelOasis2 = rest.resources().createResourceForGroup(domainModel, groupModel, resourceModelOasis2);
        rest.resources().addMembersToResource(domainModel, groupModel, resourceModelOasis2, memberAdmin);

        XMLUtils documentXML = resourcesPage.openURLResouceDocument(resourceModelOasis2.getIdentifierValue(), resourceModelOasis2.getIdentifierScheme());

        soft.assertEquals(documentXML.getNodeValue("ParticipantID"), resourceModelOasis2.getIdentifierValue(), "ParticipantIdentifier value is wrong");
        soft.assertAll();
    }

    @Test(description = "SRCRES-09 User is able to Open URL for resource with OASIS 3.0")
    public void userIsAbleToOpenURLForResourceWithOASIS3() throws Exception {
        ResourceModel resourceModelOasis3 = ResourceModel.generatePublicResourceUnregisteredToSML();
        resourceModelOasis3.setResourceTypeIdentifier(ResourceTypes.OASIS3.getName());

        resourceModelOasis3 = rest.resources().createResourceForGroup(domainModel, groupModel, resourceModelOasis3);
        rest.resources().addMembersToResource(domainModel, groupModel, resourceModelOasis3, memberAdmin);

        XMLUtils documentXML = resourcesPage.openURLResouceDocument(resourceModelOasis3.getIdentifierValue(), resourceModelOasis3.getIdentifierScheme());

        soft.assertEquals(documentXML.getNodeValue("PartyName"), resourceModelOasis3.getIdentifierValue(), "ParticipantIdentifier value is wrong");
        soft.assertAll();
    }

    @Test(description = "SRCRES-8 - Search by exact Resource schema, and/or Resource identifier produces results that match the users input")
    public void searchByExactResourceSchemaAndOrResourceIdentifierProduceResultsThatMatchTheUsersInput() throws Exception {
        ResourceModel resource = ResourceModel.generatePublicResource(ResourceTypes.OASIS1);
        resource = rest.resources().createResourceForGroup(domainModel, groupModel, resource);
        rest.resources().addMembersToResource(domainModel, groupModel, resource, memberAdmin);

        soft.assertTrue(resourcesPage.isResourcePresent(resource.getIdentifierValue(), resource.getIdentifierScheme()), "Searching by resource schema and resource identifier does not return results");
        soft.assertTrue(resourcesPage.isResourcePresent("", resource.getIdentifierScheme()), "Searching by resource schema does not return results");
        soft.assertTrue(resourcesPage.isResourcePresent(resource.getIdentifierValue(), ""), "Searching by resource identifier does not return results");
        soft.assertAll();
    }






}