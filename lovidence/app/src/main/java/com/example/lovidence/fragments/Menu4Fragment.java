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

import com.example.lovidence.LandMark.LandmarkActivity;
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
    WorkManager workManager;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        menu4_context = getActivity();
        viewGroup = (ViewGroup) inflater.inflate(R.layout.fragment_menu4, container, false);

        Intent intent = new Intent(menu4_context, LandmarkActivity.class);
        startActivity(intent);
        getActivity().finish();

        return viewGroup;
    }
}