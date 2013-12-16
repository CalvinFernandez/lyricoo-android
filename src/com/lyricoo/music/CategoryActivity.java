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
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;

import com.lyricoo.LyricooActivity;
import com.lyricoo.R;
import com.lyricoo.Utility;
import com.lyricoo.friends.FriendManager.OnFriendSelectedListener;
import com.lyricoo.messages.ConversationActivity;
import com.lyricoo.music.MusicManager.MusicHandler;
import com.lyricoo.session.Session;
import com.lyricoo.session.User;
import com.lyricoo.ui.PlayButton;
import com.lyricoo.ui.SlidingMenuHelper;

public class CategoryActivity extends LyricooActivity {
	private Category mCategory;
	private Context mContext;
	private ListView mSongListView;
	private Song mSelectedSong;

	/*
	 * Options bar at bottom of screen
	 */
	private RelativeLayout mSongOptions;
	private PlayButton mPlayButton;
	private TextView mSongTitle;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_category);
		mContext = this;

		mSongListView = (ListView) findViewById(R.id.category_song_list);

		String jCategory = getIntent().getStringExtra("category");
		// Integer position = getIntent().getIntExtra("position", 0);
		mCategory = Utility.fromJson(jCategory, Category.class);

		setTitle(mCategory.getName());

		ImageView categoryImage = new ImageView(this);
		categoryImage.setImageResource(mCategory.photo());
		categoryImage.setScaleType(ScaleType.CENTER_CROP);

		mSongListView.addHeaderView(categoryImage);

		mSongOptions = (RelativeLayout) findViewById(R.id.song_options);
		mPlayButton = (PlayButton) findViewById(R.id.play_button);
		mSongTitle = (TextView) findViewById(R.id.song_title);

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

	@Override
	protected void onPause() {
		super.onPause();
		// Stop the music if the activity looses focus
		mPlayButton.stop();

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
			Session.getFriendManager().showFriendPicker(mContext,
					"Send Lyricoo to a friend", new OnFriendSelectedListener() {

						@Override
						public void onFriendSelected(User friend) {
							sendLyricooToFriend(mSelectedSong, friend);
						}

					});
		}
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
		mPlayButton.toggle();
	}

	private void showSongOptions() {
		mSongOptions.setVisibility(View.VISIBLE);

		// if there is a selected song update the song text
		if (mSelectedSong != null) {
			mSongTitle.setText(mSelectedSong.getTitle());
		}
	}

	private void displaySongs(ArrayList<Song> songs) {
		final SongListAdapter adapter = new SongListAdapter(mContext, songs);
		mSongListView.setAdapter(adapter);

		// When a song is clicked play it
		mSongListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				// need to subtract 1 from position because the first item is the header
				Song song = (Song) adapter.getItem(position - 1);
				mSelectedSong = song;

				// show the play button, title of selected song, and option to
				// send the song. Start playing song.
				showSongOptions();
				mPlayButton.setSong(song);
				mPlayButton.play();
			}
		});
	}
}
