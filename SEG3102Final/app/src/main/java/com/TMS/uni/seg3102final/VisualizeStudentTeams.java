package com.TMS.uni.seg3102final;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ExpandableListView;
import android.widget.ListView;

import com.TMS.uni.seg3102final.Models.DBItem;
import com.TMS.uni.seg3102final.adapters.CustomDBItemAdapter;
import com.TMS.uni.seg3102final.tasks.StudentTeamsTask;
import com.TMS.uni.seg3102final.adapters.CustomExpandableListAdapter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class VisualizeStudentTeams extends AppCompatActivity {
    public ProgressDialog progress;
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_visualize_student_teams, menu);
        return true;
    }

    protected void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView(R.layout.activity_visualize_student_teams);
        progress = new ProgressDialog(this);
        progress.setTitle("Visualize student teams");
        progress.setMessage("Retrieving student teams...");
        progress.show();
        new StudentTeamsTask(this).execute();
    }

    public void dismiss() {
        progress.dismiss();
    }

    public void showStudentTeams(JSONArray teams) {
        ListView listView = (ListView) findViewById(R.id.student_teams);
        ArrayList<DBItem> items = new ArrayList<DBItem>();
        for (int i = 0; i < teams.length(); i++) {
            try {
                items.add(new DBItem(teams.getJSONObject(i), "team"));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        final CustomDBItemAdapter listAdapter = new CustomDBItemAdapter(this, android.R.layout.simple_list_item_1, items);
        listView.setAdapter(listAdapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                DBItem item = listAdapter.getItem(position);
                Intent intent = new Intent(VisualizeStudentTeams.this, TeamProfilePage.class);
                try {
                    intent.putExtra("teamName", item.getKey("teamName"));
                    intent.putExtra("dateOfCreation", item.getKey("dateOfCreation"));
                    intent.putExtra("teamSize", item.getKey("teamSize"));
                    intent.putExtra("liason", item.getKey("liason"));
                    intent.putExtra("requestedMembers", item.JSONArrayToStringArray("requestedMembers"));
                    intent.putExtra("teamMembers", item.JSONArrayToStringArray("teamMembers"));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                startActivity(intent);
            }
        });
    }

    public void logout(MenuItem m) {
        MainActivity.logout(m, this);
    }
}
