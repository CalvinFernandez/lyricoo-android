package com.lyricoo;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONObject;

import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.lyricoo.activity.ContactsActivity;
import com.lyricoo.activity.ConversationActivity;
import com.lyricoo.activity.FriendsListAdapter;
import com.lyricoo.activity.MessageListAdapter;

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
	private ProgressBar mProgress;
	private Context mContext;
	private LyricooApp mApp;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_friends);

		mContext = this;
		mApp = (LyricooApp) getApplication();

		// start progress bar to indicate loading
		mProgress = (ProgressBar) findViewById(R.id.friends_loading_progress);

		// load friends
		RequestParams params = new RequestParams();
		params.put("auth_token", Session.getAuthToken());
		LyricooAPI.get("users/57/friends", params,
				new JsonHttpResponseHandler() {
					@Override
					public void onSuccess(JSONArray json) {
						// parse json into messages
						mFriends = User.parseUserJson(json);

						// hide progress bar
						mProgress.setVisibility(View.GONE);

						// Create adapter for the list view
						FriendsListAdapter adapter = new FriendsListAdapter(
								mContext, mFriends);
						ListView list = (ListView) findViewById(R.id.friends_list);
						list.setAdapter(adapter);

						// on long click show a list of options to the user
						list.setOnItemLongClickListener(new OnItemLongClickListener() {

							@Override
							public boolean onItemLongClick(AdapterView<?> parent,
									View view, int position, long id) {
								User friend = mFriends.get(position);
								showOptions(friend);
								
								// Return true to indicate that we have handled the click
								return true;
							}
						});

						// on normal click take the user to the conversation with this friend
						list.setOnItemClickListener(new OnItemClickListener() {

							@Override
							public void onItemClick(AdapterView<?> parent,
									View view, int position, long id) {
								User friend = mFriends.get(position);
								loadConversation(friend);
							}

						});
					}

					@Override
					public void onFailure(Throwable error, JSONObject json) {
						// TODO: Handle failure
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
							removeFriend(friend);
							break;
						default:
							break;
						}
					}
				});
		builder.create();

	}

	// Remove the given friend from the user's friend list
	public void removeFriend(User friend) {
		// TODO: send destroy request to server, verify reponse, update display,
		// and notify user of success
	}

	// Start an activity where the user can add friends to their list
	public void addFriend(View v) {
		Intent i = new Intent(this, ContactsActivity.class);
		startActivity(i);
	}
	
	// Load the conversation with this friend
	public void loadConversation(User friend){
		RequestParams params = new RequestParams();
		params.put("id", Session.currentUser().getUserId());
		params.put("contact_id", friend.getUserId());
		LyricooAPI.get("users/messages", params, new JsonHttpResponseHandler() {
			@Override
			public void onSuccess(JSONArray json) {
				// TODO: Clean this up and verify that it works with the backend
				ArrayList<Conversation> convo = Conversation.parseMessagesJson(json);
				mApp.conversationToDisplay = convo.get(0);
				Intent i = new Intent(mContext, ConversationActivity.class);
				startActivity(i);				
			}

			@Override
			public void onFailure(Throwable error, JSONObject json) {
				// TODO: Handle failure
			}
		});

	}

}
