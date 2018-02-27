package Adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
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
        toImage.setVisibility(View.GONE);
        fromImage.setVisibility(View.GONE);
        message.setVisibility(View.GONE);
        User from = User.find(User.class,"username = ?",chat.getFrom()).get(0);
        User to = User.find(User.class,"username = ?",chat.getTo()).get(0);
        if (chat.getFrom().equals(prefs.getname())){
            Picasso.with(context).load(from.getPhotourl()).into(fromImage);
            message.setText(chat.getChat_message());
            message.setGravity(Gravity.RIGHT);
            message.setGravity(Gravity.END);
            message.setBackgroundColor(R.color.leaf_green);
            message.setTextColor(R.color.white);
            fromImage.setVisibility(View.VISIBLE);
            message.setVisibility(View.VISIBLE);
        } else if (chat.getTo().equals(prefs.getname())){
            Picasso.with(context).load(from.getPhotourl()).into(toImage);
            message.setText(chat.getChat_message());
            message.setGravity(Gravity.LEFT);
            message.setGravity(Gravity.START);
            message.setBackgroundColor(R.color.white);
            message.setTextColor(R.color.black);
            toImage.setVisibility(View.VISIBLE);
            message.setVisibility(View.VISIBLE);
        }

        return view;
    }
}