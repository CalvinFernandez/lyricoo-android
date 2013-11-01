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

public class ConversationAdapter extends BaseAdapter {

	private Conversation mConversation;
	private Context mContext;

	public ConversationAdapter(Context context, Conversation convo) {
		mConversation = convo;
		mContext = context;
	}

	@Override
	public int getCount() {
		return mConversation.getMessages().size();
	}

	@Override
	public Object getItem(int position) {
		return mConversation.getMessages().get(position);
	}

	@Override
	public long getItemId(int position) {
		// return the message id
		return mConversation.getMessages().get(position).getMessageId();
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		LayoutInflater inflater = (LayoutInflater) mContext
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

		// get the message that corresponds to this position
		Message msg = mConversation.getMessages().get(position);

		// use a different view depending on whether the message was sent or
		// received
		View rowView;
		if (msg.isSent()) {
			rowView = inflater.inflate(R.layout.conversation_item_sent, parent,
					false);
		} else {
			rowView = inflater.inflate(R.layout.conversation_item_received, parent,
					false);
		}		

		// Set the message text
		TextView content = (TextView) rowView
				.findViewById(R.id.message_content);
		content.setText(msg.getContent());
		
		return rowView;
	}

}
