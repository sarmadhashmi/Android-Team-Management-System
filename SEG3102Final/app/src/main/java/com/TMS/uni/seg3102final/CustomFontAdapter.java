package com.TMS.uni.seg3102final;

import android.content.Context;
import android.graphics.Typeface;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

class CustomFontAdapter extends ArrayAdapter<String> {
    Typeface tf;
    LayoutInflater inflater;
    Context context;
    public CustomFontAdapter(Context context, int layoutResourceId, String font) {
        super(context, layoutResourceId);
        this.context = context;
        tf = Typeface.createFromAsset(context.getAssets(), font);
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = inflater.inflate(android.R.layout.simple_list_item_1, parent, false);
        }
        TextView textView = (TextView) convertView;
        textView.setText(getItem(position));
        textView.setGravity(Gravity.CENTER);
        textView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 20);
        textView.setTypeface(tf);
        return textView;
    }
}
