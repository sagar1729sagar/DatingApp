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
import android.widget.Toast;

import com.backendless.Backendless;
import com.backendless.async.callback.AsyncCallback;
import com.backendless.exceptions.BackendlessFault;
import com.backendless.persistence.DataQueryBuilder;
import com.orm.SugarContext;
import com.orm.query.Select;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import Adapters.ActivityBoardAdapter;
import Models.Activity;
import Models.Message;
import cn.pedant.SweetAlert.SweetAlertDialog;
import ssapps.com.datingapp.databinding.ActivityBaordBinding;

import static weborb.util.ThreadContext.context;

public class ActivityBaord extends Fragment {

    private ActivityBaordBinding binding;
    private List<Activity> activities = new ArrayList<>();
    private SweetAlertDialog dialog,error;
    private ActivityBoardAdapter adapter;
    private static final String appKey = "7EEB2727-4E8D-944C-FFDD-3D802BC37800";
    private static final String appId = "648D896E-EDD8-49C8-FF74-2F1C32DB7A00";
    private boolean isFirstTime,isFirstIteration;
    private List<Activity> intr_activites = new ArrayList<>();
    private boolean isAffliated = false;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
       // binding = DataBindingUtil.inflate(inflater,R.layout.activity_baord,false);
        binding = DataBindingUtil.inflate(inflater,R.layout.activity_baord,container,false);
        return binding.getRoot();
    }

    @Override
    public void onResume() {
        super.onResume();
        activities.clear();
        activities.addAll(Activity.listAll(Activity.class));
        if (isAffliated) {
            adapter.notifyDataSetChanged();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        isAffliated = true;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        dialog = new SweetAlertDialog(getContext(),SweetAlertDialog.PROGRESS_TYPE);
        dialog.setTitleText("Contacting...");
        dialog.setCancelable(false);
        error = new SweetAlertDialog(getContext(),SweetAlertDialog.ERROR_TYPE);
        Backendless.initApp(getContext(),appId,appKey);
        SugarContext.init(getContext());

        if (Activity.count(Activity.class) == 0){
            Toast.makeText(getContext(),"No activites by you",Toast.LENGTH_LONG).show();
            activities = Activity.listAll(Activity.class);
            adapter = new ActivityBoardAdapter(getContext(),activities);
            RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getContext());
            binding.activityList.setLayoutManager(layoutManager);
            binding.activityList.setItemAnimator(new DefaultItemAnimator());
            binding.activityList.setAdapter(adapter);
        } else {
           // activities = Select.from(Activity.class).orderBy("time DESC").list();
            activities = Activity.listAll(Activity.class);
            adapter = new ActivityBoardAdapter(getContext(),activities);
            RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getContext());
            binding.activityList.setLayoutManager(layoutManager);
            binding.activityList.setItemAnimator(new DefaultItemAnimator());
            binding.activityList.setAdapter(adapter);
        }




//        if (Activity.count(Activity.class) == 0){
//            isFirstTime = true;
//            getData();
//        } else {
//            isFirstTime = false;
//            setView();
//
//
//
//        }

    }
//
//    private void getData() {
//        isFirstIteration = true;
//        DataQueryBuilder queryBuilder = DataQueryBuilder.create();
//        queryBuilder.setWhereClause("time after "+Calendar.getInstance().getTimeInMillis());
//        queryBuilder.setPageSize(100);
//        pullData(queryBuilder);
//    }
//
//    private void pullData(final DataQueryBuilder queryBuilder) {
//        Backendless.Data.find(Activity.class, queryBuilder, new AsyncCallback<List<Activity>>() {
//            @Override
//            public void handleResponse(List<Activity> response) {
//                if (isFirstIteration){
//                    intr_activites = Activity.listAll(Activity.class);
//                    Activity.deleteAll(Activity.class);
//                    isFirstIteration = false;
//                }
//                if (response.size() != 0){
//                    Activity.saveInTx(response);
//                    queryBuilder.prepareNextPage();
//                    pullData(queryBuilder);
//                } else {
//                    intr_activites.clear();
//                    intr_activites = Activity.listAll(Activity.class);
//                    activities.clear();
//                    activities = Activity.findWithQuery(Activity.class, "SELECT * FROM Activity " +
//                            "WHERE time > " + Calendar.getInstance().getTimeInMillis() + " ORDER BY time DESC");
//                    setView();
//                }
//            }
//
//            @Override
//            public void handleFault(BackendlessFault fault) {
//                if (isFirstTime){
//                    dialog.dismiss();
//                    error.setTitleText("Error fetching data");
//                    error.setContentText("The foloowing error occured while fetching data\n"+fault.getMessage()+"\n Please try again");
//                    error.show();
//                } else if (!isFirstTime && !isFirstIteration){
//                    Activity.deleteAll(Activity.class);
//                    Activity.saveInTx(intr_activites);
//                    activities.clear();
//                    activities = Activity.findWithQuery(Activity.class, "SELECT * FROM Activity " +
//                            "WHERE time > " + Calendar.getInstance().getTimeInMillis() + " ORDER BY time DESC");
//                    setView();
//                }
//            }
//        });
//    }
//
//    private void setView() {
//        if (isFirstTime) {
//            activities = Activity.findWithQuery(Activity.class, "SELECT * FROM Activity " +
//                    "WHERE time > " + Calendar.getInstance().getTimeInMillis() + " ORDER BY time DESC");
//            adapter = new ActivityBoardAdapter(getContext(), activities);
//            RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getContext());
//            binding.activityList.setLayoutManager(layoutManager);
//            binding.activityList.setItemAnimator(new DefaultItemAnimator());
//            binding.activityList.setAdapter(adapter);
//        } else {
//            activities.clear();
//            activities = Activity.findWithQuery(Activity.class, "SELECT * FROM Activity " +
//                    "WHERE time > " + Calendar.getInstance().getTimeInMillis() + " ORDER BY time DESC");
//            adapter.notifyDataSetChanged();
//            binding.activityList.notify();
//        }
//    }
}
