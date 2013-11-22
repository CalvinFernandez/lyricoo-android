package com.lyricoo.activity;


import org.apache.http.Header;

import java.io.IOException;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.ResponseHandler;

import org.json.JSONObject;
import org.json.JSONArray;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;



import com.lyricoo.R;
import com.lyricoo.Session;
import com.lyricoo.Utility;
import com.lyricoo.api.LyricooAPI;
import com.lyricoo.api.LyricooApiResponseHandler;
import com.lyricoo.api.LyricooResponseAdapter;

import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;

public class LoginActivity extends Activity {
	private ProgressBar mProgress;
	private Context mContext;
	private final static int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;
	
	String SENDER_ID = "69329840121";
	String regid;
	 
	/**
	 * GCM Tag
	 */
	static final String TAG = "GCM";
	
	GoogleCloudMessaging gcm;
	

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_login);

		// get progress bar
		mProgress = (ProgressBar) findViewById(R.id.sign_in_progress);

		// save context
		mContext = this;
		
		if (!checkPlayServices()) {
			Log.i(TAG, "No valid Google Play Services APK found");
		}
	}

	/**
	 * Check the device to make sure it has the Google Play Services APK. If
	 * it doesn't, display a dialog that allows users to download the APK from
	 * the Google Play Store or enable it in the device's system settings.
	 */
	private boolean checkPlayServices() {
	    /*int resultCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(getApplicationContext());
	    if (resultCode != ConnectionResult.SUCCESS) {
	        if (GooglePlayServicesUtil.isUserRecoverableError(resultCode)) {
	            GooglePlayServicesUtil.getErrorDialog(resultCode, this,
	                    PLAY_SERVICES_RESOLUTION_REQUEST).show();
	        } else {
	            Log.i(TAG, "This device is not supported.");
	            finish();
	        }
	        return false;
	    }*/
	    return true;
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.login, menu);
		return true;
	}

	public void login(View v) {
		setIsLoading(true);

		// TODO: Accept either username or email address in the text field.
		// Right now the server only accepts email
		EditText usernameView = (EditText) findViewById(R.id.username_field);
		EditText passwordView = (EditText) findViewById(R.id.password_field);

		String username = usernameView.getText().toString();
		String password = passwordView.getText().toString();

		// debug helper for testing
		if (username.equals("") && password.equals("")) {
			username = "konakid@gmail.com";
			password = "asdfasdf";
		}
		// hacky thing to allow the email address to be final and used in the
		// callback, but still be able to change it to konakid in the debug
		// check
		final String email = username;

		Session.login(username, password, new LyricooApiResponseHandler() {

			@Override
			public void onSuccess(Object responseJson) {
				Session.create(getApplicationContext(), (JSONObject) responseJson);
				
				Session.registerGCM(mContext);
				
				Intent i = new Intent(mContext, MenuActivity.class);
				startActivity(i);
			}
			
			 @Override
			 public void onFailure(int statusCode, String responseBody, Throwable error) {
			 handleLoginFailure(email);
			 }

			
			 @Override
			 public void onFinish() {
			 setIsLoading(false);
			 }

		});

	}

	private void handleLoginFailure(final String email) {
		new AlertDialog.Builder(this)
				.setTitle("Login Error")
				.setMessage("Sorry, there was a problem logging you in")
				.setPositiveButton("Try Again",
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog,
									int which) {
								// Close and do nothing
							}
						})
				.setNegativeButton("Reset Password",
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog,
									int which) {
								// Send reset email
								resetPassword(email);
							}
						}).show();
	}

	/**
	 * Send a new password to the given email address. Show toast on completion.
	 * 
	 * @param email
	 */
	private void resetPassword(final String email) {
		RequestParams params = new RequestParams();
		params.put("email", email);

		LyricooAPI.post("users/reset_password", params,
				new AsyncHttpResponseHandler() {

					@Override
					public void onSuccess(int statusCode, Header[] headers,
							byte[] responseBody) {
						// TODO: Right now response failure is in json not in
						// error codes. Change that on the server
						String msg = "New password sent to " + email;
						Utility.makeBasicToast(mContext, msg);
					}

					@Override
					public void onFailure(int statusCode,
							org.apache.http.Header[] headers,
							byte[] responseBody, java.lang.Throwable error) {
						String msg = "Failed sending new password to " + email;
						Utility.makeBasicToast(mContext, msg);
					}
				});
	}

	public void signupClicked(View v) {
		Intent i = new Intent(this, SignUpActivity.class);
		startActivity(i);
	}

	/**
	 * Set whether or not the activity should display the loading state with
	 * buttons disabled and progress bar visible
	 * 
	 * @param isLoading
	 */
	private void setIsLoading(boolean isLoading) {
		// get buttons
		Button login = (Button) findViewById(R.id.login_button);
		Button signup = (Button) findViewById(R.id.signup_button);

		// hide buttons and show progress if loading is true
		if (isLoading) {
			signup.setVisibility(View.GONE);
			login.setVisibility(View.GONE);
			mProgress.setVisibility(View.VISIBLE);
		}
		// reverse it otherwise
		else {
			signup.setVisibility(View.VISIBLE);
			login.setVisibility(View.VISIBLE);
			mProgress.setVisibility(View.GONE);
		}
	}
}
