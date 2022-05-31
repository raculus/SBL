package com.raculus.sbl.ListView_Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.raculus.sbl.Gps;
import com.raculus.sbl.OpenAPI.NearbyStation;
import com.raculus.sbl.R;

import java.util.ArrayList;

public class NearbyStation_Adapter extends BaseAdapter {
    Context mContext = null;
    LayoutInflater mLayoutInFlater = null;
    ArrayList<NearbyStation> stationList;

    public NearbyStation_Adapter(Context context, ArrayList<NearbyStation> data){
        mContext = context;
        stationList = data;
        mLayoutInFlater = LayoutInflater.from(context);
    }
    @Override
    public int getCount(){
        return stationList.size();
    }

    @Override
    public NearbyStation getItem(int positstion) {
        return stationList.get(positstion);
    }

    @Override
    public long getItemId(int position){
        return position;
    }
    @Override
    public View getView(int position, View converView, ViewGroup parent){
        View view = mLayoutInFlater.inflate(R.layout.listview_nearby_item, null);

        TextView title = view.findViewById(R.id.station_title);
        TextView number = view.findViewById(R.id.station_number);
        TextView distance = view.findViewById(R.id.station_distance);

        NearbyStation station = stationList.get(position);
        String name = station.getNodeName();
        String num = ""+station.getNodeNum();
        double lat = station.getGpsLati();
        double lng = station.getGpsLong();
        Gps gps = new Gps();
        double dist = gps.Distance(35.25557359, 128.6409908, lat, lng);
        dist = Math.round(dist);

        title.setText(name);
        number.setText(num);
        distance.setText(dist +"m");

        return view;
    }
}
