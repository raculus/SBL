package com.raculus.sbl.OpenAPI;

import com.raculus.sbl.BuildConfig;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/*
 * 서비스 가능 도시목록 조회
 * 도시명, 도시코드 반환 ex)"창원시", 38010
 */
class City{
    private String cityName;
    private int cityCode;

    public String getCityName(){
        return this.cityName;
    }
    public int getCityCode(){
        return this.cityCode;
    }

    private static String serviceKey = BuildConfig.openApiKey;
    public String getURL(){
        String urlBuilder = "http://apis.data.go.kr/1613000/BusSttnInfoInqireService/getCtyCodeList?serviceKey=";
        urlBuilder += serviceKey + "&_type=json";
        return urlBuilder;
    }

    public ArrayList<City> getCityList(String strJson){
        ArrayList<City> cityList = new ArrayList<>();
        try {
            JSONObject response = new JSONObject(strJson);
            JSONObject body = new JSONObject(response.getString("response"));
            JSONObject items = new JSONObject(body.getString("body"));
            JSONObject item = new JSONObject(items.getString("items"));
            JSONArray cityArray = item.getJSONArray("item");

            for(int index = 0; index < cityArray.length(); index++){
                JSONObject cityObject = cityArray.getJSONObject(index);

                String cityName = cityObject.getString("cityname");
                int cityCode = cityObject.getInt("citycode");

                City city = new City();
                city.cityName = cityName;
                city.cityCode = cityCode;
                cityList.add(city);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return cityList;
    }
}