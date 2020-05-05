package com.example.lovidence.fragments;

import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import com.example.lovidence.R;

import net.daum.mf.map.api.MapPoint;
import net.daum.mf.map.api.MapPolyline;
import net.daum.mf.map.api.MapView;


public class Menu3Fragment extends Fragment {
    ViewGroup viewGroup;

    MapView mapView;
    MapPolyline polyline;
    int circle_count;

    public void initialize_map()
    {
        circle_count = 0;
        mapView = new MapView(getContext());
        mapView.setMapCenterPoint(MapPoint.mapPointWithGeoCoord(35.733731, 128.081080), true);
        ViewGroup mapViewContainer = (ViewGroup)viewGroup.findViewById(R.id.map_view);
        mapViewContainer.addView(mapView);

        polyline = new MapPolyline();
        polyline.setTag(1000);
        polyline.setLineColor(Color.argb(128, 255, 51, 0)); // Polyline 컬러 지정
        polyline.addPoint(MapPoint.mapPointWithGeoCoord(35.733731, 128.081080));  // Polyline 좌표 지정.
        polyline.addPoint(MapPoint.mapPointWithGeoCoord(35.7000, 128.101080));  // Polyline 좌표 지정.
        mapView.addPolyline(polyline);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        viewGroup = (ViewGroup) inflater.inflate(R.layout.fragment_menu3, container, false);
        initialize_map();
        Button button_gps = (Button)viewGroup.findViewById(R.id.btn_get_gps);
        button_gps.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(getContext(), "Not Yet", Toast.LENGTH_SHORT).show();
            }
        });
        return viewGroup;
    }
}