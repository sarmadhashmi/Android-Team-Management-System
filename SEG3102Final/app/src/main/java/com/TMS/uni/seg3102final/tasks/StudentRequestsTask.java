package com.TMS.uni.seg3102final.tasks;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import com.TMS.uni.seg3102final.AcceptNewStudents;
import com.TMS.uni.seg3102final.MainActivity;
import com.TMS.uni.seg3102final.R;
import com.TMS.uni.seg3102final.SetupParameters;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.ConnectException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.security.InvalidParameterException;
import java.util.ArrayList;
import java.util.Arrays;

public class StudentRequestsTask extends AsyncTask<String, JSONObject, JSONObject> {
    Activity activity;
    public StudentRequestsTask(Activity activity) {
        this.activity = activity;
    }

    @Override
    protected JSONObject doInBackground(String[] params) {
        try {
            JSONObject credentials = new JSONObject();
            ArrayList<String> list = new ArrayList<String>(Arrays.asList(params));
            list.remove(0);
            JSONArray jsArray = new JSONArray(list);

            String teamId = params[0];
            credentials.put("team_id", teamId);
            credentials.put("list_of_usernames", jsArray);

            URL url = new URL("http://" + MainActivity.IP_ADDRESS + ":3001/acceptMembers");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setConnectTimeout(MainActivity.TIMEOUT);
            conn.setReadTimeout(MainActivity.TIMEOUT);

            conn.setDoOutput(true);
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/json");

            SharedPreferences settings = activity.getSharedPreferences("auth",
                    Context.MODE_PRIVATE);
            String token = settings.getString("access_token", "defaultvalue");

            conn.setRequestProperty("Authorization", "jwt " + token);

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
        try {
            ((AcceptNewStudents) activity).dismiss();
            if(response.getString("status").equals("200"))
                ((AcceptNewStudents) activity).displaySuccessMessage(response.getString("message"));
            else
            ((AcceptNewStudents) activity).displayMessage(response.getString("message"));
        } catch (JSONException e) {
            ((AcceptNewStudents) activity).displayMessage("UNEXPECTED ERROR");
        }
    }
}
