package com.lyricoo.activity;

import java.util.ArrayList;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.lyricoo.Message;
import com.lyricoo.R;



import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class MessageListAdapter extends BaseAdapter {

	private ArrayList<Message> mMessages;
	private Context mContext;

	public MessageListAdapter(Context context, ArrayList<Message> messages) {
		mMessages = messages;
		mContext = context;
	}

	@Override
	public int getCount() {
		return mMessages.size();
	}

	@Override
	public Object getItem(int arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public long getItemId(int arg0) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		LayoutInflater inflater = (LayoutInflater) mContext
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View rowView = inflater.inflate(R.layout.message_list_item, parent, false);
		
		Message msg = mMessages.get(position);
		
		TextView contactName = (TextView) rowView.findViewById(R.id.message_contact_name);
		TextView content = (TextView) rowView.findViewById(R.id.message_content_preview);
		
		contactName.setText(msg.getContactId());
		content.setText(msg.getContent());		
		
		return rowView;
	}

}
