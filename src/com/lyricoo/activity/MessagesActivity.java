package com.lyricoo.activity;

import java.util.ArrayList;

import org.json.JSONObject;


import com.loopj.android.http.JsonHttpResponseHandler;
import com.lyricoo.Conversation;
import com.lyricoo.LyricooApp;
import com.lyricoo.R;
import com.lyricoo.Session;


import android.os.Bundle;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.ProgressBar;

/** This activity loads all of the users messages
 * and shows a preview of the most recent message from each contact.
 * If a message is clicked the whole coversation is loaded.
 *
 */
public class MessagesActivity extends Activity {
	private ArrayList<Conversation> mConversations;
	private ProgressBar mProgress;
	private Context mContext;
	private LyricooApp mApp;
	

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_messages);
		mContext = this;
		mApp = (LyricooApp) getApplication();

		// start progress bar to indicate loading
		mProgress = (ProgressBar) findViewById(R.id.messages_loading_progress);
		
		Session.currentUser().get("messages", new JsonHttpResponseHandler() {
			@Override
			public void onSuccess(JSONObject json) {
				mConversations = Conversation.parseMessagesJson(json);
				
				// hide progress bar
				mProgress.setVisibility(View.GONE);				
				
				// Create adapter for the list view
				MessageListAdapter adapter = new MessageListAdapter(mContext, mConversations);
				ListView list = (ListView) findViewById(R.id.messages_list);
				list.setAdapter(adapter);
				
				// When a message is clicked load the whole conversation
				list.setOnItemClickListener(new OnItemClickListener() {

					@Override
					public void onItemClick(AdapterView<?> parent, View view,
							int position, long id) {
						// Pass selected conversation to the conversation activity to display
						// TODO: Pass conversation in a cleaner way than storing in app
						mApp.conversationToDisplay = mConversations.get(position);
						Intent i = new Intent(mContext, ConversationActivity.class);
						startActivity(i);
					}					
				});
			}

			@Override
			public void onFailure(Throwable error, JSONObject json) {
				// TODO: Handle failure
				Log.v("Messages", error.getMessage());
			}
			 
		});
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.messages, menu);
		return true;
	}

}
