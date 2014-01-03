package com.lyricoo.session;

import java.io.IOException;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.util.Log;

import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.loopj.android.http.RequestParams;
import com.lyricoo.LyricooApp;
import com.lyricoo.Utility;
import com.lyricoo.api.LyricooApiResponseHandler;
import com.lyricoo.friends.FriendManager;
import com.lyricoo.messages.ConversationManager;

public class Session {

	private static User mCurrentUser;
	private static boolean mLoggedIn = false;

	private static String mAuthToken;
	private static Context mContext;

	private static ConversationManager mConversationManager;
	private static FriendManager mFriendManager;
	private static final String GCM_REGID_PREFERENCE_KEY = "gcm_regid";

	/**
	 * GCM variables
	 */
	private final static String SENDER_ID = "69329840121";
	private static GoogleCloudMessaging gcm;

	private static String getRegistrationId(Context context) {
		SharedPreferences sharedPref = context.getSharedPreferences(
				Utility.PREFS_NAME, 0);
		return sharedPref.getString(GCM_REGID_PREFERENCE_KEY, "");
	}

	private static void storeRegistration(Context context, String _regid) {
		SharedPreferences sharedPref = context.getSharedPreferences(
				Utility.PREFS_NAME, 0);
		SharedPreferences.Editor editor = sharedPref.edit();
		editor.putString(GCM_REGID_PREFERENCE_KEY, _regid);
		editor.commit();
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
					final String regid = gcm.register(SENDER_ID);
					msg = "Device registered, registration ID=" + regid;

					// Store the id so we don't have to register again
					storeRegistration(context, regid);
					
					// attach the id to the user on the server so we can get updates
					attachGcmIdToUser(regid);

				} catch (IOException ex) {
					msg = "Error :" + ex.getMessage();
					// If there is an error, don't just keep trying to register.
					// Require the user to click a button again, or perform
					// exponential back-off.
					((LyricooApp) mContext.getApplicationContext())
							.setIsGcmRegistered(false);
				}
				return msg;
			}

			@Override
			protected void onPostExecute(String msg) {
				Log.i("GCM", msg);
			}
		}.execute(null, null, null);
	}

	/**
	 * Get the gcm id for the app and associate it with the logged in user
	 * 
	 * @param context
	 */
	public static void registerGCM(Context context) {
		gcm = GoogleCloudMessaging.getInstance(context);
		// Check if we have already have an id for this device
		String regID = getRegistrationId(context);

		// if not get a new one, otherwise attach it to the logged in user on
		// the server
		if (Utility.isStringBlank(regID)) {
			registerInBackground(context);
		} else {
			attachGcmIdToUser(regID);
		}
	}

	/**
	 * Attach the given gcm Id to the currently logged in user. This will allow
	 * the server to send message updates to the user
	 * 
	 * @param gcmId
	 */
	public static void attachGcmIdToUser(String gcmId) {
		if (!isLoggedIn()) {
			Log.i("GCM", "Encountered a problem, user is not logged in.");
			return;
		}
		RequestParams p = new RequestParams();
		p.put("gcm_id", gcmId);

		currentUser().put(p, new LyricooApiResponseHandler() {
			@Override
			public void onSuccess(Object json) {
				Utility.log("Success user gcm registered");
				((LyricooApp) mContext.getApplicationContext())
						.setIsGcmRegistered(true);				
			}

			@Override
			public void onFailure(int statusCode, String responseBody,
					Throwable error) {
				Utility.log("Error registering gcm id on server");
				((LyricooApp) mContext.getApplicationContext())
						.setIsGcmRegistered(false);
			}

		});
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
		mFriendManager = new FriendManager(context, mCurrentUser);

		return mCurrentUser;
	}
	
	public static void logout(LyricooApiResponseHandler responseHandler){
		RequestParams params = new RequestParams();
		params.put("email", currentUser().getEmail());
		Utility.log(params.toString());
		User.REST.post("sign_out", params, responseHandler);
	}

	/**
	 * Delete the current user's local session info
	 */
	public static void destroy() {			
		// clear local session data
		mCurrentUser = null;
		mLoggedIn = false;
		mAuthToken = null;

		// delete all local message data
		if(mConversationManager != null){
			mConversationManager.destroy();
			mConversationManager = null;
		}		

		// remove stored friends
		mFriendManager = null;
	}

	public static void login(String username, String password,
			LyricooApiResponseHandler responseHandler) {

		RequestParams params = new RequestParams();
		params.put("email", username);
		params.put("password", password);

		User.REST.post("sign_in", params, responseHandler);
	}

	public static String getAuthToken() {
		return mAuthToken;
	}

	public static ConversationManager getConversationManager() {
		return mConversationManager;
	}

	public static FriendManager getFriendManager() {
		return mFriendManager;
	}

	/**
	 * Helper for storing username and password. Doesn't need to be used but
	 * makes things simpler.
	 * 
	 * @param mPrefs
	 * @param username
	 * @param password
	 * @return true or false. True indicates a successful storage, false
	 *         indicates unsuccessful
	 */
	public static boolean storeRememberable(SharedPreferences mPrefs,
			String username, String password) {

		Editor mPrefsEdit = mPrefs.edit();

		mPrefsEdit.putBoolean("rememberable", true);
		mPrefsEdit.putString("username", username);
		mPrefsEdit.putString("password", password);

		return mPrefsEdit.commit();
	}

	/**
	 * Helper for destroying username and password from SharedPreferences
	 * 
	 * @param mPrefs
	 * @return True or False. True indicates successful transaction. false
	 *         indicates unsuccessful.
	 */
	public static boolean destroyRememberable(SharedPreferences mPrefs) {
		Editor mPrefsEdit = mPrefs.edit();

		mPrefsEdit.remove("username");
		mPrefsEdit.remove("password");
		mPrefsEdit.putBoolean("rememberable", false);

		return mPrefsEdit.commit();
	}

}
