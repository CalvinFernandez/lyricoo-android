package com.lyricoo;

import java.util.ArrayList;

import android.annotation.SuppressLint;
import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Build;
import android.provider.ContactsContract;
import android.provider.ContactsContract.Contacts;
import android.view.View;
import android.widget.ProgressBar;

/**
 * Asynchronously gets all contacts on the phone. Returns a list
 * of contact names and all phone numbers and email addresses for each 
 * contact. Use - new RetrieveContactsAsync(context, new OnTaskCompleted).execute();
 * 
 * OnTaskCompleted implements one function, onTaskCompleted, that will return 
 * an ArrayList<PhoneContact> as a result when the task is done.
 *
 */

public class RetrieveContactsAsync extends
		AsyncTask<Void, Integer, ArrayList<PhoneContact>>{
	
	private Context mContext;
	private OnTaskCompleted mListener;

	// In Android 3.0 (API version 11) and later, the Name column
	// is Contacts.DISPLAY_NAME_PRIMARY; in versions previous to that,
	// its name is Contacts.DISPLAY_NAME.
	@SuppressLint("InlinedApi")
	private String NAME_INDEX = Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB ? Contacts.DISPLAY_NAME_PRIMARY
			: Contacts.DISPLAY_NAME;

	public RetrieveContactsAsync(Context context, OnTaskCompleted listener) {
		mContext = context;
		mListener = listener;
	}
	
	protected void onPostExecute(ArrayList<PhoneContact> result){
		mListener.onTaskCompleted(result);
	}

	@Override
	protected ArrayList<PhoneContact> doInBackground(Void... params) {
		return getContacts(mContext);
	}

	private ArrayList<PhoneContact> getContacts(Context context) {
		ArrayList<PhoneContact> result = new ArrayList<PhoneContact>();
		// retrieve all contacts
		ContentResolver cr = context.getContentResolver();
		Cursor cursor = cr.query(ContactsContract.Contacts.CONTENT_URI, null,
				null, null, null);

		// iterate through the contacts
		while (cursor.moveToNext()) {
			String id, name, email, phone;

			// get the contact name and id
			id = cursor.getString(cursor
					.getColumnIndex(ContactsContract.Contacts._ID));
			name = cursor.getString(cursor.getColumnIndex(NAME_INDEX));

			// create a new PhoneContact to hold this contact's information
			PhoneContact contact = new PhoneContact(id, name);

			// get all phone numbers associated with this contact.
			// HAS_PHONE_NUMBER will be 1 if the contact has at least 1 number.
			// 0 otherwise
			if (Integer
					.parseInt(cursor.getString(cursor
							.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER))) > 0)

			{
				// Only retreive the phone numbers, we don't need the types or
				// labels
				String[] projection = { ContactsContract.CommonDataKinds.Phone.NUMBER };

				Cursor phoneCursor = cr.query(
						ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
						projection,
						ContactsContract.CommonDataKinds.Phone.CONTACT_ID
								+ " = ?", new String[] { id }, null);

				// get every number in the cursor results
				while (phoneCursor.moveToNext()) {
					// we only have one column since the projection only gets the address
					// so just grab the first column
					phone = phoneCursor.getString(0);

					contact.addNumber(phone);
				}

				phoneCursor.close();
			}

			// get email addresses

			// Only retreive the addresses, we don't need the types or
			// labels
			String[] projection = { ContactsContract.CommonDataKinds.Email.ADDRESS };

			Cursor emailCursor = cr.query(
					ContactsContract.CommonDataKinds.Email.CONTENT_URI,
					projection,
					ContactsContract.CommonDataKinds.Email.CONTACT_ID + " = ?",
					new String[] { id }, null);

			while (emailCursor.moveToNext()) {
				// we only have one column since the projection only gets the address
				// so just grab the first column
				email = emailCursor.getString(0);

				contact.addEmail(email);
			}

			emailCursor.close();

			// add contact to result list
			result.add(contact);
		}
		return result;
	}
}
