package ssapps.com.datingapp;

import android.databinding.DataBindingUtil;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.backendless.Backendless;
import com.backendless.DeviceRegistration;
import com.backendless.async.callback.AsyncCallback;
import com.backendless.exceptions.BackendlessFault;
import com.backendless.messaging.MessageStatus;
import com.backendless.messaging.PublishOptions;
import com.orm.SugarContext;

import java.util.Calendar;

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
    private SweetAlertDialog dialog,error;
    private Message contact_message;


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
                contactPerson();
                break;
            //todo
        }
    }

    private void contactPerson() {
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

        Backendless.Messaging.registerDevice(GCM_SENDER_ID, currentUser.getUsername(), new AsyncCallback<Void>() {
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
        publishOptions.putHeader("android-ticker-text", contact_message.getObjectId());
        publishOptions.putHeader("android-content-title", currentUser.getUsername());
        publishOptions.putHeader("android-content-text", "I found your profile interesting. I would like to get to know you more");

        Backendless.Messaging.publish(currentUser.getUsername(), "message", publishOptions, new AsyncCallback<MessageStatus>() {
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

}
