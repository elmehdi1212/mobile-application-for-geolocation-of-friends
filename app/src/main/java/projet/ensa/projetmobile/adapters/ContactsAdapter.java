package projet.ensa.projetmobile.adapters;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.recyclerview.widget.RecyclerView;

import org.w3c.dom.Text;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Random;

import projet.ensa.projetmobile.R;
import projet.ensa.projetmobile.connexion.RetrofitInstance;
import projet.ensa.projetmobile.models.Contact;
import projet.ensa.projetmobile.models.Friend;
import projet.ensa.projetmobile.models.FriendId;
import projet.ensa.projetmobile.models.User;
import projet.ensa.projetmobile.service.DataService;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ContactsAdapter extends RecyclerView.Adapter<ContactsAdapter.ViewHolder> {
    private List<User> mContacts;
    private Context context;
    DataService service = RetrofitInstance.getInstance().create(DataService.class);
    SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
    Date date = new Date();
    String d=formatter.format(date);
    private int idCurrentUser;
    public ContactsAdapter(Context context,int id, List<User> contacts) {
        this.context = context;
        this.idCurrentUser=id;
        mContacts = contacts;

    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public TextView name;
        public TextView number;
        public TextView sy;
        public Button addFriend;
        public  Button sendFriend;

        public ViewHolder(View itemView) {
            super(itemView);
            name = (TextView) itemView.findViewById(R.id.contact_name);
            number = (TextView) itemView.findViewById(R.id.contact_number);
            sy = (TextView) itemView.findViewById(R.id.symbole);
            addFriend = (Button) itemView.findViewById(R.id.addFriend);
            sendFriend=(Button) itemView.findViewById(R.id.addFriend2);




        }


    }

    @Override
    public ContactsAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        View contactView = inflater.inflate(R.layout.items_contacts, parent, false);
        ViewHolder viewHolder = new ViewHolder(contactView);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ContactsAdapter.ViewHolder holder, int position) {

        User contact = mContacts.get(position);
        TextView textView = holder.name;
        textView.setText(contact.getNom());
        TextView textView2 = holder.number;
        textView2.setText(contact.getTelephone());
        TextView bt = holder.sy;
        bt.setText(String.valueOf(contact.getNom().charAt(0)));
        Random r = new Random();
        int red = r.nextInt(255 - 0 + 1) + 0;
        int green = r.nextInt(255 - 0 + 1) + 0;
        int blue = r.nextInt(255 - 0 + 1) + 0;
        ItemAnimation.animateFadeIn(holder.itemView, position);
        GradientDrawable draw = new GradientDrawable();
        draw.setShape(GradientDrawable.OVAL);
        draw.setColor(Color.rgb(red, green, blue));
        bt.setBackground(draw);

        holder.addFriend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FriendId id=new FriendId();
                id.setUser1Id(idCurrentUser);
                id.setUser2Id(contact.getId());
                User u=new User(idCurrentUser);
                Friend f=new Friend(id,u,contact,d,1);
                Call<Friend> callFriend = service.createRequestFriend(f);
                callFriend.enqueue(new Callback<Friend>() {
                    @Override
                    public void onResponse(Call<Friend> call, Response<Friend> response) {

                    }

                    @Override
                    public void onFailure(Call<Friend> call, Throwable t) {

                    }
                });

                holder.addFriend.setVisibility(View.GONE);
                holder.sendFriend.setVisibility(View.VISIBLE);

            }
        });

    }

    @Override
    public int getItemCount() {
        return mContacts.size();
    }

    public void clear() {
        mContacts.clear();
        notifyDataSetChanged();
    }

    public void addAll(List<User> list) {
        mContacts.addAll(list);
        notifyDataSetChanged();
    }



}
