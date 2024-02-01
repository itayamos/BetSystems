package com.ashcollege.entities;

import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

public class Player {
    private int id;
    private String name;
    private int attack;
    private int defence;
    //@ManyToOne(fetch = FetchType.LAZY )
    //@JoinColumn(name = "club_id")
    private int club;



    public Player(String name, int attack, int defence, int club) {
        this.name = name;
        this.attack = attack;
        this.defence = defence;
        this.club = club;
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

    public int getAttack() {
        return attack;
    }

    public void setAttack(int attack) {
        this.attack = attack;
    }

    public int getDefence() {
        return defence;
    }

    public void setDefence(int defence) {
        this.defence = defence;
    }

    public int getClub() {
        return club;
    }

    public void setClub(int club) {
        this.club = club;
    }
}
