package ssapps.com.datingapp;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.databinding.DataBindingUtil;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.backendless.Backendless;
import com.backendless.async.callback.AsyncCallback;
import com.backendless.exceptions.BackendlessFault;
import com.orm.SugarContext;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.security.Permission;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

import Models.User;
import Util.Util;
import Util.Prefs;
import cn.pedant.SweetAlert.SweetAlertDialog;
import ssapps.com.datingapp.databinding.ActivitySignupDetailsBinding;

public class SignupDetailsActivity extends AppCompatActivity implements View.OnClickListener,EditText.OnEditorActionListener{

    ActivitySignupDetailsBinding binding;
    private Util util;
    private SweetAlertDialog dialog;
    private SweetAlertDialog error;
    private String user;
    private static final String appKey = "7EEB2727-4E8D-944C-FFDD-3D802BC37800";
    private static final String appId = "648D896E-EDD8-49C8-FF74-2F1C32DB7A00";
    private static final String GCM_SENDER_ID = "57050948456";
    private static final int ACCESS_FINE_LOCATION = 1;
    private static final int LOCATION_HARDWARE_1 = 2;
    private double[] location;
    private Prefs prefs;
    private JSONObject obj;
    private JSONArray array;
    ArrayList<String> countries = new ArrayList<String>();
    ArrayList<String> cities = new ArrayList<>();
    ArrayList<String> genders = new ArrayList<>();
    String country;
    private ArrayAdapter<String> citiesAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.activity_signup_details);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_signup_details);

       // Backendless.initApp(this,appKey,appId);
        Backendless.initApp(this,appId,appKey);
        SugarContext.init(this);




        util = new Util();
        prefs = new Prefs(this);
        dialog = new SweetAlertDialog(this,SweetAlertDialog.PROGRESS_TYPE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            dialog.getProgressHelper().setBarColor(getResources().getColor(R.color.leaf_green,null));
        } else {
            dialog.getProgressHelper().setBarColor(getResources().getColor(R.color.leaf_green));
        }
        dialog.setCancelable(false);
        dialog.dismiss();

        error =  new SweetAlertDialog(this,SweetAlertDialog.ERROR_TYPE);

        user = prefs.getname();

       // actionListeners();

        binding.profileImage.getLayoutParams().height = (int) (util.getScreenWidth(this)/2);
        Log.v("current user",prefs.getname());
      //  List<User> users_temp = User.find(User.class,"username = ?",user).size()
       Log.v("finding users", String.valueOf(User.find(User.class,"username = ?",user).size()));
        Log.v("photo url",User.find(User.class,"username = ?",user).get(0).getHasPicture());
        if (User.find(User.class,"username = ?",user).get(0).getHasPicture().equals("Yes")) {
            Picasso.with(this).load(User.find(User.class, "username = ?", user).get(0).getPhotourl())
                    .placeholder(R.drawable.fb)
                    .into(binding.profileImage);
        }
      //  Log.v("user",user);
      //  Log.v("user fetch",User.find(User.class,"username = ?",user).size())

        Locale[] locale = Locale.getAvailableLocales();
        for( Locale loc : locale ){
            country = loc.getDisplayCountry();
            if( country.length() > 0 && !countries.contains(country) ){
                countries.add( country );
            }
        }
        Collections.sort(countries, String.CASE_INSENSITIVE_ORDER);

     //   ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<String>(this,R.layout.tv_bg,countries);
      //  binding.countriesSpnner.setAdapter(spinnerAdapter);
      //  binding.countriesSpnner.setSelection(spinnerAdapter.getPosition("India"));

        ArrayAdapter<String> countriesAdapter = new ArrayAdapter<String>(this,R.layout.tv_bg1,countries);
        binding.countiresEtAuto.setThreshold(1);
        binding.countiresEtAuto.setAdapter(countriesAdapter);

        binding.countiresEtAuto.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Log.v("action info","recognised");
                InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(binding.countiresEtAuto.getWindowToken(), 0);

                try {
                    array = obj.getJSONArray(binding.countiresEtAuto.getText().toString());
                    cities = util.convertToList(array);
                    Collections.sort(cities,String.CASE_INSENSITIVE_ORDER);
                    citiesAdapter = new ArrayAdapter<String>(getApplicationContext(),R.layout.tv_bg1,cities);
                    binding.cityEtAuto.setAdapter(citiesAdapter);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        });


//        binding.cityEtAuto.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//            @Override
//            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
//                InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
//                imm.hideSoftInputFromWindow(binding.countiresEtAuto.getWindowToken(), 0);
//            }
//        });

        initialiseLocationSpinners();


       // binding.abtMeEt.setOnEditorActionListener(this);

//        binding.countriesSpnner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
//            @Override
//            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
//                try {
//                   // array = obj.getJSONArray(String.valueOf(binding.countriesSpnner.getSelectedItem()));
//                   // Log.v("json array", String.valueOf(array));
//                    Collections.sort(cities,String.CASE_INSENSITIVE_ORDER);
//                    citiesAdapter = new ArrayAdapter<String>(getApplicationContext(),R.layout.tv_bg,cities);
//                  //  binding.citiesSpinner.setAdapter(citiesAdapter);
//                   // binding.citiesSpinner.setSelection(0);
//                  //  Log.v("cities spinner","set");
//                } catch (JSONException e) {
//                    e.printStackTrace();
//                   // Log.v("json array exception", String.valueOf(e));
//                }
//            }
//
//            @Override
//            public void onNothingSelected(AdapterView<?> adapterView) {
//
//            }
//        });

//        ArrayAdapter<String> testAdapter = new ArrayAdapter<String>(this,R.layout.tv_bg,getResources().getStringArray(R.array.genderArray));
//        binding.genderTest.setAdapter(testAdapter);

        setSpinner(binding.genderSpnner,getResources().getStringArray(R.array.genderArray));
        setSpinner(binding.lifestyleSpnner,getResources().getStringArray(R.array.lifestyleArray));
        setSpinner(binding.forSpnner,getResources().getStringArray(R.array.forArray));
        setSpinner(binding.lifestyleSelfSpnner,getResources().getStringArray(R.array.lifestyleArray));
        setSpinner(binding.sexualOrientationSpnner,getResources().getStringArray(R.array.sexualOrientationArray));
        setSpinner(binding.genderIdentifySpnner,getResources().getStringArray(R.array.genderIndentifyArray));
        setSpinner(binding.statusSpnner,getResources().getStringArray(R.array.statusArray));
        setSpinner(binding.childrenSpnner,getResources().getStringArray(R.array.chldrenArray));
        setSpinner(binding.smokingSpnner,getResources().getStringArray(R.array.smokingArray));
        setSpinner(binding.religionSpnner,getResources().getStringArray(R.array.religionArray));
        setSpinner(binding.drinkingSpnner,getResources().getStringArray(R.array.smokingArray));
        setSpinner(binding.eyeSpnner,getResources().getStringArray(R.array.eyeColorArray));
        setSpinner(binding.hairSpnner,getResources().getStringArray(R.array.hairColorArray));


      //  setCountiresAutoTV();


       // setGenderSpinner();
       // setLifestyleSPinner();
       // setforSpinner();
       // setLifestyleSelfspinner();
        //setSexualOrientationSpinner();
        //setGenderIdentifySpinner();
      //  setStatusSpinner();
      //  setChildrenSpinner();

        // binding.counString jsonLocation = AssetJSONFile("formules.json", context)triesSpnner.setItems(countries);

        //  user = getIntent().getStringExtra("user");

//        if (getIntent().hasExtra("image")){
//            Bitmap bitmap = BitmapFactory.decodeByteArray(getIntent().getByteArrayExtra("image"),0,getIntent().getByteArrayExtra("image").length);
//            binding.profileImage.setImageBitmap(bitmap);
//        }

        binding.saveButton.setOnClickListener(this);


//        binding.abtMeEt.setOnKeyListener(new View.OnKeyListener() {
//            @Override
//            public boolean onKey(View view, int i, KeyEvent keyEvent) {
//                Log.v("key event","detected");
//                Log.v("key code", String.valueOf(keyEvent.getKeyCode())+"and"+String.valueOf(i));
//                return true;
//            }
//        });

//        binding.abtMeEt.setOnEditorActionListener(new TextView.OnEditorActionListener() {
//            @Override
//            public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
////                if (i == EditorInfo.IME_ACTION_DONE){
////                    Log.v("called","Keyboard");
////                }
////               // Log.v("called","Keyboard");
////               // Log.v()
////               // Log.v()
//                return true;
//            }
//        });

       // checkAllFields();

   // binding.abtMeEt.setOnEditorActionListener(this);

//        binding.abtMeEt.setOnEditorActionListener(new TextView.OnEditorActionListener() {
//            @Override
//            public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
//                InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
//                imm.hideSoftInputFromWindow(binding.abtMeEt.getWindowToken(), 0);
//                return true;
//            }
//        });
//
//        binding.ageEt.setOnEditorActionListener(new TextView.OnEditorActionListener() {
//            @Override
//            public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
//                InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
//                imm.hideSoftInputFromWindow(binding.ageEt.getWindowToken(), 0);
//            }
//        });

    }

//    private void actionListeners() {
//        binding.countiresEtAuto.setOnEditorActionListener(new TextView.OnEditorActionListener() {
//            @Override
//            public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
//                if (i == EditorInfo.IME_ACTION_DONE){
//                    Log.v("action info","recognised");
//                    return true;
//                } else if (i == EditorInfo.IME_FLAG_NO_ENTER_ACTION){
//                    Log.v("action info","recognised");
//                    return true;
//                }
//                Log.v("action info","recognised");
//                return true;
//            }
//        });
//
//
//    }

//    private void setCountiresAutoTV() {
//
//    }


    private void setSpinner(Spinner spinner,String[] list){
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getApplicationContext(),R.layout.tv_bg1,list);
       // spinner.setPadding();
        spinner.setPadding((int) util.convertDpToPixel(10,getApplicationContext()),0,0,0);
        spinner.setAdapter(adapter);
    }



//    private void setChildrenSpinner() {
//        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getApplicationContext(),R.layout.tv_bg,getResources().getStringArray(R.array.chldrenArray));
//        binding.childrenSpnner.setAdapter(adapter);
//    }
//
//    private void setStatusSpinner() {
//        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getApplicationContext(),R.layout.tv_bg,getResources().getStringArray(R.array.statusArray));
//        binding.statusSpnner.setAdapter(adapter);
//    }
//
//    private void setGenderIdentifySpinner() {
//        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getApplicationContext(),R.layout.tv_bg,getResources().getStringArray(R.array.genderIndentifyArray));
//        binding.genderIdentifySpnner.setAdapter(adapter);
//    }
//
//    private void setSexualOrientationSpinner() {
//        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getApplicationContext(),R.layout.tv_bg,getResources().getStringArray(R.array.sexualOrientationArray));
//        binding.sexualOrientationSpnner.setAdapter(adapter);
//    }
//
//    private void setLifestyleSelfspinner() {
//        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getApplicationContext(),R.layout.tv_bg,getResources().getStringArray(R.array.lifestyleArray));
//        binding.lifestyleSelfSpnner.setAdapter(adapter);
//    }
//
//    private void setforSpinner() {
//        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getApplicationContext(),R.layout.tv_bg,getResources().getStringArray(R.array.forArray));
//        binding.forSpnner.setAdapter(adapter);
//    }
//
//    private void setLifestyleSPinner() {
//        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getApplicationContext(),R.layout.tv_bg,getResources().getStringArray(R.array.lifestyleArray));
//        binding.lifestyleSpnner.setAdapter(adapter);
//    }
//
//    private void setGenderSpinner() {
//
//        ArrayAdapter<String> genderAdapter = new ArrayAdapter<String>(getApplicationContext(),R.layout.tv_bg,getResources().getStringArray(R.array.genderArray));
//        binding.genderSpnner.setAdapter(genderAdapter);
//
//    }

    private void initialiseLocationSpinners() {



        try {
           // Log.v("json","reading");
            InputStream is = getAssets().open("countriesToCities.json");
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            String json = new String(buffer,"UTF-8");
            obj = new JSONObject(json);
          //  Log.v("json ","object");
           // array = obj.getJSONArray(String.valueOf(binding.countriesSpnner.getSelectedItem()));
           // Log.v("json","array");
            cities = util.convertToList(array);
            Collections.sort(cities,String.CASE_INSENSITIVE_ORDER);
            citiesAdapter = new ArrayAdapter<String>(this,R.layout.tv_bg1,cities);
           // binding.cityEtAuto.setAdapter(citiesAdapter);
           // binding.citiesSpinner.setAdapter(citiesAdapter);
         //   binding.citiesSpinner.setSelection(citiesAdapter.getPosition("Vijayawada"));
           // Log.v("cities spinner","set");
        } catch (IOException e) {
            e.printStackTrace();
           // Log.v("json","error "+e.getMessage());
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

//    private String loadFromAsset() {
//        String json = null;
//            InputStream is = null;
//            try {
//                Log.v("json","reading");
//                is = getAssets().open("countriesToCities.json");
//                int size = is.available();
//                byte[] buffer = new byte[size];
//                is.read(buffer);
//                is.close();
//                json = new String(buffer,"UTF-8");
//            } catch (IOException e) {
//                e.printStackTrace();
//                Log.v("json","error "+e.getMessage());
//                return null;
//            }
//        return json;
//
//    }

    private boolean checkAllFields() {

      if (!util.checkEditTextField(binding.abtMeEt)){
          binding.aboutMeLayout.setError("Please write something about you");
          return false;
      }
      if (!util.checkEditTextField(binding.ageEt)){
          binding.ageLayout.setError("Please enter your age");
          return false;
      } else if (Integer.parseInt(binding.ageEt.getText().toString()) > 120 ||
              Integer.parseInt(binding.ageEt.getText().toString()) < 18 ){
          binding.ageLayout.setError("Please enter a age between 18-120");
          return false;
      }

//      if (!util.checkEditTextField(binding.residenceEt)){
//          binding.residenceLayout.setError("Please enter your city adn country information");
//          return false;
//      }
//      if (!util.checkEditTextField(binding.genderEt)){
//          binding.genderLayout.setError("Please enter your gender preference");
//          return false;
//      }
//      if (!util.checkEditTextField(binding.lifestyleEt)){
//          binding.lifestyleLayout.setError("Please enter your lifestyle preference");
//          return false;
//      }
      if (!util.checkEditTextField(binding.ageOthersEt)){
          binding.ageOthersLayout.setError("Please enter your age preference");
          return false;
      }
//      if (!util.checkEditTextField(binding.forEt)){
//          binding.forLayout.setError("Please enter your relationship preference");
//          return false;
//      }
//      if (!util.checkEditTextField(binding.lifestyleSelfEt)){
//          binding.lifestyleSelfLayout.setError("Lifestyle information is required");
//          return false;
//      }
//      if (!util.checkEditTextField(binding.soEt)){
//          binding.soLayout.setError("Sexual orientation information is required");
//          return false;
//      }
//      if (!util.checkEditTextField(binding.giEt)){
//          binding.giLayout.setError("Gender information is required");
//          return false;
//      }
//      if (!util.checkEditTextField(binding.statusEt)){
//          binding.statusLayout.setError("Relationship status information is required");
//          return false;
//      }
//      if (!util.checkEditTextField(binding.childrenEt)){
//          binding.childrenLayout.setError("Children information is required");
//          return false;
//      }
//      if (!util.checkEditTextField(binding.smokingEt)){
//          binding.smokingLayout.setError("Smoking information is required");
//          return false;
//      }
//      if (!util.checkEditTextField(binding.religionEt)){
//          binding.religionLayout.setError("Religion information is required");
//          return false;
//      }
//      if (!util.checkEditTextField(binding.drinkingLayoutEt)){
//          binding.drinkingLayout.setError("Drinking information is required");
//          return false;
//      }
      if (!util.checkEditTextField(binding.heightEt)){
          binding.heightLayout.setError("Height information is required");
          return false;
      }
//      if (!util.checkEditTextField(binding.eyeEt)){
//          binding.eyeLayout.setError("Eye color information is required");
//          return false;
//      }
//      if (!util.checkEditTextField(binding.hairEt)){
//          binding.hairLayout.setError("Hair color information is required");
//          return false;
//      }
        if (!util.checkEditTextField(binding.countiresEtAuto)){
            binding.countryLayout.setError("Country information is required");
          return false;
        }
        if (!util.checkEditTextField(binding.cityEtAuto)){
            binding.countryLayout.setError("City information is required");
            return false;
        }
        if (!countries.contains(binding.countiresEtAuto.getText().toString())){
            binding.countryLayout.setError("Your input doesn't match any of our records");
            return false;
        }
        if (!cities.contains(binding.cityEtAuto.getText().toString())){
            binding.cityLayout.setError("Your input doesn't match any of our records");
            return false;
        }
      return true;
    }

    @Override
    public void onBackPressed() {
       // this.finish();
       // do nothing
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
                    getLocation();
                   // saveDetails();
                }
                break;
        }

    }

    private void getLocation() {

        checkForFineLocationPermision();


    }


    private void checkForFineLocationPermision() {
        int check = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION);
        if (check == -1){
            Log.v("FinelocationPermission","Not present");
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION},ACCESS_FINE_LOCATION);
            }
        } else {
            Log.v("FinelocationPermission","present");
            getGPS();
           // checkForHardWarePermission();
        }
    }

    private void checkForHardWarePermission() {
        int check = ContextCompat.checkSelfPermission(this, Manifest.permission.LOCATION_HARDWARE);
        if (check == -1){
            Log.v("hardwarePermission","Not present");
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                Log.v("trying permission","hardware");
                requestPermissions(new String[]{Manifest.permission.LOCATION_HARDWARE},LOCATION_HARDWARE_1);
            }
        } else {
            Log.v("hardwarPermission","present");
            getGPS();
        }

    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode){
            case ACCESS_FINE_LOCATION:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Log.v("FinelocationPermission","granted");
                    getGPS();
                   // checkForHardWarePermission();
                } else {
                    Toast.makeText(this,"You need to give permission to access your location for others to find you",Toast.LENGTH_LONG).show();
                  //  checkForFineLocationPermision();
                    Log.v("FinelocationPermission","Not granted");
                }
                break;
            case LOCATION_HARDWARE_1:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED){
                    Log.v("Fhardware permission","granted");
                    getGPS();
                } else {
                    Toast.makeText(this,"You need to give permission to access your location for others to find you",Toast.LENGTH_LONG).show();
                  //  checkForHardWarePermission();
                    Log.v("Fhardware permission","not granted");
                }

        }
    }

    @SuppressLint("MissingPermission")
    private  void getGPS() {
        LocationManager lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        List<String> providers = lm.getProviders(true);

/* Loop over the array backwards, and if you get an accurate location, then break                 out the loop*/
        Location l = null;

        for (int i=providers.size()-1; i>=0; i--) {
            l = lm.getLastKnownLocation(providers.get(i));
            if (l != null) break;
        }

        double[] gps = new double[2];
        if (l != null) {
            gps[0] = l.getLatitude();
            gps[1] = l.getLongitude();
        }
        location = gps;
        Log.v("gps", String.valueOf(gps[1]));
        saveDetails();

    }

    private void saveDetails() {
        dialog.setTitleText("Saving....");
        dialog.show();
        List<User> users = User.find(User.class,"username = ?",user);
        final User currentUser = new User();
        currentUser.setUsername(users.get(0).getUsername());
        currentUser.setPassword(users.get(0).getPassword());
        currentUser.setEmail(users.get(0).getEmail());
        currentUser.setMailId(prefs.getTempEmail());
      //  currentUser.setGender_others(binding.genderEt.getText().toString().trim());
        currentUser.setGender_others(binding.genderSpnner.getSelectedItem().toString());
        currentUser.setAboutme(binding.abtMeEt.getText().toString().trim());
        currentUser.setAge_self(binding.ageEt.getText().toString().trim());
      //  currentUser.setCity_self(util.getCity(binding.residenceEt.getText().toString().trim()));
       // currentUser.setCity_self(binding.citiesSpinner.getSelectedItem().toString());
        currentUser.setCity_self(binding.cityEtAuto.getText().toString());
       // currentUser.setCountry_self(util.getCountry(binding.residenceEt.getText().toString().trim()));
       // currentUser.setCountry_self(binding.countriesSpnner.getSelectedItem().toString());
        currentUser.setCountry_self(binding.countiresEtAuto.getText().toString());
        currentUser.setAge_others(binding.ageOthersEt.getText().toString().trim());
        //currentUser.setGender_self(binding.giEt.getText().toString().trim());
        currentUser.setGender_self(binding.genderIdentifySpnner.getSelectedItem().toString());
       // currentUser.setLifestyle_others(binding.lifestyleEt.getText().toString().trim());
        currentUser.setLifestyle_others(binding.lifestyleSpnner.getSelectedItem().toString());
       // currentUser.setRelationship_others(binding.forEt.getText().toString().trim());
        currentUser.setRelationship_others(binding.forSpnner.getSelectedItem().toString());
       // currentUser.setLifestyle_self(binding.lifestyleSelfEt.getText().toString().trim());
        currentUser.setLifestyle_self(binding.lifestyleSelfSpnner.getSelectedItem().toString());
       // currentUser.setSexual_orientation_self(binding.soEt.getText().toString().trim());
        currentUser.setSexual_orientation_self(binding.sexualOrientationSpnner.getSelectedItem().toString());
      //  currentUser.setStatus_self(binding.statusEt.getText().toString().trim());
        currentUser.setStatus_self(binding.statusSpnner.getSelectedItem().toString());
    //    currentUser.setChildren_self(binding.childrenEt.getText().toString().trim());
        currentUser.setChildren_self(binding.childrenSpnner.getSelectedItem().toString());
        //currentUser.setSmoking_self(binding.smokingEt.getText().toString().trim());
        currentUser.setSmoking_self(binding.smokingSpnner.getSelectedItem().toString());
       // currentUser.setReligin_self(binding.religionEt.getText().toString().trim());
        currentUser.setReligin_self(binding.religionSpnner.getSelectedItem().toString());
       // currentUser.setDrinking_self(binding.drinkingLayoutEt.getText().toString().trim());
        currentUser.setDrinking_self(binding.drinkingSpnner.getSelectedItem().toString());
        currentUser.setHeight_self(binding.heightEt.getText().toString().trim());
      //  currentUser.setEyecoloe_self(binding.eyeEt.getText().toString().trim());
        currentUser.setEyecoloe_self(binding.eyeSpnner.getSelectedItem().toString());
      //  currentUser.setHaircolor_self(binding.hairEt.getText().toString().trim());
        currentUser.setHaircolor_self(binding.hairSpnner.getSelectedItem().toString());
        currentUser.setPhotourl("https://api.backendless.com/648D896E-EDD8-49C8-FF74-2F1C32DB7A00/934C0B5C-A231-E928-FF37-655A05A3AB00/files/"+user+"/1.png");
        currentUser.setIsPremiumMember("no");
       // currentUser.setObjectId(users.get(0).getObjectId());
        currentUser.setDateofBirth(users.get(0).getDateofBirth());
        currentUser.setWho_view_photos("All");
        currentUser.setFriend_requests("All");
        currentUser.setWho_view_friends("All");
        currentUser.setIncognito_mode("No");
        currentUser.setPackages("None");
        currentUser.setLatitude(String.valueOf(location[0]));
        currentUser.setLongitude(String.valueOf(location[1]));
        currentUser.setVideoUrl("None");
        currentUser.setIsOnline("Yes");
        currentUser.setHasPicture(users.get(0).getHasPicture());



        Backendless.Data.save(currentUser, new AsyncCallback<User>() {
            @Override
            public void handleResponse(User response) {
                User.deleteAll(User.class);
                currentUser.save();
                registerForPushNotifications();

            }

            @Override
            public void handleFault(BackendlessFault fault) {
                dialog.dismiss();
                Log.v("code", String.valueOf(fault));
                error.setTitleText("Error connecting to VeMeet")
                        .setContentText("The following error has occured while trying to connect to VeMeet\n"
                        +fault.getMessage()+"\n Please try again").show();
            }
        });


    }

    private void registerForPushNotifications(){


    Backendless.Messaging.registerDevice(GCM_SENDER_ID, prefs.getname(), new AsyncCallback<Void>() {
        @Override
        public void handleResponse(Void response) {
            dialog.dismiss();
            Intent i = new Intent(SignupDetailsActivity.this,MainActivity.class);
            i.putExtra("redirectProfile",true);
            startActivity(i);
            finish();
        }

        @Override
        public void handleFault(BackendlessFault fault) {
            dialog.dismiss();
            Intent i = new Intent(SignupDetailsActivity.this,MainActivity.class);
            i.putExtra("redirectProfile",true);
            startActivity(i);
            finish();
        }
    });

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //SugarContext.terminate();
    }


    @Override
    public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
        InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(binding.countiresEtAuto.getWindowToken(), 0);
      //  imm.hideSoftInputFromInputMethod(textView.getWindowToken(),0);
        Log.v("this","called");
        return true;
    }
}
