package com.lyricoo.friends;

import java.util.ArrayList;

/**
 * This class incorporates the relevant information in the phone's contact list
 * for a single contact
 * 
 */
public class PhoneContact {
	private String mId;
	private String mName;
	// the contact can have multiple email addresses
	// and phone numbers of different types. We don't worry
	// about the type
	private ArrayList<String> mEmails;
	private ArrayList<String> mNumbers;
	
	public PhoneContact(String id, String name){
		mId = id;
		mName = name;
		mEmails = new ArrayList<String>();
		mNumbers = new ArrayList<String>();
	}
	
	public void addEmail(String email){
		mEmails.add(email);
	}
	
	public void addNumber(String number){
		mNumbers.add(number);
	}

	public String getId() {
		return mId;
	}

	public String getName() {
		return mName;
	}

	public ArrayList<String> getEmails() {
		return mEmails;
	}

	public ArrayList<String> getNumbers() {
		return mNumbers;
	}

}
