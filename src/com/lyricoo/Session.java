package com.lyricoo;

import java.io.IOException;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

public class Session {
	private static User mCurrentUser;
	private static boolean mLoggedIn = false;
	private static String mAuthToken;
	private static Context mContext;
	private static ConversationManager mConversationManager;
	
	/**
	 * GCM variables 
	 */
	private static String regid = "";
	private final static String SENDER_ID = "69329840121";
	private static GoogleCloudMessaging gcm;
	
	private static String getRegistrationId() {
		return regid;
		// TODO: Set check for app update. if update, 
		// no guarantee that old regid will work 
		// with new version
	}
	
	private static void storeRegistration(String _regid) {
		regid = _regid;
	}
	
    /**
     * Registers the application with GCM servers asynchronously.
     * <p>
     * Stores the registration ID and the app versionCode in the application's
     * shared preferences.
     */
    private static void registerInBackground(final Context context) {
    	new AsyncTask<Void, Void, String>() {
            @Override
            protected String doInBackground(Void... params) {
                String msg = "";
                try {
                    if (gcm == null) {
                        gcm = GoogleCloudMessaging.getInstance(context);
                    }
                    regid = gcm.register(SENDER_ID);
                    msg = "Device registered, registration ID=" + regid;

                    // You should send the registration ID to your server over HTTP, so it
                    // can use GCM/HTTP or CCS to send messages to your app.
                    
                    
                    // For this demo: we don't need to send it because the device will send
                    // upstream messages to a server that echo back the message using the
                    // 'from' address in the message.

                    // Persist the regID - no need to register again.
                    storeRegistration(regid);
                    if (isLoggedIn()) {
                    	RequestParams p = new RequestParams();
                    	p.put("gcm_id", regid);
                    	
                    	currentUser().put(p, new JsonHttpResponseHandler() {
                    		@Override
                    		public void onSuccess(JSONObject json) {
                    			// TODO: DO something here (maybe just UI indication of
                    			//		 registration success .. 
                    			System.out.print("Success user registered");
                    		}
                    		@Override
                    		public void onSuccess(JSONArray json) {
                    			//	TODO: see above*
                    			System.out.print("Success user registered");
                    		}
                    		
                    		@Override
                    		public void onFailure(java.lang.Throwable error, JSONObject json) {
                    			// TODO: Do something when server errors! (maybe keep trying 
                    			// ... this is a big problem if this happens because no one 
                    			//	will be able to communicate with the user because our server
                    			//	will not know the users's gcm id. Perhaps we should implement
                    			//	an http polling feature if this fails.
                    			System.out.print("Error! User unable to register!");
                    		}
                    		@Override
                    		public void onFailure(java.lang.Throwable error, JSONArray json) {
                    			System.out.print("Error! User unable to register!");
                    		}
                    	});
                    } else {
                    	Log.i("GCM", "Encountered a problem, user is not logged in.");
                    }
                } catch (IOException ex) {
                    msg = "Error :" + ex.getMessage();
                    // If there is an error, don't just keep trying to register.
                    // Require the user to click a button again, or perform
                    // exponential back-off.
                }
                return msg;
            }

            @Override
            protected void onPostExecute(String msg) {
            	Log.i("GCM", msg);
                //mDisplay.append(msg + "\n");
            }
        }.execute(null, null, null);
    }
	
	public static void registerGCM(Context context) {
		gcm = GoogleCloudMessaging.getInstance(context);
		String regID = getRegistrationId();
		if (regID.isEmpty()) {
			registerInBackground(context);
		}
	}
	
	public static User currentUser() {
		return mCurrentUser;
	}
	
	public static boolean isLoggedIn() {
		return mLoggedIn;
	}
	
	public static User create(Context context, JSONObject json) {
		mContext = context;
		
		try {
			mCurrentUser = new User(json.getJSONObject("user"));
		} catch (JSONException e) {
			// TODO Handle failure
		}
		
		try {
			mAuthToken = json.getString("authentication_token");
		} catch (JSONException e) {
			// TODO Handle failure
		}
		mLoggedIn = true;
		
		mConversationManager = new ConversationManager(context, mCurrentUser);
		
		return mCurrentUser;
	}
	
	/**
	 * Delete the current user's session, if applicable
	 */
	public static void destroy() {
		mCurrentUser = null;
		mLoggedIn = false;
		mAuthToken = null;
		
		// delete all local message data
		mConversationManager.destroy();
		mConversationManager = null;
	}

	public static void login(String username, String password, JsonHttpResponseHandler 
			responseHandler) {
			
		RequestParams params = new RequestParams();
		params.put("email", username);
		params.put("password", password);
		
		User.REST.post("sign_in", params, responseHandler);
	}

	public static String getAuthToken() {
		return mAuthToken;
	}
	
	public static ConversationManager getConversationManager(){
		return mConversationManager;
	}
}
