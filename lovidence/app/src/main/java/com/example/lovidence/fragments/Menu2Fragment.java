package com.example.lovidence.fragments;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Geocoder;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;

import com.example.lovidence.*;
import com.example.lovidence.R;
import com.example.lovidence.SQLite.Couple_Location;
import com.example.lovidence.SQLite.Couple_LocationDao;
import com.example.lovidence.SQLite.MyDatabase;
import com.github.mikephil.charting.animation.Easing;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Locale;
import java.util.stream.Collectors;

public class Menu2Fragment extends Fragment {
    PieChart pieChart; //원형차트
    private getAsyncTask getDB;
    private MyDatabase db;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_menu2, container, false);
        db = MyDatabase.getAppDatabase(getActivity());
        getDB = new getAsyncTask(getActivity(),db.todoDao());
        pieChart= view.findViewById(R.id.piechart);


        try {
            setPieChart(getDB.execute().get());
        }catch (Exception e){e.printStackTrace();}

        return view;
    }
    public static class getAsyncTask extends AsyncTask<Void, Void, ArrayList<String>> {
        private Couple_LocationDao mTodoDao;
        private Context context;

        public  getAsyncTask(Context context, Couple_LocationDao todoDao){
            this.mTodoDao = todoDao;
            this.context = context;
        }

        @Override //백그라운드작업(메인스레드 X)
        protected ArrayList<String> doInBackground(Void ...voids) {
            Log.e("??",mTodoDao.getAll().toString());
            ArrayList<String> locations = new ArrayList<String>();
            Geocoder geo;
            geo = new Geocoder(context, Locale.getDefault());

            for(int i=0; i<mTodoDao.getAll().size(); i++){
                Couple_Location location = mTodoDao.getAll().get(i);
                try{locations.add(geo.getFromLocation(location.getLocationX(),location.getLocationY(),1).get(0).getAdminArea());}
                catch (Exception e){Log.e(Double.toString(location.getLocationX()),Double.toString(location.getLocationX()));}
            }
            return locations;
        }
    }
    void setPieChart(ArrayList<String> locations){
        pieChart.setUsePercentValues(true);             //percent사용
        pieChart.getDescription().setEnabled(false);    //descript사용 안함(default는 사용, 왼쪽하단면에 설명표시)
        pieChart.setExtraOffsets(5,10,5,5); //offset

        pieChart.setDragDecelerationFrictionCoef(0.95f);

        pieChart.setDrawHoleEnabled(true);             //도넛형으로 만들지? ㄴㄴ
        pieChart.setHoleColor(Color.WHITE);             //도넛형일경우 중간 빈공간 하얀색
        pieChart.setTransparentCircleRadius(61f);       //그리고 공간크기

        ArrayList<PieEntry> yValues = new ArrayList<PieEntry>();    //차트의 값의 array
        ArrayList<String> distinct = Lists.newArrayList(Sets.newHashSet(locations));
        for(String e: distinct){
            int count = Collections.frequency(locations,e);
            yValues.add(new PieEntry(count,e));
        }

        Description description = new Description();
        description.setText("세계 국가"); //라벨
        description.setTextSize(15);
        pieChart.setDescription(description);

        //pieChart.animateY(1000, Easing.EaseInOutCubic); //애니메이션

        PieDataSet dataSet = new PieDataSet(yValues,"Countries");
        dataSet.setSliceSpace(3f);
        dataSet.setSelectionShift(5f);
        dataSet.setColors(ColorTemplate.JOYFUL_COLORS);

        PieData data = new PieData((dataSet));
        data.setValueTextSize(10f);
        data.setValueTextColor(Color.YELLOW);

        pieChart.setData(data);

    }
}