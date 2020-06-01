package com.example.lovidence.fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.location.Geocoder;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.lovidence.PostAsync.PostAsync;
import com.example.lovidence.R;
import com.example.lovidence.SQLite.Couple_Location;
import com.example.lovidence.SQLite.Couple_LocationDao;
import com.example.lovidence.SQLite.MyDatabase;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.utils.ColorTemplate;

import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Locale;

public class Menu2Fragment extends Fragment {
    BarChart barChart; //원형차트
    private getAsyncTask getDB;
    private MyDatabase db;
    private TextView text;
    private static int elements;
    private SharedPreferences sharedPref;

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
        //barChart= view.findViewById(R.id.barchart);
        text = view.findViewById(R.id.elementsNum);
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
        text.setText(sendMessage);
        Log.e("elements","현재 표본 갯수 : " + elements);

        return view;
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

        BarDataSet bardataset = new BarDataSet(list, "No Of Employee");
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
    /*void setBarChart(ArrayList<String> locations){
        barChart.
        barChart.setUsePercentValues(true);             //percent사용
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


    }*/


}