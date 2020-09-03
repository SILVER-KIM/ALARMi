package com.example.silver.alarm;


import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;

public class MolegameStart extends AppCompatActivity {

    ImageButton btnStart;
    ImageButton btnHowtoplay;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_molegame_start);

        btnStart = (ImageButton)findViewById(R.id.btnStart);
        btnHowtoplay = (ImageButton)findViewById(R.id.btnHowtoplay);

        //게임 시작 누르면 게임 엑티비티로 전환!
        btnStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MolegameStart.this, MolegamePlay.class);
                startActivity(intent);
                finish();
            }
        });

        //게임 설명 화면으로 넘어가기
        btnHowtoplay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MolegameStart.this,MolegameInfo.class);
                startActivity(intent);
            }
        });
    }
}
