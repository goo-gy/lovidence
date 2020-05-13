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

import com.example.lovidence.PostAsync.PostAsync;
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
    EditText name;
    Button btn;
    RadioButton rg_btn1, rg_btn2;    /*appended it 0425*/

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
                String _id = id.getText().toString();
                String _pw = pw.getText().toString();
                String _chk = pw_chk.getText().toString();
                String _name = name.getText().toString();
                //appended 0425
                String _sex = "";
                if (rg_btn1.isChecked())
                    _sex = "male";
                else
                    _sex = "female";
                //appended 0425
                if (_pw.equals(_chk)) {
                    builderSetting(builder);
                    builder.setTitle("알림");
                    builder.setMessage("성공적으로 등록되었습니다.");
                    builder.setCancelable(true);
                    Async_Prepare(_id, _pw, _sex, _name);
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

    public void Async_Prepare(String id, String pw, String sex,String name) {  /*appended 0425 add sex*/
        String data="";
        PostAsync registAsync = new PostAsync();
        try {
            data = URLEncoder.encode("u_id", "UTF-8") + "=" + URLEncoder.encode(id, "UTF-8");
            data += "&" + URLEncoder.encode("u_pw", "UTF-8") + "=" + URLEncoder.encode(pw, "UTF-8");
            data += "&" + URLEncoder.encode("u_sex", "UTF-8") + "=" + URLEncoder.encode(sex, "UTF-8");
            data += "&" + URLEncoder.encode("u_name", "UTF-8") + "=" + URLEncoder.encode(name, "UTF-8");

            registAsync.execute("userRegist.php",data).get();
        }catch (Exception e){e.printStackTrace();}
    }

}