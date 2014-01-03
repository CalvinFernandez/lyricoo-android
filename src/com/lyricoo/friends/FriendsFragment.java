package com.lyricoo.friends;

import java.util.ArrayList;

import se.emilsjolander.stickylistheaders.StickyListHeadersListView;

import com.lyricoo.LyricooActivity;
import com.lyricoo.R;
import com.lyricoo.Utility;
import com.lyricoo.api.LyricooApiResponseHandler;
import com.lyricoo.session.Session;
import com.lyricoo.session.User;
import com.lyricoo.ui.SlidingMenuHelper;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;

public class FriendsFragment extends Fragment {
	private ArrayList<User> mFriends;
	private StickyListHeadersListView mList;
	private Context mContext;
	
	private void loadFriendsList() {

		mFriends = Session.getFriendManager().getFriends();

		// Create adapter for the list view
		FriendsListAdapter adapter = new FriendsListAdapter(mContext,
				R.layout.friend_list_item, mFriends);

		mList.setAdapter(adapter);

		// on long click show a list of options to the user
		mList.setOnItemLongClickListener(new OnItemLongClickListener() {

			@Override
			public boolean onItemLongClick(AdapterView<?> parent, View view,
					int position, long id) {
				User friend = mFriends.get(position);
				//showOptions(friend);

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
				//loadConversation(friend);
			}

		});

	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		
		View friendsView = inflater.inflate(R.layout.friends_fragment, container, false);
		// get list view
		mList = (StickyListHeadersListView) friendsView.findViewById(R.id.friends_list);
		mContext = getActivity();
		
		Session.getFriendManager().getFriends(new LyricooApiResponseHandler() {
			@Override
			public void onSuccess(Object responseJson) {
				loadFriendsList();
			}

			@Override
			public void onFailure(int statusCode, String responseBody,
					Throwable error) {
				//String toast = "Error retrieving friends";
				//Utility.makeBasicToast(mContext, toast);
			}
		}, true);
		
		
		return friendsView;
	}
}
