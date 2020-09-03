package com.example.silver.alarm;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.media.MediaPlayer;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

public class ShakeGamePlay extends AppCompatActivity {
    TextView gx,gy,gz;
    TextView txtStep, txtSensitive;
    Button btnReset;
    SeekBar seekBar;
    SensorManager sm;
    int threshold;
    float acceleration;
    float previousY, currentY;
    int steps;
    Animation mAnim1;
    private static MediaPlayer event;
    TextView time2;
    timeThread t;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //화면 세로 고정
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        setContentView(R.layout.shakeitgame_play);

        event = MediaPlayer.create(this, R.raw.event);
        event.setLooping(true);
        event.start();

        time2 = (TextView) findViewById(R.id.time2);

        final ImageView iv = (ImageView) findViewById(R.id.imageView1);
        // 트윈에니메이션 -Rotate 회전 xml의 코드를 로드해서 적용하기
        Animation anim = AnimationUtils.loadAnimation(
                getApplicationContext(), // 현재 화면의 제어권자
                R.anim.rotate);    // 설정한 에니메이션 파일
        iv.startAnimation(anim);

        txtStep=(TextView)findViewById(R.id.step);
        txtSensitive=(TextView)findViewById(R.id.sensitive);

        seekBar = (SeekBar)findViewById(R.id.seekBar);
        seekBar.setProgress(10);
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            //시크바로 문턱값(스레시 홀드)을 지정한다.
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                threshold=seekBar.getProgress();
                txtSensitive.setText(String.valueOf(threshold));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
        threshold = 10;
        txtSensitive.setText(String.valueOf(threshold));
        previousY = currentY = steps = 0;
        acceleration = 0.0f;
        //가속도계에 이벤트 리스너를 등록
        SensorManager sm = (SensorManager)getSystemService(SENSOR_SERVICE);
        sm.registerListener(stepDetector, sm.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),
                SensorManager. SENSOR_DELAY_NORMAL);

        t = new timeThread();
        t.start();


    }

    //무명 클래스를 이용하여서 이벤트 리스너를 구현
    private SensorEventListener stepDetector = new SensorEventListener() {
        @Override
        public void onAccuracyChanged(Sensor arg0, int arg1) {
        }

        @Override
        public void onSensorChanged(SensorEvent sevent) {
            float x = sevent.values[0];
            float y = sevent.values[1];
            float z = sevent.values[2];
            currentY = y;
            //단순히 y방향 가속도의 상대적인 크기가 일정 한계를 넘으면 걸음수를 증가한다.
            if (Math.abs(currentY - previousY) > threshold) {
                steps++;
                txtStep.setText(String.valueOf(steps));
            }
            previousY = y;
        }

    };

    Handler timeHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            time2.setText(" " + msg.arg1);

            event.start();
            checkEnd(msg);
        }
    };

    public void checkEnd(Message msg){
        //점수 2000점 이상을 넘지 못해서 다시 하기 화면으로 돌아가기(알람 종료 못함)
        if(msg.arg1 == 0 && steps < 30) {
            event.stop();
            Intent intent = new Intent(ShakeGamePlay.this, ShakeGame.class);
            startActivity(intent);
            finish();
        }
        else if (steps >=30) {
            event.stop();
            killAlarmThread();
            Intent intent = new Intent(ShakeGamePlay.this, GameEndActivity.class);
            startActivity(intent);
            finish();
        }
    }

    private void killAlarmThread() {
        try {
            t.interrupt();
            t.join();
        } catch(InterruptedException e) {
            e.printStackTrace();
        }
    }

    //스탑워치 쓰레드
    public class timeThread extends Thread {
        int second = 20;

        @Override
        //스레드가 실행되면 수행되는 곳
        public void run() {
            for (int i = second; i >= 0; i--) {
                Message msg = new Message();
                msg.arg1 = i;
                timeHandler.sendMessage(msg);
                try {
                    //실행 중인 스레드를 1초동안 멈추게함. (1/1000) 단위
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                    return;
                }
            }
        }
    }

    public void mClick(View v){
        steps = 0;
        txtStep.setText(String.valueOf(steps));
    }

    //뒤로 가기 x
    @Override
    public void onBackPressed() {
        //super.onBackPressed();
    }



}