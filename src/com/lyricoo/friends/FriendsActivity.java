package com.lyricoo.friends;

import java.util.ArrayList;

import uk.co.senab.actionbarpulltorefresh.extras.actionbarcompat.PullToRefreshLayout;
import uk.co.senab.actionbarpulltorefresh.library.ActionBarPullToRefresh;
import uk.co.senab.actionbarpulltorefresh.library.listeners.OnRefreshListener;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import se.emilsjolander.stickylistheaders.StickyListHeadersListView;

import com.lyricoo.LyricooActivity;
import com.lyricoo.R;
import com.lyricoo.Utility;
import com.lyricoo.api.LyricooApiResponseHandler;
import com.lyricoo.messages.ConversationActivity;
import com.lyricoo.session.Session;
import com.lyricoo.session.User;
import com.lyricoo.ui.SlidingMenuHelper;

public class FriendsActivity extends LyricooActivity {
	private ArrayList<User> mFriends;
	private StickyListHeadersListView mList;
	private Context mContext;
	private PullToRefreshLayout mPullToRefreshLayout;
	NewFriendPagerAdapter nFPAdapter;

	// Our listener for when friends is updated. Don't make this anonymous so it
	// can be removed onDestroy
	private FriendManager.OnFriendsUpdatedListener mFriendListener;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_friends);
		SlidingMenuHelper.addMenuToActivity(this, true);
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		getSupportActionBar().setHomeButtonEnabled(true);
		mContext = this;

		// get list view
		//mList = (StickyListHeadersListView) findViewById(R.id.friends_list);

		// load and display friends
		//loadFriendsList();

		// register callback for when friends list is updated
		mFriendListener = new FriendManager.OnFriendsUpdatedListener() {

			@Override
			public void onFriendsUpdated() {
				FriendsListAdapter adapter = (FriendsListAdapter) mList
						.getAdapter();
				adapter.notifyDataSetChanged();
			}
		};
/*
		// Now find the PullToRefreshLayout to setup
		mPullToRefreshLayout = (PullToRefreshLayout) findViewById(R.id.ptr_layout);

		// Now setup the PullToRefreshLayout
		ActionBarPullToRefresh.from(this)
		// Mark All Children as pullable
				.allChildrenArePullable()
				// Set the OnRefreshListener
				.listener(new OnRefreshListener() {

					@Override
					public void onRefreshStarted(View view) {
						Utility.log("refresh");

					}
				})
				// Finally commit the setup to our PullToRefreshLayout
				.setup(mPullToRefreshLayout);
*/
		nFPAdapter = new NewFriendPagerAdapter(getSupportFragmentManager());
		ViewPager vPager = (ViewPager) findViewById(R.id.pager);
		vPager.setAdapter(nFPAdapter);
	}

	@Override
	public void onDestroy() {
		super.onDestroy();

		try {
			Session.getFriendManager().unregisterOnFriendsUpdatedListener(
					mFriendListener);
		} catch (Exception e) {
			// thrown if friend manager if null
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.friends, menu);
		return true;
	}

	// Start an activity where the user can add friends to their list
	public void addFriendClicked(View v) {
		Intent i = new Intent(this, ContactsActivity.class);
		startActivity(i);
	}

	/**
	 * Private adapter for handling new friend fragment. Can just use basic
	 * fragment pager adapter as this is specifically intended for navigating
	 * between two screens (friends list <-> add new friend)
	 * 
	 * @author Lyricoo
	 * 
	 */
	private class NewFriendPagerAdapter extends FragmentPagerAdapter {
		private int FRIENDS_VIEW = 0;
		private int NEW_FRIENDS_VIEW = 1;
		
		
		
		public NewFriendPagerAdapter(FragmentManager fm) {
			super(fm);
		}

		@Override
		public Fragment getItem(int arg0) {
			if (arg0 == FRIENDS_VIEW) {
				return new FriendsFragment();
			} else if (arg0 == NEW_FRIENDS_VIEW) {
				return new NewFriendFragment();
			} else {
				return null;
			}
		}

		@Override
		public int getCount() {
			return 2;
		}

	}

}