package com.raculus.sbl;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class Station {
    private int prevStationCnt;
    private int arriveSecond;
    private String routeId;
    private int routeNum;
    private String routeType;

    public int getPrevStationCnt() {
        return prevStationCnt;
    }
    public int getArriveSecond(){
        return arriveSecond;
    }
    public String getRouteId(){
        return routeId;
    }
    public int getRouteNum(){
        return routeNum;
    }
    public String getRouteType(){
        return routeType;
    }

    private static String serviceKey = BuildConfig.openApiKey;

    public String getUrl(int cityCode, String nodeId){
        String url = "https://apis.data.go.kr/1613000/ArvlInfoInqireService/getSttnAcctoArvlPrearngeInfoList?serviceKey=";
        url += serviceKey;
        url += "&pageNo=1&numOfRows=9999&_type=json&cityCode=" + cityCode + "&nodeId=" + nodeId;
        return url;
    }

    public ArrayList<Station> getStation(String strJson) {
        ArrayList<Station> stationList = new ArrayList<>();
        Json json = new Json();
        JSONArray jsonArray = json.Parser(strJson);
        for (int index = 0; index < jsonArray.length(); index++) {
            try {
                JSONObject jsonObject = jsonArray.getJSONObject(index);

                Station station = new Station();
                station.prevStationCnt = jsonObject.getInt("arrprevstationcnt");    //도착까지 남은 정류장 수
                station.arriveSecond = jsonObject.getInt("arrtime");                //도착까지 예상시간(초)
                station.routeId = jsonObject.getString("routeid");               //노선 ID (ex: CWB123123)
                station.routeNum = jsonObject.getInt("routeno");                    //노선 번호 (ex: 212)
                station.routeType = jsonObject.getString("routetp");                //노선 유형 (ex: 간선버스)

                stationList.add(station);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return stationList;
    }
}
