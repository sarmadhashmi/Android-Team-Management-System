package com.TMS.uni.seg3102final;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import android.widget.Toast;

import com.TMS.uni.seg3102final.Models.ListItemModel;
import com.TMS.uni.seg3102final.adapters.CustomDataItemAdapter;
import com.TMS.uni.seg3102final.tasks.JoinTeamTask;
import com.TMS.uni.seg3102final.tasks.StudentRequestsTask;
import com.TMS.uni.seg3102final.tasks.TeamsInTeamParamTask;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class JoinTeam extends AppCompatActivity {

    public ProgressDialog progress;
    ListView lv;
    ListItemModel[] modelItems;
    HashMap<String, String> teamIdMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_join_team);
        teamIdMap = new HashMap<String, String>();

        lv = (ListView) findViewById(R.id.listView2);
        CustomDataItemAdapter.selected = new HashMap<String, Boolean>();
        progress = new ProgressDialog(this);
        progress.setTitle("Accept Requests");
        progress.setMessage("Retrieving Student Requests...");
        //progress.show();
        //new JoinTeamTask(this).execute();
        String teamParamID = getIntent().getStringExtra("teamParam_id");
        String [] params = {teamParamID};
        new TeamsInTeamParamTask(this).execute(params);


    }

    public void joinTeams(View view) {


        ArrayList<String> list = new ArrayList<String>();


        for (Map.Entry<String, Boolean> entry : CustomDataItemAdapter.selected.entrySet()) {
            String key = entry.getKey();
            Boolean value = entry.getValue();

            if (value == true)
            {
                list.add(teamIdMap.get(key));
            }
        }

        progress = new ProgressDialog(this);
        progress.setTitle("Join Teams");
        progress.setMessage("Joining Teams...");
        progress.show();

        new JoinTeamTask(this).execute(list.toArray(new String[list.size()]));

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
            }
        });

        // Showing Alert Message
        alertDialog.show();
    }

    public void displaySuccessMessage(String message) {
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

                Intent intent;

                SharedPreferences settings = JoinTeam.this.getSharedPreferences("USER",
                        Context.MODE_PRIVATE);
                boolean isLiason = settings.getBoolean("isLiason", false);
                intent = new Intent(JoinTeam.this, StudentOperations.class);
                intent.putExtra("isLiason", false);
                startActivity(intent);


            }
        });

        // Showing Alert Message
        alertDialog.show();
    }


    public void dismiss() {
        progress.dismiss();
    }


    public void showTeams(JSONArray teams) {
        ArrayList<ListItemModel> list = new ArrayList<ListItemModel>();
        JSONObject team;
        String teamID;
        String teamName;

        if (teams != null) {
            int len = teams.length();
            for (int i = 0; i < len; i++) {
                try {
                    team = (JSONObject) teams.get(i);
                    teamName = team.getString("teamName").toString();
                    teamID = team.getString("_id").toString();
                    list.add(new ListItemModel(teamName, 0));
                    teamIdMap.put(teamName, teamID);
                } catch (Exception e) {
                }
            }
        }

        CustomDataItemAdapter adapter = new CustomDataItemAdapter(this, list.toArray(new ListItemModel[list.size()]));
        lv.setAdapter(adapter);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_join_team, menu);
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

    public void logout(MenuItem m) {
        MainActivity.logout(m, this);
    }
}
