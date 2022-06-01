package com.raculus.sbl.OpenAPI;

import android.util.Log;

import com.raculus.sbl.BuildConfig;
import com.raculus.sbl.Json;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;

/*
* 버스 도착 예정시간
* */
public class Station implements Comparable<Station>, Serializable {
    private String stationName;
    private int prevStationCnt;
    private int arriveSecond;
    private int arriveMinutes;
    private String routeId;
    private String routeNum;
    private String routeType;

    public String getStationName(){
        return stationName;
    }

    public int getPrevStationCnt() {
        return prevStationCnt;
    }
    public int getArriveSecond(){
        return arriveSecond;
    }
    public String getRouteId(){
        return routeId;
    }
    public String getRouteNum(){
        return routeNum;
    }
    public String getRouteType(){
        return routeType;
    }
    public int getArriveMinutes() {
        return arriveMinutes;
    }

    private static String serviceKey = BuildConfig.openApiKey;

    public String getUrl(int cityCode, String nodeId){
        String url = "http://apis.data.go.kr/1613000/ArvlInfoInqireService/getSttnAcctoArvlPrearngeInfoList?serviceKey=";
        url += serviceKey;
        url += "&pageNo=1&numOfRows=9999&_type=json&cityCode=" + cityCode + "&nodeId=" + nodeId;
        Log.d("Station URL", url);
        return url;
    }

    public ArrayList<Station> getStation(String strJson) {
        ArrayList<Station> stationList = new ArrayList<>();
        Json json = new Json();
        JSONArray jsonArray = json.Parser(strJson);
        if(jsonArray == null){
            return stationList;
        }
        for (int index = 0; index < jsonArray.length(); index++) {
            try {
                JSONObject jsonObject = jsonArray.getJSONObject(index);

                Station station = new Station();
                station.prevStationCnt = jsonObject.getInt("arrprevstationcnt");    //도착까지 남은 정류장 수
                station.arriveSecond = jsonObject.getInt("arrtime");                //도착까지 예상시간(초)
                station.arriveMinutes = Math.round(station.arriveSecond / 60);             //도착까지 예상시간(분)
                station.routeId = jsonObject.getString("routeid");               //노선 ID (ex: CWB123123)
                station.routeNum = jsonObject.getString("routeno")+"";                    //노선 번호 (ex: 212)
                station.routeType = jsonObject.getString("routetp");                //노선 유형 (ex: 간선버스)
                station.stationName = jsonObject.getString("nodenm");

                stationList.add(station);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        Collections.sort(stationList);
        return stationList;
    }

    //버스 남은시간 적은순대로 정렬을 위한 메서드
    @Override
    public int compareTo(Station station){
        if(this.arriveSecond < station.getArriveSecond()){
            return -1;
        }
        else if(this.arriveSecond > station.getArriveSecond()){
            return 1;
        }
        return 0;
    }
}
