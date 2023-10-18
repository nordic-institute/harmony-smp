package rest.models;


import utils.Generator;

public class GroupModel {
    private String groupName;
    private String visibility;
    private String groupDescription;

    public static GroupModel generatePublicDomain() {
        GroupModel groupModel = new GroupModel();
        groupModel.groupName = ("AUT_groupName_" + Generator.randomAlphaNumeric(4)).toLowerCase();
        groupModel.groupDescription = Generator.randomAlphaNumeric(10).toLowerCase();
        groupModel.visibility = "PUBLIC";
        return groupModel;
    }

    public static GroupModel generatePrivateDomain() {
        GroupModel groupModel = new GroupModel();
        groupModel.groupName = ("AUT_groupName_" + Generator.randomAlphaNumeric(4)).toLowerCase();
        groupModel.groupDescription = Generator.randomAlphaNumeric(10).toLowerCase();
        groupModel.visibility = "PRIVATE";
        return groupModel;
    }

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    public String getVisibility() {
        return visibility;
    }

    public void setVisibility(String visibility) {
        this.visibility = visibility;
    }

    public String getGroupDescription() {
        return groupDescription;
    }

    public void setGroupDescription(String groupDescription) {
        this.groupDescription = groupDescription;
    }
}
