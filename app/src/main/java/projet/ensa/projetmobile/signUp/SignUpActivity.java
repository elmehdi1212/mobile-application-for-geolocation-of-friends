package projet.ensa.projetmobile.signUp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.ActivityOptions;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
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
import android.widget.DatePicker;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import java.util.Calendar;

import projet.ensa.projetmobile.DatePickerFragment;
import projet.ensa.projetmobile.R;

public class SignUpActivity extends AppCompatActivity {

    TextView titleText;
    Button next;
    ScrollView scrollView;
    TextInputLayout nom;
    TextInputLayout prenom;
    TextInputLayout email;
    TextInputLayout dateNaissance;
    private TelephonyManager tm;
    private  String imei;
    private SharedPreferences.Editor editor;

    @Override

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        SharedPreferences mySharedPreferences = this.getSharedPreferences("MYPREFERENCENAME", Context.MODE_PRIVATE);
        editor = mySharedPreferences.edit();
        titleText = findViewById(R.id.singup_title_text);
        next = findViewById(R.id.singup_next_button);
        scrollView = findViewById(R.id.mainScroll);
        scrollView.setVerticalScrollBarEnabled(false);
        scrollView.setHorizontalScrollBarEnabled(false);
        nom = findViewById(R.id.lastName);
        prenom = findViewById(R.id.firstName);
        email = findViewById(R.id.email);
        dateNaissance = findViewById(R.id.dateNaissance);
        getCurrentImei();
        dateNaissance.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                showDatePicker();
            }
        });


    }

    public void callNextSignUpScreen(View view) {
        editor.putString("IMEI",getCurrentImei());
        editor.apply();
        if (!validateLastName() | !validateFirstName() | !validateEmail() | !validateDate()) {
            return;
        }
        Intent intent = new Intent(SignUpActivity.this, SignUp2ndActivity.class);
        Pair[] pairs = new Pair[2];
        pairs[0] = new Pair<View, String>(titleText, "transition_title_text");
        pairs[1] = new Pair<View, String>(next, "transition_next_btn");
        validateLastName();
        ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(SignUpActivity.this, pairs);
        intent.putExtra("nom", getNom());
        intent.putExtra("prenom", getPrenom());
        intent.putExtra("email", getEmail());
        intent.putExtra("dateNaissance", getDateNaissance());
        startActivity(intent, options.toBundle());

    }

    /***************************************************************get the user input ****************************************/
    public String getNom() {
        String lastName = nom.getEditText().getText().toString();
        return lastName;
    }

    public String getPrenom() {
        String firstName = prenom.getEditText().getText().toString();
        return firstName;
    }

    public String getEmail() {
        String mail = email.getEditText().getText().toString();
        return mail;
    }

    public String getDateNaissance() {
        String birthDay = dateNaissance.getEditText().getText().toString();
        return birthDay;
    }

    /******************************************************** Date de naissance ************************************************************/
    public void showDatePicker() {
        DatePickerFragment date = new DatePickerFragment();
        Calendar calender = Calendar.getInstance();
        Bundle args = new Bundle();
        args.putInt("year", calender.get(Calendar.YEAR));
        args.putInt("month", calender.get(Calendar.MONTH));
        args.putInt("day", calender.get(Calendar.DAY_OF_MONTH));
        date.setArguments(args);
        date.setCallBack(ondate);
        date.show(getSupportFragmentManager(), "Date Picker");
    }

    DatePickerDialog.OnDateSetListener ondate = new DatePickerDialog.OnDateSetListener() {

        public void onDateSet(DatePicker view, int year, int monthOfYear,
                              int dayOfMonth) {

            dateNaissance.getEditText().setText(String.valueOf(dayOfMonth) + "/" + String.valueOf(monthOfYear + 1)
                    + "/" + String.valueOf(year));
        }
    };

    /************************************************ Validation User Input ***************************************/

    private boolean validateLastName() {
        String value = getNom().trim();
        if (value.isEmpty()) {
            nom.setError("Champ ne peut pas être vide");
            return false;
        } else {
            nom.setError(null);
            nom.setErrorEnabled(false);
            return true;
        }

    }

    private boolean validateFirstName() {
        String value = getPrenom().trim();
        if (value.isEmpty()) {
            prenom.setError("Champ ne peut pas être vide");
            return false;
        } else {
            prenom.setError(null);
            prenom.setErrorEnabled(false);
            return true;
        }

    }

    private boolean validateEmail() {
        String value = getEmail().trim();
        String checkEmail = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";
        if (value.isEmpty()) {
            email.setError("Champ ne peut pas être vide");
            return false;
        } else if (!value.matches(checkEmail)) {
            email.setError("Email invalide !!");
            return false;

        } else {
            email.setError(null);
            email.setErrorEnabled(false);
            return true;
        }

    }

    private boolean validateDate() {
        String date=getDateNaissance().trim();
        if (date.isEmpty()) {
            dateNaissance.setError("Champ ne peut pas être vide");
            return false;
        } else {
            dateNaissance.setError(null);
            dateNaissance.setErrorEnabled(false);
            return true;
        }

    }
    public  String getCurrentImei(){
        int permisI = ContextCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE);

        if(permisI == PackageManager.PERMISSION_GRANTED){
            tm = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
            imei = tm.getDeviceId().toString();
        }
        else{
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_PHONE_STATE},123);
        }

        return imei;


    }



}