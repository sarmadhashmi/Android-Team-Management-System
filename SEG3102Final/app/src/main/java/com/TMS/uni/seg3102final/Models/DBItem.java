package com.TMS.uni.seg3102final.Models;

import android.util.JsonWriter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.security.InvalidParameterException;
import java.util.ArrayList;

public class DBItem {
    JSONObject obj;
    String text;
    public DBItem(JSONObject obj, String type) throws JSONException {
        if (!obj.has("_id")) {
            throw new InvalidParameterException("This object does not have an id.");
        }
        switch (type) {
            case "teamParams":
                text = obj.getString("instructor_name") + " -- " + obj.getString("course_code") + obj.getString("course_section") + "\n" + "Deadline: " + obj.getString("deadline");
                break;
            case "student":
                text = obj.getString("firstName") + " " + obj.getString("lastName");
                break;
            case "team":
                text = obj.getString("teamName");
                break;
            default:
                text = obj.getString("_id");
                break;
        }
        this.obj = obj;
    }

    public String getText() {
        return text;
    }

    public String getId() throws JSONException {
        return obj.getString("_id");
    }

    public ArrayList<String> JSONArrayToStringArray(String key) throws JSONException {
        JSONArray arr = obj.getJSONArray(key);
        ArrayList<String> strings = new ArrayList<String>();
        try {
            for (int i = 0; i < arr.length() - 1; i++) {
                strings.add(arr.getString(i));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return strings;
    }

    public String getKey(String key) throws JSONException {
        return obj.getString(key);
    }

    public JSONObject getObj() {
        return obj;
    }

    @Override
    public String toString() {
        return getText();
    }

    @Override
    public boolean equals(Object o) {
        if (o == null) return false;
        if (o == this) return true;
        if (!(o instanceof DBItem)) return false;
        DBItem item = (DBItem) o;
        try {
            return item.getId().equals(this.getId());
        } catch (JSONException e) {
            return false;
        }
    }
}
