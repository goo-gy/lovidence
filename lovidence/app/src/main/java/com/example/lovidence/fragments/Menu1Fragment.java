package com.example.lovidence.fragments;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.work.WorkManager;

import com.example.lovidence.Matching;
import com.example.lovidence.PostAsync.PostAsync;
import com.example.lovidence.R;
import com.example.lovidence.splash.SplashActivity;

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
    private static TextView text_temp;
    WorkManager workManager;
    // ---------------------------------------- googy
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        menu1_context = getContext();
        View view = (View)inflater.inflate(R.layout.fragment_menu1, container, false);
        text_date = (TextView)(view.findViewById(R.id.main_date));
        text_temp = (TextView) view.findViewById(R.id.couple_temp);
        the_date = Calendar.getInstance();
        update();// update

        // ---------------------------------------- config
        Button btn_matching = (Button)view.findViewById(R.id.config_matching);
        btn_matching.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(getContext(), "Matching!", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(menu1_context, Matching.class);
                startActivity(intent);
                getActivity().finish();
            }
        });

        Button btn_test = (Button)view.findViewById(R.id.config_logout);
        btn_test.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                LogOutAction();
            }
        });

        Button btn_delete = (Button)view.findViewById(R.id.config_delete_account);
        btn_delete.setOnClickListener(new View.OnClickListener() {
            AlertDialog.Builder builder
                    = new AlertDialog.Builder(getActivity());
            boolean confirmDelete = false;
            @Override
            public void onClick(View view) {
                builder.setTitle("계정삭제")
                        .setMessage("계정을 삭제하시겠습니까?")
                        .setPositiveButton("확인", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                String data = "";
                                PostAsync deleteAsync = new PostAsync();
                                SharedPreferences sharedPref = getActivity().getSharedPreferences("USERINFO", Context.MODE_PRIVATE);
                                String id = sharedPref.getString("USERID", "");
                                if (id.equals("")) {
                                    Log.e("Error", "user id is not stored");
                                }
                                try {
                                    data = URLEncoder.encode("u_id", "UTF-8") + "=" + URLEncoder.encode(id, "UTF-8");

                                    String result = deleteAsync.execute("userDelete.php", data).get();
                                    if (result.equals("success")) {
                                        Log.e("삭제성공", "success");
                                        Toast.makeText(getActivity(), "삭제완료", Toast.LENGTH_SHORT).show();
                                        LogOutAction();
                                    } else {
                                        Log.e("삭제실패?", result);
                                        Toast.makeText(getActivity(), "삭제실패", Toast.LENGTH_SHORT).show();
                                        LogOutAction();
                                    }
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                        })
                        .setNegativeButton("취소", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                            }
                        }).create().show();
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
        String sendMessage="";
        sharedPref = menu1_context.getSharedPreferences("USERINFO", Context.MODE_PRIVATE);
        String str_date = sharedPref.getString("date", "");
        if(str_date.equals(""))
        {
            PostAsync checkMatchAsync = new PostAsync();
            String usrId = sharedPref.getString("USERID","IDError");
            /********************************************/
            String coupleTemp = sharedPref.getString("COUPLERANK_temp"," ");    //TODO.......!!!!
            text_temp.setText(coupleTemp);
            String data;
            try {
                data = URLEncoder.encode("u_usr", "UTF-8") + "=" + URLEncoder.encode(usrId, "UTF-8");
                sendMessage = checkMatchAsync.execute("IsMatched.php",data).get();
                Log.e("??",sendMessage);
                String[] args = sendMessage.split("@"); //0 is couple id and 1 is date
                sharedPref = menu1_context.getSharedPreferences("USERINFO", Context.MODE_PRIVATE);  //입력한 값을저장
                SharedPreferences.Editor editor = sharedPref.edit();
                editor.putString("COUPLEID",args[0]);
                editor.putString("date", args[1]);
                editor.putLong("LASTUPDATE", 0);
                Log.e("COUPLEID",args[0]);
                Log.e("date",args[1]);
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
    private void LogOutAction() {
        Intent intent = new Intent(getActivity(), SplashActivity.class);
        startActivity(intent);
        if (workManager != null) {
            Log.e("hello?", "hell?");
            workManager.cancelAllWork();
            Toast.makeText(getContext(), "all task deleted...", Toast.LENGTH_SHORT);
        }
        SharedPreferences sharedPref = getActivity().getSharedPreferences("USERINFO", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.remove("USERID").commit();
        editor.remove("date").commit();
        editor.clear().commit();//??
        Toast.makeText(getContext(), "로그아웃 되었습니다.", Toast.LENGTH_SHORT).show();
        getActivity().finish();
    }
}