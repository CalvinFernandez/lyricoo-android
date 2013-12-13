package com.lyricoo.music;

import java.util.ArrayList;

import com.lyricoo.R;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class CategoryAdapter extends ArrayAdapter<Category> {
	
	public CategoryAdapter(Context c, int resource, ArrayList<Category> categories) {
		super(c, resource, categories);
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		LayoutInflater inflater = (LayoutInflater) getContext()
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		
		Category category = getItem(position);
		
		View gridItem = inflater.inflate(R.layout.category_grid_item, parent, false);
		TextView name = (TextView) gridItem.findViewById(R.id.name);
		
		ImageView thumb = (ImageView) gridItem.findViewById(R.id.category_thumb_image);
		thumb.setImageResource(mThumbIds[position % mThumbIds.length]);
		
		name.setText(category.name());
		return gridItem;
	}
	
	public Integer[] mThumbIds = {
		R.drawable.flirty, R.drawable.loveyou, 
		R.drawable.missyou, R.drawable.getiton,
		R.drawable.outtatown, R.drawable.raunchy,
		R.drawable.suck, R.drawable.rock,
		R.drawable.birthday, R.drawable.fuckedup,
		R.drawable.apology, R.drawable.friday,
		R.drawable.jock, R.drawable.booze,
		R.drawable.its420, R.drawable.lastnight,
		R.drawable.selfie, R.drawable.bro,
		R.drawable.help, R.drawable.hangin
	};

}
