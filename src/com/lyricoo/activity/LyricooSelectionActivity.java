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
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.AdapterView.OnItemClickListener;

/** List all available songs and allow playback on click
 * 
 *
 */
public class LyricooSelectionActivity extends Activity {
	private ArrayList<Song> mSongs;
	private ProgressBar mProgress;
	private Context mContext;
	private LyricooApp mApp;
	private LyricooPlayer mPlayer;
	
	// TODO: Stop music playing when activity is paused or stopped

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_lyricoo_selection);

		mContext = this;
		mApp = (LyricooApp) getApplication();
		mPlayer = new LyricooPlayer(this);

		// start progress bar to indicate loading
		mProgress = (ProgressBar) findViewById(R.id.songs_loading_progress);

		// load song list
		// TODO: Cache songs instead of downloading every time
		LyricooAPI.get("songs/all", null, new JsonHttpResponseHandler() {
			@Override
			public void onSuccess(JSONArray json) {
				// parse json into songs
				mSongs = Song.parseJsonArray(json);

				// hide progress bar
				mProgress.setVisibility(View.GONE);

				// Show the songs to the user
				displaySongs();
			}

			@Override
			public void onFailure(Throwable error, JSONObject json) {
				// TODO: Handle failure
				Log.v("Songs", error.getMessage());
			}
		});
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.lyricoo_selection, menu);
		return true;
	}

	private void playSong(Song song) {
		System.out.println("Playing song");
		mPlayer.loadSongFromUrl(song.getUrl(),
				// listener for when song has loaded
				new MediaPlayer.OnPreparedListener() {
					@Override
					public void onPrepared(MediaPlayer mp) {
						mPlayer.play(new MediaPlayer.OnCompletionListener() {
							// listener for when song has finished playing
							@Override
							public void onCompletion(MediaPlayer mp) {
								System.out.println("Song finished playing");
								
							}
						});
					}
				});
	}

	private void displaySongs() {
		SongListAdapter adapter = new SongListAdapter(mContext, mSongs);
		ListView list = (ListView) findViewById(R.id.songs_list);
		list.setAdapter(adapter);

		// When a song is clicked play it
		list.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				playSong(mSongs.get(position));
			}
		});
	}

}
