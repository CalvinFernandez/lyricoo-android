package com.lyricoo.activity;


import com.lyricoo.Conversation;
import com.lyricoo.LyricooApp;
import com.lyricoo.R;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.AdapterView.OnItemClickListener;

public class ConversationActivity extends Activity {
	private LyricooApp mApp;
	private Conversation mConversation;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_conversation);
		
		mApp = (LyricooApp) getApplication();
		mConversation = mApp.conversationToDisplay;
		
		// Create adapter for the list view
		ConversationAdapter adapter = new ConversationAdapter(this, mConversation);
		ListView list = (ListView) findViewById(R.id.messages_list);
		list.setAdapter(adapter);
		
		// When a message is clicked load the whole conversation
		list.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				// TODO: Do something when a message is clicked? Maybe show detailed info
				// about it like time sent
			}					
		});
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.conversation, menu);
		return true;
	}

}
