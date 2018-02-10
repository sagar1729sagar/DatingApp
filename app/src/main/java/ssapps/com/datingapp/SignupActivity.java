package ssapps.com.datingapp;


import android.databinding.DataBindingUtil;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import java.util.regex.Pattern;
import java.util.regex.Matcher;

import Util.Util;
import ssapps.com.datingapp.databinding.ActivitySignupBinding;

public class SignupActivity extends AppCompatActivity implements View.OnClickListener {

    private ActivitySignupBinding binding;
    private Util util;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
       // setContentView(R.layout.activity_signup);
        binding = DataBindingUtil.setContentView(this,R.layout.activity_signup);

        util = new Util();

        ActionBar actionBar = getSupportActionBar();

        actionBar.setHomeButtonEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayShowHomeEnabled(true);




    }


    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.male_radiobutton :
                if (!binding.maleRadiobutton.isSelected()){
                    changeRadioButtonStatus(true,false,false,false,false);
                }
                break;
            case R.id.female_radiobutton:
                if (!binding.femaleRadiobutton.isSelected()){
                    changeRadioButtonStatus(false,true,false,false,false);
                }
                break;
            case R.id.queer_radiobutton:
                if (!binding.queerRadiobutton.isSelected()){
                    changeRadioButtonStatus(false,false,true,false,false);
                }
                break;
            case R.id.transgender_radiobutton:
                if (!binding.transgenderRadiobutton.isSelected()){
                    changeRadioButtonStatus(false,false,false,true,false);
                }
                break;
            case R.id.all_radiobutton:
                if (!binding.allRadiobutton.isSelected()){
                    changeRadioButtonStatus(false,false,false,false,true);
                }
                break;

            case R.id.signup_button:
                //checkAllFields();
                break;

        }
    }

    private  boolean checkAllFields(){
        if (!util.checkEditTextField(binding.username)){
            binding.userNameLayout.setError("Please enter a user name");
            return false;
        }
        if (!util.checkEditTextField(binding.password)){
            binding.passwordLayout.setError("Please enter a password");
            return false;
        }
        if (!util.checkEditTextField(binding.password) && !isValidPassword(binding.password.getText().toString())){
            binding.passwordLayout.setError("Pleas enter a valid password. (Your password must contain min 8 characters," +
                    " must be alpha numeric,must contain at least one symbol)");
            return false;
        }
        if (getRadiButtonStatus() == 0 ){
            setToast("Please select a gender");
            return false;
        }
        if (!util.checkEditTextField(binding.dobEt)){
            setToast("Please enter your date of birth");
            return false;
        }

        return true;
    }


    public int getRadiButtonStatus(){
        if (binding.maleRadiobutton.isSelected()){
            return 1;
        }
        if (binding.femaleRadiobutton.isSelected()){
            return 2;
        }
        if (binding.queerRadiobutton.isSelected()){
            return 3;
        }
        if (binding.transgenderRadiobutton.isSelected()){
            return 4;
        }
        if (binding.allRadiobutton.isSelected()){
            return 5;
        }
        return 0;
    }

    public  boolean isValidPassword(final String password) {

        Pattern pattern;
        Matcher matcher;
        final String PASSWORD_PATTERN = "^(?=.*[0-9])(?=.*[A-Z])(?=.*[@#$%^&+=!])(?=\\S+$).{4,}$";
        pattern = Pattern.compile(PASSWORD_PATTERN);
        matcher = pattern.matcher(password);

        return matcher.matches();

    }

    private void setToast(String messgae){
        Toast.makeText(this,messgae,Toast.LENGTH_SHORT).show();
    }




    private  void changeRadioButtonStatus(Boolean male,Boolean female,Boolean queer,Boolean tansgender,Boolean all){
        binding.maleRadiobutton.setSelected(male);
        binding.femaleRadiobutton.setSelected(female);
        binding.queerRadiobutton.setSelected(queer);
        binding.transgenderRadiobutton.setSelected(tansgender);
        binding.allRadiobutton.setSelected(all);
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
}
