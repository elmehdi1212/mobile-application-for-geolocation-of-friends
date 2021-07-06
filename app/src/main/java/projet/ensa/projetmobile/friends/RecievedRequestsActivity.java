package projet.ensa.projetmobile.friends;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import projet.ensa.projetmobile.MenuActivity;
import projet.ensa.projetmobile.R;
import projet.ensa.projetmobile.adapters.InvitationsAdapter;
import projet.ensa.projetmobile.adapters.RequestAdapter;
import projet.ensa.projetmobile.connexion.RetrofitInstance;
import projet.ensa.projetmobile.models.User;
import projet.ensa.projetmobile.service.DataService;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import java.util.List;

public class RecievedRequestsActivity extends AppCompatActivity {
    private RecyclerView rvInvitations;
    private TextView iconNoInvitations;
    private TextView noInvitations;
    DataService service = RetrofitInstance.getInstance().create(DataService.class);
    private int id;
    private SharedPreferences mySharedPreferences;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recieved_requests);
        mySharedPreferences= this.getSharedPreferences("MYPREFERENCENAME", Context.MODE_PRIVATE);
        id= mySharedPreferences.getInt("ID", 0);
        rvInvitations=findViewById(R.id.rvInvitations);
        iconNoInvitations=findViewById(R.id.iconNoInvitations);
        noInvitations=findViewById(R.id.noInvitations);
        Call<List<User>> invite = service.getRecievedRequests(id);
        invite.enqueue(new Callback<List<User>>() {
            @Override
            public void onResponse(Call<List<User>> call, Response<List<User>> response) {
                List<User> invitationResponse = response.body();
                if (invitationResponse.size() == 0) {
                    iconNoInvitations.setVisibility(View.VISIBLE);
                    noInvitations.setVisibility(View.VISIBLE);
                }
                InvitationsAdapter invitationsAdapter = new InvitationsAdapter(getApplicationContext(),id, invitationResponse);
                rvInvitations.setAdapter(invitationsAdapter);
                rvInvitations.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
            }

            @Override
            public void onFailure(Call<List<User>> call, Throwable t) {

            }
        });

    }
    public void goBack(View view){
        Intent intent=new Intent(RecievedRequestsActivity.this, MenuActivity.class);
        startActivity(intent);
    }
}