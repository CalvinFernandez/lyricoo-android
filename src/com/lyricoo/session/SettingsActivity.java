package com.lyricoo.session;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;

import com.lyricoo.LyricooActivity;
import com.lyricoo.R;
import com.lyricoo.ui.SlidingMenuHelper;

public class SettingsActivity extends LyricooActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.activity_settings);
		
		SlidingMenuHelper.addMenuToActivity(this);	
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		getSupportActionBar().setHomeButtonEnabled(true);	
		
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.settings, menu);
		return true;
	}

	// log the user out
	public void logoutClicked(View v) {
		// delete local user info
		Session.destroy();

		// TODO: A way to clear the back stack when logging out
		startActivity(new Intent(this, LoginActivity.class));
		finish();
	}

}
