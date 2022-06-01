package com.raculus.sbl.ListView_Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.raculus.sbl.R;
import com.raculus.sbl.OpenAPI.Station;

import java.util.ArrayList;

public class Station_Adapter extends BaseAdapter {
    Context mContext = null;
    LayoutInflater mLayoutInFlater = null;
    ArrayList<Station> stationList;

    public Station_Adapter(Context context, ArrayList<Station> data){
        mContext = context;
        stationList = data;
        mLayoutInFlater = LayoutInflater.from(context);
    }
    @Override
    public int getCount(){
        return stationList.size();
    }

    @Override
    public Station getItem(int positstion) {
        return stationList.get(positstion);
    }

    @Override
    public long getItemId(int position){
        return position;
    }
    @Override
    public View getView(int position, View converView, ViewGroup parent){
        View view = mLayoutInFlater.inflate(R.layout.listview_arrival_bus_item, null);

        TextView busNum = view.findViewById(R.id.busNum);
        TextView arrivalTime = view.findViewById(R.id.arrivalTime);
        TextView busType = view.findViewById(R.id.busType);

        Station station = stationList.get(position);
        int arrivMinutes = station.getArriveMinutes();
        int routeNum = station.getRouteNum();
        String routeType = station.getRouteType();

        busNum.setText(routeNum+"");
        busType.setText(routeType);
        arrivalTime.setText(arrivMinutes+"분");

        return view;
    }
}
