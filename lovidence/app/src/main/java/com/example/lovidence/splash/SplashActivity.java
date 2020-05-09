package com.example.lovidence.splash;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.lovidence.MainActivity;
import com.example.lovidence.ui.login.LoginActivity;

public class SplashActivity extends AppCompatActivity {
    SharedPreferences sharedPref;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //shared preference를 가져옴
        sharedPref = this.getSharedPreferences("USERINFO", Context.MODE_PRIVATE);
        //아이디가 기록되있으면 로그인안함
        if(!sharedPref.contains("USERID")) {
            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);
        }
        else{
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
        }
        finish();
    }
}
