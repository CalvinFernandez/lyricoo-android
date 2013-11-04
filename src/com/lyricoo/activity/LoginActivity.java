package com.lyricoo.activity;


import org.json.JSONObject;

import com.loopj.android.http.JsonHttpResponseHandler;
import com.lyricoo.R;
import com.lyricoo.Session;

import android.os.Bundle;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.view.Menu;
import android.view.View;
import android.widget.EditText;

public class LoginActivity extends Activity {

	private ProgressDialog progress;
	
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
		
		final LoginActivity _this = this;
		
		EditText usernameView = (EditText) findViewById(R.id.username_field);
		EditText passwordView = (EditText) findViewById(R.id.password_field);
		
		String username = usernameView.getText().toString();
		String password = passwordView.getText().toString();

		Session.login(username, password, new JsonHttpResponseHandler() {
			@Override
			public void onSuccess(JSONObject response) {
				progress.dismiss();
				startActivity(i);
			}
			
			@Override
			public void onFailure(Throwable error, JSONObject response) {
				progress.dismiss();
			}
			
			@Override
			public void onStart() {	
				progress = ProgressDialog.show(_this, "Loggin in...", 
						"Please Wait", true);
				
			}
			@Override 
			public void onFinish() {
				progress.dismiss();
				
			}
		});
		
		
		
	}

}
