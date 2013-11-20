package com.lyricoo.activity;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.ProgressBar;

import com.lyricoo.Conversation;
import com.lyricoo.ConversationManager;
import com.lyricoo.LyricooApp;
import com.lyricoo.R;
import com.lyricoo.Session;
import com.lyricoo.User;
import com.lyricoo.Utility;

/**
 * This activity loads all of the users messages and shows a preview of the most
 * recent message from each contact. If a message is clicked the whole
 * coversation is loaded.
 * 
 */
public class MessagesActivity extends Activity {
	private ArrayList<Conversation> mConversations;
	private MessageListAdapter mAdapter;
	private ProgressBar mProgress;
	private Context mContext;
	private LyricooApp mApp;

	// display resources
	private ListView mMessageList;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_messages);
		mContext = this;
		mApp = (LyricooApp) getApplication();

		// load conversation data
		mConversations = Session.getConversationManager().getConversations();

		// register callback on conversations update
		Session.getConversationManager().registerOnDataChangedListener(
				new ConversationManager.OnDataChangedListener() {

					@Override
					public void onDataUpdated(User user) {
						// we care about conversations with all contacts, so
						// update everything
						updateConversations();
					}

					@Override
					public void onDataReset() {
						// Get a fresh copy of the conversation
						mConversations = Session.getConversationManager()
								.getConversations();
						displayConversations();
					}
				});

		// save resources
		mMessageList = (ListView) findViewById(R.id.messages_list);

		displayConversations();
	}

	/**
	 * Tell the adapter that the data has changed and it needs to update the
	 * view
	 */
	protected void updateConversations() {
		// TODO: Show indicators for new messages
		mAdapter.notifyDataSetChanged();
	}

	/**
	 * Get all of our users conversations and show them in a listview
	 */
	private void displayConversations() {
		// Create a new adapter for this conversation data
		mAdapter = new MessageListAdapter(mContext, mConversations);

		mMessageList.setAdapter(mAdapter);

		// When a message is clicked load the whole conversation
		mMessageList.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				// Pass selected user so conversationActivity knows whose
				// conversation to display
				User contact = mConversations.get(position).getContact();

				// convert to json to make it easy to pass to the object
				String contactAsJson = Utility.toJson(contact);

				Intent i = new Intent(mContext, ConversationActivity.class);
				i.putExtra("contact", contactAsJson);
				startActivity(i);
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
