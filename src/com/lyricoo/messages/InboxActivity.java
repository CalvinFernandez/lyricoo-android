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
import android.widget.ImageButton;
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
	private LyricooPlayer mPlayer;

	// remember which play button is pressed so we can revert it's state
	private ImageView mPlayButton;

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

		// initialize player
		mPlayer = new LyricooPlayer(this);

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

		// pause any music that is playing
		mPlayer.pause();

		// set the play button back to default state
		if (mPlayButton != null) {
			mPlayButton.setImageResource(R.drawable.ic_inbox_play);
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
				Conversation convo = mConversations.get(position);

				// if the play button was clicked, play the song
				if (view.equals(findViewById(R.id.play_button))) {
					Utility.log("play clicked");
				}

				// otherwise load the conversation
				else {
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

	public void playButtonClicked(View v) {
		ImageView playButton = (ImageView) v;

		// pause the music if it's playing
		if (mPlayer.isPlaying()) {
			mPlayer.pause();

			// change the button back to stopped state
			mPlayButton.setImageResource(R.drawable.ic_inbox_play);

			// if a different button was clicked from the last song that was
			// playing, play the new song
			if (!playButton.equals(mPlayButton)) {
				play(playButton);
			}
		}

		// otherwise play the song that was clicked
		else {
			play(playButton);
		}

		mPlayButton = playButton;
	}

	private void play(final ImageView playButton) {
		// retrieve the song from the view tag
		Song song = (Song) playButton.getTag();

		// need to get the layout for this specific item so the right progress
		// bar can be grabbed
		RelativeLayout iconLayout = (RelativeLayout) playButton.getParent();

		// change button to loading
		playButton.setVisibility(View.GONE);
		final ProgressBar progress = (ProgressBar) iconLayout
				.findViewById(R.id.load_progress);
		progress.setVisibility(View.VISIBLE);

		mPlayer.loadSongFromUrl(song.getUrl(), new OnPreparedListener() {

			@Override
			public void onPrepared(MediaPlayer mp) {
				// TODO: It's possible for the user to navigate away from the
				// activity before the song loads. The music then starts playing
				// from the other activity, and the play button icons get messed
				// up.

				// hide loading and show pause button
				progress.setVisibility(View.GONE);
				playButton.setImageResource(R.drawable.ic_inbox_pause);
				playButton.setVisibility(View.VISIBLE);
				mPlayer.play(new OnCompletionListener() {

					@Override
					public void onCompletion(MediaPlayer mp) {
						// set button back to stopped state
						mPlayButton.setImageResource(R.drawable.ic_inbox_play);
					}
				});
			}
		});
	}

}
