package rest;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sun.jersey.api.client.ClientResponse;
import org.json.JSONObject;
import rest.models.DomainModel;
import rest.models.GroupModel;
import rest.models.MemberModel;
import rest.models.ResourceModel;
import utils.TestRunData;

/**
 * Rest client for group actions
 */
public class ResourceClient extends BaseRestClient {
    public ResourceModel createResourceForGroup(DomainModel domainModel, GroupModel groupModel, ResourceModel resourceModelToBeCreated) {
        JSONObject resourceJson = new JSONObject(resourceModelToBeCreated);
        String createResourcePath = RestServicePaths.getCreateResourcePath(TestRunData.getInstance().getUserId(), domainModel.getDomainId(), groupModel.getGroupId());
        ClientResponse response = jsonPUT(resource.path(createResourcePath), resourceJson);
        if (response.getStatus() != 200) {
            try {
                throw new SMPRestException("Could not create resource!", response);
            } catch (SMPRestException e) {
                throw new RuntimeException(e);
            }
        }
        log.debug("Resource have been added!");
        return response.getEntity(ResourceModel.class);
    }

    public MemberModel addMembersToResource(DomainModel domainModel, GroupModel groupModel, ResourceModel resourceModel, MemberModel groupMember) throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();
        String membersJson = mapper.writeValueAsString(groupMember);

        String addGroupMemberPath = RestServicePaths.getResourceAddMemberPath(TestRunData.getInstance().getUserId(), domainModel.getDomainId(), groupModel.getGroupId(), resourceModel.getResourceId());

        ClientResponse response = jsonPUT(resource.path(addGroupMemberPath), membersJson);
        if (response.getStatus() != 200) {
            try {
                throw new SMPRestException("Could not add members to resource", response);
            } catch (SMPRestException e) {
                throw new RuntimeException(e);
            }
        }
        log.debug("Member: " + groupMember.getUsername() + " has been added!");
        return response.getEntity(MemberModel.class);
    }
}
