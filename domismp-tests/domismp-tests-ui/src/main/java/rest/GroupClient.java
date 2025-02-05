package rest;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sun.jersey.api.client.ClientResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import rest.models.DomainModel;
import rest.models.GroupModel;
import rest.models.MemberModel;
import utils.TestRunData;

/**
 * Rest client for group actions
 */
public class GroupClient extends BaseRestClient {
    private final static Logger LOG = LoggerFactory.getLogger(GroupClient.class);

    public GroupClient() {
        super();
    }

    public MemberModel addMembersToGroup(DomainModel domainModel, GroupModel groupModel, MemberModel groupMember) throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();
        String membersJson = mapper.writeValueAsString(groupMember);

        String addGroupMemberPath = RestServicePaths.getGroupAddMemberPath(TestRunData.getInstance().getUserId(), domainModel.getDomainId(), groupModel.getGroupId());

        ClientResponse response = jsonPUT(resource.path(addGroupMemberPath), membersJson);
        if (response.getStatus() != 200) {
            try {
                throw new SMPRestException("Could not add members to group", response);
            } catch (SMPRestException e) {
                throw new RuntimeException(e);
            }
        }
        LOG.debug("Member: " + groupMember.getUsername() + " has been added!");
        return response.getEntity(MemberModel.class);
    }
}
