package Adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.github.siyamed.shapeimageview.RoundedImageView;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import Models.Message;
import Models.MessagesSorting;
import Models.User;
import Util.Prefs;
import ssapps.com.datingapp.R;
import ssapps.com.datingapp.RecyclerViewClickListener;

public class ChatListingAdapter extends RecyclerView.Adapter<ChatListingAdapter.MyViewHolder> {

    private Context context;
    private RecyclerViewClickListener mListener;
    private List<MessagesSorting> messages = new ArrayList<>();
    private Prefs prefs;
    //private List<User> users = new ArrayList<>();

    public ChatListingAdapter(Context context,List<MessagesSorting> messages,RecyclerViewClickListener listener){
        this.context = context;
        this.messages = messages;
     //   this.users = users;
        this.mListener = listener;
        prefs = new Prefs(context);
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.chat_listing_row, parent, false);

        return new MyViewHolder(itemView,mListener);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {

        MessagesSorting message = messages.get(position);

        User user = new User();

        if (message.getMessage_from().equals(prefs.getname())){
            Log.v("user to find",message.getMessage_to());
            user = User.find(User.class,"username = ?",message.getMessage_to()).get(0);
        } else if (message.getMessage_to().equals(prefs.getname())){
            user = User.find(User.class,"username = ?",message.getMessage_from()).get(0);
        }

        if (user.getHasPicture().equals("Yes")){
            Picasso.with(context).load(user.getPhotourl()).into(holder.profile_image);
        }

        holder.name_age_tv.setText(user.getUsername());
        holder.last_message_tv.setText(message.getChat_message());
        holder.last_message_time_tv.setText(getTime(message.getTime()));

        if (user.getIsOnline().equals("online")){
            holder.online_offline_tv.setText("Online");
            holder.online_offline_image.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_online_dot));
        } else  if (user.getIsOnline().equals("offline")){
            holder.online_offline_image.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_offline_dor));
            holder.online_offline_tv.setText("Offline");
        }

//        User user = User.find(User.class,"username = ?",message.getMessage_from()).get(0);
//        if (user.getHasPicture().equals("Yes")){
//            Picasso.with(context).load(user.getPhotourl()).into(holder.profile_image);
//        }
//        if (message.getMessage_from().equals())
//        holder.name_age_tv.setText(user.getUsername()+","+user.getGender_self());
//        holder.last_message_tv.setText(message.getChat_message());
//        if (user.getIsOnline().equals("Yes")){
//            holder.online_offline_tv.setText("online");
//           // holder.online_offline_image.setImageDrawable(R.drawable.ic_online_dot);
//            holder.online_offline_image.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_online_dot));
//        } else if (user.getIsOnline().equals("No")){
//            holder.online_offline_tv.setText("offline");
//          //  holder.online_offline_image.setImageDrawable(R.drawable.ic_offline_dor);
//            holder.online_offline_image.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_offline_dor));
//        }
//
//        holder.last_message_time_tv.setText(getTime(message.getTime()));


    }

    private String getTime(Long time) {
        Long difference = Calendar.getInstance().getTimeInMillis() - time;

        int years = (int)(difference/(365*24*60*60*1000));
        if (years != 0){
            return String.valueOf(years)+"years ago";
        } else {
            int months = (int)(difference/(30*24*60*60*1000));
            if (months != 0){
                return String.valueOf(months)+"months ago";
            } else {
                int days = (int)(difference/(24*60*60*1000));
                if (days != 0){
                    return String.valueOf(days)+"days ago";
                } else {
                    int hours = (int)(difference/(60*60*1000));
                    if (hours != 0){
                        return String.valueOf(hours)+"hours ago";
                    } else {
                        int minutes = (int)(difference/(60*1000));
                        if (minutes != 0){
                            return String.valueOf(minutes)+"minutes ago";
                        } else {
                            return "Just now";
                        }
                    }
                }
            }
        }


    }

    @Override
    public int getItemCount() {
        return messages.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        public TextView name_age_tv,online_offline_tv,last_message_tv,last_message_time_tv;
        public ImageView online_offline_image;
        public RoundedImageView profile_image;

        public MyViewHolder(View view, RecyclerViewClickListener listener){
            super(view);

            profile_image = (RoundedImageView)view.findViewById(R.id.profile_image_friend);
            name_age_tv = (TextView)view.findViewById(R.id.name_age_friend_tv);
            online_offline_tv = (TextView)view.findViewById(R.id.online_offline_tv);
            online_offline_image = (ImageView) view.findViewById(R.id.online_offline_image);
            last_message_tv = (TextView)view.findViewById(R.id.last_message_tv);
            last_message_time_tv = (TextView)view.findViewById(R.id.last_message_time_tv);


            mListener = listener;
            view.setOnClickListener(this);
        }
       @Override
        public void onClick(View view) {
            mListener.onClick(view,getAdapterPosition());
        }
    }

}