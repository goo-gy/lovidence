package com.example.lovidence.data;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import com.example.lovidence.data.model.LoggedInUser;
import com.example.lovidence.ui.login.LoginActivity;
import com.example.lovidence.ui.login.registerActivity;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

/**
 * Class that handles authentication w/ login credentials and retrieves user information.
 * 로그인 자격 증명으로 인증을 처리하고 사용자 정보를 검색하는 클래스입니다.
 */
public class LoginDataSource {
    static private boolean isuserExist=false;
    public Result<LoggedInUser> login(String username, String password) {
        loginCheck(username, password);
        try {
            // TODO: handle loggedInUser authentication(사용자 인증)
            if(!isuserExist){return new Result.Fail();}
            //아이디 목록에서 확인후 유효한 값이면 fakeUser에 넘겨줌
            //현재 있는 Error는 Exception이 발생했을떄 동작하는코드
            //즉, 문자열 비교할때는 Exception을 사용하지않도록
            LoggedInUser fakeUser =
                    new LoggedInUser(
                            java.util.UUID.randomUUID().toString(),
                            "Jane Doe");
            return new Result.Success<>(fakeUser);
        } catch (Exception e) {
            return new Result.Error(new IOException("Error logging in", e));
        }
    }

    public void logout() {
        // TODO: revoke authentication
    }
    public void loginCheck(String id, String pw) {  /*appended 0425 add sex*/
        loginAsync loginAsync = new loginAsync();
        String sendMessage="";
        try {
            sendMessage = loginAsync.execute(id, pw).get();
        }catch(Exception e){e.printStackTrace();}
        if(sendMessage.equals("success")){

            isuserExist = true;
        }
    }
    class loginAsync extends AsyncTask<String, Void, String> {

        int cnt = 0;

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
            Log.d("onProgress update", "" + cnt++);
        }



        @Override
        protected String doInBackground(String... params) {
            HttpURLConnection httpURLConnection = null;
            try {
                String userId = params[0];
                String userPw = params[1];

                String data = URLEncoder.encode("u_id", "UTF-8") + "=" + URLEncoder.encode(userId, "UTF-8");
                data += "&" + URLEncoder.encode("u_pw", "UTF-8") + "=" + URLEncoder.encode(userPw, "UTF-8");
                String link = "https://test-yetvm.run.goorm.io/test/" + "login.php";

                URL url = new URL(link);
                httpURLConnection = (HttpURLConnection) url.openConnection();
                httpURLConnection.setRequestMethod("POST");
                httpURLConnection.setDoInput(true);
                httpURLConnection.setDoOutput(true);
                OutputStreamWriter wr = new OutputStreamWriter(httpURLConnection.getOutputStream());
                wr.write(data);
                wr.flush();
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
