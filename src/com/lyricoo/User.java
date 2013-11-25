package com.lyricoo;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.lyricoo.api.LyricooModel;

/** The User class stores all data pertaining
 * to a logged in user.
 */
public class User extends LyricooModel {
	
	// TODO: Change backend to support username and other contact info
	private String mUsername;
	private String mEmail;
	private int mUserId;
	private String mPhoneNumber;
	
	private static String baseUrl = "users";
	public static LyricooModel REST = new LyricooModel(baseUrl);
	
	
	
	public User(JSONObject json) {
		super();
		
		// TODO: Handle exceptions
		try {
			mUserId = json.getInt("id");
			setBaseUrl(baseUrl + "/" + mUserId);
			
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
		
		try {
			mUsername = json.getString("username");
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		try {
			// TODO: Probably need to use getInt. Need to decide which form to use on server.
			mPhoneNumber = json.getString("phone_number");
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public User() {
		this(new JSONObject());
	}
	
	public String getEmail(){
		return mEmail;
	}
	
	public String getUsername(){
		return mUsername;
	}	
	

	public int getUserId() {
		return mUserId;
	}


	/** Parse a json array of users into an ArrayList<User>
	 * 
	 * @param json
	 * @return
	 * @throws JSONException 
	 */
	public static ArrayList<User> parseUserJsonArray(JSONArray json) {
		// initialize arraylist to hold results
		ArrayList<User> result = new ArrayList<User>();
		
		// loop through json array
		int userCount = json.length();
		for(int i = 0; i < userCount; i++){
			JSONObject userJson = null;
			try {
				userJson = json.getJSONObject(i);
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			if(userJson != null){
				User user = new User(userJson);
				result.add(user);
			}			
		}
		
		return result;		
	}

	/** Check if the given contact has matching user credentials to this user
	 * 
	 * @param contact
	 * @return
	 */
	public boolean isContactMatch(PhoneContact contact) {
		// only checking phone numbers. Could check email addresses if we wanted to. 
		
		// Check each phone number of the contact
		for(String number : contact.getNumbers()){
			if(Utility.isPhoneNumberEqual(mPhoneNumber, number)){
				return true;
			}
		}
		
		return false;
	}
	
	/**
	 * Test if one user object is the same as another
	 * @param user
	 */	
	@Override
	public boolean equals(Object object){
		// if object is null it is unequal by default
		if(object == null){
			return false;
		}
		
		if (object == this) {
			return true;
		}
		
		// Make sure the object is a User
		if (!(object instanceof User)){
            return false;
		}
		
		User user = (User) object;		
		
		// the only thing we have to look at is user id
		return (user.getUserId() == mUserId);
	}
	// TODO: Since we are overriding equals() we should also override hashCode() or bugs could pop up
	
	
}
