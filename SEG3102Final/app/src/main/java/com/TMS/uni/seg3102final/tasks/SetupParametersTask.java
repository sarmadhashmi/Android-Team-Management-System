package com.TMS.uni.seg3102final.tasks;

import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;
import android.widget.TextView;
import android.widget.Toast;

import com.TMS.uni.seg3102final.R;
import com.TMS.uni.seg3102final.SetupParameters;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.InvalidParameterException;

public class SetupParametersTask extends AsyncTask<String, JSONObject, JSONObject> {
    Activity activity;
    public SetupParametersTask(Activity activity) {
        this.activity = activity;
    }

    @Override
    protected JSONObject doInBackground(String[] params) {
        if (params.length < 2) throw new InvalidParameterException("Not enough parameters");
        try {
            String course_code = params[0];
            String min_num_students = params[1];
            String max_num_students = params[2];
            String deadline = params[3];
            JSONObject credentials = new JSONObject();
            credentials.put("course_code", course_code);
            credentials.put("minimum_num_students", min_num_students);
            credentials.put("maximum_num_students", max_num_students);
            credentials.put("deadline", deadline);

            URL url = new URL("http://192.168.0.103:3001/createTeamParams");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();

            conn.setDoOutput(true);
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/json");

            OutputStreamWriter writer = new OutputStreamWriter(conn.getOutputStream());
            writer.write(credentials.toString());
            writer.flush();
            writer.close();
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
            ((SetupParameters) activity).dismiss();
            Context context = activity.getApplicationContext();
            String text = response.getString("message");
            int duration = Toast.LENGTH_SHORT;
            Toast toast = Toast.makeText(context, text, duration);
            toast.show();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
