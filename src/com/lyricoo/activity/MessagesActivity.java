package com.lyricoo.activity;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.lyricoo.Conversation;
import com.lyricoo.LyricooAPI;
import com.lyricoo.R;


import android.os.Bundle;
import android.app.Activity;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.ProgressBar;

public class MessagesActivity extends Activity {
	private ArrayList<Conversation> mConversations;
	private ProgressBar mProgress;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_messages);

		// start progress bar to indicate loading
		mProgress = (ProgressBar) findViewById(R.id.messages_loading_progress);

		// load messages
		RequestParams params = new RequestParams();
		params.put("id", "45");
		LyricooAPI.get("messages/all", params, new JsonHttpResponseHandler() {
			@Override
			public void onSuccess(JSONArray json) {
				// parse json into messages
				mConversations = Conversation.parseMessagesJson(json);
				
				// hide progress bar
				mProgress.setVisibility(View.GONE);				

				// Create adapter for the list view
				Log.v("Messages", json.toString());
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
		getMenuInflater().inflate(R.menu.messages, menu);
		return true;
	}

}
