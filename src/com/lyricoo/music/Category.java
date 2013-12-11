package com.lyricoo.music;

import org.json.JSONException;
import org.json.JSONObject;

import com.lyricoo.api.LyricooModel;

public class Category extends LyricooModel {
	
	private Integer mId;
	private String mName;
	private boolean mCached = false;
	
	private static String baseUrl = "categories";
	
	public Category(Integer id, String title) {
		super();
		mId = id;
		mName = title;
	}
	
	public Category(JSONObject json) {
		super();
		try {
			mId = json.getInt("id");
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		try {
			mName = json.getString("name");
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		setBaseUrl(baseUrl + "/" + mId);
	}
	
	public String name() {
		return mName;
	}
	
	public int id() {
		return mId;
	}
	
	public boolean isequal(Category category) {
		if (category.mId != null && mId != null) {
			if (category.mId == mId) {
				return true;
			} else {
				return false;
			}
		} else if (mName != null) {
			return mName.equals(category.name());
		} else {
			return false;
		}
	}
	
	public boolean isCached() {
		return mCached;
	}
	
	public void setCached(boolean cached) {
		mCached = cached;
	}
}
