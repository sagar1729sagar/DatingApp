package ssapps.com.datingapp;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.backendless.Backendless;
import com.backendless.BackendlessUser;
import com.backendless.async.callback.AsyncCallback;
import com.backendless.exceptions.BackendlessFault;
import com.backendless.persistence.BackendlessDataQuery;
import com.backendless.persistence.DataQueryBuilder;
import com.orm.SugarContext;

import Models.User;
import Util.Prefs;
import Util.Util;
import com.orm.SugarRecord;
import com.orm.util.QueryBuilder;

import java.util.List;

import Util.Util;
import cn.pedant.SweetAlert.SweetAlertDialog;
import ssapps.com.datingapp.databinding.ActivityLoginBinding;

public class LoginActivity extends AppCompatActivity {

    private ActivityLoginBinding binding;
    private static final String appKey = "7EEB2727-4E8D-944C-FFDD-3D802BC37800";
    private static final String appId = "648D896E-EDD8-49C8-FF74-2F1C32DB7A00";
    private Util util;
    private Prefs prefs;
    private SweetAlertDialog dialog,error;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this,R.layout.activity_login);

        util = new Util();
        prefs = new Prefs(this);

        dialog = new SweetAlertDialog(this,SweetAlertDialog.PROGRESS_TYPE);
        dialog.setCancelable(false);
        error = new SweetAlertDialog(this,SweetAlertDialog.ERROR_TYPE);
        dialog.setTitleText("Logging in...");


        Backendless.initApp(this,appId,appKey);
        SugarContext.init(this);

        ActionBar actionBar = getSupportActionBar();

        actionBar.setHomeButtonEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayShowHomeEnabled(true);

        binding.loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (checkFields()){
                // fetchUserDeatils();
                    loginUser();
                }
            }
        });


    }

    private void fetchUserDeatils() {
       // QueryBuilder queryBuilder = new BackendlessDataQuery().c
        dialog.show();
        DataQueryBuilder queryBuilder = DataQueryBuilder.create();
        queryBuilder.setWhereClause("username = '"+binding.usernameEt.getText().toString().trim()+"'");
        Backendless.Data.find(User.class, queryBuilder, new AsyncCallback<List<User>>() {
            @Override
            public void handleResponse(List<User> response) {

                Log.v("user fetch","success");

                dialog.dismiss();

                if (User.find(User.class,"username = ?",binding.usernameEt.getText().toString().trim()).size() != 0){
                    User.find(User.class,"username = ?",binding.usernameEt.getText().toString().trim()).get(0).delete();
                }

                Log.v("response", String.valueOf(response.size()));

                response.get(0).save();
                prefs.setName(response.get(0).getUsername());

                goToProfilePage();

            }

            @Override
            public void handleFault(BackendlessFault fault) {
                dialog.dismiss();
                Log.v("user fetch","fail "+fault.toString());
                error.setTitleText("Error logging in");
                error.setContentText("The following error has occured while trying to login \n"+fault.getMessage()
                        +"\n Please try again");
                error.show();
            }
        });
    }

    private void goToProfilePage() {
        Intent i = new Intent(LoginActivity.this,MainActivity.class);
        i.putExtra("redirectProfile",true);
        startActivity(i);
    }

    private void loginUser() {

        dialog.show();
//        BackendlessUser user = new BackendlessUser();
//        user.setProperty("username",binding.usernameEt.getText().toString());
//        user.setPassword(.toString());

        Log.v("user", String.valueOf(User.find(User.class,"username = ?",binding.usernameEt.getText().toString().trim()).size()));

      //  Backendless.UserService.login(User.find(User.class,"username = ?",binding.usernameEt.getText().toString().trim()).get(0).getEmail(), binding.passwordEt.getText().toString(), new AsyncCallback<BackendlessUser>() {
          Backendless.UserService.login(binding.usernameEt.getText().toString().trim(), binding.passwordEt.getText().toString(), new AsyncCallback<BackendlessUser>() {
            @Override
            public void handleResponse(BackendlessUser response) {
               // dialog.dismiss();
                Log.v("login","successfull");

                fetchUserDeatils();


            }

            @Override
            public void handleFault(BackendlessFault fault) {
                dialog.dismiss();
                Log.v("login","fail "+fault.toString());
                error.setTitleText("Error logging in");
                error.setContentText("The following error has occured while trying to login \n"+fault.getMessage()
                +"\n Please try again");
                error.show();
            }
        },true);

    }

    private boolean checkFields() {
        if (!util.checkEditTextField(binding.usernameEt)){
            binding.usernameLayout.setError("Please enter a username");
            return false;
        }
        if (!util.checkEditTextField(binding.passwordEt)){
            binding.passwordLayout.setError("Please enter a password");
            return false;
        }
        return true;
    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
        this.finish();
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        SugarContext.terminate();
    }
}
