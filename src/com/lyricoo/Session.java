package com.lyricoo;

import org.json.JSONObject;
import com.loopj.android.http.*;

public class Session extends User {

	private String mAuthToken;
	
	public Session(JSONObject json) {
		super(json);
	}
	
	public static void login(String username, String password, JsonHttpResponseHandler 
				responseHandler) {
			
		RequestParams params = new RequestParams();
		params.put("email", username);
		params.put("password", password);
		LyricooAPI.post("users/sign_in", params, responseHandler);
	}
}
