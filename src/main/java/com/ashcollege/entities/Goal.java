package com.ashcollege.entities;

import com.ashcollege.games.Game;

public class Goal {
    private int id;
    private EPlayer ePlayer;
    private Game game;
    private Club club;
    private String affiliation;
    private int minute;

    public Goal(EPlayer ePlayer, Game game, Club club, String affiliation, int minute) {

        this.ePlayer = ePlayer;
        this.game = game;
        this.club = club;
        this.affiliation = affiliation;
        this.minute = minute;
    }
    public Goal( Game game, Club club, String affiliation, int minute) {
        this.game = game;
        this.club = club;
        this.affiliation = affiliation;
        this.minute = minute;
    }

    public Goal() {

    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public EPlayer getePlayer() {
        return ePlayer;
    }

    public void setePlayer(EPlayer ePlayer) {
        this.ePlayer = ePlayer;
    }

    public Game getGame() {
        return game;
    }

    public void setGame(Game game) {
        this.game = game;
    }

    public Club getClub() {
        return club;
    }

    public void setClub(Club club) {
        this.club = club;
    }

    public String getAffiliation() {
        return affiliation;
    }

    public void setAffiliation(String affiliation) {
        this.affiliation = affiliation;
    }

    public int getMinute() {
        return minute;
    }

    public void setMinute(int minute) {
        this.minute = minute;
    }
}
