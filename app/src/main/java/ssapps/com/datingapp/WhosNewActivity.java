package ssapps.com.datingapp;


import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.backendless.Backendless;
import com.backendless.async.callback.AsyncCallback;
import com.backendless.exceptions.BackendlessFault;
import com.backendless.persistence.DataQueryBuilder;
import com.orm.SugarContext;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import Adapters.WhosNewAdapter;
import Models.SearchResults;
import Models.User;
import Util.Prefs;
import cn.pedant.SweetAlert.SweetAlertDialog;
import ssapps.com.datingapp.databinding.ActivityWhosNewBinding;

public class WhosNewActivity extends Fragment {

    private ActivityWhosNewBinding binding;
    private static final String appKey = "7EEB2727-4E8D-944C-FFDD-3D802BC37800";
    private static final String appId = "648D896E-EDD8-49C8-FF74-2F1C32DB7A00";
    private boolean isFirstTime;
    private boolean isFirstIteration;
    private SweetAlertDialog dialog,error;
    private Prefs prefs;
    private List<SearchResults> temp = new ArrayList<>();
    private SearchResults result;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater,R.layout.activity_whos_new,container,false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {

        Backendless.initApp(getContext(),appId,appKey);
        SugarContext.init(getContext());
        prefs = new Prefs(getContext());
        dialog = new SweetAlertDialog(getContext(),SweetAlertDialog.PROGRESS_TYPE);
        dialog.setCancelable(false);

        error = new SweetAlertDialog(getContext(),SweetAlertDialog.ERROR_TYPE);

        List<User> allUsers = User.listAll(User.class);
        isFirstTime = true;
        getData();

    }

    private void getData() {
        if (isFirstTime){
            dialog.show();
        }

        isFirstIteration = true;

        DataQueryBuilder query = DataQueryBuilder.create();
        query.setPageSize(25);
        Long now = Calendar.getInstance().getTimeInMillis();
        Long before_30_days = now - (30*24*60*60*1000);
        query.setWhereClause("created at or after "+before_30_days);
        pullData(query);
    }

    private void pullData(final DataQueryBuilder query) {
        Backendless.Data.find(User.class, query, new AsyncCallback<List<User>>() {
            @Override
            public void handleResponse(List<User> response) {
                Log.v("response", String.valueOf(response.size()));
                for (int i = 0;i<response.size();i++){
                    result = new SearchResults(response.get(i));
                    result.save();
                }

                sortData();

//                if (isFirstIteration){
//                    User currentUser = User.find(User.class,"username = ?",prefs.getname()).get(0);
//                    User.deleteAll(User.class);
//                    currentUser.save();
////                    isFirstIteration = false;
////                }
//               // if (response.size() != 0){
//                    User.saveInTx(response);
//                    query.prepareNextPage();
//                    pullData(query);
//                } else {
//                    //   dialog.dismiss();
//                    sortData();
//                }

            }

            @Override
            public void handleFault(BackendlessFault fault) {
                dialog.dismiss();
               // if (isFirstTime) {
                    //  dialog.dismiss();
                    error.setTitleText("Error occured");
                    error.setContentText("The following error occured while conecting to VeMeet\n"
                            +fault.getMessage()+"\n Please try again");
                    error.show();
                //} else if (!isFirstIteration){
                    sortData();
                //}
            }
        });
    }

    private void sortData() {
        Log.v("sort data","called");
        temp.clear();
        List<SearchResults> results = SearchResults.listAll(SearchResults.class);
        temp = SearchResults.listAll(SearchResults.class);

        for (SearchResults result:results){

            if (result.getIncognito_mode().equals("Yes")){
                temp.add(result);
            }
        }

        results.removeAll(temp);

        SearchResults.deleteAll(SearchResults.class);

        if (results.size() != 0){
            for (SearchResults results1:results){
                result = results1;
                result.save();
            }
        }

        prefs.setOnlineRedirect(true);
        dialog.dismiss();
        startActivity(new Intent(getContext(),SearchResultsDisplayActivity.class));
        getActivity().finish();
    }


}
