package ssapps.com.datingapp;

import android.databinding.DataBindingUtil;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.backendless.Backendless;
import com.backendless.async.callback.AsyncCallback;
import com.backendless.exceptions.BackendlessFault;
import com.backendless.persistence.DataQueryBuilder;

import java.util.List;

import Models.User;
import cn.pedant.SweetAlert.SweetAlertDialog;
import ssapps.com.datingapp.databinding.ActivitySearchBinding;

public class SearchActivity extends Fragment {
    ActivitySearchBinding binding;
    private Boolean silence = true;
    private SweetAlertDialog error,dialog;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater,R.layout.activity_search,container,false);
        return binding.getroot();
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {

        dialog = new SweetAlertDialog(getContext(),SweetAlertDialog.PROGRESS_TYPE);
        dialog.setCancelable(false);
        error = new SweetAlertDialog(getContext(),SweetAlertDialog.ERROR_TYPE);

        long count = User.count(User.class);
        if (count == 0 || count == 1){
            silence = false;
            getData();
        } else if (count > 1){
            silence = true;
            //todo prepare silently
        }

    }

    private void getData(){


            if (!silence) {
                dialog.setTitleText("Preparing the page....");
                dialog.show();
            }

        DataQueryBuilder query = DataQueryBuilder.create();
            query.setPageSize(100);

            pullData(query,true);



    }

    private void pullData(final DataQueryBuilder query, final boolean isFirst) {
        Backendless.Data.find(User.class, query, new AsyncCallback<List<User>>() {
            @Override
            public void handleResponse(List<User> response) {
                if (response.size() != 0){
                    User.saveInTx(response);
                    query.prepareNextPage();
                    pullData(query,false);
                } else if (response.size() == 0){
                    //todo prepare the page
                }
            }

            @Override
            public void handleFault(BackendlessFault fault) {
                dialog.dismiss();
                if (isFirst){
                    error.setTitleText("Cannot prepare page");
                    error.setContentText("The following error has occured while contacting VeMeet\n"+fault.getMessage()+"\n Please try again later");
                    error.show();
                }
            }
        });
    }
}
