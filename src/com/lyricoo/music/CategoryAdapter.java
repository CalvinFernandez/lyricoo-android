package com.lyricoo.music;

import java.util.ArrayList;

import com.lyricoo.R;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class CategoryAdapter extends BaseAdapter {
	private Context mContext;
	private ArrayList<Category> mCategories;
	
	public CategoryAdapter(Context c, ArrayList<Category> categories) {
		mContext = c;
		mCategories = categories;
	}
	
	@Override
	public int getCount() {
		return mCategories.size();
	}

	@Override
	public Object getItem(int arg0) {
		return mCategories.get(arg0);
	}

	@Override
	public long getItemId(int arg0) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		LayoutInflater inflater = (LayoutInflater) mContext
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		
		Category category = mCategories.get(position);
		
		View gridItem = inflater.inflate(R.layout.category_grid_item, parent, false);
		TextView name = (TextView) gridItem.findViewById(R.id.name);
		name.setText(category.name());
		return gridItem;
	}

}
