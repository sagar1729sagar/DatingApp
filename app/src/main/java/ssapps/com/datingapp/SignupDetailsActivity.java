package ssapps.com.datingapp;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import com.backendless.Backendless;
import com.backendless.async.callback.AsyncCallback;
import com.backendless.exceptions.BackendlessFault;

import java.util.ArrayList;
import java.util.List;

import Models.User;
import Util.Util;
import cn.pedant.SweetAlert.SweetAlertDialog;
import ssapps.com.datingapp.databinding.ActivitySignupDetailsBinding;

public class SignupDetailsActivity extends AppCompatActivity implements View.OnClickListener {

    ActivitySignupDetailsBinding binding;
    private Util util;
    private SweetAlertDialog dialog;
    private SweetAlertDialog error;
    private String user;
    private static final String appKey = "7EEB2727-4E8D-944C-FFDD-3D802BC37800";
    private static final String appId = "648D896E-EDD8-49C8-FF74-2F1C32DB7A00";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.activity_signup_details);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_signup_details);

        Backendless.initApp(this,appKey,appId);


        ActionBar actionBar = getSupportActionBar();

        actionBar.setHomeButtonEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayShowHomeEnabled(true);

        util = new Util();
        dialog = new SweetAlertDialog(this,SweetAlertDialog.PROGRESS_TYPE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            dialog.getProgressHelper().setBarColor(getResources().getColor(R.color.leaf_green,null));
        } else {
            dialog.getProgressHelper().setBarColor(getResources().getColor(R.color.leaf_green));
        }
        dialog.setCancelable(false);
        dialog.dismiss();

        error =  new SweetAlertDialog(this,SweetAlertDialog.ERROR_TYPE);

        user = getIntent().getStringExtra("user");

        if (getIntent().hasExtra("image")){
            Bitmap bitmap = BitmapFactory.decodeByteArray(getIntent().getByteArrayExtra("image"),0,getIntent().getByteArrayExtra("image").length);
            binding.profileImage.setImageBitmap(bitmap);
        }

        binding.saveButton.setOnClickListener(this);
       // checkAllFields();

    }

    private boolean checkAllFields() {

      if (!util.checkEditTextField(binding.abtMeEt)){
          binding.aboutMeLayout.setError("Please write something about you");
          return false;
      }
      if (!util.checkEditTextField(binding.ageEt)){
          binding.ageLayout.setError("Please enter your age");
          return false;
      }
      if (!util.checkEditTextField(binding.residenceEt)){
          binding.residenceLayout.setError("Please enter your city adn country information");
          return false;
      }
      if (!util.checkEditTextField(binding.genderEt)){
          binding.genderLayout.setError("Please enter your gender preference");
          return false;
      }
      if (!util.checkEditTextField(binding.lifestyleEt)){
          binding.lifestyleLayout.setError("Please enter your lifestyle preference");
          return false;
      }
      if (!util.checkEditTextField(binding.ageOthersEt)){
          binding.ageOthersLayout.setError("Please enter your age preference");
          return false;
      }
      if (!util.checkEditTextField(binding.forEt)){
          binding.forLayout.setError("Please enter your relationship preference");
          return false;
      }
      if (!util.checkEditTextField(binding.lifestyleSelfEt)){
          binding.lifestyleSelfLayout.setError("Lifestyle information is required");
          return false;
      }
      if (!util.checkEditTextField(binding.soEt)){
          binding.soLayout.setError("Sexual orientation information is required");
          return false;
      }
      if (!util.checkEditTextField(binding.giEt)){
          binding.giLayout.setError("Gender information is required");
          return false;
      }
      if (!util.checkEditTextField(binding.statusEt)){
          binding.statusLayout.setError("Relationship status information is required");
          return false;
      }
      if (!util.checkEditTextField(binding.childrenEt)){
          binding.childrenLayout.setError("Children information is required");
          return false;
      }
      if (!util.checkEditTextField(binding.smokingEt)){
          binding.smokingLayout.setError("Smoking information is required");
          return false;
      }
      if (!util.checkEditTextField(binding.religionEt)){
          binding.religionLayout.setError("Religion information is required");
          return false;
      }
      if (!util.checkEditTextField(binding.drinkingLayoutEt)){
          binding.drinkingLayout.setError("Drinking information is required");
          return false;
      }
      if (!util.checkEditTextField(binding.heightEt)){
          binding.heightLayout.setError("Height information is required");
          return false;
      }
      if (!util.checkEditTextField(binding.eyeEt)){
          binding.eyeLayout.setError("Eye color information is required");
          return false;
      }
      if (!util.checkEditTextField(binding.hairEt)){
          binding.hairLayout.setError("Hair color information is required");
          return false;
      }
      return true;
    }

    @Override
    public void onBackPressed() {
        this.finish();
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    @Override
    public void onClick(View view) {

        switch (view.getId()){
            case R.id.saveButton:
                if (checkAllFields()){
                    saveDetails();
                }
                break;
        }

    }

    private void saveDetails() {
        dialog.setTitleText("Saving....");
        dialog.show();
        List<User> users = User.find(User.class,"username = ?",user);
        final User currentUser = new User();
        currentUser.setUsername(users.get(0).getUsername());
        currentUser.setPassword(users.get(0).getPassword());
        currentUser.setEmail(users.get(0).getEmail());
        currentUser.setGender_others(binding.genderEt.getText().toString().trim());
        currentUser.setAboutme(binding.abtMeEt.getText().toString().trim());
        currentUser.setAge_self(binding.ageEt.getText().toString().trim());
        currentUser.setCity_self(util.getCity(binding.residenceEt.getText().toString().trim()));
        currentUser.setCountry_self(util.getCountry(binding.residenceEt.getText().toString().trim()));
        currentUser.setAge_others(binding.ageOthersEt.getText().toString().trim());
        currentUser.setGender_self(binding.giEt.getText().toString().trim());
        currentUser.setLifestyle_others(binding.lifestyleEt.getText().toString().trim());
        currentUser.setRelationship_others(binding.forEt.getText().toString().trim());
        currentUser.setLifestyle_self(binding.lifestyleSelfEt.getText().toString().trim());
        currentUser.setSexual_orientation_self(binding.soEt.getText().toString().trim());
        currentUser.setStatus_self(binding.statusEt.getText().toString().trim());
        currentUser.setChildren_self(binding.childrenEt.getText().toString().trim());
        currentUser.setSmoking_self(binding.smokingEt.getText().toString().trim());
        currentUser.setReligin_self(binding.religionEt.getText().toString().trim());
        currentUser.setDrinking_self(binding.drinkingLayoutEt.getText().toString().trim());
        currentUser.setHeight_self(binding.heightEt.getText().toString().trim());
        currentUser.setEyecoloe_self(binding.eyeEt.getText().toString().trim());
        currentUser.setHaircolor_self(binding.hairEt.getText().toString().trim());
        currentUser.setPhotourl("https://api.backendless.com/648D896E-EDD8-49C8-FF74-2F1C32DB7A00/934C0B5C-A231-E928-FF37-655A05A3AB00/files/"+user+"/1.png");
        currentUser.setIsPremiumMember("no");
        currentUser.setObjectId(users.get(0).getObjectId());
        currentUser.setDateofBirth(users.get(0).getDateofBirth());
        currentUser.setWho_view_photos("All");
        currentUser.setFriend_requests("All");
        currentUser.setWho_view_friends("All");
        currentUser.setIncognito_mode("Yes");
        currentUser.setPackages("None");


        Backendless.Data.save(currentUser, new AsyncCallback<User>() {
            @Override
            public void handleResponse(User response) {
                User.deleteAll(User.class);
                currentUser.save();
                dialog.dismiss();
                Intent i = new Intent(SignupDetailsActivity.this,MainActivity.class);
                i.putExtra("redirectProfile",true);
                startActivity(i);
                finish();

            }

            @Override
            public void handleFault(BackendlessFault fault) {
                dialog.dismiss();
                error.setTitleText("Error connecting to VeMeet")
                        .setContentText("The following error has occured while trying to connect to VeMeet\n"
                        +fault.getMessage()+"\n Please try again").show();
            }
        });

        //todo
    }
}
