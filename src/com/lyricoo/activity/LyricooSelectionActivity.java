package com.lyricoo.activity;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONObject;

import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.lyricoo.Conversation;
import com.lyricoo.LyricooApp;
import com.lyricoo.LyricooPlayer;
import com.lyricoo.R;
import com.lyricoo.Session;
import com.lyricoo.User;
import com.lyricoo.Utility;

import com.lyricoo.Song;
import com.lyricoo.api.LyricooAPI;
import com.lyricoo.api.LyricooApiResponseHandler;

import android.media.MediaPlayer;
import android.os.Bundle;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.RelativeLayout;
import android.widget.TextView;

/**
 * List all available songs and allow playback on click
 * 
 * 
 */
public class LyricooSelectionActivity extends Activity {
	// Request code when launched for result
	public static final int SELECT_LYRICOO_REQUEST = 0;

	private ArrayList<Song> mSongs;
	private ArrayList<String> mCategories;
	private ProgressBar mProgress;
	private Context mContext;
	private LyricooApp mApp;
	private LyricooPlayer mPlayer;
	private ListView mCategoryList;
	private ListView mSongList;
	private Button mCategoryButton;

	// The song options are shown at the bottom of the screen when a song is
	// selected. The user can play or pause the song, see the song title, and
	// choose to send it to a friend
	private RelativeLayout mSongOptions;
	private Button mPlayButton;
	private TextView mSongTitle;
	private ProgressBar mSongProgress;

	// The last song the user clicked
	private Song mSelectedSong;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_lyricoo_selection);

		mContext = this;
		mApp = (LyricooApp) getApplication();
		mPlayer = new LyricooPlayer(this);

		// Get resouces to use later
		mProgress = (ProgressBar) findViewById(R.id.progress);
		mCategoryList = (ListView) findViewById(R.id.category_list);
		mSongList = (ListView) findViewById(R.id.song_list);
		mCategoryButton = (Button) findViewById(R.id.category_button);
		mSongOptions = (RelativeLayout) findViewById(R.id.song_options);
		mPlayButton = (Button) findViewById(R.id.play_button);
		mSongTitle = (TextView) findViewById(R.id.song_title);
		mSongProgress = (ProgressBar) findViewById(R.id.song_progress);

		// load and display songs
		loadSongs();

	}

	@Override
	protected void onPause() {
		super.onPause();
		// Stop the music if the activity looses focus
		mPlayer.stop();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();

		// Release player
		mPlayer.destroy();
	}

	private void loadSongs() {
		// load song list
		// TODO: Cache songs instead of downloading every time
		LyricooAPI.get("songs/all", null, new JsonHttpResponseHandler() {
			@Override
			public void onSuccess(JSONArray json) {
				// parse json into songs
				mSongs = Song.parseJsonArray(json);

				// get list of categories from songs
				buildCategoryList();

				// hide progress bar
				mProgress.setVisibility(View.GONE);

				// Show the songs to the user
				displayCategories();
			}

			@Override
			public void onFailure(Throwable error, JSONObject json) {
				// TODO: Handle failure
				Log.v("Songs", error.getMessage());
			}
		});
	}

	/**
	 * Populate the local list of categories by looking at all the songs.
	 */
	protected void buildCategoryList() {
		// TODO: Cache this list locally. Also, is it more efficient to make a
		// call to categories/all?
		mCategories = new ArrayList<String>();

		// go through each song and add it's category to the list if it hasn't
		// been added yet
		for (Song song : mSongs) {
			String category = song.getCategory();
			if (!mCategories.contains(category)) {
				mCategories.add(category);
			}
		}
	}

	protected void displayCategories() {
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(mContext,
				R.layout.category_list_item, mCategories);

		mCategoryList.setAdapter(adapter);

		mCategoryList.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				// display songs from this category
				String category = mCategories.get(position);
				ArrayList<Song> songs = getSongsFromCategory(category);
				displaySongs(songs);
			}
		});

		// make sure songs list and button is hidden
		mSongList.setVisibility(View.GONE);
		mCategoryButton.setVisibility(View.GONE);

		// make sure categories are visible
		mCategoryList.setVisibility(View.VISIBLE);
	}

	protected ArrayList<Song> getSongsFromCategory(String category) {
		// TODO: More efficient way than iterating through the whole song list
		// every time
		ArrayList<Song> songs = new ArrayList<Song>();

		// go through all songs and pick out ones from the given category
		for (Song song : mSongs) {
			if (category.equals(song.getCategory())) {
				songs.add(song);
			}
		}
		return songs;
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.lyricoo_selection, menu);
		return true;
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

	private void displaySongs(ArrayList<Song> songs) {
		final SongListAdapter adapter = new SongListAdapter(mContext, songs);
		mSongList.setAdapter(adapter);

		// When a song is clicked play it
		mSongList.setOnItemClickListener(new OnItemClickListener() {

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

		// make sure categories are hidden
		mCategoryList.setVisibility(View.GONE);

		// make sure songs list and button is visible
		mSongList.setVisibility(View.VISIBLE);
		mCategoryButton.setVisibility(View.VISIBLE);

		// set category button to show name of this category.
		// Get category name from the first song in the song list
		mCategoryButton.setText(songs.get(0).getCategory());

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
		// load friends
		Session.currentUser().get("friends", new LyricooApiResponseHandler() {
			@Override
			public void onSuccess(Object responseJson) {
				JSONArray json = (JSONArray) responseJson;
				// parse json
				final ArrayList<User> friends = User.parseUserJsonArray(json);
				
				// get list of just friend names to show in dialog
				ArrayList<String> names = new ArrayList<String>();
				for(User friend : friends){
					names.add(friend.getUsername());
				}
				
				// if the user doesn't have any friends show them a different dialog
				if(names.isEmpty()){
					
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
						.setItems(namesArray,
								new DialogInterface.OnClickListener() {
									// handle clicking on an option
									public void onClick(DialogInterface dialog,
											int which) {
										// get the friend that was selected and
										// pass the selected song to the
										// conversation with that friend
										try {
											User friend = friends.get(which);
											sendLyricooToFriend(mSelectedSong,
													friend);
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

			@Override
			public void onFailure(int statusCode, String responseBody, Throwable error) {
				// TODO: Handle failure
			}
		});

	}

	/** Start the conversation activity with the given friend
	 * and send the selected song as an intent so the activity can create
	 * a message with it
	 * @param song
	 * @param friend
	 */
	private void sendLyricooToFriend(final Song song, User friend) {
		// Retrieve the conversation with this friend so we can load it
		// TODO: Cleaner method for getting conversation
		RequestParams params = new RequestParams();
		params.put("contact_id", Integer.toString(friend.getUserId()));
		// TODO: Show loading dialog
		Session.currentUser().get("messages", params,
				new LyricooApiResponseHandler() {
					@Override
					public void onSuccess(Object responseJson) {
						JSONObject json = (JSONObject) responseJson;
						
						ArrayList<Conversation> conversations = Conversation
								.parseMessagesJson(json);

						// should be only one conversation in the list						
						Conversation conversation = conversations.get(0);
						
						// convert to json to make it easy to pass to the conversation activity
						String conversationAsJson = Utility.toJson(conversation);
						String songAsJson = Utility.toJson(song);
						
						Intent i = new Intent(mContext,
								ConversationActivity.class);
						i.putExtra("conversation", conversationAsJson);
						i.putExtra("song", songAsJson);
						startActivity(i);
					}

					@Override
					public void onFailure(int statusCode, String responseBody, Throwable error) {
						// TODO: Handle failure
					}
				});

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

	// Called when the category button is clicked
	public void showCategoriesClicked(View v) {
		hideSongOptions();
		displayCategories();
	}

}
