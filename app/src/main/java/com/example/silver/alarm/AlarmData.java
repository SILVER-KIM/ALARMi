package com.example.silver.alarm;


public class AlarmData {
    //리스트 데이터에 들어갈 것들을 선언
    private String time;
    private String date;
    private String name;
    private boolean type;

    public AlarmData(String time, String date, String name, boolean type) {
        this.time = time;
        this.date = date;
        this.name = name;
        this.type = type;
    }

    public boolean getType(){
        return type;
    }

    public void setType(boolean type){
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getTime(){
        return time;
    }

    public void setTime(String time){
        this.time = time;
    }

    public String getDate(){
        return date;
    }

    public void setDate(String date){
        this.date = date;
    }
}