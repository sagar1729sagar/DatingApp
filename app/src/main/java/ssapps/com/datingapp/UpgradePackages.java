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

import com.backendless.Backendless;
import com.backendless.async.callback.AsyncCallback;
import com.backendless.exceptions.BackendlessFault;
import com.backendless.persistence.DataQueryBuilder;

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
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater,R.layout.activity_settings_fragment,container,false);
        return binding.getroot();
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        Backendless.initApp(getContext(),appId,appKey);
        dialog = new SweetAlertDialog(getContext(),SweetAlertDialog.PROGRESS_TYPE);
        dialog.setCancelable(false);
        error = new SweetAlertDialog(getContext(),SweetAlertDialog.ERROR_TYPE);


        adapter = new PackagesAdapter(getContext(),packages);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getContext());
        binding.packagesList.setLayoutManager(mLayoutManager);
        binding.packagesList.setItemAnimator(new DefaultItemAnimator());
        binding.packagesList.setAdapter(adapter);
        if (!(Packages.count(Packages.class) == 0)){
            packages.clear();
            packages.addAll(Packages.listAll(Packages.class));
            adapter.notifyDataSetChanged();
        }
    }

    private void fetchPackages(){
        DataQueryBuilder query = DataQueryBuilder.create();
        query.setPageSize(100);

        if (Packages.count(Packages.class) == 0){
            dialog.setTitleText("Fetching information");
            dialog.show();
        }

        Backendless.Data.find(Packages.class, query, new AsyncCallback<List<Packages>>() {
            @Override
            public void handleResponse(List<Packages> response) {
                dialog.dismiss();
                packages.clear();
                packages.addAll(response);
//                for (int i = 0;i<response.size();i++){
//                    packages.add(response.get(i));
//                    response.get(i).save();
//                }

                adapter.notifyDataSetChanged();
            }

            @Override
            public void handleFault(BackendlessFault fault) {
                if (Packages.count(Packages.class) == 0){
                    dialog.dismiss();
                    error.setTitleText("Cannot connect to VeMeet");
                    error.setContentText("The following error has occured while connecting to VeMeet\n"+fault.getMessage()+"Please tray again later");
                    error.show();
                }
            }
        });

    }


}
