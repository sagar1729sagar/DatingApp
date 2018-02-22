package Adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import Models.SearchResults;
import ssapps.com.datingapp.RecyclerViewClickListener;

public class WhosNewAdapter extends RecyclerView.Adapter<WhosNewAdapter.MyViewHolder>{

    private List<SearchResults> results = new ArrayList<>();
    private Context context;
    private RecyclerViewClickListener mListener;

    public WhosNewAdapter(List<SearchResults> results, Context context, RecyclerViewClickListener mListener) {
        this.results = results;
        this.context = context;
        this.mListener = mListener;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.whos_new_row, parent, false);
        return new MyViewHolder(itemView,mListener);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        SearchResults result = results.get(position);
        if (result.getHasPicture().equals("Yes")){
            Picasso.with(context).load(result.getPhotourl()).into(holder.profile_image);
        }
        if (result.getIsOnline().endsWith("Yes")){
            holder.online_offline_image.setImageDrawable(R.drawable.ic_online_dot);
            holder.online_offilne_text.setText("Online");
        } else {
            holder.online_offline_image.setImageDrawable(R.drawable.ic_online_dor);
            holder.online_offilne_text.setText("Offline");
        }
        holder.name_person.setText(result.getUsername());
        holder.residence.setText(result.getCity_self()+","+result.getCountry_self());
        holder.lifestyle.setText(result.getLifestyle_self());


    }

    @Override
    public int getItemCount() {
        return results.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        public ImageView profile_image, online_offline_image;
        public TextView name_person,residence,lifestyle,online_offilne_text;

        public MyViewHolder(View view ,RecyclerViewClickListener listener){
            super(view);
            profile_image = (ImageView)view.findViewById(R.id.profile_image);
            online_offline_image = (ImageView)view.findViewById(R.id.online_offline_image);
            //details = (TextView)view.findViewById(R.id.name_age_tv);
            online_offilne_text = (TextView)view.findViewById(R.id.online_offline_tv);
            //dummy = (ImageView)view.findViewById(R.id.dummy_image);
            residence = (TextView)view.findViewById(R.is.ciy_tv);
            lifestyle = (TextView)view.findViewById(R.id.lifestyle_tv);
            name_person = (TextView)view.findViewById(R.id.name_tv);
            mListener = listener;
            view.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            mListener.onClick(view,getAdapterPosition());
        }
    }
}