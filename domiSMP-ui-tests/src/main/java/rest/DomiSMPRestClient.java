package rest;

public class DomiSMPRestClient extends BaseRestClient {
    public DomiSMPRestClient() {
        super();
    }
    public UserClient users() {
        return new UserClient(username, password);
    }
    public DomainClient domains() {
        return new DomainClient();
    }

    public GroupClient groups() {
        return new GroupClient();
    }

}

