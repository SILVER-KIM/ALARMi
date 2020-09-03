package com.example.silver.alarm;

import android.graphics.drawable.Drawable;

public class GameListItem {
    private Boolean gameChecked;
    private String gameName;
    private Drawable gameInfo;

    public Boolean getGameChecked() {
        return gameChecked;
    }

    public void setGameChecked(Boolean gameChecked) {
        this.gameChecked = gameChecked;
    }

    public String getGameName() {
        return gameName;
    }

    public void setGameName(String gameName) {
        this.gameName = gameName;
    }

    public Drawable getGameInfo() {
        return gameInfo;
    }

    public void setGameInfo(Drawable gameInfo) {
        this.gameInfo = gameInfo;
    }
}
