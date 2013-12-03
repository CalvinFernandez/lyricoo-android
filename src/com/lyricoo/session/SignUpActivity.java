package com.lyricoo.session;

import java.util.ArrayList;
import java.util.Iterator;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.PhoneNumberUtils;
import android.text.Html;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;

import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.lyricoo.LyricooActivity;
import com.lyricoo.R;
import com.lyricoo.session.Session;
import com.lyricoo.Utility;
import com.lyricoo.activity.MenuActivity;
import com.lyricoo.api.LyricooApi;

public class SignUpActivity extends LyricooActivity {
	private Context mContext;
	private ProgressBar mProgress;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_sign_up);

		// get progress bar
		mProgress = (ProgressBar) findViewById(R.id.sign_up_progress);

		// save context
		mContext = this;
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.sign_up, menu);
		return true;
	}

	public void createAccountClicked(View v) {
		// retrieve form data
		String username = ((EditText) findViewById(R.id.username_field))
				.getText().toString();
		String password = ((EditText) findViewById(R.id.password_field))
				.getText().toString();
		String email = ((EditText) findViewById(R.id.email_field)).getText()
				.toString();
		String number = ((EditText) findViewById(R.id.number_field)).getText()
				.toString();

		// use the helper class to pass data around
		AccountCreationFormData data = new AccountCreationFormData(username,
				password, email, number);

		// make sure the input is well formed. If it is, send a request to
		// create a new account, otherwise alert the user to the errors
		ArrayList<ErrorMessage> errors = validateFormData(data);
		if (errors.isEmpty()) {
			createAccount(data);
		} else {
			displayErrors(errors);
		}
	}

	/**
	 * Sends a request to the server to create a new user account with the given
	 * data
	 * 
	 * @param data
	 */
	private void createAccount(AccountCreationFormData data) {
		// show loading bar
		setIsLoading(true);

		// format phone number to properly include hyphens
		data.number = Utility.formatPhoneNumberForServer(data.number);

		// create parameters for post
		RequestParams params = new RequestParams();
		params.put("email", data.email);
		params.put("password", data.password);
		params.put("username", data.username);
		params.put("phone_number", data.number);

		LyricooApi.post("users", params, new JsonHttpResponseHandler() {
			@Override
			public void onSuccess(JSONObject response) {
				// store user credentials
				Session.create(mContext, response);

				// navigate to main activity
				Intent i = new Intent(mContext, MenuActivity.class);
				startActivity(i);
			}

			@Override
			public void onFailure(int statusCode,
					org.apache.http.Header[] headers,
					java.lang.String responseBody, java.lang.Throwable e) {
				// if the request failed because of form errors then the json
				// response will list the problems
				try {
					Object responseJson = parseResponse(responseBody);
					if (responseJson instanceof JSONObject) {
						// display errors to user
						handleFailure((JSONObject) responseJson);
						return;
					}
				} catch (Exception ex) {
					// json exception
				}
				
				// if that didn't work then something went wrong in the connection with the server
				Utility.makeBasicToast(mContext, "Unable to create a new account, please try again");
			}

			@Override
			public void onFinish() {
				// hide loading bar
				setIsLoading(false);
			}
		});
	}

	/**
	 * Take the json response from a failed account creation request and alert
	 * the user of the problems.
	 * 
	 * @param response
	 */
	private void handleFailure(JSONObject response) {
		// get list of errors from response
		ArrayList<ErrorMessage> errors = parseFailureResponse(response);
		// show the user an alert with the errors
		displayErrors(errors);
	}

	/**
	 * Generate a list of error messages from the json reponse to a failed
	 * account creation
	 * 
	 * @param response
	 * @return
	 */
	private ArrayList<ErrorMessage> parseFailureResponse(JSONObject response) {
		ArrayList<ErrorMessage> result = new ArrayList<ErrorMessage>();

		try {
			JSONObject errors = response.getJSONObject("errors");

			// keys() Gives us an iterator through the String names of the json
			// object
			Iterator it = errors.keys();
			// add each message to the result list
			while (it.hasNext()) {
				String key = (String) it.next();
				// each key has an associated array which has at least one
				// message
				JSONArray array = errors.getJSONArray(key);
				int numMessages = array.length();
				for (int i = 0; i < numMessages; i++) {
					String msg = array.getString(i);
					result.add(new ErrorMessage(key, msg));
				}

			}
		} catch (JSONException e) {
			// Can't get errors for some reason. Leave list empty
		} catch (NullPointerException e) {

		}

		return result;
	}

	/**
	 * Display an alert dialog with the error messages in the given list. If the
	 * list is empty show a generic error message.
	 * 
	 * @param errors
	 */
	private void displayErrors(ArrayList<ErrorMessage> errors) {
		String msg = "";

		// Format the error messages into a bulleted list using html
		for (ErrorMessage error : errors) {
			msg += "&#8226;" + error.type + " " + error.message + "<br/>";
		}

		// If there are no specific error messages show a generic message
		if (errors.isEmpty()) {
			msg = "Unable to create account. Please try again.";
		}

		// Use the Builder class for convenient dialog construction
		AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
		builder.setTitle("Error");
		// Our bulleted list must be formatted as html to display properly
		builder.setMessage(Html.fromHtml(msg));
		builder.setPositiveButton("OK", null);

		// Create the AlertDialog object and return it
		builder.create().show();

	}

	private void setIsLoading(boolean isLoading) {
		// get buttons
		Button create = (Button) findViewById(R.id.create_account_button);

		// hide buttons and show progress if loading is true
		if (isLoading) {
			create.setVisibility(View.GONE);
			mProgress.setVisibility(View.VISIBLE);
		}
		// reverse it otherwise
		else {
			create.setVisibility(View.VISIBLE);
			mProgress.setVisibility(View.GONE);
		}
	}

	/**
	 * Check the values in the account creation form and make sure they are all
	 * well formed. Display an alert dialog with any errors
	 * 
	 * @return
	 */
	private ArrayList<ErrorMessage> validateFormData(
			AccountCreationFormData data) {
		ArrayList<ErrorMessage> result = new ArrayList<ErrorMessage>();

		// check username
		// Simply make sure it's not blank. For now let the server do any other
		// necessary checks
		if (Utility.isStringBlank(data.username)) {
			result.add(new ErrorMessage("Username", "can't be blank"));
		}

		// check password
		// Simply make sure it's not blank. For now let the server do any other
		// necessary checks
		if (Utility.isStringBlank(data.password)) {
			result.add(new ErrorMessage("Password", "can't be blank"));
		}

		// check email
		if (!Utility.isValidEmail(data.email)) {
			result.add(new ErrorMessage("Email", "must be valid"));
		}

		// check phone number
		if (!PhoneNumberUtils.isGlobalPhoneNumber(data.number)) {
			result.add(new ErrorMessage("Phone Number", "must be valid"));
		}
		// make sure there is an area code (and allow for country code)
		if (data.number.length() < 10) {
			result.add(new ErrorMessage("Phone Number",
					"must include area code"));
		}

		return result;
	}

	/**
	 * Helper class to pass around form data
	 * 
	 */
	private class AccountCreationFormData {
		public String username;
		public String password;
		public String email;
		public String number;

		public AccountCreationFormData(String username, String password,
				String email, String number) {

			this.username = username;
			this.password = password;
			this.email = email;
			this.number = number;
		}

	}

	/**
	 * Helper class to store an error message from the account creation process
	 */
	private class ErrorMessage {
		public String type;
		public String message;

		public ErrorMessage(String type, String message) {
			this.type = type;
			this.message = message;
		}

	}

}
