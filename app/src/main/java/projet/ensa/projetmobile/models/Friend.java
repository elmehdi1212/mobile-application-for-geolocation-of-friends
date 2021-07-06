package projet.ensa.projetmobile.models;

import java.util.Date;
public class Friend {
    private FriendId id;
    private User user1;
    private User user2;
    private String date;

    private int etat;

    public Friend() {
        super();
    }


    public Friend(FriendId id, User user1, User user2, String date, int etat) {
        super();
        this.id = id;
        this.user1 = user1;
        this.user2 = user2;
        this.date = date;
        this.etat = etat;
    }



    public FriendId getId() {
        return id;
    }

    public void setId(FriendId id) {
        this.id = id;
    }

    public User getUser1() {
        return user1;
    }

    public void setUser1(User user1) {
        this.user1 = user1;
    }

    public User getUser2() {
        return user2;
    }

    public void setUser2(User user2) {
        this.user2 = user2;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public int getEtat() {
        return etat;
    }

    public void setEtat(int etat) {
        this.etat = etat;
    }
}


