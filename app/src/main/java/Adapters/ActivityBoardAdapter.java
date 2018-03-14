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
import com.squareup.picasso.Picasso;

import org.w3c.dom.Text;

import java.util.Calendar;
import java.util.List;

import Models.Activity;
import Models.Message;
import Util.Prefs;
import cn.pedant.SweetAlert.SweetAlertDialog;
import ssapps.com.datingapp.R;
import ssapps.com.datingapp.RecyclerViewClickListener;

public class ActivityBoardAdapter extends RecyclerView.Adapter<ActivityBoardAdapter.MyViewHolder>{

    private Context context;
    //private RecyclerViewClickListener mListener;
    private List<Activity> activities;
    private Prefs prefs;
    private SweetAlertDialog dialog,error;
    private static final String appKey = "7EEB2727-4E8D-944C-FFDD-3D802BC37800";
    private static final String appId = "648D896E-EDD8-49C8-FF74-2F1C32DB7A00";
    private static final String GCM_SENDER_ID = "57050948456";
    private Message contact_message;

    public ActivityBoardAdapter(Context context, List<Activity> activities) {
        this.context = context;
        this.activities = activities;
        prefs = new Prefs(context);
        dialog = new SweetAlertDialog(context,SweetAlertDialog.PROGRESS_TYPE);
        dialog.setTitleText("Contacting...");
        dialog.setCancelable(false);
        error = new SweetAlertDialog(context,SweetAlertDialog.ERROR_TYPE);
        Backendless.initApp(context,appId,appKey);
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.activity_board_row, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        final Activity activity = activities.get(position);
        holder.subject.setText(activity.getSubject());
        holder.date.setText(getDate(activity.getTime()));
        holder.location.setText(activity.getCity()+","+activity.getCountry());
        if (activity.getHasPicture().equals("Yes")){
            Picasso.with(context).load(activity.getPictureUrl()).placeholder(context.getResources().getDrawable(R.drawable.fb)).into(holder.picture);
        }
        holder.description.setText(activity.getDescription());
        holder.owner.setText(activity.getUser());

        holder.remove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                removeItem(activity);
            }
        });



//        holder.contact_Button.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                contactPerson(activity);
//            }
//        });
    }

    private void removeItem(final Activity activity) {
        dialog.setTitle("Removing activity");
        dialog.show();

        Backendless.Persistence.of(Activity.class).remove(activity, new AsyncCallback<Long>() {
            @Override
            public void handleResponse(Long response) {
                dialog.dismiss();
                activity.delete();
                activities.remove(activity);
                notifyDataSetChanged();
            }

            @Override
            public void handleFault(BackendlessFault fault) {
                dialog.dismiss();
                error.setTitle("Error occured");
                error.setContentText("The following error occured while trying to remove the object\n"+fault.getMessage()+"\n Please try again");
                error.show();
            }
        });

    }

//    private void contactPerson(final Activity activity) {
//
//
//            final Calendar calendar = Calendar.getInstance();
//
//            final Message message = new Message();
//           // message.setFrom(prefs.getname());
//        message.setMessage_from(prefs.getname());
//         //   message.setTo(activity.getUser());
//        message.setMessage_to(activity.getUser());
//            message.setTime(String.valueOf(calendar.getTimeInMillis()));
//            message.setChat_message("I found your activity on "+getDate(activity.getTime())+". I would like to be part of it");
//
//           // dialog.setTitleText("Sending message...");
//            dialog.show();
//
//            Backendless.Data.save(message, new AsyncCallback<Message>() {
//                @Override
//                public void handleResponse(Message response) {
//                   //  dialog.dismiss();
//                    response.save();
//                    contact_message = response;
//                    //sendNotification(activity);
//                    checkPushNotificationRegistration(activity);
//                   // Toast.makeText(context,"Notification sent sucessfully. Please check in chats",Toast.LENGTH_LONG).show();
//                }
//
//                @Override
//                public void handleFault(BackendlessFault fault) {
//                    dialog.dismiss();
//                    error.setTitleText("Error sending messge");
//                    error.setContentText("The following error has occured while sending message\n"+
//                            fault.getMessage()+"\n Please try again");
//                    error.show();
//                }
//            });
//
//
//    }

//    private void checkPushNotificationRegistration(final Activity activity) {
//        Backendless.Messaging.getDeviceRegistration(new AsyncCallback<DeviceRegistration>() {
//            @Override
//            public void handleResponse(DeviceRegistration response) {
//                if (response.getChannels().contains(prefs.getname())){
//                    sendNotification(activity);
//                } else {
//                    registerDevice(activity);
//                }
//            }
//
//            @Override
//            public void handleFault(BackendlessFault fault) {
//                //do nothing
//                dialog.dismiss();
//                Toast.makeText(context,"Message sent successfully. Please check in chats",Toast.LENGTH_LONG).show();
//            }
//        });
//    }

//    private void registerDevice(final Activity activity) {
//        Backendless.Messaging.registerDevice(GCM_SENDER_ID, prefs.getname(), new AsyncCallback<Void>() {
//            @Override
//            public void handleResponse(Void response) {
//                sendNotification(activity);
//            }
//
//            @Override
//            public void handleFault(BackendlessFault fault) {
//                //do nothing
//                dialog.dismiss();
//                Toast.makeText(context,"Message sent successfully. Please check in chats",Toast.LENGTH_LONG).show();
//            }
//        });
//    }

//    private void sendNotification(Activity activity) {
//        PublishOptions publishOptions = new PublishOptions();
//        publishOptions.putHeader("android-ticker-text", contact_message.getObjectId());
//        publishOptions.putHeader("android-content-title", prefs.getname());
//        publishOptions.putHeader("android-content-text", "About activity on "+getDate(activity.getTime()));
//
//        Backendless.Messaging.publish(prefs.getname(), "message", publishOptions, new AsyncCallback<MessageStatus>() {
//            @Override
//            public void handleResponse(MessageStatus response) {
//                dialog.dismiss();
//                Toast.makeText(context,"Message sent successfully. Please check in chats",Toast.LENGTH_LONG).show();
//            }
//
//            @Override
//            public void handleFault(BackendlessFault fault) {
//                //do nothing
//                dialog.dismiss();
//                Toast.makeText(context,"Message sent successfully. Please check in chats",Toast.LENGTH_LONG).show();
//            }
//        });
//    }


    private String getDate(Long time) {
        Calendar c = Calendar.getInstance();
        c.setTimeInMillis(time);
        return  String.valueOf(c.get(Calendar.DATE))+"."+
                String.valueOf(c.get(Calendar.MONTH))+"."+String.valueOf(c.get(Calendar.YEAR));
    }

    @Override
    public int getItemCount() {
        return activities.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder{

        public TextView subject,date,location,description,owner;
        public ImageView picture,remove;
       // public Button contact_Button;

        public MyViewHolder(View itemView) {
            super(itemView);

            subject = (TextView) itemView.findViewById(R.id.subject_tv);
            date = (TextView) itemView.findViewById(R.id.date_tv);
            location = (TextView)itemView.findViewById(R.id.location_tv);
            picture = (ImageView)itemView.findViewById(R.id.activiy_picture);
            description = (TextView)itemView.findViewById(R.id.acitivity_description);
            owner = (TextView)itemView.findViewById(R.id.activity_owner);
            remove = (ImageView)itemView.findViewById(R.id.remoove_button);
           // contact_Button = (Button)itemView.findViewById(R.id.contact_button);
        }
    }


}