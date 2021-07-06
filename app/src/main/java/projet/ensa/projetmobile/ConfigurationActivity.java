package projet.ensa.projetmobile;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.google.android.material.textfield.TextInputLayout;

public class ConfigurationActivity extends AppCompatActivity {
    private TextInputLayout rayon;
    private  soup.neumorphism.NeumorphButton nextBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_configuration);
        rayon = findViewById(R.id.rayon);
        nextBtn=(soup.neumorphism.NeumorphButton)findViewById(R.id.nextBtn);

        nextBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i=new Intent(ConfigurationActivity.this,MapActivity.class);
                i.putExtra("rayon",Double.parseDouble(rayon.getEditText().getText().toString()));
                startActivity(i);
            }
        });
    }

    public void goBack(View v){
        Intent i=new Intent(ConfigurationActivity.this,MapActivity.class);
        startActivity(i);
    }
}