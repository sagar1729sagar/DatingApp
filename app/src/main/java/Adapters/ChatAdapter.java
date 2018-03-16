package Adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.github.siyamed.shapeimageview.BubbleImageView;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import Models.IndividualChats;
import Models.Message;
import Models.User;
import Util.Prefs;
import ssapps.com.datingapp.R;

public class ChatAdapter extends BaseAdapter {

    private List<IndividualChats> chats = new ArrayList<>();
    private Prefs prefs;
    private Context context;

    public ChatAdapter(Context context, List<IndividualChats> chats){
    this.chats = chats;
    this.context = context;
    prefs = new Prefs(context);
    }
    @Override
    public int getCount() {
        return chats.size();
    }

    @Override
    public Object getItem(int i) {
        return chats.get(i);
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @SuppressLint("ResourceAsColor")
    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        IndividualChats chat = chats.get(i);
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        view = inflater.inflate(R.layout.chat_row,null);

        BubbleImageView toImage = (BubbleImageView)view.findViewById(R.id.to_image);
        BubbleImageView fromImage = (BubbleImageView)view.findViewById(R.id.from_image);
        TextView message = (TextView)view.findViewById(R.id.chat_tv);
        RelativeLayout rl = (RelativeLayout)view.findViewById(R.id.tv_rl);
        toImage.setVisibility(View.INVISIBLE);
        fromImage.setVisibility(View.INVISIBLE);
        message.setVisibility(View.GONE);
        rl.setVisibility(View.GONE);
        Log.v("user chat",chat.getMessage_from());
        User from = User.find(User.class,"username = ?",chat.getMessage_from()).get(0);
        User to = User.find(User.class,"username = ?",chat.getMessage_to()).get(0);
        if (chat.getMessage_from().equals(prefs.getname())){
            if (from.getHasPicture().equals("Yes")) {
                Picasso.with(context).load(from.getPhotourl()).into(fromImage);
            }
            message.setText(chat.getChat_message());
            rl.setGravity(Gravity.RIGHT);
            rl.setGravity(Gravity.END);
//            message.setGravity(Gravity.LEFT);
//            message.setGravity(Gravity.START);
//            message.setGravity(Gravity.RIGHT);
//            message.setGravity(Gravity.END);
          //  message.setBackgroundColor(context.getResources().getColor(R.color.leaf_green));
            message.setTextColor(context.getResources().getColor(R.color.black));
            fromImage.setVisibility(View.VISIBLE);
            message.setVisibility(View.VISIBLE);
            rl.setVisibility(View.VISIBLE);
        } else if (chat.getMessage_to().equals(prefs.getname())){
            if (to.getHasPicture().equals("Yes")) {
                Picasso.with(context).load(from.getPhotourl()).into(toImage);
            }
            message.setText(chat.getChat_message());
//            message.setGravity(Gravity.LEFT);
//            message.setGravity(Gravity.START);
            rl.setGravity(Gravity.LEFT);
            rl.setGravity(Gravity.START);
            message.setTextColor(context.getResources().getColor(R.color.black));
            toImage.setVisibility(View.VISIBLE);
            message.setVisibility(View.VISIBLE);
            rl.setVisibility(View.VISIBLE);
        }

        return view;
    }
}