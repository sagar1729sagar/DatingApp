package Adapters;

import android.app.Fragment;
import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.github.siyamed.shapeimageview.RoundedImageView;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

import java.util.List;

import Models.User;
import Util.Prefs;
import cn.pedant.SweetAlert.SweetAlertDialog;
import ssapps.com.datingapp.R;


/**
 * Created by sagar on 16/03/18.
 */

public class FavouritesAdapter extends RecyclerView.Adapter<FavouritesAdapter.MyViewHolder>{

    private Context context;
    private List<User> users;
    private DatabaseReference mDatabase;
    private Prefs prefs;

    public FavouritesAdapter(Context context,List<User> users){
        this.context = context;
        this.users = users;
        prefs = new Prefs(context);
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.friends_list_row, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, final int position) {

        holder.name.setText(users.get(position).getUsername()+","+users.get(position).getAge_self());
        if (users.get(position).getHasPicture().equals("Yes")){
            Picasso.with(context).load(users.get(position).getPhotourl()).placeholder(context.getResources().getDrawable(R.drawable.fb)).into(holder.profile_image);
        }

        if (users.get(position).getIsOnline().equals("Yes")){
            holder.status_text.setText("Online");
            holder.status_image.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_online_dot));
        } else {
            holder.status_text.setText("Offline");
            holder.status_image.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_offline_dor));
        }
        holder.deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.v("Delete Pressed for",users.get(position).getUsername());
                mDatabase = FirebaseDatabase.getInstance().getReference();
                mDatabase.child(prefs.getname()).child("Friends").child(users.get(position).getUsername()).removeValue(new DatabaseReference.CompletionListener() {
                    @Override
                    public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                        if (databaseError != null){
                            SweetAlertDialog error = new SweetAlertDialog(context,SweetAlertDialog.ERROR_TYPE);
                            error.setTitle("Error removing friend");
                            error.setContentText(databaseError.getMessage()+"\n Please try again");
                            error.show();
                        } else {
                            Toast.makeText(context,"Friend removed successfully",Toast.LENGTH_LONG).show();
                            users.remove(position);
                            notifyDataSetChanged();
                        }
                    }
                });
            }
        });

    }

    @Override
    public int getItemCount() {
        return users.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder{

        public TextView name,status_text;
        public Button deleteButton;
        public RoundedImageView profile_image;
        public ImageView status_image;

        public MyViewHolder(View itemView) {
            super(itemView);
            name = (TextView)itemView.findViewById(R.id.content);
            profile_image = (RoundedImageView)itemView.findViewById(R.id.profile_image);
            deleteButton = (Button)itemView.findViewById(R.id.delete_button);
            status_image = (ImageView)itemView.findViewById(R.id.online_offline_image);
            status_text = (TextView)itemView.findViewById(R.id.online_offline_text);

//            b1 = (Button)itemView.findViewById(R.id.btnTop);
//            b2 = (Button)itemView.findViewById(R.id.btnDelete);
//            b3 = (Button)itemView.findViewById(R.id.btnUnRead);
        }
    }

}
