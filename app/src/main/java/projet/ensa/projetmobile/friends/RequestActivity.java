package projet.ensa.projetmobile.friends;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
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

public class RequestActivity extends AppCompatActivity {
    private RecyclerView rvFriendRequest;
    private TextView icon;
    private TextView textIcon;
    DataService service = RetrofitInstance.getInstance().create(DataService.class);
    private int id;
    private SharedPreferences mySharedPreferences;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mySharedPreferences= this.getSharedPreferences("MYPREFERENCENAME", Context.MODE_PRIVATE);
        id= mySharedPreferences.getInt("ID", 0);
        setContentView(R.layout.activity_request);
        rvFriendRequest = findViewById(R.id.rvFriendRequest);
        icon = findViewById(R.id.iconNothingFound);
        textIcon = findViewById(R.id.nothingFound);
        Call<List<User>> friendRequest = service.getAllRequests(id);
        friendRequest.enqueue(new Callback<List<User>>() {
            @Override
            public void onResponse(Call<List<User>> call, Response<List<User>> response) {
                List<User> requestResponse = response.body();
                if (requestResponse.size() == 0) {
                    icon.setVisibility(View.VISIBLE);
                    textIcon.setVisibility(View.VISIBLE);
                }
                RequestAdapter requestAdapter = new RequestAdapter(getApplicationContext(), requestResponse,"request");
                rvFriendRequest.setAdapter(requestAdapter);
                rvFriendRequest.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
            }

            @Override
            public void onFailure(Call<List<User>> call, Throwable t) {

            }
        });


    }

    public void goBack(View view){
        Intent intent=new Intent(RequestActivity.this, MenuActivity.class);
        startActivity(intent);
    }


}