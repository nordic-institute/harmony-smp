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
        String subject = "Request for reset of the Credential";
        JsonObject lastMessageArray = null;

        String regex = "https?://[^\\s\"<>]+(?=\\s|<|$)";
        Pattern pattern = Pattern.compile(regex);

        for (int i = 0; i < 6; i++) {
            lastMessageArray = getlastmessageOfUser(userName, subject);

            if (lastMessageArray != null
                    && !lastMessageArray.isEmpty()
                    && lastMessageArray.has("subject")
                    && !lastMessageArray.get("subject").isJsonNull()
                    && lastMessageArray.get("subject").getAsString().contains(subject)) {

                String text = (lastMessageArray.has("text") && !lastMessageArray.get("text").isJsonNull())
                        ? lastMessageArray.get("text").getAsString()
                        : "";

                Matcher matcher = pattern.matcher(text);
                if (matcher.find()) {
                    return matcher.group(0);
                }

                LOG.error("Reset URL not found in the email: " + text);
                throw new NullPointerException("Reset URL not found in the email: " + text);
            }

            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                throw new RuntimeException("Interrupted while waiting for reset email", e);
            }
        }

        String lastSubject = (lastMessageArray != null && lastMessageArray.has("subject") && !lastMessageArray.get("subject").isJsonNull())
                ? lastMessageArray.get("subject").getAsString()
                : "null";

        throw new NullPointerException("Could not find reset email. Last subject found is: " + lastSubject);
    }

}
