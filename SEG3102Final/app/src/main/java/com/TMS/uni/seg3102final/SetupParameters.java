package com.TMS.uni.seg3102final;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;

import com.TMS.uni.seg3102final.tasks.LoginTask;
import com.TMS.uni.seg3102final.tasks.RegisterTask;
import com.TMS.uni.seg3102final.tasks.SetupParametersTask;

public class SetupParameters extends AppCompatActivity {
    public ProgressDialog progress;
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        progress = new ProgressDialog(this);
        getMenuInflater().inflate(R.menu.menu_setup_parameters, menu);
        return true;
    }

    public void dismiss() {
        progress.dismiss();
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
