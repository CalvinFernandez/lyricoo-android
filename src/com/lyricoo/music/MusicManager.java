package com.lyricoo.music;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;

import com.loopj.android.http.JsonHttpResponseHandler;
import com.lyricoo.api.LyricooApi;

public class MusicManager {
	private static Boolean cachedSongs = false;
	private static Boolean cachedCats = false;
	
	private static ArrayList<Song> mSongs = new ArrayList<Song>();
	private static ArrayList<Category> mCategories;
	
	public interface MusicHandler {
		//
		//	Custom handler for asynchronous http events
		//	
		void onSuccess(ArrayList<Song> songs, ArrayList<Category> categories);
		void onFailure(int statusCode, org.apache.http.Header[] headers, 
				java.lang.String responseBody, java.lang.Throwable e);
	}
	/** 
	 * Turn a JSONArray of songs from the server into an ArrayList of Songs
	 * @param json
	 * @return
	 */
	private static ArrayList<Song> parseJsonArray(JSONArray json){
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
	
	private static ArrayList<Category> parseCategories(JSONArray json) {
		ArrayList<Category> result = new ArrayList<Category>();
		int numCats = json.length();
		
		for (int i = 0; i < numCats; i ++) {
			JSONObject catJson;
			try {
				catJson = json.getJSONObject(i);
				result.add(new Category(catJson));
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
		
		return result;
	}
	
	/**
	 * Retrieves all songs. If refresh is true, songs
	 * will be fetched from the server. If refresh is false
	 * songs will be retrieved from cache if possible 
	 * or from server if they don't exist in the cache.
	 * 
	 * @param handler
	 * @param refresh
	 */
	public static void getAll(final MusicHandler handler, final Boolean refresh) {
		if (!cachedSongs || refresh) { 
			LyricooApi.get("songs/all", null, new JsonHttpResponseHandler() {
				@Override
				public void onSuccess(JSONArray json) {
					//	Add songs to our local storage 
					setSongs(parseJsonArray(json));
					cachedSongs = true;
					
					//	Call success method with songs and categories
					handler.onSuccess(mSongs, categories(refresh));
				}
				

				@Override
				public void onFailure(int statusCode, org.apache.http.Header[] headers, 
						java.lang.String responseBody, java.lang.Throwable e) {
					// TODO: Handle failure
					handler.onFailure(statusCode, headers, responseBody, e);
					Log.v("Songs: ", e.getMessage());
				}
			});
		} else {
			handler.onSuccess(mSongs, categories(refresh));
		}
	}
	
	/**
	 * Retrieves all songs from the cache if possible 
	 * and then remotely if the cache has not been set
	 * 
	 * @param handler
	 */
	public static void getAll(final MusicHandler handler) {
		getAll(handler, false);
	}
	
	
	public static ArrayList<Category> categories(Boolean refresh) {
		if (!cachedCats || refresh) { 
			mCategories = new ArrayList<Category>();
			for (Song song : mSongs) {
				Category category = song.getCategory();
				if (!mCategories.contains(category)) {
					mCategories.add(category);
				}
			}
			cachedCats = true;
		}
		return mCategories;
	}
	
	/**
	 * Fetches all the categories from the server and updates mCategories 
	 * with a complete array of categories. 
	 * 
	 * @param handler
	 */
	public static void getAllCategories(final MusicHandler handler) {
		if (cachedCats == true) {
			handler.onSuccess(null, mCategories);
		} else {
			LyricooApi.get("categories", null, new JsonHttpResponseHandler() {
				@Override
				public void onSuccess(JSONArray json) {
					cachedCats = true;
					mCategories = parseCategories(json);
					handler.onSuccess(null, mCategories);
				}

				@Override
				public void onFailure(int statusCode, org.apache.http.Header[] headers, 
						java.lang.String responseBody, java.lang.Throwable e) {
					// TODO: Handle failure
					handler.onFailure(statusCode, headers, responseBody, e);
					Log.v("Categories: ", e.getMessage());
				}
			});
		}
	}
	
	private static void setSongs(ArrayList<Song> songs) {
		mSongs = songs;
	}
	
	public static Song get(int id) {
		// TODO
		return null;		
	}
}
