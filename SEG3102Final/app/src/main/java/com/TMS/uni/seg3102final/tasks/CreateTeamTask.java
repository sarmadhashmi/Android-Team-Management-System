package com.TMS.uni.seg3102final.tasks;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;

import com.TMS.uni.seg3102final.MainActivity;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.InvalidParameterException;

public class CreateTeamTask extends AsyncTask<String, JSONObject, JSONObject> {
    Activity activity;
    public CreateTeamTask(Activity activity) {
        this.activity = activity;
    }

    @Override
    protected JSONObject doInBackground(String[] params) {

        return null;
    }

    protected void onPostExecute(JSONObject response) {


    }
}
