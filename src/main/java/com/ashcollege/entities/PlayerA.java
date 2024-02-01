package com.ashcollege.entities;

public class PlayerA {
    private int id;
    private String name;
    private int attack;
    private int defence;
    private Club club;

    public PlayerA(String name, int attack, int defence) {
        this.name = name;
        this.attack = attack;
        this.defence = defence;
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

    public Club getClub() {
        return club;
    }

    public void setClub(Club club) {
        this.club = club;
    }

    public void t(){
        setDefence(getDefence());setAttack(getAttack());

    }
}
