package com.raculus.sbl.OpenAPI;

import android.util.Log;

import com.raculus.sbl.BuildConfig;
import com.raculus.sbl.Json;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class BusStop extends Station {
    private static String serviceKey = BuildConfig.openApiKey;

    public String getUrl2(int cityCode, String nodeId, String routeId){
        String url = "http://apis.data.go.kr/1613000/ArvlInfoInqireService/getSttnAcctoSpcifyRouteBusArvlPrearngeInfoList?serviceKey=";
        url += serviceKey;
        url += "&pageNo=1&numOfRows=9999&_type=json&cityCode="+cityCode+"&nodeId="+nodeId+"&routeId="+routeId;
        Log.d("BusStop URL", url);
        return url;
    }
    public Station getBusStop(String strJson) {
        Json json = new Json();
        JSONArray jsonArray = json.Parser(strJson);
        if(jsonArray == null){
            return null;
        }
        JSONObject jsonObject = null;

        try {
            jsonObject = jsonArray.getJSONObject(0);
            Station station = new Station();
            station.prevStationCnt = jsonObject.getInt("arrprevstationcnt");    //도착까지 남은 정류장 수
            station.arriveSecond = jsonObject.getInt("arrtime");                //도착까지 예상시간(초)
            station.routeId = jsonObject.getString("routeid");               //노선 ID (ex: CWB123123)
            station.routeNum = jsonObject.get("routeno")+"";                    //노선 번호 (ex: 212)
            station.routeType = jsonObject.getString("routetp");                //노선 유형 (ex: 간선버스)
            station.stationName = jsonObject.getString("nodenm");

            return station;
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }
}