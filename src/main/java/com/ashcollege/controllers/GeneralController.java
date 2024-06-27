package com.ashcollege.controllers;

import com.ashcollege.BetService.Bet;
import com.ashcollege.Persist;
import com.ashcollege.entities.*;
import com.ashcollege.games.Game;
import com.ashcollege.responses.*;
import com.ashcollege.utils.DbUtils;
import com.ashcollege.utils.Errors;
import com.github.javafaker.Faker;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.Collections;
import java.util.Optional;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;


import static com.ashcollege.utils.Errors.*;

@RestController
public class GeneralController {

    @Autowired
    private DbUtils dbUtils;

    @Autowired
    private Persist persist;

    private ScheduledExecutorService service = Executors.newSingleThreadScheduledExecutor();



    private List<EventClient> clients=new ArrayList<>();
    private List<SseEmitter> sseEmitters=new ArrayList<>();

    @PostConstruct
    public void initBet(){
        new Thread(()->{
            while(true){
                try {
                    Thread.sleep(30*1000);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
                List<Bet> bets=persist.loadList(Bet.class).stream()
                        .filter(bet -> !bet.isResponse()) // Filter bets where response is false
                        .collect(Collectors.toList());
                if(!(bets.isEmpty())){
                    for (Bet bet:bets){
                        pendingBet(bet.getId());
                    }
                }

            }
        }).start();
    }
    /*@PostConstruct
    public void init(){

    }*/
    /*@PostConstruct
    public void notStarted(){
        new Thread(()->{
            while(true){
                try {
                    Thread.sleep(333);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }

                for(EventClient client:clients){
                    try {
                        if (client.getSecret().equals("1")){
                            JSONObject object=new JSONObject();
                            object.put("teamA", System.currentTimeMillis()+"__"+3);
                            client.getSseEmitter().send(object.toString());
                        }else {
                            JSONObject object=new JSONObject();
                            JSONArray jsonArray = new JSONArray();
                            object.put("teamA", System.currentTimeMillis()+"__"+4);
                            List<Game> upcoming_games=persist.getGamesByStatus("not started");
                            if(upcoming_games!=null&&!upcoming_games.isEmpty()){
                                List<Integer> gameIDs=upcoming_games.stream().map((Game g)->{return g.getId();}).collect(Collectors.toList());
                                List<String> gameNames=upcoming_games.stream().map((Game g)->{return g.getHome().getName()+" - "+g.getAway().getName();}).collect(Collectors.toList());
                                for (int i = 0; i < upcoming_games.size(); i++) {
                                    JSONObject teamJsonObject = new JSONObject();
                                    teamJsonObject.put("game_name",gameNames.get(i)).put("game_id",gameIDs.get(i));
                                    jsonArray.put(teamJsonObject);
                                }
                                object.put("games",jsonArray);
                                client.getSseEmitter().send(object.toString());
                            }
                        }

                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                }

            }
        }).start();
    }*/
    /*@PostConstruct
    public void active(){
        new Thread(()->{
            while(true){
                try {
                    Thread.sleep(333);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }

                for(EventClient client:clients){
                    try {
                        if (client.getSecret().equals("1")){
                            JSONObject object=new JSONObject();
                            object.put("teamA", System.currentTimeMillis()+"__"+3);
                            client.getSseEmitter().send(object.toString());
                        }else {
                            JSONObject object=new JSONObject();
                            JSONArray home_goals = new JSONArray();
                            JSONArray away_goals = new JSONArray();
                            List<Game> active_game=persist.getGamesByStatus("active");
                            if (active_game != null&&!active_game.isEmpty()) {
                                Game game=active_game.get(0);
                                if(game!=null){
                                    object
                                            .put("home",game.getHome().getName())
                                            .put("home_score",game.getScore_home())
                                            .put("away_score",game.getScore_away())
                                            .put("away",game.getAway().getName())
                                            .put("minute",game.getMinute());

                                    List<Goal> homeGoals=persist.getGameGoalsBySide(game.getId(),"home");
                                    List<Goal> awayGoals=persist.getGameGoalsBySide(game.getId(),"away");
                                    if (homeGoals!=null&&!homeGoals.isEmpty()){
                                        for (int i = 0; i < homeGoals.size(); i++) {
                                            JSONObject homeJsonObject = new JSONObject();
                                            homeJsonObject.put("minute",homeGoals.get(i).getMinute())
                                                    .put("side",homeGoals.get(i).getAffiliation())
                                                    .put("scorer",homeGoals.get(i).getePlayer().getName());
                                            home_goals.put(homeJsonObject);
                                        }object.put("home_goals",home_goals);
                                    }

                                    if (awayGoals!=null&&!awayGoals.isEmpty()){
                                        for (int i = 0; i < awayGoals.size(); i++) {
                                            JSONObject awayJsonObject = new JSONObject();
                                            awayJsonObject.put("minute",awayGoals.get(i).getMinute())
                                                    .put("side",awayGoals.get(i).getAffiliation())
                                                    .put("scorer",awayGoals.get(i).getePlayer().getName());
                                            away_goals.put(awayJsonObject);
                                        }object.put("away_goals",away_goals);
                                    }


                                    client.getSseEmitter().send(object.toString());
                                }
                            }
                        }

                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                }

            }
        }).start();
    }*/
    //4444444444444444444444444444444444444444444444444444444444444

    @RequestMapping(value = "start-streaming", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter createStreamingSession(String secret) {
        try {
            SseEmitter sseEmitter = new SseEmitter(Long.MAX_VALUE);
            EventClient eventClient = new EventClient(sseEmitter, secret);
            eventClient.setConnected(true);
            clients.add(eventClient);
            return sseEmitter;
        } catch (Exception e) {
            // Handle exception gracefully (e.g., log the error)
            System.err.println("Error creating streaming session:"+ e);
            return null; // Indicate an error occurred (optional)
        }
    }

    @PostConstruct
    public void activate() {
        new Thread(() -> {
            while (true) {
                try {
                    Thread.sleep(333);
                } catch (InterruptedException e) {
                    // Handle interruption gracefully (optional)
                    break; // Consider stopping the thread or logging the interruption
                }

                for (EventClient client : clients.stream().filter(EventClient::isConnected).toList()) {
                    try {
                        if (client.getSecret().equals("1")) {
                            JSONObject object = new JSONObject();
                            object.put("teamA", System.currentTimeMillis() + "__" + 3);
                            client.getSseEmitter().send(object.toString());
                        } else {
                            JSONObject object = new JSONObject();
                            JSONArray home_goals = new JSONArray();
                            JSONArray away_goals = new JSONArray();
                            try {
                                object.put("money",persist.getMoneyBySecret("qwert"));
                            }catch (NullPointerException e){
                                e.printStackTrace();
                            }


                            // Retrieve active games with null check
                            List<Game> activeGames = persist.getGamesByStatus("active");
                            Game game = activeGames != null && !activeGames.isEmpty() ? activeGames.get(0) : null;

                            if (game != null) {
                                object
                                        .put("home", game.getHome().getName())
                                        .put("home_score", game.getScore_home())
                                        .put("away_score", game.getScore_away())
                                        .put("away", game.getAway().getName())
                                        .put("minute", game.getMinute());

                                // Retrieve and handle potential null lists for goals (using Optional)
                                Optional<List<Goal>> optionalHomeGoals = Optional.ofNullable(persist.getGameGoalsBySide(game.getId(), "home"));
                                List<Goal> homeGoals = optionalHomeGoals.orElse(Collections.emptyList());

                                Optional<List<Goal>> optionalAwayGoals = Optional.ofNullable(persist.getGameGoalsBySide(game.getId(), "away"));
                                List<Goal> awayGoals = optionalAwayGoals.orElse(Collections.emptyList());

                                // Process home and away goals with null checks
                                for (Goal goal : homeGoals) {
                                    JSONObject homeJsonObject = new JSONObject();
                                    homeJsonObject.put("minute", goal.getMinute())
                                            .put("side", goal.getAffiliation())
                                            .put("scorer", goal.getePlayer().getName());
                                    home_goals.put(homeJsonObject);
                                }
                                object.put("home_goals", home_goals);

                                for (Goal goal : awayGoals) {
                                    JSONObject awayJsonObject = new JSONObject();
                                    awayJsonObject.put("minute", goal.getMinute())
                                            .put("side", goal.getAffiliation())
                                            .put("scorer", goal.getePlayer().getName());
                                    away_goals.put(awayJsonObject);
                                }
                                object.put("away_goals", away_goals);

                                client.getSseEmitter().send(object.toString());
                            }
                        }
                    } catch (IOException e) {
                        // Handle IOException gracefully (log the error)
                        System.err.println("Error sending data:"+ e);
                        // Consider removing the client if there's a persistent issue
                    }
                }
            }
        }).start();
        Thread.onSpinWait();
    }


    //4444444444444444444444444444444444444444444444444444444444444


    @RequestMapping(value = "/", method = {RequestMethod.GET, RequestMethod.POST})
    public Object test () {
        return "Hello From Server";
    }

    @RequestMapping (value = "/login", method = {RequestMethod.GET, RequestMethod.POST})
    public BasicResponse login (String email, String password) {
        BasicResponse basicResponse = null;
        boolean success = false;
        Integer errorCode = null;
        if (email != null && email.length() > 0) {
            if (password != null && password.length() > 0) {
                User user = persist.login(email, password);
                if (user != null) {
                    basicResponse = new LoginResponse(true, errorCode, user);
                } else {
                    errorCode = ERROR_LOGIN_WRONG_CREDS;
                }
            } else {
                errorCode = ERROR_SIGN_UP_NO_PASSWORD;
            }
        } else {
            errorCode = ERROR_NO_SIGN_UP_EMAIL;
        }
        if (errorCode != null) {
            basicResponse = new BasicResponse(success, errorCode);
        }
        return basicResponse;
    }

    @RequestMapping (value = "/change-email", method = {RequestMethod.GET, RequestMethod.POST})
    public BasicResponse changeEmail (String secret, String email) {
        BasicResponse basicResponse = null;
        boolean success = false;
        Integer errorCode = null;
        String emailRegex = "^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$";
        Pattern pattern_email = Pattern.compile(emailRegex);
        Matcher matcher_email = pattern_email.matcher(email);
        if (email != null && email.length() > 0 &&matcher_email.matches()) {
            User user = persist.getUserBySecret(secret);
            if (user != null) {
                if(persist.getUserByEmail(email).isEmpty()){
                    user.setEmail(email);
                    persist.save(user);
                    basicResponse = new UserDetailsResponse(true, errorCode, user);
                }else {
                    errorCode=EMAIL_ALREADY_EXISTS;
                }
            } else {
                errorCode = ERROR_LOGIN_WRONG_CREDS;
            }
        } else {
            errorCode = ERROR_NO_SIGN_UP_EMAIL;
        }
        if (errorCode != null) {
            basicResponse = new BasicResponse(success, errorCode);
        }
        return basicResponse;
    }

    @RequestMapping (value = "/change-username", method = {RequestMethod.GET, RequestMethod.POST})
    public BasicResponse changeUsername (String secret, String username) {
        BasicResponse basicResponse = null;
        boolean success = false;
        Integer errorCode = null;
        if (username != null && username.length() > 0) {
            User user = persist.getUserBySecret(secret);
            if (user != null) {
                if(persist.getUserByUserName(username).isEmpty()){
                    user.setUsername(username);
                    persist.save(user);
                    basicResponse = new UserDetailsResponse(true, errorCode, user);
                }else {
                    errorCode=EMAIL_ALREADY_EXISTS;
                }
            } else {
                errorCode = ERROR_LOGIN_WRONG_CREDS;
            }
        } else {
            errorCode = ERROR_NO_SUCH_USERNAME;
        }
        if (errorCode != null) {
            basicResponse = new BasicResponse(success, errorCode);
        }
        return basicResponse;
    }



    @RequestMapping (value = "add-user")
    public boolean addUser (String username, String password) {
        User userToAdd = new User(username, password);
        return dbUtils.addUser(userToAdd);
    }
    @RequestMapping (value = "signup")
    public BasicResponse signup (String username, String password,String email) {
        boolean success=false;
        Integer error=null;
        User user=null;
        BasicResponse basicResponse=null;
        String emailRegex = "^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$";
        Pattern pattern_email = Pattern.compile(emailRegex);
        Matcher matcher_email = pattern_email.matcher(email);
        String passwordRegex = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=!])(?=\\S+$).{8,}$";
        Pattern pattern_password = Pattern.compile(passwordRegex);
        Matcher matcher_password = pattern_password.matcher(password);
        if (username!=null && username.length()>0){
          if (email!=null){
              if (matcher_email.matches()){
                  if (password!=null){
                      if(matcher_password.matches()){
                          if (persist.EmailNotExists(email)){
                              if(persist.UserNameNotExists(username)){
                                  user=persist.signup(username,password,email);

                                  success=true;
                              }else {
                                  error= ERROR_SIGN_UP_USERNAME_TAKEN;
                              }
                          }else {
                              error= EMAIL_ALREADY_EXISTS;
                          }
                      }else {
                          error=PASSWORD_NOT_MATCHES_PATTERN;
                      }
                  } else {
                      error=ERROR_SIGN_UP_NO_PASSWORD;
                  }
              }else {
                  error=EMAIL_NOT_MATCHES_PATTERN;
              }
          }else {
              error=ERROR_NO_SIGN_UP_EMAIL;
          }
        }else {
            error=ERROR_SIGN_UP_NO_USERNAME;
        }
        if (user != null&&error==null) {
            basicResponse=new LoginResponse(success,error,user);
        }else {
            basicResponse=new BasicResponse(success,error);
        }
        return basicResponse;
        //return persist.signup(username, password,email);
    }


    @RequestMapping (value = "get-users")
    public List<User> getUsers () {
        return dbUtils.getAllUsers();
    }



    /*@RequestMapping (value = "get-notes")
    public BasicResponse getNotes(String name){
        boolean success=false;
        Integer errorCode=null;
        BasicResponse basicResponse=null;
        if(name!=null){
            List<Note> notes=persist.getNotes(name);
            basicResponse=new NotesResponse(success,errorCode,notes);

        }else {
            errorCode=ERROR_SECRET_WAS_NOT_SENT;
        }
        if (errorCode!=null){
            basicResponse=new BasicResponse(false,errorCode);
        }

        return basicResponse;
    }*/
    @RequestMapping (value = "get-clubs")
    public List<Club> getClubs(){
        return persist.getClubs();
    }

    @RequestMapping (value = "get-games")
    public List<Game> getGames(){
        return persist.getGames();
    }
    @RequestMapping (value = "get-round")
    public List<Game> getRounds(int round){
        if (0>=round){
            round=0;
        } else if (round>9) {
            round=9;
        }
        return persist.getGamesByRound(round);
    }

    @RequestMapping (value ="constant-update")
    public BasicResponse constantUpdate(String secret){
        BasicResponse basicResponse = null;
        boolean success = false;
        Integer errorCode = null;
        User user = persist.getUserBySecret(secret);
        if (user != null) {
            basicResponse = new UserDetailsResponse(true, errorCode, user);
        } else {
            errorCode = ERROR_SECRET_WAS_NOT_SENT;
        }
        if (errorCode != null) {
            basicResponse = new BasicResponse(success, errorCode);
        }
        return basicResponse;
    }
    @RequestMapping (value = "get-team-rank")
    public String getTeamRank(){
        JSONObject object = new JSONObject();
        JSONArray clubs = new JSONArray();
        List<Club> clubList=persist.sortByRank();
        for (int i = 0; i < clubList.size(); i++) {
            Club club=clubList.get(i);
            JSONObject clubJsonObject = new JSONObject();
            clubJsonObject.put("name", club.getName())
                    .put("points", club.getPoints())
                    .put("goal_diff",club.getGoal_diff())
                    .put("wins",club.getWins())
                    .put("draws",club.getDraws())
                    .put("loses",club.getLoses()).put("rank",i+1);
            clubs.put(clubJsonObject);
        }
        object.put("clubs", clubs);
        return object.toString();
    }
    @RequestMapping(value = "stream-games")
    public String streamGame(){

        JSONObject object = new JSONObject();
        JSONArray home_goals = new JSONArray();
        JSONArray away_goals = new JSONArray();



            // Retrieve active games with null check
            List<Game> activeGames = persist.getGamesByStatus("active");
            Game game = activeGames != null && !activeGames.isEmpty() ? activeGames.get(0) : null;

            if (game != null) {
                object
                        .put("home", game.getHome().getName())
                        .put("home_score", game.getScore_home())
                        .put("away_score", game.getScore_away())
                        .put("away", game.getAway().getName())
                        .put("minute", game.getMinute());

                // Retrieve and handle potential null lists for goals (using Optional)
                Optional<List<Goal>> optionalHomeGoals = Optional.ofNullable(persist.getGameGoalsBySide(game.getId(), "home"));
                List<Goal> homeGoals = optionalHomeGoals.orElse(Collections.emptyList());

                Optional<List<Goal>> optionalAwayGoals = Optional.ofNullable(persist.getGameGoalsBySide(game.getId(), "away"));
                List<Goal> awayGoals = optionalAwayGoals.orElse(Collections.emptyList());

                // Process home and away goals with null checks
                for (Goal goal : homeGoals) {
                    JSONObject homeJsonObject = new JSONObject();
                    homeJsonObject.put("minute", goal.getMinute())
                            .put("side", goal.getAffiliation())
                            .put("scorer", goal.getePlayer().getName());
                    home_goals.put(homeJsonObject);
                }
                object.put("home_goals", home_goals);

                for (Goal goal : awayGoals) {
                    JSONObject awayJsonObject = new JSONObject();
                    awayJsonObject.put("minute", goal.getMinute())
                            .put("side", goal.getAffiliation())
                            .put("scorer", goal.getePlayer().getName());
                    away_goals.put(awayJsonObject);
                }
                object.put("away_goals", away_goals);
            }
       return object.toString();
    }
    @RequestMapping(value = "stream-upcoming-games")
    public String streamUpcomingGames(){

        JSONObject object=new JSONObject();
        JSONArray jsonArray = new JSONArray();
        List<Game> upcoming_games=persist.getGamesByStatus("not started");
        if(upcoming_games!=null&&!upcoming_games.isEmpty()){
            List<Integer> gameIDs=upcoming_games.stream().map((Game g)->{return g.getId();}).collect(Collectors.toList());
            List<String> gameNames=upcoming_games.stream().map((Game g)->{return g.getHome().getName()+" - "+g.getAway().getName();}).collect(Collectors.toList());
            for (int i = 0; i < upcoming_games.size(); i++) {
                JSONObject teamJsonObject = new JSONObject();
                Prediction prediction=persist.getPredictionByGameId(gameIDs.get(i));
                teamJsonObject.put("game_name",gameNames.get(i))
                        .put("game_id",gameIDs.get(i))
                        .put("home_prob",prediction.getHomeWin())
                        .put("draw_prob",prediction.getDraw())
                        .put("away_prob",prediction.getAwayWin());
                jsonArray.put(teamJsonObject);
            }
            object.put("games",jsonArray);

        }
        return object.toString();
    }



    /*@RequestMapping (value = "start-streaming",produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter createStreamingSession(String secret){
        try{

            SseEmitter sseEmitter=new SseEmitter((long) 3600*1000);
            EventClient eventClient=new EventClient(sseEmitter,secret);
            clients.add(eventClient);
            return sseEmitter;
        }catch (Exception e){
            throw new RuntimeException(e);
        }
    }*/
    @RequestMapping (value = "start-streaming1",produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter createStreamingSession1(){
        try{

            SseEmitter sseEmitter=new SseEmitter((long) 3600*1000);
            sseEmitters.add(sseEmitter);
            sseEmitter.onCompletion(() -> {
                System.out.println("Emitter completed");
                sseEmitters.remove(sseEmitter);
                service.shutdown();
            });

            sseEmitter.onTimeout(() -> {
                System.out.println("Emitter timed out");
                sseEmitters.remove(sseEmitter);
                sseEmitter.complete();
                service.shutdown();
            });

            sseEmitter.onError((throwable) -> {
                System.out.println("Emitter error: " + throwable.getMessage());
                sseEmitters.remove(sseEmitter);
                service.shutdown();

            });

            return sseEmitter;
        }catch (Exception e){
            throw new RuntimeException(e);
        }
    }

    //@#@#@#@#@##@#@#@#@#@#@#@#@#@#@#@#@#@#@##@@#@#@#@
    @RequestMapping(value="user-bet-games")
    public String getUsersGames(String secret){
        //BasicResponse basicResponse = null;
        //boolean success = false;
        //Integer errorCode = null;
        JSONObject object=new JSONObject();
        JSONArray jsonArray = new JSONArray();
        List<Bet> bets=persist.getBetByUserSecret(secret);
        List<Game> games=bets.stream().map(Bet::getGame).toList();
        /*if (persist.getUserBySecret(secret)!=null){

            basicResponse=new JsonResponse(true,null,object.toString());
        }else {
            errorCode=ERROR_SECRET_WAS_NOT_SENT;
        }if (errorCode!=null){
            basicResponse=new BasicResponse(false,errorCode);
        }
        return basicResponse;*/
        if(!bets.isEmpty()){
            for (int i = 0; i < bets.size(); i++) {
                JSONObject betJsonObject = new JSONObject();
                betJsonObject.put("home_team_name",games.get(i).getHome().getName())
                        .put("away_team_name",games.get(i).getAway().getName())
                        .put("game_status",games.get(i).getStatus())
                        .put("guess",bets.get(i).getGuess())
                        .put("money_bet_on_game",bets.get(i).getMoney()).put("bonus",bets.get(i).getBonus());
                if (games.get(i).getStatus().equals("not started")){
                    //prediction
                    Prediction prediction=persist.getPredictionByGameId(games.get(i).getId());
                    betJsonObject.put("home_prob",prediction.getHomeWin())
                            .put("draw_prob",prediction.getDraw())
                            .put("away_prob",prediction.getAwayWin());
                } else if (games.get(i).getStatus().equals("active")) {
                    //stream
                    betJsonObject.put("home_score",games.get(i).getScore_home())
                            .put("away_score",games.get(i).getScore_away())
                            .put("minute",games.get(i).getMinute());
                } else if (games.get(i).getStatus().equals("finished")) {
                    //result
                    betJsonObject.put("home_score",games.get(i).getScore_home())
                            .put("away_score",games.get(i).getScore_away())
                            .put("response_bet",bets.get(i).isResponse());
                    if(bets.get(i).isResponse()){
                        if (bets.get(i).isResult()){
                            betJsonObject.put("result","correct");
                        } else{
                            betJsonObject.put("result","wrong");
                        }
                    }
                }


                jsonArray.put(betJsonObject);
            }
        }
        object.put("bet_history_games",jsonArray);
        return object.toString();
    }
    @RequestMapping (value = "start-streaming-table",produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter createStreamingSessionTable(){
        try{
            SseEmitter sseEmitter=new SseEmitter((long) 3600*1000);
            return sseEmitter;
        }catch (Exception e){
            throw new RuntimeException(e);
        }
    }
    @RequestMapping (value = "bet-user")
    public List<Bet> betByUser(String secret){
        return persist.getBetByUserSecret(secret);
    }
    @RequestMapping(value="upcoming-games")
    public List<Game> upcomingGames(){
        return persist.getGamesByStatus("not started");
    }
    @RequestMapping(value="goals")
    public List<Goal> gameGoals(){
        List<Game> active_game=persist.getGamesByStatus("active");
        List<Goal> goals=new ArrayList<>();
        if(active_game != null&&!active_game.isEmpty()){
            Game game=active_game.get(0);
            if(game!=null){
                goals=persist.getGameGoals(game.getId());
            }
        }
        return goals;
    }
    @RequestMapping(value = "bet")
    public BetResponse placeBet(int gameId, String secret, int money, int guess){
        //"D71D7BD3"
        BetResponse betResponse=null;
        Bet bet=new Bet();
        bet.setGuess(guess);
        bet.setMoney(money);
        boolean success=false;
        Integer errorCode=null;
        if (secret!=null){
            User user=persist.getUserBySecret(secret);
            if (user!=null){
                bet.setUser(user);
                if(0<gameId&&gameId<=90){
                    Game game=persist.getGameById(gameId);
                    if (game!=null){
                        bet.setGame(game);


                        if (bet.getUser().getMoney()<bet.getMoney()){
                            bet=null;
                            errorCode= Errors.NOT_ENOUGH_MONEY;
                        }else{

                            if (bet.getGuess()<=2&&bet.getGuess()>=0) {
                                String gameStatus = bet.getGame().getStatus();
                                if (gameStatus != null && gameStatus.equals("not started")) {
                                    // Create a new bet and add it to the pending bets map
                                    bet.getUser().setMoney(bet.getUser().getMoney()-bet.getMoney());
                                    bet.setUser(bet.getUser());
                                    persist.save(bet.getUser());
                                    //pendingBet(bet.getId());
                                    //pendingBets.put(userSecret, bet);
                                    success=true;
                                    Prediction prediction=persist.getPredictionByGameId(bet.getGame().getId());
                                    if (bet.getGuess()==0){
                                        bet.setBonus((100-prediction.getHomeWin())*0.01);
                                    } else if (bet.getGuess()==1) {
                                        bet.setBonus((100-prediction.getDraw())*0.01);
                                    } else if (bet.getGuess()==2) {
                                        bet.setBonus((100-prediction.getAwayWin())*0.01);
                                    }
                                    persist.save(bet);

                                } else {
                                    bet=null;
                                    errorCode= GAME_ALREADY_STARTED;
                                }
                            }else {
                                bet=null;
                                errorCode=Errors.INVALID_GUESS;
                            }
                            // Schedule a task to check if the game status is "finished" and process the bet
                            /**/
                        }
                    }else {bet=null;errorCode= Errors.GAME_NOT_AVAILABLE;}
                }else {bet=null; errorCode= GAME_NOT_AVAILABLE;}
            }else {bet=null; errorCode= ERROR_NO_SUCH_USER;}
        }else {bet=null; errorCode= Errors.ERROR_SECRET_WAS_NOT_SENT;}
        betResponse=new BetResponse(success,errorCode,bet);









        //persist.save(bet);
        //await&async
            /*try {
                Thread.sleep(40000);

            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }*/
        //bet.placeBet("",1);

        return betResponse;
    }

    public void pendingBet(int id){
        Bet bet=persist.getBetById(id);
        //executorService.schedule(() -> {
            new Thread(() -> {
                while (true) {
                    /*try {
                        Thread.sleep(60*1000); // Delay between iterations (adjust as needed)
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }*/
                    Game game=persist.getGameById(bet.getGame().getId());
                    bet.setGame(game);
                    String updatedStatus = bet.getGame().getStatus();
                    if (updatedStatus != null && updatedStatus.equals("finished")) {
                        //processPendingBet(userSecret);
                        int diff=bet.getGame().getScore_home()-bet.getGame().getScore_away();
                        int res=0;
                        if (diff>0){
                            res=0;

                        }else if (diff==0){
                            res=1;

                        } else if(diff<0){
                            res=2;
                        }
                        if (bet.getGuess()==res){
                            bet.setResult(true);
                            int prize=(int) ((2+(bet.getBonus()))*bet.getMoney());
                            bet.getUser().setMoney(bet.getUser().getMoney()+prize);
                            bet.setUser(bet.getUser());
                            persist.save(bet.getUser());
                        }else {
                            bet.setResult(false);
                            /*bet.getUser().setMoney(bet.getUser().getMoney()-bet.getMoney());
                            bet.setUser(bet.getUser());
                            persist.save(bet.getUser());*/
                        }bet.setResponse(true);
                        persist.save(bet);
                        break; // Exit the loop once the game is finished
                    }
                }
            }).start();
        //}, 0, TimeUnit.SECONDS);
    }



}
/*Client client=persist.loadObject(Client.class, id);
        client.setFirstName("Shlomo");
        persist.save(client);
        return client;*/
