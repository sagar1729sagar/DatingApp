package Adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.backendless.Backendless;
import com.backendless.DeviceRegistration;
import com.backendless.async.callback.AsyncCallback;
import com.backendless.exceptions.BackendlessFault;
import com.backendless.messaging.MessageStatus;
import com.backendless.messaging.PublishOptions;
import com.orm.SugarContext;
import com.squareup.picasso.Picasso;

import java.util.Calendar;
import java.util.List;

import Models.Message;
import Models.SavedActivities;
import Models.SearchedActivities;
import Util.Prefs;
import cn.pedant.SweetAlert.SweetAlertDialog;
import ssapps.com.datingapp.R;

/**
 * Created by sagar on 15/03/18.
 */

public class SaveForLaterAdapter extends RecyclerView.Adapter<SaveForLaterAdapter.MyViewHolder> {

    private Context context;
    //private RecyclerViewClickListener mListener;
    private List<SavedActivities> activities;
    private Prefs prefs;
    private SweetAlertDialog dialog,error;
    private static final String appKey = "7EEB2727-4E8D-944C-FFDD-3D802BC37800";
    private static final String appId = "648D896E-EDD8-49C8-FF74-2F1C32DB7A00";
    private static final String GCM_SENDER_ID = "57050948456";
    private Message contact_message;


    public SaveForLaterAdapter(Context context, List<SavedActivities> activities) {
        this.context = context;
        this.activities = activities;
        prefs = new Prefs(context);
        dialog = new SweetAlertDialog(context,SweetAlertDialog.PROGRESS_TYPE);
        dialog.setTitleText("Contacting...");
        dialog.setCancelable(false);
        error = new SweetAlertDialog(context,SweetAlertDialog.ERROR_TYPE);
        Backendless.initApp(context,appId,appKey);
        SugarContext.init(context);
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.save_for_later_row,parent,false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        final SavedActivities activity =  activities.get(position);
        holder.subject.setText(activity.getSubject());
        holder.date.setText(activity.getDateActivity());
        holder.location.setText(activity.getCity()+","+activity.getCountry());
        if (activity.getHasPicture().equals("Yes")){
            Picasso.with(context).load(activity.getPictureUrl()).placeholder(context.getResources()
                    .getDrawable(R.drawable.fb)).into(holder.image);
        }
        holder.description.setText(activity.getDescription());
        holder.posted.setText("Posted by "+activity.getUser());
//       older.save.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                activity.save();
//            }
//        });



//        if (SavedActivities.find(SavedActivities.class,"object_id = ?",activity.getObjectId()).size() > 0){
//            holder.save.setText("ALREADY SAVED");
//            holder.save.setEnabled(false);
//        } else {
//            holder.save.setText("SAVE FOR LATER");
//            holder.save.setEnabled(true);
//        }

        holder.contact.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                contactPerson(activity);
            }
        });

        holder.remove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                activities.remove(activity);
                SearchedActivities.delete(activity);
                notifyDataSetChanged();
            }
        });

    }

    private void contactPerson(final SavedActivities activity) {


        final Calendar calendar = Calendar.getInstance();

        final Message message = new Message();
        // message.setFrom(prefs.getname());
        message.setMessage_from(prefs.getname());
        //   message.setTo(activity.getUser());
        message.setMessage_to(activity.getUser());
        message.setTime(String.valueOf(calendar.getTimeInMillis()));
        message.setChat_message("I found your activity on " + activity.getDateActivity() + ". I would like to be part of it");

        // dialog.setTitleText("Sending message...");
        dialog.show();

        Backendless.Data.save(message, new AsyncCallback<Message>() {
            @Override
            public void handleResponse(Message response) {
                //  dialog.dismiss();
                response.setId(Message.count(Message.class));
                response.save();
                contact_message = response;
                //sendNotification(activity);
                checkPushNotificationRegistration(activity);
                // Toast.makeText(context,"Notification sent sucessfully. Please check in chats",Toast.LENGTH_LONG).show();
            }

            @Override
            public void handleFault(BackendlessFault fault) {
                dialog.dismiss();
                error.setTitleText("Error sending messge");
                error.setContentText("The following error has occured while sending message\n" +
                        fault.getMessage() + "\n Please try again");
                error.show();
            }
        });
    }

    private void checkPushNotificationRegistration(final SavedActivities activity) {
        Backendless.Messaging.getDeviceRegistration(new AsyncCallback<DeviceRegistration>() {
            @Override
            public void handleResponse(DeviceRegistration response) {
                if (response.getChannels().contains(prefs.getname())){
                    sendNotification(activity);
                } else {
                    registerDevice(activity);
                }
            }

            @Override
            public void handleFault(BackendlessFault fault) {
                registerDevice(activity);
                //do nothing
                //  dialog.dismiss();
                // Toast.makeText(context,"Message sent successfully. Please check in chats",Toast.LENGTH_LONG).show();
            }
        });
    }

    private void registerDevice(final SavedActivities activity) {
        Backendless.Messaging.registerDevice(GCM_SENDER_ID, prefs.getname(), new AsyncCallback<Void>() {
            @Override
            public void handleResponse(Void response) {
                sendNotification(activity);
            }

            @Override
            public void handleFault(BackendlessFault fault) {
                //do nothing
                dialog.dismiss();
                Toast.makeText(context,"Message sent successfully. Please check in chats",Toast.LENGTH_LONG).show();
            }
        });
    }

    private void sendNotification(SavedActivities activity) {
        PublishOptions publishOptions = new PublishOptions();
        publishOptions.putHeader("android-ticker-text", prefs.getname());
        publishOptions.putHeader("android-content-title", activity.getUser());
        publishOptions.putHeader("android-content-text", "About activity on "+activity.getDateActivity());

        Backendless.Messaging.publish(prefs.getname(), "chat,"+contact_message.getObjectId(), publishOptions, new AsyncCallback<MessageStatus>() {
            @Override
            public void handleResponse(MessageStatus response) {
                dialog.dismiss();
                Toast.makeText(context,"Message sent successfully. Please check in chats",Toast.LENGTH_LONG).show();
            }

            @Override
            public void handleFault(BackendlessFault fault) {
                //do nothing
                dialog.dismiss();
                Toast.makeText(context,"Message sent successfully. Please check in chats",Toast.LENGTH_LONG).show();
            }
        });
    }

    @Override
    public int getItemCount() {
        return activities.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder{

        TextView subject,date,location,description,posted;
        ImageView image,remove;
        Button contact;

        public MyViewHolder(View itemView) {
            super(itemView);

            subject = (TextView)itemView.findViewById(R.id.subject_tv);
            date = (TextView)itemView.findViewById(R.id.date_tv);
            location = (TextView)itemView.findViewById(R.id.location_tv);
            image = (ImageView)itemView.findViewById(R.id.activiy_picture);
            description = (TextView)itemView.findViewById(R.id.acitivity_description);
            posted = (TextView)itemView.findViewById(R.id.poseted_by);
            contact = (Button)itemView.findViewById(R.id.contact_button);
            remove = (ImageView)itemView.findViewById(R.id.remoove_button);
           // save = (Button)itemView.findViewById(R.id.save_button);
        }
    }
}
