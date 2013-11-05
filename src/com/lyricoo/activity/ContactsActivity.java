package com.lyricoo.activity;

import java.util.ArrayList;

import com.lyricoo.OnTaskCompleted;
import com.lyricoo.PhoneContact;
import com.lyricoo.R;
import com.lyricoo.RetrieveContactsAsync;

import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.provider.ContactsContract.Contacts;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.AdapterView.OnItemClickListener;

public class ContactsActivity extends Activity {
	private ArrayList<PhoneContact> mPhoneContacts;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_contacts);

		// progress bar to indicate loading
		final ProgressBar progressbar = (ProgressBar) findViewById(R.id.contacts_loading_progress);

		// retrieve contacts in background
		new RetrieveContactsAsync(this, new OnTaskCompleted() {

			@Override
			public void onTaskCompleted(Object result) {
				// save result. We know that result is a list of contacts, so
				// just cast it and ignore the warning
				mPhoneContacts = (ArrayList<PhoneContact>) result;

				// hide loading bar
				progressbar.setVisibility(View.GONE);

				// display contacts
				populateContactsList();
			}
		// run the task	
		}).execute();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.contacts, menu);
		return true;
	}

	private void populateContactsList() {
		// Create adapter for the list view
		ContactsListAdapter adapter = new ContactsListAdapter(this,
				mPhoneContacts);
		ListView list = (ListView) findViewById(R.id.contacts_list);
		list.setAdapter(adapter);

		// When a contact is clicked we can do something
		list.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {

			}
		});
	}

}
