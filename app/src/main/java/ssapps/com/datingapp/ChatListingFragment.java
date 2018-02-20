package ssapps.com.datingapp;

import android.databinding.DataBindingUtil;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import ssapps.com.datingapp.databinding.ActivityChatListingFragmentBinding;

public class ChatListingFragment extends Fragment{
    private ActivityChatListingFragmentBinding binding;
    private boolean silence;
    private String[] usersNames = {};
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater,R.layout.activity_chat_listing_fragment,false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {

        if ()

    }

    //todo dont forget to sort messages before sending into adapter


}
