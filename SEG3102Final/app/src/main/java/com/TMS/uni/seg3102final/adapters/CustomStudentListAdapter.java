package com.TMS.uni.seg3102final.adapters;

import android.content.Context;
import android.graphics.Typeface;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.TMS.uni.seg3102final.CreateTeam;
import com.TMS.uni.seg3102final.Models.DBItem;
import com.TMS.uni.seg3102final.R;

import java.util.ArrayList;

public class CustomStudentListAdapter extends CustomDBItemAdapter {
    public CustomStudentListAdapter(Context context, int id) {
        super(context, id);
    }

    public CustomStudentListAdapter(Context context, int id, ArrayList<DBItem> items) {
        super(context, id, items);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.student_minus, parent, false);
        }
        RelativeLayout layout = (RelativeLayout) convertView.findViewById(R.id.layout_minus);
        Typeface tf = Typeface.createFromAsset(context.getAssets(), "Roboto-Regular.ttf");
        Button button = (Button) layout.findViewById(R.id.remove_member);
        final DBItem item = getItem(position);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CreateTeam c = (CreateTeam) context;
                c.removeMember(item);
            }
        });
        TextView textView = (TextView) layout.findViewById(R.id.student_name);
        textView.setText(item.getText());
        textView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 15);
        textView.setTypeface(tf);
        return layout;
    }
}
