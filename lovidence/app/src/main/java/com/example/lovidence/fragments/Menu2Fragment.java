package com.example.lovidence.fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.location.Geocoder;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.lovidence.PostAsync.PostAsync;
import com.example.lovidence.R;
import com.example.lovidence.SQLite.Couple_Location;
import com.example.lovidence.SQLite.Couple_LocationDao;
import com.example.lovidence.SQLite.MyDatabase;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.charts.ScatterChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.data.ScatterData;
import com.github.mikephil.charting.data.ScatterDataSet;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.github.mikephil.charting.utils.ColorTemplate;


import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.Formatter;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;

public class Menu2Fragment extends Fragment {
    private static PieChart  pieChart; //원형차트
    private getAsyncTask getDB;
    private MyDatabase db;
    private TextView text;
    private static int elements;
    private SharedPreferences sharedPref;
    private String[] Days = {"dd", "M", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun"};

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

        pieChart = view.findViewById(R.id.piechart);
        try {
            ArrayList<String> locationLists = getDB.execute().get();
            setPieChart(locationLists);
        }catch(Exception e){e.printStackTrace();}
        sharedPref = getActivity().getSharedPreferences("USERINFO",Context.MODE_PRIVATE);
        //--------------------------------------------
        BarChart chart = view.findViewById(R.id.barchart);
        ArrayList<BarEntry> NoOfEmp = new ArrayList();

        NoOfEmp.add(new BarEntry(1f, Float.parseFloat(sharedPref.getString("COUPLERANK_dist",""))));
        NoOfEmp.add(new BarEntry(2f, Float.parseFloat(sharedPref.getString("COUPLERANK_time",""))));
        NoOfEmp.add(new BarEntry(3f, Float.parseFloat(sharedPref.getString("COUPLERANK_all",""))));
        setBarChart(NoOfEmp,chart);
        //--------------------------------------------
        PostAsync checkMatchAsync = new PostAsync();
        SharedPreferences sharedPref = getActivity().getSharedPreferences("USERINFO",Context.MODE_PRIVATE);
        String couple_id = sharedPref.getString("COUPLEID","");
        String data2="";
        String sendMessage="";
        try{
            data2 = URLEncoder.encode("u_coupleId", "UTF-8") + "=" + URLEncoder.encode(couple_id, "UTF-8");
            sendMessage = checkMatchAsync.execute("type_request.php",data2).get();
            Log.e("유형",sendMessage);
        }catch (Exception e){e.printStackTrace();}
        //text.setText(sendMessage);
        Log.e("elements","현재 표본 갯수 : " + elements);

        //-------------------------------------------- time char
        Thread_DB db_thread = new Thread_DB(0);
        db_thread.start();
        List<Couple_Location> location_list = new ArrayList<Couple_Location>();
        try {
            db_thread.join();
            location_list = db_thread.get_location_data();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        ScatterChart time_chart = view.findViewById(R.id.time_chart);
        setTimeChart(location_list, time_chart);
        return view;
    }
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


    private static class getAsyncTask extends AsyncTask<Void, Void, ArrayList<String>> {
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
            elements = mTodoDao.getAll().size();


            for(int i=0; i<elements; i++){
                Couple_Location location = mTodoDao.getAll().get(i);
                try{locations.add(geo.getFromLocation(location.getLocationX(),location.getLocationY(),1).get(0).getAdminArea());}
                catch (Exception e){Log.e(Double.toString(location.getLocationX()),Double.toString(location.getLocationX()));}
            }
            return locations;
        }
    }

    void setBarChart(ArrayList<BarEntry> list, BarChart chart){

        BarDataSet bardataset = new BarDataSet(list, "Time(rate) Distance(rate) Ranking ");
        chart.animateY(5000);
        //그래프 격자 없애기
        chart.getAxisLeft().setDrawGridLines(false);
        chart.getXAxis().setDrawGridLines(false);
        chart.getAxisRight().setDrawGridLines(false);
        //밑의 최소값 최대값설정
        chart.getXAxis().setAxisMinimum(0);
        chart.getXAxis().setAxisMaximum(4);
        BarData data = new BarData(bardataset);      // MPAndroidChart v3.X 오류 발생
        data.setBarWidth(0.5f);
        bardataset.setColors(ColorTemplate.COLORFUL_COLORS);
        chart.setData(data);

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
        HashSet<String> distinct = new HashSet<String>(locations);

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
    private void setTimeChart(List<Couple_Location> locations, ScatterChart time_chart)
    {
        XAxis xAxis = time_chart.getXAxis();
        xAxis.setAxisMinimum(0);
        xAxis.setAxisMaximum(24);
        xAxis.setLabelCount(12);
        //xAxis.setValueFormatter(new Formatter());

        //-----------------------------
        final ArrayList<String> yEntrys = new ArrayList<>();
        for(int i = 0; i < Days.length; i++)
        {
            yEntrys.add(Days[i]);
        }

        YAxis yAxis_left = time_chart.getAxisLeft();
        yAxis_left.setAxisMinimum(1);
        yAxis_left.setAxisMaximum(7);
        yAxis_left.setGranularity(1f);

        YAxis yAxis_right = time_chart.getAxisRight();
        yAxis_right.setDrawLabels(false);
        yAxis_right.setDrawAxisLine(false);
        yAxis_right.setDrawGridLines(false);

        List<Entry> entries = new ArrayList<Entry>();
        Iterator iterator = locations.iterator();

        long divider = 24*60*60*1000;
        float divider_f = 60*60*1000;
        while(iterator.hasNext())
        {
            Couple_Location location = (Couple_Location)iterator.next();
            Long time = location.getTime();
            Date date = new Date(time);
            Calendar cal = Calendar.getInstance();
            cal.setTime(date);
            int day_of_week = (int)cal.get(Calendar.DAY_OF_WEEK);
            time = time%divider;
            //Toast.makeText(getContext(), Integer.toString(day_of_week), Toast.LENGTH_SHORT).show();
            entries.add(new Entry(time/divider_f + 9, day_of_week));
        }

        ScatterDataSet time_data_set = new ScatterDataSet(entries, "timeline");
        ScatterData time_data = new ScatterData(time_data_set);
        time_data.setDrawValues(false);
        time_chart.setData(time_data);
        time_chart.invalidate();
    }
    public class GraphAxisValueFormatter implements IAxisValueFormatter {
        private String[] mValues;
        // 생성자 초기화
        GraphAxisValueFormatter(String[] values){
            this.mValues = values;
        }

        @Override
        public String getFormattedValue(float value, AxisBase axis){
            return mValues[(int) value];
        }
    }
}