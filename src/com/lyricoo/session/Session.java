package com.lyricoo.session;

import java.io.IOException;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.AsyncTask;
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
		// TODO: Store using shared preferences
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

					// You should send the registration ID to your server over
					// HTTP, so it
					// can use GCM/HTTP or CCS to send messages to your app.

					// For this demo: we don't need to send it because the
					// device will send
					// upstream messages to a server that echo back the message
					// using the
					// 'from' address in the message.

					// Persist the regID - no need to register again.
					storeRegistration(regid);
					if (isLoggedIn()) {
						RequestParams p = new RequestParams();
						p.put("gcm_id", regid);

						currentUser().put(p, new LyricooApiResponseHandler() {
							@Override
							public void onSuccess(Object json) {
								// TODO: DO something here (maybe just UI
								// indication of
								// registration success ..
								Utility.log("Success user registered");
								((LyricooApp)mContext.getApplicationContext()).setIsGcmRegistered(true);
							}

							@Override
							public void onFailure(int statusCode,
									String responseBody, Throwable error) {
								Utility.log("Error! User unable to register!");
								((LyricooApp)mContext.getApplicationContext()).setIsGcmRegistered(false);
							}

						});
					} else {
						Log.i("GCM",
								"Encountered a problem, user is not logged in.");
					}
				} catch (IOException ex) {
					msg = "Error :" + ex.getMessage();
					// If there is an error, don't just keep trying to register.
					// Require the user to click a button again, or perform
					// exponential back-off.
					((LyricooApp)mContext.getApplicationContext()).setIsGcmRegistered(false);
				}
				return msg;
			}

			@Override
			protected void onPostExecute(String msg) {
				Log.i("GCM", msg);
				// mDisplay.append(msg + "\n");
			}
		}.execute(null, null, null);
	}

	public static void registerGCM(Context context) {
		gcm = GoogleCloudMessaging.getInstance(context);
		String regID = getRegistrationId();
		if (Utility.isStringBlank(regID)) {
			registerInBackground(context);
		} else {
			((LyricooApp)mContext.getApplicationContext()).setIsGcmRegistered(true);
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
		mFriendManager = new FriendManager(context, mCurrentUser);

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
	
	public static FriendManager getFriendManager(){
		return mFriendManager;
	}
	
	/**
	 * Helper for storing username and password. Doesn't need
	 * to be used but makes things simpler.
	 * @param mPrefs
	 * @param username
	 * @param password
	 * @return true or false. True indicates a successful storage, false 
	 * indicates unsuccessful 
	 */
	public static boolean storeRememberable(SharedPreferences mPrefs, String username, String password) {
		
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
	 * @return True or False. True indicates successful transaction.
	 * false indicates unsuccessful.
	 */
	public static boolean destroyRememberable(SharedPreferences mPrefs) {
		Editor mPrefsEdit = mPrefs.edit();
		
		mPrefsEdit.remove("username");
		mPrefsEdit.remove("password");
		mPrefsEdit.putBoolean("rememberable", false);
		
		return mPrefsEdit.commit();
	}
	
}
