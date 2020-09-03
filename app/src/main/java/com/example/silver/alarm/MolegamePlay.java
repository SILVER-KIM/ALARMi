package com.example.silver.alarm;

import android.content.Intent;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Random;

public class MolegamePlay extends AppCompatActivity {

    TextView tv_timeCount;
    TextView tv_catchMole;
    SoundPool sound;
    int soundId;

    //두더지 들어가있는 이미지
    ImageView[] img_array = new ImageView[9];
    int[] imageID = {R.id.imageView1, R.id.imageView2, R.id.imageView3, R.id.imageView4,
            R.id.imageView5, R.id.imageView6, R.id.imageView7, R.id.imageView8, R.id.imageView9};

    int catchMoleNum = 0; //잡은 두더지 개수
    final String TAG_ON = "on";  // 태그 - 두더지 올라옴
    final String TAG_OFF = "off";  // 태그 - 두더지 들어감

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_molegame_play);

        //두더지 맞을 때 소리
        sound = new SoundPool(1, AudioManager.STREAM_MUSIC, 0);
        soundId = sound.load(this, R.raw.jab, 1);

        tv_timeCount = (TextView) findViewById(R.id.tv_timeCount);

        tv_catchMole = (TextView) findViewById(R.id.tv_catchMole);

        //처음 이미지를 두더지 들어간 모습으로 셋팅
        for (int i = 0; i < img_array.length; i++) {
            img_array[i] = (ImageView) findViewById(imageID[i]);
            img_array[i].setImageResource(R.drawable.moledown);
            img_array[i].setTag(TAG_OFF); //들어갔으니까

            //두더지를 클릭했을때 기능
            img_array[i].setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //두더지가 올라왔을 상태일 때 클릭하면
                    if (((ImageView) v).getTag().toString().equals(TAG_ON)) {
                        catchMoleNum += 100;
                        tv_catchMole.setText(" " +String.valueOf(catchMoleNum));
                        ((ImageView) v).setImageResource(R.drawable.molehurt);
                        sound.play(soundId, 1, 1, 0, 0, 1);
                        v.setTag(TAG_OFF);

                        //점수가 2000점 이상이면 알람 종료 화면으로 넘어가기
                        if (catchMoleNum >= 2000) {
                            Intent intent = new Intent(MolegamePlay.this, GameEndActivity.class);
                            startActivity(intent);
                            finish();
                        }
                    }
                    //두더지가 들어가 있는 걸 눌렀을 때
                    else {
                        //현재 스코어가 0보다 작으면(계속 차감하면 오류땜에)
                        if (catchMoleNum <= 0) {
                            catchMoleNum = 0;
                            tv_catchMole.setText(" " +String.valueOf(catchMoleNum));
                        }
                        //점수 차감!
                        else {
                            catchMoleNum -= 100;
                            tv_catchMole.setText(" " +String.valueOf(catchMoleNum));
                            //Toast.makeText(getApplicationContext(), "점수 100점을 차감합니다.", Toast.LENGTH_SHORT).show();
                        }
                    }
                }
            });
        }

        //스레드 객체 생성 후 start메소드로 스레드의 run을 실행 시켜줌!
        timeThread t = new timeThread();
        t.start();

        //두더지 스레드
        for (int i = 0; i < img_array.length; i++) {
            new Thread(new DThread(i)).start();
        }

    }

    //안드로이드에서 UI 업데이트는 main스레드에서만 가능하기 때문에 내가 생성한 외부 스레드에서
    //UI를 업데이트 할라면 hanler가 따로 필요함
    // 시간 줄어드는거 전달 받음
    Handler timeHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            tv_timeCount.setText(" " + msg.arg1);

            //점수 2000점 이상을 넘지 못해서 다시 하기 화면으로 돌아가기(알람 종료 못함)
            if(msg.arg1 == 0 && catchMoleNum < 2000) {
                Intent intent = new Intent(MolegamePlay.this, MolegameRestart.class);
                startActivity(intent);
                finish();
            }
        }
    };

    //두더지 올라왔다는 메세지 전달받을 헨들러 함수
    Handler onHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            img_array[msg.arg1].setImageResource(R.drawable.moleup);
            img_array[msg.arg1].setTag(TAG_ON); //두더지가 올라오면 ON태그 달아줌
        }
    };

    //두더지 내려갔다는 메세지 전달받을 헨들러 함수
    Handler offHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            img_array[msg.arg1].setImageResource(R.drawable.moledown);
            img_array[msg.arg1].setTag(TAG_OFF); //두더지 내려오면 OFF태그 달아줌
        }
    };

    //Thread 클래스를 상속 받는 하위 스레드 클래스 생성
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

    //두더지를 올라갔다 내려갔다 하게 하는 클레스 ( Runnable로 임플 받는건 다중 상속이 불가능 함으로 더 권장하는 방법)
    public class DThread implements Runnable {
        int index = 0; //두더지 번호
        public DThread(int index) {
            this.index = index;
        }

        @Override
        public void run() {
            while(true){
                try {
                    Message msg1 = new Message();
                    int offtime = new Random().nextInt(3000)+500;
                    Thread.sleep(offtime); //두더지가 내려가 있는 시간

                    msg1.arg1 = index;
                    onHandler.sendMessage(msg1);

                    Message msg2 = new Message();
                    int ontime  = new Random().nextInt(500)+500;
                    Thread.sleep(ontime); //두더지가 올라와있는 시간

                    offHandler.sendMessage(msg2);
                    msg2.arg1 = index;
                }
                catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    //뒤로 가기 키 막기
    @Override
    public void onBackPressed() {
        //super.onBackPressed();
    }
}

