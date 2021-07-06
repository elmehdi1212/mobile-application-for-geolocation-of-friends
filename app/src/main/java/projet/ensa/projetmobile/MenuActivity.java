package projet.ensa.projetmobile;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.navigation.NavigationView;
import com.quickblox.auth.QBAuth;
import com.quickblox.auth.session.QBSession;
import com.quickblox.auth.session.QBSettings;
import com.quickblox.core.QBEntityCallback;
import com.quickblox.core.exception.QBResponseException;
import com.quickblox.users.QBUsers;
import com.quickblox.users.model.QBUser;

import projet.ensa.projetmobile.friends.FriendActivity;
import projet.ensa.projetmobile.friends.RecievedRequestsActivity;
import projet.ensa.projetmobile.friends.RequestActivity;
import projet.ensa.projetmobile.friends.SendInvitationActivity;
import projet.ensa.projetmobile.models.User;
import projet.ensa.projetmobile.signUp.SignUp3thActivity;

public class MenuActivity extends AppCompatActivity {
    NavigationView navigationView;
    ActionBarDrawerToggle toggle;
    DrawerLayout drawerLayout;
    private String imei;
    private TextView sy;
    public final String APP_ID="88787";
    public final String AUTH_KEY="n3zpwLhz8zzb2NX";
    public final String AUTH_SECRET="SZJEN8RSYmhU3M5";
    public final String ACCOUNT_KEY="1QUWLB4YaWWHuCP2RTYU";
    private SharedPreferences mySharedPreferences;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);
        initializeFramework();
        registerSession();
        mySharedPreferences= this.getSharedPreferences("MYPREFERENCENAME", Context.MODE_PRIVATE);
        imei= mySharedPreferences.getString("IMEI", "");
        drawerLayout=findViewById(R.id.drawer);
        toggle=new ActionBarDrawerToggle(this,drawerLayout,R.string.open,R.string.close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();
        navigationView=findViewById(R.id.nav_view);
        sy=(TextView) findViewById(R.id.imageView);
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                int id=menuItem.getItemId();
                Fragment fragment=null;
                switch (id)
                {
                    case R.id.friends:
                        Intent intent=new Intent(MenuActivity.this, FriendActivity.class);
                        startActivity(intent);;
                        break;
                    case R.id.friendRequest:
                        Intent intent1=new Intent(MenuActivity.this, RequestActivity.class);

                        startActivity(intent1);;
                        break;
                    case R.id.map:
                        Intent intent2=new Intent(MenuActivity.this,MapActivity.class);
                        startActivity(intent2);
                        break;
                    case R.id.demandesReçues:
                        Intent intent3=new Intent(MenuActivity.this, RecievedRequestsActivity.class);
                        startActivity(intent3);
                        break;
                    case R.id.chat:
                        QBUser qbUser=new QBUser("elmehdi","Azerti1234");
                        QBUsers.signIn(qbUser).performAsync(new QBEntityCallback<QBUser>() {
                            @Override
                            public void onSuccess(QBUser qbUser, Bundle bundle) {
                                Intent intent4=new Intent(MenuActivity.this,ChatDialogActivity.class);
                                startActivity(intent4);
                                finish();
                            }

                            @Override
                            public void onError(QBResponseException e) {
                                Toast.makeText(getApplicationContext(),""+e.getMessage(),Toast.LENGTH_LONG).show();

                            }
                        });

                        break;
                    case R.id.invite:
                        Intent intent5=new Intent(MenuActivity.this, SendInvitationActivity.class);
                        startActivity(intent5);
                        break;
                    case R.id.configuration:
                        Intent intent6=new Intent(MenuActivity.this, ConfigurationActivity.class);
                        startActivity(intent6);
                        break;
                    default:
                        return true;
                }
                return true;
            }
        });
    }
    private void initializeFramework() {
        QBSettings.getInstance().init(getApplicationContext(),APP_ID,AUTH_KEY,AUTH_SECRET);
        QBSettings.getInstance().setAccountKey(ACCOUNT_KEY);
    }
    private void registerSession() {
        QBAuth.createSession().performAsync(new QBEntityCallback<QBSession>() {
            @Override
            public void onSuccess(QBSession qbSession, Bundle bundle) {

            }

            @Override
            public void onError(QBResponseException e) {
                Log.d("ERROR",""+e.getMessage());
            }
        });
    }

}