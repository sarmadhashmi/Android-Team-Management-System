package com.TMS.uni.seg3102final;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.format.DateFormat;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import com.TMS.uni.seg3102final.Models.DBItem;
import com.TMS.uni.seg3102final.adapters.CustomDBItemAdapter;
import com.TMS.uni.seg3102final.adapters.CustomDataItemAdapter;
import com.TMS.uni.seg3102final.tasks.AllTeamParametersTask;
import com.TMS.uni.seg3102final.tasks.StudentTeamsTask;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class SelectTeamParameters extends AppCompatActivity {
    public ProgressDialog progress;
    String nextPage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_team_parameters);
        progress = new ProgressDialog(this);
        nextPage = getIntent().getStringExtra("nextPage");

        progress.setTitle("Create team");

        if(nextPage.equals("Join Team"))
            progress.setTitle("Join team");

        progress.setMessage("Retrieving list of team parameters...");
        progress.show();
        new AllTeamParametersTask(this).execute();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_select_team_parameters, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void dismiss() {
        progress.dismiss();
    }

    public void displayMessage(String message) {
        AlertDialog alertDialog = new AlertDialog.Builder(
                this).create();

        // Setting Dialog Title
        alertDialog.setTitle("Information");

        // Setting Dialog Message
        alertDialog.setMessage(message);

        // Setting Icon to Dialog
        // alertDialog.setIcon(R.drawable.tick);

        // Setting OK Button
        alertDialog.setButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                finish();
            }
        });

        // Showing Alert Message
        alertDialog.show();
    }



    public void showTeamParams(JSONArray teamParams) {
        try {
            ListView view = (ListView) findViewById(R.id.team_parameters_list);
            ArrayList<DBItem> items = new ArrayList<DBItem>();
            for (int i = 0; i < teamParams.length(); i++) {
                items.add(new DBItem(teamParams.getJSONObject(i), "teamParams"));
            }
            final CustomDBItemAdapter list = new CustomDBItemAdapter(this, R.layout.list_item, items);
            view.setAdapter(list);
            view.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    DBItem item = list.getItem(position);
                    SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy HH:mm");
                    try {
                        Date deadline = format.parse(item.getKey("deadline"));
                        if (deadline.before(new Date())) {
                            Toast.makeText(getApplicationContext(), "The deadline for these parameters has passed!", Toast.LENGTH_LONG).show();
                            return;
                        }
                    } catch (ParseException | JSONException e) {
                        e.printStackTrace();
                        return;
                    }

                    Intent intent;

                    if(nextPage.equals("Create Team")){
                        intent = new Intent(SelectTeamParameters.this, CreateTeam.class);
                    }
                    else{
                        intent = new Intent(SelectTeamParameters.this, JoinTeam.class);
                    }

                    try {
                        intent.putExtra("teamParam_id", item.getId());
                        intent.putExtra("teamParam_min", item.getKey("minimumNumberOfStudents"));
                        intent.putExtra("teamParam_max", item.getKey("maximumNumberOfStudents"));
                        intent.putExtra("teamParam_instructor_name", item.getKey("instructor_name"));
                        intent.putExtra("teamParam_course_section", item.getKey("course_section"));
                        intent.putExtra("teamParam_course_code", item.getKey("course_code"));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    startActivity(intent);
                }
            });
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void logout(MenuItem m) {
        MainActivity.logout(m, this);
    }
}
