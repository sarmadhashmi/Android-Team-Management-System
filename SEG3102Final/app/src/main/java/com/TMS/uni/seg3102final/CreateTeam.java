package com.TMS.uni.seg3102final;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.TMS.uni.seg3102final.Models.DBItem;
import com.TMS.uni.seg3102final.adapters.CustomDBItemAdapter;
import com.TMS.uni.seg3102final.adapters.CustomStudentListAdapter;
import com.TMS.uni.seg3102final.tasks.AllStudentsTask;
import com.TMS.uni.seg3102final.tasks.CreateTeamTask;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;

public class CreateTeam extends AppCompatActivity {
    public ProgressDialog progress;
    int max;
    int min;
    String team_param_id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_team);
        Intent intent = getIntent();
        team_param_id = intent.getStringExtra("teamParam_id");
        max = Integer.parseInt(intent.getStringExtra("teamParam_max"));
        min = Integer.parseInt(intent.getStringExtra("teamParam_min"));
        progress = new ProgressDialog(this);
        progress.setTitle("Create Team");
        progress.setMessage("Retrieving information to create team...");
        new AllStudentsTask(this).execute();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_create_team, menu);
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
    public void addMember(View v) {
        Spinner dropdown = (Spinner) findViewById(R.id.students);
        ListView addedStudents = (ListView) findViewById(R.id.added_members);
        DBItem item = (DBItem) dropdown.getSelectedItem();
        CustomStudentListAdapter currentStudents = (CustomStudentListAdapter) addedStudents.getAdapter();
        CustomDBItemAdapter allStudents = (CustomDBItemAdapter) dropdown.getAdapter();
        if (currentStudents.length() >= max) {
            Toast.makeText(getApplicationContext(), "No more students can be added to this team.", Toast.LENGTH_SHORT).show();
        } else {
            currentStudents.add(item);
            allStudents.remove(item);
            allStudents.notifyDataSetChanged();
            currentStudents.notifyDataSetChanged();
            Toast.makeText(getApplicationContext(), "Added " + item.getText() + " to team.", Toast.LENGTH_SHORT).show();
        }
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
                intent = new Intent(CreateTeam.this, StudentOperations.class);
                intent.putExtra("isLiason", true);
                startActivity(intent);
            }
        });

        // Showing Alert Message
        alertDialog.show();
    }



    public void removeMember(DBItem item) {
        Spinner dropdown = (Spinner) findViewById(R.id.students);
        ListView addedStudents = (ListView) findViewById(R.id.added_members);
        CustomDBItemAdapter allStudents = (CustomDBItemAdapter) dropdown.getAdapter();
        CustomStudentListAdapter currentStudents = (CustomStudentListAdapter) addedStudents.getAdapter();
        currentStudents.remove(item);
        allStudents.add(item);
        allStudents.notifyDataSetChanged();
        currentStudents.notifyDataSetChanged();
        Toast.makeText(getApplicationContext(), "Removed " + item.getText() + " from team.", Toast.LENGTH_SHORT).show();
    }

    public void dismiss() {
        progress.dismiss();
    }

    public void setStudents(JSONArray students) {
        try {
            Spinner dropdown = (Spinner) findViewById(R.id.students);
            ListView addedStudents = (ListView) findViewById(R.id.added_members);
            dropdown.setEmptyView(findViewById(R.id.no_students_empty));
            addedStudents.setEmptyView(findViewById(R.id.no_members_empty));
            ArrayList<DBItem> items = new ArrayList<DBItem>();
            for (int i = 0; i < students.length(); i++) {
                items.add(new DBItem(students.getJSONObject(i), "student"));
            }
            CustomDBItemAdapter adapter = new CustomDBItemAdapter(this, android.R.layout.simple_spinner_dropdown_item, items);
            dropdown.setAdapter(adapter);
            addedStudents.setAdapter(new CustomStudentListAdapter(this, R.layout.list_item, new ArrayList<DBItem>()));
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void createTeam(View v) {
        ListView addedStudents = (ListView) findViewById(R.id.added_members);
        CustomStudentListAdapter currentStudents = (CustomStudentListAdapter) addedStudents.getAdapter();
        TextView teamName = (TextView) findViewById(R.id.teamName);
        String name = teamName.getText().toString();
        progress.setTitle("Create Team");
        progress.setMessage("Creating team with the given parameters...");
        progress.show();
        new CreateTeamTask(this, team_param_id, name, currentStudents.toJSONArray()).execute();
    }

    public void logout(MenuItem m) {
        MainActivity.logout(m, this);
    }
}
