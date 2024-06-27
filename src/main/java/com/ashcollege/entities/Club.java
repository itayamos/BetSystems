package com.ashcollege.entities;

import javax.persistence.CascadeType;
import javax.persistence.OneToMany;
import java.util.ArrayList;
import java.util.List;

public class Club {
    private int id;
    private String name;
    private double skill;
    private int wins;
    private int loses;
    private int draws;
    private int points;
    private int goal_diff;

    public Club(int id,String name, double skill) {
        this(name,skill);
        this.id = id;

    }
    public Club(String name, double skill) {
        this(name);
        this.skill = skill;

    }
    public Club(String name) {
        this.name = name;

    }
    public Club() {
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

    public double getSkill() {
        return skill;
    }

    public void setSkill(double skill) {
        this.skill = skill;
    }



    public int getWins() {
        return wins;
    }

    public void setWins(int wins) {
        this.wins = wins;
    }

    public int getLoses() {
        return loses;
    }

    public void setLoses(int loses) {
        this.loses = loses;
    }

    public int getDraws() {
        return draws;
    }

    public void setDraws(int draws) {
        this.draws = draws;
    }

    public int getPoints() {
        return points;
    }

    public void setPoints(int points) {
        this.points = points;
    }

    public int getGoal_diff() {
        return goal_diff;
    }

    public void setGoal_diff(int goal_diff) {
        this.goal_diff = goal_diff;
    }

    /*private int wins;
    private int loses;
    private int draws;
    private int points;
    private int goal_diff;*/
}
