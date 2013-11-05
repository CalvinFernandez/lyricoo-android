package com.lyricoo.activity;

import java.util.ArrayList;

import com.lyricoo.Conversation;
import com.lyricoo.Message;
import com.lyricoo.PhoneContact;
import com.lyricoo.R;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class ContactsListAdapter extends BaseAdapter {
	private ArrayList<PhoneContact> mContacts;
	private Context mContext;

	public ContactsListAdapter(Context context, ArrayList<PhoneContact> contacts) {
		mContacts = contacts;
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
		
		// email
		if(!contact.getEmails().isEmpty()){
			// only show first one for now
			String email = contact.getEmails().get(0);
			TextView emailView = (TextView) rowView
					.findViewById(R.id.contact_email);
			emailView.setText(email);
		}
		
		
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
