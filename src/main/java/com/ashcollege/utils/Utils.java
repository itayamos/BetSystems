package com.ashcollege.utils;

import com.ashcollege.Persist;
import com.ashcollege.entities.*;
import com.ashcollege.games.Game;
import com.github.javafaker.Faker;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.web.servlet.error.DefaultErrorViewResolver;
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
    @Autowired
    private DefaultErrorViewResolver conventionErrorViewResolver;

    @PostConstruct
    public void init2(){
        new Thread(()->{
            try {
                Thread.sleep(3000);
                List<User> users=persist.loadList(User.class);
                Faker faker=new Faker();
                User userQ=new User("joe",
                        "america","biden@gmail.com",
                        "qwert"
                );
                userQ.setMoney(1000);
                persist.save(userQ);
                if (users.size()<50){
                    for (int i = 0; i < 20; i++) {

                        User user=new User(faker.name().firstName(),
                                faker.lorem().word(),faker.animal().name()+"@gmail.com",
                                faker.random().hex()
                        );
                        user.setMoney(1000);
                        persist.save(user);
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

                    List<Double> skillAvgs=new ArrayList<>();
                    for (int i = 1; i <= 10; i++) {
                        double skillAvg=0;
                        for (int j = 0; j < 11; j++) {
                            int skill=new Random().nextInt(55,80);
                            EPlayer ePlayer=new EPlayer(faker.funnyName().name(),0,persist.getClubById(i),skill);
                            skillAvg+=skill;
                            persist.save(ePlayer);
                        }
                        skillAvgs.add(skillAvg/11);
                    }
                    for (int i = 0; i < 10; i++) {


                        double skill=skillAvgs.get(i);
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

                double home_advantage=new Random().nextDouble(0.5,0.65);
                double away_disadvantage=1-home_advantage;
                double weatherFactor = new Random().nextDouble(0.9,1.1); // 0.9 to 1.1
                double injuryFactor = new Random().nextDouble(0.8,1.0); // 0.8 to 1.0
                double moraleFactor_home = new Random().nextDouble(0.85,1.15); // 0.85 to 1.25
                double moraleFactor_away = new Random().nextDouble(0.85,1.15); // 0.85 to 1.25
                double team1Modifier = new Random().nextInt(0,10) * home_advantage; // Random factor (optional)
                double team2Modifier = new Random().nextInt(0,10) * away_disadvantage; // Random factor (optional)
                int home_goal=(int) ((game.getHome().getSkill())/100 * (1 + team1Modifier)*moraleFactor_home*weatherFactor);
                int away_goal=(int) ((game.getAway().getSkill())/100 * (1 + team2Modifier)*moraleFactor_away*weatherFactor);


                game.setMinute(0);
                List<EPlayer> ePlayersHome=persist.getEPlayersClubById(game.getHome().getId());
                List <EPlayer> ePlayersAway=persist.getEPlayersClubById(game.getAway().getId());
                List<Goal> goalsHome=new ArrayList<>();
                List<Goal> goalsAway=new ArrayList<>();
                for (int i = 0; i < home_goal; i++) {
                    goalsHome.add(new Goal(game,game.getHome(),"home",new Random().nextInt(0,90)));
                }for (int i = 0; i < away_goal; i++) {
                    goalsAway.add(new Goal(game,game.getAway(),"away",new Random().nextInt(0,90)));
                }
                while (game.getMinute()<90){
                    List<Goal> goalsByMinute_home=goalsHome.stream().filter((Goal g)->{return game.getMinute()==g.getMinute();}).toList();
                    List<Goal> goalsByMinute_away=goalsAway.stream().filter((Goal g)->{return game.getMinute()==g.getMinute();}).toList();
                    if (!goalsByMinute_home.isEmpty()/*&&game.getHome_goals()!=null*/){
                        //for(Goal g:goalsByMinute_home){
                        //  game.getHome_goals().add(g);
                        //}
                        game.setScore_home(game.getScore_home()+goalsByMinute_home.size());
                        for (int i = 0; i < goalsByMinute_home.size(); i++) {
                            EPlayer ePlayer=ePlayersHome.get(new Random().nextInt(0,ePlayersHome.size()));
                            ePlayer.setGoalAmount(ePlayer.getGoalAmount()+1);
                            ePlayer.setSkill(ePlayer.getSkill()+1);
                            persist.save(ePlayer);
                            goalsByMinute_home.get(i).setePlayer(ePlayer);
                            persist.save(goalsByMinute_home.get(i));
                        }

                    }if (!goalsByMinute_away.isEmpty()/*&&game.getAway_goals()!=null*/){
                        //for(Goal g:goalsByMinute_away){
                        //  game.getAway_goals().add(g);
                        //}
                        game.setScore_away(game.getScore_away()+goalsByMinute_away.size());
                        for (int i = 0; i < goalsByMinute_away.size(); i++) {
                            EPlayer ePlayer=ePlayersAway.get(new Random().nextInt(0,ePlayersAway.size()));
                            ePlayer.setGoalAmount(ePlayer.getGoalAmount()+1);
                            ePlayer.setSkill(ePlayer.getSkill()+1);
                            persist.save(ePlayer);
                            goalsByMinute_away.get(i).setePlayer(ePlayer);
                            persist.save(goalsByMinute_away.get(i));
                        }

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
                System.out.println("here we gooooo! ^%$#");
                if(clubs.size()==10&&games.size()<90){
                    for (int j = 1; j < 10; j++) {
                        for (int i = 0; i < 10; i++) {
                            Game game=new Game(clubs.get(i),clubs.get((i+j)%clubs.size()));game.setRound(j);game.setStatus("not started");
                            persist.save(game);
                            Prediction prediction=new Prediction(game);
                            double skillSum=game.getHome().getSkill()+game.getAway().getSkill();
                            int home_win_away_loss=game.getHome().getWins();//+game.getAway().getLoses();
                            int away_win_home_loss=/*game.getHome().getLoses()+*/game.getAway().getWins();
                            skillSum+=home_win_away_loss+away_win_home_loss;
                            int homeWin_pred=(int)((game.getHome().getSkill()*100)/skillSum);
                            int awayWin_pred=(int)((game.getAway().getSkill()*100)/skillSum);
                            int sumDraws=game.getHome().getDraws()+game.getAway().getDraws();
                            int draw_div_2=new Random().nextInt(5+sumDraws,13+sumDraws)+sumDraws;
                            int draw_pred=draw_div_2*2+1;
                            homeWin_pred-=draw_div_2;
                            awayWin_pred-= draw_div_2;
                            prediction.setHomeWin(homeWin_pred);
                            prediction.setAwayWin(awayWin_pred);
                            prediction.setDraw((draw_pred));
                            int max=Math.max(draw_pred,Math.max(homeWin_pred,away_win_home_loss));

                            if (homeWin_pred>awayWin_pred && homeWin_pred>draw_pred){
                                prediction.setPred(0);
                            }else if(awayWin_pred>homeWin_pred && awayWin_pred>draw_pred){
                                prediction.setPred(2);
                            }else if (draw_pred>homeWin_pred && draw_pred>awayWin_pred){
                                prediction.setPred(1);
                            }else if (homeWin_pred==draw_pred){
                                prediction.setPred(1);
                            }else if (awayWin_pred==draw_pred){
                                prediction.setPred(1);
                            }else if (homeWin_pred==awayWin_pred){
                                prediction.setPred(2);
                            }

                            persist.save(prediction);
                        }
                        Thread.sleep(30*1000);
                        //for (int i = 0; i < 10; i++) {}
                        //Thread.sleep(333*90);
                        for (int i = 0; i < 10; i++) {
                            Game game=persist.getGameById(10*(j-1)+i+1);
                            simulateGame(game);
                            game.setStatus("finished");
                            persist.save(game);


                            Game game_post_match=persist.getGameById(10*(j-1)+i+1);
                            double homeSkill=game_post_match.getHome().getSkill();
                            double awaySkill=game_post_match.getAway().getSkill();
                            if (game_post_match.getScore_home()>game_post_match.getScore_away()){
                                List<EPlayer> ePlayersHome=persist.getEPlayersClubById(game_post_match.getHome().getId());
                                List <EPlayer> ePlayersAway=persist.getEPlayersClubById(game_post_match.getAway().getId());
                                for (int k = 0; k < 11; k++) {
                                    EPlayer eph=ePlayersHome.get(k);
                                    EPlayer epa=ePlayersAway.get(k);
                                    eph.setSkill(eph.getSkill()+3);
                                    epa.setSkill(epa.getSkill()-1);
                                    persist.save(eph);
                                    persist.save(epa);
                                    homeSkill+= eph.getSkill();
                                    awaySkill+=epa.getSkill();

                                }
                                System.out.println("home win");
                                game_post_match.getHome().setWins(game_post_match.getHome().getWins()+1);
                                game_post_match.getAway().setLoses(game_post_match.getAway().getLoses()+1);

                                game_post_match.getHome().setPoints(game_post_match.getHome().getPoints()+3);

                                //persist.save(games.get(i).getHome());persist.save(games.get(i).getAway());
                            }else if (game_post_match.getScore_home()<game_post_match.getScore_away()){

                                List<EPlayer> ePlayersHome=persist.getEPlayersClubById(game_post_match.getHome().getId());
                                List <EPlayer> ePlayersAway=persist.getEPlayersClubById(game_post_match.getAway().getId());

                                for (int k = 0; k < 11; k++) {
                                    EPlayer eph=ePlayersHome.get(k);
                                    EPlayer epa=ePlayersAway.get(k);
                                    eph.setSkill(eph.getSkill()-1);
                                    epa.setSkill(epa.getSkill()+3);
                                    persist.save(eph);
                                    persist.save(epa);
                                    homeSkill+= eph.getSkill();
                                    awaySkill+=epa.getSkill();
                                }
                                System.out.println("home loss");
                                game_post_match.getHome().setLoses(game_post_match.getHome().getLoses()+1);
                                game_post_match.getAway().setWins(game_post_match.getAway().getWins()+1);

                                game_post_match.getAway().setPoints(game_post_match.getAway().getPoints()+3);

                                //persist.save(games.get(i).getHome());persist.save(games.get(i).getAway());
                            }else if(game_post_match.getScore_home()==game_post_match.getScore_away()){
                                game_post_match.getHome().setDraws(game_post_match.getHome().getDraws()+1);
                                game_post_match.getAway().setDraws(game_post_match.getAway().getDraws()+1);

                                game_post_match.getHome().setPoints(game_post_match.getHome().getPoints()+1);
                                game_post_match.getAway().setPoints(game_post_match.getAway().getPoints()+1);

                            }

                            Prediction prediction=persist.getPredictionByGameId(game_post_match.getId());
                            int diff=game_post_match.getScore_home()-game_post_match.getScore_away();
                            boolean correctPred=(diff>0&&prediction.getPred()==0)
                                    ||(diff==0&&prediction.getPred()==1)
                                    ||(diff<0&&prediction.getPred()==2);
                            prediction.setCorrect(correctPred);
                            persist.save(prediction);
                            game_post_match.getHome().setGoal_diff(game_post_match.getHome().getGoal_diff()+diff);
                            game_post_match.getAway().setGoal_diff(game_post_match.getAway().getGoal_diff()-diff);

                            if(diff!=0){
                                homeSkill-=game_post_match.getHome().getSkill();
                                awaySkill-=game_post_match.getAway().getSkill();
                                game_post_match.getHome().setSkill(homeSkill/11);
                                game_post_match.getAway().setSkill(awaySkill/11);
                            }
                            persist.save(game_post_match.getHome());persist.save(game_post_match.getAway());

                        }
                        //Thread.sleep(1000);
                        //for (int i = 0; i < 10; i++) {
                          //Thread.sleep(1000);
                        //}
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



