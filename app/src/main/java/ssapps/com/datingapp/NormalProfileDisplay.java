package ssapps.com.datingapp;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.backendless.Backendless;
import com.backendless.DeviceRegistration;
import com.backendless.Media;
import com.backendless.async.callback.AsyncCallback;
import com.backendless.exceptions.BackendlessFault;
import com.backendless.messaging.MessageStatus;
import com.backendless.messaging.PublishOptions;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.orm.SugarContext;
import com.squareup.picasso.Picasso;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import Models.Message;
import Models.User;
import Util.Prefs;
import Util.Util;
import cn.pedant.SweetAlert.SweetAlertDialog;
import ssapps.com.datingapp.databinding.ActivityNormalProfileDisplayBinding;

public class NormalProfileDisplay extends AppCompatActivity implements View.OnClickListener {

    private ActivityNormalProfileDisplayBinding binding;
    private static final String appKey = "7EEB2727-4E8D-944C-FFDD-3D802BC37800";
    private static final String appId = "648D896E-EDD8-49C8-FF74-2F1C32DB7A00";
    private static final String GCM_SENDER_ID = "57050948456";
    private Prefs prefs;
    private Util util;
    private User currentUser;
    private SweetAlertDialog dialog,error,confirmDialog;
    private Message contact_message;
    private DatabaseReference mDatabase;
    private static final String fav = "Favourites";
    private static final String friend = "Friends";
    private static final String contact = "contact";
    private static final String block = "Blocked";
    private static final String liked = "Liked";

//   class Favourites{
//        String time,name;
//        public Favourites(){
//
//        }
//
//        private Favourites(Long time,String name){
//            this.time = String.valueOf(time);
//            this.name = name;
//        }
//    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
       // setContentView(R.layout.activity_normal_profile_display);
        binding = DataBindingUtil.setContentView(this,R.layout.activity_normal_profile_display);

        Backendless.initApp(this,appId,appKey);
        SugarContext.init(this);
        util = new Util();
        prefs = new Prefs(this);

        ActionBar actionBar = getSupportActionBar();

        actionBar.setHomeButtonEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayShowHomeEnabled(true);

        dialog = new SweetAlertDialog(this,SweetAlertDialog.PROGRESS_TYPE);
        dialog.setTitle("Contacting....");
        dialog.setCancelable(false);

        error = new SweetAlertDialog(this,SweetAlertDialog.ERROR_TYPE);



        binding.profileImage.getLayoutParams().height = (int) (util.getScreenHeight(this) / 3);



        if (getIntent().hasExtra("name")){
            currentUser = User.find(User.class,"username = ?",getIntent().getStringExtra("name")).get(0);


            if (currentUser.getHasPicture().equals("Yes")){
                Picasso.with(this).load(currentUser.getPhotourl()).placeholder(getResources().getDrawable(R.drawable.fb))
                        .into(binding.profileImage);
            }

            binding.abtMeTv.setText(currentUser.getAboutme());
            binding.ageTv.setText(currentUser.getAge_self());
            binding.locationTv.setText(currentUser.getCity_self()+","+currentUser.getCountry_self());
            binding.lifestyleTv.setText(currentUser.getLifestyle_self());
            binding.sexualOrientationTv.setText(currentUser.getSexual_orientation_self());
            binding.genderTv.setText(currentUser.getGender_self());
            binding.statusTv.setText(currentUser.getStatus_self());
            binding.childrenTv.setText(currentUser.getChildren_self());
            binding.smokingTv.setText(currentUser.getSmoking_self());
            binding.religionTv.setText(currentUser.getReligin_self());
            binding.drinkingTv.setText(currentUser.getDrinking_self());
            binding.heightTv.setText(currentUser.getHeight_self());
            binding.hairColorTv.setText(currentUser.getHaircolor_self());
            binding.eyeColorTv.setText(currentUser.getEyecoloe_self());

            binding.contactButton.setOnClickListener(this);
            binding.stactChatIcon.setOnClickListener(this);
            binding.favouriteAddIcon.setOnClickListener(this);
            binding.addFriendIcon.setOnClickListener(this);
            binding.blockUserIcon.setOnClickListener(this);
            binding.likeIcon.setOnClickListener(this);
        }

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        this.finish();
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.contact_button:
                contactPerson(contact);
                break;
            case R.id.stact_chat_icon:
                Log.v("start chat with",currentUser.getUsername());
                Intent i = new Intent(NormalProfileDisplay.this,ChatActivity.class);
                i.putExtra("user",currentUser.getUsername());
                startActivity(i);
                break;
            case R.id.favourite_add_icon:
                addFavourite();
                break;
            case R.id.add_friend_icon:
                addFriend();
                break;
            case R.id.block_user_icon:
                blockUser();
                break;
            case R.id.like_icon:
                likeUser();
                break;
            //todo
        }
    }

    private void likeUser() {
        confirmDialog = new SweetAlertDialog(this,SweetAlertDialog.NORMAL_TYPE);
        confirmDialog.setConfirmButton("Like", new SweetAlertDialog.OnSweetClickListener() {
            @Override
            public void onClick(SweetAlertDialog sweetAlertDialog) {
                Log.v("confirmed",currentUser.getUsername());
                confirmLike(currentUser.getUsername());
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
        confirmDialog.setContentText("Are you sure you want to like "+currentUser.getUsername());

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
                Log.v("confirmed",currentUser.getUsername());
                confirmBlock(currentUser.getUsername());
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
        confirmDialog.setContentText("Are you sure you want to block "+currentUser.getUsername());

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
                Log.v("confirmed",currentUser.getUsername());
                confirmFriend(currentUser.getUsername());
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
        confirmDialog.setContentText("Are you sure you want to add "+currentUser.getUsername()+" as a friend?");

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
                Log.v("confirmed",currentUser.getUsername());
                confirmFavourite(currentUser.getUsername());
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
        confirmDialog.setContentText("Are you sure you want to add "+currentUser.getUsername()+" as a favourite?");

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
        message.setMessage_to(currentUser.getUsername());
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

        Backendless.Messaging.registerDevice(GCM_SENDER_ID, currentUser.getUsername(), new AsyncCallback<Void>() {
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
            publishOptions.putHeader("android-content-title", currentUser.getUsername());
            publishOptions.putHeader("android-content-text", "I found your profile interesting. I would like to get to know you more");

            Backendless.Messaging.publish(currentUser.getUsername(), "chat," + contact_message.getObjectId(), publishOptions, new AsyncCallback<MessageStatus>() {
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

            Backendless.Messaging.publish(currentUser.getUsername(), "fav", publishOptions, new AsyncCallback<MessageStatus>() {
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

            Backendless.Messaging.publish(currentUser.getUsername(), "frnd", publishOptions, new AsyncCallback<MessageStatus>() {
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

            Backendless.Messaging.publish(currentUser.getUsername(), "frnd", publishOptions, new AsyncCallback<MessageStatus>() {
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

