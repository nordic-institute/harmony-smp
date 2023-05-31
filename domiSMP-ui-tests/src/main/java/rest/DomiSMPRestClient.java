package rest;

public class DomiSMPRestClient extends BaseRestClient {
    public DomiSMPRestClient() {
        super();
    }

    public UsersClient users() {
        return new UsersClient(username, password);
    }
}

