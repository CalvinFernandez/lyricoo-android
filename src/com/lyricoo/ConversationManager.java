package com.lyricoo;

import java.util.ArrayList;

/**
 * This class manages all of a user's conversations with other users.
 * 
 */
public class ConversationManager {
	private User mUser;

	/**
	 * Initialize a ConversationManager to manage a particular user's messages
	 * 
	 * @param user
	 *            The Lyricoo User who's messages we are managing
	 */
	public ConversationManager(User user) {
		mUser = user;

		// could call sync() automatically here and then offer a callback, or
		// force manual call to sync()
	}

	/**
	 * Get all conversations the user is involved in
	 * 
	 * @return
	 */
	public ArrayList<Conversation> getConversations() {
		// Need to decide how this local data is kept consistent with server.
		// Possible options - 1. Make user call sync() before calling a method
		// like this. 2. Have callback for onSuccess and onFailure like in the
		// Friends abstraction

		// Do we pass a deep or shallow copy of the conversations? Shallow is
		// easier, and might be nice because if we add a message to our copy it
		// will be reflected in all other copies. However, this also allows changes
		// to other copies to affect our data as well.
		return null;
	}

	/**
	 * Get the conversation with the given contact
	 * 
	 * @param contact
	 *            The user that our conversation is with
	 * @return A Conversation containing all messages with this contact. Will be
	 *         empty if no messages have been sent or received
	 */
	public Conversation getConversation(User contact) {
		// same issues as with getConversations
		return null;
	}

	/**
	 * Send a message to a user
	 * 
	 * @param message
	 *            The message to add to the conversation
	 * @param contact
	 *            The user that the conversation is with
	 */
	public void sendMessage(Message message, User contact) {
		// add the message to the local conversation

		// send a post request to server
		// update local message with message id that server creates

		// handle failure? Could retry automatically or simply delete the local
		// copy and tell the user to try again

		// Could register a callback for this specific method, or could leave it
		// to the generic onDataChangedListener
	}

	public void receiveMessage(Message message, User contact) {
		// Add a message that a contact sent to us. Is this necessary?

		// Two ways to handle messages sent to us. 1. GCM receives the message
		// json and passes a new Message to this function. 2. GCM simply tells
		// us that a new message is waiting on the server and leave us to
		// retrieve it ourselves.

		// I'm partial to 2, it makes receiveMessage unnecessary and guarantees
		// that we are properly synced
	}

	/**
	 * Force the ConversationManager to sync local data with server
	 */
	public void sync() {
		// This might be nice to allow activities to refresh data when they want
		// to. The other option is remove this method and add a "refresh"
		// boolean parameter to methods liked getConversation() that lets us
		// choose whether we want to force a
		// server sync

		// could accept a callback for onSuccess and onFailure
	}

	/**
	 * Whether or not data has yet been synced with the server
	 * 
	 * @return
	 */
	public boolean isSynced() {
		// if we implement sync() we might want a method like this to tell us if
		// data has been synced yet

		// could also make the return type Date and have it say the last time a
		// sync happened
		return false;
	}

	/**
	 * Register a callback that alerts you whenever our local dataset changed
	 * 
	 * @param listener
	 */
	public void setOnDataChangedListener(OnDataChangedListener listener) {

	}

	/**
	 * A callback for when any of the local data is updated
	 * 
	 */
	public interface OnDataChangedListener {
		void dataUpdated();
	}

}
