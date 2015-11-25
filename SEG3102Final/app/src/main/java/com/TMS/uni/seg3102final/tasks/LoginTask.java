package com.TMS.uni.seg3102final.tasks;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.TextView;

import com.TMS.uni.seg3102final.MainActivity;
import com.TMS.uni.seg3102final.R;

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
    public LoginTask(Activity activity) {
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

            URL url = new URL("http://" + MainActivity.IP_ADDRESS + ":3001/auth");
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

        ((MainActivity) activity).dismiss();

        try
        {
            String auth = response.getString("access_token");

            if(response.getString("access_token") != null)
            {
                SharedPreferences settings = activity.getSharedPreferences("auth",
                        Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = settings.edit();
                editor.putString("access_token", auth);
                editor.commit();

                String userType = response.getString("user_type");
                ((MainActivity) activity).loadOperations(userType);
            }
        } catch (JSONException e) {
            ((MainActivity) activity).displayError();
            e.printStackTrace();
        }
    }
}
