package com.example.silver.alarm;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.support.annotation.Nullable;
import android.util.Log;

public class DatabaseHelper extends SQLiteOpenHelper {

    //데이터 베이스 테이블 이름
    public static final String tableName = "alarmset";

    public DatabaseHelper(@Nullable Context context, @Nullable String name, @Nullable SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }
    //테이블을 만들기위한 과정
    @Override
    public void onCreate(SQLiteDatabase db) {
        createTable(db);
    }

    private void createTable(SQLiteDatabase db) {
        //테이블 만들기
        String sql = "CREATE TABLE " + tableName + "("
                + " time text, "                     //알람 시간
                + " days text, "                     //선택한 날짜
                + " alarmName text, "                //알람 이름
                + " soundSet integer default 0,"    //소리 설정 // SQLite에서 boolean 타입쓰는 법: 0 과 1 정수형으로 만들면됨
                + " soundName text, "                //알람 소리 이름
                + " vibSet integer default 0,"      //진동 설정
                + " gameSet integer default 0,"     //게임 설정
                + " gameName text, "
                + " state text )";                //게임 이름
        try {
            db.execSQL(sql);
        } catch (Exception ex) {
            Log.e("Create", "Exception in CREATE_SQL", ex);
        }
    }

    public void deleteData(SQLiteDatabase db, String alarmNAME){
        String sql = "DELETE FROM " + tableName + " WHERE alarmName = " + "'" + alarmNAME + "'";
        db.execSQL(sql);
    }

    public void changeState(SQLiteDatabase db, String alarmNAME, String state){
        String sql = "UPDATE " + tableName + " SET state = " + "'" + state + "'" + " WHERE alarmName = " + "'" + alarmNAME + "'";
        db.execSQL(sql);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + tableName);
        //테이블 다시 만들기
        onCreate(db);
        Log.w("version", "Upgrading database from version " + oldVersion + "to " + newVersion + ".");
    }

    public void insertAlarm(SQLiteDatabase db, String time, String days, String alarmName, int soundSet, String soundName,int vibSet, int gameSet, String gameName, String state) {
        db.beginTransaction();
        try{
            String sql = "INSERT INTO " + tableName + "(time, days, alarmName, soundSet, soundName, vibSet, gameSet, gameName, state) VALUES (" +
                    "'" + time + "'" + "," + "'" + days + "'" + "," + "'" + alarmName + "'" + ","
                    + "'" + soundSet + "'" + "," + "'" + soundName + "'" + "," + "'" + vibSet + "'" + "," + "'" + gameSet +"'"+ ","+ "'" + gameName + "'" + "," + "'" + state + "')";
            db.execSQL(sql);
            db.setTransactionSuccessful();
        }
        catch (Exception e){
            e.printStackTrace();
        }
        finally {
            db.endTransaction();
        }
    }
}
