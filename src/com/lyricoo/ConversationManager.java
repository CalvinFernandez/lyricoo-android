package com.lyricoo;

import java.util.ArrayList;

/**
 * This class manages all of a user's conversations with other users.
 * 
 */
public class ConversationManager {
	private User mUser;
	private ArrayList<Conversation> mConversations;
	// Whether the last sync was successful or not
	private boolean mIsSynced;
	// callback for when local data is changed
	private OnDataChanged mOnDataChangedListener;

	/**
	 * Initialize a ConversationManager to manage a particular user's messages
	 * 
	 * @param user
	 *            The Lyricoo User who's messages we are managing
	 */
	public ConversationManager(User user) {
		// store the user whose messages we are managing
		mUser = user;

		// store the callback
		mOnDataChangedListener = listener;

		// Initialize an empty list of conversations
		mConversations = new ArrayList<Conversation>();

		// Do the initial sync to get up to date. We don't have to worry about
		// the callback
		sync(null);
	}

	/**
	 * Get all conversations the user is involved in
	 * 
	 * @return
	 */
	public ArrayList<Conversation> getConversations() {
		// Data is refreshed automatically in background, or manually with a
		// call to sync(). This method returns the latest available data

		// Pass a shallow copy of the data
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
		// same as getConversations
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
	public void sendMessage(Message message, User contact,
			OnMessageSent listener) {
		// add the message to the local conversation

		// send a post request to server
		// update local message with message id that server creates

		// Alert the user of failure or success
	}

	public interface OnMessageSent {
		public void onSuccess();

		public void onFailure();
	}

	/**
	 * Add a message to the local conversation that the contact sent to us.
	 * Should be received through GCM
	 * 
	 * @param message
	 * @param contact
	 */
	public void receiveMessage(Message message, User contact) {
		// Add message to the relevant conversation

		// Call onDataChanged Listener
	}

	/**
	 * Force the ConversationManager to sync local data with server
	 */
	public void sync(OnSyncCompleted listener) {

	}

	/**
	 * Callback methods for when sync() finishes
	 * 
	 */
	public interface OnSyncCompleted {
		public void onSuccess();

		public void onFailure(String errorMessage);
	}

	/**
	 * Whether or not the last sync was successful
	 * 
	 * @return
	 */
	public boolean isSynced() {
		return mIsSynced;
	}

	/**
	 * Register a callback that alerts you whenever our local dataset changed
	 * 
	 * @param listener
	 */
	public void setOnDataChangedListener(OnDataChanged listener) {
		// should we allow the user to set multiple listeners? I'm not sure if
		// that's done in Android
		mOnDataChangedListener = listener;
	}

	/**
	 * Callback for when any of the local data is updated
	 * 
	 */
	public interface OnDataChanged {
		void dataUpdated();
	}

	/**
	 * Delete all memory held by the manager
	 */
	public void destroy() {
		// clear conversations
		mConversations.clear();
		mConversations = null;

		// handle any other things we need to
	}

	/**
	 * Called when our local data changes. Alerts any callbacks that have been
	 * registered with the manager
	 */
	private void notifyDataChanged() {

	}

}
