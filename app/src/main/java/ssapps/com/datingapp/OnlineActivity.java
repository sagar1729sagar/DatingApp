package ssapps.com.datingapp;


import android.content.Intent;
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
import com.backendless.persistence.DataQueryBuilder;

import java.util.List;

import Models.SearchResults;
import Models.User;
import Util.Prefs;
import cn.pedant.SweetAlert.SweetAlertDialog;
import ssapps.com.datingapp.databinding.ActivityOnlineBinding;

public class OnlineActivity extends Fragment {

    private ActivityOnlineBinding binding;
    private static final String appKey = "7EEB2727-4E8D-944C-FFDD-3D802BC37800";
    private static final String appId = "648D896E-EDD8-49C8-FF74-2F1C32DB7A00";
    private boolean isFirstTime;
    private boolean isFirstIteration;
    private SweetAlertDialog dialog,error;
    private Prefs prefs;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater,R.layout.activity_inding,container,false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {

        Backendless.initApp(getContext(),appId,appKey);
        prefs = new Prefs(getContext());
        dialog = new SweetAlertDialog(getContext(),SweetAlertDialog.PROGRESS_TYPE);
        dialog.setCancelable(false);

        error = new SweetAlertDialog(getContext(),SweetAlertDialog.ERROR_TYPE);

        List<User> allUsers = User.listAll(User.class);
        if (allUsers.size() >= 3){
            isFirstTime = false;
            sortData();
        } else {
            isFirstTime = true;
            getData();
        }

    }

    private void getData() {
        if (isFirstTime){
            dialog.show();
        }

        isFirstIteration = true;

        DataQueryBuilder query = DataQueryBuilder.create();
        query.setPageSize(100);
        pullData(query);
    }

    private void pullData(final DataQueryBuilder query) {
        Backendless.Data.find(User.class, query, new AsyncCallback<List<User>>() {
            @Override
            public void handleResponse(List<User> response) {
                if (isFirstIteration){
                    User currentUser = User.find(User.class,"username = ?",prefs.getname()).get(0);
                    User.deleteAll(User.class);
                    currentUser.save();
                    isFirstIteration = false;
                }
                if (response.size() != 0){
                    User.saveInTx(response);
                    query.prepareNextPage();
                    pullData(query);
                } else {
                 //   dialog.dismiss();
                    sortData();
                }

            }

            @Override
            public void handleFault(BackendlessFault fault) {
                dialog.dismiss();
                if (isFirstTime) {
                  //  dialog.dismiss();
                    error.setTitleText("Error occured");
                    error.setContentText("The following error occured while conecting to VeMeet\n"
                                +fault.getMessage()+"\n Please try again");
                    error.show();
                } else if (!isFirstIteration){
                    sortData();
                }
            }
        });
    }

    private void sortData() {
        List<User> users = User.listAll(User.class);
        for (User user:users){
            if (user.getIncognito_mode().equals("Yes") || user.getIsOnline().equals("No")){
                users.remove(user);
            }
        }

        SearchResults.deleteAll(SearchResults.class);

        for (User user:users){
            new SearchResults(user).save();
        }
        dialog.dismiss();
        startActivity(new Intent(getContext(),SearchResultsDisplayActivity.class));
    }
}
