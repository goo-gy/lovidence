package com.example.lovidence;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.work.Constraints;
import androidx.work.NetworkType;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import com.example.lovidence.PostAsync.PostAsync;
import com.example.lovidence.SQLite.Couple_Location;
import com.example.lovidence.SQLite.Couple_LocationDao;
import com.example.lovidence.SQLite.MyDatabase;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class MeetCheck extends Worker {
    private Calendar calendar;
    static public Context context;

    public MeetCheck(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
        this.context = context;
    }

    @Override
    public Result doWork() {
        calendar = Calendar.getInstance();
        Long time = calendar.getTime().getTime();
        String myId = getInputData().getString("myId");
        Double latitude = getInputData().getDouble("latitude",-1);
        Double longitude = getInputData().getDouble("longitude",-1);
        HttpURLConnection httpURLConnection = null;
        String data="";
        String link="";
        Log.e("hi",Double.toString(latitude));
        try {
            data = URLEncoder.encode("u_time", "UTF-8") + "=" + URLEncoder.encode(Long.toString(time), "UTF-8");
            data +="&" +  URLEncoder.encode("u_usr", "UTF-8") + "=" + URLEncoder.encode(myId, "UTF-8");
            data +="&" +  URLEncoder.encode("u_x", "UTF-8") + "=" + URLEncoder.encode(Double.toString(latitude), "UTF-8");
            data +="&" +  URLEncoder.encode("u_y", "UTF-8") + "=" + URLEncoder.encode(Double.toString(longitude), "UTF-8");
            Log.e("myworker",data);
            link = "https://test-yetvm.run.goorm.io/test/"+"updateLocation2.php";
            URL url = new URL(link);
            httpURLConnection = (HttpURLConnection) url.openConnection();
            httpURLConnection.setRequestMethod("POST");
            httpURLConnection.setDoInput(true);
            httpURLConnection.setDoOutput(true);
            OutputStreamWriter wr = new OutputStreamWriter(httpURLConnection.getOutputStream());
            wr.write(data); //data 전송
            wr.flush();
            //결과 받음
            BufferedReader reader = new BufferedReader(new InputStreamReader(httpURLConnection.getErrorStream(), "UTF-8"));
            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                sb.append(line);
            }
            httpURLConnection.disconnect();
            Log.e("work1",sb.toString());
            if(sb.toString().equals("fail")){
                return Result.failure();
            }
            else {
                String[] locations = sb.toString().split("-");

                Log.e("latitude",locations[0]);
                Log.e("logitude",locations[1]);
                Log.e("logitude",locations[2]);
                MyDatabase db = MyDatabase.getAppDatabase(context);
                db.todoDao().insert(new Couple_Location(Long.parseLong(locations[0]),Double.parseDouble(locations[1]),Double.parseDouble(locations[2])));
                return Result.success();
            }
        } catch (Exception e) {
            Log.d("Exception occur...", data, e);
            httpURLConnection.disconnect();
            return Result.failure();
        }
    }
}
