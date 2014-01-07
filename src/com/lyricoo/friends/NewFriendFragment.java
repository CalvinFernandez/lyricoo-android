package com.lyricoo.friends;

import com.lyricoo.R;
import com.lyricoo.session.Session;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

public class NewFriendFragment extends Fragment {
	private EditText new_friend_info;
	private Button add_friend;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		//TODO: Turn off drawer slider
		View newFriendView = inflater.inflate(R.layout.new_friend_fragment, container, false);
		new_friend_info = (EditText) newFriendView.findViewById(R.id.new_friend_info);
		add_friend = (Button) newFriendView.findViewById(R.id.add_friend);
		
		add_friend.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				String info = new_friend_info.getText().toString();
				Session.getFriendManager().addFriend(info);
			}
			
		});
		
		return newFriendView;
	}
}
