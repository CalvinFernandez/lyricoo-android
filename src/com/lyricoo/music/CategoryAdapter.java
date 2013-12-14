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
		TextView name = (TextView) gridItem.findViewById(R.id.category_name);
		
		CategoryGridImage thumb = (CategoryGridImage) gridItem.findViewById(R.id.category_thumb_image);
		thumb.setImageResource(category.photo());
		
		name.setText(category.name());
		return gridItem;
	}

}
