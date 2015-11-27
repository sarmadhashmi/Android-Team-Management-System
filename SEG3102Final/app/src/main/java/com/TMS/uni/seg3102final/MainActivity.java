package com.TMS.uni.seg3102final;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Layout;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.TMS.uni.seg3102final.tasks.LoginTask;
import com.TMS.uni.seg3102final.tasks.RegisterTask;

import org.json.JSONException;
import org.json.JSONObject;

public class MainActivity extends AppCompatActivity {
    JSONObject response;
    public static final String IP_ADDRESS = "10.0.3.2";
    public static final int TIMEOUT = 5000;
    public ProgressDialog progress;
    private boolean register = false;
    LinearLayout registerLayout;
    ImageButton imgButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        progress = new ProgressDialog(this);
        registerLayout = (LinearLayout) findViewById(R.id.registerLayout);
        //imgButton =(ImageButton)findViewById(R.id.imageButton);
        //imgButton.setOnClickListener(new View.OnClickListener() {
          //  @Override
            //public void onClick(View v) {
              //  Toast.makeText(getApplicationContext(), "You download is resumed", Toast.LENGTH_LONG).show();
            //}
        //});
    }

    public void register(View view) {

        if (registerLayout.getVisibility() == View.GONE) {
            Animation slideUp = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.slide_up);
            registerLayout.startAnimation(slideUp);
            registerLayout.setVisibility(View.VISIBLE);
        }else{


        }

    }

    public void displayError() {
        Toast.makeText(getApplicationContext(), "Invalid User Or Password", Toast.LENGTH_LONG).show();
    }

    public void dismiss() {
        progress.dismiss();
    }

    public void login(View view) {
        if (registerLayout.getVisibility() == View.GONE) {
            EditText username = (EditText) findViewById(R.id.username);
            EditText password = (EditText) findViewById(R.id.password);
            String[] params = {username.getText().toString(), password.getText().toString()};

            progress.setTitle("logging in");
            progress.setMessage("Wait while loading...");
            progress.show();
            new LoginTask(this).execute(params);
        }else{
            Animation slideDown = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.slide_down);
            registerLayout.startAnimation(slideDown);
            registerLayout.setVisibility(View.GONE);
        }
    }

    public void loadOperations(String type) {
        Intent intent;

        if(type.equals("instructor")) {
            startActivity(new Intent(this, InstructorOperations.class));
        }else if(type.equals("student")) {
            intent = new Intent(this, StudentOperations.class);
            intent.putExtra("isLiason", false);
            startActivity(intent);
        }else if(type.equals("liason")) {
            intent = new Intent(this, StudentOperations.class);
            intent.putExtra("isLiason", true);
            startActivity(intent);
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

    public static JSONObject getObj(String key, String value) {
        try {
            JSONObject obj = new JSONObject();
            obj.put(key, value);
            return obj;
        } catch (JSONException je) {
            je.printStackTrace();
        }
        return null;
    }
}
