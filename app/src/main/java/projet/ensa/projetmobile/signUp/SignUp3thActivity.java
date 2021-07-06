package projet.ensa.projetmobile.signUp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.ActivityOptions;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.util.Pair;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.hbb20.CountryCodePicker;
import com.quickblox.auth.QBAuth;
import com.quickblox.auth.session.QBSession;
import com.quickblox.auth.session.QBSettings;
import com.quickblox.core.QBEntityCallback;
import com.quickblox.core.exception.QBResponseException;
import com.quickblox.users.QBUsers;
import com.quickblox.users.model.QBUser;

import java.util.List;

import projet.ensa.projetmobile.ChatDialogActivity;
import projet.ensa.projetmobile.ContactActivity;
import projet.ensa.projetmobile.MapActivity;
import projet.ensa.projetmobile.R;
import projet.ensa.projetmobile.SendSMSActivity;
import projet.ensa.projetmobile.VerifySMSActivity;
import projet.ensa.projetmobile.connexion.RetrofitInstance;
import projet.ensa.projetmobile.models.User;
import projet.ensa.projetmobile.service.DataService;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SignUp3thActivity extends AppCompatActivity {
    private TextView titleText;
    private Button next;
    private ImageView backBtn;
    private TextInputLayout telephone;
    private TelephonyManager tm;
    private String imei;
    private CountryCodePicker ccp;
    private SharedPreferences mySharedPreferences;
    public final String APP_ID="88787";
    public final String AUTH_KEY="n3zpwLhz8zzb2NX";
    public final String AUTH_SECRET="SZJEN8RSYmhU3M5";
    public final String ACCOUNT_KEY="1QUWLB4YaWWHuCP2RTYU";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up3th);
        mySharedPreferences= this.getSharedPreferences("MYPREFERENCENAME", Context.MODE_PRIVATE);
        imei= mySharedPreferences.getString("IMEI", "");
        titleText = findViewById(R.id.singup_title_text);
        next = findViewById(R.id.singup_next_button);
        backBtn = findViewById(R.id.singup_back_arrow_btn);
        telephone = findViewById(R.id.telephone);
        ccp = (CountryCodePicker) findViewById(R.id.ccp);
        ccp.registerCarrierNumberEditText(telephone.getEditText());
        initializeFramework();
        registerSession();



    }


    public void callNextSignUpScreen(View view) {
        if (!validatePhoneNumber()) {
            return;
        }
        DataService service = RetrofitInstance.getInstance().create(DataService.class);
        User user=new User(getLastName(),getFirstName(),getEmail(),getPhoneNumber(),imei,getDateNaissance(),getGender(),true);
        Call<User> call = service.createUser(user);
        call.enqueue(new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {


            }

            @Override
            public void onFailure(Call<User> call, Throwable t) {



            }
        });
    /*    QBUser userQB=new QBUser(""+getFirstName(),""+imei);
        QBUsers.signUp(userQB).performAsync(new QBEntityCallback<QBUser>() {
            @Override
            public void onSuccess(QBUser qbUser, Bundle bundle) {
               Toast.makeText(getApplicationContext(),"sign up seccuss",Toast.LENGTH_LONG).show();
            }

            @Override
            public void onError(QBResponseException e) {
                Toast.makeText(getApplicationContext(),""+e.getMessage(),Toast.LENGTH_LONG).show();

            }
        });*/
        Intent intent = new Intent(SignUp3thActivity.this, SendSMSActivity.class);
        intent.putExtra("user","elmehdi");
        intent.putExtra("password",""+imei);
        startActivity(intent);






    }

    public void callBackScreen(View view) {
        Pair[] pairs = new Pair[3];
        pairs[0] = new Pair<View, String>(titleText, "transition_title_text");
        pairs[1] = new Pair<View, String>(next, "transition_next_btn");
        pairs[2] = new Pair<View, String>(backBtn, "transition_back_arrow_btn");
        Intent intent = new Intent(SignUp3thActivity.this, SignUp2ndActivity.class);
        ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(SignUp3thActivity.this, pairs);
        startActivity(intent, options.toBundle());
        ;
    }

    /******************************************************** ALl User Input *****************************************/
    public String getPhoneNumber() {
        String numberPhone=ccp.getFullNumberWithPlus();
        return numberPhone;
    }

    public String getFirstName() {
        String firstName = getIntent().getStringExtra("prenom");
        return firstName;
    }

    public String getLastName() {
        String lastName = getIntent().getStringExtra("nom");
        return lastName;
    }

    public String getEmail() {
        String email = getIntent().getStringExtra("email");
        return email;
    }

    public String getDateNaissance() {
        String dateNaissance = getIntent().getStringExtra("dateNaissance");
        return dateNaissance;
    }

    public String getGender() {
        String sexe = getIntent().getStringExtra("sexe");
        return sexe;
    }

    private boolean validatePhoneNumber() {
        if (getPhoneNumber().isEmpty()) {
            telephone.setError("Champ ne peut pas être vide");
            return false;
        } else if (ccp.isValidFullNumber() == true) {
            telephone.setBoxStrokeColor(0xFF00FF00);
            telephone.setError(null);
            telephone.setErrorEnabled(false);
            return true;
        } else {
            telephone.setError("invalid Number");
            return false;
        }

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