package com.ashcollege.entities;

public class EPlayer {
    private int id;
    private String name;
    private int goalAmount;
    private Club club;
    private int skill;
    public EPlayer() {

    }public EPlayer(String name, int goalAmount, Club club, int skill) {
        this.name = name;
        this.goalAmount = goalAmount;
        this.club = club;
        this.skill = skill;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getGoalAmount() {
        return goalAmount;
    }

    public void setGoalAmount(int goalAmount) {
        this.goalAmount = goalAmount;
    }

    public Club getClub() {
        return club;
    }

    public void setClub(Club club) {
        this.club = club;
    }

    public int getSkill() {
        return skill;
    }

    public void setSkill(int skill) {
        this.skill = skill;
    }


}
