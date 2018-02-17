package ssapps.com.datingapp;

import android.databinding.DataBindingUtil;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.google.android.youtube.player.YouTubeBaseActivity;
import com.google.android.youtube.player.YouTubeInitializationResult;
import com.google.android.youtube.player.YouTubePlayer;

import Models.User;
import Util.Prefs;
import ssapps.com.datingapp.databinding.ActivitySearchItemDetailsBinding;

public class SearchItemDetailsActivity extends YouTubeBaseActivity implements YouTubePlayer.OnInitializedListener {

    private static final String key = "AIzaSyCXCH0moJoeDqFi9XIV2A8ogclFxo9zoJI";
    ActivitySearchItemDetailsBinding binding;
    private static final int RECOVERY_REQUEST = 1;
    private User user;
  //  private Prefs prefs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //   setContentView(R.layout.activity_search_item_details);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_search_item_deails);

        if (getIntent().hasExtra("name")){
            String userName = getIntent().getStringExtra("name");

            user = User.find(User.class,"username = ?",userName).get(0);

            binding.leftHeaderTv.setText(user.getGender_self()+" looking for "+user.getGender_others());

            if (user.getGender_self().equals("Male")){
                //todo
            }





        }



    }

    @Override
    public void onInitializationSuccess(YouTubePlayer.Provider provider, YouTubePlayer youTubePlayer, boolean b) {

    }

    @Override
    public void onInitializationFailure(YouTubePlayer.Provider provider, YouTubeInitializationResult youTubeInitializationResult) {

    }
}