package utils;

import org.json.JSONException;
import org.json.JSONObject;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Iterator;

public class TestDataProvider {

    private JSONObject testData = null;


    public TestDataProvider(){

        String content = null;
        try {
            content = new String(Files.readAllBytes(Paths.get(PROPERTIES.TESTDATAFILE)));
         	testData = new JSONObject(content);
        } catch (Exception e) {
           throw new RuntimeException("Error reading test data file: " + PROPERTIES.TESTDATAFILE);
        }
    }

    public HashMap<String, String> getUserWithRole(String key){
        HashMap<String, String> toReturn = new HashMap<>();

		try {
			JSONObject user = null;
			user = testData.getJSONObject("loginUsers").getJSONObject(key);

			Iterator<String> keysItr = user.keys();
			while(keysItr.hasNext()) {
				String usrKey = keysItr.next();
				toReturn.put(usrKey, user.getString(usrKey));
			}
		} catch (JSONException e) {	}

		return toReturn;
    }

	public String getDefaultTestPass(){
		try {
			return testData.getString("passwordForTestUsers");
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return "";
	}







}
