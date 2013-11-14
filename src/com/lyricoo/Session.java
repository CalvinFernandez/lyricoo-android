package com.lyricoo;

import org.json.JSONException;
import org.json.JSONObject;
import com.loopj.android.http.*;

public class Session {
	private static User mCurrentUser;
	private static boolean mLoggedIn = false;
	private static String mAuthToken;
	
	public static User currentUser() {
		return mCurrentUser;
	}
	
	public static boolean isLoggedIn() {
		return mLoggedIn;
	}
	
	public static User create(JSONObject json) {
		try {
			mCurrentUser = new User(json.getJSONObject("user"));
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		try {
			mAuthToken = json.getString("authentication_token");
		} catch (JSONException e) {
			e.printStackTrace();
		}
		mLoggedIn = true;
		return mCurrentUser;
	}
	
	public static void destroy() {
		mCurrentUser = null;
		mLoggedIn = false;
	}

	public static void login(String username, String password, AsyncHttpResponseHandler 
				asyncHttpResponseHandler) {
			
		RequestParams params = new RequestParams();
		params.put("email", username);
		params.put("password", password);
		LyricooAPI.post("users/sign_in", params, asyncHttpResponseHandler);
	}

	public static String getAuthToken() {
		return mAuthToken;
	}
}
