package com.lyricoo.activity;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONObject;

import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.lyricoo.Conversation;
import com.lyricoo.LyricooAPI;
import com.lyricoo.LyricooApp;
import com.lyricoo.LyricooPlayer;
import com.lyricoo.R;
import com.lyricoo.Song;

import android.media.MediaPlayer;
import android.os.Bundle;
import android.app.Activity;
import android.content.Context;
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

/**
 * List all available songs and allow playback on click
 * 
 * 
 */
public class LyricooSelectionActivity extends Activity {
	private ArrayList<Song> mSongs;
	private ArrayList<String> mCategories;
	private ProgressBar mProgress;
	private Context mContext;
	private LyricooApp mApp;
	private LyricooPlayer mPlayer;
	private ListView mCategoryList;
	private ListView mSongList;
	private Button mCategoryButton;

	// TODO: Stop music playing when activity is paused or stopped

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

		// load and display songs
		loadSongs();

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
	
	public void showCategoriesClicked(View v){
		displayCategories();
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
		mPlayer.loadSongFromUrl(song.getUrl(),
		// listener for when song has loaded
				new MediaPlayer.OnPreparedListener() {
					@Override
					public void onPrepared(MediaPlayer mp) {
						mPlayer.play(new MediaPlayer.OnCompletionListener() {
							// listener for when song has finished playing
							@Override
							public void onCompletion(MediaPlayer mp) {
								// TODO: Update display play button

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
				playSong(song);
			}
		});

		// make sure categories are hidden
		mCategoryList.setVisibility(View.GONE);

		// make sure songs list and button is visible
		mSongList.setVisibility(View.VISIBLE);
		mCategoryButton.setVisibility(View.VISIBLE);

	}

}
