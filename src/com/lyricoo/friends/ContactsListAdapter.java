package com.lyricoo.friends;

import java.util.ArrayList;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.lyricoo.R;

public class ContactsListAdapter extends ArrayAdapter<ContactsListViewEntry> {

	public ContactsListAdapter(Context context, int resource,
			ArrayList<ContactsListViewEntry> contacts) {
		super(context, resource, contacts);
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		LayoutInflater inflater = (LayoutInflater) getContext()
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

		View rowView = inflater.inflate(R.layout.contacts_list_item, parent,
				false);

		ContactsListViewEntry entry = getItem(position);

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
