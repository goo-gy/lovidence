package com.example.lovidence.fragments.communityfrags;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import com.example.lovidence.LandMark.LandmarkActivity;
import com.example.lovidence.PostAsync.PostAsync;
import com.example.lovidence.R;
import com.example.lovidence.SQLite.CommunityDatabase;
import com.example.lovidence.SQLite.Community_Scrap;

import java.io.ByteArrayOutputStream;
import java.net.URLEncoder;

public class community_content extends Fragment {
    private View view;
    static private Bitmap bmp;
    static private String str;
    static private long   time;
    static private int flag;
    private ImageView imageview;
    private ImageView search;
    private ImageView scrap;
    private ImageView shared;
    private TextView textview;
    private static Context context;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        Log.e("???","error");
        super.onCreate(savedInstanceState);


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_community_content, container, false);
        //dundle로 값받은후에 확인하고
        search = view.findViewById(R.id.search_landmark);
        scrap = view.findViewById(R.id.scrap_content);
        shared = view.findViewById(R.id.share_content);
        context = getActivity();
        Bundle bundle = getArguments();
        byte[] byteArray = bundle.getByteArray("image");
        bmp = BitmapFactory.decodeByteArray(byteArray, 0, byteArray.length);
        str = bundle.getString("text");
        time = bundle.getLong("time");
        //"flag" : 0 - private, 1 - public, 2- scrap
        //private : search,share /// public : scrap, search /// scrap : only search
        flag = bundle.getInt("flag",-1);


        imageview = (ImageView)view.findViewById(R.id.comm_cont_img);
        textview = (TextView)view.findViewById(R.id.comm_cont_text);

        imageview.setImageBitmap(bmp);
        textview.setText(str);
        checkPermission();
        search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), LandmarkActivity.class);
                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                bmp.compress(Bitmap.CompressFormat.JPEG, 50, stream);
                byte[] bytes = stream.toByteArray();
                intent.putExtra("fromCommunity",bytes);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                getActivity().finish();
            }
        });
        if(flag == 0){
            shared.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View v) {
                    PostAsync sharing = new PostAsync();
                    String data = "";
                    String coupleId = context.getSharedPreferences("USERINFO",Context.MODE_PRIVATE).getString("COUPLEID","");
                    try {
                        data = URLEncoder.encode("c_id", "UTF-8") + "=" + URLEncoder.encode(coupleId, "UTF-8");
                        data += "&" + URLEncoder.encode("c_time", "UTF-8") + "=" + URLEncoder.encode(Long.toString(time), "UTF-8");
                        /*******************************공유 링크 초기화해야함.*******************************/
                        if(sharing.execute("share.php",data).get().equals("share success")) {
                            Toast.makeText(context, "공유되었습니다.", Toast.LENGTH_SHORT).show();
                        }
                        else{
                            Log.e("sharingError!","ERROR..");
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
        }
        else{
            shared.setVisibility(View.INVISIBLE);
        }

        if(flag == 1) {
            scrap.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Thread_DB scraping = new Thread_DB(bmp, str,time);
                    scraping.start();
                    try {
                        scraping.join();
                        Toast.makeText(context, "저장되었습니다.", Toast.LENGTH_SHORT).show();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            });
        }
        else{
            scrap.setVisibility(View.INVISIBLE);
        }
        return view;
    }

    private void checkPermission() {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) { // 마시멜로우 버전과 같거나 이상이라면
            if(getActivity().checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED
                    || getActivity().checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                if(shouldShowRequestPermissionRationale(Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                    Toast.makeText(getActivity(), "외부 저장소 사용을 위해 읽기/쓰기 필요", Toast.LENGTH_SHORT).show();
                }

                requestPermissions(new String[]
                                {Manifest.permission.WRITE_EXTERNAL_STORAGE,Manifest.permission.READ_EXTERNAL_STORAGE},
                        2);  //마지막 인자는 체크해야될 권한 갯수

            } else {
                //Toast.makeText(this, "권한 승인되었음", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private class Thread_DB extends Thread {
        CommunityDatabase db;
        byte[] content_img;
        String content_str;
        long   content_time;

        public Thread_DB(Bitmap _img, String _str, long _time) {
            db = CommunityDatabase.getAppDatabase(context);
            content_str = _str;
            content_time = _time;
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            _img.compress(Bitmap.CompressFormat.PNG, 100, stream);
            byte[] byteArray = stream.toByteArray();
            _img.recycle();
            content_img = byteArray;
        }
        public void run() {
            db.todoDao().insert(new Community_Scrap(content_time,content_img,content_str));
        }
    }

}
