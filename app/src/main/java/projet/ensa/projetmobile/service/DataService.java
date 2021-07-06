package projet.ensa.projetmobile.service;

import org.checkerframework.checker.nullness.compatqual.PolyNullDecl;

import java.util.List;

import projet.ensa.projetmobile.models.Friend;
import projet.ensa.projetmobile.models.Position;
import projet.ensa.projetmobile.models.User;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface DataService {

    @GET("/users/all")
    Call<List<User>> getAllUsers();

    @GET("/users/allUsers/{id}")
    Call<List<User>> getAllUser(@Path("id") int id);

    @POST("/users/save")
    Call<User> createUser(@Body User user);

    @POST("/friends/save")
    Call<Friend> createRequestFriend(@Body Friend friend);

    @GET("/users/findRequests/{id}")
    Call<List<User>> getAllRequests(@Path("id") int id);

    @GET("/users/findRecievdRequests/{id}")
    Call<List<User>> getRecievedRequests(@Path("id") int id);


    @GET("/users/findFriends/{id}")
    Call<List<User>> getAllFriends(@Path("id") int id);

    @POST("/positions/save")
    Call<Position> createPosition(@Body Position position);


    @GET("/positions/user/{id}")
    Call<Position> getLastPositionOfFriend(@Path("id") int id);




}