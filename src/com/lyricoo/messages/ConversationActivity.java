package com.lyricoo.messages;

import java.util.Date;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.view.View.OnLongClickListener;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.lyricoo.LyricooActivity;
import com.lyricoo.R;
import com.lyricoo.Utility;
import com.lyricoo.music.LyricooPlayer;
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

	// The last used play button
	private PlayButton mPlayButton;

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
		displayConversation();

		// If a song was included with the activity intent then load it.
		// retrieve data from intent, will be null if no song was included
		String songJson = getIntent().getStringExtra("song");
		attachSong(songJson);

		attachKeyboardListener();
	}

	@Override
	protected void onPause() {
		super.onPause();
		// Stop the music if the activity looses focus
		if (mPlayButton != null) {
			mPlayButton.stop();
			mPlayButton = null;
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
		EditText conversationInputView = (EditText) findViewById(R.id.conversation_input);
		String messageContent = conversationInputView.getText().toString();

		// if content is blank and no song is selected don't do anything
		if (Utility.isStringBlank(messageContent) && mSelectedLyricoo == null) {
			return;
		}

		Message newMessage = new Message(messageContent, Session.currentUser()
				.getUserId(), mContact.getUserId(), true, mSelectedLyricoo);

		// send message
		Session.getConversationManager().sendMessage(newMessage, mContact);
	}

	// When the lyricoo title is clicked
	public void selectedLyricooClicked(View v) {
		// play/pause the selected lyricoo if there is one
		if (mSelectedLyricoo == null)
			return;

		// play selected song
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

	/**
	 * Called when a play button is pressed
	 * 
	 * @param v
	 */
	public void playButtonClicked(View v) {
		PlayButton playButton = (PlayButton) v;

		// if a new play button was hit, stop the old one and start the new one
		if (!playButton.equals(mPlayButton)) {
			if (mPlayButton != null) {
				mPlayButton.stop();
			}
			mPlayButton = playButton;
			mPlayButton.play();
		}

		// otherwise the same button was hit so toggle it's state
		else {
			playButton.toggle();
		}
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
			mLyricooTitle.setText("No Lyricoo Selected");
			return;
		}

		// save the result as a private instance variable
		mSelectedLyricoo = song;

		// update display to show song
		mLyricooTitle.setText(mSelectedLyricoo.getTitle());
	}

	/**
	 * When the keyboard pops up to write a message the view size is changed and
	 * the bottom of the message list is hidden. We can listen for the keyboard
	 * and update the view accordingly
	 */
	private boolean isKeyboardShown = false;

	private void attachKeyboardListener() {
		final View activityRootView = findViewById(R.id.root_view);
		activityRootView.getViewTreeObserver().addOnGlobalLayoutListener(
				new OnGlobalLayoutListener() {
					@Override
					public void onGlobalLayout() {
						// if the height changes enough the keyboard was
						// probably introduced
						int heightDiff = activityRootView.getRootView()
								.getHeight() - activityRootView.getHeight();
						if (heightDiff > 100) { // if more than 100 pixels, its
												// probably a keyboard...

							// if the keyboard was just shown, scroll the
							// messages down to the bottom
							if (!isKeyboardShown) {
								isKeyboardShown = true;
								scrollToBottom();
							}
						}
						// keep track of whether or not the keyboard was just
						// shown. For some reason this callback is called even
						// when the layout doesn't seem to change much and this
						// prevents duplicate calls
						else {
							isKeyboardShown = false;
						}
					}
				});
	}
}
