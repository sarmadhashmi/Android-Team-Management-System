package com.TMS.uni.seg3102final.tasks;

import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;
import android.widget.TextView;
import android.widget.Toast;

import com.TMS.uni.seg3102final.MainActivity;
import com.TMS.uni.seg3102final.R;
import com.TMS.uni.seg3102final.SetupParameters;
import com.TMS.uni.seg3102final.VisualizeStudentTeams;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.InvalidParameterException;

public class StudentTeamsTask extends AsyncTask<Void, JSONObject, JSONObject> {
    Activity activity;
    public StudentTeamsTask(Activity activity) {
        this.activity = activity;
    }

    @Override
    protected JSONObject doInBackground(Void... params) {
        try {
            URL url = new URL("http://192.168.0.103:3001/teams");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
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
            VisualizeStudentTeams context = (VisualizeStudentTeams) activity;
            context.showStudentTeams(response.getJSONArray("teams"));
            context.dismiss();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
