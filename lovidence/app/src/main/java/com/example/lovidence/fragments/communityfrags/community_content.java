package com.example.lovidence.fragments.communityfrags;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import com.example.lovidence.R;

public class community_content extends Fragment {
    private View view;
    private Bitmap bmp;
    private String str;
    private ImageView imageview;
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
        Bundle bundle = getArguments();
        byte[] byteArray = bundle.getByteArray("image");
        bmp = BitmapFactory.decodeByteArray(byteArray, 0, byteArray.length);
        str = bundle.getString("text");


        imageview = (ImageView)view.findViewById(R.id.comm_cont_img);
        textview = (TextView)view.findViewById(R.id.comm_cont_text);

        imageview.setImageBitmap(bmp);
        textview.setText(str);

        return view;
    }

}
