package com.lyricoo;

import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;

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
	
	private boolean read = true;
	
	public Conversation(ArrayList<Message> messages, User contact) {
		this.mMessages = messages;
		mContact = contact;
	}	
	
	/**
	 * Create a blank conversation with this contact
	 * @param contact
	 */
	public Conversation(User contact){
		this(new ArrayList<Message>(), contact);
	}
	
	public Conversation() {
		this(new ArrayList<Message>(), new User());
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
	public Message buildNewMessage(String content, Song song, boolean sent, Date time) {
		Message m = new Message(content, mUserId, mContact.getUserId(), sent, song, time);
		//mMessages.add(m);
		return m;
	}
	
	/**
	 * Add a message to 
	 * @param message
	 */
	public void add(Message message){
		mMessages.add(message);
	}
	
	
	/**
	 * Remove the given if message from the conversation. If no matching message is found nothing is done
	 * @param message
	 */
	public void remove(Message message){
		mMessages.remove(message);
	}
	
	public static ArrayList<Conversation> parseMessagesJson(JSONObject json) {
		ArrayList<Conversation> conversations = new ArrayList<Conversation>();
		
		Iterator<?> keys = json.keys();
		
		while (keys.hasNext()) {
			String key = (String)keys.next();
			JSONObject conversationObj, contactJson;
			JSONArray conversation;
			
			try {
				conversationObj = json.getJSONObject(key);
				contactJson = conversationObj.getJSONObject("contact");
				conversation = conversationObj.getJSONArray("conversation");
				
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
				
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}	
		}
		return conversations;
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
	
	/**
	 * Returns true if conversation contains 
	 * unread messages.
	 * Returns false if conversation contains no
	 * unread messages
	 * @return true | false
	 */
	public boolean hasUnread() {
		for (Message message : mMessages) {
			if (message.isUnread()) {
				return true;
			}
		}
		return false;
	}
	
	/**
	 * Updates all unread 
	 * messages to the 'read' state
	 */
	public void read() {
		for (Message message : mMessages) {
			if (message.isUnread()) {
				message.read();
				message.put();
			}
		}
	}
}
