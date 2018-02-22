package Adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.github.siyamed.shapeimageview.RoundedImageView;
import com.squareup.picasso.Picasso;

import java.util.List;

import Models.User;
import ssapps.com.datingapp.RecyclerViewClickListener;

public class OnlineAdapter extends RecyclerView.Adapter<OnlineAdapter.MyViewHolder{

    private List<User> users;
    private Context context;
    private RecyclerViewClickListener mListener;

    public OnlineAdapter(List<User> users, Context context, RecyclerViewClickListener mListener) {
        this.users = users;
        this.context = context;
        this.mListener = mListener;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.online_row, parent, false);

        return new MyViewHolder(itemView,mListener);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        User user = users.get(position);

        if ()
    }

    @Override
    public int getItemCount() {
        return users.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{


        public ImageView online_offline_image;
        public RoundedImageView profile_image;
        public TextView userName,online_offline_text;


        public MyViewHolder(View view,RecyclerViewClickListener listener){
            super(view);

            online_offline_image = (ImageView)view.findViewById(R.id.online_offline_image);
            profile_image = (RoundedImageView)view.findViewById(R.id.profile_image);
            userName = (TextView)view.findViewById(R.id.name_age_tv);
            online_offline_text = (TextView)view.findViewById(R.id.online_offline_tv);

            mListener =listener;
            view.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            mListener.onClick(view,getAdapterPosition());
        }
    }

}
