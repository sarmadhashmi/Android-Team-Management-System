package com.TMS.uni.seg3102final.tasks;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;

import com.TMS.uni.seg3102final.AcceptNewStudents;
import com.TMS.uni.seg3102final.MainActivity;
import com.TMS.uni.seg3102final.VisualizeStudentTeams;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class StudentRequestsTask extends AsyncTask<Void, JSONObject, JSONObject> {
    Activity activity;
    public StudentRequestsTask(Activity activity) {
        this.activity = activity;
    }

    @Override
    protected JSONObject doInBackground(Void... params) {
        try {
            URL url = new URL("http://" + MainActivity.IP_ADDRESS + ":3001/viewRequestedMembers");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");

            SharedPreferences settings = activity.getSharedPreferences("auth",
                    Context.MODE_PRIVATE);
            String token = settings.getString("access_token", "defaultvalue");

            conn.setRequestProperty("Authorization", "jwt " + token);

            // Get response
            BufferedReader reader = null;
            if (conn.getResponseCode() / 100 == 2) {
                reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            } else {
                reader = new BufferedReader(new InputStreamReader(conn.getErrorStream()));
            }
            String line;
            StringBuilder str = new StringBuilder();
            while ((line = reader.readLine()) != null) {
                str.append(line);
            }
            reader.close();
            return new JSONObject(str.toString());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    protected void onPostExecute(JSONObject response) {
        try {
            AcceptNewStudents context = (AcceptNewStudents) activity;
            context.showStudentRequests(response.getJSONArray("requestedMembers"));
            context.dismiss();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
