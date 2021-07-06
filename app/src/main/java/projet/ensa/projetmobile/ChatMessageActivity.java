package projet.ensa.projetmobile;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.ContextMenu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Toast;

import com.quickblox.chat.QBChat;
import com.quickblox.chat.QBChatService;
import com.quickblox.chat.QBIncomingMessagesManager;
import com.quickblox.chat.QBRestChatService;
import com.quickblox.chat.exception.QBChatException;
import com.quickblox.chat.listeners.QBChatDialogMessageListener;
import com.quickblox.chat.model.QBChatDialog;
import com.quickblox.chat.model.QBChatMessage;
import com.quickblox.chat.request.QBMessageGetBuilder;
import com.quickblox.chat.request.QBMessageUpdateBuilder;
import com.quickblox.core.QBEntityCallback;
import com.quickblox.core.exception.QBResponseException;

import org.jivesoftware.smack.SmackException;

import java.util.ArrayList;

import projet.ensa.projetmobile.adapters.ChatMessageAdapter;
import projet.ensa.projetmobile.common.Common;
import projet.ensa.projetmobile.holder.QBChatMessagesHolder;

public class ChatMessageActivity extends AppCompatActivity {
    QBChatDialog qbChatDialog;
    ListView lsChatMessage;
    ImageButton submitButton;
    EditText  edtContent;
    ChatMessageAdapter adapter;
    int contextMenuIndexClicked=-1;
    boolean isEditMode =false;
    QBChatMessage editMessage;

    @Override
    public boolean onContextItemSelected(@NonNull MenuItem item) {
        AdapterView.AdapterContextMenuInfo  info=(AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        contextMenuIndexClicked=info.position;
        switch (item.getItemId()){
            case R.id.chat_message_update:
                updateMessage();
                break;
            case  R.id.chat_message_delete:
                deleteMessage();
                break;
        }
        return true;
    }



    private void deleteMessage() {
        ProgressDialog mDialog=new ProgressDialog(ChatMessageActivity.this);
        mDialog.setMessage("Attend  ...");
        mDialog.show();
        editMessage=QBChatMessagesHolder.getInstance().getMessagesByDialiogId(qbChatDialog.getDialogId())
                .get(contextMenuIndexClicked);
        QBRestChatService.deleteMessage(editMessage.getId(),false).performAsync(new QBEntityCallback<Void>() {
            @Override
            public void onSuccess(Void aVoid, Bundle bundle) {
                retrieveMessage();
                mDialog.dismiss();
            }

            @Override
            public void onError(QBResponseException e) {
                Toast.makeText(getApplicationContext(),""+e.getMessage(),Toast.LENGTH_LONG).show();

            }
        });
    }

    private void updateMessage() {
        editMessage=QBChatMessagesHolder.getInstance().getMessagesByDialiogId(qbChatDialog.getDialogId())
        .get(contextMenuIndexClicked);
        edtContent.setText(editMessage.getBody());
        isEditMode=true;

    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        getMenuInflater().inflate(R.menu.chat_message_context,menu);

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_message);
        initViews();
        initChatDialogs();
        retrieveMessage();
        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!isEditMode) {
                    QBChatMessage chatMessage = new QBChatMessage();
                    chatMessage.setBody(edtContent.getText().toString());
                    chatMessage.setSenderId(QBChatService.getInstance().getUser().getId());
                    chatMessage.setSaveToHistory(true);
                    try {
                        qbChatDialog.sendMessage(chatMessage);
                    } catch (SmackException.NotConnectedException e) {
                        e.printStackTrace();
                    }
                    QBChatMessagesHolder.getInstance().putMessage(qbChatDialog.getDialogId(), chatMessage);
                    ArrayList<QBChatMessage> messages = QBChatMessagesHolder.getInstance().getMessagesByDialiogId(qbChatDialog.getDialogId());
                    adapter = new ChatMessageAdapter(getBaseContext(), messages);
                    lsChatMessage.setAdapter(adapter);
                    adapter.notifyDataSetChanged();
                    edtContent.setText("");
                    edtContent.setFocusable(true);

            }
                else{
                    ProgressDialog updateDialog=new ProgressDialog(ChatMessageActivity.this);
                    updateDialog.setMessage("Attend  ...");
                    updateDialog.show();
                    QBMessageUpdateBuilder messageUpdateBuilder=new QBMessageUpdateBuilder();
                    messageUpdateBuilder.updateText(edtContent.getText().toString()).markDelivered().markRead();
                    QBRestChatService.updateMessage(editMessage.getId(),qbChatDialog.getDialogId(),messageUpdateBuilder)
                            .performAsync(new QBEntityCallback<Void>() {
                                @Override
                                public void onSuccess(Void aVoid, Bundle bundle) {
                                    retrieveMessage();
                                    isEditMode=false;
                                    updateDialog.dismiss();
                                    edtContent.setText("");
                                    edtContent.setFocusable(true);
                                }

                                @Override
                                public void onError(QBResponseException e) {
                                    Toast.makeText(getApplicationContext(),""+e.getMessage(),Toast.LENGTH_LONG).show();
                                }
                            });
                }
            }


        });
    }

    private void retrieveMessage() {
        QBMessageGetBuilder messageGetBuilder=new QBMessageGetBuilder();
        messageGetBuilder.setLimit(500);
        if(qbChatDialog !=null){
            QBRestChatService.getDialogMessages(qbChatDialog,messageGetBuilder).performAsync(new QBEntityCallback<ArrayList<QBChatMessage>>() {
                @Override
                public void onSuccess(ArrayList<QBChatMessage> qbChatMessages, Bundle bundle) {
                    QBChatMessagesHolder.getInstance().putMessages(qbChatDialog.getDialogId(),qbChatMessages);
                    adapter=new ChatMessageAdapter(getBaseContext(),qbChatMessages);
                    lsChatMessage.setAdapter(adapter);
                    adapter.notifyDataSetChanged();
                }

                @Override
                public void onError(QBResponseException e) {

                }
            });
        }
    }

    private void initChatDialogs() {
        qbChatDialog=(QBChatDialog) getIntent().getSerializableExtra(Common.DIALOG_EXTRA);
        qbChatDialog.initForChat(QBChatService.getInstance());
        QBIncomingMessagesManager incomingMessagesManager=QBChatService.getInstance().getIncomingMessagesManager();
        incomingMessagesManager.addDialogMessageListener(new QBChatDialogMessageListener() {
            @Override
            public void processMessage(String s, QBChatMessage qbChatMessage, Integer integer) {
            }

            @Override
            public void processError(String s, QBChatException e, QBChatMessage qbChatMessage, Integer integer) {

            }
        });
        qbChatDialog.addMessageListener(new QBChatDialogMessageListener() {
            @Override
            public void processMessage(String s, QBChatMessage qbChatMessage, Integer integer) {
                QBChatMessagesHolder.getInstance().putMessage(qbChatMessage.getDialogId(),qbChatMessage);
                ArrayList<QBChatMessage> messages=QBChatMessagesHolder.getInstance().getMessagesByDialiogId(qbChatMessage.getDialogId());
                adapter=new ChatMessageAdapter(getBaseContext(),messages);
                lsChatMessage.setAdapter(adapter);
                adapter.notifyDataSetChanged();

            }

            @Override
            public void processError(String s, QBChatException e, QBChatMessage qbChatMessage, Integer integer) {
                Log.e("ERROR",e.getMessage());
            }
        });
    }

    private void initViews() {
        lsChatMessage=(ListView) findViewById(R.id.list_of_message);
        submitButton=(ImageButton) findViewById(R.id.send_button);
        edtContent=(EditText) findViewById(R.id.edt_content);

        registerForContextMenu(lsChatMessage);
    }
}