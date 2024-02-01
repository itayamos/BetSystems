package com.ashcollege.utils;

import com.ashcollege.Persist;
import com.ashcollege.entities.*;
import com.ashcollege.games.Game;
import com.ashcollege.games.Goal;
import com.github.javafaker.Faker;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;

@Component
public class Utils {
    @Autowired
    private Persist persist;
    @PostConstruct
    public void init2(){
        new Thread(()->{
            try {
                Thread.sleep(3000);
                List<User> users=persist.loadList(User.class);
                Faker faker=new Faker();
                if (users.size()<50){
                    for (int i = 0; i < 20; i++) {

                        User user=new User(faker.name().firstName(),
                                faker.lorem().word(),
                                faker.random().hex()
                        );
                        user.setMoney(1000);
                        persist.save(user);
                    }
                    Random random=new Random();
                    for (int i = 0; i < 10; i++) {
                        StringBuilder stringBuilder=new StringBuilder();
                        for (int j = 0; j < 5; j++) {
                            stringBuilder.append(faker.lorem().word()).append(" ");
                        }
                        Note note=new Note();
                        note.setContent(stringBuilder.toString());
                        note.setDate(new Date());
                        int randomId=random.nextInt(1,5);
                        User user=new User();
                        user.setId(randomId);
                        note.setOwner(user);

                        persist.save(note);

                    }
                }
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }).start();

    }
    /*@PostConstruct
    public void init2(){
        new Thread(()->{
            try {
                Thread.sleep(3000);
                List<Club> clubs=persist.loadList(Club.class);
                Faker faker=new Faker();
                //if (clubs.size()<10){
                    double skill=0;List<Player> players=new ArrayList<>();Club club=new Club(faker.address().city()+" fc",skill/11);

                    for (int j = 0; j < 11; j++) {
                        Player player=new Player(faker.name().lastName(),new Random().nextInt(40,80),new Random().nextInt(40,80));
                        Club club1=new Club();
                        club1.setId(1);
                        player.setClub(club1);
                        persist.save(player);
                        players.add(player);
                        skill+=player.getAttack()*0.6+ player.getDefence()*0.4;
                    }club.setSkill(skill/11);persist.save(club);;




                    //for (int i = 0; i < 10; i++) {

                    //}

                //}
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }).start();

    }*/













    @PostConstruct
    public void init3(){
        new Thread(()->{
            try {
                Thread.sleep(1000);
                List<Club> clubList=persist.loadList(Club.class);
                Faker faker=new Faker();
                if (clubList.size()<10){

                    List<Club> clubs=new ArrayList<>();
                    Random random=new Random();
                    for (int i = 0; i < 10; i++) {
                        Club club=new Club(faker.pokemon().name(),random.nextInt(60,80));
                        clubs.add(club);
                        persist.save(club);
                    }
                    //List<Player> players;
                    for (int i = 0; i < 10; i++) {

                        double skill=new Random().nextInt(55,75);
                        /*for (int j = 0; j < 11; j++) {
                            player=new Player(faker.name().lastName(),new Random().nextInt(40,80),new Random().nextInt(40,80));
                            persist.save(player);
                            int randomId=random.nextInt(1,10);
                            //player.setClub(clubs.get(i));
                            skill+=(player.getAttack()*0.6+ player.getDefence()*0.4)/11;

                        }*/


                        clubs.get(i).setSkill(skill);
                        persist.save(clubs.get(i));
                    }

                }
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }).start();}




    public Game simulateGame(Game game) {
        //double players_general_condition_team1=1+(double) (new Random().nextInt(1,5))/5;
        //double players_general_condition_team2=1+(double) (new Random().nextInt(1,5))/5;
        // Calculate a score based on team strengths (replace with your logic)
        //new Thread(()->{
          //  while (!game.getStatus().equals("finished")){
                game.setStatus("active");
                Faker faker=new Faker();
                double team1Modifier = new Random().nextInt(0,10) * 0.6; // Random factor (optional)
                double team2Modifier = new Random().nextInt(0,10) * 0.4; // Random factor (optional)
                int home_goal=(int) ((game.getHome().getSkill())/100 * (1 + team1Modifier));
                int away_goal=(int) ((game.getAway().getSkill())/100 * (1 + team2Modifier));

                game.setMinute(0);
                List<Goal> goalsHome=new ArrayList<>();
                List<Goal> goalsAway=new ArrayList<>();
                for (int i = 0; i < home_goal; i++) {
                    goalsHome.add(new Goal(faker.pokemon().name(),new Random().nextInt(0,90)));
                }for (int i = 0; i < away_goal; i++) {
                    goalsAway.add(new Goal(faker.pokemon().name(),new Random().nextInt(0,90)));
                }
                while (game.getMinute()<90){
                    List<Goal> goalsByMinute_home=goalsHome.stream().filter((Goal g)->{return game.getMinute()==g.getMinute();}).toList();
                    List<Goal> goalsByMinute_away=goalsAway.stream().filter((Goal g)->{return game.getMinute()==g.getMinute();}).toList();
                    if (!goalsByMinute_home.isEmpty()/*&&game.getHome_goals()!=null*/){
                        //for(Goal g:goalsByMinute_home){
                        //  game.getHome_goals().add(g);
                        //}
                        game.setScore_home(game.getScore_home()+goalsByMinute_home.size());

                    }if (!goalsByMinute_away.isEmpty()/*&&game.getAway_goals()!=null*/){
                        //for(Goal g:goalsByMinute_away){
                        //  game.getAway_goals().add(g);
                        //}
                        game.setScore_away(game.getScore_away()+goalsByMinute_away.size());
                    }
                    persist.save(game);
                    try {
                        Thread.sleep(333);
                        game.setMinute(game.getMinute()+1);
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                }
                game.setScore_home(home_goal);
                game.setScore_away(away_goal);
                //game.setStatus("finished");
                //persist.save(game);
            //    break;
            //}
        //}).start();
        return game;



    }

    @PostConstruct
    public void init4(){
        new Thread(()->{
            try {
                Thread.sleep(5000);
                List<Club> clubs=persist.loadList(Club.class);
                List<Game> games=persist.loadList(Game.class);
                if(clubs.size()==10&&games.size()<90){
                    for (int j = 1; j < 10; j++) {
                        for (int i = 0; i < 10; i++) {
                            Game game=new Game(clubs.get(i),clubs.get((i+j)%clubs.size()));game.setRound(j);game.setStatus("not started");
                            persist.save(game);
                        }
                        //for (int i = 0; i < 10; i++) {}
                        for (int i = 0; i < 10; i++) {
                            Game game=persist.getGameById(10*(j-1)+i+1);
                            simulateGame(game);
                            game.setStatus("finished");
                            persist.save(game);


                        }
                        //Thread.sleep(1000);
                        for (int i = 0; i < 10; i++) {
                            Game game=persist.getGameById(10*(j-1)+i+1);
                            if (game.getScore_home()>game.getScore_away()){
                                game.getHome().setSkill(game.getHome().getSkill()+3);
                                game.getAway().setSkill(game.getAway().getSkill()-1);

                                game.getHome().setWins(game.getHome().getWins()+1);
                                game.getAway().setLoses(game.getAway().getLoses()+1);

                                game.getHome().setPoints(game.getHome().getPoints()+3);

                                //persist.save(games.get(i).getHome());persist.save(games.get(i).getAway());
                            }else if (game.getScore_home()<game.getScore_away()){
                                game.getHome().setSkill(game.getHome().getSkill()-1);
                                game.getAway().setSkill(game.getAway().getSkill()+3);

                                game.getHome().setLoses(game.getHome().getLoses()+1);
                                game.getAway().setWins(game.getAway().getWins()+1);

                                game.getAway().setPoints(game.getAway().getPoints()+3);

                                //persist.save(games.get(i).getHome());persist.save(games.get(i).getAway());
                            }else if(game.getScore_home()==game.getScore_away()){
                                game.getHome().setDraws(game.getHome().getDraws()+1);
                                game.getAway().setDraws(game.getAway().getDraws()+1);

                                game.getHome().setPoints(game.getHome().getPoints()+1);
                                game.getAway().setPoints(game.getAway().getPoints()+1);

                            }
                            int diff=game.getScore_home()-game.getScore_away();

                            game.getHome().setGoal_diff(game.getHome().getGoal_diff()+diff);
                            game.getAway().setGoal_diff(game.getAway().getGoal_diff()-diff);

                            persist.save(game.getHome());persist.save(game.getAway());
                            //Thread.sleep(1000);
                        }
                    }

                }
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }).start();}

    /*@PostConstruct
    public void init5(){
        new Thread(()->{

        }).start();}*/
}



