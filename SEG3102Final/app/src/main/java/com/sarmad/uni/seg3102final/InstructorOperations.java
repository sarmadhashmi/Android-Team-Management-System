package com.sarmad.uni.seg3102final;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

public class InstructorOperations extends Activity {
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
}
