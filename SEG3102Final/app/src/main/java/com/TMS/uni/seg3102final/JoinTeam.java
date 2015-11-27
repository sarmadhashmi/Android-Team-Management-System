package com.TMS.uni.seg3102final;

import android.app.ProgressDialog;
import android.content.Context;
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

public class JoinTeam extends AppCompatActivity {

    public ProgressDialog progress;
    ListView lv;
    ListItemModel[] modelItems;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_join_team);

        lv = (ListView) findViewById(R.id.listView2);

        progress = new ProgressDialog(this);
        progress.setTitle("Accept Requests");
        progress.setMessage("Retrieving Student Requests...");
        //progress.show();
        //new JoinTeamTask(this).execute();


        /* temp until integrated with Backend */
            modelItems = new ListItemModel[5];
            modelItems[0] = new ListItemModel("SegFour", 0);
            modelItems[1] = new ListItemModel("SegSix", 0);
            modelItems[2] = new ListItemModel("SnakeTeam", 0);
            modelItems[3] = new ListItemModel("BunkzTeam", 0);
            modelItems[4] = new ListItemModel("Python", 0);
            CustomDataItemAdapter adapter = new CustomDataItemAdapter(this, modelItems);
            lv.setAdapter(adapter);
        /* temp until integrated with Backend */
    }

    public void joinTeams(View view) {

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
