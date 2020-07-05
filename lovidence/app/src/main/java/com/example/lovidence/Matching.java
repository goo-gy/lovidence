package com.example.lovidence;

import android.Manifest;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.example.lovidence.PostAsync.PostAsync;
import com.example.lovidence.splash.SplashActivity;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.Calendar;

public class Matching extends AppCompatActivity {
    SharedPreferences sharedPref;
    Button  matchingButton;
    Button  outButton;
    EditText matchingId;
    public Calendar the_date;
    private Button createQRBtn;
    private Button scanQRBtn;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityCompat.requestPermissions(Matching.this,new String[]{Manifest.permission.CAMERA}, PackageManager.PERMISSION_GRANTED);
        setContentView(R.layout.activity_matching);
        matchingButton = findViewById(R.id.domatching);
        outButton = findViewById(R.id.exit);
        matchingId = findViewById(R.id.loveperson);
        createQRBtn = findViewById(R.id.gQR);
        scanQRBtn = findViewById(R.id.sQR);

        createQRBtn.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v){
                Intent intent = new Intent(Matching.this, GenerateQrCode.class);
                startActivity(intent);
            }
        });
        scanQRBtn.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v){
                ScanButton(v);
            }
        });
        outButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Matching.this, SplashActivity.class);
                startActivity(intent);
                finish();
            }
        });
        matchingButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                if(commnuicate(matchingId.getText().toString()).equals("matched")){ //아이디가 존재하는경우
                    Toast.makeText(Matching.this, "언제부터 1일?", Toast.LENGTH_LONG).show();
                    the_date = Calendar.getInstance();
                    DatePickerDialog dialog = new DatePickerDialog(Matching.this, listener, //달력가져와서 입력함(listner로)
                            the_date.get(Calendar.YEAR), the_date.get(Calendar.MONTH), the_date.get(Calendar.DAY_OF_MONTH));
                    dialog.show();

                }
                else{
                    Toast.makeText(Matching.this, "Fail matching... not exists Id", Toast.LENGTH_LONG).show();
                }

            }
        });
    }
    public String commnuicate(String id) {  /*appended 0425 add sex*/
        PostAsync matchingAsync = new PostAsync();
        //성공적으로 매칭시 그아이디값 그외에는 오류
        String data="";
        String sendMessage="";
        try {
            data = URLEncoder.encode("u_id1", "UTF-8") + "=" + URLEncoder.encode(id, "UTF-8");
            Log.e("communicate",id);
            sendMessage = matchingAsync.execute("matchcheck.php",data).get();
        }catch(Exception e){e.printStackTrace();}
        return sendMessage;
    }
    public void ScanButton(View view){
        IntentIntegrator integrator = new IntentIntegrator(Matching.this);
        integrator.initiateScan();
    }
    //scan값 받아올때 onActivityResult사용
    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        IntentResult intentResult = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);

        if (intentResult != null) {
            if (intentResult.getContents() == null) {
                Toast.makeText(Matching.this, "Cancelled", Toast.LENGTH_LONG).show();
            } else {
                matchingId.setText(intentResult.getContents());
                Toast.makeText(Matching.this, intentResult.getContents(), Toast.LENGTH_LONG).show();
                matchingButton.performClick();
            }
        }

        super.onActivityResult(requestCode, resultCode, data);
    }
    private DatePickerDialog.OnDateSetListener listener = new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker view, int year, int month, int day) {
            the_date = Calendar.getInstance();    // 현재 입력된 시간을 받음.
            the_date.set(Calendar.YEAR, year);
            the_date.set(Calendar.MONTH, month);
            the_date.set(Calendar.DAY_OF_MONTH, day);


            SimpleDateFormat date_format = new SimpleDateFormat("yyyy-MM-dd");
            String str_the_date = date_format.format(the_date.getTime());
            //--------------------------------------------------------
            sharedPref = Matching.this.getSharedPreferences("USERINFO", Context.MODE_PRIVATE);  //입력한 값을저장
            SharedPreferences.Editor editor = sharedPref.edit();
            editor.putString("date",str_the_date );
            editor.commit();
            //Toast.makeText(Matching.this, , Toast.LENGTH_SHORT).show();
            //preference에 저장했으니 서버로 보내는코드
            PostAsync matchingAsync = new PostAsync();
            //성공적으로 매칭시 그아이디값 그외에는 오류
            sharedPref = Matching.this.getSharedPreferences("USERINFO", Context.MODE_PRIVATE);
            String myId = sharedPref.getString("USERID","");
            if(myId.equals("")){
                Log.e("ERROR!!","INVALID ID");
            }
            String partnerId = matchingId.getText().toString();
            String data="";
            String sendMessage="";
            try {
                data = URLEncoder.encode("u_id1", "UTF-8") + "=" + URLEncoder.encode(myId, "UTF-8");
                data += "&" + URLEncoder.encode("u_id2", "UTF-8") + "=" + URLEncoder.encode(partnerId, "UTF-8");
                data += "&" + URLEncoder.encode("u_couple", "UTF-8") + "=" + URLEncoder.encode(myId+partnerId, "UTF-8");
                data += "&" + URLEncoder.encode("u_year", "UTF-8") + "=" + URLEncoder.encode(String.valueOf(the_date.get(Calendar.YEAR)), "UTF-8");
                data += "&" + URLEncoder.encode("u_month", "UTF-8") + "=" + URLEncoder.encode(String.valueOf(the_date.get(Calendar.MONTH)+1), "UTF-8");
                data += "&" + URLEncoder.encode("u_day", "UTF-8") + "=" + URLEncoder.encode(String.valueOf(the_date.get(Calendar.DATE)), "UTF-8");
                sendMessage = matchingAsync.execute("matching.php",data).get();
                Log.e("mathing..",sendMessage);
            }catch(Exception e){e.printStackTrace();}
            if(sendMessage.equals("matching info query successbuffer update")){
                Intent intent = new Intent(Matching.this, SplashActivity.class);
                editor.putString("COUPLEID",myId+partnerId);
                finish();
                startActivity(intent);
                Toast.makeText(Matching.this, "매칭 성공!!!", Toast.LENGTH_SHORT).show();
            }
            else{//매칭실패시...
                Intent intent = new Intent(Matching.this, MainActivity.class);
                finish();
                startActivity(intent);
                Toast.makeText(Matching.this, sendMessage, Toast.LENGTH_SHORT).show();

            }
        }
    };
    @Override
    public void onBackPressed() {
        Intent intent = new Intent(Matching.this, SplashActivity.class);
        finish();
        startActivity(intent);

    }

}

