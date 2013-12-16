package com.lyricoo.music;

import org.json.JSONException;
import org.json.JSONObject;

import com.lyricoo.api.LyricooModel;

public class Category extends LyricooModel {

	private Integer mId;
	private String mName;
	private Integer mPhotoId;

	private boolean mCached = false;

	private static String baseUrl = "categories";

	public Category(Integer id, String title) {
		super();
		mId = id;
		mName = title;
		mPhotoId = CategoryHelper.mapToImage(mId);
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
		mPhotoId = CategoryHelper.mapToImage(mId);
		setBaseUrl(baseUrl + "/" + mId);
	}

	public String name() {
		return mName;
	}

	public int id() {
		return mId;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == null) {
			return false;
		}

		if (obj == this) {
			return true;
		}

		if (!(obj instanceof Category)) {
			return false;
		}

		// songs are equal if they have the same id
		Category category = (Category) obj;

		if (category.mId != null && mId != null) {
			return category.mId == mId;
		} else if (mName != null) {
			return mName.equals(category.name());
		} else {
			return false;
		}
	}
	
	public int photo(){
		return mPhotoId;
	}

	@Override
	public int hashCode() {
		return Integer.valueOf(mId).hashCode();
	}

	public boolean isCached() {
		return mCached;
	}

	public void setCached(boolean cached) {
		mCached = cached;
	}

	public String getName() {
		return mName;
	}
}
