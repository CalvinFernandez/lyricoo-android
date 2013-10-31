package com.lyricoo;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/** A Conversation is a collection of all the messages
 * between a user and one of their contacts
 *
 */
public class Conversation {
	private ArrayList<Message> mMessages;
	private int mUserId;
	private int mContactId;
	private String mContactName;
	
	public Conversation(ArrayList<Message> messages, int userId,
			int contactId, String contactName) {
		this.mMessages = messages;
		this.mUserId = userId;
		this.mContactId = contactId;
		this.mContactName = contactName;
	}
	

	public int getUserId() {
		return mUserId;
	}



	public int getContactId() {
		return mContactId;
	}



	public String getContactName() {
		return mContactName;
	}



	public ArrayList<Message> getMessages() {
		return mMessages;
	}
	
	/** Takes a JSONArray from api/messages/all and returns a
	 * parsed array of Conversations
	 * @param json
	 * @return
	 */
	public static ArrayList<Conversation> parseMessagesJson(JSONArray json){
		int numContacts = json.length();
		for(int i = 0; i < numContacts; i++){
			JSONObject entry = null;
			JSONObject contact = null;
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
				contact = entry.getJSONObject("contact");
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
			
			
			
		}
		return null;		
	}
}
