package com.lyricoo;

import org.json.JSONException;
import org.json.JSONObject;

/** The User class stores all data pertaining
 * to a logged in user.
 */
public class User {
	// TODO: Change backend to support username and other contact info
	private String mUsername;
	private String mEmail;
	private int mUserId;
	private String mAuthToken;
	
	public User(JSONObject json){
		// TODO: Handle exceptions
		try {
			mUserId = json.getInt("id");
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		try {
			mEmail = json.getString("email");
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public String getEmail(){
		return mEmail;
	}

}