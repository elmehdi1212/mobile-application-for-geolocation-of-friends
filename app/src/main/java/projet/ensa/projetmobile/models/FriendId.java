package projet.ensa.projetmobile.models;

import java.io.Serializable;

public class FriendId implements Serializable {

    private int user1Id;

    private int user2Id;
    public FriendId() {
        super();

    }


    public int getUser1Id() {
        return user1Id;
    }

    public void setUser1Id(int user1Id) {
        this.user1Id = user1Id;
    }

    public int getUser2Id() {
        return user2Id;
    }

    public void setUser2Id(int user2Id) {
        this.user2Id = user2Id;
    }


}
