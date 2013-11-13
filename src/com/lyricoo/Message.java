package com.lyricoo;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * The message class represents a message that has been sent from one user to
 * another. Two copies are stored on the server, one belonging to the sender and
 * the other to the receiver. The userId is the owner of the message, and the
 * sent boolean tells you whether they are the sender or receiver. The optional
 * songId gives the song if one was sent.
 * 
 */

public class Message {
	private String mContent;
	private int mMessageId;
	private int mUserId;  // this should reference Session id.
	private int mContactId;
	private boolean mSent;
	// songId can be null if no song was sent in this message
	private Integer mSongId;
	private Date mTime;
	
	// format of the date that the server uses
	// TODO: Adjust message time for user's timezone
	private final String DATE_FORMAT = "yyyy-mm-dd'T'HH:mm:ss.SSS'Z'";

	public Message(String content, int userId, int contactId, boolean sent,
			int songId, Date time) {
		mContent = content;
		mUserId = userId;
		mContactId = contactId;
		mSent = sent;
		mSongId = songId;
		mTime = time;
	}
	
	//
	//	No date provided, defaults to current
	//
	public Message(String content, int userId, int contactId, boolean sent,
			int songId) {
		this(content, userId, contactId, sent, -1, new Date());
	}
	
	//
	//  No date or song provided, defaults to current time and song with value -1 
	//	(indicating no song selected for now .... )
	//
	public Message(String content, int userId, int contactId, boolean sent) {
		this(content, userId, contactId, sent, -1, new Date());
	}

	public Message(JSONObject json) {
		// TODO: Make sure these json keys match what the server uses
		// TODO: Handle exceptions on json parsing
		try {
			mMessageId = json.getInt("id");
		} catch (JSONException e1) {

		}

		try {
			mContent = json.getString("content");
		} catch (JSONException e1) {

		}

		try {
			mUserId = json.getInt("user_id");
		} catch (JSONException e1) {

		}

		try {
			mContactId = json.getInt("contact_id");
		} catch (JSONException e1) {

		}

		try {
			mSent = json.getBoolean("sent");
		} catch (JSONException e) {

		}

		try {
			mSongId = json.getInt("song_id");
		} catch (JSONException e) {

		}

		// Parse date string into usable java format
		try {
			String createdAt = json.getString("created_at");
			mTime = new SimpleDateFormat(DATE_FORMAT, Locale.US).parse(createdAt);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
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

	public int getMessageId() {
		return mMessageId;
	}

}
