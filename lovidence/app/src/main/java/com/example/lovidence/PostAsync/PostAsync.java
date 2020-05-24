package com.example.lovidence.PostAsync;

import android.app.ProgressDialog;
import android.graphics.Canvas;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.ProgressBar;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

//transfer data params[0] = URL params[1] = data..
//data form is
//      data = URLEncoder.encode("u_id1", "UTF-8") + "=" + URLEncoder.encode(usr1, "UTF-8");
//      data += "&" + URLEncoder.encode("u_id2", "UTF-8") + "=" + URLEncoder.encode(usr2, "UTF-8");
public class PostAsync extends AsyncTask<String, Void, String> {
    ProgressBar loading;
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
        String data="";
        String link="";
        try {
            data = params[1];
            link = "https://test-yetvm.run.goorm.io/test/"+params[0];
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
            Log.d("PostAsync : ", "Exception Occure", e);
            httpURLConnection.disconnect();
            return new String("Error");
        }
    }
}