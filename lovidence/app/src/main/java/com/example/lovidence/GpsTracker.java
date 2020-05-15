package com.example.lovidence;

import android.Manifest;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;

import androidx.core.content.ContextCompat;

public class GpsTracker extends Service implements LocationListener {
    private final Context mContext;
    Location location;
    double latitude;
    double longitude;

    private static final long MIN_DISTANCE_CHANGE_FOR_UPDATES = 10;
    private static final long MIN_TIME_BW_UPDATES = 1000 * 60 * 1;
    protected LocationManager locationManager;

    public GpsTracker(Context context) {
        this.mContext = context;
        getLocation();
    }

    //getLocation
    public Location getLocation() {
        try {
            /*시스템에서 Location정보를가져와서 location manager class로 변환*/
            locationManager = (LocationManager) mContext.getSystemService(LOCATION_SERVICE);

            //GPS와NETWORK가 enable된건지 확인 , 현재 시스템에서 켜졌는지 확인
            boolean isGPSEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
            boolean isNetworkEnabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);


            if (!isGPSEnabled && !isNetworkEnabled) {
                //Both GPS and NETWORK is disabled
            }
            else {
                /*현재 시스템에서 위치정보에대한 권한을 받아옴
                 * fineLocation : 실질적 GPS+ 네트워크 위치
                 * coarseLocation : 네트워크 제공자의 위치
                 * 즉, 사용자가 와이파이를 켜놓고 GPS와 동시에 받고있다면 와이파이 위치와 GPS위치 두개를 받아옴
                 */
                int hasFineLocationPermission = ContextCompat.checkSelfPermission(mContext,
                        Manifest.permission.ACCESS_FINE_LOCATION);
                int hasCoarseLocationPermission = ContextCompat.checkSelfPermission(mContext,
                        Manifest.permission.ACCESS_COARSE_LOCATION);


                if (hasFineLocationPermission == PackageManager.PERMISSION_GRANTED &&
                        hasCoarseLocationPermission == PackageManager.PERMISSION_GRANTED)
                {
                    //두 권한 모두 허가 된 상태일떄 처리
                    ;
                } else
                    return null;
                /*####*/										}

// GPS와 NETWORK가 켜져있지않다면 킬수있도록 만들어줘야함

            if (isNetworkEnabled) {

                //위치업데이트요청(manager, 받을 최소 시간간격(ms), 위치가 업데이트될 최소 거리(m), listener(현재 class가 상속받고있으므로 this)
                locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, MIN_TIME_BW_UPDATES, MIN_DISTANCE_CHANGE_FOR_UPDATES, this);

                if (locationManager != null)
                {
                    location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);//마지막으로 저장한 위치 정보 가져옴
                    if (location != null)
                    {
                        latitude = location.getLatitude();   //해당 위도와 경도값을 가져옴
                        longitude = location.getLongitude();
                    }
                }
            }


            if (isGPSEnabled)
            {
                if (location == null)
                {
                    locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, MIN_TIME_BW_UPDATES, MIN_DISTANCE_CHANGE_FOR_UPDATES, this);
                    if (locationManager != null)
                    {
                        location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                        if (location != null)
                        {
                            latitude = location.getLatitude();
                            longitude = location.getLongitude();
                        }
                    }
                }
                /*####*/        }
        }catch (Exception e){
            Log.d("@@@", ""+e.toString()); }

        return location;
    }

    //getLatitude
    public double getLatitude()
    {
        if(location != null)
        {
            latitude = location.getLatitude();
        }

        return latitude;
    }
    //getLongitude
    public double getLongitude()
    {
        if(location != null)
        {
            longitude = location.getLongitude();
        }

        return longitude;
    }

    @Override
    public void onLocationChanged(Location location)
    {
    }

    @Override
    public void onProviderDisabled(String provider)
    {
    }

    @Override
    public void onProviderEnabled(String provider)
    {
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras)
    {
    }

    @Override
    public IBinder onBind(Intent arg0)
    {
        return null;
    }


    public void stopUsingGPS()
    {
        if(locationManager != null)
        {
            locationManager.removeUpdates(GpsTracker.this);
        }
    }


}