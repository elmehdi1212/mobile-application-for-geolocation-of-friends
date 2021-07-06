package projet.ensa.projetmobile.friends;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.i18n.phonenumbers.PhoneNumberUtil;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import projet.ensa.projetmobile.MenuActivity;
import projet.ensa.projetmobile.R;
import projet.ensa.projetmobile.adapters.ContactsAdapter;
import projet.ensa.projetmobile.connexion.RetrofitInstance;
import projet.ensa.projetmobile.models.User;
import projet.ensa.projetmobile.service.DataService;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SendInvitationActivity extends AppCompatActivity {
    private List<User> contactsList;
    private List<User> listContacts;
    private RecyclerView rvAddFriend;
    DataService service = RetrofitInstance.getInstance().create(DataService.class);
    PhoneNumberUtil pnu = PhoneNumberUtil.getInstance();
    Set<String> phones;
    private String imei;
    private int id;
    private SharedPreferences.Editor editor;
    private TextView icon;
    private TextView textIcon;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_send_invitation);
        SharedPreferences mySharedPreferences = this.getSharedPreferences("MYPREFERENCENAME", Context.MODE_PRIVATE);
        editor = mySharedPreferences.edit();
        imei= mySharedPreferences.getString("IMEI", "");
        id= mySharedPreferences.getInt("ID", 0);
        rvAddFriend = findViewById(R.id.rvAddFriends);
        contactsList = new ArrayList<>();
        listContacts = new ArrayList<>();
        icon = findViewById(R.id.iconNothingFound);
        textIcon = findViewById(R.id.nothingFound);
        phones = new HashSet<>();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && checkSelfPermission(Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.READ_CONTACTS}, 1);

        }
        else {
            getContacts();
            for (String p : phones) {
                contactsList.add(new User("mehdi", p));
            }


            Call<List<User>> users = service.getAllUser(id);
            users.enqueue(new Callback<List<User>>() {
                @Override
                public void onResponse(Call<List<User>> call, Response<List<User>> response) {
                    List<User> userM = response.body();
                    for (User user : userM) {
                        for (User user2 : contactsList) {
                            PhoneNumberUtil.MatchType mt = pnu.isNumberMatch(user.getTelephone(), user2.getTelephone());
                            if (mt == PhoneNumberUtil.MatchType.NSN_MATCH || mt == PhoneNumberUtil.MatchType.EXACT_MATCH) {
                                listContacts.add(user);
                            }

                        }
                    }
                    if (listContacts.size() == 0) {
                        icon.setVisibility(View.VISIBLE);
                        textIcon.setVisibility(View.VISIBLE);
                    }
                   ContactsAdapter addFriendAdapter = new ContactsAdapter(getApplicationContext(),id, listContacts);
                    rvAddFriend.setAdapter(addFriendAdapter);
                    rvAddFriend.setLayoutManager(new LinearLayoutManager(getApplicationContext()));

                }

                @Override
                public void onFailure(Call<List<User>> call, Throwable t) {

                }
            });


        }
    }

    public  void goBack(View view){
        Intent intent=new Intent(SendInvitationActivity.this, MenuActivity.class);
        startActivity(intent);
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