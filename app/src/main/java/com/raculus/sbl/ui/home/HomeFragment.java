package com.raculus.sbl.ui.home;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.raculus.sbl.Activity.NearbyActivity;
import com.raculus.sbl.Activity.SetAlarm_Activity;
import com.raculus.sbl.ListView_Adapter.Bus_Adapter;
import com.raculus.sbl.OpenAPI.Station;
import com.raculus.sbl.R;
import com.raculus.sbl.databinding.FragmentHomeBinding;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

public class HomeFragment extends Fragment{
    ListView listView;
    ArrayList<Station> stationArrayList = new ArrayList<>();
    Bus_Adapter bus_adapter;

    private static final String SETTING_NAME = "BusList";
    private void putSettingItem(String key, String val){
        SharedPreferences preferences = getActivity().getSharedPreferences(SETTING_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(key, val);
        editor.apply();
    }
    private String getSettingItem(String key) {
        return getActivity().getSharedPreferences(SETTING_NAME, 0).getString(key, null);
    }
    private void restoreList(){
         String strJson = getSettingItem("strJson");
         Log.e("strJson", strJson);
         if(strJson == null){
             return;
         }
         stationArrayList = new Station().getStation(strJson);
    }
    private void updateItem(){
        bus_adapter.notifyDataSetChanged();

        String strJson = new Station().listToJson(stationArrayList);
        if(strJson != null)
            putSettingItem("strJson", strJson);
    }

    private void addBus(Station station){
        stationArrayList.add(station);
        setListView();
    }
    private void setListView(){
        if(stationArrayList.size() > 0 ){
            bus_adapter = new Bus_Adapter(getContext(), stationArrayList);

            listView.setAdapter(bus_adapter);
            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                    Station s = bus_adapter.getItem(position);

                    //Dialog
                    AlertDialog.Builder dlg = new AlertDialog.Builder(getContext());
                    dlg.setTitle(s.getRouteNum()+""); //제목
                    String setAlarm = getResources().getString(R.string.set_alarm);
                    String remove = getResources().getString(R.string.remove);
                    final String[] itemArr = new String[] {setAlarm, remove};
                    //dialog에서 선택
                    dlg.setItems(itemArr, (dialog, index) -> {
                        if(index == 0){
                            Intent intent = new Intent(getActivity(), SetAlarm_Activity.class);
                            intent.putExtra("arriveMinutes", s.getArriveMinutes());
                            startActivity(intent);
                        }
                        else if(index == 1){
                            //listView에서 item제거
                            stationArrayList.remove(position);
                            listView.clearChoices();
                            updateItem();
                        }
                    });
                    dlg.show();
                }
            });
        }
    }
    ActivityResultLauncher<Intent> mStartForResult = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if(result.getResultCode() == Activity.RESULT_OK) {
                    Intent intent = result.getData();
                    Station station = (Station) intent.getSerializableExtra("Station");
                    addBus(station);
                }
            }
    );

    private FragmentHomeBinding binding;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        testValueAdd();
        restoreList();

        HomeViewModel homeViewModel =
                new ViewModelProvider(this).get(HomeViewModel.class);

        binding = FragmentHomeBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        FloatingActionButton fabAdd = root.findViewById(R.id.fabAdd);
        fabAdd.setOnClickListener(view -> busAdd());

        listView = root.findViewById(R.id.listView);
        setListView();

        Timer timer = new Timer();
        TimerTask timerTask = new TimerTask() {
            @Override
            public void run() {
                ArrayList<Station> list = new ArrayList<>();
                for(Station station : stationArrayList){
                    int arrivalSec = station.getArriveSecond();
                    if(arrivalSec >= 60){
                        arrivalSec -= 60;
                        station.setArriveSecond(arrivalSec);
                    }
                    list.add(station);
                }
                stationArrayList = list;
            }
        };
        /* 갱신
            // 60초마다 갱신
            Timer timer = new Timer();
            TimerTask timerTask = new TimerTask() {
                @Override
                public void run() {
                    if(stationArrayList.size() > 0) {
                        ArrayList<Station> list = new ArrayList<>();

                        Log.d("갱신", "메인화면 60초마다 갱신");
                        for (Station station : stationArrayList) {
                            Get get = new Get();
                            int cityCode = station.getCityCode();
                            String nodeId = station.getNodeId();
                            String routeId = station.getRouteId();
                            String url = new BusStop().getUrl2(cityCode, nodeId, routeId);
                            new Thread(new Runnable() {
                                @Override
                                public void run() {
                                    try {
                                        String strJson = get.HttpBody(url);
                                        BusStop busStop = new BusStop();
                                        Station s = busStop.getBusStop(strJson);
                                        Log.i("routeNum",s.getRouteNum());
                                        list.add(s);
                                    } catch (IOException e) {
                                        e.printStackTrace();
                                    }
                                }
                            });

                        }
                        if(list.size() > 0){
                            stationArrayList = list;
                            Message msg = handler.obtainMessage();
                            handler.sendMessage(msg);
                        }
                    }
                }
            };
            timer.schedule(timerTask, 0, 1*60000); //Timer 1분마다 실행
            */

        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
    private void busAdd(){
        Intent intent = new Intent(getActivity(), NearbyActivity.class);
        mStartForResult.launch(intent);
    }
    final Handler handler = new Handler(){
      public void handleMessage(Message message) {
          listView.clearChoices();
          updateItem();
      }
    };

    //TODO: it is debug code
    private void testValueAdd(){
        Station station = new Station();
        station.routeNum = "151";
        station.arriveSecond = 256;
        station.stationName = "정류장명";
        station.nodeId = "정류장id";
        station.routeId ="버스id";
        station.routeType = "노선 타입";

        Station station1 = new Station();
        station1.routeNum = "800";
        station1.arriveSecond = 528;
        station1.stationName = "정류장명22";
        station1.nodeId = "정류장id22";
        station1.routeId ="버스id22";
        station1.routeType = "노선 타입22";

        stationArrayList = new ArrayList<>();
        stationArrayList.add(station);
        stationArrayList.add(station1);
    }

    public void test(String test_val) {
        Log.d("test",test_val);
        for(Station station : stationArrayList){
            Log.d("", station.getRouteNum());
        }
    }
}