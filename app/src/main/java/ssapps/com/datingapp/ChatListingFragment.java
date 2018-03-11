package ssapps.com.datingapp;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;

import com.backendless.Backendless;
import com.backendless.async.callback.AsyncCallback;
import com.backendless.exceptions.BackendlessFault;
import com.backendless.messaging.MessageStatus;
import com.backendless.persistence.BackendlessDataQuery;
import com.backendless.persistence.DataQueryBuilder;
import com.orm.SugarContext;
import com.orm.query.Select;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.SimpleTimeZone;

import Adapters.ChatListingAdapter;
import Models.Message;
import Models.MessagesSorting;
import Models.User;
import Util.Prefs;
import cn.pedant.SweetAlert.SweetAlertDialog;
import ssapps.com.datingapp.databinding.ActivityChatListingFragmentBinding;

public class ChatListingFragment extends Fragment{
    private ActivityChatListingFragmentBinding binding;
    private boolean silence;
    private String[] usersNames = {};
    private boolean isFirstTime,isFirstIteration,isUserFirstIteration;
    private List<MessagesSorting> messages = new ArrayList<>();
    private List<Message> intr_messages = new ArrayList<>();
    private SweetAlertDialog dialog,error;
    private Prefs prefs;
    private List<User> intr_users = new ArrayList<>();
    private static final String appKey = "7EEB2727-4E8D-944C-FFDD-3D802BC37800";
    private static final String appId = "648D896E-EDD8-49C8-FF74-2F1C32DB7A00";
    private ChatListingAdapter adapter;
    private MessagesSorting tempMessage;
    private Message temp;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater,R.layout.activity_chat_listing_fragment,container,false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {

        Backendless.initApp(getContext(),appId,appKey);
        SugarContext.init(getContext());

        prefs = new Prefs(getContext());

        dialog = new SweetAlertDialog(getContext(),SweetAlertDialog.PROGRESS_TYPE);
        dialog.setTitleText("Fetching data...");
        dialog.setCancelable(false);

        error = new SweetAlertDialog(getContext(),SweetAlertDialog.ERROR_TYPE);


        if (Message.count(Message.class) == 0){
            isFirstTime = true;
        } else {
            isFirstTime = false;
        }


        if (isFirstTime){
            dialog.show();
            getData();
        } else {
            getData();
            sortData(Message.listAll(Message.class));
        }





    }

    private void getData() {

        isFirstIteration = true;
     //   String whereClause = "messageFromn = '"+prefs.getname()+"' OR messageTo = '"+prefs.getname()+"'";
        DataQueryBuilder query = DataQueryBuilder.create();
        query.setPageSize(100);
     //   query.setWhereClause(whereClause);

        pullData(query);

    }

    private void pullData(final DataQueryBuilder query) {
        Backendless.Data.find(Message.class, query, new AsyncCallback<List<Message>>() {
            @SuppressLint("LongLogTag")
            @Override
            public void handleResponse(List<Message> response) {
                if (isFirstIteration){
                    intr_messages = Message.listAll(Message.class);
                    Message.deleteAll(Message.class);
                    isFirstIteration = false;
                }
                if (response.size() != 0){
                    Log.v("response sixw", String.valueOf(response.size()));
                   // Message.saveInTx(response);
//                    Message.saveInTx(response);
                    int id = (int) Message.count(Message.class);
                    for (int i=0;i<response.size();i++){
                        temp = response.get(i);
                        temp.setId(Long.valueOf(id+i+1));
                        temp.save();
                    }
                    Log.v("count", String.valueOf(Message.count(Message.class)));
                    query.prepareNextPage();
                    pullData(query);
                } else {
                    intr_messages.clear();
                    intr_messages = Message.listAll(Message.class);
                    Log.v("Messgaes recieved from server", String.valueOf(intr_messages.size()));
                    sortData(Message.listAll(Message.class));
                }
            }

            @Override
            public void handleFault(BackendlessFault fault) {
                if (isFirstTime){
                    dialog.dismiss();
                    error.setTitleText("Error fetching data");
                    error.setContentText("The foloowing error occured while fetching data\n"+fault.getMessage()+"\n Please try again");
                    error.show();
                } else if (!isFirstTime && !isFirstIteration){
                    Message.deleteAll(Message.class);
                    Message.saveInTx(intr_messages);
                }
            }
        });
    }

    @SuppressLint("LongLogTag")
    private void sortData(List<Message> messageList) {

        Log.v("sort data ","called");

        // add latest message

//        if (MessagesSorting.count(MessagesSorting.class) > 0){
//            MessagesSorting.deleteAll(MessagesSorting.class);
//        }
//
//        for (Message message:messageList){
//            MessagesSorting newMessage = new MessagesSorting(message);
//            newMessage.save();
//        }

        if (MessagesSorting.count(MessagesSorting.class) > 0){
            MessagesSorting.deleteAll(MessagesSorting.class);
            Log.v("All message sortings","deleted");
        } else {
            Log.v("Message sortings","not available");
        }

      for (Message message:messageList){
            Log.v("new message","called");
          List<MessagesSorting> sortings = new ArrayList<>();
          if (message.getMessage_from().equals(prefs.getname())) {
              Log.v("message","from me");
              if (MessagesSorting.count(MessagesSorting.class) > 0) {
                  Log.v("Message sortings ","available");
                //  sortings = MessagesSorting.find(MessagesSorting.class, "message_from = ? or message_to = ?", message.getMessage_to(), message.getMessage_to());
                  for (MessagesSorting sorting:Message.listAll(MessagesSorting.class)){
                      if (sorting.getMessage_from().equals(message.getMessage_to())){
                          sortings.add(sorting);
                      } else if (sorting.getMessage_to().equals(message.getMessage_to())){
                          sortings.add(sorting);
                      }
                  }
                  Log.v("messages pulled with other person", String.valueOf(sortings.size()));
              } else {
                  Log.v("Messages","not availble");
              }
          } else if (message.getMessage_to().equals(prefs.getname())){
              Log.v("message","to me");
              if (MessagesSorting.count(MessagesSorting.class) > 0) {
                  Log.v("Message sortings ","available");
                 // sortings = MessagesSorting.find(MessagesSorting.class, "message_from = ? or message_to = ?", message.getMessage_from(), message.getMessage_from());
                  for (MessagesSorting sorting:Message.listAll(MessagesSorting.class)){
                      if (sorting.getMessage_from().equals(message.getMessage_from())){
                          sortings.add(sorting);
                      } else if (sorting.getMessage_to().equals(message.getMessage_from())){
                          sortings.add(sorting);
                      }
                  }
                  Log.v("messages pulled with other person", String.valueOf(sortings.size()));
              } else {
                  Log.v("Messages","not availble");
              }
          }
              if (sortings.size() == 0){
              Log.v("sorting array size","zero");
                  tempMessage = new MessagesSorting(message);
                  tempMessage.save();
                  Log.v("message","saved");
              } else {
                  Log.v("sorting array size", String.valueOf(sortings.size()));
                  if (Long.parseLong(message.getTime()) >= sortings.get(0).getTime()){
                      Log.v("new sorting time","more");
                      sortings.get(0).delete();
                      tempMessage = new MessagesSorting(message);
                      tempMessage.save();
                      Log.v("message","saved");
                      //MessagesSorting.save(new MessagesSorting(message));
                  }
              }
      }



        Log.v("sorting data count", String.valueOf(MessagesSorting.count(MessagesSorting.class)));
      //generate user list

        ArrayList<String> userList = new ArrayList<>();
        for (MessagesSorting sorting:MessagesSorting.listAll(MessagesSorting.class)){
            if (sorting.getMessage_from().equals(prefs.getname())){
                if (!(Arrays.asList(userList).contains(sorting.getMessage_to()))){
                  //  Arrays.asList(userList).add(sorting.getMessageTo());
                    if (!userList.contains(sorting.getMessage_to())) {
                        userList.add(sorting.getMessage_to());
                    }
                }
            }else if (sorting.getMessage_to().equals(prefs.getname())){
                if (!(Arrays.asList(userList).contains(sorting.getMessage_from()))){
                   // Arrays.asList(userList).add(sorting.getMessageFromn());
                    if (!userList.contains(sorting.getMessage_from())) {
                        userList.add(sorting.getMessage_from());
                    }
                }
            }
        }

        //check if profiles are present

        List<User> users = User.listAll(User.class);
        ArrayList<String> userNames = new ArrayList<>();
        ArrayList<String> names1 = new ArrayList<>();
        for (User user:users){
            userNames.add(user.getUsername());
        }


        ArrayList<String> temp = new ArrayList<>();
        for (String name:userList){
            if (userNames.contains(name)){
                temp.add(name);
            }
        }

        if (temp.size() != 0){
            userList.removeAll(temp);
        }

        if (userList.size() != 0){
            fetchUserInfo(userList);
        } else {
          //  sortChatsWithTime();
            buildView();
        }


    }

    private void fetchUserInfo(ArrayList<String> userList) {
        isUserFirstIteration = true;
        DataQueryBuilder queryBuilder = DataQueryBuilder.create();
        queryBuilder.setPageSize(100);

        String clause = "username in (";
        for (int i=0;i<userList.size()-1;i++){
            if (i == 0){
                clause = clause + "'"+userList.get(i)+"'";
            } else if (i == userList.size()-1){
                clause = clause + ",'"+userList.get(i)+"')";
            } else {
                clause = clause + ",'"+userList.get(i)+"'";
            }
        }

        queryBuilder.setWhereClause(clause);

        pullUserData(queryBuilder);


    }

    private void pullUserData(final DataQueryBuilder queryBuilder) {

        Backendless.Data.find(User.class, queryBuilder, new AsyncCallback<List<User>>() {
            @Override
            public void handleResponse(List<User> response) {
                if (isUserFirstIteration)  {
                    intr_users = User.listAll(User.class);
                    User.deleteAll(User.class);
                    isUserFirstIteration = false;
                }
                if (response.size() != 0){
                    User.saveInTx(response);
                    queryBuilder.prepareNextPage();
                    pullUserData(queryBuilder);
                } else {
                    intr_users.clear();
                  //  sortChatsWithTime();
                    buildView();

                }
            }

            @Override
            public void handleFault(BackendlessFault fault) {
                if (isFirstTime){
                    dialog.dismiss();
                    error.show();
                } else if (!isFirstTime && !isUserFirstIteration){
                    User.deleteAll(User.class);
                    User.saveInTx(intr_messages);
                }
            }
        });
    }

    private void buildView() {
      //  if (isFirstTime) {
            dialog.dismiss();
            Log.v("sorting count", String.valueOf(MessagesSorting.count(MessagesSorting.class)));

         //   messages = MessagesSorting.findWithQuery(MessagesSorting.class,"SELECT * FROM MessagesSorting " +
         //           "ORDER BY time DESC",null);
            messages = Select.from(MessagesSorting.class).orderBy("time desc").list();
            for (MessagesSorting sorting:messages){
                Log.v("from",sorting.getMessage_from());
                Log.v("to",sorting.getMessage_to());
            }
            RecyclerViewClickListener listener = new RecyclerViewClickListener() {
                @Override
                public void onClick(View view, int position) {
                    Log.v("get position", String.valueOf(position));
//                    Intent i = new Intent(getContext(),ChatActivity.class);
//                    if (messages.get(position).getMessageFromn().equals(prefs.getname())){
//                        i.putExtra("user",messages.get(position).getMessageTo());
//                    } else if (messages.get(position).getMessageTo().equals(prefs.getname())){
//                        i.putExtra("user",messages.get(position).getMessageFromn());
//                    }
//                    startActivity(i);
                }
            };


            adapter = new ChatListingAdapter(getContext(),messages,listener);
            RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getContext());
            binding.chatListing.setLayoutManager(layoutManager);
            binding.chatListing.setItemAnimator(new DefaultItemAnimator());
            binding.chatListing.setAdapter(adapter);
//        } else {
//            messages.clear();
//            messages = MessagesSorting.listAll(MessagesSorting.class);
//            adapter.notifyDataSetChanged();
//            binding.chatListing.notify();
//        }
    }


}
