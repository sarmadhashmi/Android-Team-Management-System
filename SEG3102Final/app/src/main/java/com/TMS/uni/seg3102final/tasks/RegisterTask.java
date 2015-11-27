package com.TMS.uni.seg3102final.tasks;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.widget.TextView;
import android.widget.Toast;

import com.TMS.uni.seg3102final.MainActivity;
import com.TMS.uni.seg3102final.R;

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
            String userType = params[5];
            String pgmStudy = params[6];

            JSONObject registerationInformation = new JSONObject();
            registerationInformation.put("username", username);
            registerationInformation.put("password", password);
            registerationInformation.put("email", email);
            registerationInformation.put("first_name", f_name);
            registerationInformation.put("last_name", l_name);
            registerationInformation.put("user_type", userType);
            registerationInformation.put("programOfStudy", pgmStudy);

            System.out.println(registerationInformation.toString(4));

            URL url = new URL("http://" + MainActivity.IP_ADDRESS + ":3001/register");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();

            conn.setDoOutput(true);
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/json");


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
            if(response.has("message"))
            {
                ((MainActivity) activity).displayMessage(response.getString("message"));
            }
        } catch (JSONException e) {
            ((MainActivity) activity).displayMessage("Unexpected Error");
            e.printStackTrace();
        }
    }
}
