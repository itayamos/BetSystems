package com.ashcollege.games;

import com.ashcollege.entities.Player;

public class Goal {
    private String player;
    private int minute;

    public Goal(String player, int minute) {
        this.player = player;
        this.minute = minute;
    }

    public String getPlayer() {
        return player;
    }

    public void setPlayer(String player) {
        this.player = player;
    }

    public int getMinute() {
        return minute;
    }

    public void setMinute(int minute) {
        this.minute = minute;
    }
}
