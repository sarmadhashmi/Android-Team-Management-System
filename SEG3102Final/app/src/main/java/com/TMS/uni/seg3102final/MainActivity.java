package com.TMS.uni.seg3102final;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
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
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.TMS.uni.seg3102final.tasks.LoginTask;
import com.TMS.uni.seg3102final.tasks.RegisterTask;

import org.json.JSONException;
import org.json.JSONObject;

public class MainActivity extends AppCompatActivity {
    JSONObject response;
    public static final String IP_ADDRESS = "10.0.2.2";
    public static final int TIMEOUT = 5000;
    public ProgressDialog progress;
    private boolean register = false;
    LinearLayout registerLayout;
    RadioGroup rg;
    View pgmOfStudy;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        if (getIntent().getExtras() != null && getIntent().getExtras().getBoolean("loggedOut")) {
            Toast.makeText(getApplicationContext(), "Logged out!", Toast.LENGTH_SHORT);
        }

        pgmOfStudy = findViewById(R.id.programOfStudy);

        setSupportActionBar(toolbar);
        progress = new ProgressDialog(this);
        registerLayout = (LinearLayout) findViewById(R.id.registerLayout);
        rg = (RadioGroup)findViewById(R.id.radio_group_user);

        rg.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener()
        {
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch(checkedId){
                    case R.id.radio_instructor:
                        pgmOfStudy.setVisibility(View.GONE);
                        break;

                    case R.id.radio_student:
                        pgmOfStudy.setVisibility(View.VISIBLE);
                        break;
                }


            }
        });



    }

    public void dismiss() {
        progress.dismiss();
    }



    void register(){
        EditText username = (EditText) findViewById(R.id.username);
        EditText password = (EditText) findViewById(R.id.password);
        EditText email = (EditText) findViewById(R.id.email);
        EditText firstName = (EditText) findViewById(R.id.firstName);
        EditText lastName = (EditText) findViewById(R.id.lastName);
        EditText pgmStudy = (EditText) findViewById(R.id.programOfStudy);


        String radioValue;
        String programOfStudy;


        try{
            radioValue = ((RadioButton)findViewById(rg.getCheckedRadioButtonId())).getText().toString();
        }catch(Exception e){
            radioValue = "null";
        }

        if (radioValue.equals("Student"))
        {
            programOfStudy = pgmStudy.getText().toString();
        }else
            programOfStudy = null;

        String[] params = {username.getText().toString(), password.getText().toString(),email.getText().toString(),firstName.getText().toString(),lastName.getText().toString(), radioValue, programOfStudy};

        progress.setTitle("Register");
        progress.setMessage("Registering Please Wait...");
        progress.show();
        new RegisterTask(this).execute(params);
    }


    void login(){
        EditText username = (EditText) findViewById(R.id.username);
        EditText password = (EditText) findViewById(R.id.password);
        String[] params = {username.getText().toString(), password.getText().toString()};

        progress.setTitle("Log in");
        progress.setMessage("Logging in...");
        progress.show();
        new LoginTask(this).execute(params);
    }

    public void performAction(View view) {

        if (registerLayout.getVisibility() == View.GONE){
            login();
        }else{
            register();
        }

    }


    void reinit()
    {
        pgmOfStudy.setVisibility(View.GONE);
        ((TextView)findViewById (R.id.username)).setText("");
        ((TextView)findViewById (R.id.password)).setText("");
        ((TextView)findViewById (R.id.email)).setText("");
        ((TextView)findViewById (R.id.firstName)).setText("");
        ((TextView)findViewById (R.id.lastName)).setText("");
        ((TextView)findViewById (R.id.programOfStudy)).setText("");
    }

    public void invert(View view) {

        reinit();

        if (registerLayout.getVisibility() == View.GONE) {
            Animation slideUp = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.slide_up);
            registerLayout.startAnimation(slideUp);
            registerLayout.setVisibility(View.VISIBLE);
            pgmOfStudy.setVisibility(View.GONE);
            rg.clearCheck();
            ((TextView)findViewById (R.id.inverseButton)).setText("Login?");
            ((TextView)findViewById (R.id.actionButton)).setText("Register");

        }else{
            Animation slideDown = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.slide_down);
            registerLayout.startAnimation(slideDown);
            registerLayout.setVisibility(View.GONE);
            ((TextView)findViewById (R.id.inverseButton)).setText("Register?");
            ((TextView)findViewById (R.id.actionButton)).setText("Login");
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
                // Write your code here to execute after dialog closed
            }
        });

        // Showing Alert Message
        alertDialog.show();


    }

    public void loadOperations(String type) {
        Intent intent;

        SharedPreferences settings = getSharedPreferences("USER",
                Context.MODE_PRIVATE);

        SharedPreferences.Editor editor = settings.edit();

        if(type.equals("instructor")) {
            startActivity(new Intent(this, InstructorOperations.class));
        }else if(type.equals("student")) {
            intent = new Intent(this, StudentOperations.class);
            intent.putExtra("isLiason", false);
            editor.putBoolean("isLiason", false);
            editor.commit();
            startActivity(intent);
        }else if(type.equals("liason")) {
            intent = new Intent(this, StudentOperations.class);
            editor.putBoolean("isLiason", true);
            editor.commit();
            intent.putExtra("isLiason", true);
            startActivity(intent);
        }
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

    public static void logout(MenuItem m, AppCompatActivity a) {
        SharedPreferences settings = a.getSharedPreferences("auth", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = settings.edit();
        editor.putString("access_token", null);
        editor.commit();
        Intent intent = new Intent(a, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        Toast.makeText(a, "Logged out!", Toast.LENGTH_SHORT);
        a.startActivity(intent);
    }
}
