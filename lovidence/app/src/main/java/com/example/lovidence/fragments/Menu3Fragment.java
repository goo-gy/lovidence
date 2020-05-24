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
    //서버에서 저장된 커플의 모든 좌표는 database에 저장됨.
    //이것을 불러와서 사용



}