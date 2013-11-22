package com.lyricoo.activity;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.lyricoo.Conversation;
import com.lyricoo.LyricooApp;

import com.lyricoo.FriendManager;
import com.lyricoo.R;
import com.lyricoo.Session;
import com.lyricoo.User;
import com.lyricoo.Utility;
import com.lyricoo.api.LyricooApiResponseHandler;

import android.os.Bundle;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.AdapterView.OnItemClickListener;

public class FriendsActivity extends Activity {
	private ArrayList<User> mFriends;
	private ListView mList;
	private Context mContext;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_friends);

		mContext = this;

		// get list view
		mList = (ListView) findViewById(R.id.friends_list);
		
		// load and display friends
		loadFriendsList();
		
		// register callback for when friends list is updated
		Session.getFriendManager().registerOnFriendsUpdatedListener(new FriendManager.OnFriendsUpdatedListener() {
			
			@Override
			public void onFriendsUpdated() {
				//mList.getAdapter().notifyDataSetChanged();
				
			}
		});		
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.friends, menu);
		return true;
	}

	// shows options for interacting with the given friend
	// in a dialog popup
	private void showOptions(final User friend) {
		// the options to show in the dialog list
		String[] options = { "Delete" };
		// create a new dialog
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		// set the title to be the username of the friend
		builder.setTitle(friend.getUsername())
		// add the options to the list
				.setItems(options, new DialogInterface.OnClickListener() {
					// handle clicking on an option
					public void onClick(DialogInterface dialog, int which) {
						switch (which) {
						// "Delete" is the first option in the array
						case 0:
							Session.getFriendManager().removeFriend(friend);
							break;
						default:
							break;
						}
					}
				});
		builder.create().show();
	}

	// Start an activity where the user can add friends to their list
	public void addFriendClicked(View v) {
		Intent i = new Intent(this, ContactsActivity.class);
		startActivity(i);
	}

	// Load the conversation with this friend
	private void loadConversation(User friend) {
		// Pass the friend so conversationActivity knows whose conversation to
		// display.
		// Convert to json to make it easy to pass to the object
		String friendAsJson = Utility.toJson(friend);

		Intent i = new Intent(mContext, ConversationActivity.class);
		i.putExtra("contact", friendAsJson);
		startActivity(i);
	}

	private void loadFriendsList() {

		mFriends = Session.getFriendManager().getFriends();

		// Create adapter for the list view
		FriendsListAdapter adapter = new FriendsListAdapter(mContext, R.layout.friend_list_item, mFriends);

		mList.setAdapter(adapter);

		// on long click show a list of options to the user
		mList.setOnItemLongClickListener(new OnItemLongClickListener() {

			@Override
			public boolean onItemLongClick(AdapterView<?> parent, View view,
					int position, long id) {
				User friend = mFriends.get(position);
				showOptions(friend);

				// Return true to indicate that we have handled the
				// click
				return true;
			}
		});

		// on normal click take the user to the conversation with this
		// friend
		mList.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				User friend = mFriends.get(position);
				loadConversation(friend);
			}

		});

	}

}
