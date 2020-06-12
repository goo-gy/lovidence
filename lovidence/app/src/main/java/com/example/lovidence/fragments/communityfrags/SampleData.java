package com.example.lovidence.fragments.communityfrags;

import android.graphics.Bitmap;

public class SampleData {
    private Bitmap img;
    private String content;
    private long time;

    public SampleData(Bitmap _img, String _content, long _time){
        this.img = _img;
        this.content = _content;
        this.time = _time;
    }

    public Bitmap getImg()
    {
        return this.img;
    }

    public String getContent()
    {
        return this.content;
    }

    public long getTime()
    {
        return this.time;
    }
}
