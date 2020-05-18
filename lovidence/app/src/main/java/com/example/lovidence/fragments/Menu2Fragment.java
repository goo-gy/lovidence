package com.example.lovidence.fragments;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Geocoder;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;

import com.example.lovidence.*;
import com.example.lovidence.R;
import com.example.lovidence.SQLite.Couple_Location;
import com.example.lovidence.SQLite.Couple_LocationDao;
import com.example.lovidence.SQLite.MyDatabase;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import java.util.Locale;

public class Menu2Fragment extends Fragment {
    private Button createQRBtn;
    private Button scanQRBtn;
    private TextView DB_content;
    private getAsyncTask getDB;
    private MyDatabase db;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_menu2, container, false);
        db = MyDatabase.getAppDatabase(getActivity());
        getDB = new getAsyncTask(getActivity(),db.todoDao());

        DB_content = view.findViewById(R.id.showsDB);
        try {
            DB_content.setText(getDB.execute().get());
        }catch (Exception e){e.printStackTrace();}

        return view;
    }
    public static class getAsyncTask extends AsyncTask<Void, Void, String> {
        private Couple_LocationDao mTodoDao;
        private Context context;

        public  getAsyncTask(Context context, Couple_LocationDao todoDao){
            this.mTodoDao = todoDao;
            this.context = context;
        }

        @Override //백그라운드작업(메인스레드 X)
        protected String doInBackground(Void ...voids) {
            Log.e("??",mTodoDao.getAll().toString());
            Geocoder geo;
            geo = new Geocoder(context, Locale.getDefault());
            String result="";
            for(int i=0; i<mTodoDao.getAll().size(); i++){
                Couple_Location location = mTodoDao.getAll().get(i);
                try{result +=geo.getFromLocation(location.getLocationX(),location.getLocationY(),1).get(0).getAdminArea();result +="\n";}
                catch (Exception e){Log.e(Double.toString(location.getLocationX()),Double.toString(location.getLocationX()));}
            }
            return result;
        }
    }
}