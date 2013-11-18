package com.lyricoo;
 
/**
 * This class holds the information of one entry in the contacts ListView
 * 
 * 
 */
public class ContactsListViewEntry {
	private String mName;
	private String mNumber;
	private String mUsername;

	/**
	 * 
	 * @param name
	 * @param number
	 * @param username
	 */
	public ContactsListViewEntry(String name, String number, String username) {
		mName = name;
		mNumber = number;
		mUsername = username;
	}
	
	public String getUsername(){
		return mUsername;
	}

	public String getName() {
		return mName;
	}

	public String getNumber() {
		return mNumber;
	}
}
