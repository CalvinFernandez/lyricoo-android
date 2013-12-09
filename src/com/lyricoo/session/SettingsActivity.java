package com.lyricoo.session;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.ToggleButton;

import com.lyricoo.LyricooActivity;
import com.lyricoo.R;
import com.lyricoo.messages.InboxActivity;
import com.lyricoo.ui.SlidingMenuHelper;

public class SettingsActivity extends LyricooActivity {
	private ToggleButton mNotificationToggle;
	private SeekBar mVolumeSeek;
	private LyricooSettings mSettings;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_settings);
		SlidingMenuHelper.addMenuToActivity(this);

		// retrieve resources for later
		mNotificationToggle = (ToggleButton) findViewById(R.id.notification_toggle);
		mVolumeSeek = (SeekBar) findViewById(R.id.volume_seek);

		// Get the user settings
		mSettings = LyricooSettings.getUserSettings();

		// Update layout to reflect user's settings
		mVolumeSeek.setMax(mSettings.getMaxVolume());
		mVolumeSeek.setProgress(mSettings.getVolume());
		mNotificationToggle.setChecked(mSettings.showNotifications());

		// setup callback for volume setting changed
		mVolumeSeek.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {

			@Override
			public void onProgressChanged(SeekBar seekBar, int progress,
					boolean fromUser) {
				// Save the new volume
				mSettings.setVolume(progress);
			}

			@Override
			public void onStartTrackingTouch(SeekBar seekBar) {
				// Don't care
			}

			@Override
			public void onStopTrackingTouch(SeekBar seekBar) {
				// Don't care
			}
		});

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

	public void notificationToggleClicked(View v) {
		// Is the toggle on?
		boolean on = ((ToggleButton) v).isChecked();

		mSettings.setShowNotifications(on);
	}

}
