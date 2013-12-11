package com.lyricoo.music;

import java.util.ArrayList;

import org.apache.http.Header;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;

import com.lyricoo.LyricooActivity;
import com.lyricoo.R;
import com.lyricoo.Utility;
import com.lyricoo.messages.ConversationActivity;
import com.lyricoo.music.MusicManager.MusicHandler;
import com.lyricoo.session.Session;
import com.lyricoo.session.User;

public class CategoryActivity extends LyricooActivity {
	private Category mCategory;
	private Context mContext;
	private ListView mSongListView;
	private Song mSelectedSong;
	private LyricooPlayer mPlayer;

	/*
	 * Options bar at bottom of screen
	 */
	private RelativeLayout mSongOptions;
	private Button mPlayButton;
	private TextView mSongTitle;
	private ProgressBar mSongProgress;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_category);
		mContext = this;

		mSongListView = (ListView) findViewById(R.id.category_song_list);

		String jCategory = getIntent().getStringExtra("category");
		mCategory = Utility.fromJson(jCategory, Category.class);

		mPlayer = new LyricooPlayer(this);
		mSongOptions = (RelativeLayout) findViewById(R.id.song_options);
		mPlayButton = (Button) findViewById(R.id.play_button);
		mSongTitle = (TextView) findViewById(R.id.song_title);
		mSongProgress = (ProgressBar) findViewById(R.id.song_progress);

		MusicManager.findSongsByCategory(mCategory, new MusicHandler() {

			@Override
			public void onSuccess(ArrayList<Song> songs,
					ArrayList<Category> categories) {
				displaySongs(songs);
			}

			@Override
			public void onFailure(int statusCode, Header[] headers,
					String responseBody, Throwable e) {
				String toast = "Error retrieving songs";
				Utility.makeBasicToast(mContext, toast);

			}

		});

	}
	
	
	// The button to send this lyricoo to a friend triggers this call
	public void sendClicked(View v) {
		// We handle this differently depending on how the activity was
		// launched. If launched for result we return a result intent and
		// finish(). Otherwise we let the user pick which friend to send to

		// getCallingActivity is non null if this activity was started for a
		// result
		if (getCallingActivity() != null) {
			// Return the selected lyricoo as a result
			Intent returnIntent = new Intent();
			returnIntent.putExtra("lyricoo", Utility.toJson(mSelectedSong));
			setResult(RESULT_OK, returnIntent);
			finish();
		}

		// otherwise the user hasn't yet picked somebody to send this lyricoo
		// too. Show a list of friends to send to
		else {
			// Show friends list and so user can send song to a friend
			showFriendsList();
		}
	}

	/**
	 * Load the current user's friends in a dialog list and send the currently
	 * selected lyricoo to the clicked friend
	 */
	private void showFriendsList() {

		final ArrayList<User> friends = Session.getFriendManager().getFriends();

		// get list of just friend names to show in dialog
		ArrayList<String> names = new ArrayList<String>();
		for (User friend : friends) {
			names.add(friend.getUsername());
		}

		// if the user doesn't have any friends show them a different dialog
		if (names.isEmpty()) {
			// TODO: Show no friends dialog
			return;
		}

		// convert ArrayList to Array so AlertDialog can use it
		String[] namesArray = new String[names.size()];
		namesArray = names.toArray(namesArray);

		// create a new dialog
		AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
		// set the title to be the username of the friend
		builder.setTitle("Send Lyricoo")
		// add the options to the list
				.setItems(namesArray, new DialogInterface.OnClickListener() {
					// handle clicking on an option
					public void onClick(DialogInterface dialog, int which) {
						// get the friend that was selected and
						// pass the selected song to the
						// conversation with that friend
						try {
							User friend = friends.get(which);
							sendLyricooToFriend(mSelectedSong, friend);
						} catch (Exception e) {
							// If this gets caught it's probably
							// index out of bound error. TODO:
							// Log it
							// so we can see why it happened
						}
					}
				});
		builder.create().show();
	}
	
	/**
	 * Start the conversation activity with the given friend and send the
	 * selected song as an intent so the activity can create a message with it
	 * 
	 * @param song
	 * @param friend
	 */
	private void sendLyricooToFriend(final Song song, User friend) {
		// convert to json to make it easy to pass to the conversation activity

		String contactAsJson = Utility.toJson(friend);
		String songAsJson = Utility.toJson(song);

		Intent i = new Intent(mContext, ConversationActivity.class);
		i.putExtra("contact", contactAsJson);
		i.putExtra("song", songAsJson);
		startActivity(i);
	}

	// The play button only shows when a song has been selected. It defaults to
	// saying play, and switches to pause when music is playing
	public void playClicked(View v) {
		// pause if music is playing and update button to play
		if (mPlayer.isPlaying()) {
			mPlayer.pause();
			mPlayButton.setText("Play");
		}

		// else start playing
		else {
			// play the last selected song if available
			if (mSelectedSong != null) {
				mPlayButton.setText("Pause");
				boolean success = mPlayer
						.play(new MediaPlayer.OnCompletionListener() {
							// listener for when song has finished playing
							@Override
							public void onCompletion(MediaPlayer mp) {
								// Change pause button to play when song ends
								mPlayButton.setText("Play");
							}
						});

				// if something went wrong clear the song and make the user pick
				// it again
				if (!success) {
					hideSongOptions();
				}
			}
		}
	}

	private void playSong(Song song) {
		// show loading icon until song is ready to play
		mSongProgress.setVisibility(View.VISIBLE);
		mPlayButton.setVisibility(View.INVISIBLE);

		mPlayer.loadSongFromUrl(song.getUrl(),
		// listener for when song has loaded
				new MediaPlayer.OnPreparedListener() {
					@Override
					public void onPrepared(MediaPlayer mp) {
						// hide loading bar and show play button
						mSongProgress.setVisibility(View.GONE);
						mPlayButton.setVisibility(View.VISIBLE);

						// Change play button to pause
						mPlayButton.setText("Pause");

						mPlayer.play(new MediaPlayer.OnCompletionListener() {
							// listener for when song has finished playing
							@Override
							public void onCompletion(MediaPlayer mp) {
								// Change pause button to play
								mPlayButton.setText("Play");
							}
						});
					}
				});
	}

	private void showSongOptions() {
		mSongOptions.setVisibility(View.VISIBLE);

		// Make sure the play button is reset
		mPlayButton.setText("Play");

		// make sure the progress bar is hidden and play button is showing
		mSongProgress.setVisibility(View.GONE);
		mPlayButton.setVisibility(View.VISIBLE);

		// if there is a selected song update the song text
		if (mSelectedSong != null) {
			mSongTitle.setText(mSelectedSong.getTitle());
		}
	}

	private void hideSongOptions() {
		// stop any music that is playing
		mPlayer.stop();

		// reset the selected song
		mSelectedSong = null;
		// Reset play button
		mPlayButton.setText("Play");
		// delete title text
		mSongTitle.setText("");

		// Hide the layout
		mSongOptions.setVisibility(View.GONE);
	}

	private void displaySongs(ArrayList<Song> songs) {
		final SongListAdapter adapter = new SongListAdapter(mContext, songs);
		mSongListView.setAdapter(adapter);

		// When a song is clicked play it
		mSongListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				Song song = (Song) adapter.getItem(position);
				mSelectedSong = song;

				// show the play button, title of selected song, and option to
				// send the
				// song
				showSongOptions();
				playSong(song);
			}
		});
	}
}
