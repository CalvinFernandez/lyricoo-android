package com.lyricoo.messages;

import java.util.ArrayList;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

import com.lyricoo.LyricooActivity;
import com.lyricoo.R;
import com.lyricoo.Utility;
import com.lyricoo.session.LoginActivity;
import com.lyricoo.session.Session;
import com.lyricoo.session.User;
import com.lyricoo.ui.SlidingMenuHelper;

/**
 * This activity loads all of the users messages and shows a preview of the most
 * recent message from each contact. If a message is clicked the whole
 * conversation is loaded.
 * 
 */
public class InboxActivity extends LyricooActivity {
	private ArrayList<Conversation> mConversations;
	private MessageListAdapter mAdapter;
	private Context mContext;

	// Callback listener for when messages are updated
	private ConversationManager.OnDataChangedListener mConversationListener;

	// display resources
	private ListView mMessageList;

	@Override
	protected void onCreate(Bundle savedInstanceState) {		
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.activity_messages);
		SlidingMenuHelper.addMenuToActivity(this);
		mContext = this;

		// load conversation data
		mConversations = Session.getConversationManager().getConversations();

		// register callback on conversations update
		mConversationListener = new ConversationManager.OnDataChangedListener() {

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
		};

		Session.getConversationManager().registerOnDataChangedListener(
				mConversationListener);

		// save resources
		mMessageList = (ListView) findViewById(R.id.messages_list);

		displayConversations();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();

		try {
			Session.getConversationManager().unregisterOnDataChangedListener(
					mConversationListener);
		} catch (Exception e) {
			// thrown if conversation manager if null
		}
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
				Conversation convo = mConversations.get(position);
				// Pass selected user so conversationActivity knows whose
				// conversation to display
				User contact = convo.getContact();
				
				//  Mark conversation as read
				convo.read();
				
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
