package com.lyricoo.activity;






import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;

import com.lyricoo.LyricooActivity;
import com.lyricoo.R;
import com.lyricoo.friends.FriendsActivity;
import com.lyricoo.messages.MessagesActivity;
import com.lyricoo.music.LyricooSelectionActivity;
import com.lyricoo.session.LoginActivity;
import com.lyricoo.session.SettingsActivity;

public class MenuActivity extends LyricooActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		boolean logout = getIntent().getBooleanExtra("logout", false);
		if (logout) {
			/*
			 * If logout, go back to the LoginActivity.
			 */
			startActivity(new Intent(this, LoginActivity.class));
			finish();
		} else {
			/*
			 * Normal onCreate. Just build main menu
			 */
			setContentView(R.layout.activity_menu);
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.menu, menu);
		return true;
	}
	
	public void messagesClicked(View v){
		Intent i = new Intent(this, MessagesActivity.class);
		startActivity(i);
	}
	
	public void lyricoosClicked(View v){
		Intent i = new Intent(this, LyricooSelectionActivity.class);
		startActivity(i);
	}
	
	public void friendsClicked(View v){
		Intent i = new Intent(this, FriendsActivity.class);
		startActivity(i);
	}

	public void settingsClicked(View v){
		Intent i = new Intent(this, SettingsActivity.class);
		startActivity(i);
	}
}
