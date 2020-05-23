package com.example.lovidence.fragments;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.lovidence.MainActivity;
import com.example.lovidence.PostAsync.PostAsync;
import com.example.lovidence.R;
import com.example.lovidence.SQLite.Couple_Location;
import com.example.lovidence.SQLite.Couple_LocationDao;

import net.daum.mf.map.api.MapCircle;
import net.daum.mf.map.api.MapPoint;
import net.daum.mf.map.api.MapPolyline;
import net.daum.mf.map.api.MapView;

import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Calendar;

//지도
//저장된 커플 좌표를 모두 가져옴
public class Menu3Fragment extends Fragment {
    ViewGroup viewGroup;

    MapView mapView;
    MapPolyline polyline;
    int circle_count;
    boolean set_focus;
    int address_number;
    Context menu3_context;

    public void initialize_map() {
        address_number = 0;
        set_focus = false;
        circle_count = 0;
        mapView = new MapView(getContext());
        ViewGroup mapViewContainer = (ViewGroup) viewGroup.findViewById(R.id.map_view);
        mapViewContainer.addView(mapView);

        polyline = new MapPolyline();
        polyline.setTag(1000);
        polyline.setLineColor(Color.argb(128, 255, 51, 0)); // Polyline 컬러 지정
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        viewGroup = (ViewGroup) inflater.inflate(R.layout.fragment_menu3, container, false);
        menu3_context = getActivity();
        getListOnServer();
        initialize_map();
        Button button_gps = (Button) viewGroup.findViewById(R.id.btn_get_gps);
        button_gps.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MainActivity main_activity = (MainActivity) getActivity();
                main_activity.get_GPS();
                set_point();
            }
        });
        return viewGroup;
    }

    public void set_point() {
        MainActivity main_activity = (MainActivity) getActivity();
        for (int i = address_number; i < main_activity.address_vector.size(); i++) {
            double latitude = main_activity.address_vector.elementAt(i).latitude;
            double longitude = main_activity.address_vector.elementAt(i).longitude;
            polyline.addPoint(MapPoint.mapPointWithGeoCoord(latitude, longitude));  // Polyline 좌표 지정.

            MapCircle new_circle = new MapCircle(
                    MapPoint.mapPointWithGeoCoord(latitude, longitude), // center
                    30, // radius
                    Color.argb(128, 0xDD, 0xEE, 0), // strokeColor
                    Color.argb(128, 55, 0xFF, 0xEE) // fillColor
            );
            new_circle.setTag(circle_count);
            mapView.addCircle(new_circle);
        }
        mapView.addPolyline(polyline);
        address_number = main_activity.address_vector.size();
        if (!set_focus) {
            mapView.setMapCenterPoint(MapPoint.mapPointWithGeoCoord(main_activity.address_vector.elementAt(0).latitude, main_activity.address_vector.elementAt(0).longitude), true);
            set_focus = true;
        }
    }
    //현재 받지못한 데이터를 서버에서 가져오는 method
    //받지못한 데이터를 구분하기위해 마지막으로 받아온 time을 preference에 기록함.
    private void getListOnServer(){
        PostAsync getLocations = new PostAsync();
        SharedPreferences sharedPref;
        sharedPref = menu3_context.getSharedPreferences("USERINFO", Context.MODE_PRIVATE);
        String couple_id  = sharedPref.getString("COUPLEID","");
        Long   lastUpdate = sharedPref.getLong("LASTUPDATE",0);
        Long   current    = Calendar.getInstance().getTime().getTime();
        String data="";
        try {
            data = URLEncoder.encode("u_couple", "UTF-8") + "=" + URLEncoder.encode(couple_id, "UTF-8");
            data += "&" + URLEncoder.encode("u_last", "UTF-8") + "=" + URLEncoder.encode(Long.toString(lastUpdate), "UTF-8");
            data += "&" + URLEncoder.encode("u_current", "UTF-8") + "=" + URLEncoder.encode(Long.toString(current), "UTF-8");
            //마지막 업데이트 시간부터 현재시간까지 모든 location을 가져오는 php 필요
            String result = getLocations.execute("userRegist.php",data).get();  //result는 좌표들과 시간임.(구분자로 구분해주던지 해야함.)
            if(true){   //성공
                SharedPreferences.Editor editor = sharedPref.edit();
                editor.putLong("LASTUPDATE",current);   //마지막시간을 현재시간으로.
               //TODO::                                         //여기서 받은 String을 array로 바꿔서 넣도록
            }
            else{   //실패시
                Log.e("update fail..","fail");
            }
        }catch (Exception e){e.printStackTrace();}

    }
    public static class putDBAsync extends AsyncTask<ArrayList<String>, Void, Void> {
        private Couple_LocationDao mTodoDao;

        public  putDBAsync(Couple_LocationDao todoDao){
            this.mTodoDao = todoDao;
        }

        @Override //백그라운드작업(메인스레드 X)
        protected Void doInBackground(ArrayList<String> ...params) {
            int size = params[0].size();

            for(int i=0; i<size; i++){
                String line = params[0].get(i);
                String[] args = line.split("!"); //TODO:: split어떻게할지.   //한개의 라인(element)에는 시간, latitude, longitude값이 들어있음 그것을 구분해줘야함
                long time = Long.parseLong(args[0]);
                Double latitude = Double.parseDouble(args[1]);
                Double longitude = Double.parseDouble(args[2]);
                Couple_Location value = new Couple_Location(time,latitude,longitude);   //database에 insert
                mTodoDao.insert(value);
            }

            return null;
        }
    }

}