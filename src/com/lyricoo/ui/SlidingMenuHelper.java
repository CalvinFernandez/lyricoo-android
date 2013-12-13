package com.lyricoo.ui;

import java.util.ArrayList;

import android.content.Intent;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

import com.lyricoo.LyricooActivity;
import com.lyricoo.R;
import com.lyricoo.friends.FriendsActivity;
import com.lyricoo.messages.InboxActivity;
import com.lyricoo.music.CategoriesActivity;
import com.lyricoo.music.LyricooSelectionActivity;
import com.lyricoo.session.SettingsActivity;

public class SlidingMenuHelper {
	// TODO: Hide contextual action buttons on drawer show
	// TODO: Show app name in action bar on drawer show
	// TODO: Drawer tutorial
	// TODO: Highlight activity name when drawer opens in that activity
	// TODO: Show messages count next to Messages label
	// TODO: Make actionbar icon trigger drawer
	// TODO: Add actionbar icon to indicate drawer

	/**
	 * Get a list of the items to place in the sliding menu
	 * 
	 * @return An arraylist of objects to add to the sliding menu
	 */
	private static ArrayList<SlidingMenuItem> getMenuEntries() {
		ArrayList<SlidingMenuItem> items = new ArrayList<SlidingMenuItem>();

		items.add(new SlidingMenuItem(R.drawable.ic_action_unread, "Messages",
				InboxActivity.class));
		items.add(new SlidingMenuItem(R.drawable.ic_action_play, "Lyricoos",
				CategoriesActivity.class));
		items.add(new SlidingMenuItem(R.drawable.ic_action_person, "Friends",
				FriendsActivity.class));
		items.add(new SlidingMenuItem(R.drawable.ic_action_settings,
				"Settings", SettingsActivity.class));

		return items;
	}

	/**
	 * Adds a fully customized sliding menu to the activity. The activity xml
	 * must be setup for a drawerlayout, otherwise nothing is done.
	 * 
	 * @param activity
	 *            The activity to add the menu to
	 */
	public static void addMenuToActivity(final LyricooActivity activity) {
		final DrawerLayout drawerLayout = (DrawerLayout) activity
				.findViewById(R.id.drawer_layout);

		// make sure this activity actually has a drawer layout
		if (drawerLayout == null) {
			return;
		}

		final ListView drawerList = (ListView) activity
				.findViewById(R.id.sliding_menu_list);

		// Set the adapter for the list view
		drawerList.setAdapter(new SlidingMenuAdapter(activity,
				SlidingMenuHelper.getMenuEntries()));

		addClickListener(activity, drawerList);
		addDrawerListener(activity, drawerLayout);
	}

	private static void addClickListener(final LyricooActivity activity,
			final ListView drawerList) {
		// Add click listener to change activities when an item is clicked
		drawerList.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View v,
					int position, long id) {
				SlidingMenuItem item = (SlidingMenuItem) parent
						.getItemAtPosition(position);

				drawerList.setItemChecked(position, true);

				// load the selected activity
				activity.startActivity(new Intent(activity, item
						.getActivityToStart()));

			}
		});
	}

	/**
	 * Add drawer listener to listen for open and close events
	 * 
	 * @param activity
	 * @param drawerLayout
	 */
	private static void addDrawerListener(final LyricooActivity activity,
			DrawerLayout drawerLayout) {
		// interaction
		// between the action bar and drawer. To do this, the actionbar
		// needs a drawer icon
		drawerLayout.setDrawerListener(new DrawerLayout.DrawerListener() {
			private String title = "Lyricoo";

			/**
			 * Called when a drawer has settled in a completely closed state.
			 */
			public void onDrawerClosed(View view) {
				// change title back to activity name
				activity.getSupportActionBar().setTitle(title);
			}

			/** Called when a drawer has settled in a completely open state. */
			public void onDrawerOpened(View drawerView) {
				// When the drawer is opened change the action bar text to
				// the app name. Remember the old title so we can change it back
				// when the drawer closes
				title = activity.getSupportActionBar().getTitle().toString();
				activity.getSupportActionBar().setTitle("Lyricoo");

				// TODO: Need to close contextual action views. Normally
				// this is
				// done with invalidateOptionsMenu(), but that is only
				// available in API 11+
			}

			@Override
			public void onDrawerSlide(View arg0, float arg1) {

			}

			@Override
			public void onDrawerStateChanged(int arg0) {

			}

		});
	}

}
