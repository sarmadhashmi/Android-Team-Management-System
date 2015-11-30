package com.TMS.uni.seg3102final.tasks;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.widget.Toast;

import com.TMS.uni.seg3102final.JoinTeam;
import com.TMS.uni.seg3102final.MainActivity;
import com.TMS.uni.seg3102final.VisualizeStudentTeams;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ConnectException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;

public class TeamsInTeamParamTask extends AsyncTask<String, JSONObject, JSONObject> {
    Activity activity;
    public TeamsInTeamParamTask(Activity activity) {
        this.activity = activity;
    }

    @Override
    protected JSONObject doInBackground(String [] params) {
        try {

            String teamParam_id = params[0];

            String charset = "UTF-8";
            String query = String.format("teamParam_id=%s",
                    URLEncoder.encode(teamParam_id, charset));


            URL url = new URL("http://" + MainActivity.IP_ADDRESS + ":3001/teamsInTeamParam" + "?" + query);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setConnectTimeout(MainActivity.TIMEOUT);
            conn.setReadTimeout(MainActivity.TIMEOUT);
            conn.setRequestMethod("GET");
            conn.setRequestProperty("Accept-Charset", charset);

            SharedPreferences settings = activity.getSharedPreferences("auth",
                    Context.MODE_PRIVATE);
            String token = settings.getString("access_token", "defaultvalue");

            conn.setRequestProperty("Authorization", "jwt " + token);
            conn.setRequestProperty("teamParam_id", params[0]);

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
            JoinTeam context = (JoinTeam) activity;
            context.dismiss();
            if (response.has("list_of_teams")) {
                if (response.getJSONArray("list_of_teams").length() > 0) {
                    context.showTeams(response.getJSONArray("list_of_teams"));
                } else {
                    Toast.makeText(activity.getApplicationContext(), "No teams found.", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(activity.getApplicationContext(), response.getString("message"), Toast.LENGTH_LONG).show();
            }
        } catch (JSONException e) {
            Toast.makeText(activity.getApplicationContext(), "UNEXPECTED ERROR", Toast.LENGTH_LONG).show();
        }
    }
}
