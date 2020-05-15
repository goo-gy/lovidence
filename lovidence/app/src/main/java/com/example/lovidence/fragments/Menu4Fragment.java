package com.example.lovidence.fragments;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
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
import androidx.work.WorkManager;

import com.example.lovidence.MainActivity;
import com.example.lovidence.Matching;
import com.example.lovidence.R;
import com.example.lovidence.splash.SplashActivity;
import com.example.lovidence.ui.login.LoginActivity;

import java.util.Calendar;

public class Menu4Fragment extends Fragment {
    ViewGroup viewGroup;
    Button button1;
    Button logoutBtn;
    WorkManager workManager;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        viewGroup = (ViewGroup) inflater.inflate(R.layout.fragment_menu4, container, false);
        button1 = (Button)viewGroup.findViewById(R.id.btn1);
        logoutBtn = (Button)viewGroup.findViewById(R.id.logout);
        //예약된 work 모두삭제
        button1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (workManager != null) {
                    Log.e("hello?","hell?");
                    workManager.cancelAllWork();
                    Toast.makeText(getContext(),"all task deleted...",Toast.LENGTH_SHORT);
                }
            }
        });
        logoutBtn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                Intent intent = new Intent(getActivity(), SplashActivity.class);
                startActivity(intent);
                if (workManager != null) {
                    Log.e("hello?","hell?");
                    workManager.cancelAllWork();
                    Toast.makeText(getContext(),"all task deleted...",Toast.LENGTH_SHORT);
                }
                SharedPreferences sharedPref =getActivity().getSharedPreferences("USERINFO", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPref.edit();
                editor.remove("USERID").commit();
                editor.remove("date").commit();
                getActivity().finish();
            }
        });
        return viewGroup;
    }
}