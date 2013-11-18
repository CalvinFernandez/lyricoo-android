package com.lyricoo.activity;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.lyricoo.Conversation;
import com.lyricoo.LyricooApp;

import com.lyricoo.R;
import com.lyricoo.Session;
import com.lyricoo.User;
import com.lyricoo.Utility;

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
	private ListView mList;
	private Context mContext;
	private LyricooApp mApp;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_friends);

		mContext = this;
		mApp = (LyricooApp) getApplication();

		// get progress bar and list view to use
		mProgress = (ProgressBar) findViewById(R.id.friends_loading_progress);
		mList = (ListView) findViewById(R.id.friends_list);

	}

	@Override
	protected void onResume() {
		super.onResume();

		// on resume refresh friends list to make sure it is current
		// TODO: Provide manual refresh button? Not efficient to make server
		// call every time the activity is resumed but it guarantees updated
		// results
		loadFriendsList();
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
		builder.create().show();
	}

	// Remove the given friend from the user's friend list
	private void removeFriend(final User friend) {
		// alert user that friend is being removed
		String msg = "Removing " + friend.getUsername() + " from friends";
		Utility.makeBasicToast(getApplicationContext(), msg);

		Session.currentUser().delete("friends/" + friend.getUserId(),
				new JsonHttpResponseHandler() {

					@Override
					public void onSuccess(JSONObject json) {
						String msg = friend.getUsername()
								+ " removed from friends";
						Utility.makeBasicToast(getApplicationContext(), msg);
					}

					@Override
					public void onSuccess(JSONArray json) {
						String msg = friend.getUsername()
								+ " removed from friends";
						Utility.makeBasicToast(getApplicationContext(), msg);
					}

					@Override
					public void onFailure(Throwable error, JSONObject json) {
						Utility.log("on failure");
						String msg = "Error removing friend";
						Utility.makeBasicToast(getApplicationContext(), msg);
					}

					@Override
					public void onFinish() {
						// reload the updated friend list
						// TODO: Get server to return updated friends list so we
						// don't
						// have to make another request
						loadFriendsList();
					}

				});
	}

	// Start an activity where the user can add friends to their list
	public void addFriendClicked(View v) {
		Intent i = new Intent(this, ContactsActivity.class);
		startActivity(i);
	}

	// Load the conversation with this friend
	private void loadConversation(final User friend) {
		RequestParams params = new RequestParams();
		params.put("contact_id", Integer.toString(friend.getUserId()));
		// TODO: Show loading dialog
		Session.currentUser().get("messages", params,
				new JsonHttpResponseHandler() {
					@Override
					public void onSuccess(JSONObject json) {

						ArrayList<Conversation> conversations = Conversation
								.parseMessagesJson(json);

						// should be only one conversation in the list, but if
						// no messages have yet been sent to this friend then it
						// will be empty
						Conversation conversation;
						if (conversations.isEmpty()) {
							conversation = new Conversation(friend);
						} else {
							conversation = conversations.get(0);
						}

						// convert to json to make it easy to pass to the
						// conversation activity
						String conversationAsJson = Utility
								.toJson(conversation);

						Intent i = new Intent(mContext,
								ConversationActivity.class);
						i.putExtra("conversation", conversationAsJson);
						startActivity(i);
					}

					@Override
					public void onFailure(Throwable error, JSONObject json) {
						// TODO: Handle failure
					}
				});

	}

	private void loadFriendsList() {
		// hide list while it loads and show progress bar to indicate loading
		mList.setVisibility(View.GONE);
		mProgress.setVisibility(View.VISIBLE);

		// load friends
		Session.currentUser().get("friends", new JsonHttpResponseHandler() {
			@Override
			public void onSuccess(JSONArray json) {
				// parse json into messages
				mFriends = User.parseUserJsonArray(json);

				// hide progress bar and show list
				mList.setVisibility(View.VISIBLE);
				mProgress.setVisibility(View.GONE);

				// Create adapter for the list view
				FriendsListAdapter adapter = new FriendsListAdapter(mContext,
						mFriends);

				mList.setAdapter(adapter);

				// on long click show a list of options to the user
				mList.setOnItemLongClickListener(new OnItemLongClickListener() {

					@Override
					public boolean onItemLongClick(AdapterView<?> parent,
							View view, int position, long id) {
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

			@Override
			public void onFailure(Throwable error, JSONObject json) {
				// TODO: Handle failure
			}
		});
	}

}
