package Adapters;

import android.app.Fragment;
import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import java.util.List;

import Models.User;
import ssapps.com.datingapp.R;


/**
 * Created by sagar on 16/03/18.
 */

public class FriendsListAdapter extends RecyclerView.Adapter<FriendsListAdapter.MyViewHolder>{

private Context context;
private List<User> users;

    public FriendsListAdapter(Context context,List<User> users){
        this.context = context;
        this.users = users;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.friends_list_row, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {

        holder.name.setText(users.get(position).getUsername());

    }

    @Override
    public int getItemCount() {
        return users.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder{

        public TextView name;
        public Button b1,b2,b3;

        public MyViewHolder(View itemView) {
            super(itemView);
            name = (TextView)itemView.findViewById(R.id.content);
            b1 = (Button)itemView.findViewById(R.id.btnTop);
            b2 = (Button)itemView.findViewById(R.id.btnDelete);
            b3 = (Button)itemView.findViewById(R.id.btnUnRead);
        }
    }

}
