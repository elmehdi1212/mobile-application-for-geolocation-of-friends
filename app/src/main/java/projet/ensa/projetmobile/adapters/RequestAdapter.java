package projet.ensa.projetmobile.adapters;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;
import java.util.Random;

import projet.ensa.projetmobile.R;
import projet.ensa.projetmobile.connexion.RetrofitInstance;
import projet.ensa.projetmobile.friends.FriendActivity;
import projet.ensa.projetmobile.friends.FriendDetailsActivity;
import projet.ensa.projetmobile.models.Friend;
import projet.ensa.projetmobile.models.FriendId;
import projet.ensa.projetmobile.models.User;
import projet.ensa.projetmobile.service.DataService;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RequestAdapter extends RecyclerView.Adapter<RequestAdapter.ViewHolder> {
    private List<User> mContacts;
    private Context context;
    private String type;

    public RequestAdapter(Context context, List<User> contacts,String type) {
        this.context = context;
        this.mContacts = contacts;
        this.type=type;

    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private TextView name;
        private TextView number;
        private TextView sy;
        private Button sendFriend;
        private CardView cardView;



        public ViewHolder(View itemView) {
            super(itemView);
            name = (TextView) itemView.findViewById(R.id.requestName);
            number = (TextView) itemView.findViewById(R.id.requestPhone);
            sy = (TextView) itemView.findViewById(R.id.symbole);
            sendFriend = (Button) itemView.findViewById(R.id.sendRequest);
            cardView=(CardView) itemView.findViewById(R.id.carmdViewFriends);
            if(type.equals("friends")){
                sendFriend.setVisibility(View.INVISIBLE);
            }
            else {
                sendFriend.setVisibility(View.VISIBLE);
            }



        }


    }

    @Override
    public RequestAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        View contactView = inflater.inflate(R.layout.items_request, parent, false);
        ViewHolder viewHolder = new ViewHolder(contactView);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(RequestAdapter.ViewHolder holder, int position) {
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
        if(type.equals("friends")) {
            holder.cardView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent=new Intent(context, FriendDetailsActivity.class);
                    intent.putExtra("symbole", ""+contact.getNom().charAt(0));
                    intent.putExtra("name",""+contact.getPrenom()+" "+contact.getNom());
                    intent.putExtra("phone",""+contact.getTelephone());
                    intent.putExtra("id",""+contact.getId());
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    context.startActivity(intent);
                }
            });
        }



    }

    @Override
    public int getItemCount() {
        return mContacts.size();
    }


}
