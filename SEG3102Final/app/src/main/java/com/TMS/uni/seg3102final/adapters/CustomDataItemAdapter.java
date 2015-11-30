package com.TMS.uni.seg3102final.adapters;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.TextView;

import com.TMS.uni.seg3102final.AcceptNewStudents;
import com.TMS.uni.seg3102final.Models.ListItemModel;
import com.TMS.uni.seg3102final.R;

public class CustomDataItemAdapter extends ArrayAdapter{

    ListItemModel[] modelItems = null;
    Context context;

    public CustomDataItemAdapter(Context context, ListItemModel[] resource) {
        super(context, R.layout.row,resource);
        this.context = context;
        this.modelItems = resource;
    }
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = ((Activity)context).getLayoutInflater();
        convertView = inflater.inflate(R.layout.row, parent, false);
        TextView name = (TextView) convertView.findViewById(R.id.textView1);
        final CheckBox cb = (CheckBox) convertView.findViewById(R.id.checkBox1);
        name.setText(modelItems[position].getName());

        final String label =  name.getText().toString();

        if(modelItems[position].getValue() == 1)
          cb.setChecked(true);
        else
            cb.setChecked(false);

        cb.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {

                if(cb.isChecked())
                {
                    AcceptNewStudents.selectedRequests.put(label, true);
                }
                else
                {
                    AcceptNewStudents.selectedRequests.put(label, false);
                }
            }
        });


        return convertView;
    }
}