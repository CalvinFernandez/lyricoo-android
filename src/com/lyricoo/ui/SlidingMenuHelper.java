package com.lyricoo.ui;

import java.util.ArrayList;

import android.app.Activity;
import android.support.v4.widget.DrawerLayout;
import android.widget.ListView;

import com.lyricoo.R;

public class SlidingMenuHelper {

	/**
	 * Get a list of the items to place in the sliding menu
	 * 
	 * @return An arraylist of objects to add to the sliding menu
	 */
	public static ArrayList<SlidingMenuItem> getMenuEntries() {
		ArrayList<SlidingMenuItem> items = new ArrayList<SlidingMenuItem>();

		items.add(new SlidingMenuItem(R.drawable.abc_ic_search, "Messages"));
		items.add(new SlidingMenuItem(R.drawable.abc_ic_search, "Lyricoos"));
		items.add(new SlidingMenuItem(R.drawable.abc_ic_search, "Friends"));
		items.add(new SlidingMenuItem(R.drawable.abc_ic_search, "Settings"));

		return items;
	}

	public static void addMenuToActivity(Activity activity) {
		DrawerLayout mDrawerLayout = (DrawerLayout) activity
				.findViewById(R.id.drawer_layout);

		// not all activities have a drawer, eg login/signup
		if (mDrawerLayout != null) {
			ListView mDrawerList = (ListView) activity
					.findViewById(R.id.sliding_menu_list);

			// Set the adapter for the list view
			mDrawerList.setAdapter(new SlidingMenuAdapter(activity,
					SlidingMenuHelper.getMenuEntries()));
		}
	}
}
