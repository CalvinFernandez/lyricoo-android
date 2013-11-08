package com.lyricoo.activity;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONObject;

import com.loopj.android.http.JsonHttpResponseHandler;
import com.lyricoo.Conversation;
import com.lyricoo.LyricooAPI;
import com.lyricoo.OnTaskCompleted;
import com.lyricoo.PhoneContact;
import com.lyricoo.R;
import com.lyricoo.RetrieveContactsAsync;
import com.lyricoo.User;

import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.provider.ContactsContract.Contacts;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.AdapterView.OnItemClickListener;

public class ContactsActivity extends Activity {
	private ProgressBar mProgress;
	// contacts list from our phone
	private ArrayList<PhoneContact> mPhoneContacts;
	// people from our contacts list with existing lyricoo accounts. We can add
	// them as friends
	private ArrayList<User> mExistingFriends;
	// contacts without accounts that we can invite
	private ArrayList<PhoneContact> mContactsToInvite;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_contacts);

		loadContacts();
	}

	private void loadContacts() {
		// progress bar to indicate loading
		mProgress = (ProgressBar) findViewById(R.id.contacts_loading_progress);

		// retrieve contacts in background
		new RetrieveContactsAsync(this, new OnTaskCompleted() {

			@Override
			public void onTaskCompleted(Object result) {
				// save result. We know that result is a list of contacts, so
				// just cast it and ignore the warning
				mPhoneContacts = (ArrayList<PhoneContact>) result;

				// see which contacts already have accounts and display them
				sortPhoneContacts();
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

	// Sort contacts into two lists depending on whether they already have
	// lyricoo accounts. Display the results.
	private void sortPhoneContacts() {
		// retrieve list of all lyricoo users to cross reference
		LyricooAPI.get("users", null, new JsonHttpResponseHandler() {
			@Override
			public void onSuccess(JSONArray json) {
				// parse json into users
				ArrayList<User> lyricooUsers = User.parseUserJson(json);

				// sort our contacts
				for (PhoneContact contact : mPhoneContacts) {
					// check if our contact already has an account
					User user = findUser(lyricooUsers, contact);

					// sort the contact into the corresponding list based on the
					// result
					if (user != null) {
						mExistingFriends.add(user);
					} else {
						mContactsToInvite.add(contact);
					}
				}

				// hide progress bar
				mProgress.setVisibility(View.GONE);

				// display the results
				populateContactsList();
			}

			@Override
			public void onFailure(Throwable error, JSONObject json) {
				// TODO: Handle failure
			}
		});
	}

	// Check if the given contact is in the list of users. Return the matching
	// user if one is found, and null otherwise
	private User findUser(ArrayList<User> userList, PhoneContact contact) {
		for (User lyricooUser : userList) {
			if (lyricooUser.isContactMatch(contact)) {
				return lyricooUser;
			}
		}
		return null;
	}

	private void populateContactsList() {
		// Create adapter for the list view
		ContactsListAdapter adapter = new ContactsListAdapter(this,
				mContactsToInvite, mExistingFriends);
		ListView list = (ListView) findViewById(R.id.contacts_list);
		list.setAdapter(adapter);

		// When a contact is clicked we can do something
		list.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				// if it is an existing user then add them as a friend
				//addFriend()
				
				// if the contact doesn't have an account send them an invite
				//inviteFriend()

			}
		});
	}

	// send an invite to a phone number
	// TODO: make this work
	private void inviteFriend() {
		String inviteText = "You're invited to join lyricoo";

		Intent smsIntent = new Intent(Intent.ACTION_VIEW);

		smsIntent.putExtra("sms_body", inviteText);
		smsIntent.putExtra("address", "0123456789");
		smsIntent.setType("vnd.android-dir/mms-sms");

		startActivity(smsIntent);
	}

}
