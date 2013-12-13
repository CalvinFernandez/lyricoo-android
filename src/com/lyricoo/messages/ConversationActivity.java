package com.lyricoo.messages;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.lyricoo.LyricooActivity;
import com.lyricoo.R;
import com.lyricoo.Utility;
import com.lyricoo.messages.MessageList.onSizeChangedListener;
import com.lyricoo.music.CategoriesActivity;
import com.lyricoo.music.LyricooSelectionActivity;
import com.lyricoo.music.Song;
import com.lyricoo.session.Session;
import com.lyricoo.session.User;
import com.lyricoo.ui.PlayButton;
import com.lyricoo.ui.SlidingMenuHelper;

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
public class ConversationActivity extends LyricooActivity {
	private Conversation mConversation;
	private User mContact;
	private ConversationAdapter mConversationAdapter;

	// Callback listener for when messages are updated
	private ConversationManager.OnDataChangedListener mConversationListener;

	// The last used play button for songs in the messages list
	private PlayButton mMessagePlayButton;

	// Layout resources to display message sending options
	private MessageList mMessageList;
	private EditText mTextInput;

	// The Lyricoo that the user selected to include in their message. Null if
	// none selected
	private Song mSelectedLyricoo;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_conversation);
		SlidingMenuHelper.addMenuToActivity(this);

		// get the User who we are talking to
		String contactAsJson = getIntent().getStringExtra("contact");
		mContact = Utility.fromJson(contactAsJson, User.class);
		if (mContact == null) {
			// uh oh, no contact was passed... TODO: What do? Error message and
			// return to main menu
		}

		// load the conversation data
		mConversation = Session.getConversationManager().getConversation(
				mContact);

		// Create a listener for message updates. Don't make it anonymous so we
		// can remove it onDestroy
		mConversationListener = new ConversationManager.OnDataChangedListener() {

			@Override
			public void onDataUpdated(User user) {
				// only update our view if our contact was updated
				if (user.equals(mContact)) {
					updateConversation();
				}
			}

			@Override
			public void onDataReset() {
				// Get a fresh copy of the conversation
				mConversation = Session.getConversationManager()
						.getConversation(mContact);
				displayConversation();
			}
		};

		// register data update callback
		Session.getConversationManager().registerOnDataChangedListener(
				mConversationListener);

		// set activity title to reflect contact's name
		setTitle(mContact.getUsername());

		// Retrieve resources for later
		// mLyricooTitle = (TextView) findViewById(R.id.lyricoo_title);
		mMessageList = (MessageList) findViewById(R.id.messages_list);

		// build the list view for the messages in the conversation
		displayConversation();

		// listen for resizing of the message list as it indicates the keyboard
		// popping up. When the keyboard pops up the bottom of the list gets
		// covered, we should scroll down to return to the bottom.
		mMessageList.setOnSizeChangedListener(new onSizeChangedListener() {

			@Override
			public void onSizeChanged(int w, int h, int oldw, int oldh) {
				// if the height is much less than before the keyboard probably
				// popped up. The height reduces slightly when the text input
				// expands to multiple lines, but that is a much smaller change
				if (oldh - h > 100) {
					scrollToBottom();
				}
			}
		});

		mTextInput = (EditText) findViewById(R.id.conversation_input);

		// If a song was included with the activity intent then load it.
		// retrieve data from intent, will be null if no song was included
		String songJson = getIntent().getStringExtra("song");
		attachSong(songJson);

	}

	@Override
	protected void onPause() {
		super.onPause();
		// Stop the music if the activity looses focus
		if (mMessagePlayButton != null) {
			mMessagePlayButton.stop();
			mMessagePlayButton = null;
		}
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();

		try {
			Session.getConversationManager().unregisterOnDataChangedListener(
					mConversationListener);
		} catch (Exception e) {
			// thrown if conversation manager if null
		}
	}
	
	@Override 
	protected void onSaveInstanceState(Bundle outState){
		super.onSaveInstanceState(outState);
		
		// save the lyricoo that was selected
		if(mSelectedLyricoo != null){
			outState.putString("lyricoo", Utility.toJson(mSelectedLyricoo));
		}
	}
	
	@Override
	protected void onRestoreInstanceState(Bundle savedInstanceState){
		super.onRestoreInstanceState(savedInstanceState);
		
		//  check for a previously attached song
		String songJson = savedInstanceState.getString("lyricoo");
		
		if(songJson != null){
			attachSong(songJson);
		}		
	}
	
	
	

	/**
	 * Tell the adapter that the data has changed and it needs to update the
	 * view
	 */
	protected void updateConversation() {
		// Make sure that this is run on the UI thread so it can update the
		// view. The call can originate from a GCM message update, which is a
		// background thread
		runOnUiThread(new Runnable() {
			public void run() {
				mConversationAdapter.notifyDataSetChanged();
				scrollToBottom();
			}
		});

	}

	private void displayConversation() {
		mConversationAdapter = new ConversationAdapter(this,
				R.layout.conversation_item_sent, mConversation);

		// Create adapter with the new conversation data
		mMessageList.setAdapter(mConversationAdapter);

		scrollToBottom();

		// Add click listener to list
		mMessageList.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				// TODO: Allow interaction on click
			}
		});

		mMessageList.setOnItemLongClickListener(new OnItemLongClickListener() {

			@Override
			public boolean onItemLongClick(AdapterView<?> arg0, View arg1,
					int arg2, long arg3) {
				// TODO: Enter data selection mode

				// Return true to signal that we have handled the click
				return true;
			}
		});
	}

	/**
	 * Scroll to the bottom of the message list, showing the most recent
	 * messages.
	 * 
	 */
	private void scrollToBottom() {
		mMessageList.setSelection(mConversationAdapter.getCount() - 1);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.conversation, menu);
		return true;
	}

	/**
	 * Called when send is clicks. Creates a message with the entered info and
	 * sends it to the contact this conversation is with. If no message has been
	 * entered and no song has been selected then no message is sent
	 * 
	 * @param v
	 *            The view that called this
	 */
	public void sendMessage(View v) {
		mTextInput = (EditText) findViewById(R.id.conversation_input);
		String messageContent = mTextInput.getText().toString();

		// if content is blank and no song is selected don't do anything
		if (Utility.isStringBlank(messageContent) && mSelectedLyricoo == null) {
			return;
		}

		Message newMessage = new Message(messageContent, Session.currentUser()
				.getUserId(), mContact.getUserId(), true, mSelectedLyricoo);

		// send message
		Session.getConversationManager().sendMessage(newMessage, mContact);

		// Clear out the input text
		mTextInput.setText("");
	}

	// the button to select a lyricoo to include
	public void lyricooButtonClicked(View v) {
		// Launch LyricooSelectionActivity for a result
		Intent i = new Intent(this, CategoriesActivity.class);
		startActivityForResult(i,
				CategoriesActivity.SELECT_LYRICOO_REQUEST);
	}

	/**
	 * Called when a play button is pressed
	 * 
	 * @param v
	 */
	public void playButtonClicked(View v) {
		PlayButton playButton = (PlayButton) v;

		// if a new play button was hit, stop the old one and start the new one
		if (!playButton.equals(mMessagePlayButton)) {
			if (mMessagePlayButton != null) {
				mMessagePlayButton.stop();
			}
			mMessagePlayButton = playButton;
			mMessagePlayButton.play();
		}

		// otherwise the same button was hit so toggle it's state
		else {
			playButton.toggle();
		}
	}

	protected void removeLyricoo() {
		// Remove the currently selected lyricoo from the user's message
		mSelectedLyricoo = null;
	}

	// After returning from the LyricooSelectionActivity this is called
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {

		if (requestCode == CategoriesActivity.SELECT_LYRICOO_REQUEST) {

			if (resultCode == RESULT_OK) {
				String songJson = data.getStringExtra("lyricoo");
				attachSong(songJson);

			}
			if (resultCode == RESULT_CANCELED) {
				// Don't do anything if a lyricoo wasn't selected
			}
		}
	}

	/**
	 * Attaches the given song to the message to send
	 * 
	 * @param songJson
	 *            The song to attach in String json format
	 */
	private void attachSong(String songJson) {
		// don't do anything if null was passed in
		if (songJson == null) {
			return;
		}

		// Convert json to Song object
		Song song = Utility.fromJson(songJson, Song.class);

		// if song is null then either the song was in a bad format or
		// something really bad happened with the json function.
		if (song == null) {
			// TODO: Record this error
			mSelectedLyricoo = null;
			return;
		}

		// save the result as a private instance variable
		mSelectedLyricoo = song;

		// show play button and hide button to attach lyricoo
		RelativeLayout buttonContainer = (RelativeLayout) findViewById(R.id.song_selection_container);
		ImageView button = (ImageView) buttonContainer
				.findViewById(R.id.add_song_button);
		button.setVisibility(View.GONE);
		PlayButton playButton = (PlayButton) buttonContainer
				.findViewById(R.id.play_button);
		playButton.setVisibility(View.VISIBLE);
		playButton.setSong(mSelectedLyricoo);
	}
}
