package projet.ensa.projetmobile;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.kaopiz.kprogresshud.KProgressHUD;
import com.quickblox.auth.QBAuth;
import com.quickblox.auth.session.QBSession;
import com.quickblox.auth.session.QBSettings;
import com.quickblox.chat.QBChatService;
import com.quickblox.chat.QBRestChatService;
import com.quickblox.chat.model.QBChatDialog;
import com.quickblox.chat.model.QBDialogType;
import com.quickblox.chat.utils.DialogUtils;
import com.quickblox.core.QBEntityCallback;
import com.quickblox.core.exception.QBResponseException;
import com.quickblox.users.QBUsers;
import com.quickblox.users.model.QBUser;

import java.util.ArrayList;

import projet.ensa.projetmobile.adapters.ListUsersAdapter;
import projet.ensa.projetmobile.common.Common;
import projet.ensa.projetmobile.holder.QBUsersHolder;

public class ListUserActivity extends AppCompatActivity {
    private ListView lstUsers;
    private Button createChat;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_user);

        retrieveAllUser();
        lstUsers=(ListView) findViewById(R.id.lstUsers);
        lstUsers.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
        createChat=(Button) findViewById(R.id.btn_create_chat);
        createChat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int countChoice=lstUsers.getCount();
                if(lstUsers.getCheckedItemPositions().size()==1){
                    createChatPrivateChat(lstUsers.getCheckedItemPositions());

                }
                else if(lstUsers.getCheckedItemPositions().size()>1){
                    createGroupChat(lstUsers.getCheckedItemPositions());
                }
                else
                    Toast.makeText(ListUserActivity.this,"Please select friend to chat",Toast.LENGTH_LONG).show();
            }
        });

    }

    private void createGroupChat(SparseBooleanArray checkedItemPositions) {
        final KProgressHUD mDialog = KProgressHUD.create(ListUserActivity.this)
                .setStyle(KProgressHUD.Style.SPIN_INDETERMINATE)
                // .setLabel("Please wait")
                .setDetailsLabel("Attend...")
                .setCancellable(true)
                .setAnimationSpeed(2)
                .setDimAmount(0.5f)
                .show();
      /*  ProgressDialog mDialog=new ProgressDialog(ListUserActivity.this);
        mDialog.setMessage("Attend ....");
        mDialog.setCanceledOnTouchOutside(false);
        mDialog.show();*/
        int countChoice=lstUsers.getCount();
        ArrayList<Integer> occupantIdsList=new ArrayList<Integer>();
        for(int i=0;i<countChoice;i++){
            if(checkedItemPositions.get(i)){
                QBUser user=(QBUser) lstUsers.getItemAtPosition(i);
                occupantIdsList.add(user.getId());
            }
        }
        QBChatDialog dialog=new QBChatDialog();
        dialog.setName(Common.createChatDialogName(occupantIdsList));
        dialog.setType(QBDialogType.GROUP);
        dialog.setOccupantsIds(occupantIdsList);

        QBRestChatService.createChatDialog(dialog).performAsync(new QBEntityCallback<QBChatDialog>() {
            @Override
            public void onSuccess(QBChatDialog qbChatDialog, Bundle bundle) {
                mDialog.dismiss();
                Toast.makeText(getBaseContext(),"Create Chat dialog succesfully",Toast.LENGTH_LONG).show();
                finish();
            }

            @Override
            public void onError(QBResponseException e) {
                Log.e("ERRROR",e.getMessage());


            }
        });
    }

    private void createChatPrivateChat(SparseBooleanArray checkedItemPositions) {
        ProgressDialog mDialog=new ProgressDialog(ListUserActivity.this);
        mDialog.setMessage("Attend ....");
        mDialog.setCanceledOnTouchOutside(false);
        mDialog.show();
        int countChoice=lstUsers.getCount();
        for(int i=0;i<countChoice;i++){
            if(checkedItemPositions.get(i)){
                QBUser user=(QBUser) lstUsers.getItemAtPosition(i);
                QBChatDialog dialog= DialogUtils.buildPrivateDialog(user.getId());
                QBRestChatService.createChatDialog(dialog).performAsync(new QBEntityCallback<QBChatDialog>() {
                    @Override
                    public void onSuccess(QBChatDialog qbChatDialog, Bundle bundle) {
                        mDialog.dismiss();
                        Toast.makeText(getBaseContext(),"Create private Chat dialog succesfully",Toast.LENGTH_LONG).show();
                        finish();
                    }

                    @Override
                    public void onError(QBResponseException e) {
                        Log.e("ERROR",e.getMessage());


                    }
                });
            }
        }

    }


    private void retrieveAllUser() {
        QBUsers.getUsers(null).performAsync(new QBEntityCallback<ArrayList<QBUser>>() {
            @Override
            public void onSuccess(ArrayList<QBUser> qbUsers, Bundle bundle) {
                QBUsersHolder.getInstance().putUsers(qbUsers);
                ArrayList<QBUser> qbUserWithoutCurrent=new ArrayList<QBUser>();
                for(QBUser user:qbUsers){
                    if(!user.getLogin().equals(QBChatService.getInstance().getUser().getLogin()))
                        qbUserWithoutCurrent.add(user);



                }

                ListUsersAdapter adapter=new ListUsersAdapter(getBaseContext(),qbUserWithoutCurrent);
                lstUsers.setAdapter(adapter);
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onError(QBResponseException e) {
                Log.e("ERROR",e.getMessage());

            }
        });
    }

}