package com.lyricoo;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONObject;

import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.lyricoo.activity.ContactsActivity;
import com.lyricoo.activity.ConversationActivity;
import com.lyricoo.activity.FriendsListAdapter;
import com.lyricoo.activity.MessageListAdapter;

import android.os.Bundle;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.AdapterView.OnItemClickListener;

public class FriendsActivity extends Activity {
	private ArrayList<User> mFriends;
	private ProgressBar mProgress;
	private Context mContext;
	private LyricooApp mApp;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_friends);
		
		mContext = this;
		mApp = (LyricooApp) getApplication();

		// start progress bar to indicate loading
		mProgress = (ProgressBar) findViewById(R.id.friends_loading_progress);

		// load friends
		RequestParams params = new RequestParams();
		// TODO: Use the id of the logged in user
		LyricooAPI.get("users/45/friends", null, new JsonHttpResponseHandler() {
			@Override
			public void onSuccess(JSONArray json) {
				// parse json into messages
				mFriends = User.parseUserJson(json);
				
				// hide progress bar
				mProgress.setVisibility(View.GONE);				

				// Create adapter for the list view
				FriendsListAdapter adapter = new FriendsListAdapter(mContext, mFriends);
				ListView list = (ListView) findViewById(R.id.friends_list);
				list.setAdapter(adapter);
				

				list.setOnItemClickListener(new OnItemClickListener() {

					@Override
					public void onItemClick(AdapterView<?> parent, View view,
							int position, long id) {
	

					}					
				});
			}

			@Override
			public void onFailure(Throwable error, JSONObject json) {
				// TODO: Handle failure
				Log.v("Messages", error.getMessage());
			}
		});
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.friends, menu);
		return true;
	}
	
	public void addFriend(View v){
		Intent i = new Intent(this, ContactsActivity.class);
		startActivity(i);
	}

}
