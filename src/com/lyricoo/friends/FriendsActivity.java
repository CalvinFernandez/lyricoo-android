package com.lyricoo.friends;

import java.util.ArrayList;
import java.util.List;

import uk.co.senab.actionbarpulltorefresh.extras.actionbarcompat.PullToRefreshLayout;
import uk.co.senab.actionbarpulltorefresh.library.ActionBarPullToRefresh;
import uk.co.senab.actionbarpulltorefresh.library.listeners.OnRefreshListener;

import android.app.AlertDialog;
import android.app.SearchManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.SearchView.OnQueryTextListener;

import android.view.Menu;
import android.view.MenuItem;
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

public class FriendsActivity extends LyricooActivity implements OnQueryTextListener{
	private ArrayList<User> mFriends;
	private StickyListHeadersListView mList;
	private Context mContext;
	private PullToRefreshLayout mPullToRefreshLayout;

	NewFriendPagerAdapter nFPAdapter;
	ViewPager vPager;
	Menu mMenu;
	DrawerLayout drawerLayout;

	// Our listener for when friends is updated. Don't make this anonymous so it
	// can be removed onDestroy
	private FriendManager.OnFriendsUpdatedListener mFriendListener;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_friends);

		SlidingMenuHelper.addMenuToActivity(this, true);
		drawerLayout = (DrawerLayout) this.findViewById(R.id.drawer_layout);

		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		getSupportActionBar().setHomeButtonEnabled(true);
		mContext = this;

		// get list view
		// mList = (StickyListHeadersListView) findViewById(R.id.friends_list);

		// load and display friends
		// loadFriendsList();

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
		 * // Now find the PullToRefreshLayout to setup mPullToRefreshLayout =
		 * (PullToRefreshLayout) findViewById(R.id.ptr_layout);
		 * 
		 * // Now setup the PullToRefreshLayout
		 * ActionBarPullToRefresh.from(this) // Mark All Children as pullable
		 * .allChildrenArePullable() // Set the OnRefreshListener .listener(new
		 * OnRefreshListener() {
		 * 
		 * @Override public void onRefreshStarted(View view) {
		 * Utility.log("refresh");
		 * 
		 * } }) // Finally commit the setup to our PullToRefreshLayout
		 * .setup(mPullToRefreshLayout);
		 */
		nFPAdapter = new NewFriendPagerAdapter(getSupportFragmentManager());
		vPager = (ViewPager) findViewById(R.id.pager);
		vPager.setAdapter(nFPAdapter);
		vPager.setOnPageChangeListener(new OnPageChangeListener() {

			@Override
			public void onPageScrollStateChanged(int arg0) {
				// TODO Auto-generated method stub

			}

			@Override
			public void onPageScrolled(int arg0, float arg1, int arg2) {
				// TODO Auto-generated method stub

			}

			@Override
			public void onPageSelected(int position) {
				if (position == 0) {
					mMenu.findItem(R.id.action_new_friend).setVisible(true);
					drawerLayout
							.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED);
				} else if (position == 1) {
					mMenu.findItem(R.id.action_new_friend).setVisible(false);
					drawerLayout
							.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
				}
			}

		});
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
		mMenu = menu;
		getMenuInflater().inflate(R.menu.friends, menu);

		SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);

		MenuItem searchItem = (MenuItem) menu.findItem(R.id.search);
		SearchView searchView = (SearchView) MenuItemCompat
				.getActionView(searchItem);

		searchView.setSearchableInfo(searchManager
				.getSearchableInfo(getComponentName()));
		
		searchView.setIconifiedByDefault(true);
		searchView.setOnQueryTextListener(this);
		
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle presses on the action bar items
		switch (item.getItemId()) {
		case R.id.action_new_friend:
			vPager.setCurrentItem(1, true);
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
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

	@Override
	public boolean onQueryTextChange(String text) {
		// TODO Speed this up!
		// There has to be a better way of getting
		// the current fragment.
		FragmentManager fragmentManager = (FragmentManager) getSupportFragmentManager();
		int currentFragment = vPager.getCurrentItem();
		Fragment fragment = fragmentManager
				.getFragments().get(currentFragment);
		
		if (fragment instanceof FriendsFragment) {
			((FriendsFragment) fragment).filter(text);
		} else if (fragment instanceof NewFriendFragment) {
			((NewFriendFragment) fragment).filter(text);
		}
		return false;
	}

	@Override
	public boolean onQueryTextSubmit(String arg0) {
		// TODO Auto-generated method stub
		return false;
	}

}