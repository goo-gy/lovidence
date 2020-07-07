package com.example.lovidence.fragments.communityfrags;

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;

import com.example.lovidence.LandMark.PermissionUtils;
import com.example.lovidence.PostAsync.PostAsync;
import com.example.lovidence.R;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.net.URLEncoder;
import java.util.Calendar;

public class community_edit extends Fragment {
    public static final String FILE_NAME = "temp.jpg";
    private static final int GALLERY_PERMISSIONS_REQUEST = 0;
    private static final int GALLERY_IMAGE_REQUEST = 1;
    public static final int CAMERA_PERMISSIONS_REQUEST = 2;
    public static final int CAMERA_IMAGE_REQUEST = 3;
    private static final int MAX_DIMENSION = 1200;
    private static ImageView edit_img;
    // private static ImageView mMainImage;
    private static String sendImg;
    private Button closeBtn;
    private Button btn;
    private SharedPreferences sharedPref;
    private EditText str;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.community_edit, container, false);
        sharedPref = getActivity().getSharedPreferences("USERINFO", Context.MODE_PRIVATE);
        btn = (Button) view.findViewById(R.id.uploadBtn);
        closeBtn = (Button) view.findViewById(R.id.close_btn);
        str = (EditText) view.findViewById(R.id.community_text);
        edit_img = (ImageView) view.findViewById(R.id.edit_img);
        Init();

        //uploading
        closeBtn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                getActivity().onBackPressed();
            }
        });
        btn.setOnClickListener(new View.OnClickListener(){
            PostAsync sending;
            //Bitmap resized = compressBitmap(sendImg);
            //Bitmap resized = Bitmap.createScaledBitmap(sendImg,(int)(sendImg.getWidth()*0.8), (int)(sendImg.getHeight()*0.8), true);
            public void onClick(View v) {
                sending = new PostAsync();
                String data="";
                String sendMessage="";
                String couple_id = sharedPref.getString("COUPLEID","");
                String time = Long.toString(Calendar.getInstance().getTime().getTime());

                try {
                    data = URLEncoder.encode("u_cp", "UTF-8") + "=" + URLEncoder.encode(couple_id, "UTF-8");
                    data += "&" + URLEncoder.encode("u_time", "UTF-8") + "=" + URLEncoder.encode(time, "UTF-8");
                    data += "&" + URLEncoder.encode("u_text", "UTF-8") + "=" + URLEncoder.encode(String.valueOf(str.getText()), "UTF-8");
                    data += "&" + URLEncoder.encode("u_img", "UTF-8") + "=" + URLEncoder.encode(sendImg, "UTF-8");
                    //data += "&" + URLEncoder.encode("u_img2", "UTF-8") + "=" + URLEncoder.encode(getBase64String(sendImg).substring(bitmapLength/2), "UTF-8");
                    Log.e("send Community",data);
                    sendMessage = sending.execute("contentupload.php",data).get();
                    Log.e("result community",sendMessage);
                }catch(Exception e){e.printStackTrace();}
                if(sendMessage.equals("upload success")){
                    str.setText("");
                    getActivity().onBackPressed();

                }
            }

        });
        return view;
    }

    private void Init() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder
                .setMessage("Choose a picture")
                .setPositiveButton("Gallery", (dialog, which) -> startGalleryChooser())
                .setNegativeButton("Camera", (dialog, which) -> startCamera());
        Dialog dialog = builder.create();
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();
        //builder.create().show();
    }
    public void startGalleryChooser() {             //floating action button 에서 갤러리 서낵시
        if (PermissionUtils.requestPermission(getActivity(), GALLERY_PERMISSIONS_REQUEST, Manifest.permission.READ_EXTERNAL_STORAGE)) {  //허가 없을시 요청
            Intent intent = new Intent();
            intent.setType("image/*");
            intent.setAction(Intent.ACTION_GET_CONTENT);
            startActivityForResult(Intent.createChooser(intent, "Select a photo"),
                    GALLERY_IMAGE_REQUEST);
        }
    }

    public File getCameraFile() {
        File dir = getActivity().getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        return new File(dir, FILE_NAME);
    }

    public void startCamera() {

        if (PermissionUtils.requestPermission(
                getActivity(),
                CAMERA_PERMISSIONS_REQUEST,
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.CAMERA)) {
            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            Uri photoUri = FileProvider.getUriForFile(getActivity(), getActivity().getApplicationContext().getPackageName()
                    + ".provider", getCameraFile());
            intent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri);
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            startActivityForResult(intent, CAMERA_IMAGE_REQUEST);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        Bitmap resultImg= null;
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == GALLERY_IMAGE_REQUEST && resultCode == Activity.RESULT_OK && data != null) {      //현재 리퀘스트가있고 결과 ok일시 업로드
            resultImg = uploadImage(data.getData());
        } else if (requestCode == CAMERA_IMAGE_REQUEST && resultCode == Activity.RESULT_OK) {                //Intent가 null인경우
            Uri photoUri = FileProvider.getUriForFile(getActivity(), getActivity().getApplicationContext().getPackageName() + ".provider", getCameraFile());
            resultImg = uploadImage(photoUri);
        }
        edit_img.setImageBitmap(resultImg);
    }

    public Bitmap uploadImage(Uri uri) {
        Bitmap bitmapImg;
        if (uri != null) {
            try {
                // scale the image to save on bandwidth
                bitmapImg =
                        scaleBitmapDown(
                                MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), uri),
                                MAX_DIMENSION);
                sendImg = getBase64String(bitmapImg);
                //PostAsync uploadImage = new PostAsync();
                //String data= = URLEncoder.encode("u_cp", "UTF-8") + "=" + URLEncoder.encode(couple_id, "UTF-8");
                //mMainImage.setImageBitmap(sendImg);
                return bitmapImg;
            } catch (IOException e) {
                Log.d("community_edit", "Image picking failed because " + e.getMessage());
                Toast.makeText(getActivity(), "Something is wrong with that image. Pick a different one please.", Toast.LENGTH_LONG).show();
            }
        } else {
            Log.d("community_edit", "Image picker gave us a null image.");
            Toast.makeText(getActivity(), "Something is wrong with that image. Pick a different one please.", Toast.LENGTH_LONG).show();
        }
        return null;
    }

    @Override
    public void onRequestPermissionsResult(
            int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case CAMERA_PERMISSIONS_REQUEST:
                if (PermissionUtils.permissionGranted(requestCode, CAMERA_PERMISSIONS_REQUEST, grantResults)) {
                    startCamera();
                }
                break;
            case GALLERY_PERMISSIONS_REQUEST:
                if (PermissionUtils.permissionGranted(requestCode, GALLERY_PERMISSIONS_REQUEST, grantResults)) {
                    Log.e("gallery","1");
                    startGalleryChooser();
                }
                break;
        }
    }
    private Bitmap scaleBitmapDown(Bitmap bitmap, int maxDimension) {

        int originalWidth = bitmap.getWidth();
        int originalHeight = bitmap.getHeight();
        int resizedWidth = maxDimension;
        int resizedHeight = maxDimension;

        if (originalHeight > originalWidth) {
            resizedHeight = maxDimension;
            resizedWidth = (int) (resizedHeight * (float) originalWidth / (float) originalHeight);
        } else if (originalWidth > originalHeight) {
            resizedWidth = maxDimension;
            resizedHeight = (int) (resizedWidth * (float) originalHeight / (float) originalWidth);
        } else if (originalHeight == originalWidth) {
            resizedHeight = maxDimension;
            resizedWidth = maxDimension;
        }
        return Bitmap.createScaledBitmap(bitmap, resizedWidth, resizedHeight, false);
    }
    public String getBase64String(Bitmap bitmap)
    {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 60, byteArrayOutputStream);
        byte[] imageBytes = byteArrayOutputStream.toByteArray();
        return Base64.encodeToString(imageBytes, Base64.NO_WRAP);
    }

}