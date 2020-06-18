package com.example.lovidence.fragments.communityfrags;

import android.Manifest;
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
import com.example.lovidence.R;

import java.io.ByteArrayOutputStream;

public class community_content extends Fragment {
    private View view;
    private Bitmap bmp;
    private String str;
    private ImageView imageview;
    private ImageView search;
    private TextView textview;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        Log.e("???","error");
        super.onCreate(savedInstanceState);


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_community_content, container, false);
        search = view.findViewById(R.id.search_landmark);
        Bundle bundle = getArguments();
        byte[] byteArray = bundle.getByteArray("image");
        bmp = BitmapFactory.decodeByteArray(byteArray, 0, byteArray.length);
        str = bundle.getString("text");


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




}
