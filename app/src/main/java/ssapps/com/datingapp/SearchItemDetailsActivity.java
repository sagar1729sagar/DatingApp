package ssapps.com.datingapp;


import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.backendless.Backendless;
import com.backendless.DeviceRegistration;
import com.backendless.async.callback.AsyncCallback;
import com.backendless.exceptions.BackendlessFault;
import com.backendless.messaging.MessageStatus;
import com.backendless.messaging.PublishOptions;
import com.google.android.youtube.player.YouTubeBaseActivity;
import com.google.android.youtube.player.YouTubeInitializationResult;
import com.google.android.youtube.player.YouTubePlayer;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Calendar;

import Models.Message;
import Models.User;
import Util.Prefs;
import cn.pedant.SweetAlert.SweetAlertDialog;
import ssapps.com.datingapp.databinding.ActivitySearchItemDetailsBinding;

public class SearchItemDetailsActivity extends YouTubeBaseActivity implements YouTubePlayer.OnInitializedListener, View.OnClickListener {

    private static final String key = "AIzaSyCXCH0moJoeDqFi9XIV2A8ogclFxo9zoJI";
    private static final String appKey = "7EEB2727-4E8D-944C-FFDD-3D802BC37800";
    private static final String appId = "648D896E-EDD8-49C8-FF74-2F1C32DB7A00";
    private static final String GCM_SENDER_ID = "57050948456";
    ActivitySearchItemDetailsBinding binding;
    private static final int RECOVERY_REQUEST = 1;
    private User user;
    private SweetAlertDialog error,dialog,confirmDialog;
    private Prefs prefs;
    private Message contact_message;
    private DatabaseReference mDatabase;
    private static final String fav = "Favourites";
    private static final String friend = "Friends";
    private static final String contact = "contact";
    private static final String block = "Blocked";
    private static final String liked = "Liked";
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

        contact_message = new Message();

        prefs = new Prefs(this);



        if (getIntent().hasExtra("name")){
            String userName = getIntent().getStringExtra("name");

            user = User.find(User.class,"username = ?",userName).get(0);

            binding.leftHeaderTv.setText(user.getGender_self()+" looking for "+user.getGender_others());
            binding.heading.setText("Meet "+user.getUsername()+"!!");

            if (user.getGender_self().equals("Male")){
                binding.basicIntroTv.setText("He is "+user.getAge_self()+" years ole,"+
                        user.getLifestyle_self()+",live in "+user.getCity_self()+" and has a wicked hobby");
                binding.aboutVideoTv.setText("Then watch his self introduction. If uou dare, he would be delighted to recieve a message from you");
            } else if (user.getGender_self().equals("Female")){
                binding.basicIntroTv.setText("She is "+user.getAge_self()+" years ole,"+
                        user.getLifestyle_self()+",live in "+user.getCity_self()+" and has a wicked hobby");
                binding.aboutVideoTv.setText("Then watch her self introduction. If uou dare, she would be delighted to recieve a message from you");
            }

            binding.aboutMeTv.setText("Do you want to know what it is?");

            binding.youtubePlayer.initialize(key,this);

            binding.contactButton.setText("Contact "+user.getUsername());

            binding.contactButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (prefs.getname().equals("None")){
                        Toast.makeText(getApplicationContext(),"Please register or login to contact "+user.getUsername(),Toast.LENGTH_LONG).show();
                    } else {
                        contactPerson(contact);
                    }

                }
            });


            binding.contactButton.setOnClickListener(this);
            binding.stactChatIcon.setOnClickListener(this);
            binding.favouriteAddIcon.setOnClickListener(this);
            binding.addFriendIcon.setOnClickListener(this);
            binding.blockUserIcon.setOnClickListener(this);
            binding.likeIcon.setOnClickListener(this);

//            binding.goBackButton.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View view) {
//                    onBackPressed();
//                }
//            });


        }



    }


    private void contactPerson(){

        dialog.setTitle("Contacting");
        dialog.show();
        Calendar calendar = Calendar.getInstance();

        final Message message = new Message();
//        message.setFrom(prefs.getname());
//        message.setTo(user.getUsername());
        message.setMessage_from(prefs.getname());
        message.setMessage_to(user.getUsername());
        message.setTime(String.valueOf(calendar.getTimeInMillis()));
        message.setChat_message("I found your profile interesting. I would like to get to know you more");

        dialog.setTitleText("Sending message...");
        dialog.show();

        Backendless.Data.save(message, new AsyncCallback<Message>() {
            @Override
            public void handleResponse(Message response) {
               // dialog.dismiss();
                response.setId(Message.count(Message.class)+1);
                response.save();
                contact_message = response;

                checkPushNotificationRegistration();
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

    private void checkPushNotificationRegistration() {
        Backendless.Messaging.getDeviceRegistration(new AsyncCallback<DeviceRegistration>() {
            @Override
            public void handleResponse(DeviceRegistration response) {
                if (response.getChannels().contains(prefs.getname())){
                    sendNotification();
                } else {
                    registerDevice();
                }
            }

            @Override
            public void handleFault(BackendlessFault fault) {
                //do nothing
                dialog.dismiss();
                Toast.makeText(getApplicationContext(),"Message sent successfully. Please check in chats",Toast.LENGTH_LONG).show();
            }
        });
    }

    private void registerDevice() {

        Backendless.Messaging.registerDevice(GCM_SENDER_ID, user.getUsername(), new AsyncCallback<Void>() {
            @Override
            public void handleResponse(Void response) {
                sendNotification();
            }

            @Override
            public void handleFault(BackendlessFault fault) {
                //do nothing
                dialog.dismiss();
                Toast.makeText(getApplicationContext(),"Message sent successfully. Please check in chats",Toast.LENGTH_LONG).show();
            }
        });
    }

    private void sendNotification() {

        PublishOptions publishOptions = new PublishOptions();
        publishOptions.putHeader("android-ticker-text", prefs.getname());
        publishOptions.putHeader("android-content-title", user.getUsername());
        publishOptions.putHeader("android-content-text", "I found your profile interesting. I would like to get to know you more");

        Backendless.Messaging.publish(user.getUsername(), "chat,"+contact_message.getObjectId(), publishOptions, new AsyncCallback<MessageStatus>() {
            @Override
            public void handleResponse(MessageStatus response) {
                dialog.dismiss();
                Toast.makeText(getApplicationContext(),"Message sent successfully. Please check in chats",Toast.LENGTH_LONG).show();
            }

            @Override
            public void handleFault(BackendlessFault fault) {
                //do nothing
                dialog.dismiss();
                Toast.makeText(getApplicationContext(),"Message sent successfully. Please check in chats",Toast.LENGTH_LONG).show();
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

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.stact_chat_icon:
                if (prefs.getname().equals("None")){
                    Toast.makeText(getApplicationContext(),"Please register or login to chat with "+user.getUsername(),Toast.LENGTH_LONG).show();
                } else {
                    Log.v("start chat with", user.getUsername());
                    Intent i = new Intent(SearchItemDetailsActivity.this, ChatActivity.class);
                    i.putExtra("user", user.getUsername());
                    startActivity(i);
                }
                break;
            case R.id.favourite_add_icon:
                if (prefs.getname().equals("None")){
                    Toast.makeText(getApplicationContext(),"Please register or login to add "+user.getUsername()+" as your favourite",Toast.LENGTH_LONG).show();
                } else {
                    addFavourite();
                }
                break;
            case R.id.add_friend_icon:
                if (prefs.getname().equals("None")){
                    Toast.makeText(getApplicationContext(),"Please register or login to add "+user.getUsername()+" as your friend",Toast.LENGTH_LONG).show();
                } else {
                    addFriend();
                }
                break;
            case R.id.block_user_icon:
                if (prefs.getname().equals("None")){
                    Toast.makeText(getApplicationContext(),"Please register or login to block "+user.getUsername(),Toast.LENGTH_LONG).show();
                } else {
                    blockUser();
                }
                break;
            case R.id.like_icon:
                if (prefs.getname().equals("None")){
                    Toast.makeText(getApplicationContext(),"Please register or login to show your interest on "+user.getUsername(),Toast.LENGTH_LONG).show();
                } else {
                    likeUser();
                }
                break;

        }
    }

    private void likeUser() {
        confirmDialog = new SweetAlertDialog(this,SweetAlertDialog.NORMAL_TYPE);
        confirmDialog.setConfirmButton("Like", new SweetAlertDialog.OnSweetClickListener() {
            @Override
            public void onClick(SweetAlertDialog sweetAlertDialog) {
                Log.v("confirmed",user.getUsername());
                confirmLike(user.getUsername());
                confirmDialog.dismiss();
            }
        });
        confirmDialog.setCancelButton("Later", new SweetAlertDialog.OnSweetClickListener() {
            @Override
            public void onClick(SweetAlertDialog sweetAlertDialog) {
                confirmDialog.dismiss();
            }
        });
        confirmDialog.setTitle("Confirm?");
        confirmDialog.setContentText("Are you sure you want to like "+user.getUsername());

        confirmDialog.show();
    }

    private void confirmLike(String username) {
        dialog.setTitle("Liking...");
        dialog.show();
        mDatabase = FirebaseDatabase.getInstance().getReference();
        mDatabase.child(prefs.getname()).child(liked).child(username).setValue(Calendar.getInstance().getTimeInMillis(), new DatabaseReference.CompletionListener() {
            @Override
            public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                if (databaseError != null) {
                    dialog.dismiss();
                    error.setTitle("Error adding like");
                    error.setContentText(databaseError.getMessage() + "\n Please try again");
                } else {
                    dialog.dismiss();
                    checkPushNotificationRegistration(liked);
                    //  Toast.makeText(getApplicationContext(),"User liked ",Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    private void blockUser() {
        confirmDialog = new SweetAlertDialog(this,SweetAlertDialog.NORMAL_TYPE);
        confirmDialog.setConfirmButton("Block", new SweetAlertDialog.OnSweetClickListener() {
            @Override
            public void onClick(SweetAlertDialog sweetAlertDialog) {
                Log.v("confirmed",user.getUsername());
                confirmBlock(user.getUsername());
                confirmDialog.dismiss();
            }
        });
        confirmDialog.setCancelButton("Later", new SweetAlertDialog.OnSweetClickListener() {
            @Override
            public void onClick(SweetAlertDialog sweetAlertDialog) {
                confirmDialog.dismiss();
            }
        });
        confirmDialog.setTitle("Confirm?");
        confirmDialog.setContentText("Are you sure you want to block "+user.getUsername());

        confirmDialog.show();
    }

    private void confirmBlock(String username) {
        dialog.setTitle("Blocking...");
        dialog.show();
        mDatabase = FirebaseDatabase.getInstance().getReference();
        mDatabase.child(prefs.getname()).child(block).child(username).setValue(Calendar.getInstance().getTimeInMillis(), new DatabaseReference.CompletionListener() {
            @Override
            public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                if (databaseError != null) {
                    dialog.dismiss();
                    error.setTitle("Error adding favourite");
                    error.setContentText(databaseError.getMessage() + "\n Please try again");
                } else {
                    dialog.dismiss();
                    checkPushNotificationRegistration(block);
                    //   Toast.makeText(getApplicationContext(),"User blocked",Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    private void addFriend() {
        confirmDialog = new SweetAlertDialog(this,SweetAlertDialog.NORMAL_TYPE);
        confirmDialog.setConfirmButton("Add", new SweetAlertDialog.OnSweetClickListener() {
            @Override
            public void onClick(SweetAlertDialog sweetAlertDialog) {
                Log.v("confirmed",user.getUsername());
                confirmFriend(user.getUsername());
                confirmDialog.dismiss();
            }
        });
        confirmDialog.setCancelButton("Later", new SweetAlertDialog.OnSweetClickListener() {
            @Override
            public void onClick(SweetAlertDialog sweetAlertDialog) {
                confirmDialog.dismiss();
            }
        });
        confirmDialog.setTitle("Confirm?");
        confirmDialog.setContentText("Are you sure you want to add "+user.getUsername()+" as a friend?");

        confirmDialog.show();
    }

    private void confirmFriend(String username) {
        dialog.setTitle("Adding friend...");
        dialog.show();
        mDatabase = FirebaseDatabase.getInstance().getReference();
        mDatabase.child(prefs.getname()).child(friend).child(username).setValue(Calendar.getInstance().getTimeInMillis(), new DatabaseReference.CompletionListener() {
            @Override
            public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {

                if (databaseError != null) {
                    dialog.dismiss();
                    error.setTitle("Error adding favourite");
                    error.setContentText(databaseError.getMessage() + "\n Please try again");
                } else {
                    checkPushNotificationRegistration(friend);
                    // Toast.makeText(getApplicationContext(),"Favourite added successfully",Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    private void addFavourite() {

        confirmDialog = new SweetAlertDialog(this,SweetAlertDialog.NORMAL_TYPE);
        confirmDialog.setConfirmButton("Add", new SweetAlertDialog.OnSweetClickListener() {
            @Override
            public void onClick(SweetAlertDialog sweetAlertDialog) {
                Log.v("confirmed",user.getUsername());
                confirmFavourite(user.getUsername());
                confirmDialog.dismiss();
            }
        });
        confirmDialog.setCancelButton("Later", new SweetAlertDialog.OnSweetClickListener() {
            @Override
            public void onClick(SweetAlertDialog sweetAlertDialog) {
                confirmDialog.dismiss();
            }
        });
        confirmDialog.setTitle("Confirm?");
        confirmDialog.setContentText("Are you sure you want to add "+user.getUsername()+" as a favourite?");

        confirmDialog.show();
    }

    private void confirmFavourite(String username) {
        dialog.setTitle("Adding favourite...");
        dialog.show();
        mDatabase = FirebaseDatabase.getInstance().getReference();
        //  HashMap<String,Long> map = new HashMap<>();
        // map.put(username,Calendar.getInstance().getTimeInMillis());
        mDatabase.child(prefs.getname()).child(fav).child(username).setValue(Calendar.getInstance().getTimeInMillis(), new DatabaseReference.CompletionListener() {
            @Override
            public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                dialog.dismiss();
                if (databaseError != null) {
                    error.setTitle("Error adding favourite");
                    error.setContentText(databaseError.getMessage() + "\n Please try again");
                } else {
                    checkPushNotificationRegistration(fav);
                    // Toast.makeText(getApplicationContext(),"Favourite added successfully",Toast.LENGTH_LONG).show();
                }
            }
        });
        //  map.put(String.valueOf(username,Calendar.getInstance().getTimeInMillis());
        //Favourites favourites = new Favourites(Calendar.getInstance().getTimeInMillis(),username);
//        mDatabase.child(prefs.getname()).child(fav).setValue(map, new DatabaseReference.CompletionListener() {
//            @Override
//            public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
//                if (databaseError != null) {
//                    error.setTitle("Error adding favourite");
//                    error.setContentText(databaseError.getMessage() + "\n Please try again");
//                } else {
//                    Toast.makeText(getApplicationContext(),"Favourite added successfully",Toast.LENGTH_LONG).show();
//                }
//            }
//        });
    }

    private void contactPerson(final String type) {
        Calendar calendar = Calendar.getInstance();

        final Message message = new Message();
        //   message.setFrom(prefs.getname());
        message.setMessage_from(prefs.getname());
        // message.setTo(currentUser.getUsername());
        message.setMessage_to(user.getUsername());
        message.setTime(String.valueOf(calendar.getTimeInMillis()));
        message.setChat_message("I found your profile interesting. I would like to get to know you more");

        dialog.setTitleText("Sending message...");
        dialog.show();

        Backendless.Data.save(message, new AsyncCallback<Message>() {
            @Override
            public void handleResponse(Message response) {
                // dialog.dismiss();
                Log.v("message count", String.valueOf(Message.count(Message.class)));
//                if (Message.count(Message.class) < 0){
//                    Log.v("Message count","less");
//                    Message.deleteAll(Message.class);
//                }
                response.setId(Message.count(Message.class)+1);
                response.save();
                contact_message = response;

                checkPushNotificationRegistration(type);
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

    private void checkPushNotificationRegistration(final String type) {
        Backendless.Messaging.getDeviceRegistration(new AsyncCallback<DeviceRegistration>() {
            @Override
            public void handleResponse(DeviceRegistration response) {
                if (response.getChannels().contains(prefs.getname())){
                    sendNotification(type);
                } else {
                    registerDevice(type);
                }
            }

            @Override
            public void handleFault(BackendlessFault fault) {
                //do nothing
                if (type.equals(contact)) {
                    dialog.dismiss();
                    Toast.makeText(getApplicationContext(), "Message sent successfully. Please check in chats", Toast.LENGTH_LONG).show();
                } else if (type.equals(fav)){
                    dialog.dismiss();
                    Toast.makeText(getApplicationContext(),"Favourite added successfully",Toast.LENGTH_LONG).show();
                } else if (type.equals(friend)){
                    dialog.dismiss();
                    Toast.makeText(getApplicationContext(),"Friend added successfully",Toast.LENGTH_LONG).show();
                } else if (type.equals(liked)){
                    Toast.makeText(getApplicationContext(),"Liked successfully",Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    private void registerDevice(final String type) {

        Backendless.Messaging.registerDevice(GCM_SENDER_ID, user.getUsername(), new AsyncCallback<Void>() {
            @Override
            public void handleResponse(Void response) {
                sendNotification(type);
            }

            @Override
            public void handleFault(BackendlessFault fault) {
                //do nothing
                dialog.dismiss();
                if (type.equals(contact)) {
                    Toast.makeText(getApplicationContext(), "Message sent successfully. Please check in chats", Toast.LENGTH_LONG).show();
                } else if (type.equals(fav)){
                    dialog.dismiss();
                    Toast.makeText(getApplicationContext(),"Favourite added successfully",Toast.LENGTH_LONG).show();
                } else if (type.equals(friend)){
                    dialog.dismiss();
                    Toast.makeText(getApplicationContext(),"Friend added successfully",Toast.LENGTH_LONG).show();
                } else if (type.equals(liked)){
                    Toast.makeText(getApplicationContext(),"Liked successfully",Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    private void sendNotification(String type) {

        if (type.equals(contact)) {
            PublishOptions publishOptions = new PublishOptions();
            publishOptions.putHeader("android-ticker-text", prefs.getname());
            publishOptions.putHeader("android-content-title", user.getUsername());
            publishOptions.putHeader("android-content-text", "I found your profile interesting. I would like to get to know you more");

            Backendless.Messaging.publish(user.getUsername(), "chat," + contact_message.getObjectId(), publishOptions, new AsyncCallback<MessageStatus>() {
                @Override
                public void handleResponse(MessageStatus response) {
                    dialog.dismiss();
                    Toast.makeText(getApplicationContext(), "Message sent successfully. Please check in chats", Toast.LENGTH_LONG).show();
                }

                @Override
                public void handleFault(BackendlessFault fault) {
                    //do nothing
                    dialog.dismiss();
                    Toast.makeText(getApplicationContext(), "Message sent successfully. Please check in chats", Toast.LENGTH_LONG).show();
                }
            });
        } else if (type.equals(fav)){
            PublishOptions publishOptions = new PublishOptions();
            publishOptions.putHeader("android-ticker-text", "You are added as favourite");
            publishOptions.putHeader("android-content-title", "VeMeet");
            publishOptions.putHeader("android-content-text", prefs.getname()+" added you as a favourite");

            Backendless.Messaging.publish(user.getUsername(), "fav", publishOptions, new AsyncCallback<MessageStatus>() {
                @Override
                public void handleResponse(MessageStatus response) {
                    dialog.dismiss();
                    Toast.makeText(getApplicationContext(),"Favourite added successfully",Toast.LENGTH_LONG).show();
                }

                @Override
                public void handleFault(BackendlessFault fault) {
                    dialog.dismiss();
                    Toast.makeText(getApplicationContext(),"Favourite added successfully",Toast.LENGTH_LONG).show();
                }
            });
        } else if (type.equals(friend)){
            PublishOptions publishOptions = new PublishOptions();
            publishOptions.putHeader("android-ticker-text", "You are added as friend");
            publishOptions.putHeader("android-content-title", "VeMeet");
            publishOptions.putHeader("android-content-text", prefs.getname()+" added you as a friend");

            Backendless.Messaging.publish(user.getUsername(), "frnd", publishOptions, new AsyncCallback<MessageStatus>() {
                @Override
                public void handleResponse(MessageStatus response) {
                    dialog.dismiss();
                    Toast.makeText(getApplicationContext(),"Friend added successfully",Toast.LENGTH_LONG).show();
                }

                @Override
                public void handleFault(BackendlessFault fault) {
                    dialog.dismiss();
                    Toast.makeText(getApplicationContext(),"Friend added successfully",Toast.LENGTH_LONG).show();
                }
            });
        } else if (type.equals(liked)){
            PublishOptions publishOptions = new PublishOptions();
            publishOptions.putHeader("android-ticker-text", "You are added as friend");
            publishOptions.putHeader("android-content-title", "VeMeet");
            publishOptions.putHeader("android-content-text", prefs.getname()+" liked you");

            Backendless.Messaging.publish(user.getUsername(), "frnd", publishOptions, new AsyncCallback<MessageStatus>() {
                @Override
                public void handleResponse(MessageStatus response) {
                    dialog.dismiss();
                    Toast.makeText(getApplicationContext(),"Liked successfully",Toast.LENGTH_LONG).show();
                }

                @Override
                public void handleFault(BackendlessFault fault) {
                    dialog.dismiss();
                    Toast.makeText(getApplicationContext(),"Liked successfully",Toast.LENGTH_LONG).show();
                }
            });
        }

    }


}