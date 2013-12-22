package com.lyricoo.friends;

import com.lyricoo.R;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class NewFriendFragment extends Fragment {
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		
		View newFriendView = inflater.inflate(R.layout.new_friend_fragment, container, false);
		
		return newFriendView;

	}
}
