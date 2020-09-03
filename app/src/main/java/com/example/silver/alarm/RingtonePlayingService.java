package com.example.silver.alarm;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.IBinder;
import android.os.Vibrator;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.widget.Toast;

import static android.app.Service.START_NOT_STICKY;

public class RingtonePlayingService extends Service {

    MediaPlayer mediaPlayer;
    int startId;
    boolean isRunning;
    private Vibrator vibrator;
    NotificationChannel channel;
    NotificationManager mNotificationManager;
    String CHANNEL_ID;
    int startId2;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        if (Build.VERSION.SDK_INT >= 26) {
            CHANNEL_ID = "default";
            channel = new NotificationChannel(CHANNEL_ID,
                    "Channel human readable title",
                    NotificationManager.IMPORTANCE_DEFAULT);

            mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            mNotificationManager.createNotificationChannel(channel);

            Notification notification = new NotificationCompat.Builder(this, CHANNEL_ID)
                    .setContentTitle("알람시작")
                    .setContentText("알람음이 재생됩니다.")
                    .setSmallIcon(R.mipmap.ic_launcher)
                    .build();
            notification.flags |= Notification.FLAG_AUTO_CANCEL;

            startForeground(1, notification);
        }

    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        String getState = intent.getExtras().getString("state");
        String gameType = intent.getExtras().getString("game");
        String getVibState = intent.getExtras().getString("vibState");
        int vibrate = intent.getExtras().getInt("vibrate");

        assert getState != null;
        switch (getState) {
            case "alarm on":
                if(gameType.equals("두더지 잡기"))
                    startId = 1;
                else if(gameType.equals("과녁 맞추기"))
                    startId = 2;
                else if(gameType.equals("핸드폰 흔들기"))
                    startId = 3;
                break;
            case "alarm off":
                startId = 0;
                break;
            default:
                startId = 0;
                break;
        }
        assert getVibState != null;
        switch (getVibState) {
            case "vibrator on":
                if(vibrate == 1)
                    startId2 = 0;
                break;
            case "vibrator off":
                startId2 = 1;
                break;
            default:
                startId2 = 1;
                break;
        }

        // 알람음 재생 X , 두더지 잡기
        if(!this.isRunning && startId == 1) {
            mediaPlayer = MediaPlayer.create(this,R.raw.song1);
            mediaPlayer.setLooping(true);
            mediaPlayer.start();

            this.isRunning = true;
            this.startId = 0;

            if(startId2 == 0) {
                vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
                long[] pattern = {500, 2500, 500, 2500};
                vibrator.vibrate(pattern, 0);
            }

            Intent mintent = new Intent(getApplicationContext(), MolegameStart.class);
            mintent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(mintent);
        }

        // 알람음 재생 X , 양궁 게임
        if(!this.isRunning && startId == 2) {
            mediaPlayer = MediaPlayer.create(this,R.raw.song1);
            mediaPlayer.setLooping(true);
            mediaPlayer.start();

            this.isRunning = true;
            this.startId = 0;

            if(startId2 == 0) {
                vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
                long[] pattern = {500, 2500, 500, 2500};
                vibrator.vibrate(pattern, 0);
            }

            Intent aintent = new Intent(getApplicationContext(), FpsStartActivity.class);
            aintent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(aintent);
        }
        // 알람음 재생 X , 핸드폰 흔들기
        if(!this.isRunning && startId == 3) {
            mediaPlayer = MediaPlayer.create(this,R.raw.song1);
            mediaPlayer.setLooping(true);
            mediaPlayer.start();

            this.isRunning = true;
            this.startId = 0;

            if(startId2 == 0) {
                vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
                long[] pattern = {500, 2500, 500, 2500};
                vibrator.vibrate(pattern, 0);
            }

            Intent aintent = new Intent(getApplicationContext(), ShakeGame.class);
            aintent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(aintent);
        }
        // 진동세팅
        if(!this.isRunning && startId == 4) {
            vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
            long[] pattern = {500, 2500, 500, 2500};
            vibrator.vibrate(pattern, 0);
        }
        // 알람음 재생 O , 알람음 종료 버튼 클릭
        else if(this.isRunning && startId == 5) {
            vibrator.cancel();
        }
        // 알람음 재생 O , 알람음 종료 버튼 클릭
        else if(this.isRunning && startId == 0) {
            Toast.makeText(getApplicationContext(), "알람이 종료되었습니다.", Toast.LENGTH_SHORT).show();
            mediaPlayer.stop();
            mediaPlayer.reset();
            mediaPlayer.release();
            this.isRunning = false;
            this.startId = 0;
            if(vibrator!= null && startId2 == 1){
                vibrator.cancel();
            }
            if (Build.VERSION.SDK_INT >= 26) {
                mNotificationManager.deleteNotificationChannel(CHANNEL_ID);
            }
        }

        else {
        }
        return START_NOT_STICKY;
    }

    @Override
    public void onDestroy() {
        Toast.makeText(getApplicationContext(), "알람 종료됩니다:)", Toast.LENGTH_SHORT).show();
        super.onDestroy();
        Log.d("onDestory() 실행", "서비스 파괴");
    }
}