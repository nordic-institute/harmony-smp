package rest;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.WebResource;
import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import utils.TestRunData;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.testng.AssertJUnit.fail;

public class InbucketRestClient {
    private final static Logger LOG = LoggerFactory.getLogger(InbucketRestClient.class);

    protected TestRunData data = TestRunData.getInstance();
    protected Client client = Client.create();
    public WebResource resource = client.resource(TestRunData.getInstance().getPropertyValue(TestRunData.TestEnvironmentProperty.MAIL_URL));

    private JSONArray getAllMessagesOfUser(String userName) {
        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        WebResource.Builder builder = resource.path("api/v1/mailbox/" + userName).getRequestBuilder();
        String rawStringResponse = builder.get(String.class);
        JSONArray jsonArray = new JSONArray(rawStringResponse);

        LOG.debug("All messages of users have been retrieved!");
        return jsonArray;
    }

    public JsonObject getlastmessageOfUser(String userName, String title) {
        JSONArray getAllMessagesOfUser;
        getAllMessagesOfUser = getAllMessagesOfUser(userName);
        JSONObject lastmessage = (JSONObject) getAllMessagesOfUser.get(getAllMessagesOfUser.length() - 1);
        String lastmessageId = lastmessage.get("id").toString();
        WebResource.Builder builder = resource.path("serve/mailbox/" + userName + "/" + lastmessageId).getRequestBuilder();
        String rawStringResponse = builder.get(String.class);
        JsonObject jsonArray = JsonParser.parseString(rawStringResponse).getAsJsonObject();
        LOG.debug("Last email of user has been retrieved!");
        return jsonArray;
    }


    public String getResetPasswordTokenFromLastEmailOfUser(String userName) {
        JsonObject lastMessageArray = null;
        int i = 0;
        boolean isResetEmail = false;
        while (i <= 5 & !isResetEmail) {
            lastMessageArray = getlastmessageOfUser(userName, "Request for reset of the Credential");
            if (lastMessageArray.isEmpty()) {
                LOG.error("Last email of user is empty!");
                fail();
            }
            if (lastMessageArray.get("subject").toString().contains("Request for reset of the Credential")) {
                isResetEmail = true;
                String text = lastMessageArray.get("text").toString();
                String regex = "http://[^\\s\"<>]+(?=\\s|<|$)";
                Pattern pattern = Pattern.compile(regex);
                Matcher matcher = pattern.matcher(text);
                while (matcher.find()) {
                    return matcher.group(0);
                }
                LOG.error("Reset URL found in the email: " + text);
                throw new NullPointerException("Reset URL found in the email: " + text);
            }
            i++;

        }
        throw new NullPointerException("Could not find reset email. The current subject found is: " + lastMessageArray.get("subject").toString());





    }

}
