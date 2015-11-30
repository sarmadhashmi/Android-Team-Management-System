package com.TMS.uni.seg3102final.tasks;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.widget.Toast;

import com.TMS.uni.seg3102final.CreateTeam;
import com.TMS.uni.seg3102final.MainActivity;
import com.TMS.uni.seg3102final.SetupParameters;
import com.TMS.uni.seg3102final.exceptions.InternetConnectException;

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

public class CreateTeamTask extends AsyncTask<Void, JSONObject, JSONObject> {
    Activity activity;
    String team_param_id;
    String team_name;
    JSONArray members;
    public CreateTeamTask(Activity activity, String team_param_id, String team_name, JSONArray membersData) {
        this.activity = activity;
        this.team_param_id = team_param_id;
        this.team_name = team_name;
        JSONArray members = new JSONArray();
        try {
            for (int i = 0; i < membersData.length(); i++) {
                members.put(membersData.getJSONObject(i).getString("username"));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        this.members = members;
    }
    @Override
    protected JSONObject doInBackground(Void... params) {
        try {
            MainActivity.checkInternetConnected(this.activity);
            JSONObject credentials = new JSONObject();
            credentials.put("team_param_id", team_param_id);
            credentials.put("team_name", team_name);
            credentials.put("team_members", members);
            URL url = new URL("http://" + MainActivity.IP_ADDRESS + ":3001/createTeam");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();

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
        } catch (InternetConnectException e) {
            return MainActivity.getObj("message", "You are not connected to the internet!!");
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
            ((CreateTeam) activity).dismiss();
            Toast.makeText(activity.getApplicationContext(), response.getString("message"), Toast.LENGTH_LONG).show();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
