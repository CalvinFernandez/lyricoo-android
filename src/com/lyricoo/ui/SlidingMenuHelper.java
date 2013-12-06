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
	private static ArrayList<SlidingMenuItem> getMenuEntries() {
		ArrayList<SlidingMenuItem> items = new ArrayList<SlidingMenuItem>();

		items.add(new SlidingMenuItem(R.drawable.ic_action_unread, "Messages"));
		items.add(new SlidingMenuItem(R.drawable.ic_action_play, "Lyricoos"));
		items.add(new SlidingMenuItem(R.drawable.ic_action_person, "Friends"));
		items.add(new SlidingMenuItem(R.drawable.ic_action_settings, "Settings"));

		return items;
	}

	/**
	 * Adds a fully customized sliding menu to the activity. The activity xml
	 * must be setup for a drawerlayout, otherwise nothing is done.
	 * @param activity The activity to add the menu to
	 */
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
