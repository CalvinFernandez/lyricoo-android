package com.lyricoo.activity;

import java.util.ArrayList;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.lyricoo.Conversation;
import com.lyricoo.Message;
import com.lyricoo.R;

import com.lyricoo.User;


public class FriendsListAdapter extends BaseAdapter {

	private ArrayList<User> mFriends;
	private Context mContext;

	public FriendsListAdapter(Context context, ArrayList<User> friends) {
		mFriends= friends;
		mContext = context;
	}

	@Override
	public int getCount() {
		return mFriends.size();
	}

	@Override
	public Object getItem(int position) {
		return mFriends.get(position);
	}

	@Override
	public long getItemId(int position) {
		// shouldn't apply to this situation since we don't have item ids
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		LayoutInflater inflater = (LayoutInflater) mContext
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View rowView = inflater.inflate(R.layout.friend_list_item, parent,
				false);

		TextView friendName = (TextView) rowView
				.findViewById(R.id.friend_name);


		// get the last message in the conversation
		User friend = mFriends.get(position);
		
		friendName.setText(friend.getUsername());

		return rowView;
	}

}