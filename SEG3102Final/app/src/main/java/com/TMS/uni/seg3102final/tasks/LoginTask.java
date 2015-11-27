package com.TMS.uni.seg3102final.tasks;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import com.TMS.uni.seg3102final.MainActivity;
import com.TMS.uni.seg3102final.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.ConnectException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.security.InvalidParameterException;

public class LoginTask extends AsyncTask<String, JSONObject, JSONObject> {
    Activity activity;
    public LoginTask(Activity activity) {
        this.activity = activity;
    }

    @Override
    protected JSONObject doInBackground(String[] params) {
        try {
            String username = params[0];
            String password = params[1];
            JSONObject credentials = new JSONObject();
            credentials.put("username", username);
            credentials.put("password", password);

            URL url = new URL("http://" + MainActivity.IP_ADDRESS + ":3001/auth");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setConnectTimeout(MainActivity.TIMEOUT);
            conn.setReadTimeout(MainActivity.TIMEOUT);
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
        } catch (ConnectException | SocketTimeoutException e) {
            return MainActivity.getObj("message", "Seems like the server is down or cannot be reached for some reason at this moment!");
        } catch (JSONException e) {
            return MainActivity.getObj("message", "The returned data was not in the correct format!");
        } catch (MalformedURLException e) {
            return MainActivity.getObj("message", "The URL is not in the correct format!");
        } catch (IOException e) {
            return MainActivity.getObj("message", "Could not get data from server!");
        }
    }

    protected void onPostExecute(JSONObject response) {
        ((MainActivity) activity).dismiss();

        try
        {
            if(response.has("access_token"))
            {
                SharedPreferences settings = activity.getSharedPreferences("auth",
                        Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = settings.edit();
                editor.putString("access_token", response.getString("access_token"));
                editor.commit();

                String userType = response.getString("user_type");
                ((MainActivity) activity).loadOperations(userType);
            } else {
                ((MainActivity) activity).displayMessage(response.getString("message"));
            }
        } catch (JSONException e) {
            ((MainActivity) activity).displayMessage("Unexpected Error");
            e.printStackTrace();
        }
    }
}
