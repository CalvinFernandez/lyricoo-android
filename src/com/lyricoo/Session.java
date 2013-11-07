package com.lyricoo;

import org.json.JSONObject;
import com.loopj.android.http.*;

public class Session {
	private static User current_user;
	private static boolean logged_in = false;
	
	public static User currentUser() {
		return current_user;
	}
	
	public static boolean isLoggedIn() {
		return logged_in;
	}
	
	public static User create(JSONObject json) {
		current_user = new User(json);
		logged_in = true;
		return current_user;
	}
	
	public static void destroy() {
		current_user = null;
		logged_in = false;
	}

	public static void login(String username, String password, JsonHttpResponseHandler 
				responseHandler) {
			
		RequestParams params = new RequestParams();
		params.put("email", username);
		params.put("password", password);
		LyricooAPI.post("users/sign_in", params, responseHandler);
	}

	public static String getAuthToken() {
		// TODO Auto-generated method stub
		return null;
	}
}
