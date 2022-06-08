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
    //TODO: change it
    public String stationName;
    public int prevStationCnt;
    public int arriveSecond;
    public String routeId;
    public String routeNum;
    public String routeType;
    public int cityCode;
    public String nodeId;
    /*
    protected String stationName;
    protected int prevStationCnt;
    protected int arriveSecond;
    protected String routeId;
    protected String routeNum;
    protected String routeType;
    protected int cityCode;
    protected String nodeId;
     */

    public String getNodeId() {
        return nodeId;
    }
    public void setCityCode(int cityCode) {
        this.cityCode = cityCode;
    }
    public void setArriveSecond(int arriveSecond) {
        this.arriveSecond = arriveSecond;
    }
    public void setNodeId(String nodeId) {
        this.nodeId = nodeId;
    }
    public int getCityCode(){ return cityCode; }
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
        return Math.round(arriveSecond / 60);
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
                station.routeId = jsonObject.getString("routeid");               //노선 ID (ex: CWB123123)
                station.routeNum = jsonObject.get("routeno")+"";                 //노선 번호 (ex: 212)
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
    /*
    * ArrayList<Station>을 json으로 바꾸고 String으로 반환
    * */
    public String listToJson(ArrayList<Station> stationArrayList){
        try {
            JSONObject root = new JSONObject();
            JSONObject response = new JSONObject();
            JSONObject resultCode = new JSONObject();
            JSONObject items = new JSONObject();
            JSONObject item = new JSONObject();

            JSONArray jsonArray = new JSONArray();
            for(Station s : stationArrayList){
                JSONObject object = new JSONObject();
                object.put("arrprevstationcnt", s.getPrevStationCnt());
                object.put("arrtime", s.arriveSecond);
                object.put("routeid", s.routeId);
                object.put("routeno", s.routeNum);
                object.put("routetp", s.routeType);
                object.put("nodenm", s.stationName);
                jsonArray.put(object);
            }
            item.put("item", jsonArray);
            items.put("items", item);
            resultCode.put("resultCode", "00");
            response.put("header", resultCode);
            response.put("body", items);
            root.put("response", response);

            Log.e("json create", root.toString());
            return root.toString();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
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