package ssapps.com.datingapp;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.backendless.Backendless;
import com.backendless.async.callback.AsyncCallback;
import com.backendless.exceptions.BackendlessFault;
import com.google.android.youtube.player.YouTubeBaseActivity;
import com.google.android.youtube.player.YouTubeInitializationResult;
import com.google.android.youtube.player.YouTubePlayer;

import java.util.Calendar;

import Models.Message;
import Models.User;
import Util.Prefs;
import cn.pedant.SweetAlert.SweetAlertDialog;
import ssapps.com.datingapp.databinding.ActivitySearchItemDetailsBinding;

public class SearchItemDetailsActivity extends YouTubeBaseActivity implements YouTubePlayer.OnInitializedListener{

    private static final String key = "AIzaSyCXCH0moJoeDqFi9XIV2A8ogclFxo9zoJI";
    private static final String appKey = "7EEB2727-4E8D-944C-FFDD-3D802BC37800";
    private static final String appId = "648D896E-EDD8-49C8-FF74-2F1C32DB7A00";
    ActivitySearchItemDetailsBinding binding;
    private static final int RECOVERY_REQUEST = 1;
    private User user;
    private SweetAlertDialog error,dialog;
    private Prefs prefs;
  //  private Prefs prefs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //   setContentView(R.layout.activity_search_item_details);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_search_item_details);
       Backendless.initApp(this,appId,appKey);

        error = new SweetAlertDialog(this,SweetAlertDialog.ERROR_TYPE);
        dialog = new SweetAlertDialog(this,SweetAlertDialog.PROGRESS_TYPE);
        dialog.setCancelable(false);
        dialog.dismiss();

        prefs = new Prefs(this);

        if (getIntent().hasExtra("name")){
            String userName = getIntent().getStringExtra("name");

            user = User.find(User.class,"username = ?",userName).get(0);

            binding.leftHeaderTv.setText(user.getGender_self()+" looking for "+user.getGender_others());

            if (user.getGender_self().equals("Male")){
                binding.basicIntroTv.setText("He is "+user.getAge_self()+" years ole,"+
                        user.getLifestyle_self()+",live in "+user.getCity_self()+" and has a wicked hobby");
                binding.aboutVideoTv.setText("Then watch his self introduction. If uou dare, he would be delighted to recieve a message from you");
            } else if (user.getGender_self().equals("Female")){
                binding.basicIntroTv.setText("SHe is "+user.getAge_self()+" years ole,"+
                        user.getLifestyle_self()+",live in "+user.getCity_self()+" and has a wicked hobby");
                binding.aboutVideoTv.setText("Then watch her self introduction. If uou dare, she would be delighted to recieve a message from you");
            }

            binding.youtubePlayer.initialize(key,this);


            binding.contactButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    contactPerson();
                    //todo send push notification
                }
            });


        }



    }


    private void contactPerson(){

        Calendar calendar = Calendar.getInstance();

        final Message message = new Message();
        message.setFrom(prefs.getname());
        message.setTo(user.getUsername());
        message.setTime(String.valueOf(calendar.getTimeInMillis()));
        message.setChat_message("I found your profile interesting. I would like to get to know you more");

        dialog.setTitleText("Sending message...");
        dialog.show();

        Backendless.Data.save(message, new AsyncCallback<Message>() {
            @Override
            public void handleResponse(Message response) {
                dialog.dismiss();
                response.save();
                Toast.makeText(getApplicationContext(),"Meesage sent successfully. Please check in chats",Toast.LENGTH_LONG).show();
            }

            @Override
            public void handleFault(BackendlessFault fault) {
                dialog.dismiss();
                error.setTitleText("Error sending messge");
                error.setContentText("The following error has occured while sending message\n"+
                            fault.getMessage()+"\n Please try again");
                error.show();
            }
        });

    }


    @Override
    public void onInitializationSuccess(YouTubePlayer.Provider provider, YouTubePlayer youTubePlayer, boolean b) {
        if (!b){
            youTubePlayer.cueVideo(user.getVideoUrl());
        }
    }

    @Override
    public void onInitializationFailure(YouTubePlayer.Provider provider, YouTubeInitializationResult youTubeInitializationResult) {
        if (youTubeInitializationResult.isUserRecoverableError()){
            youTubeInitializationResult.getErrorDialog(this,RECOVERY_REQUEST).show();
        } else {
            error.setTitleText("Error playing video");
            error.show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == RECOVERY_REQUEST){
            getYouTubePlayerProvider().initialize(key,this);
        }


    }
    protected YouTubePlayer.Provider getYouTubePlayerProvider() {
        return binding.youtubePlayer;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        this.finish();
    }
}