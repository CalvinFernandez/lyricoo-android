package com.lyricoo;

import java.util.ArrayList;

import org.json.JSONArray;

import android.content.Context;

import com.loopj.android.http.RequestParams;
import com.lyricoo.api.LyricooApiResponseHandler;

public class FriendManager {
	private User mUser;
	private Context mContext;
	private ArrayList<User> mFriends;

	// callbacks registered for when local data is changed
	private ArrayList<OnFriendsUpdatedListener> mOnFriendsUpdatedListeners;

	/**
	 * Initialize a FriendManager to manage a particular user's friends
	 * 
	 * @param user
	 *            The Lyricoo User whose friends we are managing
	 * @param context
	 *            The global application context
	 */
	public FriendManager(Context context, User user) {
		// store the user whose friends we are managing
		mUser = user;

		// store context
		mContext = context;

		// Initialize an empty list of friends
		mFriends = new ArrayList<User>();

		// initialize listeners list
		mOnFriendsUpdatedListeners = new ArrayList<OnFriendsUpdatedListener>();

		// Do the initial sync to get up to date. We don't have to worry about
		// the callback
		sync(null);
	}

	/**
	 * Get all friends of the user
	 * 
	 * @return
	 */
	public ArrayList<User> getFriends() {
		return mFriends;
	}

	/**
	 * Attempt to add a friend to the user's friend list. This will fail if the
	 * user is already a friend or the username isn't valid
	 * 
	 * @param username
	 *            The username of the user to add to the friends list
	 */
	public void addFriend(final String username) {
		// send post request to server
		RequestParams params = new RequestParams();
		params.put("username", username);

		mUser.post("friends", params, new LyricooApiResponseHandler() {

			@Override
			public void onSuccess(Object json) {
				// TODO: Right now the whole friend list is returned. If only
				// the added friend is returned we can add just them instead of
				// rebuilding the whole list

				// update friend list
				setFriendList((JSONArray) json);

				notifyDataUpdate();

				String msg = username + " added to friends!";
				Utility.makeBasicToast(mContext, msg);
			}

			@Override
			public void onFailure(int statusCode, String responseBody,
					Throwable error) {
				// TODO: Different status codes for different error reasons

				// Display a customized error message depending on why the
				// request failed
				String errorMessage = "";
				switch (statusCode) {
				case 400:
					// TODO: 400 is also returned when friend was already added
					errorMessage = "Invalid username";
					break;
				default:
					errorMessage = "Couldn't connect to server";
					break;
				}

				String msg = "Error adding friend: " + errorMessage;
				Utility.makeBasicToast(mContext, msg);
			}
		});
	}
	
	/**
	 * Attempts to find the friend in the users 
	 * friends list 
	 * @param id
	 * @return null if none found
	 */
	public User findFriend(int id) {
		for (User friend : mFriends) {
			if (friend.getUserId() == id) {
				return friend;
			}
		}
		return null;
	}
	
	/**
	 * Remove a friend from the user's friend list
	 * 
	 * @param friend
	 *            The user to remove
	 */
	public void removeFriend(final User friend) {
		// Tell user the friend is being removed
		String msg = "Removing " + friend.getUsername() + " from friends...";
		Utility.makeBasicToast(mContext, msg);

		// make request to server to remove friend
		mUser.delete("friends/" + friend.getUserId(),
				new LyricooApiResponseHandler() {

					@Override
					public void onSuccess(Object responseJson) {
						// remove friend locally
						mFriends.remove(friend);
						notifyDataUpdate();

						String msg = friend.getUsername()
								+ " removed from friends";
						Utility.makeBasicToast(mContext, msg);
					}

					@Override
					public void onFailure(int statusCode, String responseBody,
							Throwable error) {
						String msg = "Error removing friend";
						Utility.makeBasicToast(mContext, msg);
					}
				});
	}

	/**
	 * Force the FriendnManager to sync local data with server.
	 */
	public void sync(final OnSyncCompleted listener) {
		// Send request to server to get all of our user's friends
		mUser.get("friends", new LyricooApiResponseHandler() {
			@Override
			public void onSuccess(Object responseJson) {
				// update the friend list with the response data
				setFriendList((JSONArray) responseJson);

				// Alert data listeners that there was an update
				notifyDataUpdate();

				// alert the sync callback
				if (listener != null) {
					listener.onSuccess();
				}
			}

			@Override
			public void onFailure(int statusCode, String responseBody,
					Throwable error) {

				// TODO: Create specific error messages based on response code
				// and error
				if (listener != null) {
					listener.onFailure("Could not sync friends");
				}

				// create error toast
				String toast = "Error retrieving friends";
				Utility.makeBasicToast(mContext, toast);
			}
		});
	}

	/**
	 * Set the user's friends list to the user's in the given JSONArray. Updates
	 * the current mFriends arraylist in place
	 */
	private void setFriendList(JSONArray json) {
		// parse the json
		ArrayList<User> newData = User.parseUserJsonArray(json);

		// Rebuild the the local friends list with the new data.
		// Repopulating the old list keeps copies linked
		mFriends.clear();
		for (User friend : newData) {
			mFriends.add(friend);
		}
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
	 * Register a callback that alerts you whenever our local dataset changed
	 * 
	 * @param listener
	 */
	public void registerOnFriendsUpdatedListener(
			OnFriendsUpdatedListener listener) {
		mOnFriendsUpdatedListeners.add(listener);
	}

	/**
	 * Remove a callback that was registered
	 * @param listener
	 */
	public void unregisterOnFriendsUpdatedListener(
			OnFriendsUpdatedListener listener) {
		mOnFriendsUpdatedListeners.remove(listener);
	}

	/**
	 * Callback for when any of the local data is updated
	 * 
	 */
	public interface OnFriendsUpdatedListener {
		/**
		 * Called when the friends data is updated
		 * 
		 */
		void onFriendsUpdated();
	}

	/**
	 * Called when our local data updates. Alerts any callbacks that have been
	 * registered with the manager
	 * 
	 */
	private void notifyDataUpdate() {
		// Call all listeners that have been registered
		for (OnFriendsUpdatedListener listener : mOnFriendsUpdatedListeners) {
			listener.onFriendsUpdated();
		}
	}
}