package rest;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sun.jersey.api.client.ClientResponse;
import ddsl.enums.ResourceTypes;
import org.json.JSONObject;
import rest.models.DomainModel;
import rest.models.MemberModel;
import utils.TestRunData;

import java.util.ArrayList;
import java.util.List;

public class DomainClient extends BaseRestClient {

    /**
     * Rest client for domain actions
     */
    public DomainClient() {
        super();
    }

    public JSONObject createDomain(DomainModel domainModel) {

        JSONObject domainJson = new JSONObject(domainModel);

        if (!isLoggedIn()) {
            try {
                createSession();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }

        String createDomainPath = RestServicePaths.getCreateDomainPath(TestRunData.getInstance().getUserId());

        ClientResponse response = jsonPUT(resource.path(createDomainPath), domainJson);
        JSONObject responseBody = new JSONObject(response.getEntity(String.class));
        if (response.getStatus() != 200) {
            try {
                throw new SMPRestException("Could not create domain", response);
            } catch (SMPRestException e) {
                throw new RuntimeException(e);
            }
        }
        log.debug("Domain: " + domainModel.getDomainCode() + "  has been created successfully!");
        return responseBody;

    }


    public MemberModel addMembersToDomain(String domainId, MemberModel domainMember) throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();
        String membersJson = mapper.writeValueAsString(domainMember);
        if (!isLoggedIn()) {
            try {
                createSession();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
        String addMemberPath = RestServicePaths.getDomainAddMemberPath(TestRunData.getInstance().getUserId(), domainId);

        ClientResponse response = jsonPUT(resource.path(addMemberPath), membersJson);
        if (response.getStatus() != 200) {
            try {
                throw new SMPRestException("Could not create domain", response);
            } catch (SMPRestException e) {
                throw new RuntimeException(e);
            }
        }
        log.debug("Member: " + domainMember.getUsername() + " has been added!");
        return response.getEntity(MemberModel.class);
    }

    public DomainModel addResourcesToDomain(String domainId, List<ResourceTypes> resourceTypesList) throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();
        List<String> resourceListToBeAdded = new ArrayList<>();
        for (ResourceTypes resourceType : resourceTypesList) {
            resourceListToBeAdded.add(resourceType.getName());
        }

        String resourceTypes = mapper.writeValueAsString(resourceListToBeAdded);
        if (!isLoggedIn()) {
            try {
                createSession();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
        String addMemberPath = RestServicePaths.getAddResourcePath(TestRunData.getInstance().getUserId(), resourceTypes);
        ClientResponse response = requestPOST(resource.path(addMemberPath), resourceTypes);
        if (response.getStatus() != 200) {
            try {
                throw new SMPRestException("Could not add resource!", response);
            } catch (SMPRestException e) {
                throw new RuntimeException(e);
            }
        }
        log.debug("Resources have been added!");
        return response.getEntity(DomainModel.class);
    }
}
