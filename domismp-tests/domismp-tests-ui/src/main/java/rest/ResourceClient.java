package rest;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.sun.jersey.api.client.ClientResponse;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import rest.models.DomainModel;
import rest.models.GroupModel;
import rest.models.MemberModel;
import rest.models.ResourceModel;
import utils.TestRunData;

/**
 * Rest client for group actions
 */
public class ResourceClient extends BaseRestClient {
    private final static Logger LOG = LoggerFactory.getLogger(ResourceClient.class);

    public ResourceModel createResourceForGroup(DomainModel domainModel, GroupModel groupModel, ResourceModel resourceModelToBeCreated) {
        JSONObject resourceJson = new JSONObject(resourceModelToBeCreated);
        String userId = TestRunData.getInstance().getUserId();
        if (userId.isEmpty()) {
            startSession();
        }
        String createResourcePath = RestServicePaths.getCreateResourcePath(userId, domainModel.getDomainId(), groupModel.getGroupId());
        ClientResponse response = jsonPUT(resource.path(createResourcePath), resourceJson);
        if (response.getStatus() != 200) {
            try {
                throw new SMPRestException("Could not create resource!", response);
            } catch (SMPRestException e) {
                throw new RuntimeException(e);
            }
        }
        LOG.debug("Resource have been added!");
        return response.getEntity(ResourceModel.class);
    }

    public MemberModel addMembersToResource(DomainModel domainModel, GroupModel groupModel, ResourceModel resourceModel, MemberModel groupMember) throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();
        String membersJson = mapper.writeValueAsString(groupMember);
        String userId = TestRunData.getInstance().getUserId();

        if (userId.isEmpty()) {
            startSession();
        }
        String addGroupMemberPath = RestServicePaths.getResourceAddMemberPath(userId, domainModel.getDomainId(), groupModel.getGroupId(), resourceModel.getResourceId());

        ClientResponse response = jsonPUT(resource.path(addGroupMemberPath), membersJson);
        if (response.getStatus() != 200) {
            try {
                throw new SMPRestException("Could not add members to resource", response);
            } catch (SMPRestException e) {
                throw new RuntimeException(e);
            }
        }
        LOG.debug("Member: " + groupMember.getUsername() + " has been added!");
        return response.getEntity(MemberModel.class);
    }

    public ResourceModel updateResource(DomainModel domainModel, GroupModel groupModel, ResourceModel resourceToBeUpdated) {
        String updateResorcePath = RestServicePaths.getResourceUpdatePath(TestRunData.getInstance().getUserId(), domainModel.getDomainId(), groupModel.getGroupId(), resourceToBeUpdated.getResourceId());
        ClientResponse response = requestPOST(resource.path(updateResorcePath), new Gson().toJson(resourceToBeUpdated));
        if (response.getStatus() != 200) {
            try {
                throw new SMPRestException("Could not update resource!", response.getStatus(), response.getEntity(String.class));
            } catch (SMPRestException e) {
                throw new RuntimeException(e);
            }
        }
        LOG.debug("Resource " + resourceToBeUpdated.getIdentifierValue() + " has been updated!");
        return response.getEntity(ResourceModel.class);
    }

}
