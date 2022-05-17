package com.raculus.sbl;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class NearbyNode {
    private int cityCode;
    private double gpsLati;
    private double gpsLong;
    private String nodeId;
    private String nodeName;
    private int nodeNum;

    public void setCityCode(int code){
        this.cityCode = code;
    }
    public int getCityCode(){
        return this.cityCode;
    }
    public double getGpsLati(){
        return this.gpsLati;
    }
    public double getGpsLong(){
        return this.gpsLong;
    }
    public String getNodeId(){
        return this.nodeId;
    }
    public String getNodeName(){
        return this.nodeName;
    }
    public int getNodeNum(){
        return this.nodeNum;
    }

    private static String serviceKey = BuildConfig.openApiKey;

    /*
    * 위도(latitude), 경도(longitude)를 받아
    * api에 요청할 주소를 만든다.
    * 한국 대략: 위도 34~37, 경도 126~128
    * */
    public String getUrl(double latitude, double longitude){
        String url = "http://apis.data.go.kr/1613000/BusSttnInfoInqireService/getCrdntPrxmtSttnList?serviceKey=";
        url += serviceKey;
        url += "&pageNo=1&numOfRows=9999&_type=json&gpsLati="+ latitude +"&gpsLong=" + longitude;
        Log.e("url", url);
        return url;
    }
    public ArrayList<NearbyNode> getNearbyNode(String strJson){
        //todo: set메서드 대신 변수에 할당
        ArrayList<NearbyNode> nearbyNodeList = new ArrayList<>();
        try {
            JSONObject response = new JSONObject(strJson);
            JSONObject body = new JSONObject(response.getString("response"));
            JSONObject items = new JSONObject(body.getString("body"));
            JSONObject item = new JSONObject(items.getString("items"));
            JSONArray jsonArray = item.getJSONArray("item");

            for(int index = 0; index < jsonArray.length(); index++){
                JSONObject jsonObject = jsonArray.getJSONObject(index);

                int cityCode = jsonObject.getInt("citycode");
                double gpsLati = jsonObject.getDouble("gpslati");
                double gpsLong = jsonObject.getDouble("gpslong");
                String nodeId = jsonObject.getString("nodeid");
                String nodeName = jsonObject.getString("nodenm");
                int nodeNum = jsonObject.getInt("nodeno");

                NearbyNode nearbyNode = new NearbyNode();
                nearbyNode.cityCode = cityCode;
                nearbyNode.gpsLati = gpsLati;
                nearbyNode.setGpsLati(gpsLati);
                nearbyNode.setGpsLong(gpsLong);
                nearbyNode.setNodeId(nodeId);
                nearbyNode.setNodeName(nodeName);
                nearbyNode.setNodeNum(nodeNum);

                nearbyNodeList.add(nearbyNode);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return nearbyNodeList;
    }
}
