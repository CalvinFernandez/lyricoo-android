package com.lyricoo.ui;

import java.util.ArrayList;

import com.lyricoo.R;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class SlidingMenuAdapter extends ArrayAdapter<SlidingMenuItem> {

	public SlidingMenuAdapter(Context context, int resource, ArrayList<SlidingMenuItem> items) {
	    super(context, resource, items);
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {

	    View v = convertView;

	    if (v == null) {

	        LayoutInflater vi;
	        vi = LayoutInflater.from(getContext());
	        v = vi.inflate(R.layout.sliding_menu_item, null);

	    }

	    SlidingMenuItem item = getItem(position);

	    TextView label = (TextView) v.findViewById(R.id.label);
	    ImageView icon = (ImageView) v.findViewById(R.id.icon);
	    
	    label.setText(item.getLabel());
	    icon.setImageResource(item.getIcon());

	    return v;
	}
}
