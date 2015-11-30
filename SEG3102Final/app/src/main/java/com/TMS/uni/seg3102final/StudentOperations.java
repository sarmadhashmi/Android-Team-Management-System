package com.TMS.uni.seg3102final;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.view.View;


public class StudentOperations extends AppCompatActivity {

    ListView lv;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student_operations);

        initList();

        lv = (ListView) findViewById(R.id.listView);

        SimpleAdapter simpleAdpt = new SimpleAdapter(this, operationsList, android.R.layout.simple_list_item_1, new String[]{"operation"}, new int[]{android.R.id.text1});

        lv.setAdapter(simpleAdpt);

        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            public void onItemClick(AdapterView<?> parentAdapter, View view, int position, long id) {

                TextView clickedView = (TextView) view;
                Intent intent = null;

                if(clickedView.getText().equals("Create Team")) {
                    intent = new Intent(StudentOperations.this, SelectTeamParameters.class);
                    intent.putExtra("nextPage", "Create Team");
                }
                else if(clickedView.getText().equals("Join Team")){
                    intent = new Intent(StudentOperations.this, SelectTeamParameters.class);
                    intent.putExtra("nextPage", "Join Team");
                }
                else if(clickedView.getText().equals("Accept New Students")){
                    intent = new Intent(StudentOperations.this, AcceptNewStudents.class);
                }

                startActivity(intent);
            }
        });
    }

    List<Map<String, String>> operationsList = new ArrayList<Map<String, String>>();


    private void initList() {
        // We populate the planets
        boolean isLiason = getIntent().getBooleanExtra("isLiason", false);

        operationsList.add(createOperation("operation", "Create Team"));
        operationsList.add(createOperation("operation", "Join Team"));
        if(isLiason){
            operationsList.add(createOperation("operation", "Accept New Students"));
            setTitle("Liason Operations");
        }


    }

    private HashMap<String, String> createOperation(String key, String name) {
        HashMap<String, String> operation = new HashMap<String, String>();
        operation.put(key, name);

        return operation;
    }

    @Override
    public void onBackPressed() {
        Toast.makeText(getApplicationContext(), "You need to logout first!", Toast.LENGTH_LONG).show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_student_operations, menu);
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