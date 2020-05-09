package com.example.lovidence;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;

import android.os.Bundle;
import android.widget.ImageView;
import androidx.appcompat.app.AppCompatActivity;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.common.BitMatrix;
import com.journeyapps.barcodescanner.BarcodeEncoder;

public class GenerateQrCode extends AppCompatActivity {
    private ImageView iv;
    private String text;
    private SharedPreferences sharedPref;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_generate_qr_code);

        sharedPref = getSharedPreferences("USERINFO", Context.MODE_PRIVATE); //꾸리함..
        text = sharedPref.getString("USERID","sharedPrefError");

        iv = (ImageView)findViewById(R.id.hell);
        //text = "https://park-duck.tistory.com";

        MultiFormatWriter multiFormatWriter = new MultiFormatWriter();
        try{
            BitMatrix bitMatrix = multiFormatWriter.encode(text, BarcodeFormat.QR_CODE,200,200);
            BarcodeEncoder barcodeEncoder = new BarcodeEncoder();
            Bitmap bitmap = barcodeEncoder.createBitmap(bitMatrix);
            iv.setImageBitmap(bitmap);
        }catch (Exception e){}
    }
}
