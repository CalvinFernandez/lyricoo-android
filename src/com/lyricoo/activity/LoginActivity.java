package com.lyricoo.activity;


import org.json.JSONObject;

import com.loopj.android.http.JsonHttpResponseHandler;
import com.lyricoo.R;
import com.lyricoo.Session;

import android.os.Bundle;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.Menu;
import android.view.View;
import android.widget.EditText;

public class LoginActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_login);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.login, menu);
		return true;
	}
	
	public void login(View v){
		final Intent i = new Intent(this, MenuActivity.class);
	
		// TODO: Accept either username or email address in the text field
		EditText usernameView = (EditText) findViewById(R.id.username_field);
		EditText passwordView = (EditText) findViewById(R.id.password_field);
		
		String username = usernameView.getText().toString();
		String password = passwordView.getText().toString();
		
		// debug helper for testing
		if(username.equals("") && password.equals("")){
			username = "konakid@gmail.com";
			password = "asdfasdf";
		}

		Session.login(username, password, new JsonHttpResponseHandler() {
			@Override
			public void onSuccess(JSONObject response) {
				System.out.println(response.toString());
				// TODO: Get server to return complete user info. Right now only auth token and user id are returned
				Session.create(response);
				startActivity(i);
			}
			
			@Override
			public void onFailure(Throwable error, JSONObject response) {				
				handleLoginFailure(response);
			}
			
			@Override
			public void onStart() {
				System.out.println("started");
			}
			@Override 
			public void onFinish() {
				System.out.println("ended");
			}
		});		
		
	}

	private void handleLoginFailure(JSONObject response) {
		new AlertDialog.Builder(this)
	    .setTitle("Login Error")
	    .setMessage("Sorry, there was a problem logging you in")
	    .setPositiveButton("Try Again", new DialogInterface.OnClickListener() {
	        public void onClick(DialogInterface dialog, int which) { 
	            // Close and do nothing
	        }
	     })
	    .setNegativeButton("Reset Password", new DialogInterface.OnClickListener() {
	        public void onClick(DialogInterface dialog, int which) { 
	            // Send reset email
	        	resetPassword();
	        }
	     })
	     .show();		
	}
	
	private void resetPassword(){
		// TODO: Send a reset password email and provide some user feedback about it
	}

}
