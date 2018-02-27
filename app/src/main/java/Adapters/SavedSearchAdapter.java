package Adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import Models.SavedSearch;
import ssapps.com.datingapp.R;
import ssapps.com.datingapp.RecyclerViewClickListener;

public class SavedSearchAdapter extends RecyclerView.Adapter<SavedSearchAdapter.MyViewHolder>{

    private List<SavedSearch> searches = new ArrayList<>();
    private RecyclerViewClickListener listener;

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.saved_search_row, parent, false);

        return new MyViewHolder(itemView,listener);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        SavedSearch searchParams = new SavedSearch();

        holder.string_1.setText(searchParams.getGender()+","+searchParams.getWho_are()
            +","+searchParams.getLifestyle()+","+searchParams.getStatus());
        holder.string_2.setText(searchParams.getMin_age()+"-"+searchParams.getMax_age());
        holder.string_3.setText(searchParams.getCity()+","+searchParams.getCountry());

        holder.string_time.setText(getSearchTIme(searchParams.getSaved_time()));
    }

    private String getSearchTIme(long saved_time) {

        Long now = Calendar.getInstance().getTimeInMillis();
        Long searched_time = saved_time;

        Long time_difference = now - searched_time;

        int years = (int) (time_difference / (365*24*60*60*1000));

        if (years != 0){
            return String.valueOf(years)+"years ago";
        } else {
            int months = (int) (time_difference/(30*24*60*60*1000));
            if (months != 0){
                return String.valueOf(months)+"months ago";
            } else {
                int days = (int)(time_difference/24*60*60*1000);
                if (days != 0){
                    return String.valueOf(days)+"days ago";
                } else {
                    int hours = (int)(time_difference/60*60*1000);
                    if (hours != 0) {
                        return String.valueOf(hours) + "hours ago";
                    } else {
                        int minutes = (int)(time_difference/60*1000);
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
        return searches.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        TextView string_1,string_2,string_3,string_time;

        public MyViewHolder(View view,RecyclerViewClickListener listener1){
            super(view);
            string_1 = (TextView)view.findViewById(R.id.search_string_1);
            string_2 = (TextView)view.findViewById(R.id.search_string_2);
            string_3 = (TextView)view.findViewById(R.id.search_string_3);
            string_time = (TextView)view.findViewById(R.id.search_time);
            listener = listener1;
            view.setOnClickListener(this);
        }


        @Override
        public void onClick(View view) {
            listener.onClick(view,getAdapterPosition());
                    }
    }

    public SavedSearchAdapter(List<SavedSearch> searches,RecyclerViewClickListener listener){
        this.searches = searches;
        this.listener = listener;
    }



}