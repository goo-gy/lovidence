package com.example.lovidence.fragments.communityfrags;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.example.lovidence.R;

import java.util.ArrayList;

public class CommunityAdapter extends BaseAdapter {
    Context mContext = null;
    LayoutInflater mLayoutInflater = null;
    ArrayList<SampleData> sample;

    public CommunityAdapter(Context context, ArrayList<SampleData> data) {
        mContext = context;
        sample = data;
        mLayoutInflater = LayoutInflater.from(mContext);
    }

    public void add(SampleData data){
        sample.add(data);
    }
    public void clear(){
        sample.clear();
    }

    @Override
    public int getCount() {
        return sample.size();
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public SampleData getItem(int position) {
        return sample.get(position);
    }

    @Override
    public View getView(int position, View converView, ViewGroup parent) {
        View view = mLayoutInflater.inflate(R.layout.community_base, null);
        ImageView imageView = (ImageView)view.findViewById(R.id.img);
        Bitmap bitmap = sample.get(position).getImg();
        imageView.setImageBitmap(bitmap);

        return view;
    }
}
