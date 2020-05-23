package com.example.lovidence.fragments;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
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
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.work.WorkManager;

import com.example.lovidence.MainActivity;
import com.example.lovidence.Matching;
import com.example.lovidence.PostAsync.PostAsync;
import com.example.lovidence.R;
import com.example.lovidence.splash.SplashActivity;
import com.example.lovidence.ui.login.LoginActivity;

import java.net.URLEncoder;
import java.util.Calendar;

public class Menu4Fragment extends Fragment {
    ViewGroup viewGroup;
    Context menu4_context;
    Button button1;
    Button logoutBtn;
    Button mathingBtn;
    Button deleteBtn;
    WorkManager workManager;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        menu4_context = getActivity();
        viewGroup = (ViewGroup) inflater.inflate(R.layout.fragment_menu4, container, false);
        button1 = (Button) viewGroup.findViewById(R.id.btn1);
        logoutBtn = (Button) viewGroup.findViewById(R.id.logout);
        mathingBtn = (Button) viewGroup.findViewById(R.id.mtBtn);
        deleteBtn = (Button) viewGroup.findViewById(R.id.deleteAccount);
        //예약된 work 모두삭제
        button1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (workManager != null) {
                    Log.e("hello?", "hell?");
                    workManager.cancelAllWork();
                    Toast.makeText(getContext(), "all task deleted...", Toast.LENGTH_SHORT);
                }
            }
        });
        logoutBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                LogOutAction();
            }
        });
        mathingBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(menu4_context, Matching.class);
                startActivity(intent);
                getActivity().finish();
            }
        });
        deleteBtn.setOnClickListener(new View.OnClickListener() {
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
        return viewGroup;
    }

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
        getActivity().finish();
    }
}