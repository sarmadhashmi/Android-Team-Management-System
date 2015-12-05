package com.TMS.uni.seg3102final;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TimePicker;

import com.TMS.uni.seg3102final.tasks.LoginTask;
import com.TMS.uni.seg3102final.tasks.RegisterTask;
import com.TMS.uni.seg3102final.tasks.SetupParametersTask;

public class SetupParameters extends AppCompatActivity {
    public ProgressDialog progress;
    Dialog dialog;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        progress = new ProgressDialog(this);
        dialog  = new Dialog(this);
        dialog.setContentView(R.layout.date_time_picker);
        dialog.setTitle("Deadline");

        EditText deadline = (EditText) findViewById(R.id.deadline);
        deadline.setKeyListener(null);
        deadline.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.show();
            }
        });
        getMenuInflater().inflate(R.menu.menu_setup_parameters, menu);
        return true;
    }

    public void dismiss() {
        progress.dismiss();
    }

    public void closeDialog(View v) {
        DatePicker date = (DatePicker) dialog.findViewById(R.id.deadline_date);
        TimePicker time = (TimePicker) dialog.findViewById(R.id.deadline_time);
        EditText deadline = (EditText) findViewById(R.id.deadline);
        // dd/MM/yyyy HH:MM:00
        String day = "" + date.getDayOfMonth();
        String month = "" + date.getMonth();
        String year = "" + date.getYear();
        String hour = "" + time.getCurrentHour();
        String minute = "" + time.getCurrentMinute();
        if(date.getMonth() < 10){
            month = "0" + month;
        }
        if(date.getDayOfMonth() < 10){
            day  = "0" + day ;
        }
        if (time.getCurrentHour() < 10 ){
            hour = "0" + time.getCurrentHour();
        }
        if (time.getCurrentMinute() < 10 ){
            minute = "0" + time.getCurrentMinute();
        }
        deadline.setText(day + "/" + month + "/" + year + " " + hour + ":" + minute + ":00");
        dialog.dismiss();
    }

    public void setupParameters(View v) {
        EditText course_code = (EditText) findViewById(R.id.course_code);
        EditText course_section = (EditText) findViewById(R.id.course_section);
        EditText min_students = (EditText) findViewById(R.id.min_students);
        EditText max_students = (EditText) findViewById(R.id.max_students);
        EditText deadline = (EditText) findViewById(R.id.deadline);
        String[] params = {
                course_code.getText().toString(),
                course_section.getText().toString(),
                min_students.getText().toString(),
                max_students.getText().toString(),
                deadline.getText().toString()
        };
        progress.setTitle("Setup parameters");
        progress.setMessage("Setting up team parameters...");
        progress.show();
        new SetupParametersTask(this).execute(params);
    }

    protected void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView(R.layout.activity_setup_parameters);
    }

    public void logout(MenuItem m) {
        MainActivity.logout(m, this);
    }
}
