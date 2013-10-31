package com.lyricoo;

import java.text.DateFormat;
import java.util.Date;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * The message class represents a message that has been sent from one user to another.
 * Two copies are stored on the server, one belonging to the sender and the other to 
 * the receiver. The userId is the owner of the message, and the sent boolean
 * tells you whether they are the sender or receiver. The optional songId
 * gives the song if one was sent.
 *
 */


public class Message {
	private String mContent;
	private int mUserId;
	private int mContactId;
	private boolean mSent;
	// songId can be null if no song was sent in this message
	private Integer mSongId;
	private Date mTime;

	public Message(String content, int userId, int contactId, boolean sent,
			int songId, Date time) {
		mContent = content;
		mUserId = userId;
		mContactId = contactId;
		mSent = sent;
		mSongId = songId;
		mTime = time;
	}

	public Message(JSONObject json) {	
		// TODO: Make sure these json keys match what the server uses
		// TODO: Handle exceptions on json parsing
		try {
			mContent = json.getString("content");
		} catch (JSONException e1) {
			
		}
		
		try {
			mUserId = json.getInt("user_id");
		} catch (JSONException e1) {

		}
		
		try {
			mContactId = json.getInt("contactId");
		} catch (JSONException e1) {

		}
		
		try {
			mSent = json.getBoolean("sent");
		} catch (JSONException e) {

		}
		
		try {
			mSongId = json.getInt("songId");
		} catch (JSONException e) {
			
		}
		
		// TODO: Parse date string into usable java format
		//mTime = json.getString("created_at");
	}

	public String getContent() {
		return mContent;
	}

	public int getUserId() {
		return mUserId;
	}

	public int getContactId() {
		return mContactId;
	}

	public boolean isSent() {
		return mSent;
	}

	public int getSongId() {
		return mSongId;
	}

	public Date getTime() {
		return mTime;
	}

}
