package com.example.silver.alarm;

import android.content.Intent;
import android.graphics.Point;
import android.media.MediaPlayer;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Display;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

public class FpsActivity extends AppCompatActivity{
    private static MediaPlayer mp1, mp2, mp3, mp4, mp5;
    TranslateAnimation animation;
    LinearLayout fps;
    TextView score, arrowNum;
    ImageView target, bow, arrow, shootingBTN, crushImg;
    ProgressBar pb;
    private final Handler handler = new Handler();
    Timer timer = new Timer();
    Timer shootTimer = new Timer();
    private TimerTask second, shootSecond;
    int timer_sec = 0;
    int pGage;
    int xPos, yPos;
    int gameScore = 0;
    int arrowNumber = 10;
    Random rand;

    private int viewX;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fps);

        mp1 = MediaPlayer.create(this, R.raw.alarmloop);
        mp1.setLooping(true);
        mp1.start();

        mp2 = MediaPlayer.create(this, R.raw.pulling); // 쏘는 소리
        mp3 = MediaPlayer.create(this, R.raw.shooting); // 땅에 떨어지는 소리
        mp4 = MediaPlayer.create(this, R.raw.shootend); // 과녁에 맞는 소리
        mp5 = MediaPlayer.create(this, R.raw.flying); // 날아가는 소리
        mp5.setLooping(true);

        fps = (LinearLayout) findViewById(R.id.fps);
        bow = (ImageView) findViewById(R.id.bow);
        shootingBTN = (ImageView) findViewById(R.id.shootingBTN);
        target = (ImageView) findViewById(R.id.target);
        arrow = (ImageView) findViewById(R.id.arrow);
        score = (TextView) findViewById(R.id.score);
        arrowNum = (TextView) findViewById(R.id.arrowNum);
        pb = (ProgressBar) findViewById(R.id.pb);
        crushImg = (ImageView) findViewById(R.id.crush);

        pb.setX(bow.getX() + (bow.getWidth() / 2) - 190);
        pb.setY(bow.getY() + (bow.getHeight() / 2) - 120);

        score.setText("0점");
        // 화살 개수 10개로 기본 지정!
        arrowNum.setText(String.valueOf(arrowNumber) + "/10개");

        // 과녁의 랜덤 위치 지정
        rand = new Random();
        xPos = rand.nextInt(631) + 100;
        yPos = rand.nextInt(801) + 500;
        target.setX(xPos);
        target.setY(yPos);

        // 활을 터치했을 때 기능들
        bow.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {

                // 활을 눌렀을때는 프로그레스바가 상승하는 함수를 호출한다
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    pb.setVisibility(View.VISIBLE);
                    arrow.setVisibility(View.INVISIBLE);
                    shootingBTN.setVisibility(View.INVISIBLE);
                    viewX = (int) event.getX();
                    second = new TimerTask() {
                        @Override
                        public void run() {
                            Update();
                            timer_sec++;
                        }
                    };
                    timer.schedule(second, 0, 50);
                }
                // 활을 누른채로 드래그할때는 드래그하는 곳으로 활을 이동시켜준다
                if (event.getAction() == MotionEvent.ACTION_MOVE) {
                    v.setX(v.getX() + (event.getX()) - (v.getWidth() / 2));
                    if (v.getX() < 150) {
                        pb.setX(v.getX() + (v.getWidth() / 2) + 190);
                        pb.setY(v.getY() + (v.getHeight() / 2) - 120);
                        shootingBTN.setX(v.getX() + (v.getWidth() / 2) + 130);
                        shootingBTN.setY(v.getY() + (v.getHeight() / 2) + 120);
                    } else if (v.getX() >= 150) {
                        pb.setX(v.getX() + (v.getWidth() / 2) - 190);
                        pb.setY(v.getY() + (v.getHeight() / 2) - 120);
                        shootingBTN.setX(v.getX() + (v.getWidth() / 2) - 240);
                        shootingBTN.setY(v.getY() + (v.getHeight() / 2) + 120);
                    }
                }
                // 활에서 손을 떼면 화살을 활을 위치에 지정해주고 슈팅버튼을 VISIBLE해준다
                if (event.getAction() == MotionEvent.ACTION_UP) {
                    float arrowXPos = bow.getX() + (bow.getWidth() / 2) - 20;
                    final float arrowYPos = bow.getY() + (bow.getHeight() / 2) + 50;
                    arrow.setX(arrowXPos);
                    arrow.setY(arrowYPos);
                    timer_sec = 0;
                    timer.cancel();
                    timer = new Timer();
                    arrowNumber -= 1;
                    arrowNum.setText(String.valueOf(arrowNumber) + "/10개");
                    mp2.start();
                    shootTimer = new Timer();
                    shootSecond = new TimerTask() {
                        @Override
                        public void run() {
                            runOnUiThread(new Runnable() {
                                public void run() {
                                    mp5.start();
                                    Shooting(pGage);
                                }
                            });
                        }
                    };
                    shootTimer.schedule(shootSecond, 0, 30);
                    shootingBTN.setImageResource(R.drawable.nonclick);
                    change();
                }
                return true;
            }
        });
    }

    // 과녁의 위치를 랜덤으로 지정해주는 함수
    public void changeTarget(){
        xPos = rand.nextInt(631) + 100;
        yPos = rand.nextInt(801) + 500;
        target.setX(xPos);
        target.setY(yPos);
    }

    // 충돌판정 함수
    public boolean crush(){
        int targetX = (int)target.getX() +(int)target.getWidth();       // 과녁의 x길이
        int targetY = (int)target.getY() + (int)target.getHeight();     // 과녁의 y길이

        int arrowX = (int)arrow.getX() +(int)arrow.getWidth();       // 과녁의 x길이
        int arrowY = (int)arrow.getY() + (int)arrow.getHeight();     // 과녁의 y길이

        if(target.getX() + 30 < arrow.getX() && targetX - 30 > arrowX && target.getY() < arrow.getY() && target.getY() + 150 > arrow.getY()){
            mp5.stop();
            mp4.start();
            shootTimer.cancel();
            gameScore += 100;
            score.setText(String.valueOf(gameScore) + "점");
            checkEnd();
            crushImg.setX(target.getX());
            crushImg.setY(target.getY() - 180);
            crushImg.setVisibility(View.VISIBLE);
            final Runnable changer = new Runnable() {
                @Override
                public void run() {
                    crushImg.setVisibility(View.INVISIBLE);
                    changeTarget();
                    arrow.setVisibility(View.INVISIBLE);
                }
            };
            handler.postDelayed(changer, 500);

        }
        return true;
    }

    public void checkEnd(){
        if(arrowNumber == 0 && gameScore < 800){
            mp1.stop();
            setResult(100);
            this.finish();
        }
        else if(gameScore >= 800){
            mp1.stop();
            Intent gointent = new Intent(getApplicationContext(), GameEndActivity.class);
            startActivity(gointent);
            this.finish();
        }
    }
    // 프로그레스바의 수치를 받아와서 화살이 날라가는 거리를 지정 -> 쏘기 함수
    public void Shooting(final int gage) {

        arrow.setVisibility(View.VISIBLE);
        final Runnable updater = new Runnable() {
            @Override
            public void run() {
                if (gage <= 10) {
                    if(arrow.getY() > 1400) {
                        arrow.setY(arrow.getY() - 20);
                        crush();
                    }
                    else if(arrow.getY() <= 1400) {
                        shootTimer.cancel();
                        mp5.stop();
                        mp3.start();
                        checkEnd();
                    }
                }
                else if (gage <= 20) {
                    if(arrow.getY() > 1200) {
                        arrow.setY(arrow.getY() - 30);
                        crush();
                    }
                    else if(arrow.getY() <= 1200) {
                        shootTimer.cancel();
                        mp5.stop();
                        mp3.start();
                        checkEnd();
                    }
                }
                else if (gage <= 30) {
                    if(arrow.getY() > 1000) {
                        arrow.setY(arrow.getY() - 40);
                        crush();
                    }
                    else if(arrow.getY() <= 1000) {
                        shootTimer.cancel();
                        mp5.stop();
                        mp3.start();
                        checkEnd();
                    }
                }
                else if (gage <= 40) {
                    if(arrow.getY() > 600) {
                        arrow.setY(arrow.getY() - 50);
                        crush();
                    }
                    else if(arrow.getY() <= 600) {
                        shootTimer.cancel();
                        mp5.stop();
                        mp3.start();
                        checkEnd();
                    }
                }
            }
        };
        handler.post(updater);
    }

    // 슈팅버튼의 모습을 변경시켜주는 함수
    public void change(){
        final Runnable changer = new Runnable() {
            @Override
            public void run() {
                shootingBTN.setImageResource(R.drawable.click);
            }
        };
        handler.postDelayed(changer, 1000);
    }

    // 타이머를 받아와서 0.125초마다 프로그레스바를 움직여주는 함수
    public void Update() {
        final Runnable updater = new Runnable() {
            @Override
            public void run() {
                pGage = 0;
                // 홀수일 때 프로그레스바가 줄어들고
                if ((timer_sec / 40) % 2 == 1) {
                    pGage = 40 - (timer_sec % 40);
                    pb.setProgress(pGage);
                }
                // 짝수일 때 프로그레스바가 늘어난다.
                else if ((timer_sec / 40) % 2 == 0) {
                    pGage = timer_sec % 40;
                    pb.setProgress(pGage);
                }
            }
        };
        handler.post(updater);
    }
}