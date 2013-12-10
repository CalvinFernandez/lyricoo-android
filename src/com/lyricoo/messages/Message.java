package com.lyricoo.messages;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import org.json.JSONException;
import org.json.JSONObject;

import android.os.Bundle;

import com.loopj.android.http.RequestParams;
import com.lyricoo.Utility;
import com.lyricoo.api.LyricooApiResponseHandler;
import com.lyricoo.api.LyricooModel;
import com.lyricoo.music.Song;

/**
 * The message class represents a message that has been sent from one user to
 * another. Two copies are stored on the server, one belonging to the sender and
 * the other to the receiver. The userId is the owner of the message, and the
 * sent boolean tells you whether they are the sender or receiver. The optional
 * songId gives the song if one was sent.
 * 
 */

public class Message extends LyricooModel {
	// TODO: Since we are overriding equals() we should also override hashCode() or bugs could pop up
	private String mContent;
	// use integer because the message id can be null for locally created
	// messages
	private Integer mMessageId;
	
	private int mUserId; // this should reference Session id.
	private int mContactId;
	
	
	private boolean mSent;
	// default read to true
	private boolean mRead = true;
	
	// the Song included with this message. Null if none
	private Song mSong;
	private Date mTime;

	// format of the date that the server uses
	// TODO: Adjust message time for user's timezone
	private final String DATE_FORMAT = "yyyy-mm-dd'T'HH:mm:ss.SSS'Z'";
	
	private static String baseUrl = "messages";

	public Message(String content, int userId, int contactId, boolean sent,
			Song song, Date time, boolean read) {
		super();
		
		mContent = content;
		mUserId = userId;
		mContactId = contactId;
		mSent = sent;
		mSong = song;
		mTime = time;
		mRead = read;
	}

	/**
	 * Message constructor without read boolean 
	 * defaults to true. Use this if the message
	 * is being sent by the user.
	 * @param content
	 * @param userId
	 * @param contactId
	 * @param sent
	 * @param song
	 * @param time
	 */
	public Message(String content, int userId, int contactId, boolean sent,
			Song song, Date time) {
		this(content, userId, contactId, sent, song, time, true);
	}
	/**
	 * A new message with no date provided. Initialized to current time
	 * 
	 * @param content
	 *            Message content
	 * @param userId
	 *            The user that this message belongs to
	 * @param contactId
	 *            The contact the user is talking to
	 * @param sent
	 *            Whether or not the user sent or received this message
	 * @param song
	 *            The song attached to the message. Null if none
	 */
	public Message(String content, int userId, int contactId, boolean sent,
			Song song) {
		this(content, userId, contactId, sent, song, new Date());
	}

	/**
	 * No date or song provided, defaults to current time and song with value
	 * null (indicating no song selected for now .... )
	 * 
	 * @param content
	 * @param userId
	 * @param contactId
	 * @param sent
	 */
	public Message(String content, int userId, int contactId, boolean sent) {
		this(content, userId, contactId, sent, null, new Date());
	}

	public Message(JSONObject json) {
		super();
		setContent(json);
	}

	/**
	 * Build a message from an android bundle
	 * object
	 * @param bundle
	 * @throws MessageException 
	 */
	public Message(Bundle bundle) throws MessageException {
		if (	bundle.containsKey("user_id") &&
				bundle.containsKey("contact_id") &&
				bundle.containsKey("sent") &&
				bundle.containsKey("content") &&
				bundle.containsKey("read")) {
			
			mUserId = Integer.parseInt(bundle.getString("user_id"));
			mContactId = Integer.parseInt(bundle.getString("contact_id"));
			mSent = Boolean.getBoolean(bundle.getString("sent"));
			mContent = bundle.getString("content");
			mRead = Boolean.getBoolean(bundle.getString("read"));
			// TODO: Add Song and date to message
			
		} else {
			throw new MessageException("Malformed message");
		}
		
	}
	
	private void setContent(JSONObject json) {
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
			JSONObject song = json.getJSONObject("song");
			mSong = new Song(song);
		} catch (JSONException e) {

		}

		// Parse date string into usable java format
		try {
			String createdAt = json.getString("created_at");
			mTime = new SimpleDateFormat(DATE_FORMAT, Locale.US)
					.parse(createdAt);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		try {
			mRead = json.getBoolean("read");
		} catch (JSONException e) {
			Utility.log("error setting read");
		}
		
		setBaseUrl(baseUrl + "/" + mMessageId);
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
	
	public boolean isRead() {
		return mRead;
	}
	
	public boolean isUnread() {
		return !mRead;
	}

	public void read() {
		mRead = true;
	}
	
	public Song getSong() {
		return mSong;
	}

	public Date getTime() {
		return mTime;
	}

	public Integer getMessageId() {
		return mMessageId;
	}
	

	public boolean getIsSent() {
		return mSent;
	}

	/**
	 * Check if two messages are equal.
	 * 
	 * @param msg An object to check for equality
	 * @return True if the messages are equal and false otherwise
	 */
	@Override
	public boolean equals(Object object) {
		// if the object is null they are not equal by default
		if (object == null){
			return false;
		}
		
		if (object == this) {
			return true;
		}
		
		if (!(object instanceof Message)) {
			return false;
		}
		
		Message msg = (Message) object;

		// if the message ids are equal the messages are equal
		if (msg.getMessageId() == mMessageId){
			return true;
		}

		// if at least one of the messages has an id return false
		if (msg.getMessageId() != null || mMessageId != null){
			return false;
		}

		// if both of the messages don't have an id we can check all the other
		// parameters

		// check if user and contact are the same
		if (msg.getContactId() != mContactId){
			return false;
		}
		if (msg.getUserId() != mUserId){
			return false;
		}

		// Use the Boolean class to check if the two boolean sent values are the
		// same.
		// Returns 0 if they are the same
		if (Boolean.valueOf(msg.getIsSent()).compareTo(mSent) != 0){
			return false;
		}

		// Check that message content is equal
		if (!msg.getContent().equals(mContent)){
			return false;
		}

		// Check that songs are equal
		int id1 = mSong == null ? -1 : mSong.getId();
		int id2 = msg.getSong() == null ? -1 : msg.getSong().getId();
		if (id1 != id2){
			return false;
		}

		// if we got through all the checks then the messages must be equal
		return true;		
	}
	
	/**
	 * Updates the message with new json data
	 * @param json
	 */
	public void update(JSONObject json) {
		setContent(json);
	}
	
	/**
	 * Converts an entire message into request parameters
	 * @return
	 */
	public RequestParams parameterize() {
		RequestParams params = new RequestParams();
		
		if (mContent != null) {
			params.put("content", mContent);
		}
		params.put("user_id", Integer.toString(mUserId));
		params.put("contact_id", Integer.toString(mContactId));
		params.put("read", Boolean.toString(mRead));
		params.put("sent", Boolean.toString(mSent));
		
		if (mMessageId != null) {
			params.put("id", Integer.toString(mMessageId));
		}
		
		if (mSong != null) {
			params.put("song_id", Integer.toString(mSong.getId()));
		}
		
		return params;
	}
	

	/**
	 * Custom put methods for updating a message
	 * Use this if you want to update the message
	 * content.
	 * @param responseHandler
	 */
	public void put(LyricooApiResponseHandler responseHandler) {
		put(parameterize(), responseHandler);
	}
	
	public void put() {
		put(new LyricooApiResponseHandler());
	}
	
	
}
