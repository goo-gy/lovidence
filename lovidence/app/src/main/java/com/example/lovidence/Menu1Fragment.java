package com.example.lovidence;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import java.net.URI;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;

public class Menu1Fragment extends Fragment {
    ViewGroup viewGroup;
    WebView wView;

    // ---------------------------------------- googy
    Context menu1_context;
    public final String str_preference = "menu1";
    public Calendar the_date;
    TextView text_date;
    // ---------------------------------------- googy
    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
       /* wView = (WebView)getView().findViewById(R.id.webView);
        Intent intent = getActivity().getIntent();
        Uri data = intent.getData();
        wView.setWebViewClient(new WebViewClient());

        if(data!=null) wView.loadUrl(data.toString());
        else wView.loadUrl("http://google.com/");*/
    }
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        viewGroup =(ViewGroup) inflater.inflate(R.layout.fragment_menu1, container, false);
        /*
        wView = (WebView) viewGroup.findViewById(R.id.webView);
        Intent intent = getActivity().getIntent();
        Uri data = intent.getData();
        wView.getSettings().setJavaScriptEnabled(true);
        wView.setWebViewClient(new WebViewClient());

        if(data!=null) wView.loadUrl(data.toString());
        else wView.loadUrl("http://google.com/");
         */

        // ---------------------------------------- googy
        menu1_context = getContext();
        View view = (View)inflater.inflate(R.layout.fragment_menu1, container, false);
        text_date = (TextView)(view.findViewById(R.id.main_date));
        the_date = Calendar.getInstance();
        update();// update
        Button button_set = (Button)view.findViewById(R.id.btn_set);
        button_set.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DatePickerDialog dialog = new DatePickerDialog(menu1_context, listener, the_date.get(Calendar.YEAR), the_date.get(Calendar.MONTH), the_date.get(Calendar.DAY_OF_MONTH));
                dialog.show();
            }
        });
        // ---------------------------------------- googy
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
        SharedPreferences pref = menu1_context.getSharedPreferences(str_preference, menu1_context.MODE_PRIVATE);
        String str_date = pref.getString("date", "");
        if(str_date == "")
        {
            Calendar today = Calendar.getInstance();
            int year = today.get(Calendar.YEAR);
            int month = today.get(Calendar.MONTH);
            int day_of_month = today.get(Calendar.DAY_OF_MONTH);
            DatePickerDialog dialog = new DatePickerDialog(menu1_context, listener, year, month, day_of_month);
            dialog.show();
        }
        else{
            SimpleDateFormat date_format = new SimpleDateFormat("yyyy-MM-dd");
            try {
                the_date.setTime(date_format.parse(str_date));
            } catch (ParseException e) {
                e.printStackTrace();
            }
            long love_day = get_love_day();
            text_date.setText(Long.toString(love_day));
        }
    }

    private DatePickerDialog.OnDateSetListener listener = new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker view, int year, int month, int day) {
            the_date = Calendar.getInstance();    // 현재 시간을 받음.
            the_date.set(Calendar.YEAR, year);
            the_date.set(Calendar.MONTH, month);
            the_date.set(Calendar.DAY_OF_MONTH, day);

            SimpleDateFormat date_format = new SimpleDateFormat("yyyy-MM-dd");
            String str_the_date = date_format.format(the_date.getTime());
            //--------------------------------------------------------
            SharedPreferences pref = menu1_context.getSharedPreferences(str_preference, menu1_context.MODE_PRIVATE);
            SharedPreferences.Editor editor = pref.edit();
            editor.putString("date",str_the_date );
            editor.commit();
            //--------------------------------------------------------
            long love_day = get_love_day();
            text_date.setText(Long.toString(love_day));
        }
    };

    public long get_love_day(){
        Calendar today = Calendar.getInstance();
        long love_time = today.getTime().getTime() - the_date.getTime().getTime();
        long love_day = love_time/(24*60*60*1000);
        love_day = Math.abs(love_day) + 1;
        return love_day;
    }
    // ---------------------------------------- googy
}