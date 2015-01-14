package geogebra.web.move.ggtapi.models;

import geogebra.common.move.ggtapi.models.ClientInfo;
import geogebra.common.move.ggtapi.models.GeoGebraTubeUser;
import geogebra.common.move.ggtapi.models.Material;
import geogebra.common.move.ggtapi.models.MaterialRequest;
import geogebra.html5.main.AppW;
import geogebra.html5.main.GeoGebraTubeAPIWSimple;
import geogebra.html5.util.ggtapi.JSONparserGGT;

import java.util.List;
import java.util.TreeMap;

import com.google.gwt.http.client.Request;
import com.google.gwt.http.client.RequestBuilder;
import com.google.gwt.http.client.RequestCallback;
import com.google.gwt.http.client.RequestException;
import com.google.gwt.http.client.Response;
import com.google.gwt.json.client.JSONArray;
import com.google.gwt.json.client.JSONException;
import com.google.gwt.json.client.JSONNumber;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONParser;
import com.google.gwt.json.client.JSONString;
import com.google.gwt.json.client.JSONValue;

/**
 * API Interface for GeoGebraTube requests and responses
 * 
 * @author Matthias Meisinger
 * 
 */
public class GeoGebraTubeAPIW extends GeoGebraTubeAPIWSimple {
	private RequestBuilder requestBuilder;
	private ClientInfo client;

	/**
	 * @param url
	 *            String
	 * @param client
	 *            {@link ClientInfo}
	 */
	public GeoGebraTubeAPIW(String url, ClientInfo client) {
		this.requestBuilder = new RequestBuilder(RequestBuilder.POST, url);
		this.client = client;
	}

	/**
	 * Search for materials containing the String query
	 * 
	 * @param query
	 *            search String
	 * @param callback
	 *            {@link MaterialCallback}
	 */
	public void search(String query, MaterialCallback callback) {
		performRequest(new MaterialRequest(query, client).toJSONString(),
		        callback);
	}

	/**
	 * Returns materials in the given amount and order
	 * 
	 * @param callback
	 *            {@link MaterialCallback}
	 */
	public void getFeaturedMaterials(MaterialCallback callback) {
		performRequest(MaterialRequest.forFeatured(client).toJSONString(),
		        callback);
	}

	// /**
	// * Returns a String-Array of popular tags fetched from the GGT API
	// *
	// */
	// public String[] getPopularTags()
	// {
	// // TODO fetch popular tags from the API
	// return new String[] { "algebra", "dment", "pythagorean", "circle",
	// "triangle", "functions", "jerzy", "geometry", "trigonometry", "3d" };
	// }

	/**
	 * Return a specific Material by its ID
	 * 
	 * @param id
	 *            int
	 * @param callback
	 *            {@link MaterialCallback}
	 */
	public void getItem(String id, MaterialCallback callback) {
		performRequest(MaterialRequest.forId(id, client).toJSONString(),
		        callback);
	}

	// /**
	// * Return a list of all Materials from the specified author
	// * ! Should be the same search as for materials!
	// * @param author
	// */
	// public void getAuthorsMaterials(String author, RequestCallback callback)
	// {
	// throw new UnsupportedOperationException();
	// }

	/**
	 * @param requestString
	 *            String
	 * @param cb
	 *            {@link MaterialCallback}
	 */
	protected void performRequest(String requestString,
	        final MaterialCallback cb) {
		try {
			RequestCallback callback = new RequestCallback() {

				@Override
				public void onResponseReceived(Request request,
				        Response response) {
					cb.onLoaded(JSONparserGGT.parseResponse(response.getText()));
				}

				@Override
				public void onError(Request request, Throwable exception) {
					cb.onError(exception);

				}
			};
			this.requestBuilder.sendRequest(requestString, callback);
		} catch (RequestException e) {
			// TODO Handle the error!
			e.printStackTrace();
		}
	}

	@Override
	protected geogebra.common.util.HttpRequest createHttpRequest() {
		return new geogebra.html5.util.HttpRequestW();
	}

	/**
	 * Copies the user data from the API response to this user.
	 * 
	 * @return true if the data could be parsed successfully, false otherwise
	 */
	@Override
	public boolean parseUserDataFromResponse(GeoGebraTubeUser user,
	        String result) {
		try {
			JSONValue tokener = JSONParser.parseStrict(result);

			JSONObject response = tokener.isObject();
			JSONObject userinfo = (JSONObject) response.get("responses");

			userinfo = ((JSONArray) userinfo.get("response")).get(0).isObject();
			userinfo = (JSONObject) userinfo.get("userinfo");

			if (userinfo.get("user_id") instanceof JSONNumber) {
				user.setUserId((int) (((JSONNumber) userinfo.get("user_id"))
				        .doubleValue()));
			} else {
				user.setUserId(Integer.valueOf(((JSONString) userinfo
				        .get("user_id")).stringValue()));
			}
			user.setUserName(((JSONString) userinfo.get("username"))
			        .stringValue());
			user.setRealName(((JSONString) userinfo.get("realname"))
			        .stringValue());
			user.setIdentifier(((JSONString) userinfo.get("identifier"))
			        .stringValue());
			if (userinfo.get("image") instanceof JSONString) {
				user.setImageURL(((JSONString) userinfo.get("image"))
				        .stringValue());
			}
			if (userinfo.get("lang_ui") instanceof JSONString) {
				user.setLanguage(((JSONString) userinfo.get("lang_ui"))
				        .stringValue());
			}

			if (userinfo.get("token") instanceof JSONString) {
				user.setToken(((JSONString) userinfo.get("token"))
				        .stringValue());
			}

			// Further fields are not parsed yet, because they are not needed
			// This is the complete response with all available fields:
			/*
			 * <responses> <response> <userinfo> <user_id>4711</user_id>
			 * <username>johndoe</username>
			 * <ggt_profile_url>http://tube.geogebra.org/user/profile/id/4711
			 * </ggt_profile_url> <group>user</group>
			 * <date_created>2012-09-18</date_created> <lang_ui>en</lang_ui>
			 * <lang_content>en,en_US,it</lang_content>
			 * <timezone>America/New_York</timezone> <materials>31</materials>
			 * <favorites>4</favorites> <collections>2</collections>
			 * <identifier>forum:0815</identifier> <realname>John Doe</realname>
			 * <occupation>Maths teacher</occupation> <location>New
			 * York</location> <website>www.thisisme.com</website>
			 * <profilemessage>Any text</profilemessage> </userinfo> </response>
			 * </responses>
			 */

			// user.setGGTProfileURL(userinfo.getString("ggt_profile_url"));
			// user.setGroup(userinfo.getString("group"));
			// user.setDateCreated(userinfo.getString("date_created"));
		} catch (JSONException e) {
			e.printStackTrace();
			return false;
		}

		return true;
	}

	/**
	 * Builds the request to check if the login token of a user is valid. This
	 * request will send detailed user information as response.
	 * 
	 * @return The JSONObject that contains the request.
	 */
	@Override
	protected String buildTokenLoginRequest(String token, String cookie) {
		JSONObject requestJSON = new JSONObject();
		JSONObject apiJSON = new JSONObject();
		JSONObject loginJSON = new JSONObject();
		try {
			if (token != null) {
				loginJSON.put("token", new JSONString(token));
			} else {
				loginJSON.put("cookie", new JSONString(cookie));
			}
			loginJSON.put("getuserinfo", new JSONString("true"));
			loginJSON.put("image", new JSONString("true"));
			apiJSON.put("login", loginJSON);
			apiJSON.put("api", new JSONString("1.0.0"));
			requestJSON.put("request", apiJSON);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return requestJSON.toString();
	}

	/**
	 * Uploads the actual opened application to ggt
	 * 
	 * @param app
	 *            AppW
	 * @param filename
	 *            String
	 * @param cb
	 *            MaterialCallback
	 */
	public void uploadMaterial(final AppW app, final String filename,
	        String base64, final MaterialCallback cb) {

		performRequest(UploadRequest.getRequestElement(app, filename, base64)
		        .toJSONString(), cb);

	}

	/**
	 * to rename materials on ggt; TODO no use of base64
	 * 
	 * @param app
	 *            {@link AppW}
	 * @param mat
	 *            {@link Material}
	 * @param cb
	 *            {@link MaterialCallback}
	 */
	public void uploadRenameMaterial(final AppW app, Material mat,
	        final MaterialCallback cb) {
		performRequest(
		        UploadRequest.getRequestElement(app, mat.getTitle(),
		                mat.getId()).toJSONString(), cb);
	}

	/**
	 * Uploads a local saved file (web - localStorage; touch - device) to ggt
	 * 
	 * @param app
	 *            {@link AppW}
	 * @param mat
	 *            {@link Material}
	 * @param cb
	 *            {@link MaterialCallback}
	 */
	public void uploadLocalMaterial(final AppW app, final Material mat,
	        final MaterialCallback cb) {
		performRequest(
		        UploadRequest.getRequestElement(app, mat).toJSONString(), cb);
	}

	/**
	 * @param app
	 *            {@link AppW}
	 * @param material
	 *            {@link Material}
	 * @param cb
	 *            {@link MaterialCallback}
	 */
	public void deleteMaterial(AppW app, Material material,
	        final MaterialCallback cb) {
		performRequest(DeleteRequest.getRequestElement(app, material)
		        .toJSONString(), cb);
	}

	/**
	 * @param userId
	 *            int
	 * @param cb
	 *            {@link MaterialCallback}
	 */
	public void getUsersMaterials(int userId, MaterialCallback cb) {
		performRequest(MaterialRequest.forUser(userId, client).toJSONString(),
		        cb);
	}

	/**
	 * @param id
	 *            int
	 * @param cb
	 *            {@link MaterialCallback}
	 */
	public void getBookItems(int id, MaterialCallback cb) {
		performRequest(MaterialRequest.forBook(id, client).toJSONString(), cb);
	}

	public static TreeMap<Integer, Long> toTimestamps(
	        List<Material> parseResponse) {
		TreeMap<Integer, Long> ret = new TreeMap<Integer, Long>();
		for (Material mat : parseResponse) {
			ret.put(mat.getId(), mat.getTimestamp());
		}
		return ret;
	}
}
