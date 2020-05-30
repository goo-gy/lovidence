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
import androidx.lifecycle.Observer;
import androidx.room.Room;

import com.example.lovidence.MainActivity;
import com.example.lovidence.PostAsync.PostAsync;
import com.example.lovidence.R;
import com.example.lovidence.SQLite.Couple_Location;
import com.example.lovidence.SQLite.Couple_LocationDao;
import com.example.lovidence.SQLite.MyDatabase;

import net.daum.mf.map.api.MapCircle;
import net.daum.mf.map.api.MapPoint;
import net.daum.mf.map.api.MapPolyline;
import net.daum.mf.map.api.MapView;

import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Iterator;
import java.util.List;

import androidx.lifecycle.Observer;

//지도
//저장된 커플 좌표를 모두 가져옴
public class Menu3Fragment extends Fragment {
    ViewGroup viewGroup;

    MapView mapView;
    MapPolyline polyline;
    boolean set_focus;

    private class Thread_DB extends Thread {
        private int thread_number;
        private List<Couple_Location> location_list;

        public Thread_DB(int number) {
            this.thread_number = number;
        }
        public void run() {
            MyDatabase db = MyDatabase.getAppDatabase(getContext());
            location_list = db.todoDao().getAll();
        }
        public List<Couple_Location> get_location_data()
        {
            return location_list;
        }
    }

    public void initialize_map() {
        set_focus = false;
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

        initialize_map();
        Button button_gps = (Button) viewGroup.findViewById(R.id.btn_get_gps);
        Button button_update = (Button) viewGroup.findViewById(R.id.btn_update);

        button_gps.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MainActivity main_activity = (MainActivity) getActivity();
                main_activity.get_GPS();
            }
        });

        button_update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Thread_DB db_thread = new Thread_DB(0);
                db_thread.start();
                try {
                    db_thread.join();
                    List<Couple_Location>location_list = db_thread.get_location_data();

                    Iterator iterator = location_list.iterator();
                    int circle_count = 0;
                    double latitude = 0;
                    double longitude = 0;

                    mapView.removeAllCircles();
                    while(iterator.hasNext())
                    {
                        Couple_Location location = (Couple_Location)iterator.next();
                        latitude = location.getLocationX();
                        longitude = location.getLocationY();

                        MapCircle new_circle = new MapCircle(
                                MapPoint.mapPointWithGeoCoord(latitude, longitude), // center
                                30, // radius
                                Color.argb(128, 0xDD, 0xEE, 0), // strokeColor
                                Color.argb(128, 55, 0xFF, 0xEE) // fillColor
                        );
                        new_circle.setTag(circle_count);
                        mapView.addCircle(new_circle);
                        circle_count++;
                    }
                    mapView.setMapCenterPoint(MapPoint.mapPointWithGeoCoord(latitude, longitude), true);

                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });
        return viewGroup;
    }
}