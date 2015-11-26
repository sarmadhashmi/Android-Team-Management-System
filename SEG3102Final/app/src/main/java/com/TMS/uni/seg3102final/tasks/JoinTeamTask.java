package com.TMS.uni.seg3102final.tasks;

import android.app.Activity;
import android.os.AsyncTask;

import org.json.JSONObject;

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
