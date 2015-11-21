package com.TMS.uni.seg3102final;

import android.content.Context;
import android.graphics.Typeface;
import android.util.DisplayMetrics;
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
        DisplayMetrics metrics = this.context.getResources().getDisplayMetrics();
        float dp = 10f;
        float fpixels = metrics.density * dp;
        int pixels = (int) (fpixels + 0.5f);
        textView.setText(getItem(position));
        textView.setGravity(Gravity.CENTER);
        textView.setTextSize(pixels);
        textView.setTypeface(tf);
        return textView;
    }
}
