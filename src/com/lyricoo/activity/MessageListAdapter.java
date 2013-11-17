package com.lyricoo.activity;

import java.util.ArrayList;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.lyricoo.Conversation;
import com.lyricoo.Message;
import com.lyricoo.R;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class MessageListAdapter extends BaseAdapter {

	private ArrayList<Conversation> mConversations;
	private Context mContext;

	public MessageListAdapter(Context context, ArrayList<Conversation> messages) {
		mConversations = messages;
		mContext = context;
	}

	@Override
	public int getCount() {
		return mConversations.size();
	}

	@Override
	public Object getItem(int position) {
		return mConversations.get(position);
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
		View rowView = inflater.inflate(R.layout.message_list_item, parent,
				false);

		TextView contactName = (TextView) rowView
				.findViewById(R.id.message_contact_name);
		TextView content = (TextView) rowView
				.findViewById(R.id.message_content_preview);

		// get the last message in the conversation
		Conversation convo = mConversations.get(position);
		Message msg = convo.getMostRecentMessage();

		contactName.setText(convo.getContact().getUsername());
		content.setText(msg.getContent());

		return rowView;
	}

}
