package com.lyricoo;

import com.lyricoo.session.LoginActivity;
import com.lyricoo.session.Session;
import com.lyricoo.session.SignUpActivity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

public class LyricooActivity extends Activity {
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		checkSession();
	}

	@Override
	protected void onResume() {
		super.onResume();
		
		checkSession();
		
		((LyricooApp)getApplication()).resume();
	}
	
	@Override
	protected void onPause() {
		super.onPause();
		
		((LyricooApp)getApplication()).pause();
	}
	
	@Override
	protected void onStop() {
		super.onStop();
		
	}
	
	/**
	 * Make sure the user is properly signed in. If they aren't return to the loginActivity
	 */
	private void checkSession(){
		// The signup and login pages don't need a login to access
		if(getClass().equals(SignUpActivity.class) || getClass().equals(LoginActivity.class)){
			return;
		}
		// otherwise make sure the user is logged in
		else if(!Session.isLoggedIn()){
			Intent i = new Intent(this, LoginActivity.class);
			startActivity(i);			
		}
	}

}
