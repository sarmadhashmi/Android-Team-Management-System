package com.TMS.uni.seg3102final;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.widget.ExpandableListView;

import com.TMS.uni.seg3102final.tasks.StudentTeamsTask;
import com.TMS.uni.seg3102final.adapters.CustomExpandableListAdapter;

import org.json.JSONArray;
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

    private String JSONArrayToString(JSONArray arr, String seperator) {
        StringBuilder str = new StringBuilder();
        try {
            for (int i = 0; i < arr.length() - 1; i++) {
                str.append(arr.getString(i) + seperator + " ");
            }
            str.append(arr.getString(arr.length() - 1));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return str.toString();
    }

    public void showStudentTeams(JSONArray teams) {
        ExpandableListView expListView = (ExpandableListView) findViewById(R.id.expand_student_teams);
        ArrayList<String> headers = new ArrayList<String>();
        HashMap<String, List<String>> children = new HashMap<String, List<String>>();
        for (int i = 0; i < teams.length(); i++) {
            try {
                JSONObject team = teams.getJSONObject(i);
                List<String> teamInfo = new ArrayList<String>();
                teamInfo.add("Date of creation: " + team.getString("dateOfCreation"));
                teamInfo.add("Size: " + team.getString("teamSize"));
                teamInfo.add("Liason: " + team.getString("liason"));
                JSONArray requestedMembers = team.getJSONArray("requestedMembers");
                JSONArray teamMembers = team.getJSONArray("teamMembers");
                teamInfo.add("Students who have requested to join the team: " + JSONArrayToString(requestedMembers, ","));
                teamInfo.add("Students currently in team: " + JSONArrayToString(teamMembers, ","));

                // Add to list for expandable list view
                children.put(team.getString("teamName"), teamInfo);
                headers.add(team.getString("teamName"));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        CustomExpandableListAdapter listAdapter = new CustomExpandableListAdapter(this, headers, children);
        expListView.setAdapter(listAdapter);
    }
}
