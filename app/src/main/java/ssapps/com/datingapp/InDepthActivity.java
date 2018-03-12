package ssapps.com.datingapp;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.backendless.Backendless;
import com.backendless.async.callback.AsyncCallback;
import com.backendless.exceptions.BackendlessFault;
import com.google.android.youtube.player.YouTubeInitializationResult;
import com.google.android.youtube.player.YouTubePlayer;
import com.google.android.youtube.player.YouTubePlayerFragment;
import com.google.android.youtube.player.YouTubePlayerSupportFragment;
import com.orm.SugarContext;


import Models.User;
import Util.Prefs;
import Util.Util;
import cn.pedant.SweetAlert.SweetAlertDialog;
import ssapps.com.datingapp.databinding.ActivityInDepthBinding;

public class InDepthActivity extends Fragment implements YouTubePlayer.OnInitializedListener{

    private ActivityInDepthBinding binding;
    private static final String key = "AIzaSyCXCH0moJoeDqFi9XIV2A8ogclFxo9zoJI";
    private static final String appKey = "7EEB2727-4E8D-944C-FFDD-3D802BC37800";
    private static final String appId = "648D896E-EDD8-49C8-FF74-2F1C32DB7A00";
    private static final String GCM_SENDER_ID = "57050948456";
    private static final int RECOVERY_REQUEST = 1;
    private User loggedUser;
    private SweetAlertDialog error,dialog;
    private Prefs prefs;
    private YouTubePlayer.Provider Yprovider;
    private Util util;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater,R.layout.activity_in_depth,container,false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Backendless.initApp(getContext(),appId,appKey);
        SugarContext.init(getContext());
        prefs = new Prefs(getContext());
        util = new Util();

        error = new SweetAlertDialog(getContext(),SweetAlertDialog.ERROR_TYPE);
        dialog = new SweetAlertDialog(getContext(),SweetAlertDialog.PROGRESS_TYPE);
        dialog.setCancelable(false);
        dialog.dismiss();

        loggedUser = User.find(User.class,"username = ?",prefs.getname()).get(0);

        binding.leftHeaderTv.setText(loggedUser.getGender_self()+" looking for "+loggedUser.getGender_others());
        binding.heading.setText("Meet "+loggedUser.getUsername()+"!");
        binding.aboutMeTv.setText("-Do you want to know what it is? ;-)");
        if (loggedUser.getGender_self().equals("Male")){
            binding.basicIntroTv.setText("He is "+loggedUser.getAge_self()+" years ole,"+
                    loggedUser.getLifestyle_self()+",live in "+loggedUser.getCity_self()+" and has a wicked hobby");
            binding.aboutVideoTv.setText("Then watch his self introduction. If uou dare, he would be delighted to recieve a message from you");
        } else if (loggedUser.getGender_self().equals("Female")){
            binding.basicIntroTv.setText("She is "+loggedUser.getAge_self()+" years ole,"+
                    loggedUser.getLifestyle_self()+",live in "+loggedUser.getCity_self()+" and has a wicked hobby");
            binding.aboutVideoTv.setText("Then watch her self introduction. If you dare, she would be delighted to recieve a message from you.");
        }

        if (!loggedUser.getVideoUrl().equals("None")) {
            binding.addVideoButtom.setText(R.string.new_vid);
            YouTubePlayerSupportFragment youTubePlayerSupportFragment = YouTubePlayerSupportFragment.newInstance();
            FragmentTransaction transaction = getChildFragmentManager().beginTransaction();
            transaction.add(R.id.youtube_fragment, youTubePlayerSupportFragment).commit();
            youTubePlayerSupportFragment.initialize(key,this);
//            youTubePlayerSupportFragment.initialize(key, new YouTubePlayer.OnInitializedListener() {
//                @Override
//                public void onInitializationSuccess(YouTubePlayer.Provider provider, YouTubePlayer youTubePlayer, boolean b) {
//
//                }
//
//                @Override
//                public void onInitializationFailure(YouTubePlayer.Provider provider, YouTubeInitializationResult youTubeInitializationResult) {
////                    if (youTubeInitializationResult.isUserRecoverableError()){
////                        youTubeInitializationResult.getErrorDialog(getActivity(),RECOVERY_REQUEST).show();
////                    } else {
//                        error.setTitleText("Error playing video");
//                        error.show();
//                   // }
//                }
//            });
        } else {
            binding.addVideoButtom.setText(R.string.v);
        }

        binding.addVideoButtom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (binding.urlLayout.getVisibility() == View.GONE) {
                    binding.urlLayout.setVisibility(View.VISIBLE);
                    binding.addVideoButtom.setText(R.string.save);
                } else {
                    if (util.checkEditTextField(binding.urlEt)){
                        saveData();
                    } else {
                        Toast.makeText(getContext(),"Please enter the video url",Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });

    }

    private void saveData() {

        dialog.setTitle("Saving...");
        dialog.show();
        loggedUser.setVideoUrl(binding.urlEt.getText().toString().trim());
       // loggedUser.update();
        Backendless.Data.save(loggedUser, new AsyncCallback<User>() {
            @Override
            public void handleResponse(User response) {
                dialog.dismiss();
                loggedUser.update();
                prefs.setOnlineRedirect(true);
                startActivity(new Intent(getContext(),MainActivity.class));
            }

            @Override
            public void handleFault(BackendlessFault fault) {
                dialog.dismiss();
                error.setTitle("Cannot save data");
                error.setContentText("The following error has occured while saving user data \n"+fault.getMessage()+"\n Please try again");
                error.show();
            }
        });

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == RECOVERY_REQUEST){
            Yprovider.initialize(key,this);
                    }
    }

    @Override
    public void onInitializationSuccess(YouTubePlayer.Provider provider, YouTubePlayer youTubePlayer, boolean b) {
        Yprovider =provider;
        if (!b){
            youTubePlayer.loadVideo(loggedUser.getVideoUrl());
            youTubePlayer.play();
        }
    }

    @Override
    public void onInitializationFailure(YouTubePlayer.Provider provider, YouTubeInitializationResult youTubeInitializationResult) {
        if (youTubeInitializationResult.isUserRecoverableError()){
            youTubeInitializationResult.getErrorDialog(getActivity(),RECOVERY_REQUEST).show();
        } else {
            error.setTitleText("Error playing video");
            error.show();
        }
    }

    //    @Override
//    public void onAttach(Context context) {
//
//        if(getActivity() instanceof Fragment)
//        super.onAttach(context);
//    }


    //todo last

}
