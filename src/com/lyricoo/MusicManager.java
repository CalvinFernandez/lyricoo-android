package com.lyricoo;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;

import com.loopj.android.http.JsonHttpResponseHandler;
import com.lyricoo.api.LyricooApi;
import com.lyricoo.music.Song;

public class MusicManager {
	private ArrayList<Song> mSongs;
	private ArrayList<String> mCategories;
	
	public MusicManager() {
		mSongs = new ArrayList<Song>();	// Initialize our data structures
		//mCategories = new ArrayList<String>();
	}
	
	public interface MusicHandler {
		//
		//	Custom handler for asynchronous http events
		//	
		void onSuccess(ArrayList<Song> songs, ArrayList<String> categories);
		void onFailure(Throwable error);
	}
	/** 
	 * Turn a JSONArray of songs from the server into an ArrayList of Songs
	 * @param json
	 * @return
	 */
	public ArrayList<Song> parseJsonArray(JSONArray json){
		ArrayList<Song> result = new ArrayList<Song>();
		int numSongs = json.length();
		for(int i = 0; i < numSongs; i++){
			JSONObject songJson;
			try {
				songJson = json.getJSONObject(i);
				result.add(new Song(songJson));
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}			
		}
		return result;		
	}
	
	public void getAll(final MusicHandler handler) {
		LyricooApi.get("songs/all", null, new JsonHttpResponseHandler() {
			@Override
			public void onSuccess(JSONArray json) {
				//	Add songs to our local storage 
				addSongs(parseJsonArray(json));
				
				//	Call success method with songs and categories
				handler.onSuccess(mSongs, categories());
			}

			@Override
			public void onFailure(Throwable error, JSONObject json) {
				// TODO: Handle failure
				handler.onFailure(error);
				Log.v("Songs: ", error.getMessage());
			}
		});
	}
	
	public ArrayList<String> categories() {
		// Cache me
		// go through each song and add it's category to the list if it hasn't
		// been added yet
		
		mCategories = new ArrayList<String>();
		for (Song song : mSongs) {
			String category = song.getCategory();
			if (!mCategories.contains(category)) {
				mCategories.add(category);
			}
		}
		return mCategories;
	}
	
	private void addSongs(ArrayList<Song> songs) {
		mSongs = songs;
	}
	
	public Song get(int id) {
		return null;
		
	}
}
