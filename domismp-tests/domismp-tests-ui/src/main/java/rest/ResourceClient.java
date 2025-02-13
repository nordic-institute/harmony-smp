package rest;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.sun.jersey.api.client.ClientResponse;
import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import rest.models.DomainModel;
import rest.models.GroupModel;
import rest.models.MemberModel;
import rest.models.ResourceModel;
import utils.TestRunData;

import java.util.HashMap;
import java.util.List;
import java.util.Optional;

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

    public MemberModel updateMemberOfResource(DomainModel domainModel, GroupModel groupModel, ResourceModel resourceModel, MemberModel resourceMember) throws JsonProcessingException {

        Optional<MemberModel> memberToUpdate = getMembersOfResource(domainModel, groupModel, resourceModel)
                .stream()
                .filter(member -> member.getUsername().equals(resourceMember.getUsername()))
                .findFirst();
        if (memberToUpdate.isPresent()) {
            resourceMember.setMemberId(memberToUpdate.get().getMemberId());
            resourceMember.setMemberOf(memberToUpdate.get().getMemberOf());
        } else {
            LOG.error("Member not found");
            throw new RuntimeException();
        }

        ObjectMapper mapper = new ObjectMapper();
        String membersJson = mapper.writeValueAsString(resourceMember);
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
        LOG.debug("Member: " + resourceMember.getUsername() + " has been added!");
        return response.getEntity(MemberModel.class);
    }

    public List<MemberModel> getMembersOfResource(DomainModel domainModel, GroupModel groupModel, ResourceModel resourceModel) throws JsonProcessingException {


        HashMap<String, String> params = new HashMap<>();
        String addGroupMemberPath = RestServicePaths.getResourceMembers(TestRunData.getInstance().getUserId(), domainModel.getDomainId(), groupModel.getGroupId(), resourceModel.getResourceId());

        ClientResponse response = requestGET(resource.path(addGroupMemberPath), params);
        if (response.getStatus() != 200) {
            try {
                throw new SMPRestException("Could not add members to resource", response);
            } catch (SMPRestException e) {
                throw new RuntimeException(e);
            }
        }


        JSONArray responseMembers = new JSONObject(response.getEntity(String.class)).getJSONArray("serviceEntities");

        return new Gson().fromJson(responseMembers.toString(), new TypeToken<List<MemberModel>>() {
        }.getType());


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

    public String getDocumentID(ResourceModel resourceToBeUpdated) {
        String updateResorcePath = RestServicePaths.getDocumentPath(TestRunData.getInstance().getUserId(), resourceToBeUpdated.getResourceId());
        ClientResponse response = requestGet(resource.path(updateResorcePath));
        if (response.getStatus() != 200) {
            try {
                throw new SMPRestException("Could not get document id!", response.getStatus(), response.getEntity(String.class));
            } catch (SMPRestException e) {
                throw new RuntimeException(e);
            }
        }
        return new JSONObject(response.getEntity(String.class)).get("documentId").toString();
    }

    public String reviewRequest(ResourceModel resourceToBeUpdated, String documentId, int version) {
        JSONObject resourceJson = new JSONObject();
        resourceJson.put("documentId", documentId);
        resourceJson.put("payloadVersion", version);


        String updateResorcePath = RestServicePaths.getReviewRequestPath(TestRunData.getInstance().getUserId(), resourceToBeUpdated.getResourceId());
        ClientResponse response = requestPOST(resource.path(updateResorcePath), resourceJson.toString());
        if (response.getStatus() != 200) {
            try {
                throw new SMPRestException("Could not send task to review!", response.getStatus(), response.getEntity(String.class));
            } catch (SMPRestException e) {
                throw new RuntimeException(e);
            }
        }
        LOG.debug("Document " + documentId + " has been sent to review!");
        return new JSONObject(response.getEntity(String.class)).get("documentId").toString();
    }

    public String rejectReview(ResourceModel resourceToBeUpdated, String documentId, int version) {
        JSONObject resourceJson = new JSONObject();
        resourceJson.put("documentId", documentId);
        resourceJson.put("payloadVersion", version);


        String updateResorcePath = RestServicePaths.getReviewRejectPath(TestRunData.getInstance().getUserId(), resourceToBeUpdated.getResourceId());
        ClientResponse response = requestPOST(resource.path(updateResorcePath), resourceJson.toString());
        if (response.getStatus() != 200) {
            try {
                throw new SMPRestException("Could not reject task!", response.getStatus(), response.getEntity(String.class));
            } catch (SMPRestException e) {
                throw new RuntimeException(e);
            }
        }
        LOG.debug("Document " + documentId + " has been rejected!");
        return new JSONObject(response.getEntity(String.class)).get("documentId").toString();
    }

    public String approveReview(ResourceModel resourceToBeUpdated, String documentId, int version) {
        JSONObject resourceJson = new JSONObject();
        resourceJson.put("documentId", documentId);
        resourceJson.put("payloadVersion", version);


        String updateResorcePath = RestServicePaths.getReviewApprovePath(TestRunData.getInstance().getUserId(), resourceToBeUpdated.getResourceId());
        ClientResponse response = requestPOST(resource.path(updateResorcePath), resourceJson.toString());
        if (response.getStatus() != 200) {
            try {
                throw new SMPRestException("Could not approve task!", response.getStatus(), response.getEntity(String.class));
            } catch (SMPRestException e) {
                throw new RuntimeException(e);
            }
        }
        LOG.debug("Document " + documentId + " has been approved!");
        return new JSONObject(response.getEntity(String.class)).get("documentId").toString();
    }

}
