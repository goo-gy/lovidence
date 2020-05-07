package com.example.lovidence;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class Matching extends AppCompatActivity {

    TextView matching;
    Button  matchingButton;
    Button  outButton;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_matching);
        matching = findViewById(R.id.matching);
        matchingButton = findViewById(R.id.domatching);
        outButton = findViewById(R.id.exit);
        matching.setText("hello?");

        outButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }
}
