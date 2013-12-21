package com.lyricoo;

import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v7.app.ActionBarActivity;
import android.view.MenuItem;

import com.lyricoo.session.LoginActivity;
import com.lyricoo.session.Session;
import com.lyricoo.session.SignUpActivity;

public class LyricooActivity extends ActionBarActivity {
	// we need to be able to access the drawer toggle at certain points in the
	// activity's life
	protected ActionBarDrawerToggle mDrawerToggle;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		checkSession();

	}	

	@Override
	protected void onResume() {
		super.onResume();

		checkSession();

		((LyricooApp) getApplication()).resume();
	}

	@Override
	protected void onPause() {
		super.onPause();
		((LyricooApp) getApplication()).pause();
	}

	@Override
	protected void onStop() {
		super.onStop();

	}
	
	@Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        // Sync the toggle state after onRestoreInstanceState has occurred.
        if(mDrawerToggle != null){
        	mDrawerToggle.syncState();
        }
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        if(mDrawerToggle != null){
        	mDrawerToggle.onConfigurationChanged(newConfig);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Pass the event to ActionBarDrawerToggle, if it returns
        // true, then it has handled the app icon touch event
        if (mDrawerToggle != null && mDrawerToggle.onOptionsItemSelected(item)) {
          return true;
        }

        return super.onOptionsItemSelected(item);
    }


	/**
	 * Make sure the user is properly signed in. If they aren't return to the
	 * loginActivity
	 */
	private void checkSession() {
		// The signup and login pages don't need a login to access
		if (getClass().equals(SignUpActivity.class)
				|| getClass().equals(LoginActivity.class)) {
			return;
		}
		// otherwise make sure the user is logged in
		else if (!Session.isLoggedIn()) {
			Intent i = new Intent(this, LoginActivity.class);
			startActivity(i);
		}
	}
	
	public void setDrawerToggle(ActionBarDrawerToggle toggle){
		mDrawerToggle = toggle;
	}

}
