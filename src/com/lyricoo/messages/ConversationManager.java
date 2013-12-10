package com.lyricoo.messages;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;

import com.loopj.android.http.RequestParams;
import com.lyricoo.Utility;
import com.lyricoo.api.LyricooApiResponseHandler;
import com.lyricoo.session.Session;
import com.lyricoo.session.User;

/**
 * This class manages all of a user's conversations with other users.
 * Conversations are retrieved with getConversations() and
 * getConversation(contact). Shallow copies of the conversations are passed, so
 * when a single message is added the data updates automatically. However,
 * sync() recreates all Conversations with new data structures, unlinking
 * existing copies from future updates. The OnDataChangedListener will register
 * for both cases, but in the case of a sync it will pass oldDataInvalidated =
 * true to notify the client that they need to retrieve a fresh copy.
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
		mConversations = null;

		// remove any listeners
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
	public void sendMessage(final Message message, final User contact) {
		// add the message to the local conversation
		final Conversation conversation = getConversation(contact);
		conversation.add(message);
		message.read();
		notifyDataUpdate(contact);

		// send a post request to server to create message
		RequestParams params = new RequestParams();
		params.put("contact_id", Integer.toString(message.getContactId()));
		params.put("content", message.getContent());
		params.put("sent", "true");
		if (message.getSong() != null) {
			params.put("song_id", Integer.toString(message.getSong().getId()));
		}

		mUser.post("messages", params, new LyricooApiResponseHandler() {

			@Override
			public void onSuccess(Object responseJson) {
				message.update((JSONObject) responseJson);
				// TODO: Cleaner way to mark a sent message as read with the server
				message.read();
				message.put(new LyricooApiResponseHandler() {
					public void onSuccess(Object responseJson) {
						message.read();
						notifyDataUpdate(contact);
					}
				});
				sortConversations(mConversations);
				notifyDataUpdate(contact);
			}

			public void onFailure(int statusCode, String responseBody,
					Throwable error) {
				// TODO: Customize and error based on statusCode
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
		notifyDataUpdate(conversation.getContact());

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
		sortConversations(mConversations);

		// alert listeners to changes
		notifyDataUpdate(contact);
	}

	/**
	 * Add a message to the local conversation. Will attempt to guess the
	 * appropriate conversation to which the add the message, or initialize a
	 * new conversation if the contact is in contacts but doesn't currently have
	 * a conversation thread with the user
	 * 
	 * @param message
	 */
	public void receiveMessage(Message message, String _contact) {
		User contact = Session.getFriendManager().findFriend(
				message.getContactId());
		if (contact == null) {
			if (_contact != null) {
				try {
					contact = new User(_contact);
					receiveMessage(message, contact);
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		} else {
			receiveMessage(message, contact);
		}
	}

	/**
	 * Force the ConversationManager to sync local data with server.
	 */
	public void sync(final OnSyncCompleted listener) {
		// Send request to server to get all of our user's messages
		mUser.get("messages", new LyricooApiResponseHandler() {
			@Override
			public void onSuccess(Object responseJson) {
				// This will create all new Conversation objects, unlinking any
				// copies that activities may be using
				mConversations = Conversation
						.parseMessagesJson((JSONObject) responseJson);

				sortConversations(mConversations);

				// Alert listeners that their old data copies are no longer
				// linked
				notifyDataReset();

				mIsSynced = true;

				// call the callback
				if (listener != null) {
					listener.onSuccess();
				}
			}

			@Override
			public void onFailure(int statusCode, String responseBody,
					Throwable error) {
				mIsSynced = false;

				// TODO: Create specific error messages based on response code
				// and error
				if (listener != null) {
					listener.onFailure("Could not sync");
				}

				// create error toast
				String toast = "Error retrieving messages";
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
		mOnDataChangedListeners.add(listener);
	}

	/**
	 * Remove a listener that was registered
	 * 
	 * @param listener
	 */
	public void unregisterOnDataChangedListener(OnDataChangedListener listener) {
		mOnDataChangedListeners.remove(listener);
	}

	/**
	 * Callback for when any of the local data is updated
	 * 
	 */
	public interface OnDataChangedListener {
		/**
		 * Called when the conversation data for a specific user changes
		 * 
		 */
		void onDataUpdated(User user);

		/**
		 * Called when all data is reset. The client must grab a new copy of the
		 * data with getConversation()
		 */
		void onDataReset();
	}

	/**
	 * Called when our local data updates. Alerts any callbacks that have been
	 * registered with the manager
	 * 
	 * @param contact
	 *            The user whose conversation was updated
	 * 
	 */
	private void notifyDataUpdate(User contact) {
		// Call all listeners that have been registered
		for (OnDataChangedListener listener : mOnDataChangedListeners) {
			listener.onDataUpdated(contact);
		}
	}

	/**
	 * Called when our local data is reset. Alerts any callbacks that have been
	 * registered with the manager
	 * 
	 */
	private void notifyDataReset() {
		// Call all listeners that have been registered
		for (OnDataChangedListener listener : mOnDataChangedListeners) {
			listener.onDataReset();
		}
	}

	/**
	 * Check if our user has received new messages. If so they are retrieved
	 * from the server.
	 */
	public void checkForNewMessages() {
		mUser.get(new LyricooApiResponseHandler() {
			@Override
			public void onSuccess(Object json) {
				try {
					JSONObject userJson = (JSONObject) json;
					boolean synced = userJson.getBoolean("synced");
					if (!synced) {
						sync(null);
					}
				} catch (Exception e) {
					Utility.log("Error parsing user json in ConversationManager.checkNewMessages()");
				}
			}

			@Override
			public void onFailure(int statusCode, String responseBody,
					Throwable error) {
				// TODO: If the internet/server is out and the Poller keeps
				// calling us this toast will be made continually. Maybe add a
				// check so it's not called every ten seconds in that case
				Utility.makeBasicToast(mContext,
						"Error retrieving new messages");
			}
		});

	}
	
	/**
	 * Get the total number of unread messages for this user
	 * @return
	 */
	public int getUnreadCount(){
		int count = 0;
		for(Conversation c : mConversations){
			count += c.getUnreadCount();
		}
		
		return count;
	}

	/**
	 * Sort conversations by most recent
	 * 
	 * @param conversations
	 */
	private void sortConversations(ArrayList<Conversation> conversations) {
		Collections.sort(conversations, new ConversationComparator());
	}

	private class ConversationComparator implements Comparator<Conversation> {
		public int compare(Conversation left, Conversation right) {
			return right.getMostRecentMessage().getTime()
					.compareTo(left.getMostRecentMessage().getTime());
		}
	}

}
