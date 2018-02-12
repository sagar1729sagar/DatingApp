package ssapps.com.datingapp;


import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.database.Cursor;
import android.databinding.DataBindingUtil;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.ParcelFileDescriptor;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v4.graphics.drawable.RoundedBitmapDrawable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import java.io.FileDescriptor;
import java.io.IOException;
import java.util.regex.Pattern;
import java.util.regex.Matcher;

import Util.Util;
import ssapps.com.datingapp.databinding.ActivitySignupBinding;

public class SignupActivity extends AppCompatActivity implements View.OnClickListener{

    private ActivitySignupBinding binding;
    private Util util;
    private static final int READ_EXT_STORAGE = 1;
    private static final int SELECT_PICTURE = 2;
    private Bitmap bitmap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
       // setContentView(R.layout.activity_signup);
        binding = DataBindingUtil.setContentView(this,R.layout.activity_signup);

       // radioButtonAdjstments()

        util = new Util();

        ActionBar actionBar = getSupportActionBar();

        actionBar.setHomeButtonEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayShowHomeEnabled(true);

//        binding.photoCircle.setOnClickListener(this);
//        binding.signupButton.setOnClickListener(this);
//
//
//        binding.maleRadiobutton.setOnCheckedChangeListener(this);
//        binding.femaleRadiobutton.setOnCheckedChangeListener(this);
//        binding.allRadiobutton.setOnCheckedChangeListener(this);
//        binding.queerRadiobutton.setOnCheckedChangeListener(this);
//        binding.transgenderRadiobutton.setOnCheckedChangeListener(this);
//
//        binding.genderGroup.setOnCheckedChangeListener(this);


//        binding.maleRadiobutton.setOnClickListener(this);
//        binding.femaleRadiobutton.setOnClickListener(this);
//        binding.queerRadiobutton.setOnClickListener(this);
//        binding.transgenderRadiobutton.setOnClickListener(this);
//        binding.allRadiobutton.setOnClickListener(this);





    }

//    private void radioButtonAdjstments() {
//        binding.maleRadiobutton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
//            @Override
//            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
//
//            }
//        });
//    }


    @Override
    public void onClick(View view) {
        switch (view.getId()){

            case R.id.photo_circle:
                selectImage();
                break;

            case R.id.signup_button:
               // startActivity(new Intent(SignupActivity.this,SignupDetailsActivity.class));
                //checkAllFields();
                setToast(String.valueOf(checkRadioButtonstatus()));
                break;

        }
    }

    private void selectImage() {
        // check for read external storage permission

        int check = ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE);
        if (check == -1){

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},READ_EXT_STORAGE);
            }

        }else {

            Intent i = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            startActivityForResult(i, SELECT_PICTURE);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode){
            case SELECT_PICTURE:
                if (resultCode==this.RESULT_OK && data != null && data.getData() != null){

                    //get Image Location
                    Uri image = data.getData();
                    String[] filePath = {MediaStore.Images.Media.DATA};
                    Cursor cursor = this.getContentResolver().query(image,filePath,null,null,null);
                    cursor.moveToFirst();
                    int columnIndex = cursor.getColumnIndex(filePath[0]);
                    cursor.close();
                    try {
                        bitmap = getBitmapFromUri(image);
                    } catch (IOException e) {

                    }
                    //Display on imageView
                    //imageV.setImageURI(image);
                    Resources res = getResources();
//                    Bitmap src = BitmapFactory.decodeResource(res, iconResource);
                    RoundedBitmapDrawable dr =
                            RoundedBitmapDrawableFactory.create(res, bitmap);
                    dr.setCornerRadius(Math.max(bitmap.getWidth(), bitmap.getHeight()) / 2.0f);
                   // imageView.setImageDrawable(dr);
                    binding.photoCircle.setImageDrawable(dr);

                }
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

    private int checkRadioButtonstatus(){
        int i = 0;
        if (binding.maleRadiobutton.isSelected()) {
            i++;
        }
        if (binding.femaleRadiobutton.isSelected()){
            i++;
        }
        if (binding.allRadiobutton.isSelected()){
            i++;
        }
        if (binding.queerRadiobutton.isSelected()){
            i++;
        }
        if (binding.transgenderRadiobutton.isSelected()){
            i++;
        }
        return i;
    }




    private  void changeRadioButtonStatus(Boolean male,Boolean female,Boolean queer,Boolean tansgender,Boolean all){
        binding.maleRadiobutton.setChecked(male);
        binding.femaleRadiobutton.setChecked(female);
        binding.queerRadiobutton.setChecked(queer);
        binding.transgenderRadiobutton.setChecked(tansgender);
        binding.allRadiobutton.setChecked(all);
    }

    private Bitmap getBitmapFromUri(Uri uri) throws IOException {
        ParcelFileDescriptor parcelFileDescriptor =
                this.getContentResolver().openFileDescriptor(uri, "r");
        FileDescriptor fileDescriptor = parcelFileDescriptor.getFileDescriptor();
        Bitmap image = BitmapFactory.decodeFileDescriptor(fileDescriptor);
        parcelFileDescriptor.close();
        return image;
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode){
            case READ_EXT_STORAGE:
                if( grantResults[0] == PackageManager.PERMISSION_GRANTED){

                    Intent i = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    startActivityForResult(i, SELECT_PICTURE);

                } else {
                    setToast("You need to give permission to access gallery for uploading picture");
                   // Toast.makeText(getContext(),"You need to give permission to access gallery",Toast.LENGTH_LONG).show();

                }
                break;

        }
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

//    @Override
//    public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
//        switch (compoundButton.getId()){
//            case R.id.male_radiobutton :
//                if (!binding.maleRadiobutton.isChecked()){
//                    changeRadioButtonStatus(true,false,false,false,false);
//                }
//                break;
//            case R.id.female_radiobutton:
//                if (!binding.femaleRadiobutton.isChecked()){
//                    changeRadioButtonStatus(false,true,false,false,false);
//                }
//                break;
//            case R.id.queer_radiobutton:
//                if (!binding.queerRadiobutton.isChecked()){
//                    changeRadioButtonStatus(false,false,true,false,false);
//                }
//                break;
//            case R.id.transgender_radiobutton:
//                if (!binding.transgenderRadiobutton.isChecked()){
//                    changeRadioButtonStatus(false,false,false,true,false);
//                }
//                break;
//            case R.id.all_radiobutton:
//                if (!binding.allRadiobutton.isChecked()){
//                    changeRadioButtonStatus(false,false,false,false,true);
//                }
//                break;
//        }
//    }

//    @Override
//    public void onCheckedChanged(RadioGroup radioGroup, int i) {
//        setToast("ca");
//    }
}
