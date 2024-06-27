package com.ashcollege;


import com.ashcollege.BetService.Bet;
import com.ashcollege.entities.*;
import com.ashcollege.games.*;
import com.github.javafaker.Faker;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;


@Transactional
@Component
@SuppressWarnings("unchecked")
public class Persist {

    private static final Logger LOGGER = LoggerFactory.getLogger(Persist.class);

    private final SessionFactory sessionFactory;





    @Autowired
    public Persist(SessionFactory sf) {
        this.sessionFactory = sf;
    }

    public Session getQuerySession() {
        return sessionFactory.getCurrentSession();
    }

    public void save(Object object) {
        this.sessionFactory.getCurrentSession().saveOrUpdate(object);
    }

    public <T> T loadObject(Class<T> clazz, int oid) {
        return this.getQuerySession().get(clazz, oid);
    }

    public <T> List<T> loadList(Class<T> clazz) {
        return this.sessionFactory.getCurrentSession().createQuery("FROM "+clazz.getSimpleName()).list();
    }



    public User login(String email,String password){
        return (User) this.sessionFactory.getCurrentSession().createQuery(
                "FROM User WHERE email= :email AND password= :password").setParameter("email",email).setParameter("password",password).uniqueResult();
    }

    public List<Club> getClubs(){
        return loadList(Club.class);
    }

    public List<User> getUserByEmail(String email){
        return  this.sessionFactory.getCurrentSession().createQuery("FROM User WHERE email=:email").setParameter("email",email).list();
    }public List<User> getUserByUserName(String username){
        return this.sessionFactory.getCurrentSession().createQuery("FROM User WHERE username=:username").setParameter("username",username).list();
    }public User getUserBySecret(String secret){
        return (User) this.sessionFactory.getCurrentSession().createQuery("FROM User WHERE secret=:secret").setParameter("secret",secret).uniqueResult();
    }public Game getGameById(int id){
        return (Game) this.sessionFactory.getCurrentSession().createQuery("FROM Game WHERE id=:id").setParameter("id",id).uniqueResult();
    }public Bet getBetById(int id){
        return (Bet) this.sessionFactory.getCurrentSession().createQuery("FROM Bet WHERE id=:id").setParameter("id",id).uniqueResult();
    }public Club getClubById(int id){
        return (Club) this.sessionFactory.getCurrentSession().createQuery("FROM Club WHERE id=:id").setParameter("id",id).uniqueResult();
    }public List<EPlayer> getEPlayersClubById(int id){
        return  this.sessionFactory.getCurrentSession().createQuery("FROM EPlayer WHERE club.id=:id").setParameter("id",id).list();
    }public List<Goal> getGameGoals(int id){
        return  this.sessionFactory.getCurrentSession().createQuery("FROM Goal WHERE game.id=:id").setParameter("id",id).list();
    }public List<Goal> getGameGoalsBySide(int id,String affiliation){
        return  this.sessionFactory.getCurrentSession().createQuery("FROM Goal WHERE game.id=:id AND affiliation=:affiliation").setParameter("id",id).setParameter("affiliation",affiliation).list();
    }public int getMoneyBySecret(String secret){
        User u=(User)(this.sessionFactory.getCurrentSession().createQuery("FROM User WHERE secret=:secret").setParameter("secret",secret).uniqueResult());
        if (u==null) return 0;
        return u.getMoney();
    }public Prediction getPredictionByGameId(int id){
        return  (Prediction) this.sessionFactory.getCurrentSession().createQuery("FROM Prediction WHERE game.id=:id").setParameter("id",id).uniqueResult();
    }
    public List<Bet> getBetByUserSecret(String secret){
        return this.sessionFactory.getCurrentSession().createQuery("FROM Bet WHERE user.secret=:secret").setParameter("secret",secret).list();
    }


    public List<Game> getGames(){
        return loadList(Game.class);
    }

    public List<Game> getGamesByRound(int round){
        return this.sessionFactory.getCurrentSession().createQuery("FROM Game WHERE round = :round").setParameter("round",round).list();
    }
    public List<Game> getGamesByStatus(String status){
        return  this.sessionFactory.getCurrentSession().createQuery("FROM Game WHERE status = :status").setParameter("status",status).list();
    }

    public List<Club> sortByRank() {List<Club> clubs=this.loadList(Club.class);
        Collections.sort(clubs, new Comparator<Club>() {
            @Override
            public int compare(Club club1, Club club2) {
                // Sort by points first (descending)
                if (club1.getPoints() != club2.getPoints()) {
                    return Integer.compare(club2.getPoints(), club1.getPoints());
                }

                // If points are equal, sort by goal difference (descending)
                if (club1.getGoal_diff() != club2.getGoal_diff()) {
                    return Integer.compare(club2.getGoal_diff(), club1.getGoal_diff());
                }

                // If both points and goal difference are equal, sort by name alphabetically (ascending)
                return club1.getName().compareTo(club2.getName());
            }
        });
        return clubs; // Return the sorted list
    }
    public User signup(String username, String password, String email){
        //no such user
        //validate email
        Faker faker=new Faker();
        User user=new User(username,password,email,faker.random().hex());
        user.setMoney(1000);
        save(user);
        return login(email,password);
    }
    public boolean EmailNotExists(String email){
        return this.sessionFactory.getCurrentSession().createQuery(
                "FROM User WHERE email= :email").setParameter("email",email).list().isEmpty();
    }public boolean UserNameNotExists(String username){
        return this.sessionFactory.getCurrentSession().createQuery(
                "FROM User WHERE username= :username").setParameter("username",username).list().isEmpty();
    }





}

/*try (Session = this.getQuerySession()) {
            String hql = "FROM " + clazz.getName();
            return session.createQuery(hql, clazz).list();
        }



        Faker faker=new Faker();
                if(persist.loadList(Player.class).size()<110){
                    for (int j = 0; j < 10; j++) {
                        List <Player> players=new ArrayList<>();
                        //Club club=new Club();
                        //club.setId(j+1);
                        double skill=0;
                        for (int i = 0; i < 11; i++) {
                            Player player=new Player(faker.leagueOfLegends().champion(),new Random().nextInt(40,80),new Random().nextInt(40,80));
                            player.setClub(j+1);
                            players.add(player);
                            persist.save(player);
                            //skill+=(player.getAttack()*0.6+ player.getDefence()*0.4)/11;

                        }
                        //club.setPlayers(players);
                        //club.setSkill(skill);
                        //persist.save(club);
                    }
                }

        */