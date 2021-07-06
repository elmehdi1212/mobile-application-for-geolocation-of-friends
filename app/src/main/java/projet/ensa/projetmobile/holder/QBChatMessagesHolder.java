package projet.ensa.projetmobile.holder;

import com.quickblox.chat.model.QBChatMessage;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class QBChatMessagesHolder {
    private static QBChatMessagesHolder instance;
    private HashMap<String, ArrayList<QBChatMessage>> qbChatMessageArray;
    public static  synchronized QBChatMessagesHolder getInstance(){
        QBChatMessagesHolder qbChatMessgesHolder;
        synchronized (QBChatMessagesHolder.class)
        {
            if(instance==null)
                instance=new QBChatMessagesHolder();
            qbChatMessgesHolder=instance;
        }
        return qbChatMessgesHolder;

    }
    private QBChatMessagesHolder(){
        this.qbChatMessageArray=new HashMap<>();
    }
    public void putMessages(String dialogId,ArrayList<QBChatMessage> qbChatMessages){
        this.qbChatMessageArray.put(dialogId,qbChatMessages);


    }
    public void putMessage(String dialogId,QBChatMessage qbChatMessage){
        List<QBChatMessage> lsResult=(List) this.qbChatMessageArray.get(dialogId);
        lsResult.add(qbChatMessage);
        ArrayList<QBChatMessage> lsAdded= new ArrayList<>();
        lsAdded.addAll(lsResult);
        putMessages(dialogId,lsAdded);
    }

    public ArrayList<QBChatMessage> getMessagesByDialiogId(String dialogId){
        return (ArrayList<QBChatMessage>) this.qbChatMessageArray.get(dialogId);
    }
}
