package com.example.lovidence.splash;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.lovidence.MainActivity;
import com.example.lovidence.ui.login.LoginActivity;

import java.security.MessageDigest;

public class SplashActivity extends AppCompatActivity {
    SharedPreferences sharedPref;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //shared preference를 가져옴
        sharedPref = this.getSharedPreferences("USERINFO", Context.MODE_PRIVATE);
        //아이디가 기록되있으면 로그인안함
        if(!sharedPref.contains("USERID")) {
            getAppKeyHash();
            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);
        }
        else{
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
        }
        finish();
    }
    private void getAppKeyHash() {
        try {
            PackageInfo info = getPackageManager().getPackageInfo(getPackageName(), PackageManager.GET_SIGNATURES);
            for (Signature signature : info.signatures) {
                MessageDigest md;
                md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
                String something = new String(Base64.encode(md.digest(), 0));
                Log.e("Hash key", something);
            }
        } catch (Exception e) {
            // TODO Auto-generated catch block
            Log.e("name not found", e.toString());
        }
    }
}
