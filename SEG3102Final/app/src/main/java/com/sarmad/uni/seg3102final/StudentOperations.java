package com.sarmad.uni.seg3102final;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
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


public class StudentOperations extends Activity {

    ListView lv;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student_operations);

        initList();

        // We get the ListView component from the layout
        lv = (ListView) findViewById(R.id.listView);

        // This is a simple adapter that accepts as parameter
        // Context
        // Data list
        // The row layout that is used during the row creation
        // The keys used to retrieve the data
        // The View id used to show the data. The key number and the view id must match
        SimpleAdapter simpleAdpt = new SimpleAdapter(this, planetsList, android.R.layout.simple_list_item_1, new String[]{"planet"}, new int[]{android.R.id.text1});

        lv.setAdapter(simpleAdpt);

        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            public void onItemClick(AdapterView<?> parentAdapter, View view, int position, long id) {

                // We know the View is a TextView so we can cast it
                TextView clickedView = (TextView) view;
                Intent intent = null;

                Toast.makeText(StudentOperations.this, "Item with id [" + id + "] - Position [" + position + "] - Planet [" + clickedView.getText() + "]", Toast.LENGTH_SHORT).show();

                if(clickedView.getText().equals("Create Team")) {
                    intent = new Intent(StudentOperations.this, CreateTeam.class);
                }
                else if(clickedView.getText().equals("Join Team")){
                    intent = new Intent(StudentOperations.this, JoinTeam.class);
                }
                else if(clickedView.getText().equals("Accept New Students")){
                    intent = new Intent(StudentOperations.this, AcceptNewStudents.class);
                }

                startActivity(intent);
            }
        });
    }

    List<Map<String, String>> planetsList = new ArrayList<Map<String, String>>();


    private void initList() {
        // We populate the planets

        planetsList.add(createPlanet("planet", "Create Team"));
        planetsList.add(createPlanet("planet", "Join Team"));
        planetsList.add(createPlanet("planet", "Accept New Students"));

    }


    private HashMap<String, String> createPlanet(String key, String name) {
        HashMap<String, String> planet = new HashMap<String, String>();
        planet.put(key, name);

        return planet;


    }


}