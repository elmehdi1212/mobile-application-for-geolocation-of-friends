package projet.ensa.projetmobile;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.getbase.floatingactionbutton.FloatingActionButton;
import com.quickblox.auth.QBAuth;
import com.quickblox.auth.session.BaseService;
import com.quickblox.auth.session.QBSession;
import com.quickblox.auth.session.QBSettings;
import com.quickblox.chat.QBChatService;
import com.quickblox.chat.QBRestChatService;
import com.quickblox.chat.model.QBChatDialog;
import com.quickblox.core.QBEntityCallback;
import com.quickblox.core.exception.BaseServiceException;
import com.quickblox.core.exception.QBResponseException;
import com.quickblox.core.request.QBRequestBuilder;
import com.quickblox.core.request.QBRequestGetBuilder;
import com.quickblox.users.QBUsers;
import com.quickblox.users.model.QBUser;

import java.util.ArrayList;

import projet.ensa.projetmobile.adapters.ChatDialogsAdapter;
import projet.ensa.projetmobile.common.Common;
import projet.ensa.projetmobile.holder.QBUsersHolder;

public class ChatDialogActivity extends AppCompatActivity {
    private com.google.android.material.floatingactionbutton.FloatingActionButton floatingActionButton;
    private  ListView lstChatDialogs;
    public final String APP_ID="88787";
    public final String AUTH_KEY="n3zpwLhz8zzb2NX";
    public final String AUTH_SECRET="SZJEN8RSYmhU3M5";
    public final String ACCOUNT_KEY="1QUWLB4YaWWHuCP2RTYU";

    @Override
    protected void onResume() {
        super.onResume();
        loadChatDialog();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_dialog);
        lstChatDialogs=this.<ListView> findViewById(R.id.lsChatDialog);
        initializeFramework();
        registerSession();
        createSessionForChat();
        loadChatDialog();

        floatingActionButton=(com.google.android.material.floatingactionbutton.FloatingActionButton)findViewById(R.id.chatDialog_addUser);
        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(ChatDialogActivity.this,ListUserActivity.class);
                startActivity(intent);
            }
        });
        lstChatDialogs.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                QBChatDialog qbChatDialog=(QBChatDialog) lstChatDialogs.getAdapter().getItem(position);
                Intent intent=new Intent(ChatDialogActivity.this,ChatMessageActivity.class);
                intent.putExtra(Common.DIALOG_EXTRA,qbChatDialog);
                startActivity(intent);
            }
        });

    }

    private  void loadChatDialog() {
        QBRequestGetBuilder requestBuilder=new QBRequestGetBuilder();
        requestBuilder.setLimit(100);
        QBRestChatService.getChatDialogs(null,requestBuilder).performAsync(new QBEntityCallback<ArrayList<QBChatDialog>>() {
            @Override
            public void onSuccess(ArrayList<QBChatDialog> qbChatDialogs, Bundle bundle) {
                ChatDialogsAdapter adapter=new ChatDialogsAdapter(getBaseContext(),qbChatDialogs);
                lstChatDialogs.setAdapter(adapter);
                adapter.notifyDataSetChanged();

            }

            @Override
            public void onError(QBResponseException e) {
                Log.e("ERROR",e.getMessage());

            }
        });
    }

    private void createSessionForChat() {
        ProgressDialog mDialog=new ProgressDialog(ChatDialogActivity.this);
        mDialog.setMessage("Attend  ...");
        mDialog.setCanceledOnTouchOutside(false);
        mDialog.show();
        String user="elmehdi";
        String password="Azerti1234";
        QBUsers.getUsers(null).performAsync(new QBEntityCallback<ArrayList<QBUser>>() {
            @Override
            public void onSuccess(ArrayList<QBUser> qbUsers, Bundle bundle) {
                QBUsersHolder.getInstance().putUsers(qbUsers);
               

            }

            @Override
            public void onError(QBResponseException e) {

            }
        });
        final QBUser qbUser=new QBUser(user,password);
        QBAuth.createSession(qbUser).performAsync(new QBEntityCallback<QBSession>() {
            @Override
            public void onSuccess(QBSession qbSession, Bundle bundle) {
                qbUser.setId(qbSession.getUserId());
                try {
                    qbUser.setPassword(BaseService.getBaseService().getToken());
                } catch (BaseServiceException e) {
                    e.printStackTrace();
                }

                QBChatService.getInstance().login(qbUser, new QBEntityCallback() {
                    @Override
                    public void onSuccess(Object o, Bundle bundle) {
                        mDialog.dismiss();
                    }

                    @Override
                    public void onError(QBResponseException e) {
                         Log.e("ERROR",""+e.getMessage());
                    }
                });

            }

            @Override
            public void onError(QBResponseException e) {

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