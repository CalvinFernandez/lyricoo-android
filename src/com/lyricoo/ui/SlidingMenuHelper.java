package com.lyricoo.ui;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Intent;
import android.support.v4.widget.DrawerLayout;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

import com.lyricoo.R;
import com.lyricoo.friends.FriendsActivity;
import com.lyricoo.messages.MessagesActivity;
import com.lyricoo.music.LyricooSelectionActivity;
import com.lyricoo.session.SettingsActivity;

public class SlidingMenuHelper {
	// TODO: Hide contextual action buttons on drawer show
	// TODO: Show app name in action bar on drawer show
	// TODO: Drawer tutorial
	// TODO: Highlight activity name when drawer opens in that activity
	// TODO: Show messages count next to Messages label

	/**
	 * Get a list of the items to place in the sliding menu
	 * 
	 * @return An arraylist of objects to add to the sliding menu
	 */
	private static ArrayList<SlidingMenuItem> getMenuEntries() {
		ArrayList<SlidingMenuItem> items = new ArrayList<SlidingMenuItem>();

		items.add(new SlidingMenuItem(R.drawable.ic_action_unread, "Messages", MessagesActivity.class));
		items.add(new SlidingMenuItem(R.drawable.ic_action_play, "Lyricoos", LyricooSelectionActivity.class));
		items.add(new SlidingMenuItem(R.drawable.ic_action_person, "Friends", FriendsActivity.class));
		items.add(new SlidingMenuItem(R.drawable.ic_action_settings, "Settings", SettingsActivity.class));

		return items;
	}

	/**
	 * Adds a fully customized sliding menu to the activity. The activity xml
	 * must be setup for a drawerlayout, otherwise nothing is done.
	 * @param activity The activity to add the menu to
	 */
	public static void addMenuToActivity(final Activity activity) {
		final DrawerLayout drawerLayout = (DrawerLayout) activity
				.findViewById(R.id.drawer_layout);

		// not all activities have a drawer, eg login/signup
		if (drawerLayout != null) {
			final ListView drawerList = (ListView) activity
					.findViewById(R.id.sliding_menu_list);

			// Set the adapter for the list view
			drawerList.setAdapter(new SlidingMenuAdapter(activity,
					SlidingMenuHelper.getMenuEntries()));
			
			// Add click listener to change activities when an item is clicked
			drawerList.setOnItemClickListener(new OnItemClickListener() {

				@Override
				public void onItemClick(AdapterView<?> parent, View v,
						int position, long id) {
					SlidingMenuItem item = (SlidingMenuItem) parent.getItemAtPosition(position);
					
					drawerList.setItemChecked(position, true);
				    
				    // load the selected activity
				    activity.startActivity(new Intent(activity, item.getActivityToStart()));
					
				}
			});
		}
	}
}
