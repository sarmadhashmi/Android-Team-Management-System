package com.TMS.uni.seg3102final.tasks;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.widget.TextView;

import com.TMS.uni.seg3102final.MainActivity;
import com.TMS.uni.seg3102final.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.InvalidParameterException;

public class RegisterTask extends AsyncTask<String, JSONObject, JSONObject> {
    Activity activity;
    public RegisterTask(Activity activity) {
        this.activity = activity;
    }

    @Override
    protected JSONObject doInBackground(String[] params) {
        try {
            String username = params[0];
            String password = params[1];
            String email = params[2];
            String f_name = params[3];
            String l_name = params[4];
            String userType = params[4];

            JSONObject registerationInformation = new JSONObject();
            registerationInformation.put("username", username);
            registerationInformation.put("password", password);
            registerationInformation.put("email", email);
            registerationInformation.put("first_name", f_name);
            registerationInformation.put("last_name", l_name);
            registerationInformation.put("user_type", userType);

            URL url = new URL("http://" + MainActivity.IP_ADDRESS + ":3001/register");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();

            conn.setDoOutput(true);
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/json");

            SharedPreferences settings = activity.getSharedPreferences("auth",
                    Context.MODE_PRIVATE);
            String token = settings.getString("access_token", "defaultvalue");

            conn.setRequestProperty("Authorization", "jwt " + token);


            OutputStreamWriter writer = new OutputStreamWriter(conn.getOutputStream());
            writer.write(registerationInformation.toString());
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
            if(response.getString("message") != null)
            {
                ((MainActivity) activity).registerMessage(response.getString("message"));
            }
        } catch (JSONException e) {
            ((MainActivity) activity).registerMessage("Error Unable to Connect to Server");
            e.printStackTrace();
        }





    }
}
