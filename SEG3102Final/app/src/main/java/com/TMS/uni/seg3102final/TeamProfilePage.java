package com.TMS.uni.seg3102final;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

public class TeamProfilePage extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_team_profile_page);
        Intent intent = getIntent();
        Bundle extras = intent.getExtras();
        setTitle(extras.getString("teamName"));
        TextView dateOfCreation = (TextView) findViewById(R.id.dateOfCreation);
        TextView size = (TextView) findViewById(R.id.teamSize);
        TextView liason = (TextView) findViewById(R.id.liason);
        ListView teamMembers = (ListView) findViewById(R.id.addedMembers);
        ListView requestedMembers = (ListView) findViewById(R.id.requestedMembers);
        dateOfCreation.setText(extras.getString("dateOfCreation"));
        size.setText(extras.getString("teamSize"));
        liason.setText(extras.getString("liason"));
        teamMembers.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, extras.getStringArrayList("teamMembers")));
        requestedMembers.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, extras.getStringArrayList("requestedMembers")));
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

    public void logout(MenuItem m) {
        MainActivity.logout(m, this);
    }
}
