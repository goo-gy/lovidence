package com.example.lovidence;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import java.util.Calendar;
import java.util.GregorianCalendar;

public class Menu3Fragment extends Fragment {
    ViewGroup viewGroup;
    Calendar baseCal = new GregorianCalendar(2018, Calendar.JANUARY, 11);
    Calendar targetCal = new GregorianCalendar(2018, Calendar.MAY, 24);
    long diffSec =(targetCal.getTimeInMillis() - baseCal.getTimeInMillis())/1000;
    long diffDays = diffSec / (24*60*60);
    String str="the gap is "+diffDays;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        viewGroup = (ViewGroup) inflater.inflate(R.layout.fragment_menu3, container, false);
        //change TextView att
        TextView dateText = viewGroup.findViewById(R.id.showDate);
        dateText.setText(str);
        return viewGroup;
    }
}