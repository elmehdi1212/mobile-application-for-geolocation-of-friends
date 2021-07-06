package projet.ensa.projetmobile.friends;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import java.util.List;

import projet.ensa.projetmobile.MenuActivity;
import projet.ensa.projetmobile.R;
import projet.ensa.projetmobile.adapters.RequestAdapter;
import projet.ensa.projetmobile.connexion.RetrofitInstance;
import projet.ensa.projetmobile.models.User;
import projet.ensa.projetmobile.service.DataService;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class FriendActivity extends AppCompatActivity {
    private TextView icon;
    private TextView textIcon;
    private DataService service = RetrofitInstance.getInstance().create(DataService.class);
    private int id;
    private SharedPreferences mySharedPreferences;
    private RecyclerView rvFriends;
    private SharedPreferences.Editor editor;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friend);
        mySharedPreferences= this.getSharedPreferences("MYPREFERENCENAME", Context.MODE_PRIVATE);
        editor = mySharedPreferences.edit();
        id= mySharedPreferences.getInt("ID", 0);
        rvFriends = findViewById(R.id.rvFriends);
        icon = findViewById(R.id.iconNoFriends);
        textIcon = findViewById(R.id.noFriends);
        Call<List<User>> friends = service.getAllFriends(id);
        friends.enqueue(new Callback<List<User>>() {
            @Override
            public void onResponse(Call<List<User>> call, Response<List<User>> response) {
                List<User> requestResponse = response.body();

                if (requestResponse.size() == 0) {
                    icon.setVisibility(View.VISIBLE) ;
                    textIcon.setVisibility(View.VISIBLE);
                }

                RequestAdapter friendsAdapter = new RequestAdapter(getApplicationContext(), requestResponse,"friends");
                rvFriends.setAdapter(friendsAdapter);
                rvFriends.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
            }

            @Override
            public void onFailure(Call<List<User>> call, Throwable t) {

            }
        });

    }

    public void goBack(View view){
        Intent intent=new Intent(FriendActivity.this, MenuActivity.class);
        startActivity(intent);

    }
}