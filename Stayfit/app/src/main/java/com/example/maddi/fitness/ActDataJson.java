package com.example.maddi.fitness;

import android.content.Context;
import android.util.Log;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ActDataJson {
    List<Map<String, ?>> ActList;
    public TextView t;

    public List<Map<String, ?>> getMoviesList() {
        return ActList;
    }

    public int getSize() {
        return ActList.size();
    }

    public HashMap getItem(int i) {
        if (i >= 0 && i < ActList.size()) {
            return (HashMap) ActList.get(i);
        } else
            return null;
    }

    public ActDataJson() {
        ActList = new ArrayList<Map<String, ?>>();
    }

    public void removeItem(int i) {
        ActList.remove(i);
    }

    public void addItem(int position, HashMap clone) {
        ActList.add(position, clone);
    }

    public void populateActDataJson(String activity_query, Context context) throws JSONException {
        ActList.clear(); // clear the list


        String ActArray = "{'activities':[{'activity_name':'test1','l1':25,'l2':30,'l3':35},{'activity_name':'test2','l1':125,'l2':130,'l3':135}]}";

        String json = "Assuming that here is your JSON response";
        try {
            JSONObject parentObject = new JSONObject(readJSONFromAsset(context));
            JSONArray activitiesJsonArray = parentObject.getJSONArray("activities");
            JSONObject jsonObj;
            for (int i = 0; i < activitiesJsonArray.length(); ++i) {
                jsonObj = activitiesJsonArray.getJSONObject(i);
                if (jsonObj.getString("activity_name").toLowerCase().contains(activity_query.toLowerCase()))
                {
                    String iname = jsonObj.getString("activity_name");
                    String ical = jsonObj.getString("155p");
                    ActList.add(createAct_brief(iname, ical));
                }
            }

        } catch (JSONException e) {
            // TODO Auto-generated catch block
            Log.d("MyDebugMsg", "JSONException in populateActDataJson");
            e.printStackTrace();
        }

    }

    public String readJSONFromAsset(Context context) {
        String json = null;
        try {
            InputStream is;
            is = context.getResources().openRawResource(R.raw.calories_burned_in_30_minute_activities);
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            json = new String(buffer, "UTF-8");
        } catch (IOException ex) {
            ex.printStackTrace();
            return null;
        }
        return json;
    }

    private static HashMap createAct_brief(String iname, String ical) {
        HashMap fd = new HashMap();

        fd.put("iname", iname);
        fd.put("ical", ical);
        return fd;
    }

    public static void longInfo(String str) {
        if (str.length() > 4000) {
            Log.i("ActArray1:", str.substring(0, 4000));
            longInfo(str.substring(4000));
        } else
            Log.i("ActArray2", str);
    }
}
