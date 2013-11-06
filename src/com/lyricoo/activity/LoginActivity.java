package com.lyricoo.activity;


import org.json.JSONObject;

import com.loopj.android.http.JsonHttpResponseHandler;
import com.lyricoo.R;
import com.lyricoo.Session;

import android.os.Bundle;
import android.app.Activity;
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
				Session.create(response);
				startActivity(i);
			}
			
			@Override
			public void onFailure(Throwable error, JSONObject response) {
				System.out.println(response.toString());
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

}
