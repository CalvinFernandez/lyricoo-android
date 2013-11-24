package com.lyricoo.activity;

import java.util.ArrayList;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.lyricoo.Conversation;
import com.lyricoo.Message;
import com.lyricoo.R;

import com.lyricoo.User;


public class FriendsListAdapter extends ArrayAdapter<User> {

	public FriendsListAdapter(Context context, int resource, ArrayList<User> friends) {
		super(context, resource, friends);
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);				
		
		View rowView = inflater.inflate(R.layout.friend_list_item, parent,
				false);

		TextView friendName = (TextView) rowView
				.findViewById(R.id.friend_name);
		
		User friend = getItem(position);
		friendName.setText(friend.getUsername());

		return rowView;
	}

}