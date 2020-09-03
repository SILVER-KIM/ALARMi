package com.example.silver.alarm;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import java.util.Timer;
import java.util.TimerTask;

public class GameEndActivity extends AppCompatActivity {
    private TimerTask second;
    int timer_sec = 3;
    TextView diatime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_end);

        diatime = (TextView)findViewById(R.id.diatext);

        Intent my_intent = new Intent(getApplicationContext(), AlarmReceiver.class);
        my_intent.putExtra("state", "alarm_off");
        my_intent.putExtra("vibState","vibrator off");
        sendBroadcast(my_intent);

        Timer timer = new Timer(true);
        second = new TimerTask() {
            @Override
            public void run() {
                Message msg = handler.obtainMessage();
                handler.sendMessage(msg);
            }

            @Override
            public boolean cancel() {
                return super.cancel();
            }
        };
        timer.schedule(second, 0, 1000);
    }

    public void change(int timer_sec){
        diatime.setText(String.valueOf(timer_sec) + "초 뒤에 종료됩니다:)");

        if(timer_sec == 0){
            diatime.setText("이제 종료합니다:)");
            finish();
        }
    }

    final Handler handler = new Handler(){
        public void handleMessage(Message msg){
            // 원래 하려던 동작 (UI변경 작업 등)
            change(timer_sec);
            timer_sec--;
        }
    };
}