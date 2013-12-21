package com.lyricoo.music;

import java.util.ArrayList;

import org.apache.http.Header;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;

import com.lyricoo.LyricooActivity;
import com.lyricoo.R;
import com.lyricoo.Utility;
import com.lyricoo.music.MusicManager.MusicHandler;

public class CategoryActivity extends LyricooActivity {
	private Category mStartingCategory;
	private ArrayList<Category> mCategories;
	private ViewPager mPager;
	private PagerAdapter mPagerAdapter;
	private Context mContext;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_category);
		mContext = this;

		// show up button
		ActionBar actionBar = getSupportActionBar();
		actionBar.setDisplayHomeAsUpEnabled(true);

		MusicManager.getAllCategories(new MusicHandler() {

			@Override
			public void onSuccess(ArrayList<Song> songs,
					ArrayList<Category> categories) {
				mCategories = categories;
			}

			@Override
			public void onFailure(int statusCode, Header[] headers,
					String responseBody, Throwable e) {
				String toast = "Error retrieving categories";
				Utility.makeBasicToast(mContext, toast);
			}
		});

		int startingCategoryIndex = getIntent().getIntExtra("categoryIndex", 0);

		mPager = (ViewPager) findViewById(R.id.pager);
		mPagerAdapter = new CategoryPagerAdapter(getSupportFragmentManager());
		mPager.setAdapter(mPagerAdapter);
		
		mPager.setCurrentItem(startingCategoryIndex);
	}

	private class CategoryPagerAdapter extends FragmentStatePagerAdapter {
		public CategoryPagerAdapter(FragmentManager fm) {
			super(fm);
		}

		@Override
		public Fragment getItem(int position) {
			Category category = mCategories.get(position);
			
			// update title to category name
			getSupportActionBar().setTitle(category.getName());
			
			CategoryFragment frag = new CategoryFragment();			
			
			// let the fragment know which category they are supposed to show
			Bundle args = new Bundle();
			args.putString("category", Utility.toJson(category));
			frag.setArguments(args);
			
			return frag;
		}

		@Override
		public int getCount() {
			return mCategories.size();
		}
	}
	
	@Override
	public void onBackPressed() {
		if (mPager.getCurrentItem() == 0) {
            // If the user is currently looking at the first step, allow the system to handle the
            // Back button. This calls finish() on this activity and pops the back stack.
            super.onBackPressed();
        } else {
            // Otherwise, select the previous step.
            mPager.setCurrentItem(mPager.getCurrentItem() - 1);
        }
	}

}
