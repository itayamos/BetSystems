package com.ashcollege.BetService;

import com.ashcollege.Persist;
import com.ashcollege.entities.User;
import com.ashcollege.games.Game;
import org.springframework.beans.factory.annotation.Autowired;

public class Bet {
    private int id;
    private Game game;
    private User user;
    private int money;
    private int guess;
    private boolean result;
    private boolean response;
    private double bonus;


    public Bet(Game game, User user, int money, int guess) {
        this.game = game;
        this.user = user;
        this.money = money;
        this.guess = guess;
    }
    public Bet(){}

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Game getGame() {
        return game;
    }

    public void setGame(Game game) {
        this.game = game;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public int getMoney() {
        return money;
    }

    public void setMoney(int money) {
        this.money = money;
    }

    public int getGuess() {
        return guess;
    }

    public void setGuess(int guess) {
        this.guess = guess;
    }

    public boolean isResult() {
        return result;
    }

    public void setResult(boolean result) {
        this.result = result;
    }

    public boolean isResponse() {
        return response;
    }

    public void setResponse(boolean response) {
        this.response = response;
    }

    public double getBonus() {
        return bonus;
    }

    public void setBonus(double bonus) {
        this.bonus = bonus;
    }

    public int determine(){
        int result=this.getGame().getScore_home()-this.getGame().getScore_away();
        int guess1=0;
        if (result>0){
            guess1=0;
        }if (result==0){
            guess1=1;
        }if (result<0){
            guess1=2;
        }return guess1;
    }
    public boolean canDetermine(){
        return this.getGame().getStatus().equals("finished");
    }
    public boolean canBet(){
        return this.getGame().getStatus().equals("not started")&&this.getMoney()<=this.getUser().getMoney();
    }
    public void placeBet(String secret,int gameId){
        //new Thread(()->{
            //User user1;
            //Game game1;
            //this.setUser(persist.getUserBySecret(secret));
            //this.setGame(persist.getGameById(gameId));
            if (canBet()){
                this.getUser().setMoney(this.getUser().getMoney()-this.getMoney());
                //persist.save(this.getUser());
            }//await&async
            try {
                Thread.sleep(40000);

            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            if(canDetermine()){
                if (determine()==this.getGuess()) {
                    this.getUser().setMoney(this.getUser().getMoney()+2*this.getMoney());
                    //persist.save(this.getUser());
                    this.setResult(true);
                }else {
                    this.setResult(false);
                }
            }
            //persist.save((Bet)this);
        //}).start();
    }



}
