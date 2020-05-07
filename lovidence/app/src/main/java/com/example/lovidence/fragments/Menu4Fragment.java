package com.example.lovidence.fragments;

import android.app.DatePickerDialog;
import android.content.Intent;
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
import com.example.lovidence.ui.login.LoginActivity;

import java.util.Calendar;

public class Menu4Fragment extends Fragment {
    ViewGroup viewGroup;
    Button button;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        viewGroup = (ViewGroup) inflater.inflate(R.layout.fragment_menu4, container, false);
        button = (Button)viewGroup.findViewById(R.id.btn1);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), Matching.class);
                startActivity(intent);

            }
        });
        return viewGroup;
    }
}