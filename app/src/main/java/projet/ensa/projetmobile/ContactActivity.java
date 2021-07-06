package projet.ensa.projetmobile;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.ContactsContract;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.i18n.phonenumbers.PhoneNumberUtil;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import projet.ensa.projetmobile.adapters.ContactsAdapter;
import projet.ensa.projetmobile.connexion.RetrofitInstance;
import projet.ensa.projetmobile.models.Contact;
import projet.ensa.projetmobile.models.User;
import projet.ensa.projetmobile.service.DataService;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ContactActivity extends AppCompatActivity {
    private List<User> contactsList;
    private List<User> listContacts;
    private User u;
    private RecyclerView rvContacts;
    private Button next;
    DataService service = RetrofitInstance.getInstance().create(DataService.class);
    PhoneNumberUtil pnu = PhoneNumberUtil.getInstance();
    Set<String> phones;
    private String imei;
    private int id;
    private SharedPreferences.Editor editor;
    private SwipeRefreshLayout swipeRefreshLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact);
        SharedPreferences mySharedPreferences = this.getSharedPreferences("MYPREFERENCENAME", Context.MODE_PRIVATE);
        editor = mySharedPreferences.edit();
        imei = mySharedPreferences.getString("IMEI", "");
        swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipeContainer);
        rvContacts = findViewById(R.id.list_contact);
        next = findViewById(R.id.nextBtn);
        contactsList = new ArrayList<>();
        listContacts = new ArrayList<>();
        phones = new HashSet<>();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && checkSelfPermission(Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.READ_CONTACTS}, 1);

        } else {
            getContacts();
            for (String p : phones) {
                contactsList.add(new User("mehdi", p));
            }
            Call<List<User>> users = service.getAllUsers();
            users.enqueue(new Callback<List<User>>() {
                @Override
                public void onResponse(Call<List<User>> call, Response<List<User>> response) {
                    List<User> userM = response.body();
                    for (User u : userM) {
                        if (u.getImei().equals(imei)) {
                            editor.putInt("ID", u.getId());
                            editor.apply();
                        }
                    }

                    for (User user : userM) {
                        for (User user2 : contactsList) {
                            PhoneNumberUtil.MatchType mt = pnu.isNumberMatch(user.getTelephone(), user2.getTelephone());
                            if (mt == PhoneNumberUtil.MatchType.NSN_MATCH || mt == PhoneNumberUtil.MatchType.EXACT_MATCH) {
                                listContacts.add(user);
                            }

                        }
                    }
                    ContactsAdapter contactsAdapter = new ContactsAdapter(getApplicationContext(), mySharedPreferences.getInt("ID", 0), listContacts);
                    rvContacts.setAdapter(contactsAdapter);
                    rvContacts.setLayoutManager(new LinearLayoutManager(getApplicationContext()));

                }

                @Override
                public void onFailure(Call<List<User>> call, Throwable t) {

                }
            });
        }
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                Intent refresh = new Intent(ContactActivity.this, ContactActivity.class);
                finish();
                overridePendingTransition(0, 0);
                startActivity(refresh);
                overridePendingTransition(0, 0);
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        swipeRefreshLayout.setRefreshing(false);
                    }
                }, 4);


            }
        });
        swipeRefreshLayout.setColorSchemeColors(getResources().getColor(android.R.color.holo_blue_light));
        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ContactActivity.this, MapActivity.class);
                startActivity(intent);
            }
        });
    }

    public void getContacts() {
        String lastName = "";
        Cursor cursor = getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null, null, null, null);
        while (cursor.moveToNext()) {
            String name = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME_PRIMARY));
            String mobile = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
            String phone = mobile.replaceAll("\\s", "");
            String p = phone.replaceAll("-", "");
            if (!lastName.contains(name)) {
                lastName = name;
                phones.add(p);

            }


        }
        cursor.close();

    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 1) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                getContacts();
            }
        }
    }


}