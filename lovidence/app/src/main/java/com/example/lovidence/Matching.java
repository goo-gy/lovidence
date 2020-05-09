package com.example.lovidence;

import androidx.appcompat.app.AppCompatActivity;

import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

public class Matching extends AppCompatActivity {

    TextView matching;
    Button  matchingButton;
    Button  outButton;
    EditText matchingId;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_matching);
        //matching = findViewById(R.id.matching);
        matchingButton = findViewById(R.id.domatching);
        outButton = findViewById(R.id.exit);
        matchingId = findViewById(R.id.loveperson);
        //matching.setText("hello?");

        outButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) { finish(); }
        });
        matchingButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                Log.e("communication",commnuicate(matchingId.getText().toString()));
                //매칭한후의 처리.
                //애니메이션을 넣던지 약간 분위기있게?
            }
        });
    }
    public String commnuicate(String id) {  /*appended 0425 add sex*/
        matchingAsync loginAsync = new matchingAsync();
        //성공적으로 매칭시 그아이디값 그외에는 오류
        String sendMessage="";
        try {
            Log.e("communicate",id);
            sendMessage = loginAsync.execute(id).get();
        }catch(Exception e){e.printStackTrace();}
        return sendMessage;
    }
    //서버에서 매칭할 아이디를 찾아서 매칭시켜주는 통신class
    //결과값은 성공적으로 받아왔는지 확인하면됨

    class matchingAsync extends AsyncTask<String, Void, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
        }

        @Override
        protected void onProgressUpdate(Void... values) {
            super.onProgressUpdate(values);
        }

        @Override
        protected String doInBackground(String... params) {
            HttpURLConnection httpURLConnection = null;
            try {
                String matchingUserId = params[0];
                //매칭할 유저아이디와 서버url
                String data = URLEncoder.encode("u_id", "UTF-8") + "=" + URLEncoder.encode(matchingUserId, "UTF-8");
                String link = "https://www.naver.com";
                Log.e("matchingId", matchingUserId);
                Log.e("data", data);
                Log.e("link", link);
                URL url = new URL(link);
                httpURLConnection = (HttpURLConnection) url.openConnection();
                httpURLConnection.setRequestMethod("POST");
                httpURLConnection.setDoInput(true);
                httpURLConnection.setDoOutput(true);
                OutputStreamWriter wr = new OutputStreamWriter(httpURLConnection.getOutputStream());
                wr.write(data); //data 전송
                wr.flush();
                //결과 받음
                BufferedReader reader = new BufferedReader(new InputStreamReader(httpURLConnection.getErrorStream(), "UTF-8"));
                StringBuilder sb = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    sb.append(line);
                }
                httpURLConnection.disconnect();
                return sb.toString();
            } catch (Exception e) {
                Log.d("ya", "ho", e);
                httpURLConnection.disconnect();
                return new String("Exception Occure" + e.getMessage());
            }
        }
    }
}

