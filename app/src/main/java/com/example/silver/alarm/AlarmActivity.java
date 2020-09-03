package com.example.silver.alarm;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.media.MediaPlayer;
import android.os.Handler;
import android.os.Vibrator;
import android.provider.ContactsContract;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.example.silver.alarm.AlarmAdapter;
import com.example.silver.alarm.AlarmData;
import com.example.silver.alarm.SetAlarmActivity;
import com.melnykov.fab.FloatingActionButton;

import java.util.ArrayList;
import java.util.Calendar;

public class AlarmActivity extends AppCompatActivity {
    TextView t1;
    TextView t2;
    public static Context mContext;
    String[] weekDay = {"일", "월", "화", "수", "목", "금", "토"};
    Boolean today = false;
    String str_time, str_date, str_name, bol_state;
    // 알람을 울리는데 필요한 코드들
    AlarmManager alarm_manager;
    MediaPlayer test;
    PendingIntent pendingIntent = null;
    Context context;
    Calendar calendar;
    String hour, minute, gameType, day, date, state;
    Intent alarm_intent;
    private Handler handler;
    private Vibrator vibrator;
    private long[] pattern = {500, 500, 1000, 1000};
    public static Context mcontext;
    int currentHour, vibrate;
    ImageView sun;
    ImageView moon;
    int alarmCount = 0;

    private ArrayList<AlarmData> arrayList;
    private AlarmAdapter mainAdapter;
    private RecyclerView recyclerView;
    private LinearLayoutManager linearLayoutManager;

    int version = 1;
    DatabaseHelper helper;
    SQLiteDatabase database;   //db를 다루기 위한 SQLiteDatabase 객체 생성
    Cursor cursor; //select문 출력을 위해 사용하는 Cursor 형태 객체 생성
    String sql;
    ImageButton alarmBTN;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alarm);
        mContext = this;
        this.context = this;

        // 알람매니저 설정
        alarm_manager = (AlarmManager)getSystemService(ALARM_SERVICE);
        // Calender 객체 설정
        calendar = Calendar.getInstance();
        // 알람리시버 intent 생성
        alarm_intent = new Intent(getApplicationContext(), AlarmReceiver.class);

        //db 읽어오기 위해서 열기
        helper = new DatabaseHelper(AlarmActivity.this, DatabaseHelper.tableName,null,version);
        database = helper.getReadableDatabase();

        recyclerView = (RecyclerView)findViewById(R.id.rv);
        linearLayoutManager = new LinearLayoutManager( this);
        recyclerView.setLayoutManager(linearLayoutManager);

        //arraylist에 값 저장
        arrayList =new ArrayList<>();
        mainAdapter = new AlarmAdapter(arrayList, getApplicationContext());
        //가져온 값을 recylerview에 세팅
        recyclerView.setAdapter(mainAdapter);

        TextView t1 = (TextView)findViewById(R.id.t1);
        TextView t2 = (TextView)findViewById(R.id.t2);

        alarmBTN = (ImageButton)findViewById(R.id.alarmBTN);

        alarmBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent0 = new Intent(getApplicationContext(), SetAlarmActivity.class);
                startActivity(intent0);
                finish();
            }
        });

        readAlarm();
        readINFO();
        moveSun();
    }
    // 시간에 따라 해 움직이기 기능
    private void moveSun() {

        sun = (ImageView)findViewById(R.id.sun);
        moon = (ImageView)findViewById(R.id.moon);
        currentHour = calendar.get(calendar.HOUR_OF_DAY); //현재 시

        //해
        if(currentHour == 6 || currentHour == 7  ) {
            sun.setVisibility(View.VISIBLE);
            sun.setX(sun.getX() + (sun.getWidth()/ 2) - 930);
            sun.setY(sun.getY() + (sun.getHeight()/ 2) );
        }
        if(currentHour == 8 || currentHour == 9  ) {
            sun.setVisibility(View.VISIBLE);
            sun.setX(sun.getX() + (sun.getWidth()/ 2) - 830);
            sun.setY(sun.getY() + (sun.getHeight()/ 2) - 230);
        }
        if(currentHour == 10 || currentHour == 11  ) {
            sun.setVisibility(View.VISIBLE);
            sun.setX(sun.getX() + (sun.getWidth() / 2) - 630);
            sun.setY(sun.getY() + (sun.getHeight() / 2) - 350);
        }
        if(currentHour == 12 || currentHour == 13 ) {
            sun.setVisibility(View.VISIBLE);
            sun.setX(sun.getX() + (sun.getWidth()/ 2) - 280);
            sun.setY(sun.getY() + (sun.getHeight()/ 2) - 350);
        }
        if(currentHour == 14 || currentHour == 15  ) {
            sun.setVisibility(View.VISIBLE);
            sun.setX(sun.getX() + (sun.getWidth()/ 2) - 80);
            sun.setY(sun.getY() + (sun.getHeight()/ 2) - 230);
        }
        if(currentHour == 16 || currentHour == 17  ) {
            sun.setVisibility(View.VISIBLE);
            sun.setX(sun.getX() + (sun.getWidth()/ 2) );
            sun.setY(sun.getY() + (sun.getHeight()/ 2) );
        }
        //달
        if(currentHour == 18 || currentHour == 19  ) {
            moon.setVisibility(View.VISIBLE);
            moon.setX(moon.getX() + (moon.getWidth()/ 2) - 930);
            moon.setY(moon.getY() + (moon.getHeight()/ 2) );
        }
        if(currentHour == 20 || currentHour == 21  ) {
            moon.setVisibility(View.VISIBLE);
            moon.setX(moon.getX() + (moon.getWidth()/ 2) - 830);
            moon.setY(moon.getY() + (moon.getHeight()/ 2) - 230);
        }
        if(currentHour == 22 || currentHour == 23  ) {
            moon.setVisibility(View.VISIBLE);
            moon.setX(moon.getX() + (moon.getWidth() / 2) - 630);
            moon.setY(moon.getY() + (moon.getHeight() / 2) - 350);
        }
        if(currentHour == 24 || currentHour == 1 ) {
            moon.setVisibility(View.VISIBLE);
            moon.setX(moon.getX() + (moon.getWidth()/ 2) - 280);
            moon.setY(moon.getY() + (moon.getHeight()/ 2) - 350);
        }
        if(currentHour == 2 || currentHour == 3  ) {
            moon.setVisibility(View.VISIBLE);
            moon.setX(moon.getX() + (moon.getWidth()/ 2) - 80);
            moon.setY(moon.getY() + (moon.getHeight()/ 2) - 230);
        }
        if(currentHour == 4 || currentHour == 5  ) {
            moon.setVisibility(View.VISIBLE);
            moon.setX(moon.getX() + (moon.getWidth()/ 2) );
            moon.setY(moon.getY() + (moon.getHeight()/ 2) );
        }
    }

    //저장된 알람 읽어오기
    private void readAlarm() {
        try {
            sql = "select * from " + DatabaseHelper.tableName;
            cursor = database.rawQuery(sql, null); // select 사용시 사용(sql문, where조건 줬을 때 넣는 값)

            int count = cursor.getCount(); // db에 저장된 행 개수를 읽어옴

            for(int i = 0; i < count; i++) {
                cursor.moveToNext(); //  Cursor를 다음 행(Row)으로 이동 시킨다.
                str_time = cursor.getString(0); //첫번째 속성
                str_date = cursor.getString(1); //두번째 속성
                str_name = cursor.getString(2); //세번째 속성
                bol_state = cursor.getString(8); // 마지막 속성(스위치버튼)

                // 스위치가 ON
                if(bol_state.equals("true")) {
                    AlarmData mainData = new AlarmData(str_time, str_date, str_name, true);
                    arrayList.add(mainData);
                    mainAdapter.notifyDataSetChanged();
                }
                // 스위치가 OFF
                else{
                    AlarmData mainData = new AlarmData(str_time, str_date, str_name, false);
                    arrayList.add(mainData);
                    mainAdapter.notifyDataSetChanged();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // 리스트뷰에서 시, 분 나눠서 저장하는 함수!
    public void setTime(String time){
        for(int i = 0; i < time.length(); i++) {
            if (time.charAt(1) != ':') {
                if (i == 0)
                    hour = Character.toString(time.charAt(i));
                else if (i == 1)
                    hour += Character.toString(time.charAt(i));
                else if (i == 3)
                    minute = Character.toString(time.charAt(i));
                else if (i == 4)
                    minute += Character.toString(time.charAt(i));
            }
            else if (time.charAt(1) == ':'){
                if (i == 0)
                    hour = Character.toString(time.charAt(i));
                else if (i == 2)
                    minute = Character.toString(time.charAt(i));
                else if (i == 3)
                    minute += Character.toString(time.charAt(i));
            }
        }
        checkDay(date);
    }

    public void checkDay(String date){
        int num = calendar.get(Calendar.DAY_OF_WEEK)-1;
        String today_what = weekDay[num];

        for(int i = 0; i < date.length(); i++) {
            if (today_what.equals(String.valueOf(date.charAt(i)))) {
                today = true;
            }
        }
        setAlarm(hour, minute, gameType, vibrate);
    }

    public void setAlarm(String hour, String minute, String gameType, int vibrate) {
        calendar.set(Calendar.HOUR_OF_DAY, Integer.valueOf(hour));
        calendar.set(Calendar.MINUTE, Integer.valueOf(minute));

        // 지금 내 핸드폰의 시간이 알람 설정 시간보다 적을 때 알람을 설정한다. (지난 시간을 설정했을 때 바로 울리는 것을 방지!)
        if(System.currentTimeMillis() < calendar.getTimeInMillis()) {
            //진동 0, 게임 선택 0
            if(vibrate == 1 && gameType != null ) {
                alarm_intent.putExtra("vibState", "vibrator on");
                alarm_intent.putExtra("vibrate", vibrate);
                alarm_intent.putExtra("state", "alarm on");
                alarm_intent.putExtra("game", gameType);
            }
            //진동 x, 게임 선택 0
            if(vibrate == 0 && gameType != null) {
                alarm_intent.putExtra("state", "alarm on");
                alarm_intent.putExtra("vibState", "vibrator off");
                alarm_intent.putExtra("game", gameType);
            }

            pendingIntent = PendingIntent.getBroadcast(getApplicationContext(), alarmCount, alarm_intent,
                    PendingIntent.FLAG_UPDATE_CURRENT);
            alarm_manager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);
        }
    }

    //저장된 알람 읽어오기
    public void readINFO() {

        int count = mainAdapter.getItemCount();

        if(count > 0) {
            for(int i = 0; i < count; i++) {
                String time = arrayList.get(i).getTime();
                try {
                    sql = "SELECT * FROM " + DatabaseHelper.tableName + " WHERE time = " + "'" + time + "'";
                    cursor = database.rawQuery(sql, null); // select 사용시 사용(sql문, where조건 줬을 때 넣는 값)

                    cursor.moveToNext(); //  Cursor를 다음 행(Row)으로 이동 시킨다.
                    date = cursor.getString(1);
                    gameType = cursor.getString(7);
                    state = cursor.getString(8);
                    vibrate = cursor.getInt(5);
                    if(state.equals("true")) {
                        setTime(time);
                        alarmCount++;
                    }
                    else{
                        if(pendingIntent != null) {

                        }
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }
}