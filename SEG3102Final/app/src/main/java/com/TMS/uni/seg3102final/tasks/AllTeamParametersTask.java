package com.TMS.uni.seg3102final.tasks;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import com.TMS.uni.seg3102final.MainActivity;
import com.TMS.uni.seg3102final.R;
import com.TMS.uni.seg3102final.SelectTeamParameters;
import com.TMS.uni.seg3102final.SetupParameters;
import com.TMS.uni.seg3102final.VisualizeStudentTeams;

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

public class AllTeamParametersTask extends AsyncTask<Void, JSONObject, JSONObject> {
    Activity activity;
    public AllTeamParametersTask(Activity activity) {
        this.activity = activity;
    }

    @Override
    protected JSONObject doInBackground(Void... params) {
        try {
            URL url = new URL("http://" + MainActivity.IP_ADDRESS + ":3001/teamParams");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setConnectTimeout(MainActivity.TIMEOUT);
            conn.setReadTimeout(MainActivity.TIMEOUT);
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

        SelectTeamParameters context = (SelectTeamParameters) activity;
        context.dismiss();

        try {
            if (response.has("teamParams")) {
                if (response.getJSONArray("teamParams").length() > 0) {
                    context.showTeamParams(response.getJSONArray("teamParams"));
                } else {
                    context.displayMessage(response.getString("message"));
                }
            } else {
                context.displayMessage(response.getString("message"));
            }
        } catch (JSONException e) {
            context.displayMessage("Unexpected ERROR");
        }
    }
}
