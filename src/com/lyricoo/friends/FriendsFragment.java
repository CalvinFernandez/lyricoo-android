package com.lyricoo.friends;

import java.util.ArrayList;

import se.emilsjolander.stickylistheaders.StickyListHeadersListView;

import com.lyricoo.LyricooActivity;
import com.lyricoo.R;
import com.lyricoo.Utility;
import com.lyricoo.api.LyricooApiResponseHandler;
import com.lyricoo.messages.ConversationActivity;
import com.lyricoo.session.Session;
import com.lyricoo.session.User;
import com.lyricoo.ui.SlidingMenuHelper;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;

import android.app.AlertDialog;

public class FriendsFragment extends Fragment {
	private ArrayList<User> mFriends;
	private StickyListHeadersListView mList;
	private Context mContext;
	private FriendsListAdapter mAdapter;

	private void loadFriendsList() {

		/* We want to shallow copy the friends so that we can filter this list
		 * without changing the underlying data.
		 */
		mFriends = Session.getFriendManager().cloneFriends();

		// Create adapter for the list view
		mAdapter = new FriendsListAdapter(mContext,
				R.layout.friend_list_item, mFriends);

		mList.setAdapter(mAdapter);

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

	// shows options for interacting with the given friend
	// in a dialog popup
	private void showOptions(final User friend) {
		// the options to show in the dialog list
		String[] options = { "Delete" };
		// create a new dialog
		AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
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

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		View friendsView = inflater.inflate(R.layout.friends_fragment,
				container, false);
		// get list view
		mList = (StickyListHeadersListView) friendsView
				.findViewById(R.id.friends_list);
		mContext = getActivity();

		Session.getFriendManager().getFriends(new LyricooApiResponseHandler() {
			@Override
			public void onSuccess(Object responseJson) {
				loadFriendsList();
			}

			@Override
			public void onFailure(int statusCode, String responseBody,
					Throwable error) {
				// String toast = "Error retrieving friends";
				// Utility.makeBasicToast(mContext, toast);
			}
		}, true);

		return friendsView;
	}

	public void filter(String term) {
		mFriends = Session.getFriendManager().getFriends(term);
		mAdapter.clear();
		mAdapter.addAll(mFriends);
		mAdapter.notifyDataSetChanged();
	}
}
