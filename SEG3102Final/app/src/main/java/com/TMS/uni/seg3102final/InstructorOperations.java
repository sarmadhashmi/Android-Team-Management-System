package com.TMS.uni.seg3102final;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.TMS.uni.seg3102final.adapters.CustomFontAdapter;

public class InstructorOperations extends AppCompatActivity {
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_instructor_operations);
        final ListView listView = (ListView) findViewById(R.id.instructer_operations);
        CustomFontAdapter adapter = new CustomFontAdapter(this, android.R.layout.simple_list_item_1, "Roboto-Light.ttf");
        adapter.add("Setup parameters");
        adapter.add("Visualize student teams");
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String itemClicked = (String) listView.getItemAtPosition(position);
                Intent intent = null;
                switch(itemClicked) {
                    case "Setup parameters":
                        intent = new Intent(InstructorOperations.this, SetupParameters.class);
                        break;
                    case "Visualize student teams":
                        intent = new Intent(InstructorOperations.this, VisualizeStudentTeams.class);
                }
                startActivity(intent);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_instructor_operations, menu);
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

    @Override
    public void onBackPressed() {
        Toast.makeText(getApplicationContext(), "You need to logout first!", Toast.LENGTH_LONG).show();
    }

    public void logout(MenuItem m) {
        MainActivity.logout(m, this);
    }
}
