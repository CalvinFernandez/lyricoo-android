package com.lyricoo;

import java.util.ArrayList;
import java.util.Date;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/** A Conversation is a collection of all the messages
 * between a user and one of their contacts
 *
 */
public class Conversation {
	private ArrayList<Message> mMessages;
	private User mContact;
	private int mUserId; // This should go away and reference the session id
						 // we don't want duplicate data
						 // Duplicate data 
	
	public Conversation(ArrayList<Message> messages, User contact) {
		this.mMessages = messages;
		mContact = contact;
	}	

	public int getUserId() {
		return mUserId;
	}
	
	public User getContact(){
		return mContact;
	}

	public ArrayList<Message> getMessages() {
		return mMessages;
	}
	
	public Message getMostRecentMessage(){
		return mMessages.get(mMessages.size() - 1);
	}
	
	//
	//  Creates a new Message and adds to mMessage array at the end.
	//	No default.
	//	Returns new message object
	//
	public Message buildNewMessage(String content, int songId, boolean sent, Date time) {
		Message m = new Message(content, mUserId, mContact.getUserId(), sent, songId, time);
		//mMessages.add(m);
		return m;
	}
	
	//
	//	Creates a new message and adds to mMessage array to the end. Defaults sent to true and 
	//	current date to now and song value to -1.
	//	Returns new message object
	//
	public Message buildNewMessage(String content) {
		Message m = new Message(content, mUserId, mContact.getUserId(), true);
		//mMessages.add(m);
		return m;
	}
	
	/** Takes a JSONArray from api/messages/all and returns a
	 * parsed array of Conversations
	 * @param json
	 * @return
	 */
	public static ArrayList<Conversation> parseMessagesJson(JSONArray json){
		ArrayList<Conversation> conversations = new ArrayList<Conversation>();
		int numContacts = json.length();
		for(int i = 0; i < numContacts; i++){
			JSONObject entry = null;
			JSONObject contactJson = null;
			JSONArray conversation = null;
			// TODO: Handle json exceptions
			
			try {
				entry = json.getJSONObject(i);
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			// Get contact info
			try {
				contactJson = entry.getJSONObject("contact");
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			try {
				conversation = entry.getJSONArray("conversation");
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			// parse all messages
			ArrayList<Message> messages = new ArrayList<Message>();
			int numMessages = conversation.length();
			for(int j = 0; j < numMessages; j++){
				JSONObject msg = null;
				
				try {
					msg = conversation.getJSONObject(j);
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
				messages.add(new Message(msg));				
			}
			
		
			User contact = new User(contactJson);
			conversations.add(new Conversation(messages, contact));		
		}
		
		return conversations;		
	}
}
