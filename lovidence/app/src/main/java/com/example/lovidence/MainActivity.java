package com.example.lovidence;
import com.example.lovidence.*;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.location.Address;
import android.location.Geocoder;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.MenuItem;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.work.Constraints;
import androidx.work.Data;
import androidx.work.ExistingPeriodicWorkPolicy;
import androidx.work.NetworkType;
import androidx.work.Operation;
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkManager;

import android.view.View;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.example.lovidence.SQLite.Couple_Location;
import com.example.lovidence.SQLite.MyDatabase;
import com.example.lovidence.fragments.*;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.io.IOException;
import java.security.MessageDigest;
import java.util.List;
import java.util.Locale;
import java.util.Vector;
import java.util.concurrent.TimeUnit;

public class MainActivity extends AppCompatActivity {
    PeriodicWorkRequest periodicRequest;
    SharedPreferences sharedPref;

    BottomNavigationView bottomNavigationView;
    Menu1Fragment menu1Fragment;
    Menu2Fragment menu2Fragment;
    Menu3Fragment menu3Fragment;
    Menu4Fragment menu4Fragment;
    Menu5Fragment menu5Fragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        sharedPref = MainActivity.this.getSharedPreferences("USERINFO", Context.MODE_PRIVATE);
        String myId = sharedPref.getString("USERID","");
        long lastUpdate = sharedPref.getLong("LASTUPDATE",0);
        if(!myId.equals("")) {
            get_GPS();
            Log.e("??","GPS");
            Data workData = new Data.Builder()
                    .putDouble("latitude", gpsTracker.getLatitude())
                    .putDouble("longitude", gpsTracker.getLongitude())
                    .putLong("lastUpdate",lastUpdate)
                    .putString("myId", myId)
                    .build();

            periodicRequest = new PeriodicWorkRequest.Builder(MeetCheck.class, 15, TimeUnit.MINUTES)
                    .setInputData(workData)
                    .build();
            WorkManager.getInstance(MainActivity.this)
                    .enqueueUniquePeriodicWork("transfer Location", ExistingPeriodicWorkPolicy.REPLACE, periodicRequest);
            //나중에 해야할것, worker내부에 또다른 worker?(alarm써도무관)을 사용하여 만남이 감지되었을시 유저에게 알람보내도록
        }

        //현재 서비스 상태 check,GPS와network연결
        if (!checkLocationServicesStatus()) {
            showDialogForLocationServiceSetting();          //로케이션세팅을위한 dialog열기
        }else {
            checkRunTimePermission();                       //실행에서 권한이 부여되있는지 확인후 부여 요청
        }
        bottomNavigationView = findViewById(R.id.bottomNavigationView);

        menu1Fragment = new Menu1Fragment();
        menu2Fragment = new Menu2Fragment();
        menu3Fragment = new Menu3Fragment();
        menu4Fragment = new Menu4Fragment();
        menu5Fragment = new Menu5Fragment();
        // 첫 화면 지정

        getSupportFragmentManager().beginTransaction().replace(R.id.main_layout,menu1Fragment).commitAllowingStateLoss();

        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener(){
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem){
                switch (menuItem.getItemId()){
                    case R.id.tab1:{
                        getSupportFragmentManager().beginTransaction().replace(R.id.main_layout,menu1Fragment).commitAllowingStateLoss();
                        return true;
                    }
                    case R.id.tab2:{
                        getSupportFragmentManager().beginTransaction().replace(R.id.main_layout,menu2Fragment).commitAllowingStateLoss();
                        return true;
                    }
                    case R.id.tab3:{
                        getSupportFragmentManager().beginTransaction().replace(R.id.main_layout,menu3Fragment).commitAllowingStateLoss();
                        return true;
                    }
                    case R.id.tab4:{
                        getSupportFragmentManager().beginTransaction().replace(R.id.main_layout,menu4Fragment).commitAllowingStateLoss();
                        return true;
                    }
                    case R.id.tab5:{
                        getSupportFragmentManager().beginTransaction().replace(R.id.main_layout, menu5Fragment).commitAllowingStateLoss();
                        return true;
                    }
                    default: return false;
                }
            }
        });
    }

    //GPS
    private GpsTracker gpsTracker;
    private static final int GPS_ENABLE_REQUEST_CODE = 2001;
    private static final int PERMISSIONS_REQUEST_CODE = 100;
    String[] REQUIRED_PERMISSIONS  = {Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION};

    /*
     * ActivityCompat.requestPermissions를 사용한 퍼미션 요청의 결과를 리턴받는 메소드입니다.
     */
    @Override
    public void onRequestPermissionsResult(int permsRequestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grandResults)
    {

        if ( permsRequestCode == PERMISSIONS_REQUEST_CODE && grandResults.length == REQUIRED_PERMISSIONS.length) {
            // 요청 코드가 PERMISSIONS_REQUEST_CODE 이고, 요청한 퍼미션 개수만큼 수신되었다면
            boolean check_result = true;
            // 모든 퍼미션을 허용했는지 체크합니다.

            for (int result : grandResults)
            {
                if (result != PackageManager.PERMISSION_GRANTED) {
                    check_result = false;
                    break;
                }
            }

            if ( check_result )
            {
                //위치 값을 가져올 수 있음
                ;
            }
            else {
                // 거부한 퍼미션이 있다면 앱을 사용할 수 없는 이유를 설명해주고 앱을 종료합니다.2 가지 경우가 있습니다.
                if (ActivityCompat.shouldShowRequestPermissionRationale(this, REQUIRED_PERMISSIONS[0])
                        || ActivityCompat.shouldShowRequestPermissionRationale(this, REQUIRED_PERMISSIONS[1]))
                {
                    Toast.makeText(MainActivity.this, "퍼미션이 거부되었습니다. 앱을 다시 실행하여 퍼미션을 허용해주세요.", Toast.LENGTH_LONG).show();
                    finish();
                }else {
                    Toast.makeText(MainActivity.this, "퍼미션이 거부되었습니다. 설정(앱 정보)에서 퍼미션을 허용해야 합니다. ", Toast.LENGTH_LONG).show();
                }
            }
        }
    }
    //런타임 퍼미션 처리
    void checkRunTimePermission(){
        // 1. 위치 퍼미션을 가지고 있는지 체크합니다.
        int hasFineLocationPermission = ContextCompat.checkSelfPermission(MainActivity.this,
                Manifest.permission.ACCESS_FINE_LOCATION);
        int hasCoarseLocationPermission = ContextCompat.checkSelfPermission(MainActivity.this,
                Manifest.permission.ACCESS_COARSE_LOCATION);

        if (hasFineLocationPermission == PackageManager.PERMISSION_GRANTED &&
                hasCoarseLocationPermission == PackageManager.PERMISSION_GRANTED)
        {
            // 2. 이미 퍼미션을 가지고 있다면
            // ( 안드로이드 6.0 이하 버전은 런타임 퍼미션이 필요없기 때문에 이미 허용된 걸로 인식합니다.)
            // 3.  위치 값을 가져올 수 있음
        } else {  //2. 퍼미션 요청을 허용한 적이 없다면 퍼미션 요청이 필요합니다. 2가지 경우(3-1, 4-1)가 있습니다.
            // 3-1. 사용자가 퍼미션 거부를 한 적이 있는 경우에는
            if (ActivityCompat.shouldShowRequestPermissionRationale(MainActivity.this, REQUIRED_PERMISSIONS[0]))
            {
                // 3-2. 요청을 진행하기 전에 사용자가에게 퍼미션이 필요한 이유를 설명해줄 필요가 있습니다.
                Toast.makeText(MainActivity.this, "이 앱을 실행하려면 위치 접근 권한이 필요합니다.", Toast.LENGTH_LONG).show();
                // 3-3. 사용자게에 퍼미션 요청을 합니다. 요청 결과는 onRequestPermissionResult에서 수신됩니다.
                ActivityCompat.requestPermissions(MainActivity.this, REQUIRED_PERMISSIONS,
                        PERMISSIONS_REQUEST_CODE);
            } else {
                // 4-1. 사용자가 퍼미션 거부를 한 적이 없는 경우에는 퍼미션 요청을 바로 합니다.
                // 요청 결과는 onRequestPermissionResult에서 수신됩니다.
                ActivityCompat.requestPermissions(MainActivity.this, REQUIRED_PERMISSIONS,
                        PERMISSIONS_REQUEST_CODE);
            }
        }
    }


    public String getCurrentAddress( double latitude, double longitude) {
        //지오코더... GPS를 주소로 변환(좀더 조사해봄)
        Geocoder geocoder = new Geocoder(this, Locale.getDefault());
        List<Address> addresses;

        try
        {
            addresses = geocoder.getFromLocation(
                    latitude,
                    longitude,
                    7);
        } catch (IOException ioException) {
            //네트워크 문제
            Toast.makeText(this, "지오코더 서비스 사용불가", Toast.LENGTH_LONG).show();
            return "지오코더 서비스 사용불가";
        } catch (IllegalArgumentException illegalArgumentException) {
            Toast.makeText(this, "잘못된 GPS 좌표", Toast.LENGTH_LONG).show();
            return "잘못된 GPS 좌표";
        }

        if (addresses == null || addresses.size() == 0)
        {
            Toast.makeText(this, "주소 미발견", Toast.LENGTH_LONG).show();
            return "주소 미발견";
        }

        Address address = addresses.get(0);
        return address.getAddressLine(0).toString()+"\n";
    }


    //여기부터는 GPS 활성화를 위한 메소드들
    //alert창을 사용하여 위치서비스 활성화
    private void showDialogForLocationServiceSetting()
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        builder.setTitle("위치 서비스 비활성화");
        builder.setMessage("앱을 사용하기 위해서는 위치 서비스가 필요합니다.\n"
                + "위치 설정을 수정하실래요?");
        builder.setCancelable(true);
        //위치설정 변경시 안드로이드의 location setting설정창로 이동(intent)
        builder.setPositiveButton("설정", new DialogInterface.OnClickListener()
        {
            @Override
            public void onClick(DialogInterface dialog, int id)
            {
                Intent callGPSSettingIntent
                        = new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                startActivityForResult(callGPSSettingIntent, GPS_ENABLE_REQUEST_CODE);
            }
        });
        builder.setNegativeButton("취소", new DialogInterface.OnClickListener()
        {
            @Override
            public void onClick(DialogInterface dialog, int id) {
                dialog.cancel();
            }
        });
        builder.create().show();
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode)
        {
            case GPS_ENABLE_REQUEST_CODE:
                //사용자가 GPS 활성 시켰는지 검사
                if (checkLocationServicesStatus())
                {
                    if (checkLocationServicesStatus())
                    {
                        Log.d("@@@", "onActivityResult : GPS 활성화 되있음");
                        checkRunTimePermission();
                        return;
                    }
                }
                break;
        }
    }

    public boolean checkLocationServicesStatus() {
        LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);

        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
                || locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
    }

    private class Thread_insert extends Thread {
        private int thread_number;
        private Couple_Location couple_location;

        public Thread_insert(int number, Couple_Location location) {
            this.thread_number = number;
            this.couple_location = location;
        }
        public void run() {
            MyDatabase db = MyDatabase.getAppDatabase(MainActivity.this);
            db.todoDao().insert(couple_location);
        }
    }

    public void get_GPS()
    {
        gpsTracker = new GpsTracker(MainActivity.this);
        double latitude = gpsTracker.getLatitude();
        double longitude = gpsTracker.getLongitude();
        //String address = getCurrentAddress(latitude, longitude);

        Couple_Location new_location= new Couple_Location(0, latitude, longitude);
        Thread_insert thread_insert = new Thread_insert(0, new_location);
        thread_insert.start();
        Toast.makeText(this, Double.toString(latitude) + ", " + Double.toString(longitude), Toast.LENGTH_SHORT).show();
    }
}