package com.raculus.sbl;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class Json {
    public JSONArray Parser(String strJson){
        try {
            JSONObject response = new JSONObject(strJson);
            JSONObject body = new JSONObject(response.getString("response"));
            JSONObject items = new JSONObject(body.getString("body"));
            JSONObject item = new JSONObject(items.getString("items"));
            JSONArray jsonArray = item.getJSONArray("item");
            return jsonArray;
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }
}
