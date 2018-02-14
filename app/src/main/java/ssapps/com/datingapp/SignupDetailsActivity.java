package ssapps.com.datingapp;

import android.databinding.DataBindingUtil;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.R;
import android.view.View;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.activity_signup_details);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_signup_details);


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

        user = getIntent().getStringExtra("user");

        if (getIntent().hasExtra("image")){
            Bitmap bitmap = BitmapFactory.decodeByteArray(getIntent().getByteArrayExtra("image"),0,getIntent().getByteArrayExtra("image").length);
            binding.profileImage.setImageBitmap(bitmap);
        }

        binding.saveButton.setOnClickListener(this);
       // checkAllFields();
        //todo
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
                saveDetails();
                break;
        }

    }

    private void saveDetails() {
        dialog.setTitleText("Saving....");
        List<User> users = User.find(User.class,"username = ?",user);
        //todo
    }
}
