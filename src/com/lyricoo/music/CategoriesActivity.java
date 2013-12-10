package com.lyricoo.music;

import java.util.ArrayList;

import org.apache.http.Header;

import android.content.Context;
import android.os.Bundle;
import android.widget.GridView;

import com.lyricoo.LyricooActivity;
import com.lyricoo.R;
import com.lyricoo.music.MusicManager.MusicHandler;
import com.lyricoo.ui.SlidingMenuHelper;

public class CategoriesActivity extends LyricooActivity {
	private Context mContext;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_categories);
		final GridView gridview = (GridView) findViewById(R.id.categories_grid);
		mContext = this;
		SlidingMenuHelper.addMenuToActivity(this);
		
		MusicManager.getAllCategories(new MusicHandler() {

			@Override
			public void onSuccess(ArrayList<Song> songs,
					ArrayList<Category> categories) {
				
				gridview.setAdapter(new CategoryAdapter(mContext, 
						R.layout.category_grid_item, categories));
			}

			@Override
			public void onFailure(int statusCode, Header[] headers,
					String responseBody, Throwable e) {
				// TODO Auto-generated method stub
				
			}
			
		});
		
		
	}
}
