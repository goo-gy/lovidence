package com.example.lovidence.fragments;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

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
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        viewGroup = (ViewGroup) inflater.inflate(R.layout.fragment_menu4, container, false);
        button1 = (Button)viewGroup.findViewById(R.id.btn1);
        logoutBtn = (Button)viewGroup.findViewById(R.id.logout);
        button1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), Matching.class);
                startActivity(intent);

            }
        });
        logoutBtn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                Intent intent = new Intent(getActivity(), SplashActivity.class);
                startActivity(intent);
                SharedPreferences sharedPref =getActivity().getSharedPreferences("USERINFO", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPref.edit();
                editor.remove("USERID").commit();
                getActivity().finish();
            }
        });
        return viewGroup;
    }
}