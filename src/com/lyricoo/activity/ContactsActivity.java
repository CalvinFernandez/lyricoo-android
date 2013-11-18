package com.lyricoo.activity;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.lyricoo.ContactsListViewEntry;
import com.lyricoo.OnTaskCompleted;
import com.lyricoo.PhoneContact;

import com.lyricoo.R;
import com.lyricoo.RetrieveContactsAsync;
import com.lyricoo.Session;
import com.lyricoo.User;
import com.lyricoo.Utility;

public class ContactsActivity extends Activity {
	private ProgressBar mProgress;
	// contacts list from our phone
	private ArrayList<PhoneContact> mPhoneContacts;
	// Sorted and list of contacts ready to be displayed
	private ArrayList<ContactsListViewEntry> mContacts;

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
		User.REST.get(new JsonHttpResponseHandler() {
			@Override
			public void onSuccess(JSONArray json) {
				// parse json into users
				ArrayList<User> lyricooUsers = User.parseUserJsonArray(json);

				// initialize list
				mContacts = new ArrayList<ContactsListViewEntry>();

				for (PhoneContact contact : mPhoneContacts) {
					// check if our contact already has an account
					User user = findUser(lyricooUsers, contact);

					// sort the contact into the corresponding list based on the
					// result
					if (user != null) {
						mContacts.add(new ContactsListViewEntry(contact
								.getName(), null, user.getUsername()));
					} else {
						// if they have more than one phone number create a
						// separate entry for each number. This makes the
						// display simpler and places the burden of choosing
						// which number to send the invite to on the user
						for (String number : contact.getNumbers()) {
							mContacts.add(new ContactsListViewEntry(contact
									.getName(), number, null));
						}
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
		ContactsListAdapter adapter = new ContactsListAdapter(this, mContacts);
		ListView list = (ListView) findViewById(R.id.contacts_list);
		list.setAdapter(adapter);
	
		// When a contact is clicked we can do something
		list.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				ContactsListViewEntry contact = mContacts.get(position);
				String number = contact.getNumber();
				String username = contact.getUsername();
				// if it is an existing user then add them as a friend
				if(username != null){
					addFriend(username);
					// TODO: Show visual cue in list that friend can/has been added
				}
				
				// if the contact doesn't have an account send them an invite
				else if(number != null){
					inviteFriend(number);
				}
			}
		});
	}

	// send an invite to a phone number
	// TODO: make this work
	private void inviteFriend(String number) {
		String inviteText = "Add me on Lyricoo! Username: " + Session.currentUser().getUsername() + " www.lyricoo.com/download";

		Intent smsIntent = new Intent(Intent.ACTION_VIEW);

		smsIntent.putExtra("sms_body", inviteText);
		smsIntent.putExtra("address", number);
		smsIntent.setType("vnd.android-dir/mms-sms");

		startActivity(smsIntent);
	}
	
	// called when the user presses the add button
	public void addFriendClicked(View v){
		// Retrieve the entered text and remove white space
		EditText userText = (EditText) findViewById(R.id.username_field);
		final String username = userText.getText().toString().trim();

		// If the username is empty don't try to add it as a friend
		if (username.length() == 0)
			return;
		else {
			addFriend(username);
		}
	}

	// Add the friend with the given username
	public void addFriend(final String username) {
		// disable button so it isn't pressed again
		setAddButtonState(false);

		// send post request to server
		RequestParams params = new RequestParams();
		params.put("username", username);
		
		Session.currentUser().post("friends", params, new JsonHttpResponseHandler() {

			@Override
			public void onSuccess(JSONObject json) {
				toastAddFriendResult(username, true);
			}	
			
			@Override
			public void onSuccess(JSONArray json) {
				toastAddFriendResult(username, true);
			}	


			
			@Override
			public void onFailure(Throwable error, JSONArray json) {
				// Could have failed because friend doesn't exist, friend was
				// already added, or a connection problem
				// TODO: Make the failure notice more specific
				toastAddFriendResult(username, false);
			}
			
			@Override
			public void onFailure(Throwable error, JSONObject json) {
				// Could have failed because friend doesn't exist, friend was
				// already added, or a connection problem
				// TODO: Make the failure notice more specific
				toastAddFriendResult(username, false);
			}

			
			
			@Override
			public void onFinish() {
				// re-enable button when process is over
				setAddButtonState(true);
			}
		});
	}

	/**
	 * Raise a toast letting the user know the result of the add friend request
	 * 
	 */
	private void toastAddFriendResult(String username, boolean success) {
		CharSequence text;
		if (success) {
			text = "Added " + username + " to friends!";
		} else {
			text = "Failed adding " + username + " to friends";
		}

		Context context = getApplicationContext();
		int duration = Toast.LENGTH_SHORT;

		Toast toast = Toast.makeText(context, text, duration);
		toast.show();
	}

	/**
	 * Enables or disables the button to add a friend
	 * 
	 * @param enabled
	 */
	private void setAddButtonState(boolean enabled) {
		Button button = (Button) findViewById(R.id.add_friend_button);
		button.setEnabled(enabled);
	}

}
