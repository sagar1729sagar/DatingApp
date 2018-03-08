package ssapps.com.datingapp;


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

import com.backendless.Backendless;
import com.backendless.async.callback.AsyncCallback;
import com.backendless.exceptions.BackendlessFault;
import com.backendless.persistence.DataQueryBuilder;
import com.orm.SugarContext;

import java.util.ArrayList;
import java.util.List;

import Adapters.PackagesAdapter;
import Models.Packages;
import cn.pedant.SweetAlert.SweetAlertDialog;
import ssapps.com.datingapp.databinding.ActivityUpgradePackagesBinding;

public class UpgradePackages extends Fragment {

    ActivityUpgradePackagesBinding binding;
    private static final String appKey = "7EEB2727-4E8D-944C-FFDD-3D802BC37800";
    private static final String appId = "648D896E-EDD8-49C8-FF74-2F1C32DB7A00";
    private SweetAlertDialog dialog,error;
    private PackagesAdapter adapter;
    private List<Packages> packages = new ArrayList<>();
    private boolean isFirstTime = false;
    private boolean isFirtsIteration = false;
    private List<Packages> temp_packages = new ArrayList<>();
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater,R.layout.activity_upgrade_packages,container,false);
        return binding.getRoot();
       // binding.getRoot()
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        Backendless.initApp(getContext(),appId,appKey);
        SugarContext.init(getContext());
        dialog = new SweetAlertDialog(getContext(),SweetAlertDialog.PROGRESS_TYPE);
        dialog.setTitleText("Fetching Packages");
        dialog.setCancelable(false);
        error = new SweetAlertDialog(getContext(),SweetAlertDialog.ERROR_TYPE);

        if (Packages.count(Packages.class) != 0){
            Log.v("package count", String.valueOf(Packages.count(Packages.class)));
            packages = Packages.listAll(Packages.class);
        }
        adapter = new PackagesAdapter(getContext(),packages);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getContext());
        binding.packagesList.setLayoutManager(mLayoutManager);
        binding.packagesList.setItemAnimator(new DefaultItemAnimator());
        binding.packagesList.setAdapter(adapter);
        if (!(Packages.count(Packages.class) == 0)){
            packages.clear();
            packages.addAll(Packages.listAll(Packages.class));
            adapter.notifyDataSetChanged();
//            binding.packagesList.notifyAll();
            isFirstTime = false;
            fetchPackages();
        } else {
            isFirstTime = true;
            fetchPackages();
        }
    }
    //todo testing paused here
    private void fetchPackages() {
        Log.v("fetch packages","called");
        temp_packages.clear();

        if (isFirstTime){
            dialog.show();
        } else {
            temp_packages = Packages.listAll(Packages.class);
        }

        isFirtsIteration = true;
        DataQueryBuilder queryBuilder = DataQueryBuilder.create();
        queryBuilder.setPageSize(100);
        getPackages(queryBuilder);

    }

    private void getPackages(final DataQueryBuilder queryBuilder) {
        Log.v("getting packages","started");
        Backendless.Data.find(Packages.class, queryBuilder, new AsyncCallback<List<Packages>>() {
            @Override
            public void handleResponse(List<Packages> response) {
                Log.v("getting packages","response recieeved");
                Log.v("response size", String.valueOf(response.size()));
                if (response.size() != 0){
                    if (isFirtsIteration){
                        Packages.deleteAll(Packages.class);
                        isFirtsIteration = false;
                        Log.v("getting packages","response first iteration");
                    }
                    Packages.saveInTx(response);
                    queryBuilder.prepareNextPage();
                    getPackages(queryBuilder);
                } else {
                    dialog.dismiss();
                    packages.clear();
                    Log.v("db count", String.valueOf(Packages.count(Packages.class)));
                   // packages = Packages.listAll(Packages.class);
                    packages.addAll(Packages.listAll(Packages.class));
                    Log.v("list count", String.valueOf(packages.size()));
                    Log.v("notifying","adapter");
                    adapter.notifyDataSetChanged();
//                    binding.packagesList.notifyAll();
                }
            }

            @Override
            public void handleFault(BackendlessFault fault) {
                    Log.v("error getting pakages", String.valueOf(fault));
                if (!isFirtsIteration){
                    Packages.deleteAll(Packages.class);
                    Packages.saveInTx(temp_packages);
                    dialog.dismiss();
                }
                if (isFirstTime){
                    error.setTitleText("Cannot connect to VeMeet");
                    error.setContentText("The following error has occured while connecting to VeMeet\n"+fault.getMessage()+"Please tray again later");
                    error.show();
                }

            }
        });

    }

//    private void fetchPackages(){
//        DataQueryBuilder query = DataQueryBuilder.create();
//        query.setPageSize(100);
//        if (Packages.count(Packages.class) == 0){
//            dialog.setTitleText("Fetching information");
//            dialog.show();
//        }
//
//        Backendless.Data.find(Packages.class, query, new AsyncCallback<List<Packages>>() {
//            @Override
//            public void handleResponse(List<Packages> response) {
//                dialog.dismiss();
//                packages.clear();
//                packages.addAll(response);
////                for (int i = 0;i<response.size();i++){
////                    packages.add(response.get(i));
////                    response.get(i).save();
////                }
//                adapter.notifyDataSetChanged();
//            }
//
//            @Override
//            public void handleFault(BackendlessFault fault) {
//                if (Packages.count(Packages.class) == 0){
//                    dialog.dismiss();
//                    error.setTitleText("Cannot connect to VeMeet");
//                    error.setContentText("The following error has occured while connecting to VeMeet\n"+fault.getMessage()+"Please tray again later");
//                    error.show();
//                } fetchPackages();
//            }
//        });
//
//    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        SugarContext.terminate();
    }
}
