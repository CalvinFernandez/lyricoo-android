package com.lyricoo.activity;

import org.json.JSONArray;
import org.json.JSONObject;

import com.google.gson.JsonSyntaxException;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.lyricoo.Conversation;

import com.lyricoo.LyricooApp;
import com.lyricoo.LyricooPlayer;
import com.lyricoo.Message;
import com.lyricoo.R;
import com.lyricoo.Session;
import com.lyricoo.Song;
import com.lyricoo.Utility;

import android.os.Bundle;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.Menu;
import android.view.View;
import android.view.View.OnLongClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.TextView;

/**
 * Display a conversation with another Lyricoo User. The conversation is simply
 * a list view of all messages between the currentUser and the contact with the
 * option to play a Lyricoo that has been included in a message.
 * 
 * Messages in the conversation can be clicked and long clicked. Clicking plays
 * the attached lyricoo, if applicable, and long clicking opens a dialog with
 * options/info (still TODO).
 * 
 * The activity also provides tools for creating a new message to send to the
 * contact.
 * 
 */
public class ConversationActivity extends Activity {
	private LyricooApp mApp;
	private Conversation mConversation;
	private ConversationAdapter mConversationAdapter;

	// player to handle playing lyricoos from messages
	private LyricooPlayer mPlayer;

	// Layout resources to display message sending options
	private TextView mLyricooTitle;
	private ListView mMessageList;

	// The Lyricoo that the user selected to include in their message. Null if
	// none selected
	private Song mSelectedLyricoo;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_conversation);

		mApp = (LyricooApp) getApplication();

		mConversation = mApp.conversationToDisplay;

		// initialize player
		mPlayer = new LyricooPlayer(this);

		// set activity title to reflect contact's name
		setTitle(mConversation.getContact().getUsername());

		// Retrieve resources for later
		mLyricooTitle = (TextView) findViewById(R.id.lyricoo_title);
		mMessageList = (ListView) findViewById(R.id.messages_list);

		// set callback for selected Lyricoo long click
		mLyricooTitle.setOnLongClickListener(new OnLongClickListener() {

			@Override
			public boolean onLongClick(View v) {
				onLyricooTitleLongClick();
				// return true to signify we have handled the click
				return true;
			}
		});

		// build the list view for the messages in the conversation
		displayMessages();
	}

	@Override
	protected void onPause() {
		super.onPause();
		// Stop the music if the activity looses focus
		mPlayer.stop();
	}

	private void displayMessages() {
		mConversationAdapter = new ConversationAdapter(this,
				R.id.messages_list, mConversation);

		// Create adapter for the list view
		mMessageList.setAdapter(mConversationAdapter);

		// Add click listener to list
		mMessageList.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				// Get the message that was clicked and play attached lyricoo if
				// applicable
				Message msg = mConversation.getMessages().get(position);
				Song song = msg.getSong();
				if (song != null) {
					playSong(song);
				}
			}
		});

		mMessageList.setOnItemLongClickListener(new OnItemLongClickListener() {

			@Override
			public boolean onItemLongClick(AdapterView<?> arg0, View arg1,
					int arg2, long arg3) {
				// TODO: Show popup with message options

				// Return true to signal that we have handled the click
				return true;
			}
		});
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.conversation, menu);
		return true;
	}

	public void sendMessage(View v) {
		EditText conversationInputView = (EditText) findViewById(R.id.conversation_input);
		String messageContent = conversationInputView.getText().toString();

		// if content is blank and no song is selected don't do anything
		if (Utility.isStringBlank(messageContent) && mSelectedLyricoo == null)
			return;

		final Message newMessage = new Message(messageContent, Session.currentUser()
				.getUserId(), mConversation.getContact().getUserId(), true,
				mSelectedLyricoo);

		// Update display with message
		mConversationAdapter.add(newMessage);

		// Select the last row so the new message will scroll into view
		mMessageList.setSelection(mConversationAdapter.getCount() - 1);

		// Send post request to server to save message
		
		RequestParams params = new RequestParams();
		params.put("contact_id", Integer.toString(newMessage.getContactId()));
		params.put("content", messageContent);
		params.put("sent", "true");
		if(mSelectedLyricoo != null){
			params.put("song_id", Integer.toString(mSelectedLyricoo.getId()));
		}
		
		Session.currentUser().post("messages", params, new JsonHttpResponseHandler() {
			
			// TODO: Better way for handling synchronicity with server if sending fails
			
			@Override
			public void onSuccess(JSONArray json) {
				// TODO: On success update the sent message with its new id from the server. Need to get server to return message info

			}
			
			@Override
			public void onSuccess(JSONObject json) {

			}

			// Response should be a JSONObject but include JSONArray method to be safe
			@Override
			public void onFailure(Throwable error, JSONObject json) {
				handleMessageSendFailure(newMessage);
			}
			
			@Override
			public void onFailure(Throwable error, JSONArray json) {
				handleMessageSendFailure(newMessage);
			}
		});

	}

	protected void handleMessageSendFailure(Message message) {
		// create toast
		String toast = "Error sending message";
		Utility.makeBasicToast(getApplicationContext(), toast);
		
		// Delete the message
		mConversationAdapter.remove(message);		
	}

	// When the lyricoo title is clicked
	public void selectedLyricooClicked(View v) {
		// play/pause the selected lyricoo if there is one
		if (mSelectedLyricoo == null)
			return;

		// play selected song
		playSong(mSelectedLyricoo);
	}

	private void playSong(Song song) {
		// TODO: Handle pausing if player is currently playing
		// TODO: Show some indication of song playing.
		// TODO: Cache song so it doesn't have to load every time
		mPlayer.loadSongFromUrl(song.getUrl(), null);
	}

	// the button to select a lyricoo to include
	public void lyricooButtonClicked(View v) {
		// Launch LyricooSelectionActivity for a result
		Intent i = new Intent(this, LyricooSelectionActivity.class);
		startActivityForResult(i,
				LyricooSelectionActivity.SELECT_LYRICOO_REQUEST);
	}

	/**
	 * Handle a long click on the selected lyricoo
	 * 
	 */
	private void onLyricooTitleLongClick() {
		// if a lyricoo isn't currently selected do nothing
		if (mSelectedLyricoo == null)
			return;

		// otherwise show an option to remove the lyricoo

		// the options to show in the dialog list
		String[] options = { "Remove Lyricoo" };
		// create a new dialog
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		// set the title to be the song name
		builder.setTitle(mSelectedLyricoo.getTitle())
		// add the options to the list
				.setItems(options, new DialogInterface.OnClickListener() {
					// handle clicking on an option
					public void onClick(DialogInterface dialog, int which) {
						switch (which) {
						// "Remove Lyricoo" is the first option in the array
						case 0:
							removeLyricoo();
							break;
						default:
							break;
						}
					}
				});
		builder.create().show();
	}

	protected void removeLyricoo() {
		// Remove the currently selected lyricoo from the user's message
		mSelectedLyricoo = null;

		// update lyricoo text
		mLyricooTitle.setText("No Lyricoo Selected");
	}

	// After returning from the LyricooSelectionActivity this is called
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {

		if (requestCode == LyricooSelectionActivity.SELECT_LYRICOO_REQUEST) {

			if (resultCode == RESULT_OK) {
				String songJson = data.getStringExtra("lyricoo");

				// Convert json to Song object
				try {
					// save the result as a private instance variable
					mSelectedLyricoo = Utility.fromJson(songJson, Song.class);
				} catch (JsonSyntaxException e) {
					// Problem converting result into Song. Exit and do nothing
					mSelectedLyricoo = null;
					return;
				}

				mLyricooTitle.setText(mSelectedLyricoo.getTitle());
			}
			if (resultCode == RESULT_CANCELED) {
				// Don't do anything if a lyricoo wasn't selected
			}
		}
	}

}
