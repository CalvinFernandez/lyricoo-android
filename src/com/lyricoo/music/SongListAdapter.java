package com.lyricoo.music;

import java.util.ArrayList;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.lyricoo.R;

import com.lyricoo.messages.Conversation;
import com.lyricoo.messages.Message;

public class SongListAdapter extends BaseAdapter {

	private ArrayList<Song> mSongs;
	private Context mContext;

	public SongListAdapter(Context context, ArrayList<Song> songs) {
		mSongs = songs;
		mContext = context;
	}

	@Override
	public int getCount() {
		return mSongs.size();
	}

	@Override
	public Object getItem(int position) {
		return mSongs.get(position);
	}

	@Override
	public long getItemId(int position) {
		// return the song id
		return mSongs.get(position).getId();
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		LayoutInflater inflater = (LayoutInflater) mContext
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

		// get the song that corresponds to this position
		Song song = mSongs.get(position);

		View rowView = inflater.inflate(R.layout.song_list_item,
				parent, false);

		// Set the song title and artist
		TextView title = (TextView) rowView
				.findViewById(R.id.title);
		title.setText(song.getTitle());
		
		TextView artist = (TextView) rowView
				.findViewById(R.id.artist);
		artist.setText(song.getArtist());

		return rowView;
	}

}