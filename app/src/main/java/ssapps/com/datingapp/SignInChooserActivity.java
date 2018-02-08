package ssapps.com.datingapp;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import ssapps.com.datingapp.databinding.ActivitySignInChooserBinding;

public class SignInChooserActivity extends AppCompatActivity {

    ActivitySignInChooserBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
       // setContentView(R.layout.activity_sign_in_chooser);
        binding = DataBindingUtil.setContentView(this,R.layout.activity_sign_in_chooser);

        binding.registerChoose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               // startActivityForResult(new Intent(SignInChooserActivity.this,SignupActivity.class),0);
                startActivity(new Intent(SignInChooserActivity.this,SignupActivity.class));
            }
        });


    }
}
