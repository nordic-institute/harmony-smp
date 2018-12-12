package utils.rest;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;
import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.StaxDriver;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import pages.service_groups.search.pojo.ServiceGroup;
import utils.PROPERTIES;
import utils.TestDataProvider;

import javax.ws.rs.core.Cookie;
import javax.ws.rs.core.MediaType;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class SMPRestClient {

	private static Client client = Client.create();
	private static WebResource resource = client.resource(PROPERTIES.UI_BASE_URL);

	public static ServiceGroup getServiceGroup(String url){
//		downloading XML and parsing
		XStream xstream = new XStream(new StaxDriver());
		xstream.ignoreUnknownElements();
		xstream.processAnnotations(ServiceGroup.class);
		ServiceGroup serviceGroup = null;
		try {
			String tmp = client.resource(url).get(String.class);
			serviceGroup = (ServiceGroup) xstream.fromXML(tmp);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return serviceGroup;
	}


	public static List<String> getDomainAndSubdomain(){
		List<String> domainList = new ArrayList<>();
		try {
			String responseRaw = resource.path(SMPPaths.REST_DOMAIN_LIST)
					.queryParam("page", "-1")
					.queryParam("pageSize", "-1")
					.accept(MediaType.APPLICATION_JSON_TYPE)
					.type(MediaType.APPLICATION_JSON_TYPE)
					.get(String.class);
			JSONArray restDomains = new JSONObject(responseRaw).getJSONArray("serviceEntities");

			for (int i = 0; i < restDomains.length(); i++) {
				JSONObject currentDomain = restDomains.getJSONObject(i);
				String currentDomainStr = currentDomain.getString("domainCode").trim();
				String currentSubdomainStr = "" + currentDomain.getString("smlSubdomain").trim().replaceAll("null", "");

				String tmp = String.format("%s (%s)", currentDomainStr, currentSubdomainStr);

				domainList.add(tmp);
			}
		} catch (Exception e) { }
		return domainList;
	}

	public static List<String> getDomainCodes(){

		List<String> domainList = new ArrayList<>();
		try {
			String responseRaw = resource.path(SMPPaths.REST_DOMAIN_LIST)
					.queryParam("page", "-1")
					.queryParam("pageSize", "-1")
					.accept(MediaType.APPLICATION_JSON_TYPE)
					.type(MediaType.APPLICATION_JSON_TYPE)
					.get(String.class);
			JSONArray restDomains = new JSONObject(responseRaw).getJSONArray("serviceEntities");

			for (int i = 0; i < restDomains.length(); i++) {
				JSONObject currentDomain = restDomains.getJSONObject(i);
				String currentDomainStr = currentDomain.getString("domainCode").trim();
				domainList.add(currentDomainStr);
			}
		} catch (Exception e) { }
		return domainList;
	}

	public static Cookie login(String role){
		String authTemplate = "{\"username\": \"%s\", \"password\": \"%s\"}";
		TestDataProvider tdp = new TestDataProvider();
		Map<String, String> user = tdp.getUserWithRole(role);
		String auth = String.format(authTemplate, user.get("username"), user.get("password"));

		Cookie session = resource.path(SMPPaths.LOGIN_PATH).accept(MediaType.APPLICATION_JSON).type(MediaType.APPLICATION_JSON_TYPE)
				.post(ClientResponse.class, auth).getCookies().get(0);
		return session;
	}

	public static List<String> getSysAdmins(){
		Cookie jssesionID = login("SYS_ADMIN");

		try {
			String responseRaw = resource.path(SMPPaths.USER_LIST)
					.queryParam("page", "-1")
					.queryParam("pageSize", "-1")
					.accept(MediaType.APPLICATION_JSON_TYPE)
					.type(MediaType.APPLICATION_JSON_TYPE)
					.cookie(jssesionID)
					.get(String.class);
			JSONArray users = new JSONObject(responseRaw).getJSONArray("serviceEntities");

			List<String> sysadmins = new ArrayList<>();

			for (int i = 0; i < users.length(); i++) {
				JSONObject usr = users.getJSONObject(i);
				if(usr.getString("role").equalsIgnoreCase("SYSTEM_ADMIN")){
					sysadmins.add(usr.getString("username"));
				}
			}
			return sysadmins;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	public static List<String> getKeyStoreEntries(){
		Cookie jssesionID = login("SYS_ADMIN");

		try {
			String responseRaw = resource.path(SMPPaths.KEYSTORE)
					.accept(MediaType.APPLICATION_JSON_TYPE)
					.type(MediaType.APPLICATION_JSON_TYPE)
					.cookie(jssesionID)
					.get(String.class);
			JSONArray entries = new JSONObject(responseRaw).getJSONArray("serviceEntities");

			List<String> entryList = new ArrayList<>();

			for (int i = 0; i < entries.length(); i++) {
				String id = entries.getJSONObject(i).getString("certificateId");
				String alias = entries.getJSONObject(i).getString("alias");
				entryList.add(String.format("%s (%s)", alias, id));
			}
			return entryList;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}


	public static boolean createDomain(String domainCode){
		Cookie jssesionID = login("SYS_ADMIN");
		String template = "[{\"domainCode\":\"%s\",\"smlSubdomain\":\"%s\",\"smlSmpId\":\"%s\",\"smlClientKeyAlias\":\"%s\",\"signatureKeyAlias\":\"%s\",\"status\":2,\"smlClientCertHeader\":\"%s\"}]";
		String domainPostStr = String.format(template, domainCode, domainCode, domainCode, domainCode, domainCode, domainCode);

		try {

			ClientResponse getResponse = resource.path(SMPPaths.REST_POST_DOMAIN)
					.accept(MediaType.APPLICATION_JSON_TYPE)
					.type(MediaType.APPLICATION_JSON_TYPE)
					.cookie(jssesionID)
					.put(ClientResponse.class, domainPostStr);

			return getResponse.getStatus() == 200;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}

	public static boolean createUser(String username, String role){
		Cookie jssesionID = login("SYS_ADMIN");
		String template = "[{\"active\":true,\"username\":\"%s\",\"emailAddress\":\"\",\"password\":\"QW!@qw12\",\"confirmation\":\"\",\"role\":\"%s\",\"status\":2,\"statusPassword\":2,\"certificate\":{\"subject\":\"\",\"validFrom\":null,\"validTo\":null,\"issuer\":\"\",\"serialNumber\":\"\",\"certificateId\":\"\",\"fingerprints\":\"\"}}]";
		String postStr = String.format(template, username, role);

		try {
			ClientResponse getResponse = resource.path(SMPPaths.USER_LIST)
					.accept(MediaType.APPLICATION_JSON_TYPE)
					.type(MediaType.APPLICATION_JSON_TYPE)
					.cookie(jssesionID)
					.put(ClientResponse.class, postStr);

			return getResponse.getStatus() == 200;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}

	public static boolean createServiceGroup(String pi, String ps, List<String> usernames, List<String> domainCodes){
		String template = "[{\"id\":null,\"participantIdentifier\":\"%s\",\"participantScheme\":\"%s\",\"serviceMetadata\":[],\"users\":%s,\"serviceGroupDomains\":%s,\"extension\":\"\",\"status\":2}]";
		try {

			JSONArray users = new JSONArray();
			for (String username : usernames) {
				users.put(transformUserForSGPost(getUserForName(username)));
			}

			JSONArray domains = new JSONArray();
			for (String codes : domainCodes) {
				domains.put(transformDomainForSGPost(getDomainForName(codes)));
			}

			String postStr = String.format(template, pi, ps, users.toString(), domains.toString());

			Cookie jssesionID = login("SMP_ADMIN");
			ClientResponse getResponse = resource.path(SMPPaths.SERVICE_GROUP)
					.accept(MediaType.APPLICATION_JSON_TYPE)
					.type(MediaType.APPLICATION_JSON_TYPE)
					.cookie(jssesionID)
					.put(ClientResponse.class, postStr);

			return getResponse.getStatus() == 200;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}

	public static String getMetadataString(String url){
		try {
			System.out.println("url = " + url);
//			------------------------------
			return client.resource(url).accept(MediaType.APPLICATION_XML).get(String.class);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return "";
	}

	private static JSONObject getUserForName(String username){
		Cookie jssesionID = login("SYS_ADMIN");

		try {
			String responseRaw = resource.path(SMPPaths.USER_LIST)
					.queryParam("page", "-1")
					.queryParam("pageSize", "-1")
					.accept(MediaType.APPLICATION_JSON_TYPE)
					.type(MediaType.APPLICATION_JSON_TYPE)
					.cookie(jssesionID)
					.get(String.class);
			JSONArray users = new JSONObject(responseRaw).getJSONArray("serviceEntities");

			for (int i = 0; i < users.length(); i++) {
				JSONObject usr = users.getJSONObject(i);
				if(username.equalsIgnoreCase(usr.getString("username"))){
					return usr;
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	private static JSONObject getDomainForName(String domainName){
		Cookie jssesionID = login("SYS_ADMIN");
		try {
			String responseRaw = resource.path(SMPPaths.REST_DOMAIN_LIST)
					.queryParam("page", "-1")
					.queryParam("pageSize", "-1")
					.accept(MediaType.APPLICATION_JSON_TYPE)
					.type(MediaType.APPLICATION_JSON_TYPE)
					.cookie(jssesionID)
					.get(String.class);
			JSONArray domains = new JSONObject(responseRaw).getJSONArray("serviceEntities");
			for (int i = 0; i < domains.length(); i++) {
				JSONObject dom = domains.getJSONObject(i);
				if(domainName.equalsIgnoreCase(dom.getString("domainCode"))){
					return dom;
				}
			}
		} catch (Exception e) {
			e.printStackTrace();		}
		return null;
	}

	private static JSONObject getSGforPI(String pi){
		Cookie jssesionID = login("SYS_ADMIN");
		try {
			String responseRaw = resource.path(SMPPaths.SERVICE_GROUP)
					.queryParam("page", "-1")
					.queryParam("pageSize", "-1")
					.accept(MediaType.APPLICATION_JSON_TYPE)
					.type(MediaType.APPLICATION_JSON_TYPE)
					.cookie(jssesionID)
					.get(String.class);
			JSONArray sgs = new JSONObject(responseRaw).getJSONArray("serviceEntities");
			for (int i = 0; i < sgs.length(); i++) {
				JSONObject sg = sgs.getJSONObject(i);
				if(pi.equalsIgnoreCase(sg.getString("participantIdentifier"))){
					return sg;
				}
			}
		} catch (Exception e) {
			e.printStackTrace();		}
		return null;
	}

	public static boolean deleteDomain(String domainCode){
		Cookie jssesionID = login("SYS_ADMIN");
		try {
			String domainPostStr = "[" + getDomainForName(domainCode).put("status", 3).toString() + "]";
			ClientResponse getResponse = resource.path(SMPPaths.REST_POST_DOMAIN)
					.accept(MediaType.APPLICATION_JSON_TYPE)
					.type(MediaType.APPLICATION_JSON_TYPE)
					.cookie(jssesionID)
					.put(ClientResponse.class, domainPostStr);

			return getResponse.getStatus() == 200;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}

	public static boolean deleteUser(String username){
		Cookie jssesionID = login("SYS_ADMIN");
		try {
			String putStr = "[" + getUserForName(username).put("status", 3).toString() + "]";
			ClientResponse getResponse = resource.path(SMPPaths.USER_LIST)
					.accept(MediaType.APPLICATION_JSON_TYPE)
					.type(MediaType.APPLICATION_JSON_TYPE)
					.cookie(jssesionID)
					.put(ClientResponse.class, putStr);

			return getResponse.getStatus() == 200;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}

	public static boolean deleteSG(String pi){
		Cookie jssesionID = login("SG_ADMIN");
		try {
			String putStr = "[" + getSGforPI(pi).put("status", 3).toString() + "]";
			ClientResponse getResponse = resource.path(SMPPaths.SERVICE_GROUP)
					.accept(MediaType.APPLICATION_JSON_TYPE)
					.type(MediaType.APPLICATION_JSON_TYPE)
					.cookie(jssesionID)
					.put(ClientResponse.class, putStr);

			return getResponse.getStatus() == 200;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}


	private static JSONObject transformUserForSGPost(JSONObject user){
		try {
			String template = "{\"index\":%s,\"username\":\"%s\",\"role\":\"%s\",\"id\":%s,\"status\":0,\"password\":null,\"emailAddress\":null,\"authorities\":null,\"passwordChanged\":null,\"active\":true,\"certificate\":null,\"statusPassword\":0}";
			String index = user.getString("index");
			String username = user.getString("username");
			String role = user.getString("role");
			String id = user.getString("id");
			return new JSONObject(String.format(template, index, username, role, id));
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return null;
	}

	private static JSONObject transformDomainForSGPost(JSONObject domain){
		try {
			String template = "{\"domainId\":%s,\"domainCode\":\"%s\",\"smlSubdomain\":\"%s\",\"id\":null,\"smlRegistered\":false,\"serviceMetadataCount\":0,\"status\":2}";
			String domainId = domain.getString("id");
			String domainCode = domain.getString("domainCode");
			String smlSubdomain = domain.getString("smlSubdomain");
			return new JSONObject(String.format(template, domainId, domainCode, smlSubdomain));
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return null;
	}
}
