package com.example.silver.alarm;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.GlideDrawableImageViewTarget;

import java.text.SimpleDateFormat;
import java.util.Date;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        long now =  System.currentTimeMillis();
        Date date = new Date(now);
        SimpleDateFormat NT =  new SimpleDateFormat("HH");
        String nt =  NT.format(date);
        int nowTime = Integer.parseInt(nt);

        Toast.makeText(getApplicationContext(), nt, Toast.LENGTH_SHORT).show();

        RelativeLayout start_ac = (RelativeLayout)findViewById(R.id.start_ac);
        if(nowTime <= 8 || nowTime >= 19)
        {
            start_ac.setBackgroundResource(R.mipmap.night);
        }
        else
        {
            start_ac.setBackgroundResource(R.mipmap.morning);
        }

        start_ac.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), AlarmActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }
}
