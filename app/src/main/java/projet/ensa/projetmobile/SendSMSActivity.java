package projet.ensa.projetmobile;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.firebase.FirebaseException;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.hbb20.CountryCodePicker;

import java.util.concurrent.TimeUnit;

import projet.ensa.projetmobile.connexion.NetworkChangeListenner;

public class SendSMSActivity extends AppCompatActivity {
    NetworkChangeListenner networkChangeListenner = new NetworkChangeListenner();
    CountryCodePicker ccp;
    private EditText inputNm;
    private String imei;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_send_s_m_s);
        inputNm = findViewById(R.id.inputNumber);
        Button buttonGetOtp = findViewById(R.id.getNumber);
        final ProgressBar progressBar = findViewById(R.id.progressBar);
        ccp = (CountryCodePicker) findViewById(R.id.ccp);
        ccp.registerCarrierNumberEditText(inputNm);
        imei=getIntent().getStringExtra("imei");
        buttonGetOtp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!validatePhoneNumber()) {

                    return;

                }
                progressBar.setVisibility(View.VISIBLE);
                buttonGetOtp.setVisibility(View.INVISIBLE);

                PhoneAuthProvider.getInstance().verifyPhoneNumber("+212" + inputNm.getText().toString(),
                        60,
                        TimeUnit.SECONDS,
                        SendSMSActivity.this,
                        new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {


                            @Override
                            public void onVerificationCompleted(@NonNull PhoneAuthCredential phoneAuthCredential) {
                                progressBar.setVisibility(View.GONE);
                                buttonGetOtp.setVisibility(View.VISIBLE);
                            }

                            @Override
                            public void onVerificationFailed(@NonNull FirebaseException e) {
                                progressBar.setVisibility(View.GONE);
                                buttonGetOtp.setVisibility(View.VISIBLE);
                                Toast.makeText(SendSMSActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();

                            }

                            @Override
                            public void onCodeSent(@NonNull String verificationId, @NonNull PhoneAuthProvider.ForceResendingToken forceResendingToken) {
                                progressBar.setVisibility(View.GONE);
                                buttonGetOtp.setVisibility(View.VISIBLE);
                                Intent intent = new Intent(getApplicationContext(), VerifySMSActivity.class);
                                intent.putExtra("mobile", inputNm.getText().toString());
                                intent.putExtra("verificationId", verificationId);
                                intent.putExtra("imei",imei);
                                startActivity(intent);
                            }
                        });
            }
        });


    }

    @Override
    protected void onStart() {
        IntentFilter intentFilter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
        registerReceiver(networkChangeListenner, intentFilter);
        super.onStart();
    }

    @Override
    protected void onStop() {
        unregisterReceiver(networkChangeListenner);
        super.onStop();
    }

    private boolean validatePhoneNumber() {
        if (inputNm.getText().toString().isEmpty()) {
            inputNm.setError("Champ ne peut pas être vide");
            return false;
        } else if (ccp.isValidFullNumber() == true) {
            inputNm.setError(null);

            return true;
        } else {
            inputNm.setError("invalid Number");
            return false;
        }

    }
}