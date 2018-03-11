package ssapps.com.datingapp;

import android.databinding.DataBindingUtil;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.backendless.Backendless;
import com.backendless.DeviceRegistration;
import com.backendless.Subscription;
import com.backendless.async.callback.AsyncCallback;
import com.backendless.exceptions.BackendlessFault;
import com.backendless.messaging.MessageStatus;
import com.backendless.messaging.PublishOptions;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import Adapters.ChatAdapter;
import Models.IndividualChats;
import Models.Message;
import Util.Prefs;
import Util.Util;
import cn.pedant.SweetAlert.SweetAlertDialog;
import ssapps.com.datingapp.databinding.ActivityChatBinding;

public class ChatActivity extends AppCompatActivity implements View.OnClickListener{

    private ActivityChatBinding binding;
    private String loggedUser,otherUser;
    private Prefs prefs;
  //  private List<Message> chats = new ArrayList<>();
    private ChatAdapter adapter;
    private SweetAlertDialog error;
    private List<IndividualChats> chats = new ArrayList<>();
    private Subscription subscription;
    private Util util;
    private boolean isCheckedForNotificationChannelRedistration = false;

    private static final String GCM_SENDER_ID = "57050948456";
    private static final String appKey = "7EEB2727-4E8D-944C-FFDD-3D802BC37800";
    private static final String appId = "648D896E-EDD8-49C8-FF74-2F1C32DB7A00";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.activity_chat);
        binding = DataBindingUtil.setContentView(this,R.layout.activity_chat);
        Backendless.initApp(this,appId,appKey);
        util = new Util();

        error = new SweetAlertDialog(this,SweetAlertDialog.ERROR_TYPE);

        ActionBar actionBar = getSupportActionBar();

        actionBar.setHomeButtonEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayShowHomeEnabled(true);

        prefs = new Prefs(this);
        loggedUser = prefs.getname();

        if (getIntent().hasExtra("user")){
            otherUser = getIntent().getStringExtra("user");

            List<Message> messages = Message.findWithQuery(Message.class,"SELECT * FROM Message WHERE " +
                    "from = '"+otherUser+"' or to = '"+otherUser+"' ORDER BY time ASC",null);

            for (Message message:messages){
                IndividualChats chat = new IndividualChats(message);
                chat.save();
                chats.add(chat);
            }

            adapter = new ChatAdapter(this,chats);
            binding.chat.setAdapter(adapter);

            subScribeToChat();

            binding.sendButton.setOnClickListener(this);
        }


    }

    private void subScribeToChat() {


        Backendless.Messaging.subscribe(loggedUser, 1000, new AsyncCallback<List<com.backendless.messaging.Message>>() {
            @Override
            public void handleResponse(List<com.backendless.messaging.Message> response) {
                prefs.setIsInChat(true);
                IndividualChats chat = new IndividualChats((Message) response.get(0).getData());
                chat.save();
                chats.add(chat);
                adapter.notifyDataSetChanged();
            }

            @Override
            public void handleFault(BackendlessFault fault) {
                prefs.setIsInChat(false);
                error.setTitleText("Cannot recieve chat now");
                error.setContentText("The following fault occured while connect to chat via VeMeet\n" +
                        fault.getMessage() + "\n Please try again later by reopening the page");
                error.show();
            }
        }, new AsyncCallback<Subscription>() {
            @Override
            public void handleResponse(Subscription response) {
                subscription = response;
            }

            @Override
            public void handleFault(BackendlessFault fault) {
                prefs.setIsInChat(false);
                error.setTitleText("Cannot recieve chat now");
                error.setContentText("The following fault occured while connect to chat via VeMeet\n" +
                        fault.getMessage() + "\n Please try again later by reopening the page");
                error.show();
            }
        });

    }



    @Override
    public void onBackPressed() {
        this.finish();
        prefs.setIsInChat(false);
        if (subscription != null){
            subscription.cancelSubscription();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        prefs.setIsInChat(false);
        if (subscription != null){
            subscription.cancelSubscription();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        prefs.setIsInChat(false);
        if (subscription != null){
            subscription.cancelSubscription();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        prefs.setIsInChat(false);
        if (subscription != null){
            subscription.cancelSubscription();
        }
    }



    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    @Override
    public void onClick(View view) {

        if (view.getId() == R.id.send_button){
            binding.sendButton.setEnabled(false);
            if (util.checkEditTextField(binding.messageEt)){
                Message message = new Message();
//                message.setFrom(loggedUser);
//                message.setTo(otherUser);
                message.setMessage_from(loggedUser);
                message.setType(otherUser);
                message.setChat_message(binding.messageEt.getText().toString());
                message.setTime(String.valueOf(Calendar.getInstance().getTimeInMillis()));
                message.setType("Chat");
                chats.add(new IndividualChats(message));
                adapter.notifyDataSetChanged();
                saveChatMessage(message);
            } else {
                Toast.makeText(this,"Please write a message",Toast.LENGTH_LONG).show();
            }

        }

    }

    private void saveChatMessage(final Message message) {
        Backendless.Data.save(message, new AsyncCallback<Message>() {
            @Override
            public void handleResponse(Message response) {
                binding.sendButton.setEnabled(true);
                response.save();
                new IndividualChats(response).save();

                if (isCheckedForNotificationChannelRedistration){
                    publishNotification(response);
                } else {
                    checkForNoification(response);
                }
            }

            @Override
            public void handleFault(BackendlessFault fault) {
                binding.sendButton.setEnabled(true);
                Toast.makeText(getApplicationContext(),"Cannot send message now. Please try again later",Toast.LENGTH_LONG).show();
                chats.remove(chats.size() - 1);
                adapter.notifyDataSetChanged();
            }
        });
    }

    private void checkForNoification(final Message message) {

        Backendless.Messaging.getDeviceRegistration(new AsyncCallback<DeviceRegistration>() {
            @Override
            public void handleResponse(DeviceRegistration response) {
                isCheckedForNotificationChannelRedistration = true;
                if (!(response.getChannels().contains(loggedUser))){
                    publishNotification(message);
                } else {
                    register(message);
                }
            }

            @Override
            public void handleFault(BackendlessFault fault) {
                isCheckedForNotificationChannelRedistration = false;
            }
        });

    }

    private void register(final Message message) {
        Backendless.Messaging.registerDevice(GCM_SENDER_ID, loggedUser, new AsyncCallback<Void>() {
            @Override
            public void handleResponse(Void response) {
                    publishNotification(message);
            }

            @Override
            public void handleFault(BackendlessFault fault) {
                isCheckedForNotificationChannelRedistration = false;
            }
        });
    }

    private void publishNotification(Message message) {
        PublishOptions publishOptions = new PublishOptions();
        publishOptions.putHeader("android-ticker-text", loggedUser);
        publishOptions.putHeader("android-content-title", otherUser);
        publishOptions.putHeader("android-content-text", message.getChat_message());

        Backendless.Messaging.publish(loggedUser, "Chat", publishOptions, new AsyncCallback<MessageStatus>() {
            @Override
            public void handleResponse(MessageStatus response) {
                //do nothing
            }

            @Override
            public void handleFault(BackendlessFault fault) {
                //do nothing
            }
        });
    }


}
