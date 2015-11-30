package com.TMS.uni.seg3102final;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.nfc.Tag;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ExpandableListView;
import android.widget.ListView;
import android.widget.Toast;

import com.TMS.uni.seg3102final.Models.ListItemModel;
import com.TMS.uni.seg3102final.adapters.CustomDataItemAdapter;
import com.TMS.uni.seg3102final.adapters.CustomExpandableListAdapter;
import com.TMS.uni.seg3102final.tasks.GetLiasionTeamsTask;
import com.TMS.uni.seg3102final.tasks.StudentRequestsTask;
import com.TMS.uni.seg3102final.tasks.StudentTeamsTask;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AcceptNewStudents extends AppCompatActivity {
    public ProgressDialog progress;
    ListView lv;
    ListItemModel[] modelItems;
    Dialog dialog;
    ArrayList<ListItemModel> memListOptions;
    CustomDataItemAdapter adapter;
    String selectedTeam;
    public static HashMap<String, Boolean> selectedRequests;

    HashMap<String, ArrayList<String>> teamMap;
    HashMap<String, String> teamIdMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_accept_new_students);
        teamMap = new HashMap<String, ArrayList<String>>();
        teamIdMap = new HashMap<String, String>();
        selectedRequests = new HashMap<String, Boolean>();
        lv = (ListView) findViewById(R.id.listView1);

        lv.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);

        progress = new ProgressDialog(this);
        progress.setTitle("Accept Requests");
        progress.setMessage("Retrieving Your Teams...");
        progress.show();

        new GetLiasionTeamsTask(this).execute();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_accept_new_students, menu);
        return true;
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
                finish();
            }
        });

        // Showing Alert Message
        alertDialog.show();
    }


    public void showTeams(JSONArray teams) {
        ArrayList<String> list = new ArrayList<String>();
        JSONObject team;
        JSONArray requestedMemebers = null;

        String key = "null";
        String value = "null";


        if (teams != null) {
            int len = teams.length();
            for (int i = 0; i < len; i++) {
                try {
                    team = (JSONObject) teams.get(i);
                    key = team.getString("teamName").toString();
                    value = team.getString("_id").toString();
                    requestedMemebers = team.getJSONArray("requestedMembers");
                    teamIdMap.put(key, value);
                    list.add(key);
                } catch (Exception e) {
                }

                ArrayList<String> memList = new ArrayList<String>();
                if (requestedMemebers != null) {
                    int len2 = requestedMemebers.length();
                    for (int j = 0; j < len2; j++) {
                        try {
                            memList.add(requestedMemebers.get(j).toString());
                        }catch(Exception e){}
                    }
                }

                teamMap.put(key, memList);


            }
            dialog = onCreateDialogSingleChoice(list.toArray(new String[list.size()]));
            dialog.show();
        }
    }

    public void dismiss() {
        progress.dismiss();
    }

    public void acceptRequests(View view) {
        ArrayList<String> list = new ArrayList<String>();
        list.add(teamIdMap.get(selectedTeam));

        for (Map.Entry<String, Boolean> entry : selectedRequests.entrySet()) {
            String key = entry.getKey();
            Boolean value = entry.getValue();

            if (value == true)
                list.add(key);
        }

        progress = new ProgressDialog(this);
        progress.setTitle("Accept Requests");
        progress.setMessage("Accepting Requests...");
        progress.show();

        new StudentRequestsTask(this).execute(list.toArray(new String[list.size()]));

    }


    public void showStudentRequests(String team) {

        ArrayList<String> reqList;
        selectedTeam = team;
       memListOptions = new ArrayList<ListItemModel>();

        reqList = teamMap.get(team);
        selectedRequests = new HashMap<String, Boolean>();
        for (String request : reqList) {
            memListOptions.add(new ListItemModel(request, 0));
            selectedRequests.put(request,false);
        }

        if(memListOptions.size() == 0)
        {
            displaySuccessMessage("No Requests For Selected Team");
        }
        else{
            adapter = new CustomDataItemAdapter(this, memListOptions.toArray(new ListItemModel[memListOptions.size()]));
            lv.setAdapter(adapter);

        }


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


    public Dialog onCreateDialogSingleChoice(String[] choices) {


        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        /* temp until integrated with Backend */
        CharSequence[] array = choices;
        /* temp until integrated with Backend */

        builder.setTitle("Select Team")

                .setSingleChoiceItems(array, 1, new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // TODO Auto-generated method stub

                    }
                })

                // Set the action buttons
                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {

                        ListView lw = ((AlertDialog)dialog).getListView();
                        Object checkedItem = lw.getAdapter().getItem(lw.getCheckedItemPosition());
                        showStudentRequests((String)checkedItem);
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {

                    }
                });

        return builder.create();
    }

    public void logout(MenuItem m) {
        MainActivity.logout(m, this);
    }
}
