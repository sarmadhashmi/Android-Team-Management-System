package com.TMS.uni.seg3102final;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.TMS.uni.seg3102final.tasks.LoginTask;
import com.TMS.uni.seg3102final.tasks.RegisterTask;

import org.json.JSONObject;

public class MainActivity extends AppCompatActivity {
    JSONObject response;
    public static final String IP_ADDRESS = "10.0.2.2";
    public ProgressDialog progress;
    ImageButton imgButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        progress = new ProgressDialog(this);
        imgButton =(ImageButton)findViewById(R.id.imageButton);
        imgButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getApplicationContext(), "You download is resumed", Toast.LENGTH_LONG).show();
            }
        });
    }

    public void register(View view) {
        EditText username = (EditText) findViewById(R.id.username);
        EditText password = (EditText) findViewById(R.id.password);
        String[] params = {username.getText().toString(), password.getText().toString()};
        new RegisterTask(this).execute(params);

        Intent intent = new Intent(this, StudentOperations.class);
        startActivity(intent);

    }

    public void displayError() {
        Toast.makeText(getApplicationContext(), "Invalid User Or Password", Toast.LENGTH_LONG).show();
    }

    public void dismiss() {
        progress.dismiss();
    }

    public void login(View view) {
        EditText username = (EditText) findViewById(R.id.username);
        EditText password = (EditText) findViewById(R.id.password);
        String[] params = {username.getText().toString(), password.getText().toString()};

        progress.setTitle("logging in");
        progress.setMessage("Wait while loading...");
        progress.show();
        new LoginTask(this).execute(params);
    }

    public void loadOperations(String type) {

        if(type.equals("student")) {
            Intent intent = new Intent(this, StudentOperations.class);
            startActivity(intent);
        }else if(type.equals("instructor"))
        {
            Intent intent = new Intent(this, InstructorOperations.class);
            startActivity(intent);
        }else if(type.equals("liason"))
        {

        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
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
}
