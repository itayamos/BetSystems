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

    private ScheduledExecutorService executorService;



    private List<EventClient> clients=new ArrayList<>();
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
                            object.put("teamA", System.currentTimeMillis()+"__"+4);
                            List<Game> active_game=persist.getGamesByStatus("active");
                            List<Game> upcoming_games=persist.getGamesByStatus("not started");
                            if(upcoming_games!=null&&!upcoming_games.isEmpty()){
                                List<Integer> gameIDs=upcoming_games.stream().map((Game g)->{return g.getId();}).collect(Collectors.toList());
                                List<String> gameNames=upcoming_games.stream().map((Game g)->{return g.getHome().getName()+" - "+g.getAway().getName();}).collect(Collectors.toList());
                                for (int i = 0; i < upcoming_games.size(); i++) {
                                    object.put("game_name"+i,gameNames.get(i)).append("game_id"+i,gameIDs.get(i));
                                }
                            }

                            if (active_game != null&&!active_game.isEmpty()) {
                                Game game=active_game.get(0);
                                if(game!=null){
                                    object
                                            .put("home",game.getHome().getName())
                                            .put("home_score",game.getScore_home())
                                            .put("away_score",game.getScore_away())
                                            .put("away",game.getAway().getName())
                                            .put("minute",game.getMinute());
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
    @PostConstruct
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
    }
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
            errorCode = ERROR_SIGN_UP_NO_USERNAME;
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
        if (username!=null){
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
                  }
                  else {
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


    @RequestMapping (value = "add-product")
    public boolean addProduct (String description, float price, int count) {
        Product toAdd = new Product(description, price, count);
        dbUtils.addProduct(toAdd);
        return true;
    }

    @RequestMapping (value = "get-products")
    public BasicResponse getProducts(String secret){
        boolean success=false;
        Integer errorCode=null;
        BasicResponse basicResponse=null;
        if(secret!=null){
            User user=dbUtils.getUserBySecret(secret);
            if(user!=null){
                //success=true;
                List<Product> products= dbUtils.getProductsByUserSecret(secret);
                basicResponse=new ProductsResponse(true,null,products);
            }else {
                errorCode=ERROR_NO_SUCH_USER;
            }
        }else {
            errorCode=ERROR_SECRET_WAS_NOT_SENT;
        }
        if (errorCode!=null){
            basicResponse=new BasicResponse(false,errorCode);
        }

        return basicResponse;
    }
    @RequestMapping (value = "test")
    public Client test(String firstName,String newFirstName){
        Client client=persist.getClientByFirstName(firstName);
        client.setFirstName(newFirstName);
        client.setLastName(newFirstName);
        persist.save(client);
        return client;
    }

    @RequestMapping (value = "get-notes")
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
    }
    @RequestMapping (value = "get-clubs")
    public List<Club> getClubs(){
        return persist.getClubs();
    }
    @RequestMapping (value = "get-players")
    public List<Player> getPlayers(){
        return persist.getPlayers();
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
    @RequestMapping (value = "get-team-rank")
    public List<Club> getTeamRank(){
        return persist.sortByRank();
    }



    @RequestMapping (value = "start-streaming",produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter createStreamingSession(String secret){
        try{

            SseEmitter sseEmitter=new SseEmitter((long) 3600*1000);
            EventClient eventClient=new EventClient(sseEmitter,secret);
            clients.add(eventClient);
            return sseEmitter;
        }catch (Exception e){
            throw new RuntimeException(e);
        }
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

    @RequestMapping (value = "bet")
    public BetResponse placeBet(int gameId, String secret, int money, int guess){
        //"D71D7BD3"
        BetResponse betResponse=null;
        Bet bet=new Bet();
        bet.setGuess(guess);
        bet.setMoney(money);
        boolean success=false;
        Integer error=null;
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
                            error= Errors.NOT_ENOUGH_MONEY;
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
                                    persist.save(bet);

                                } else {
                                    bet=null;
                                    error= GAME_ALREADY_STARTED;
                                }
                            }else {
                                bet=null;
                                error=Errors.INVALID_GUESS;
                            }
                            // Schedule a task to check if the game status is "finished" and process the bet
                            /**/
                        }
                    }else {bet=null;error= Errors.GAME_NOT_AVAILABLE;}
                }else {bet=null; error= GAME_NOT_AVAILABLE;}
            }else {bet=null; error= ERROR_NO_SUCH_USER;}
        }else {bet=null; error= Errors.ERROR_SECRET_WAS_NOT_SENT;}
        betResponse=new BetResponse(success,error,bet);









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

                        }else if (diff<0){
                            res=1;

                        } else {
                            res=2;
                        }
                        if (bet.getGuess()==res){
                            bet.setResult(true);
                            bet.getUser().setMoney(bet.getUser().getMoney()+2*bet.getMoney());
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
    @RequestMapping(value = "players")
    public List<Player> playerList(){
        List<Player> players=new ArrayList<>();
        players.add(new Player("asd",60,60,3));
        if (!persist.loadList(Player.class).isEmpty()){
            players=persist.loadList(Player.class);
        }
        return players;
    }


}
/*Client client=persist.loadObject(Client.class, id);
        client.setFirstName("Shlomo");
        persist.save(client);
        return client;*/
