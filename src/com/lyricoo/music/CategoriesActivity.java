package com.lyricoo.music;

import java.util.ArrayList;

import org.apache.http.Header;

import android.os.Bundle;
import android.widget.GridView;

import com.lyricoo.LyricooActivity;
import com.lyricoo.R;
import com.lyricoo.music.MusicManager.MusicHandler;

public class CategoriesActivity extends LyricooActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_categories);
		GridView gridview = (GridView) findViewById(R.id.categories_grid);
		MusicManager.getAllCategories(new MusicHandler() {

			@Override
			public void onSuccess(ArrayList<Song> songs,
					ArrayList<Category> categories) {
				
			}

			@Override
			public void onFailure(int statusCode, Header[] headers,
					String responseBody, Throwable e) {
				// TODO Auto-generated method stub
				
			}
			
		});
		gridview.setAdapter(new CategoryAdapter(this));
		
	}
}
