package ssapps.com.datingapp;

import android.databinding.DataBindingUtil;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.backendless.Backendless;
import com.backendless.DeviceRegistration;
import com.backendless.Subscription;
import com.backendless.async.callback.AsyncCallback;
import com.backendless.exceptions.BackendlessFault;
import com.backendless.messaging.MessageStatus;
import com.backendless.messaging.PublishOptions;
import com.orm.SugarContext;
import com.orm.query.Select;

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

public class ChatActivity extends AppCompatActivity implements View.OnClickListener,EditText.OnEditorActionListener{

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
    private IndividualChats temp;
    private static final String ANDROID_TICKER_TEXT = "android-ticker-text";
    private static final String ANDROID_CONTENT_TITLE = "android-content-title";
    private static final String ANDROID_CONTENT_TEXT = "android-content-text";
    private static final String GCM_SENDER_ID = "57050948456";
    private static final String appKey = "7EEB2727-4E8D-944C-FFDD-3D802BC37800";
    private static final String appId = "648D896E-EDD8-49C8-FF74-2F1C32DB7A00";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.activity_chat);
        Log.v("on create called","yes");
        binding = DataBindingUtil.setContentView(this,R.layout.activity_chat);
        Backendless.initApp(this,appId,appKey);
        SugarContext.init(this);
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

//            List<Message> messages = Message.findWithQuery(Message.class,"SELECT * FROM Message WHERE " +
//                    "message_from = '"+otherUser+"' or message_to = '"+otherUser+"' ORDER BY time ASC",null);

            //ist<Message> messages = Select.from(Message.class).where("message_from = ?",);

            if (IndividualChats.count(IndividualChats.class) > 0){
                IndividualChats.deleteAll(IndividualChats.class);
            }

            List<Message> messages = new ArrayList<>();
            for (Message message:Message.listAll(Message.class)){
                if (message.getMessage_from().equals(otherUser) || message.getMessage_to().equals(otherUser)){
                 temp = new IndividualChats(message);
                 temp.save();
                }
            }


//            for (Message message:messages){
//                IndividualChats chat = new IndividualChats(message);
//                chat.save();
//                chats.add(chat);
//            }

            chats = Select.from(IndividualChats.class).orderBy("time asc").list();

            adapter = new ChatAdapter(this,chats);
            binding.chat.setAdapter(adapter);

            subScribeToChat();

            binding.sendButton.setOnClickListener(this);
        }


    }

    private void subScribeToChat() {

        Log.v("subscibe to chat","called");
        Backendless.Messaging.subscribe(loggedUser, 1000, new AsyncCallback<List<com.backendless.messaging.Message>>() {
            @Override
            public void handleResponse(List<com.backendless.messaging.Message> response) {
                Log.v("sunscribed to chat","Success");
                prefs.setIsInChat(true);
                for (int i = 0;i<response.size();i++){
                    Log.v("recieved chat",response.get(i).toString());
                    Log.v("recieved chat headers", String.valueOf(response.get(i).getHeaders()));
                    Log.v("recv",response.get(i).getHeaders().get(ANDROID_CONTENT_TEXT));
                    Log.v("recv",response.get(i).getHeaders().get(ANDROID_CONTENT_TITLE));
                    Log.v("recv",response.get(i).getHeaders().get(ANDROID_TICKER_TEXT));
                    Log.v("rev",response.get(i).getData().toString());



                  //  if(checkForObjectID(response.get(i).getData().toString())){
                        Log.v("objectid","doesnt exist");
                   // if (Message.count(Message.class,"objectId = ?", new String[]{response.get(i).getData().toString()}) == 0){
                        Message newMessage = new Message();
                        newMessage.setId(Message.count(Message.class)+1);
                        newMessage.setMessage_to(response.get(i).getHeaders().get(ANDROID_CONTENT_TITLE));
                        newMessage.setMessage_from(response.get(i).getHeaders().get(ANDROID_TICKER_TEXT));
                        newMessage.setType("chat");
                        newMessage.setTime(String.valueOf(response.get(i).getTimestamp()));
                        newMessage.setChat_message(response.get(i).getHeaders().get(ANDROID_CONTENT_TEXT));
                        newMessage.setObjectId(response.get(i).getData().toString().substring(response.get(i).getData().toString().indexOf(",")+1));
                        Log.v("obj",response.get(i).getData().toString().substring(response.get(i).getData().toString().indexOf(",")+1));

                        newMessage.save();

                        temp = new IndividualChats(newMessage);
                        temp.save();
                        chats.add(temp);
                        adapter.notifyDataSetChanged();
                 //   } else {
                  //      Log.v("object id","exists");
                   // }

                }

//                publishOptions.putHeader("android-ticker-text", loggedUser);
//                publishOptions.putHeader("android-content-title", otherUser);
//                publishOptions.putHeader("android-content-text", message.getChat_message());
//                IndividualChats chat = new IndividualChats((Message) response.get(0).getData());
//                chat.save();
//                chats.add(chat);
//                for (int i = 0;i<response.size();i++){
//                    //if (Message.count(Message.class,"objectId = ?",(Message)response.get(i).getData().)))
//                    IndividualChats chat = new IndividualChats((Message) response.get(i).getData());
//                    if (Message.count(Message.class,"objectId = ?", new String[]{chat.getObjectId()}) == 0){
//                        Message newMessage = (Message)response.get(i).getData();
//                        newMessage.setId(Message.count(Message.class)+1);
//                        newMessage.save();
//                        chat.setId(newMessage.getId());
//                        chat.save();
//                        chats.add(chat);
//                        adapter.notifyDataSetChanged();
//                    }
//
//                }
            }

            @Override
            public void handleFault(BackendlessFault fault) {
                prefs.setIsInChat(false);
                Log.v("subscription fault",fault.toString());
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

    private boolean checkForObjectID(String objectId) {

        List<Message> allMessages = Message.listAll(Message.class);
        if (allMessages.size() == 0){
            return true;
        } else {
            for (Message message:allMessages){
                if (message.getObjectId().equals(objectId)){
                    return false;
                }
            }
        }

        return true;

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
            Log.v("button ","clicked");
            if (util.checkEditTextField(binding.messageEt)){
                binding.sendButton.setEnabled(false);
                Message message = new Message();
//                message.setFrom(loggedUser);
//                message.setTo(otherUser);
                message.setMessage_from(loggedUser);
                message.setMessage_to(otherUser);
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
        Log.v("message","sending");
        Backendless.Data.save(message, new AsyncCallback<Message>() {
            @Override
            public void handleResponse(Message response) {
                Log.v("Message","sent");
                binding.messageEt.setText("");
                binding.sendButton.setEnabled(true);
                response.setId(Message.count(Message.class)+1);
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
        Log.v("device registration","checking");
        Backendless.Messaging.getDeviceRegistration(new AsyncCallback<DeviceRegistration>() {
            @Override
            public void handleResponse(DeviceRegistration response) {
                isCheckedForNotificationChannelRedistration = true;
                if (!(response.getChannels().contains(loggedUser))){
                    Log.v("device registration","exists");
                    publishNotification(message);
                } else {
                    Log.v("device registration","doesnt exist");
                    register(message);
                }
            }

            @Override
            public void handleFault(BackendlessFault fault) {
                Log.v("device registration","fault "+fault.toString());
                isCheckedForNotificationChannelRedistration = false;
                register(message);
            }
        });

    }

    private void register(final Message message) {
        Log.v("push notification","registering");
        Backendless.Messaging.registerDevice(GCM_SENDER_ID, loggedUser, new AsyncCallback<Void>() {
            @Override
            public void handleResponse(Void response) {
                Log.v("push notification","device registered");
                    publishNotification(message);
            }

            @Override
            public void handleFault(BackendlessFault fault) {
                Log.v("push notification","registration fault "+fault.toString());
                isCheckedForNotificationChannelRedistration = false;
            }
        });
    }

    private void publishNotification(Message message) {
        Log.v("push notification","publishing");
        PublishOptions publishOptions = new PublishOptions();
        publishOptions.putHeader("android-ticker-text", loggedUser);
        publishOptions.putHeader("android-content-title", otherUser);
        publishOptions.putHeader("android-content-text", message.getChat_message());

        Backendless.Messaging.publish(otherUser, "chat,"+message.getObjectId(), publishOptions, new AsyncCallback<MessageStatus>() {
            @Override
            public void handleResponse(MessageStatus response) {
                //do nothing
                Log.v("push notification","published successfully");
            }

            @Override
            public void handleFault(BackendlessFault fault) {
                //do nothing
                Log.v("push notification","publish fault "+fault.toString());
            }
        });
    }


    @Override
    public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
        InputMethodManager imm = (InputMethodManager)getSystemService(INPUT_METHOD_SERVICE);
        if (imm != null){
            imm.hideSoftInputFromWindow(binding.messageEt.getWindowToken(),0);
        }
        return true;
    }
}
