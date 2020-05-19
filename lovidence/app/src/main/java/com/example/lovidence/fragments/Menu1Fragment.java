package com.example.lovidence.fragments;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.lovidence.Matching;
import com.example.lovidence.PostAsync.PostAsync;
import com.example.lovidence.R;

import java.net.URI;
import java.net.URLEncoder;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;

public class Menu1Fragment extends Fragment {

    // ---------------------------------------- googy
    Context menu1_context;
    public Calendar the_date;
    TextView text_date;
    SharedPreferences sharedPref;
    // ---------------------------------------- googy
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        menu1_context = getContext();
        View view = (View)inflater.inflate(R.layout.fragment_menu1, container, false);
        text_date = (TextView)(view.findViewById(R.id.main_date));
        the_date = Calendar.getInstance();
        update();// update

        return view;
    }

    private class Callback extends WebViewClient{
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url){
            return (false);
        }
    }

    // ---------------------------------------- googy
    public void update(){
        String sendMessage="";
        sharedPref = menu1_context.getSharedPreferences("USERINFO", Context.MODE_PRIVATE);
        String str_date = sharedPref.getString("date", "");
        if(str_date.equals(""))
        {
            PostAsync checkMatchAsync = new PostAsync();
            String usrId = sharedPref.getString("USERID","IDError");
            String data;
            try {
                data = URLEncoder.encode("u_usr", "UTF-8") + "=" + URLEncoder.encode(usrId, "UTF-8");
                sendMessage = checkMatchAsync.execute("IsMatched.php",data).get();
                sharedPref = menu1_context.getSharedPreferences("USERINFO", Context.MODE_PRIVATE);  //입력한 값을저장
                SharedPreferences.Editor editor = sharedPref.edit();
                editor.putString("date", sendMessage);
                editor.commit();
            }catch(Exception e){e.printStackTrace();}
        }
        str_date=sharedPref.getString("date","");
        if(!str_date.equals("fail")){
            SimpleDateFormat date_format = new SimpleDateFormat("yyyy-MM-dd");
            try {
                the_date.setTime(date_format.parse(str_date));
            } catch (ParseException e) {
                e.printStackTrace();
            }
            long love_day = get_love_day();
            text_date.setText(Long.toString(love_day));
        }
        else{
            text_date.setText("솔로");
        }
    }


    public long get_love_day(){
        Calendar today = Calendar.getInstance();
        long love_time = today.getTime().getTime() - the_date.getTime().getTime();
        long love_day = love_time/(24*60*60*1000);
        love_day = Math.abs(love_day) + 1;
        return love_day;
    }
    // ---------------------------------------- googy
}