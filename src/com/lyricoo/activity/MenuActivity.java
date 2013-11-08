package com.lyricoo.activity;



import com.lyricoo.R;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.view.Menu;
import android.view.View;

public class MenuActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_menu);
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

}
