package com.lyricoo.music;

import java.util.ArrayList;

import org.apache.http.Header;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;

import com.lyricoo.LyricooActivity;
import com.lyricoo.R;
import com.lyricoo.Utility;
import com.lyricoo.messages.ConversationActivity;
import com.lyricoo.music.MusicManager.MusicHandler;
import com.lyricoo.ui.SlidingMenuHelper;

public class CategoriesActivity extends LyricooActivity {
	private Context mContext;
	private ArrayList<Category> mCategories;

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
				mCategories = categories;

				gridview.setAdapter(new CategoryAdapter(mContext,
						R.layout.category_grid_item, categories));

				gridview.setOnItemClickListener(new OnItemClickListener() {

					@Override
					public void onItemClick(AdapterView<?> parent, View v,
							int position, long id) {
						Category category = mCategories.get(position);
						Intent i = new Intent(mContext, CategoryActivity.class);
						
						i.putExtra("category", Utility.toJson(category));
						i.putExtra("position", position);
						
						startActivity(i);
					}

				});
			}

			@Override
			public void onFailure(int statusCode, Header[] headers,
					String responseBody, Throwable e) {
				String toast = "Error retrieving categories";
				Utility.makeBasicToast(mContext, toast);
			}

		});

	}
}
