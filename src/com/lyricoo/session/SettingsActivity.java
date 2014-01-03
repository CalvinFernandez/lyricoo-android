package com.lyricoo.session;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;

import com.lyricoo.LyricooActivity;
import com.lyricoo.R;
import com.lyricoo.Utility;
import com.lyricoo.api.LyricooApiResponseHandler;
import com.lyricoo.ui.SlidingMenuHelper;

public class SettingsActivity extends LyricooActivity {
	private Context mContext;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_settings);

		SlidingMenuHelper.addMenuToActivity(this, true);
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		getSupportActionBar().setHomeButtonEnabled(true);

		mContext = this;
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.settings, menu);
		return true;
	}

	// log the user out
	public void logoutClicked(View v) {
		// log the user out on the server. If the request fails make a toast
		Session.logout(new LyricooApiResponseHandler() {

			@Override
			public void onSuccess(Object responseJson) {
				Utility.log("User signed out successfully");
				// delete local user info
				Session.destroy();

				// TODO: A way to clear the back stack when logging out
				startActivity(new Intent(mContext, LoginActivity.class));
				finish();
			}

			@Override
			public void onFailure(int statusCode, String responseBody,
					Throwable error) {
				Utility.makeBasicToast(mContext,
						"Error logging out");
			}
		});

	}

}
