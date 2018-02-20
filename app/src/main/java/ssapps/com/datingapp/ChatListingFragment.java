package ssapps.com.datingapp;

import android.databinding.DataBindingUtil;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.backendless.Backendless;
import com.backendless.async.callback.AsyncCallback;
import com.backendless.exceptions.BackendlessFault;
import com.backendless.messaging.MessageStatus;
import com.backendless.persistence.BackendlessDataQuery;
import com.backendless.persistence.DataQueryBuilder;

import java.util.ArrayList;
import java.util.List;

import Models.Message;
import Models.MessagesSorting;
import Util.Prefs;
import cn.pedant.SweetAlert.SweetAlertDialog;
import ssapps.com.datingapp.databinding.ActivityChatListingFragmentBinding;

public class ChatListingFragment extends Fragment{
    private ActivityChatListingFragmentBinding binding;
    private boolean silence;
    private String[] usersNames = {};
    private boolean isFirstTime,isFirstIteration;
    private List<Message> messages = new ArrayList<>();
    private List<Message> intr_messages = new ArrayList<>();
    private SweetAlertDialog dialog,error;
    private Prefs prefs;
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

      //todo

    }

    //todo dont forget to sort messages before sending into adapter


}
