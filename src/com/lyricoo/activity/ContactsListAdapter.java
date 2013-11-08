package com.lyricoo.activity;

import java.util.ArrayList;

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
	// phone contacts that we can invite
	private ArrayList<PhoneContact> mContacts;
	// existing lyricoo users we can invite that match contacts on our phone
	private ArrayList<User> mFriends;
	private Context mContext;

	public ContactsListAdapter(Context context, ArrayList<PhoneContact> contacts, ArrayList<User> friends) {
		// A PhoneContact can have multiple phone numbers. To simplify things we will list each number as it's own entry
		mContacts = contacts;
		mFriends = friends;		
		mContext = context;
	}
	

	@Override
	public int getCount() {
		return mContacts.size();
	}

	@Override
	public Object getItem(int position) {
		return mContacts.get(position);
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		LayoutInflater inflater = (LayoutInflater) mContext
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		
		// get the contact for this position
		PhoneContact contact = mContacts.get(position);

		View rowView = inflater.inflate(R.layout.contacts_list_item, parent,
					false);

		// Set the contact name
		TextView name = (TextView) rowView
				.findViewById(R.id.contact_name);
		name.setText(contact.getName());		
		
		// phone
		if(!contact.getNumbers().isEmpty()){
			// only show first one for now
			String number = contact.getNumbers().get(0);
			TextView numberView = (TextView) rowView
					.findViewById(R.id.contact_number);
			numberView.setText(number);
		}
		
		return rowView;
	}

}
