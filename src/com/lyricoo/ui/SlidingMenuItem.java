package com.lyricoo.ui;

/** 
 * A basic class for holding the drawable resource and the label text
 * for an entry in the sliding menu
 *
 */
public class SlidingMenuItem {
	private int mIconResource;
	private String mLabel;
	
	public SlidingMenuItem(int iconResource, String label) {
		super();
		this.mIconResource = iconResource;
		this.mLabel = label;
	}
	
	public int getIcon(){
		return mIconResource;
	}
	
	public String getLabel(){
		return mLabel;
	}
	

}
