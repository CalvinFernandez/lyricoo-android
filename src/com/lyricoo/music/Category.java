package com.lyricoo.music;

import org.json.JSONException;
import org.json.JSONObject;

public class Category {
	private Integer mId;
	private String mName;
	
	public Category(Integer id, String title) {
		mId = id;
		mName = title;
	}
	
	public Category(JSONObject json) {
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
	}
	
	public String name() {
		return mName;
	}
	
	public int id() {
		return mId;
	}
	
}
