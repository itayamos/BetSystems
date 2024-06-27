package com.ashcollege.games;

import com.ashcollege.Persist;
import com.ashcollege.entities.Club;
import com.ashcollege.entities.Goal;
import com.github.javafaker.Faker;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

public class Game {

    private int id;
    private int round;
    private Club home;
    private Club away;
    private int score_home;
    private List<Goal> home_goals;
    private List<Goal> away_goals;
    private int score_away;
    private String status;
    private int minute;
    public Game(Club home, Club away) {
        this.home = home;
        this.away = away;
        this.home_goals=new ArrayList<>();
        this.away_goals=new ArrayList<>();
    }
    public Game(){

    }
    public void simulateGame() {
        //double players_general_condition_team1=1+(double) (new Random().nextInt(1,5))/5;
        //double players_general_condition_team2=1+(double) (new Random().nextInt(1,5))/5;
        // Calculate a score based on team strengths (replace with your logic)
        //new Thread(()->{
            //this.setStatus("active");
            Faker faker=new Faker();
            double team1Modifier = new Random().nextInt(0,10) * 0.6; // Random factor (optional)
            double team2Modifier = new Random().nextInt(0,10) * 0.4; // Random factor (optional)
            int home_goal=(int) ((this.home.getSkill())/100 * (1 + team1Modifier));
            int away_goal=(int) ((this.away.getSkill())/100 * (1 + team2Modifier));

            this.minute=0;
            /*List<Goal> goalsHome=new ArrayList<>();
            List<Goal> goalsAway=new ArrayList<>();
            for (int i = 0; i < home_goal; i++) {
                goalsHome.add(new Goal(faker.pokemon().name(),new Random().nextInt(0,90)));
            }for (int i = 0; i < away_goal; i++) {
                goalsAway.add(new Goal(faker.pokemon().name(),new Random().nextInt(0,90)));
            }
            while (this.minute<=90){
                List<Goal> goalsByMinute_home=goalsHome.stream().filter((Goal g)->{return this.minute==g.getMinute();}).toList();
                List<Goal> goalsByMinute_away=goalsAway.stream().filter((Goal g)->{return this.minute==g.getMinute();}).toList();
                if (!goalsByMinute_home.isEmpty()&&this.home_goals!=null){
                    for(Goal g:goalsByMinute_home){
                        this.home_goals.add(g);

                    }

                }if (!goalsByMinute_away.isEmpty()&&this.away_goals!=null){
                    for(Goal g:goalsByMinute_away){
                        this.away_goals.add(g);
                    }

                }
                try {
                    Thread.sleep(333);
                    this.minute++;
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }*/
            this.score_home = home_goal;
            this.score_away = away_goal;

        //}).start();


    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Club getHome() {
        return home;
    }

    public void setHome(Club home) {
        this.home = home;
    }

    public Club getAway() {
        return away;
    }

    public void setAway(Club away) {
        this.away = away;
    }

    public int getScore_home() {
        return score_home;
    }

    public void setScore_home(int score_home) {
        this.score_home = score_home;
    }

    public int getScore_away() {
        return score_away;
    }

    public void setScore_away(int score_away) {
        this.score_away = score_away;
    }

    public int getRound() {
        return round;
    }

    public void setRound(int round) {
        this.round = round;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public List<Goal> getHome_goals() {
        return home_goals;
    }

    public void setHome_goals(List<Goal> home_goals) {
        this.home_goals = home_goals;
    }

    public List<Goal> getAway_goals() {
        return away_goals;
    }

    public void setAway_goals(List<Goal> away_goals) {
        this.away_goals = away_goals;
    }

    public int getMinute() {
        return minute;
    }

    public void setMinute(int minute) {
        this.minute = minute;
    }
}


/*public Game simulateGame(Game game) {
        //double players_general_condition_team1=1+(double) (new Random().nextInt(1,5))/5;
        //double players_general_condition_team2=1+(double) (new Random().nextInt(1,5))/5;
        // Calculate a score based on team strengths (replace with your logic)
        //new Thread(()->{
          //  while (!game.getStatus().equals("finished")){
                //game.setStatus("active");
                Faker faker=new Faker();
                double team1Modifier = new Random().nextInt(0,10) * 0.6; // Random factor (optional)
                double team2Modifier = new Random().nextInt(0,10) * 0.4; // Random factor (optional)
                int home_goal=(int) ((game.getHome().getSkill())/100 * (1 + team1Modifier));
                int away_goal=(int) ((game.getAway().getSkill())/100 * (1 + team2Modifier));

                game.setMinute(0);
                /*List<Goal> goalsHome=new ArrayList<>();
                List<Goal> goalsAway=new ArrayList<>();
                for (int i = 0; i < home_goal; i++) {
                    goalsHome.add(new Goal(faker.pokemon().name(),new Random().nextInt(0,90)));
                }for (int i = 0; i < away_goal; i++) {
                    goalsAway.add(new Goal(faker.pokemon().name(),new Random().nextInt(0,90)));
                }
                while (game.getMinute()<90){
                    List<Goal> goalsByMinute_home=goalsHome.stream().filter((Goal g)->{return game.getMinute()==g.getMinute();}).toList();
                    List<Goal> goalsByMinute_away=goalsAway.stream().filter((Goal g)->{return game.getMinute()==g.getMinute();}).toList();
                    if (!goalsByMinute_home.isEmpty()/*&&game.getHome_goals()!=null*///){
//for(Goal g:goalsByMinute_home){
//  game.getHome_goals().add(g);
//}
                /*        game.setScore_home(game.getScore_home()+goalsByMinute_home.size());

                    }if (!goalsByMinute_away.isEmpty()/*&&game.getAway_goals()!=null*///){
//for(Goal g:goalsByMinute_away){
//  game.getAway_goals().add(g);
//}
        /*                game.setScore_away(game.getScore_away()+goalsByMinute_away.size());
                    }
                    persist.save(game);
                    try {
                        Thread.sleep(33);
                        game.setMinute(game.getMinute()+1);
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                }*/
              /*  game.setScore_home(home_goal);
                game.setScore_away(away_goal);
//game.setStatus("finished");
                persist.save(game);
//    break;
//}
//}).start();
        return game;



    }*/
