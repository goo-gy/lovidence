package com.example.lovidence.ui.login;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.lovidence.R;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;

public class registerActivity extends AppCompatActivity {
    //TextView textView;
    EditText id;
    EditText pw;
    EditText pw_chk;
    Button btn;
    RadioButton rg_btn1, rg_btn2;    /*appended it 0425*/

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        id = (EditText) findViewById(R.id.et_id);
        pw = (EditText) findViewById(R.id.et_Password);
        pw_chk = (EditText) findViewById(R.id.et_Password_chk);
        btn = (Button) findViewById(R.id.bt_Join);
        rg_btn1 = (RadioButton) findViewById(R.id.rg_btn1);
        rg_btn2 = (RadioButton) findViewById(R.id.rg_btn2);   /*appended it 0425*/
        btn.setOnClickListener(new View.OnClickListener() {
            AlertDialog.Builder builder
                    = new AlertDialog.Builder(registerActivity.this);

            @Override
            public void onClick(View view) {
                String _id = id.getText().toString();
                String _pw = pw.getText().toString();
                String _chk = pw_chk.getText().toString();
                //appended 0425
                String _sex = "";
                if (rg_btn1.isChecked())
                    _sex = "male";
                else
                    _sex = "female";
                //appended 0425

                /*  original
                if(_pw.equals(_chk)){
                    builderSetting(builder);
                    builder.setTitle("알림");
                    builder.setMessage("성공적으로 등록되었습니다.");
                    builder.setCancelable(true);
                    Async_Prepare(_id, _pw);
                }*/
                if (_pw.equals(_chk)) {
                    builderSetting(builder);
                    builder.setTitle("알림");
                    builder.setMessage("성공적으로 등록되었습니다.");
                    builder.setCancelable(true);
                    Async_Prepare(_id, _pw, _sex);
                }
            }
        });

    }

    private void builderSetting(AlertDialog.Builder builder) {
        builder.setTitle("가입완료").setMessage("가입이 완료되었습니다.");
        builder.setPositiveButton("확인", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                finish();
            }
        });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    public void Async_Prepare(String id, String pw, String sex) {  /*appended 0425 add sex*/
        Async_test async_test = new Async_test();
        async_test.execute(id, pw, sex);//요렇게 스트링값들을 넘겨줍시다. 저번시간에 포스팅을 보시면 Data Type을 어떻게 할지 결정 할 수 있습니다. 힘내봅시다.
    }

    class Async_test extends AsyncTask<String, Void, String> {

        int cnt = 0;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            Toast.makeText(registerActivity.this, s, Toast.LENGTH_LONG).show();
            //textView.setText("I got Msg from Server! : "+s);// TextView에 보여줍니다.
//            Toast.makeText(getApplicationContext(),"i got a msg from server :"+s,Toast.LENGTH_LONG).show();
        }

        @Override
        protected void onProgressUpdate(Void... values) {
            super.onProgressUpdate(values);
            Log.d("onProgress update", "" + cnt++);
        }

        @Override
        protected String doInBackground(String... params) {
            HttpURLConnection httpURLConnection = null;
            try {
                String tmsg = params[0];
                String tmsg2 = params[1];
                String tmsg3 = params[2];   /*appended 0425*/
                String data = URLEncoder.encode("u_id", "UTF-8") + "=" + URLEncoder.encode(tmsg, "UTF-8");// UTF-8로  설정 실제로 string 상으로 봤을땐, tmsg="String" 요런식으로 설정 된다.

                data += "&" + URLEncoder.encode("u_pw", "UTF-8") + "=" + URLEncoder.encode(tmsg2, "UTF-8");
                data += "&" + URLEncoder.encode("u_sex", "UTF-8") + "=" + URLEncoder.encode(tmsg3, "UTF-8");

                //                String data2 = "tmsg="+testMsg+"&tmsg2="+testMsg2;
                String link = "https://test-yetvm.run.goorm.io/test/" + "test.php";// 요청하는 url 설정 ex)192.168.0.1/httpOnlineTest.php

                URL url = new URL(link);
                httpURLConnection = (HttpURLConnection) url.openConnection();//httpURLConnection은 url.openconnection을 통해서 만 생성 가능 직접생성은 불가능하다.
                httpURLConnection.setRequestMethod("POST");//post방식으로 설정
                httpURLConnection.setDoInput(true);// server와의 통신에서 입력 가능한 상태로 만든다.
                httpURLConnection.setDoOutput(true);//server와의 통신에서 출력 가능한 상태로 ㅏㄴ든다.
//                httpURLConnection.setConnectTimeout(30);// 타임 아웃 설정 default는 무제한 unlimit이다.
                OutputStreamWriter wr = new OutputStreamWriter(httpURLConnection.getOutputStream());//서버로 뿅 쏴줄라구용
                wr.write(data);//아까 String값을 쓱삭쓱삭 넣어서 보내주고!
                wr.flush();//flush!
                BufferedReader reader = new BufferedReader(new InputStreamReader(httpURLConnection.getErrorStream(), "UTF-8"));//자 이제 받아옵시다.
                StringBuilder sb = new StringBuilder();// String 값을 이제 스슥스슥 넣을 껍니다.
                String line;
                while ((line = reader.readLine()) != null) {
                    sb.append(line);//
                }
                httpURLConnection.disconnect();//이거 꼭해주세요. 보통은 별일 없는데, 특정상황에서 문제가 생기는 경우가 있다고 합니다.
                return sb.toString();//자 이렇게 리턴이되면 이제 post로 가겠습니다.
            } catch (Exception e) {
                Log.d("ya", "ho", e);
                httpURLConnection.disconnect();
                return new String("Exception Occure" + e.getMessage());
            }//try catch end
        }//doInbackground end


    }//asynctask  end
}