package com.lyricoo.ui;

import java.util.ArrayList;

import com.lyricoo.R;

public class SlidingMenuSettings {
	
	/**
	 * Get a list of the items to place in the sliding menu
	 * @return An arraylist of objects to add to the sliding menu
	 */
	public static ArrayList<SlidingMenuItem> getMenuEntries(){
		ArrayList<SlidingMenuItem> items = new ArrayList<SlidingMenuItem>();
		
		items.add(new SlidingMenuItem(R.drawable.abc_ic_search, "Item 1"));
		
		return items;
	}

}
