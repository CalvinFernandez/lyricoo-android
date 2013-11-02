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

public class LyricooSelectionActivity extends Activity {
	private ArrayList<Song> mSongs;
	private ProgressBar mProgress;
	private Context mContext;
	private LyricooApp mApp;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_lyricoo_selection);

		mContext = this;
		mApp = (LyricooApp) getApplication();

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

				// Create adapter for the list view
				SongListAdapter adapter = new SongListAdapter(mContext, mSongs);
				ListView list = (ListView) findViewById(R.id.songs_list);
				list.setAdapter(adapter);

				// When a message is clicked load the whole conversation
				list.setOnItemClickListener(new OnItemClickListener() {

					@Override
					public void onItemClick(AdapterView<?> parent, View view,
							int position, long id) {
						// Play song
						Song song = mSongs.get(position);
						LyricooPlayer player = new LyricooPlayer(song.getUrl(),
								new MediaPlayer.OnPreparedListener() {

									@Override
									public void onPrepared(MediaPlayer mp) {
										mp.start();
									}
								});
					}
				});
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

}
