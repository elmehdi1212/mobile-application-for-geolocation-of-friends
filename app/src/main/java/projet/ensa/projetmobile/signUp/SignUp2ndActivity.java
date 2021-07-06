package projet.ensa.projetmobile.signUp;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ActivityOptions;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.util.Pair;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import projet.ensa.projetmobile.R;

public class SignUp2ndActivity extends AppCompatActivity {
    private TextView titleText;
    private Button next;
    private ImageView backBtn;
    private RadioGroup radioGroup;
    private RadioButton radioButton;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up2nd);
        titleText = findViewById(R.id.singup_title_text);
        next = findViewById(R.id.singup_next_button);
        backBtn = findViewById(R.id.singup_back_arrow_btn);
        radioGroup = (RadioGroup) findViewById(R.id.sexe);


    }

    public void callNextSignUpScreen(View view) {
        if (!validGender()) {
            return;
        }
        String firstName = getIntent().getStringExtra("prenom");
        String lastName = getIntent().getStringExtra("nom");
        String email = getIntent().getStringExtra("email");
        String dateNaissance = getIntent().getStringExtra("dateNaissance");
        Pair[] pairs = new Pair[3];
        pairs[0] = new Pair<View, String>(titleText, "transition_title_text");
        pairs[1] = new Pair<View, String>(next, "transition_next_btn");
        pairs[2] = new Pair<View, String>(backBtn, "transition_back_arrow_btn");
        Intent intent = new Intent(SignUp2ndActivity.this, SignUp3thActivity.class);
        ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(SignUp2ndActivity.this, pairs);
        intent.putExtra("nom", lastName);
        intent.putExtra("prenom", firstName);
        intent.putExtra("email", email);
        intent.putExtra("dateNaissance", dateNaissance);
        intent.putExtra("sexe", getGender());
        startActivity(intent, options.toBundle());


    }

    public void callBackScreen(View view) {
        Pair[] pairs = new Pair[3];
        pairs[0] = new Pair<View, String>(titleText, "transition_title_text");
        pairs[1] = new Pair<View, String>(next, "transition_next_btn");
        pairs[2] = new Pair<View, String>(backBtn, "transition_back_arrow_btn");
        Intent intent = new Intent(SignUp2ndActivity.this, SignUpActivity.class);
        ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(SignUp2ndActivity.this, pairs);
        startActivity(intent, options.toBundle());


    }

    public String getGender() {
        int selectedId = radioGroup.getCheckedRadioButtonId();
        radioButton = (RadioButton) findViewById(selectedId);
        return radioButton.getText().toString();

    }

    public boolean validGender() {
        if (radioGroup.getCheckedRadioButtonId() == -1) {
            Toast.makeText(this, "veuillez sélectionner le sexe", Toast.LENGTH_SHORT).show();
            return false;
        } else {
            return true;
        }
    }

}