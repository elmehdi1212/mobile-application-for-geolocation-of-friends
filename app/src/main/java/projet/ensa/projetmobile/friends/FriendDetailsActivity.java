package projet.ensa.projetmobile.friends;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import projet.ensa.projetmobile.R;
import projet.ensa.projetmobile.connexion.RetrofitInstance;
import projet.ensa.projetmobile.models.Friend;
import projet.ensa.projetmobile.models.FriendId;
import projet.ensa.projetmobile.models.User;
import projet.ensa.projetmobile.service.DataService;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Date;

public class FriendDetailsActivity extends AppCompatActivity {
    private TextView imageSymoble;
    private TextView friendName;
    private TextView friendPhone;
    private TextView friendBlock;
    private DataService service;
    private SimpleDateFormat formatter;
    private Date date;
    private String currentDate;
    private SharedPreferences mySharedPreferences;
    private int idCurrentUser;
    private TextView friendUnBlock;
    private AlertDialog.Builder alertDialogBuilder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friend_details);
        service = RetrofitInstance.getInstance().create(DataService.class);
        formatter = new SimpleDateFormat("dd/MM/yyyy");
        date = new Date();
        currentDate = formatter.format(date);
        mySharedPreferences = this.getSharedPreferences("MYPREFERENCENAME", Context.MODE_PRIVATE);
        idCurrentUser = mySharedPreferences.getInt("ID", 0);
        alertDialogBuilder = new AlertDialog.Builder(this);
        imageSymoble = findViewById(R.id.syImage);
        imageSymoble.setText(getIntent().getStringExtra("symbole"));
        friendName = findViewById(R.id.friendName);
        friendName.setText(getIntent().getStringExtra("name"));
        friendPhone = findViewById(R.id.friendPhone);
        friendPhone.setText(getIntent().getStringExtra("phone"));
        friendBlock = findViewById(R.id.block);
        friendUnBlock = findViewById(R.id.unBlock);
        friendBlock.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FriendId id = new FriendId();
                id.setUser1Id(idCurrentUser);
                id.setUser2Id(Integer.parseInt(getIntent().getStringExtra("id")));
                User u = new User(idCurrentUser);
                User u2 = new User(Integer.parseInt(getIntent().getStringExtra("id")));
                Friend f = new Friend(id, u, u2, currentDate, 3);
                alertDialogBuilder.setMessage("Bloquer l'utilisateur "+getIntent().getStringExtra("name")+"? Les utilisateurs bloqués ne pourront plus vous envoyer de demandes.");
                alertDialogBuilder.setPositiveButton("yes",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface arg0, int arg1) {
                                Call<Friend> callFriend = service.createRequestFriend(f);
                                callFriend.enqueue(new Callback<Friend>() {
                                    @Override
                                    public void onResponse(Call<Friend> call, Response<Friend> response) {

                                    }

                                    @Override
                                    public void onFailure(Call<Friend> call, Throwable t) {

                                    }
                                });
                                friendBlock.setVisibility(View.GONE);
                                friendUnBlock.setVisibility(View.VISIBLE);

                            }
                        });
                alertDialogBuilder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override

                    public void onClick(DialogInterface dialog, int which) {
                        finish();
                    }
                });
                AlertDialog alertDialog = alertDialogBuilder.create();
                alertDialog.show();


            }
        });
        friendUnBlock.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FriendId id = new FriendId();
                id.setUser1Id(idCurrentUser);
                id.setUser2Id(Integer.parseInt(getIntent().getStringExtra("id")));
                User u = new User(idCurrentUser);
                User u2 = new User(Integer.parseInt(getIntent().getStringExtra("id")));
                Friend f = new Friend(id, u, u2, currentDate, 2);
                Call<Friend> callFriend = service.createRequestFriend(f);
                callFriend.enqueue(new Callback<Friend>() {
                    @Override
                    public void onResponse(Call<Friend> call, Response<Friend> response) {

                    }

                    @Override
                    public void onFailure(Call<Friend> call, Throwable t) {

                    }
                });

                friendBlock.setVisibility(View.VISIBLE);
                friendUnBlock.setVisibility(View.GONE);



            }
        });


    }

    public void goBack(View view) {
        Intent intent = new Intent(FriendDetailsActivity.this, FriendActivity.class);
        startActivity(intent);
    }


}