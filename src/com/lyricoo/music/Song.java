package com.lyricoo.music;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.lyricoo.api.LyricooApi;

/** This class encapsulates the song model of the backend
 *
 */
public class Song {
	private int mId;
	private String mTitle;
	private String mArtist;
	private String mPath;
	private Category mCategory;
	private int mCategoryId;
	
	public Song(int id, String title, String artist, String path,
			Category category) {
		super();
		this.mId = id;
		this.mTitle = title;
		this.mArtist = artist;
		this.mPath = path;
		this.mCategory = category;
	}
	
	public Song(JSONObject json) {
		try {
			mId = json.getInt("id");
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		try {
			mTitle = json.getString("title");
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		try {
			mArtist = json.getString("artist");
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		// in songs the key is "path" and in messages the key is "url"... TODO: Make this consistent
		try {
			mPath = json.getString("path");
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		try {
			mCategoryId = json.getInt("category_id");
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		// In some cases the JSON will include a key for the category with detailed category info
		JSONObject category = null;
		try {
			category = json.getJSONObject("category");
			mCategory = new Category(category);
			
		} catch (Exception e) {
			// Will get called if there is no category key. In this case we just have category_id
		}
	}

	public int getId() {
		return mId;
	}

	public String getTitle() {
		return mTitle;
	}

	public String getArtist() {
		return mArtist;
	}

	/** The http location of the song for streaming
	 * 
	 * @return
	 */
	public String getUrl() {
		return LyricooApi.BASE_URL + mPath;
	}

	public Category getCategory() {
		return mCategory;
	}
	
	/** 
	 * Turn a JSONArray of songs from the server into an ArrayList of Songs
	 * @param json
	 * @return
	 */
	public static ArrayList<Song> parseJsonArray(JSONArray json){
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
	

	public boolean equals(Object obj) {
        if (obj == null){
			return false;
        }
            
        if (obj == this){
        	return true;
        }
            
        if (!(obj instanceof Song)){
        	return false;
        }
            

        // songs are equal if they have the same id
        Song song = (Song) obj;
        return mId == song.getId();
    }
	
	public int hashCode() {
		// base the hashcode on the song id
        return Integer.valueOf(mId).hashCode();
    }
	
	
	
	
	
}
