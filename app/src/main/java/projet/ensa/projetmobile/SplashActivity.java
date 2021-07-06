package projet.ensa.projetmobile;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.widget.Toast;

import java.util.List;

import projet.ensa.projetmobile.connexion.RetrofitInstance;
import projet.ensa.projetmobile.models.User;
import projet.ensa.projetmobile.service.DataService;
import projet.ensa.projetmobile.signUp.SignUpActivity;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SplashActivity extends AppCompatActivity {
    private List<User> userss;
    private SharedPreferences mySharedPreferences;
    private String imei;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        mySharedPreferences= this.getSharedPreferences("MYPREFERENCENAME", Context.MODE_PRIVATE);
        imei= mySharedPreferences.getString("IMEI", "");
        DataService service = RetrofitInstance.getInstance().create(DataService.class);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Call<List<User>> users = service.getAllUsers();
                users.enqueue(new Callback<List<User>>() {
                    @Override
                    public void onResponse(Call<List<User>> call, Response<List<User>> response) {
                        userss=response.body();
                        for(User us:userss){
                            if(us.getImei().equals(imei)){
                                    Intent intent=new Intent(SplashActivity.this,MapActivity.class);
                                    startActivity(intent);
                                    finish();

                            }
                        }
                    }

                    @Override
                    public void onFailure(Call<List<User>> call, Throwable t) {

                    }
                });
                Intent intent2=new Intent(SplashActivity.this, SignUpActivity.class);
                startActivity(intent2);
                finish();

            }
        },3000);
    }


}