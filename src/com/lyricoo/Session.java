package com.lyricoo;

import java.io.IOException;

import org.json.JSONException;
import org.json.JSONObject;
import org.apache.http.Header;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.loopj.android.http.*;

public class Session {
	private static User mCurrentUser;
	private static boolean mLoggedIn = false;
	private static String mAuthToken;
	
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
                    	
                    	currentUser().put(p, new AsyncHttpResponseHandler() {
                    		@Override
                    		public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                    			System.out.print("hello");
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
}
