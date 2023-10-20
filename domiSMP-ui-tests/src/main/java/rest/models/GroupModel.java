package rest.models;

import utils.Generator;


public class GroupModel {
    private String groupName;
    private String visibility;
    private String groupDescription;
    private Object actionMessage;
    private String groupId;
    private Long index;
    private Long status;

    public static GroupModel generatePublicGroup() {
        GroupModel groupModel = new GroupModel();
        groupModel.groupName = ("AUT_groupName_" + Generator.randomAlphaNumeric(4)).toLowerCase();
        groupModel.groupDescription = Generator.randomAlphaNumeric(10).toLowerCase();
        groupModel.visibility = "PUBLIC";
        return groupModel;
    }

    public static GroupModel generatePrivateGroup() {
        GroupModel groupModel = new GroupModel();
        groupModel.groupName = ("AUT_groupName_" + Generator.randomAlphaNumeric(4)).toLowerCase();
        groupModel.groupDescription = Generator.randomAlphaNumeric(10).toLowerCase();
        groupModel.visibility = "PRIVATE";
        return groupModel;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    public void setVisibility(String visibility) {
        this.visibility = visibility;
    }

    public void setGroupDescription(String groupDescription) {
        this.groupDescription = groupDescription;
    }

    public void setActionMessage(Object actionMessage) {
        this.actionMessage = actionMessage;
    }

    public String getGroupId() {
        return groupId;
    }

    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }

    public void setIndex(Long index) {
        this.index = index;
    }

    public void setStatus(Long status) {
        this.status = status;
    }

    public String getGroupName() {
        return groupName;
    }
    public String getVisibility() {
        return visibility;
    }
    public String getGroupDescription() {
        return groupDescription;
    }


}
