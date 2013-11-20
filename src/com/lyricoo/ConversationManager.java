package com.lyricoo;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONObject;

import android.content.Context;

import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

/**
 * This class manages all of a user's conversations with other users.
 * 
 */
public class ConversationManager {
	private User mUser;
	private Context mContext;
	private ArrayList<Conversation> mConversations;
	// Whether the last sync was successful or not
	private boolean mIsSynced;
	// callbacks registered for when local data is changed
	private ArrayList<OnDataChangedListener> mOnDataChangedListeners;

	// TODO: Add polling to server when GCM isn't working on a device

	/**
	 * Initialize a ConversationManager to manage a particular user's messages
	 * 
	 * @param user
	 *            The Lyricoo User who's messages we are managing
	 */
	public ConversationManager(Context context, User user) {
		// store the user whose messages we are managing
		mUser = user;

		// store context
		mContext = context;

		// Initialize an empty list of conversations
		mConversations = new ArrayList<Conversation>();

		// initialize listeners list
		mOnDataChangedListeners = new ArrayList<OnDataChangedListener>();

		// Do the initial sync to get up to date. We don't have to worry about
		// the callback
		mIsSynced = false;
		sync(null);
	}

	/**
	 * Delete all memory held by the manager
	 */
	public void destroy() {
		// clear conversations
		mConversations.clear();
		
		// update data one last time then remove listeners	
		notifyDataChanged();
		mConversations = null;

			
		mOnDataChangedListeners.clear();
		mOnDataChangedListeners = null;
	}

	/**
	 * Get all conversations the user is involved in
	 * 
	 * @return
	 */
	public ArrayList<Conversation> getConversations() {
		return mConversations;
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
		// Check through all conversations for one that matches contact
		for (Conversation conversation : mConversations) {
			if (contact.equals(conversation.getContact())) {
				return conversation;
			}
		}

		// No match. Create an empty conversation, add it to the list, and
		// return it
		Conversation newConversation = new Conversation(contact);
		mConversations.add(newConversation);
		return newConversation;
	}

	/**
	 * Send a message to a user
	 * 
	 * @param message
	 *            The message to add to the conversation
	 * @param contact
	 *            The user that the conversation is with
	 */
	public void sendMessage(final Message message, User contact) {
		// add the message to the local conversation
		final Conversation conversation = getConversation(contact);
		conversation.add(message);
		notifyDataChanged();

		// send a post request to server to create message
		RequestParams params = new RequestParams();
		params.put("contact_id", Integer.toString(message.getContactId()));
		params.put("content", message.getContent());
		params.put("sent", "true");
		if (message.getSong() != null) {
			params.put("song_id", Integer.toString(message.getSong().getId()));
		}

		mUser.post("messages", params, new JsonHttpResponseHandler() {

			@Override
			public void onSuccess(JSONObject json) {
				// TODO: Have server messages#create return the message
				// json so we can update the local copy with its new id
			}

			// Response should be a JSONObject but include JSONArray
			// method to be safe
			@Override
			public void onFailure(Throwable error, JSONObject json) {
				handleMessageSendFailure(conversation, message);
			}

			@Override
			public void onFailure(Throwable error, JSONArray json) {
				handleMessageSendFailure(conversation, message);
			}
		});
	}

	/**
	 * Handle the case where a create message post request to the server fails
	 * 
	 * @param conversation
	 *            The conversation the message is a part of
	 * @param message
	 *            The message that was sent
	 */
	protected void handleMessageSendFailure(Conversation conversation,
			Message message) {
		// TODO: Mark message as sending failed instead of just deleting?

		// Delete the message locally
		conversation.remove(message);

		// alert listeners to changes
		notifyDataChanged();

		// create error toast
		String toast = "Error sending message";
		Utility.makeBasicToast(mContext, toast);
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
		Conversation conversation = getConversation(contact);
		conversation.add(message);

		// alert listeners to changes
		notifyDataChanged();
	}

	/**
	 * Force the ConversationManager to sync local data with server.
	 */
	public void sync(final OnSyncCompleted listener) {
		// Send request to server to get all of our user's messages
		mUser.get("messages", new JsonHttpResponseHandler() {
			@Override
			public void onSuccess(JSONObject json) {
				// This will create all new Conversation objects, unlinking any
				// copies that activities may be using
				mConversations = Conversation.parseMessagesJson(json);

				notifyDataChanged();

				mIsSynced = true;

				// call the callback
				if (listener != null) {
					listener.onSuccess();
				}
			}

			@Override
			public void onFailure(Throwable error, JSONObject json) {
				// TODO: Use String responseBody onFailure method. Need to
				// change LyricooResponseAdapter
				mIsSynced = false;

				// TODO: Create specific error messages based on response code
				if (listener != null) {
					listener.onFailure("Could not sync");
				}

				// create error toast
				String toast = "Error retrieving message";
				Utility.makeBasicToast(mContext, toast);
			}
		});
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
	public void registerOnDataChangedListener(OnDataChangedListener listener) {
		// TODO: Allow client to register a listener for just a specific
		// conversation to be more efficient

		// TODO: Conversation copies will reflect changes as long as the update
		// didn't come from sync. It is much more efficient if the client
		// doesn't have to reload fresh data, maybe include a boolean like
		// dataInvalid?
		mOnDataChangedListeners.add(listener);
	}

	// TODO: method to unregister listener

	/**
	 * Callback for when any of the local data is updated
	 * 
	 */
	public interface OnDataChangedListener {
		void onDataChanged();
	}

	/**
	 * Called when our local data changes. Alerts any callbacks that have been
	 * registered with the manager
	 */
	private void notifyDataChanged() {
		// Call all listeners that have been registered
		for (OnDataChangedListener listener : mOnDataChangedListeners) {
			listener.onDataChanged();
		}
	}

}
