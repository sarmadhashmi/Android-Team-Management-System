package com.TMS.uni.seg3102final.adapters;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.TMS.uni.seg3102final.Models.DBItem;
import com.TMS.uni.seg3102final.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class CustomDBItemAdapter extends ArrayAdapter {
    LayoutInflater inflater;
    Context context;
    ArrayList<DBItem> items;
    public CustomDBItemAdapter(Context context, int id) {
        super(context, id);
        this.context = context;
        this.items = new ArrayList<DBItem>();
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    public CustomDBItemAdapter(Context context, int id, ArrayList<DBItem> items) {
        super(context, id, items);
        this.items = items;
        this.context = context;
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = inflater.inflate(android.R.layout.simple_list_item_1, parent, false);
        }
        Typeface tf = Typeface.createFromAsset(context.getAssets(), "Roboto-Regular.ttf");
        TextView textView = (TextView) convertView;
        textView.setText(getItem(position).getText());
        textView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 15);
        textView.setTypeface(tf);
        return textView;
    }

    @Override
    public View getDropDownView(int position, View convertView, ViewGroup parent) {
        TextView text = new TextView(context);
        text.setTextColor(Color.BLACK);
        text.setText(items.get(position).getText());
        return text;
    }

    @Override
    public DBItem getItem(int position) {
        return items.get(position);
    }

    public void add(DBItem item) {
        items.add(item);
    }

    public void remove(DBItem item) {
        items.remove(item);
    }

    public int length() {
        return items.size();
    }

    public JSONArray toJSONArray() {
        JSONArray arr = new JSONArray();
        for (DBItem item: items) {
            arr.put(item.getObj());
        }
        return arr;
    }

    public boolean contains(DBItem item) {
        return items.contains(item);
    }
}
