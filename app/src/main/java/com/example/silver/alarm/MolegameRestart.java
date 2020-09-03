package com.example.silver.alarm;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;

public class MolegameRestart extends AppCompatActivity {

    ImageButton btnRestart;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_molegame_restart);

        btnRestart = (ImageButton)findViewById(R.id.btnRestart);
        btnRestart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MolegameRestart.this, MolegamePlay.class);
                startActivity(intent);
                finish();
            }
        });
    }
    //뒤로가기 키 막기
    @Override
    public void onBackPressed() {
        //super.onBackPressed();
    }
}
