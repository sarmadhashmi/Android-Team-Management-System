package com.TMS.uni.seg3102final;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ExpandableListView;
import android.widget.ListView;

import com.TMS.uni.seg3102final.Models.ListItemModel;
import com.TMS.uni.seg3102final.adapters.CustomDataItemAdapter;
import com.TMS.uni.seg3102final.adapters.CustomExpandableListAdapter;
import com.TMS.uni.seg3102final.tasks.StudentRequestsTask;
import com.TMS.uni.seg3102final.tasks.StudentTeamsTask;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class AcceptNewStudents extends AppCompatActivity {
    public ProgressDialog progress;
    ListView lv;
    ListItemModel[] modelItems;
    Dialog dialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_accept_new_students);

        lv = (ListView) findViewById(R.id.listView1);

        progress = new ProgressDialog(this);
        progress.setTitle("Accept Requests");
        progress.setMessage("Retrieving Student Requests...");
        //progress.show();
        //new StudentRequestsTask(this).execute();


        dialog = onCreateDialogSingleChoice();
        dialog.show();

        /* temp until integrated with Backend */
            modelItems = new ListItemModel[5];
            modelItems[0] = new ListItemModel("stest", 0);
            modelItems[1] = new ListItemModel("Janac", 0);
            modelItems[2] = new ListItemModel("Muraad", 0);
            modelItems[3] = new ListItemModel("Sarmad", 0);
            modelItems[4] = new ListItemModel("Quincy", 0);
            CustomDataItemAdapter adapter = new CustomDataItemAdapter(this, modelItems);
            lv.setAdapter(adapter);
        /* temp until integrated with Backend */
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_accept_new_students, menu);
        return true;
    }

    public void dismiss() {
        progress.dismiss();
    }

    public void acceptRequests(View view) {

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


    public void showStudentRequests(JSONArray requestedMemebers) {

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


    public Dialog onCreateDialogSingleChoice() {


        AlertDialog.Builder builder = new AlertDialog.Builder(this);


        CharSequence[] array = {"SnakeTeam", "SegFour", "SegSix"};


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
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {

                    }
                });

        return builder.create();
    }


}
