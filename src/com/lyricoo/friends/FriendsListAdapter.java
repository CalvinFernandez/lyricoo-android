package com.lyricoo.friends;

import java.util.ArrayList;

import se.emilsjolander.stickylistheaders.StickyListHeadersAdapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.lyricoo.R;
import com.lyricoo.session.User;

public class FriendsListAdapter extends ArrayAdapter<User> implements
		StickyListHeadersAdapter {

	private LayoutInflater mInflater;

	public FriendsListAdapter(Context context, int resource,
			ArrayList<User> friends) {
		super(context, resource, friends);
		mInflater = LayoutInflater.from(context);
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder viewHolder;
		if (convertView == null) {
			convertView = mInflater.inflate(R.layout.friend_list_item, parent,
					false);

			viewHolder = new ViewHolder();
			viewHolder.text = (TextView) convertView
					.findViewById(R.id.friend_name);
			convertView.setTag(viewHolder);
		} else {
			viewHolder = (ViewHolder) convertView.getTag();
		}
		User friend = getItem(position);
		viewHolder.text.setText(friend.friendlyId());
		return convertView;
	}

	@Override
	public View getHeaderView(int position, View convertView, ViewGroup parent) {
		HeaderViewHolder holder;
		if (convertView == null) {
			holder = new HeaderViewHolder();
			convertView = mInflater.inflate(R.layout.friends_list_header,
					parent, false);
			holder.text = (TextView) convertView
					.findViewById(R.id.friends_list_header_text);
			convertView.setTag(holder);
		} else {
			holder = (HeaderViewHolder) convertView.getTag();
		}
		String headerText = ""
				+ getItem(position).friendlyId().subSequence(0, 1).charAt(0);
		holder.text.setText(headerText.toUpperCase());
		return convertView;
	}

	@Override
	public long getHeaderId(int position) {
		User friend = getItem(position);
		return friend.friendlyId().toLowerCase().subSequence(0, 1).charAt(0);
	}

	class HeaderViewHolder {
		TextView text;
	}

	class ViewHolder {
		TextView text;
	}
}