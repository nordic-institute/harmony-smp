package rest;

public class DomiSMPRestClient extends BaseRestClient {
    public DomiSMPRestClient() {
        super();
    }

    // -------------------------------------------- get clients -----------------------------------------------------------
    public UserClient users() {
        return new UserClient(username, password);
    }
}

