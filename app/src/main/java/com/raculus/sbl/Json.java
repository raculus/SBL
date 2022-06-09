package com.raculus.sbl;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class Json {
    public JSONArray Parser(String strJson){
        JSONArray jsonArray = new JSONArray();
        try {
            JSONObject object = new JSONObject(strJson);
            JSONObject response = new JSONObject(object.getString("response"));
            JSONObject header = new JSONObject(response.getString("header"));
            int resultCode = Integer.parseInt(header.getString("resultCode"));
            if(resultCode != 0){
                Log.e("Err", "OpenAPI Server result code: "+resultCode);
                return null;
            }
            JSONObject body = new JSONObject(response.getString("body"));
            JSONObject items = new JSONObject(body.getString("items"));

            jsonArray = items.getJSONArray("item");
            if(jsonArray == null)
                jsonArray.put(items.getJSONObject("item"));

            if(jsonArray.length() < 0)
                return jsonArray;

            return jsonArray;
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }
}
