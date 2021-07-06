package projet.ensa.projetmobile.adapters;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Random;

import projet.ensa.projetmobile.R;
import projet.ensa.projetmobile.connexion.RetrofitInstance;
import projet.ensa.projetmobile.models.Friend;
import projet.ensa.projetmobile.models.FriendId;
import projet.ensa.projetmobile.models.User;
import projet.ensa.projetmobile.service.DataService;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class InvitationsAdapter extends RecyclerView.Adapter<InvitationsAdapter.ViewHolder> {
    private List<User> invitations;
    private Context context;
    DataService service = RetrofitInstance.getInstance().create(DataService.class);
    SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
    Date date = new Date();
    String d=formatter.format(date);
    private int idCurrentUser;
    public InvitationsAdapter(Context context,int id, List<User> invitations) {
        this.context = context;
        this.invitations = invitations;
        this.idCurrentUser=id;

    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private TextView name;
        private TextView number;
        private TextView sy;
        private Button accepte;
        private CardView cardView;


        public ViewHolder(View itemView) {
            super(itemView);
            name = (TextView) itemView.findViewById(R.id.recieve_name);
            number = (TextView) itemView.findViewById(R.id.recieve_number);
            sy = (TextView) itemView.findViewById(R.id.symbole);
            accepte = (Button) itemView.findViewById(R.id.accepte);
            cardView=(CardView) itemView.findViewById(R.id.invit);

        }


    }

    @Override
    public InvitationsAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        View contactView = inflater.inflate(R.layout.items_friends, parent, false);
        ViewHolder viewHolder = new ViewHolder(contactView);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(InvitationsAdapter.ViewHolder holder, int position) {
        User us = invitations.get(position);
        TextView textView = holder.name;
        textView.setText(us.getNom());
        TextView textView2 = holder.number;
        textView2.setText(us.getTelephone());
        TextView bt = holder.sy;
        bt.setText(String.valueOf(us.getNom().charAt(0)));
        Random r = new Random();
        int red = r.nextInt(255 - 0 + 1) + 0;
        int green = r.nextInt(255 - 0 + 1) + 0;
        int blue = r.nextInt(255 - 0 + 1) + 0;
        ItemAnimation.animateFadeIn(holder.itemView, position);
        GradientDrawable draw = new GradientDrawable();
        draw.setShape(GradientDrawable.OVAL);
        draw.setColor(Color.rgb(red, green, blue));
        bt.setBackground(draw);
        holder.accepte.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FriendId id=new FriendId();
                id.setUser1Id(us.getId());
                id.setUser2Id(idCurrentUser);
                User u=new User(idCurrentUser);
                Friend f=new Friend(id,us,u,d,2);
                Call<Friend> callFriend = service.createRequestFriend(f);
                callFriend.enqueue(new Callback<Friend>() {
                    @Override
                    public void onResponse(Call<Friend> call, Response<Friend> response) {

                    }

                    @Override
                    public void onFailure(Call<Friend> call, Throwable t) {

                    }
                });
                holder.cardView.setVisibility(View.INVISIBLE);

            }
           });

            }

    @Override
    public int getItemCount() {
        return invitations.size();
    }
}