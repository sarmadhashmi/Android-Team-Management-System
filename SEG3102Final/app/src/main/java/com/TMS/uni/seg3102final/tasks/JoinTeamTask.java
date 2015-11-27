package com.TMS.uni.seg3102final.tasks;

import android.app.Activity;
import android.os.AsyncTask;

import com.TMS.uni.seg3102final.MainActivity;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.ConnectException;
import java.net.MalformedURLException;
import java.net.SocketTimeoutException;

public class JoinTeamTask extends AsyncTask<String, JSONObject, JSONObject> {
    Activity activity;
    public JoinTeamTask(Activity activity) {
        this.activity = activity;
    }

    @Override
    protected JSONObject doInBackground(String[] params) {
       return null;
    }

    protected void onPostExecute(JSONObject response) {


    }
}
