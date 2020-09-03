package com.example.silver.alarm;

import android.app.AlarmManager;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.media.Image;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Environment;
import android.os.Handler;
import android.provider.ContactsContract;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;
import android.widget.ToggleButton;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static java.lang.Boolean.FALSE;
import static java.lang.Boolean.TRUE;

public class SetAlarmActivity extends AppCompatActivity implements TimePicker.OnTimeChangedListener  {
    TextView bubble_chat;
    TextView timeText;
    TextView soundText;
    ImageView bubble, mole, mRules, aRules;
    TimePicker timeP;
    Calendar cal;
    int newColor, oldColor;
    private ToggleButton monD, tueD, wedD, thuD, friD, satD, sunD;
    Button okBTN;
    Button cancelBTN;
    EditText alarmName;
    Switch soundSW;
    Switch bellSW;
    Switch typeSW;
    TextView tvGameSelected;
    TextView tvAlarmBellSelected;
    Boolean soundChecked;

    //데이터베이스 헬퍼와 연결
    int version = 1;
    DatabaseHelper helper;
    SQLiteDatabase database;
    Cursor cursor;
    int choiceIndex = 0;
    Map<String, String> list;
    String[] alarmDate = new String[7];


    //alertDialog에 필요한 변수
    private static final String TAG_IMAGE = "image";
    private static final String TAG_TEXT="text";
    int mSelectedPosition = -1;
    Boolean gameChecked;
    GameListItem item;
    String temp1; //선택한 게임이름 임시로 저장
    String temp2; //선택한 알람 벨소리 임시로 저장
    AlertDialog dialog;

    //데이터베이스에 들어가야할 속성
    String strTime;
    String strDays;
    String strAlarmName;
    int soundSet = 0;  //default 값이 0(false)인 상태
    int vibSet = 0;
    int gameSet = 0;
    String strGameName; //선택한 게임
    String strSoundName; //선택한 알람 소리 이름

    //현재 시간
    int currentHour;
    int currentMinute;

    private static final String BASE_PATH = Environment.getExternalStorageDirectory() + "/myapp";
    private static final String NORMAL_PATH = BASE_PATH + "/normal";
    private AlarmManager _am;


    //알람 소리 결과 받기
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode == 0 && data!= null){
            Uri pickedUri = data.getParcelableExtra(RingtoneManager.EXTRA_RINGTONE_PICKED_URI);
            if(pickedUri != null) {
                Ringtone r = RingtoneManager.getRingtone(this, pickedUri);
                //선택된 벨소리 이름 저장해서 보여주기
                String ringToneName = ((Ringtone) r).getTitle(this);
                temp2 = ringToneName;
                strSoundName = ringToneName;
                tvAlarmBellSelected.setText(ringToneName);
            }
        }
        else {
            //알람 소리 설정안하고 취소 눌렀을 때
            if(soundChecked == TRUE)
            {
                soundSW.setChecked(FALSE);
                soundChecked = FALSE;
                soundSet = 0;
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set_alarm);

        //데이터베이스
        helper = new DatabaseHelper(SetAlarmActivity.this, DatabaseHelper.tableName,null,version);
        database = helper.getWritableDatabase();  //데이터베이스 읽고 쓰겠다

        // 두더지 눌렀을 때, 메세지가 나오게 하려고 정의해두기!
        bubble_chat = (TextView)findViewById(R.id.bubble_chat);
        bubble = (ImageView)findViewById(R.id.bubble);
        mole = (ImageView)findViewById(R.id.mole);

        mRules = (ImageView)findViewById(R.id.mRules);
        aRules = (ImageView)findViewById(R.id.aRules);

        //선택된 애들 보여주기
        tvGameSelected = (TextView) findViewById(R.id.tvGameSelected);
        tvAlarmBellSelected = (TextView) findViewById(R.id.tvAlarmBellSelected);

        mole.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bubble.setVisibility(View.VISIBLE);
                bubble_chat.setVisibility(View.VISIBLE);
                new Handler().postDelayed(new Runnable()
                {
                    @Override
                    public void run()
                    {
                        bubble.setVisibility(View.INVISIBLE);
                        bubble_chat.setVisibility(View.INVISIBLE);
                    }
                }, 1500);

            }
        });

        _am = (AlarmManager) getSystemService(Context.ALARM_SERVICE);

        // 요일 설정의 (월요일-일요일)버튼 정의해두기!
        monD = (ToggleButton)findViewById(R.id.mon);
        tueD = (ToggleButton)findViewById(R.id.tue);
        wedD = (ToggleButton)findViewById(R.id.wed);
        monD = (ToggleButton)findViewById(R.id.mon);
        thuD = (ToggleButton)findViewById(R.id.thu);
        friD = (ToggleButton)findViewById(R.id.fri);
        satD = (ToggleButton)findViewById(R.id.sat);
        sunD = (ToggleButton)findViewById(R.id.sun);

        oldColor = Color.parseColor("#000000");
        newColor = Color.parseColor("#96C8FA");

        // 각각의 요일 버튼을 클릭하면 색이 변하도록 하는 클릭 이벤트(만약에 이미 눌려진 상태라면 색을 검은색으로 돌리기)
        Button.OnClickListener onClickListener = new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (v.getId()){
                    case R.id.mon:
                        if(monD.getCurrentTextColor() == newColor)
                            monD.setTextColor(oldColor);
                        else
                            monD.setTextColor(newColor);
                        break;
                    case R.id.tue:
                        if(tueD.getCurrentTextColor() == newColor)
                            tueD.setTextColor(oldColor);
                        else
                            tueD.setTextColor(newColor);
                        break;
                    case R.id.wed:
                        if(wedD.getCurrentTextColor() == newColor)
                            wedD.setTextColor(oldColor);
                        else
                            wedD.setTextColor(newColor);
                        break;
                    case R.id.thu:
                        if(thuD.getCurrentTextColor() == newColor)
                            thuD.setTextColor(oldColor);
                        else
                            thuD.setTextColor(newColor);
                        break;
                    case R.id.fri:
                        if(friD.getCurrentTextColor() == newColor)
                            friD.setTextColor(oldColor);
                        else
                            friD.setTextColor(newColor);
                        break;
                    case R.id.sat:
                        if(satD.getCurrentTextColor() == newColor)
                            satD.setTextColor(oldColor);
                        else
                            satD.setTextColor(newColor);
                        break;
                    case R.id.sun:
                        if(sunD.getCurrentTextColor() == newColor)
                            sunD.setTextColor(oldColor);
                        else
                            sunD.setTextColor(newColor);
                        break;
                }
            }
        };

        monD.setOnClickListener(onClickListener);
        tueD.setOnClickListener(onClickListener);
        wedD.setOnClickListener(onClickListener);
        thuD.setOnClickListener(onClickListener);
        friD.setOnClickListener(onClickListener);
        satD.setOnClickListener(onClickListener);
        sunD.setOnClickListener(onClickListener);

        // 시간 관련

        timeText = (TextView) findViewById(R.id.timeText);
        timeP = (TimePicker) findViewById(R.id.time);
        cal = Calendar.getInstance(); // 캘린더 객체 생성
        currentHour = cal.get(cal.HOUR_OF_DAY); //현재 시
        currentMinute = cal.get(cal.MINUTE); //현재 분

        timeP.setOnTimeChangedListener(this);

        //알람 이름
        alarmName = (EditText)findViewById(R.id.alarmName);

        // 알람 설정 방법
        //소리 설정
        soundSW = (Switch)findViewById(R.id.soundSW);
        soundSW.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                soundChecked = isChecked;
                //스위치가 체크된 상태라면 1로 설정
                if(isChecked == TRUE) {
                    soundSet = 1;
                    Intent alarmBellIntent = new Intent(RingtoneManager.ACTION_RINGTONE_PICKER);
                    alarmBellIntent.putExtra(RingtoneManager.EXTRA_RINGTONE_TITLE,"알림음 설정");
                    alarmBellIntent.putExtra(RingtoneManager.EXTRA_RINGTONE_TYPE,RingtoneManager.TYPE_ALARM);
                    alarmBellIntent.putExtra(RingtoneManager.EXTRA_RINGTONE_TYPE,RingtoneManager.TYPE_RINGTONE);
                    startActivityForResult(alarmBellIntent, 0);
                }
                else {
                    soundSet = 0;
                    //취소하면 보여준 소리이름 없애기
                    if (temp2 != null) {
                        temp2 = null;
                        tvAlarmBellSelected.setText(temp2);
                    }
                }
            }
        });
        //진동 설정
        bellSW = (Switch)findViewById(R.id.bellSW);
        bellSW.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                //스위치가 체크된 상태라면 1로 설정
                if(isChecked == TRUE) {
                    vibSet = 1;
                }
                else
                    vibSet = 0;
            }
        });
        // 게임 설정
        typeSW = (Switch)findViewById(R.id.typeSW);
        typeSW.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                //스위치가 체크된 상태라면 1로 설정
                if(isChecked == TRUE) {
                    gameChecked = isChecked;
                    gameSet = 1;
                    //게임 선택 다이얼로그 화면으로 넘어감
                    selectGameDialog();
                }
                else{
                    gameSet = 0;
                    //스위치 다시 껐을 때 선택된 게임 지우기
                    if(temp1 != null) {
                        temp1 = null;
                        tvGameSelected.setText(temp1);
                    }
                }
            }
        });

        okBTN = (Button)findViewById(R.id.okBTN);
        //저장하기
        okBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //알람 이름 넣기
                strAlarmName = alarmName.getText().toString();
                int count = 0;
                String result = "";
                //if문 (월,화,수,목,금.토,일 선택한 요일 만큼을 데이터베이스에 넣음)
                if(monD.getCurrentTextColor() == newColor) {
                    strDays = monD.getTextOn().toString();
                    alarmDate[count] = strDays;
                    count++;
                }
                if(tueD.getCurrentTextColor() == newColor) {
                    strDays = tueD.getTextOn().toString();
                    alarmDate[count] = strDays;
                    count++;
                }
                if(wedD.getCurrentTextColor() == newColor) {
                    strDays = wedD.getTextOn().toString();
                    alarmDate[count] = strDays;
                    count++;
                }
                if(thuD.getCurrentTextColor() == newColor) {
                    strDays = thuD.getTextOn().toString();
                    alarmDate[count] = strDays;
                    count++;
                }
                if(friD.getCurrentTextColor() == newColor) {
                    strDays = friD.getTextOn().toString();
                    alarmDate[count] = strDays;
                    count++;
                }
                if(satD.getCurrentTextColor() == newColor) {
                    strDays = satD.getTextOn().toString();
                    alarmDate[count] = strDays;
                    count++;
                }
                if(sunD.getCurrentTextColor() == newColor) {
                    strDays = sunD.getTextOn().toString();
                    alarmDate[count] = strDays;
                    count++;
                }
                for(int i = 0; i < count; i++){
                    result += alarmDate[i];
                }
                //선택한 시간이 널값이면 현재시간 넣기
                if(strTime == null) {
                    strTime = currentHour + ":" + currentMinute;
                }
                helper.insertAlarm(database, strTime, result, strAlarmName, soundSet, strSoundName, vibSet, gameSet, strGameName, "true");
                Intent intent = new Intent(getApplicationContext(), AlarmActivity.class);
                startActivity(intent);
                finish();
            }
        });

        cancelBTN = (Button)findViewById(R.id.cancelBTN);

        cancelBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), AlarmActivity.class);
                startActivity(intent);
                finish();
            }
        });

    }

    //게임 선택 다이얼로그
    private void selectGameDialog() {
        final ArrayList<GameListItem> gameList = new ArrayList<GameListItem>();
        final GameListAdapter adapter = new GameListAdapter(SetAlarmActivity.this, R.layout.game_dialog_list_row, gameList);

        LayoutInflater inflater = getLayoutInflater();
        final View dialogView = inflater.inflate(R.layout.game_select_dialog,null);
        final ListView listView = (ListView)dialogView.findViewById(R.id.listview_game_list);

        final Boolean[] bGameChecked = {false, false, false};
        final String[] gameName = {"핸드폰 흔들기", "두더지 게임", "과녁 맞추기"};
        final int[] infoImg = {R.drawable.information, R.drawable.information,R.drawable.information};

        // list row에 아이템 넣기
        for(int i = 0; i < gameName.length; i++) {
            adapter.addItem(bGameChecked[i],gameName[i],ContextCompat.getDrawable(this,infoImg[i]));
            adapter.notifyDataSetChanged();
        }

        listView.setAdapter(adapter);

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(SetAlarmActivity.this);
        alertDialogBuilder.setView(dialogView);

        dialog = alertDialogBuilder.create();
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.setCanceledOnTouchOutside(false);
        dialog.setCancelable(false);
        dialog.show();

        Button btnOk = (Button) dialogView.findViewById(R.id.btnOk);
        Button btnCancel = (Button) dialogView.findViewById(R.id.btnCancel);

        //확인
        btnOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                for(int i = 0; i < gameName.length; i++ ){
                    item = gameList.get(i);
                    if (item.getGameChecked() == true) {
                        temp1 = item.getGameName();
                    }
                    //선택한 게임 이름 넣기
                    strGameName = temp1;
                    tvGameSelected.setText(temp1);
                }
                dialog.dismiss();
            }
        });

        //취소
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //게임 체크가 ON되어 있는 것을 OFF로 바꿈
                if(gameChecked == TRUE)
                {
                    typeSW.setChecked(FALSE);
                    gameChecked = FALSE;
                    gameSet = 0;
                }
                dialog.dismiss();
            }
        });

    }

    public void showMrules(){
        mRules.setVisibility(View.VISIBLE);
    }

    public void showArules(){
        aRules.setVisibility(View.VISIBLE);
    }

    // 설정한 시간 값 가져옴
    public void onTimeChanged(TimePicker view, int hourOfDay, int minute){
        //timeText.setText("현재시간" + hourOfDay + " : " + minute);
        strTime = hourOfDay+ ":" + minute;
    }

}