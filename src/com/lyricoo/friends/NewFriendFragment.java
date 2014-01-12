package com.lyricoo.friends;

import java.util.ArrayList;

import org.json.JSONArray;

import com.lyricoo.R;
import com.lyricoo.Utility;
import com.lyricoo.api.LyricooApiResponseHandler;
import com.lyricoo.friends.FriendsActivity.SearchListener;
import com.lyricoo.session.Session;
import com.lyricoo.session.User;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.AdapterView.OnItemClickListener;

public class NewFriendFragment extends Fragment {
	private View mNewFriendView;

	private ProgressBar mProgress;
	// contacts list from our phone
	private ArrayList<PhoneContact> mPhoneContacts;
	// Sorted and list of contacts ready to be displayed
	private ArrayList<ContactsListViewEntry> mContactsMaster;
	private ArrayList<ContactsListViewEntry> mContacts;
	private ContactsListAdapter mAdapter;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO: Turn off drawer slider
		mNewFriendView = inflater.inflate(R.layout.new_friend_fragment,
				container, false);
		loadContacts();
		
		((FriendsActivity)getActivity()).registerSearchListener(new SearchListener() {

			@Override
			public void onQueryTextChange(String text) {
				filter(text);	
			}

			@Override
			public void onQueryTextSubmit(String text) {
				// TODO Auto-generated method stub
				
			}
			
		});
		return mNewFriendView;
	}

	private void loadContacts() {
		// progress bar to indicate loading
		mProgress = (ProgressBar) mNewFriendView
				.findViewById(R.id.contacts_loading_progress);

		// retrieve contacts in background
		new RetrieveContactsAsync(getActivity(),
				new RetrieveContactsAsync.onRetrieveCompleted() {

					@Override
					public void onCompleted(ArrayList<PhoneContact> result) {
						// save result. We know that result is a list of
						// contacts, so
						// just cast it and ignore the warning
						mPhoneContacts = (ArrayList<PhoneContact>) result;

						// see which contacts already have accounts and display
						// them
						sortPhoneContacts();
					}
				}).execute();// run the task
	}

	// Sort contacts into two lists depending on whether they already have
	// lyricoo accounts. Display the results.
	private void sortPhoneContacts() {
		// retrieve list of all lyricoo users to cross reference
		User.REST.get(new LyricooApiResponseHandler() {
			@Override
			public void onSuccess(Object jsonObject) {
				// parse json into users
				ArrayList<User> lyricooUsers = User
						.parseUserJsonArray((JSONArray) jsonObject);

				// initialize list
				mContactsMaster = new ArrayList<ContactsListViewEntry>();

				for (PhoneContact contact : mPhoneContacts) {
					// check if our contact already has an account
					User user = findUser(lyricooUsers, contact);

					// sort the contact into the corresponding list based on the
					// result
					if (user != null) {
						mContactsMaster.add(new ContactsListViewEntry(contact
								.getName(), null, user.getUsername()));
					} else {
						// if they have more than one phone number create a
						// separate entry for each number. This makes the
						// display simpler and places the burden of choosing
						// which number to send the invite to on the user
						for (String number : contact.getNumbers()) {
							mContactsMaster.add(new ContactsListViewEntry(contact
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
			public void onFailure(int statusCode, String responseBody,
					Throwable error) {
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
		mContacts = (ArrayList<ContactsListViewEntry>) mContactsMaster.clone();
		
		mAdapter = new ContactsListAdapter(getActivity(),
				R.layout.contacts_list_item, mContacts);

		ListView list = (ListView) mNewFriendView
				.findViewById(R.id.contacts_list);
		list.setAdapter(mAdapter);

		// When a contact is clicked we can do something
		list.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				ContactsListViewEntry contact = mContacts.get(position);
				String number = contact.getNumber();
				String username = contact.getUsername();
				// if it is an existing user then add them as a friend
				if (username != null) {
					Session.getFriendManager().addFriend(username);
				}

				// if the contact doesn't have an account send them an invite
				else if (number != null) {
					inviteFriend(number);
				}
			}
		});
	}

	// send an invite to a phone number
	// TODO: make this work
	private void inviteFriend(String number) {
		String inviteText = "Add me on Lyricoo! Username: "
				+ Session.currentUser().getUsername()
				+ " www.lyricoo.com/download";

		Intent smsIntent = new Intent(Intent.ACTION_VIEW);

		smsIntent.putExtra("sms_body", inviteText);
		smsIntent.putExtra("address", number);
		smsIntent.setType("vnd.android-dir/mms-sms");

		startActivity(smsIntent);
	}

	public void filter(String text) {
		if (Utility.isStringBlank(text)) {
			mContacts = (ArrayList<ContactsListViewEntry>) mContactsMaster.clone();
		} else {
			ArrayList<ContactsListViewEntry> mContactsCopy = new ArrayList<ContactsListViewEntry>();
			for (ContactsListViewEntry userEntry : mContactsMaster) {
				if (userEntry.getName().toLowerCase()
						.contains(text.toLowerCase())) {
					mContactsCopy.add(userEntry);
				}
			}
			mContacts = mContactsCopy;
		}
		
		mContacts.add(new ContactsListViewEntry(text, "", text));
		
		mAdapter.clear();
		mAdapter.addAll(mContacts);
		mAdapter.notifyDataSetChanged();
	}
}
