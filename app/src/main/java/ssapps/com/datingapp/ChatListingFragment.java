package ssapps.com.datingapp;

import android.databinding.DataBindingUtil;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
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
import com.orm.query.Select;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

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
    private ChatListingAdapter adapter;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater,R.layout.activity_chat_listing_fragment,false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {

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
            sortData(Message.listAll(Message.class));
        }





    }

    private void getData() {

        isFirstIteration = true;
        String whereClause = "from = '"+prefs.getname()+"' OR to = '"+prefs.getname()+"'";
        DataQueryBuilder query = DataQueryBuilder.create();
        query.setPageSize(100);
        query.setWhereClause(whereClause);

        pullData(query);

    }

    private void pullData(final DataQueryBuilder query) {
        Backendless.Data.find(Message.class, query, new AsyncCallback<List<Message>>() {
            @Override
            public void handleResponse(List<Message> response) {
                if (isFirstIteration){
                    intr_messages = Message.listAll(Message.class);
                    Message.deleteAll(Message.class);
                    isFirstIteration = false;
                }
                if (response.size() != 0){
                    Message.saveInTx(response);
                    query.prepareNextPage();
                    pullData(query);
                } else {
                    intr_messages.clear();
                    intr_messages = Message.listAll(Message.class);
                    sortData(intr_messages);
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

    private void sortData(List<Message> messageList) {

        // add latest message

      for (Message message:messageList){
          List<MessagesSorting> sortings = new ArrayList<>();
          if (message.getFrom().equals(prefs.getname())) {
              sortings = MessagesSorting.find(MessagesSorting.class, "from = ? or to = ?", message.getTo(), message.getTo());
          } else if (message.getTo().equals(prefs.getname())){
              sortings = MessagesSorting.find(MessagesSorting.class,"from = ? or to = ?",message.getFrom(),message.getFrom());
          }
              if (sortings.size() == 0){
                  MessagesSorting.save(new MessagesSorting(message));
              } else {
                  if (Long.parseLong(message.getTime()) >= sortings.get(0).getTime()){
                      sortings.get(0).delete();
                      MessagesSorting.save(new MessagesSorting(message));
                  }
              }
      }

      //generate user list

        String[] userList = {};
        for (MessagesSorting sorting:MessagesSorting.listAll(MessagesSorting.class)){
            if (sorting.getFrom().equals(prefs.getname())){
                if (!(Arrays.asList(userList).contains(sorting.getTo()))){
                    Arrays.asList(userList).add(sorting.getTo());
                }
            }else if (sorting.getTo().equals(prefs.getname())){
                if (!(Arrays.asList(userList).contains(sorting.getFrom()))){
                    Arrays.asList(userList).add(sorting.getFrom());
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



        for (String name:Arrays.asList(userList)){
            if (userNames.contains(name)){
                Arrays.asList(userList).remove(name);
            }
        }

        if (userList.length != 0){
            fetchUserInfo(userList);
        } else {
          //  sortChatsWithTime();
            buildView();
        }


    }

    private void fetchUserInfo(String[] userList) {
        isUserFirstIteration = true;
        DataQueryBuilder queryBuilder = DataQueryBuilder.create();
        queryBuilder.setPageSize(100);

        String clause = "username in (";
        for (int i=0;i<userList.length-1;i++){
            if (i == 0){
                clause = clause + "'"+userList[i]+"'";
            } else if (i == userList.length-1){
                clause = clause + ",'"+userList[i]+"')";
            } else {
                clause = clause + ",'"+userList[i]+"'";
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
        if (isFirstTime) {
            dialog.dismiss();
            messages = MessagesSorting.findWithQuery(MessagesSorting.class,"SELECT * FROM MessaagesSorting " +
                    "ORDER BY time DESC",null);
            RecyclerViewClickListener listener = new RecyclerViewClickListener() {
                @Override
                public void onClick(View view, int position) {
                    //todo go to next page
                }
            };


            adapter = new ChatListingAdapter(getContext(),messages,listener);
            RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getContext());
            binding.chatListing.setLayoutManager(layoutManager);
            binding.chatListing.setItemAnimator(new DefaultItemAnimator());
            binding.chatListing.setAdapter(adapter);
        } else {
            messages.clear();
            messages = MessagesSorting.listAll(MessagesSorting.class);
            adapter.notifyDataSetChanged();
            binding.chatListing.notify();
        }
    }

    
}
