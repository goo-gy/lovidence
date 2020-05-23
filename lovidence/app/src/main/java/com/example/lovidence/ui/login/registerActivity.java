package com.example.lovidence.ui.login;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
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

import com.example.lovidence.Matching;
import com.example.lovidence.PostAsync.PostAsync;
import com.example.lovidence.R;
import com.example.lovidence.splash.SplashActivity;

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
    EditText name;
    Button btn;
    RadioButton rg_btn1, rg_btn2;    /*appended it 0425*/
    SharedPreferences sharedPref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        id = (EditText) findViewById(R.id.et_id);
        pw = (EditText) findViewById(R.id.et_Password);
        pw_chk = (EditText) findViewById(R.id.et_Password_chk);
        name = (EditText)findViewById(R.id.et_Name);
        btn = (Button) findViewById(R.id.bt_Join);
        rg_btn1 = (RadioButton) findViewById(R.id.rg_btn1);
        rg_btn2 = (RadioButton) findViewById(R.id.rg_btn2);   /*appended it 0425*/
        btn.setOnClickListener(new View.OnClickListener() {
            AlertDialog.Builder builder
                    = new AlertDialog.Builder(registerActivity.this);

            @Override
            public void onClick(View view) {
                if(textcheck()) {
                    String _id = id.getText().toString();
                    String _pw = pw.getText().toString();
                    String _name = name.getText().toString();
                    //appended 0425
                    String _sex = "";
                    if (rg_btn1.isChecked())
                        _sex = "male";
                    else
                        _sex = "female";
                    Async_Prepare(_id, _pw, _sex, _name,builder);
                }

            }
        });

    }
    private boolean textcheck(){
        boolean result = true;
        if(id.getText().toString().equals("")){
            id.requestFocus();
            id.setError("아이디를 입력해주세요.");
            result = false;
        }
        else if(pw.getText().toString().equals("")){
            pw.requestFocus();
            pw.setError("비밀번호를 입력해주세요.");
            result = false;
        }
        else if(pw_chk.getText().toString().equals("")){
            pw_chk.requestFocus();
            pw_chk.setError("비밀번호를 한번더 입력하세요.");
            result = false;
        }
        else if(!pw.getText().toString().equals(pw_chk.getText().toString())){
            pw_chk.requestFocus();
            pw_chk.setError("비밀번호가 다릅니다. 다시 입력해주세요");
            result = false;
        }
        else if(name.getText().toString().equals("")){
            name.requestFocus();
            name.setError("이름을 입력해주세요.");
            result = false;
        }

        return result;

    }

    private void builderSetting(AlertDialog.Builder builder) {
        builder.setTitle("가입완료").setMessage("가입이 완료되었습니다.");
        builder.setPositiveButton("확인", new DialogInterface.OnClickListener() {
            AlertDialog.Builder builder2
                    = new AlertDialog.Builder(registerActivity.this);
            @Override
            public void onClick(DialogInterface dialog, int which) {
                builder2.setTitle("상대방 등록");
                builder2.setMessage("상대방 아이디를 등록하시겠습니까?");
                builder2.setPositiveButton("확인", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent = new Intent(registerActivity.this, Matching.class);
                        finish();
                        startActivity(intent);
                    }
                });
                builder2.setNegativeButton("다음에 할께요", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent = new Intent(registerActivity.this, SplashActivity.class);
                        finish();
                        startActivity(intent);
                    }
                });
                AlertDialog alertDialog2 = builder2.create();
                alertDialog2.show();
            }
        });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }
    private void idDuplicated(){
        id.requestFocus();
        id.setError("이미 존재하는 아이디입니다.");

    }
    public void Async_Prepare(String id, String pw, String sex,String name,AlertDialog.Builder _builder) {  /*appended 0425 add sex*/
        String data="";
        AlertDialog.Builder builder = _builder;
        PostAsync registAsync = new PostAsync();
        try {
            data = URLEncoder.encode("u_id", "UTF-8") + "=" + URLEncoder.encode(id, "UTF-8");
            data += "&" + URLEncoder.encode("u_pw", "UTF-8") + "=" + URLEncoder.encode(pw, "UTF-8");
            data += "&" + URLEncoder.encode("u_sex", "UTF-8") + "=" + URLEncoder.encode(sex, "UTF-8");
            data += "&" + URLEncoder.encode("u_name", "UTF-8") + "=" + URLEncoder.encode(name, "UTF-8");

            String result = registAsync.execute("userRegist.php",data).get();
            if(result.equals("Id exists")){
                idDuplicated();
            }
            else if(result.equals("success")){
                sharedPref = registerActivity.this.getSharedPreferences("USERINFO", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPref.edit();
                editor.putString("USERID",id);
                editor.commit();
                builderSetting(builder);
            }
        }catch (Exception e){e.printStackTrace();}
    }

}