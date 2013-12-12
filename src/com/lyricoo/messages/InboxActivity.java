package com.lyricoo.messages;

import java.util.ArrayList;

import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.media.MediaPlayer.OnPreparedListener;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;

import com.lyricoo.LyricooActivity;
import com.lyricoo.R;
import com.lyricoo.Utility;
import com.lyricoo.friends.FriendManager.OnFriendSelectedListener;
import com.lyricoo.music.LyricooPlayer;
import com.lyricoo.music.Song;
import com.lyricoo.session.Session;
import com.lyricoo.session.User;
import com.lyricoo.ui.PlayButton;
import com.lyricoo.ui.SlidingMenuHelper;

/**
 * This activity loads all of the users messages and shows a preview of the most
 * recent message from each contact. If a message is clicked the whole
 * conversation is loaded.
 * 
 */
public class InboxActivity extends LyricooActivity {
	private ArrayList<Conversation> mConversations;
	private InboxAdapter mAdapter;
	private Context mContext;

	// remember which play button is pressed so we can revert it's state
	private PlayButton mPlayButton;

	// Callback listener for when messages are updated
	private ConversationManager.OnDataChangedListener mConversationListener;

	// display resources
	private ListView mMessageList;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_inbox);
		SlidingMenuHelper.addMenuToActivity(this);
		mContext = this;

		// load conversation data
		mConversations = Session.getConversationManager().getConversations();

		// register callback on conversations update
		mConversationListener = new ConversationManager.OnDataChangedListener() {

			@Override
			public void onDataUpdated(User user) {

				// Make sure that this is run on the UI thread so it can update
				// the view. The call can originate from a GCM message update,
				// which is a background thread
				runOnUiThread(new Runnable() {
					public void run() {
						// we care about conversations with all contacts, so
						// update everything
						updateConversations();
					}
				});

			}

			@Override
			public void onDataReset() {
				// Get a fresh copy of the conversation
				mConversations = Session.getConversationManager()
						.getConversations();
				displayConversations();
			}
		};

		Session.getConversationManager().registerOnDataChangedListener(
				mConversationListener);

		// save resources
		mMessageList = (ListView) findViewById(R.id.messages_list);

		displayConversations();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();

		try {
			Session.getConversationManager().unregisterOnDataChangedListener(
					mConversationListener);
		} catch (Exception e) {
			// thrown if conversation manager is null
		}
	}

	@Override
	protected void onPause() {
		super.onPause();

		// set the selected play button back to default state and clear the
		// selection
		if (mPlayButton != null) {
			mPlayButton.stop();
			mPlayButton = null;
		}
	}

	@Override
	protected void onResume() {
		super.onResume();

		updateConversations();
	}

	/**
	 * Tell the adapter that the data has changed and it needs to update the
	 * view
	 */
	protected void updateConversations() {
		mAdapter.notifyDataSetChanged();
	}

	/**
	 * Get all of our users conversations and show them in a listview
	 */
	private void displayConversations() {
		// Create a new adapter for this conversation data
		mAdapter = new InboxAdapter(mContext, mConversations);

		mMessageList.setAdapter(mAdapter);

		// When a message is clicked load the whole conversation
		mMessageList.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				// load the conversation
				Conversation convo = mConversations.get(position);

				// Pass selected user so conversationActivity knows whose
				// conversation to display
				User contact = convo.getContact();

				// Mark conversation as read
				convo.read();

				// convert to json to make it easy to pass to the object
				String contactAsJson = Utility.toJson(contact);

				Intent i = new Intent(mContext, ConversationActivity.class);
				i.putExtra("contact", contactAsJson);
				startActivity(i);
			}
		});
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.inbox, menu);
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle presses on the action bar items
		switch (item.getItemId()) {
		case R.id.action_compose:
			newMessage();
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}

	private void newMessage() {
		Session.getFriendManager().showFriendPicker(this,
				"Send message to friend", new OnFriendSelectedListener() {
					@Override
					public void onFriendSelected(User friend) {
						// open the conversation activity with the selected
						// friend
						String contactAsJson = Utility.toJson(friend);
						Intent i = new Intent(mContext,
								ConversationActivity.class);
						i.putExtra("contact", contactAsJson);
						startActivity(i);
					}
				});
	}

	/**
	 * Handle a play button being pressed. There is a different button for each
	 * inbox item that has a song, so we need to keep track of multiple buttons
	 * and make sure they are managed accordingly without collisions
	 * 
	 * @param v
	 *            The ImageView containing the play button that was pressed
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

}
