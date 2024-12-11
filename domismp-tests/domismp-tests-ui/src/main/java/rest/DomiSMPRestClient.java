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

    public ResourceClient resources() {
        return new ResourceClient();
    }

    public KeystoreClient keystoreClient() {
        return new KeystoreClient();
    }

    public PropertiesClient propertiesClient() {
        return new PropertiesClient();
    }



}

