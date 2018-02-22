package ssapps.com.datingapp;


import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.backendless.Backendless;
import com.backendless.async.callback.AsyncCallback;
import com.backendless.exceptions.BackendlessFault;
import com.backendless.persistence.DataQueryBuilder;

import java.util.Calendar;
import java.util.List;

import Adapters.WhosNewAdapter;
import Models.SearchResults;
import Models.User;
import cn.pedant.SweetAlert.SweetAlertDialog;
import ssapps.com.datingapp.databinding.ActivityWhosNewBinding;

public class WhosNewActivity extends Fragment {

    private ActivityWhosNewBinding binding;
    private SweetAlertDialog dialog,error;
    private static final String appKey = "7EEB2727-4E8D-944C-FFDD-3D802BC37800";
    private static final String appId = "648D896E-EDD8-49C8-FF74-2F1C32DB7A00";
    private WhosNewAdapter adapter;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater,R.layout_activity_whos_new,container,false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {

        Backendless.initApp(getContext(),appId,appKey);

        dialog = new SweetAlertDialog(getContext(),SweetAlertDialog.PROGRESS_TYPE);
        dialog.setTitleText("Searching....");
        dialog.setCancelable(false);

        error = new SweetAlertDialog(getContext(),SweetAlertDialog.ERROR_TYPE);

        search();

    }

    private void search() {

        dialog.show();
        DataQueryBuilder query = DataQueryBuilder.create();
        query.setPageSize(25);
        Long now = Calendar.getInstance().getTimeInMillis();
        Long before_30_days = now - (30*24*60*60*1000);
        query.setWhereClause("created at or after "+before_30_days);

        Backendless.Data.find(User.class, query, new AsyncCallback<List<User>>() {
            @Override
            public void handleResponse(List<User> response) {
                dialog.dismiss();
                SearchResults.deleteAll(SearchResults.class);
                for (User user:response){
                    if (user.getIncognito_mode().equals("No")) {
                        new SearchResults(user).save();
                    }
                }

                plotView();
            }

            @Override
            public void handleFault(BackendlessFault fault) {
                dialog.dismiss();
                error.setTitleText("Error occurred");
                error.setContentText("The following error whilegetting data from VeMeet\n"
                        +fault.getMessage()+"\n Please try again");
                error.show();
            }
        });

    }

    private void plotView() {

        final List<SearchResults> results = SearchResults.listAll(SearchResults.class)

        RecyclerViewClickListener listener = new RecyclerViewClickListener() {
            @Override
            public void onClick(View view, int position) {
                Intent i = new Intent(getContext(),SearchItemDetailsActivity.class);
                i.putExtra("name",results.get(position).getUsername());
                // startActivity(new Intent(SearchResultsDisplayActivity.this,SearchItemDetailsActivity.class));
                startActivity(i);
            }
        };


        adapter = new WhosNewAdapter(results,getContext(),listener);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getContext());
        binding.whosNewList.setLayoutManager(layoutManager);
        binding.whosNewList.setItemAnimator(new DefaultItemAnimator());
        binding.whosNewList.setAdapter(adapter);

    }

}
