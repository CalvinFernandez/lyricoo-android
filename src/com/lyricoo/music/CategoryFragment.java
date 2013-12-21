package com.lyricoo.music;

import java.util.ArrayList;

import org.apache.http.Header;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView.ScaleType;

import com.lyricoo.R;
import com.lyricoo.Utility;
import com.lyricoo.friends.FriendManager.OnFriendSelectedListener;
import com.lyricoo.messages.ConversationActivity;
import com.lyricoo.music.MusicManager.MusicHandler;
import com.lyricoo.session.Session;
import com.lyricoo.session.User;
import com.lyricoo.ui.PlayButton;

public class CategoryFragment extends Fragment {
	// TODO: Save state of selected song when changing fragments
	
	private ListView mSongListView;
	private Song mSelectedSong;
	private Category mCategory;

	/*
	 * Options bar at bottom of screen
	 */
	private RelativeLayout mSongOptions;
	private PlayButton mPlayButton;
	private TextView mSongTitle;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		ViewGroup rootView = (ViewGroup) inflater.inflate(
				R.layout.fragment_category, container, false);

		// get resources
		mSongOptions = (RelativeLayout) rootView
				.findViewById(R.id.song_options);
		mPlayButton = (PlayButton) rootView.findViewById(R.id.play_button);
		mSongTitle = (TextView) rootView.findViewById(R.id.song_title);
		mSongListView = (ListView) rootView
				.findViewById(R.id.category_song_list);

		// set listener for play button
		mPlayButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				mPlayButton.toggle();
			}
		});

		// listener for send button
		ImageView sendButton = (ImageView) rootView.findViewById(R.id.send_button);
		sendButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				sendClicked(v);
			}
		});

		// get which category we are supposed to show
		String categoryJson = getArguments().getString("category");
		mCategory = Utility.fromJson(categoryJson, Category.class);

		// set the category image
		ImageView categoryImage = new ImageView(getActivity());
		categoryImage.setImageResource(mCategory.photo());
		categoryImage.setScaleType(ScaleType.CENTER_CROP);

		mSongListView.addHeaderView(categoryImage);

		// load the songs in this category
		// TODO: Could show loading progress while songs load. Otherwise it's
		// just a blank screen
		MusicManager.findSongsByCategory(mCategory, new MusicHandler() {

			@Override
			public void onSuccess(ArrayList<Song> songs,
					ArrayList<Category> categories) {
				displaySongs(songs);
			}

			@Override
			public void onFailure(int statusCode, Header[] headers,
					String responseBody, Throwable e) {
				String toast = "Error retrieving songs";
				Utility.makeBasicToast(getActivity(), toast);

			}

		});

		return rootView;
	}

	// The button to send this lyricoo to a friend triggers this call
	public void sendClicked(View v) {
		// We handle this differently depending on how the activity was
		// launched. If launched for result we return a result intent and
		// finish(). Otherwise we let the user pick which friend to send to

		// getCallingActivity is non null if this activity was started for a
		// result
		if (getActivity().getCallingActivity() != null) {
			// Return the selected lyricoo as a result
			Intent returnIntent = new Intent();
			returnIntent.putExtra("lyricoo", Utility.toJson(mSelectedSong));
			getActivity().setResult(Activity.RESULT_OK, returnIntent);
			getActivity().finish();
		}

		// otherwise the user hasn't yet picked somebody to send this lyricoo
		// too. Show a list of friends to send to
		else {
			// Show friends list and so user can send song to a friend
			Session.getFriendManager().showFriendPicker(getActivity(),
					"Send Lyricoo to a friend", new OnFriendSelectedListener() {

						@Override
						public void onFriendSelected(User friend) {
							sendLyricooToFriend(mSelectedSong, friend);
						}

					});
		}
	}

	/**
	 * Start the conversation activity with the given friend and send the
	 * selected song as an intent so the activity can create a message with it
	 * 
	 * @param song
	 * @param friend
	 */
	private void sendLyricooToFriend(final Song song, User friend) {
		// convert to json to make it easy to pass to the conversation activity

		String contactAsJson = Utility.toJson(friend);
		String songAsJson = Utility.toJson(song);

		Intent i = new Intent(getActivity(), ConversationActivity.class);
		i.putExtra("contact", contactAsJson);
		i.putExtra("song", songAsJson);
		startActivity(i);
	}

	private void showSongOptions() {
		mSongOptions.setVisibility(View.VISIBLE);

		// if there is a selected song update the song text
		if (mSelectedSong != null) {
			mSongTitle.setText(mSelectedSong.getTitle());
		}
	}

	private void displaySongs(ArrayList<Song> songs) {
		final SongListAdapter adapter = new SongListAdapter(getActivity(),
				songs);
		mSongListView.setAdapter(adapter);

		// When a song is clicked play it
		mSongListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				// need to subtract 1 from position because the first item is
				// the header
				Song song = (Song) adapter.getItem(position - 1);
				mSelectedSong = song;

				// show the play button, title of selected song, and option to
				// send the song. Start playing song.
				showSongOptions();
				mPlayButton.setSong(song);
				mPlayButton.play();
			}
		});
	}

	@Override
	public void setUserVisibleHint(boolean isVisibleToUser) {
		super.setUserVisibleHint(isVisibleToUser);

		// Make sure that we are currently visible
		if (this.isVisible()) {
			// If we are becoming invisible, then...
			if (!isVisibleToUser) {
				mPlayButton.stop();
			}
		}
	}
	
	@Override
	public void onPause(){
		super.onPause();
		mPlayButton.stop();
	}

	@Override
	public void onDestroyView() {
		super.onDestroyView();

		// TODO: Clean up any music stuff to save memory
	}

}
