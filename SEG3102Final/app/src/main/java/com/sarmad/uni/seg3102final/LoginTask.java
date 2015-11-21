package com.sarmad.uni.seg3102final;

import android.app.Activity;
import android.app.Fragment;
import android.os.AsyncTask;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.InvalidParameterException;

public class LoginTask extends AsyncTask<String, JSONObject, JSONObject> {
    Activity activity;
    LoginTask(Activity activity) {
        this.activity = activity;
    }

    @Override
    protected JSONObject doInBackground(String[] params) {
        if (params.length < 2) throw new InvalidParameterException("Not enough parameters");
        try {
            String username = params[0];
            String password = params[1];
            JSONObject credentials = new JSONObject();
            credentials.put("username", username);
            credentials.put("password", password);

            URL url = new URL("http://192.168.0.107:3001/auth");
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
            TextView status = (TextView) activity.findViewById(R.id.register_status);
            status.setText(response.getString("message"));
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
