package com.lyricoo.activity;

import java.util.ArrayList;

import com.lyricoo.ContactsListViewEntry;
import com.lyricoo.Conversation;
import com.lyricoo.Message;
import com.lyricoo.PhoneContact;
import com.lyricoo.R;
import com.lyricoo.User;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class ContactsListAdapter extends BaseAdapter {
	private ArrayList<ContactsListViewEntry> mContacts;
	private Context mContext;

	public ContactsListAdapter(Context context,
			ArrayList<ContactsListViewEntry> contacts) {
		mContacts = contacts;
		mContext = context;
	}

	@Override
	public int getCount() {
		// two lists of data plus a header for each list (2 headers total)
		return mContacts.size();
	}

	@Override
	public Object getItem(int position) {
		return mContacts.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		LayoutInflater inflater = (LayoutInflater) mContext
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

		View rowView = inflater.inflate(R.layout.contacts_list_item, parent,
				false);

		ContactsListViewEntry entry = mContacts.get(position);

		// Set the name
		TextView name = (TextView) rowView.findViewById(R.id.contact_name);
		name.setText(entry.getName());

		// set the username if they have a lyricoo account or their number
		// otherwise so we can invite them
		TextView info = (TextView) rowView.findViewById(R.id.contact_info);
		String username = entry.getUsername();
		String number = entry.getNumber();

		if (username != null) {
			info.setText(username);
		} else if (number != null) {
			info.setText(number);
		} else {
			// Why don't we have their name or number? Something went
			// terribly wrong...
		}

		return rowView;
	}

}
