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
import android.support.v4.widget.CircularProgressDrawable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.Toast;

import com.backendless.Backendless;
import com.backendless.BackendlessUser;
import com.backendless.async.callback.AsyncCallback;
import com.backendless.exceptions.BackendlessFault;
import com.backendless.files.BackendlessFile;
import com.backendless.persistence.DataQueryBuilder;
import com.orm.SugarContext;
import com.orm.util.QueryBuilder;

import java.io.ByteArrayOutputStream;
import java.io.FileDescriptor;
import java.io.IOException;
import java.util.List;
import java.util.regex.Pattern;
import java.util.regex.Matcher;

import Models.User;
import Util.Util;
import cn.pedant.SweetAlert.SweetAlertDialog;
import ssapps.com.datingapp.databinding.ActivitySignupBinding;
import Util.Prefs;


public class SignupActivity extends AppCompatActivity implements View.OnClickListener,CompoundButton.OnCheckedChangeListener{

    private ActivitySignupBinding binding;
    private Util util;
    private static final int READ_EXT_STORAGE = 1;
    private static final int SELECT_PICTURE = 2;
    private static final int INTERNET_PERMISSION = 3;
    private Bitmap bitmap;
    private SweetAlertDialog dialog;
    private SweetAlertDialog error;
    //private static final String appKey = "7EEB27274E8D944CFFDD3D802BC37800";
    //private static final String appId = "648D896EEDD849C8FF742F1C32DB7A00";
    private static final String appKey = "7EEB2727-4E8D-944C-FFDD-3D802BC37800";
    private static final String appId = "648D896E-EDD8-49C8-FF74-2F1C32DB7A00";
    private Prefs pres;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
       // setContentView(R.layout.activity_signup);
        binding = DataBindingUtil.setContentView(this,R.layout.activity_signup);

        Backendless.initApp(this,appId,appKey);
        SugarContext.init(this);

       // radioButtonAdjstments()

        util = new Util();
        pres = new Prefs(this);

        ActionBar actionBar = getSupportActionBar();

        actionBar.setHomeButtonEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayShowHomeEnabled(true);

        dialog = new SweetAlertDialog(this,SweetAlertDialog.PROGRESS_TYPE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            dialog.getProgressHelper().setBarColor(getResources().getColor(R.color.leaf_green,null));
        } else {
            dialog.getProgressHelper().setBarColor(getResources().getColor(R.color.leaf_green));
        }
        dialog.setCancelable(false);
        dialog.dismiss();

        error = new SweetAlertDialog(this,SweetAlertDialog.ERROR_TYPE);


        binding.photoCircle1.setOnClickListener(this);

//        binding.photoCircle.setOnClickListener(this);
        binding.signupButton.setOnClickListener(this);

//
//        binding.maleRadiobutton.setChecked(true);
//        binding.femaleRadiobutton.setChecked(true);
//
//        binding.allRadiobutton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
//            @Override
//            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
//                Log.v("all button", String.valueOf(b));
//                binding.femaleRadiobutton.setChecked(false);
//            }
//        });

 //       binding.username.setOnClickListener(this);
//
//
        binding.maleRadiobutton.setOnCheckedChangeListener(this);
        binding.femaleRadiobutton.setOnCheckedChangeListener(this);
        binding.allRadiobutton.setOnCheckedChangeListener(this);
        binding.queerRadiobutton.setOnCheckedChangeListener(this);
        binding.transgenderRadiobutton.setOnCheckedChangeListener(this);
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

            case R.id.photo_circle1:
                selectImage();
                break;

            case R.id.signup_button:
               // startActivity(new Intent(SignupActivity.this,SignupDetailsActivity.class));
                //checkAllFields();
              //  setToast(String.valueOf(checkRadioButtonstatus()));
//                dialog.setTitleText("Checking...");
//                dialog.show();
                if (checkAllFields()){
                    checkForInternetPermission();
                }
             //   Log.v("pass",binding.password.getText().toString());
                break;

//            case R.id.username:
//                if (!(binding.userNameLayout.getError() == null) || !binding.userNameLayout.getError().equals("")){
//                    binding.userNameLayout.setError(null);
//                }

        }
    }

    private void checkForInternetPermission() {
        int check = ContextCompat.checkSelfPermission(this, Manifest.permission.INTERNET);
        if (check == -1){
            Log.v("internet permisison","not granted");
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                requestPermissions(new String[]{Manifest.permission.INTERNET},INTERNET_PERMISSION);
            }
        } else {
            Log.v("internet permisison","available");

           checkForUser(binding.username.getText().toString());
        }
    }

    private void checkForUser(String userName) {
        dialog.setTitleText("Checking...");
        dialog.show();
        String whereClause = "username = '"+userName+"'";
        DataQueryBuilder queryBuilder = DataQueryBuilder.create();
        queryBuilder.setWhereClause(whereClause);
       // queryBuilder.setPageSize(1000);
        Backendless.Data.of(User.class).find(queryBuilder, new AsyncCallback<List<User>>() {
            @Override
            public void handleResponse(List<User> response) {
                Log.v("check", String.valueOf(response));
                if (response == null){
                    dialog.dismiss();
                    registerUser();
                } else if (response.size() == 0){
                    dialog.dismiss();
                    registerUser();
                } else {
                    dialog.dismiss();
                    error.setTitleText("User name already taken")
                            .setContentText("Please modify your username and try again");
                }
            }

            @Override
            public void handleFault(BackendlessFault fault) {
                Log.v("check",fault.getCode());
                if (fault.getCode().equals("1009")){
                    //no table exists code
                    dialog.dismiss();
                    registerUser();
                } else {
                    dialog.dismiss();
                    error.setTitleText("Error connecting to VeMEET!!")
                            .setContentText("The following error has occured while connecting to VeMeet" +
                                    "\n" + fault.getMessage() + "\nPlease try again").show();
                }

            }
        });
    }

    private void registerUser() {
        Log.v("register","initiated");
        dialog.setTitleText("Registering");
        dialog.show();
        BackendlessUser user = new BackendlessUser();
        user.setProperty("username",binding.username.getText().toString());
        user.setEmail(binding.email.getText().toString());
        Log.v("pass",binding.password.getText().toString());
        user.setPassword(binding.password.getText().toString());
        Log.v("user Pass",user.getPassword());
        Backendless.UserService.register(user, new AsyncCallback<BackendlessUser>() {
            @Override
            public void handleResponse(BackendlessUser response) {
                Log.v("user","registered");
                saveResponse(response);
              //  loginUser();
            }

            @Override
            public void handleFault(BackendlessFault fault) {
                Log.v("register ","failed");
                dialog.dismiss();
                error.setTitleText("Error connecting to VeMEET!!")
                        .setContentText("The following error has occured while connecting to VeMeet" +
                                "\n"+fault.getMessage()+"\nPlease try again").show();
            }
        });

    }

    private void loginUser() {

        dialog.setTitleText("logging you in");
        dialog.show();

        String email = binding.email.getText().toString();
        String password = binding.password.getText().toString();

        Backendless.UserService.login(binding.username.getText().toString().trim(), password, new AsyncCallback<BackendlessUser>() {
            @Override
            public void handleResponse(BackendlessUser response) {
                dialog.dismiss();
                Log.v("login ","successfull");
                if (bitmap != null) {
                    saveProfileImage();
                } else {
                    gotoNextpage();
                }
            }

            @Override
            public void handleFault(BackendlessFault fault) {

                dialog.dismiss();
                Log.v("login fail",fault.getMessage());
                if (bitmap != null) {
                    saveProfileImage();
                } else {
                    gotoNextpage();
                }
            }
        },true);

    }

    private void saveResponse(BackendlessUser response) {
        dialog.dismiss();
        User user = new User();
        user.setUsername(binding.username.getText().toString());
        user.setPassword(binding.password.getText().toString());
        user.setDateofBirth(binding.dobEt.getText().toString());
        user.setIsOnline("Yes");
        if (bitmap == null){
            user.setHasPicture("No");
            Log.v("picture","no");
        } else {
            user.setHasPicture("Yes");
            Log.v("picture","yues");
        }
        user.save();
        Log.v("user","saved");
        Log.v("user pciture",user.getHasPicture());
        pres.setName(binding.username.getText().toString());
        loginUser();

       // startActivity(new Intent(SignupActivity.this,SignupDetailsActivity.class));
    }

    private void saveProfileImage() {
        dialog.setTitleText("Uploading your picture");
        dialog.show();
        String name = "1";
        Backendless.Files.Android.upload(bitmap, Bitmap.CompressFormat.PNG, 20, name + ".png",
                binding.username.getText().toString(), true, new AsyncCallback<BackendlessFile>() {
                    @Override
                    public void handleResponse(BackendlessFile response) {
                        dialog.dismiss();
                        Log.v("upload picture","success");
                        User currentuser = User.find(User.class,"username = ?",binding.username.getText().toString()).get(0);
                        currentuser.setHasPicture("Yes");
                        currentuser.setPhotourl("https://api.backendless.com/648D896E-EDD8-49C8-FF74-2F1C32DB7A00/934C0B5C-A231-E928-FF37-655A05A3AB00/files/"+binding.username.getText().toString()+"/1.png");
                        currentuser.save();
                        gotoNextpage();
                      //  startActivity(new Intent(SignupActivity.this,SignupDetailsActivity.class));
                    }

                    @Override
                    public void handleFault(BackendlessFault fault) {
                        dialog.dismiss();
                        Log.v("pivture upload failed",fault.getMessage());
                        setToast("Error uploading picture. Try later");
                        User currentuser = User.listAll(User.class).get(0);
                        currentuser.setHasPicture("No");
                        currentuser.save();
                        gotoNextpage();
                       // startActivity(new Intent(SignupActivity.this,SignupDetailsActivity.class));
                    }
                });
    }

    private void gotoNextpage() {
        Intent intent = new Intent(SignupActivity.this,SignupDetailsActivity.class);
//        if (bitmap != null) {
//            ByteArrayOutputStream bs = new ByteArrayOutputStream();
//            bitmap.compress(Bitmap.CompressFormat.PNG,50,bs);
//            intent.putExtra("image",bs.toByteArray());
//        }
      //  intent.putExtra("user",binding.username.getText().toString());
        pres.setName(binding.username.getText().toString());
        pres.setTempEmail(binding.email.getText().toString().trim());
        startActivity(intent);

    }


//    private void signup() {
//
//        Backendless.UserService.register();
//
//    }

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
                    //binding.photoCircle.setImageDrawable(dr);
                    binding.photoCircle1.setImageDrawable(dr);

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
//        if (!isValidPassword(binding.password.getText().toString())){
//            binding.passwordLayout.setError("Pleas enter a valid password. (Your password must contain min 8 characters," +
//                    " must be alpha numeric,must contain at least one symbol)");
//            return false;
//        }
//        if (getRadiButtonStatus() == 0 ){
//            setToast("Please select a gender");
//            return false;
//        }

        if (!util.checkEditTextField(binding.email)){
            binding.emailLayout.setError("Please enter your email id");
            return false;
        }

        if (!isValidEmail(binding.email.getText().toString())){
            binding.emailLayout.setError("Please enter a valid email address");
            return false;
        }

        if (!util.checkEditTextField(binding.dobEt)){
            setToast("Please enter your date of birth");
            return false;
        }

        return true;
    }


    private boolean isValidEmail(String email){
        return Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

    private  boolean isValidPassword(final String password) {

        Pattern pattern;
        Matcher matcher;
        final String PASSWORD_PATTERN = "^(?=.*[0-9])(?=.*[A-Z])(?=.*[@#$%^&+=!])(?=\\S+$).{4,}$";
        pattern = Pattern.compile(PASSWORD_PATTERN);
        matcher = pattern.matcher(password);

        return matcher.matches();

    }

//    public int getRadiButtonStatus(){
//        if (binding.maleRadiobutton.isSelected()){
//            return 1;
//        }
//        if (binding.femaleRadiobutton.isSelected()){
//            return 2;
//        }
//        if (binding.queerRadiobutton.isSelected()){
//            return 3;
//        }
//        if (binding.transgenderRadiobutton.isSelected()){
//            return 4;
//        }
//        if (binding.allRadiobutton.isSelected()){
//            return 5;
//        }
//        return 0;
//    }



    private void setToast(String messgae){
        Toast.makeText(this,messgae,Toast.LENGTH_SHORT).show();
    }

//    private int checkRadioButtonstatus(){
//        int i = 0;
//        if (binding.maleRadiobutton.isSelected()) {
//            i++;
//        }
//        if (binding.femaleRadiobutton.isSelected()){
//            i++;
//        }
//        if (binding.allRadiobutton.isSelected()){
//            i++;
//        }
//        if (binding.queerRadiobutton.isSelected()){
//            i++;
//        }
//        if (binding.transgenderRadiobutton.isSelected()){
//            i++;
//        }
//        return i;
//    }




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

            case INTERNET_PERMISSION:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED){
                    checkForUser(binding.username.getText().toString());
                } else {
                    setToast("Internet permission is required to create an account");
                }

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
//
//    }

    @Override
    public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
        switch (compoundButton.getId()){
            case R.id.male_radiobutton :
                if (binding.maleRadiobutton.isChecked()) {
                    changeRadioButtonStatus(true, false, false, false, false);
                }
                break;
            case R.id.female_radiobutton:
                if (binding.femaleRadiobutton.isChecked()){
                    changeRadioButtonStatus(false,true,false,false,false);
                }
                break;
            case R.id.queer_radiobutton:
                if (binding.queerRadiobutton.isChecked()){
                    changeRadioButtonStatus(false,false,true,false,false);
                }
                break;
            case R.id.transgender_radiobutton:
                if (binding.transgenderRadiobutton.isChecked()){
                    changeRadioButtonStatus(false,false,false,true,false);
                }
                break;
            case R.id.all_radiobutton:
                if (binding.allRadiobutton.isChecked()){
                    changeRadioButtonStatus(false,false,false,false,true);
                }
                break;
        }
    }

//    @Override
//    public void onCheckedChanged(RadioGroup radioGroup, int i) {
//        setToast("ca");
//    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        SugarContext.terminate();
    }
}
