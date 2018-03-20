package ssapps.com.datingapp;

import android.*;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.PackageManager;
import android.databinding.DataBindingUtil;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
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
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

import Models.User;
import Util.Prefs;
import cn.pedant.SweetAlert.SweetAlertDialog;
import Util.Util;
import ssapps.com.datingapp.databinding.ActivitySignupDetailsBinding;


public class ProfileFragment extends Fragment implements View.OnClickListener{

    private Prefs prefs;
    private ImageView imageView;
    private EditText about_me,age_self,residence,gender_others,lifestyle_others,age_others,relationship_others,
                        lifestyle_self,so_self,gender_self,status_self,children_self,smoking_self,religion_self,
                        drinking_self,height_self,eyecolor_self,haircolor_self;
    private Button modifyButton;
    private static final int ACCESS_FINE_LOCATION = 1;
    private static final int LOCATION_HARDWARE_1 = 2;
    private SweetAlertDialog dialog;
    private SweetAlertDialog error;
    private User loggedUser;
    private Util util;
    private static final String appKey = "7EEB2727-4E8D-944C-FFDD-3D802BC37800";
    private static final String appId = "648D896E-EDD8-49C8-FF74-2F1C32DB7A00";
    private ActivitySignupDetailsBinding binding;
    private String user;
    ArrayList<String> countries = new ArrayList<String>();
    ArrayList<String> cities = new ArrayList<>();
    ArrayList<String> genders = new ArrayList<>();
    String country;
    private JSONObject obj;
    private JSONArray array;
    private ArrayAdapter<String> citiesAdapter;
    private double[] location;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
       // return inflater.inflate(R.layout.activity_profile_fragment,container,false);
        binding = DataBindingUtil.inflate(inflater,R.layout.activity_signup_details,container,false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {

        Backendless.initApp(getContext(),appId,appKey);
        SugarContext.init(getContext());

        util = new Util();
        prefs = new Prefs(getContext());
        dialog = new SweetAlertDialog(getContext(),SweetAlertDialog.PROGRESS_TYPE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            dialog.getProgressHelper().setBarColor(getResources().getColor(R.color.leaf_green,null));
        } else {
            dialog.getProgressHelper().setBarColor(getResources().getColor(R.color.leaf_green));
        }
        dialog.setCancelable(false);
        dialog.dismiss();

        error =  new SweetAlertDialog(getContext(),SweetAlertDialog.ERROR_TYPE);

        user = prefs.getname();

        loggedUser = User.find(User.class,"username = ?",user).get(0);

        binding.profileImage.getLayoutParams().height = (int) (util.getScreenWidth(getContext())/2);

        if (User.find(User.class,"username = ?",user).get(0).getHasPicture().equals("Yes")) {
            Picasso.with(getContext()).load(User.find(User.class, "username = ?", user).get(0).getPhotourl())
                    .placeholder(R.drawable.fb)
                    .into(binding.profileImage);
        }

        Locale[] locale = Locale.getAvailableLocales();
        for( Locale loc : locale ){
            country = loc.getDisplayCountry();
            if( country.length() > 0 && !countries.contains(country) ){
                countries.add( country );
            }
        }
        Collections.sort(countries, String.CASE_INSENSITIVE_ORDER);

        ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<String>(getContext(),R.layout.tv_bg,countries);
        binding.countiresEtAuto.setText(loggedUser.getCountry_self());
    //    binding.countriesSpnner.setAdapter(spinnerAdapter);
       // binding.countriesSpnner.setSelection(spinnerAdapter.getPosition("India"));
      //  binding.countriesSpnner.setSelection(spinnerAdapter.getPosition(loggedUser.getCountry_self()));

        initialiseLocationSpinners();

        binding.countiresEtAuto.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                InputMethodManager imm = (InputMethodManager)getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(binding.countiresEtAuto.getWindowToken(), 0);

                try {
                    array = obj.getJSONArray(String.valueOf(binding.countiresEtAuto.getText().toString()));
                    cities = util.convertToList(array);
                    Collections.sort(cities,String.CASE_INSENSITIVE_ORDER);
                    citiesAdapter = new ArrayAdapter<String>(getContext(),R.layout.tv_bg,cities);
                    binding.cityEtAuto.setAdapter(citiesAdapter);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        });

        binding.cityEtAuto.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                InputMethodManager imm = (InputMethodManager)getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(binding.countiresEtAuto.getWindowToken(), 0);
            }
        });


//        binding.countriesSpnner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
//            @Override
//            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
//                try {
//                 //   array = obj.getJSONArray(String.valueOf(binding.countriesSpnner.getSelectedItem()));
//                    // Log.v("json array", String.valueOf(array));
//                    Collections.sort(cities,String.CASE_INSENSITIVE_ORDER);
//                    citiesAdapter = new ArrayAdapter<String>(getContext(),R.layout.tv_bg,cities);
//                   // binding.citiesSpinner.setAdapter(citiesAdapter);
//                   // binding.citiesSpinner.setSelection(citiesAdapter.getPosition(loggedUser.getCity_self()));
//                    //  Log.v("cities spinner","set");
//                } catch (JSONException e) {
//                    e.printStackTrace();
//                    // Log.v("json array exception", String.valueOf(e));
//                }
//            }
//
//            @Override
//            public void onNothingSelected(AdapterView<?> adapterView) {
//
//            }
//        });

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

        binding.saveButton.setOnClickListener(this);


        binding.abtMeEt.setText(loggedUser.getAboutme());
        binding.ageEt.setText(loggedUser.getAge_self());
        binding.ageOthersEt.setText(loggedUser.getAge_others());
        binding.heightEt.setText(loggedUser.getHeight_self());

    }

    private void setSpinner(Spinner spinner, String[] list){
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getContext(),R.layout.tv_bg,list);
        spinner.setPadding((int) util.convertDpToPixel(10,getContext()),0,0,0);
        spinner.setAdapter(adapter);
        switch (spinner.getId()){
            case R.id.gender_spnner:
                spinner.setSelection(adapter.getPosition(loggedUser.getGender_others()));
                break;
            case R.id.lifestyle_spnner:
                spinner.setSelection(adapter.getPosition(loggedUser.getLifestyle_others()));
                break;
            case R.id.for_spnner:
                spinner.setSelection(adapter.getPosition(loggedUser.getRelationship_others()));
                break;
            case R.id.lifestyle_self_spnner:
                spinner.setSelection(adapter.getPosition(loggedUser.getLifestyle_self()));
                break;
            case R.id.sexual_orientation_spnner:
                spinner.setSelection(adapter.getPosition(loggedUser.getSexual_orientation_self()));
                break;
            case R.id.gender_identify_spnner:
                spinner.setSelection(adapter.getPosition(loggedUser.getGender_self()));
                break;
            case R.id.status_spnner:
                spinner.setSelection(adapter.getPosition(loggedUser.getStatus_self()));
                break;
            case R.id.children_spnner:
                spinner.setSelection(adapter.getPosition(loggedUser.getChildren_self()));
                break;
            case R.id.smoking_spnner:
                spinner.setSelection(adapter.getPosition(loggedUser.getSmoking_self()));
                break;
            case R.id.religion_spnner:
                spinner.setSelection(adapter.getPosition(loggedUser.getReligin_self()));
                break;
            case R.id.drinking_spnner:
                spinner.setSelection(adapter.getPosition(loggedUser.getDrinking_self()));
                break;
            case R.id.eye_spnner:
                spinner.setSelection(adapter.getPosition(loggedUser.getEyecoloe_self()));
                break;
            case R.id.hair_spnner:
                spinner.setSelection(adapter.getPosition(loggedUser.getHaircolor_self()));
                break;
        }
    }


    private void initialiseLocationSpinners() {



        try {
            // Log.v("json","reading");
            InputStream is = getActivity().getAssets().open("countriesToCities.json");
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            String json = new String(buffer,"UTF-8");
            obj = new JSONObject(json);
            //  Log.v("json ","object");
          //  array = obj.getJSONArray(String.valueOf(binding.countriesSpnner.getSelectedItem()));
            // Log.v("json","array");
            cities = util.convertToList(array);
            Collections.sort(cities,String.CASE_INSENSITIVE_ORDER);
            citiesAdapter = new ArrayAdapter<String>(getContext(),R.layout.tv_bg,cities);
            binding.cityEtAuto.setText(loggedUser.getCity_self());
           // binding.citiesSpinner.setAdapter(citiesAdapter);
         //ee   binding.citiesSpinner.setSelection(citiesAdapter.getPosition(loggedUser.getCity_self()));
            // Log.v("cities spinner","set");
        } catch (IOException e) {
            e.printStackTrace();
            // Log.v("json","error "+e.getMessage());
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

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
        return true;
    }

//        Backendless.initApp(getContext(),appId,appKey);
//        SugarContext.init(getContext());
//
//        prefs = new Prefs(getContext());
//        util = new Util();
//
//        error = new SweetAlertDialog(getContext(),SweetAlertDialog.ERROR_TYPE);
//        dialog = new SweetAlertDialog(getContext(),SweetAlertDialog.PROGRESS_TYPE);
//        dialog.setCancelable(false);
//        dialog.dismiss();
//
//        final List<User> users = User.find(User.class,"username = ?",prefs.getname());
//        Log.v("name",prefs.getname());
//        Log.v("size", String.valueOf(users.size()));
//        user = users.get(0);
//
//        imageView = (ImageView)view.findViewById(R.id.profile_image);
//        imageView.getLayoutParams().height = (int) (util.getScreenWidth(getContext())/2);
//
//        about_me = (EditText) view.findViewById(R.id.abtmeet);
//        about_me.setText(user.getAboutme());
//
//        age_self = (EditText) view.findViewById(R.id.ageet);
//        age_self.setText(user.getAge_self());
//
//        residence = (EditText) view.findViewById(R.id.residenceet);
//        residence.setText(user.getCity_self()+","+user.getCountry_self());
//
//        gender_others = (EditText) view.findViewById(R.id.genderet);
//        gender_others.setText(user.getGender_others());
//
//        lifestyle_others = (EditText) view.findViewById(R.id.lifestyleet);
//        lifestyle_others.setText(user.getLifestyle_others());
//
//        age_others = (EditText)view.findViewById(R.id.ageotherset);
//        age_others.setText(user.getAge_others());
//
//        relationship_others = (EditText)view.findViewById(R.id.foret);
//        relationship_others.setText(user.getRelationship_others());
//
//        lifestyle_self = (EditText)view.findViewById(R.id.lifestyleet);
//        lifestyle_self.setText(user.getLifestyle_self());
//
//        so_self = (EditText)view.findViewById(R.id.soet);
//        so_self.setText(user.getSexual_orientation_self());
//
//        gender_self = (EditText)view.findViewById(R.id.giet);
//        gender_self.setText(user.getGender_self());
//
//        status_self = (EditText)view.findViewById(R.id.statuset);
//        status_self.setText(user.getStatus_self());
//
//        children_self = (EditText)view.findViewById(R.id.childrenet);
//        children_self.setText(user.getChildren_self());
//
//        smoking_self = (EditText)view.findViewById(R.id.smokinget);
//        smoking_self.setText(user.getSmoking_self());
//
//        religion_self = (EditText)view.findViewById(R.id.religionet);
//        religion_self.setText(user.getReligin_self());
//
//        drinking_self = (EditText)view.findViewById(R.id.drinkinget);
//        drinking_self.setText(user.getDrinking_self());
//
//        height_self = (EditText)view.findViewById(R.id.heightet);
//        height_self.setText(user.getHeight_self());
//
//        eyecolor_self = (EditText)view.findViewById(R.id.eyeet);
//        eyecolor_self.setText(user.getEyecoloe_self());
//
//        haircolor_self = (EditText)view.findViewById(R.id.hairet);
//        haircolor_self.setText(user.getHaircolor_self());
//        Log.v("user", user.getHasPicture());
//        if (user.getHasPicture().equals("Yes")) {
//            Picasso.with(getContext()).load(user.getPhotourl()).into(imageView);
//        }
//
//
//        modifyButton = (Button)view.findViewById(R.id.modifyButton);
//        modifyButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                dialog.setTitleText("Modifying");
//                dialog.show();
//                User currentUser = new User();
//                currentUser.setUsername(user.getUsername());
//                //currentUser.setPassword(user.getPassword());
//                currentUser.setEmail(user.getEmail());
//                currentUser.setGender_others(gender_others.getText().toString().trim());
//                currentUser.setAboutme(about_me.getText().toString().trim());
//                currentUser.setAge_self(age_self.getText().toString().trim());
//                currentUser.setCity_self(util.getCity(residence.getText().toString().trim()));
//                currentUser.setCountry_self(util.getCountry(residence.getText().toString().trim()));
//                currentUser.setAge_others(age_others.getText().toString().trim());
//                currentUser.setGender_self(gender_self.getText().toString().trim());
//                currentUser.setLifestyle_others(lifestyle_others.getText().toString().trim());
//                currentUser.setRelationship_others(relationship_others.getText().toString().trim());
//                currentUser.setLifestyle_self(lifestyle_self.getText().toString().trim());
//                currentUser.setSexual_orientation_self(so_self.getText().toString().trim());
//                currentUser.setStatus_self(status_self.getText().toString().trim());
//                currentUser.setChildren_self(children_self.getText().toString().trim());
//                currentUser.setSmoking_self(smoking_self.getText().toString().trim());
//                currentUser.setReligin_self(religion_self.getText().toString().trim());
//                currentUser.setDrinking_self(drinking_self.getText().toString().trim());
//                currentUser.setHeight_self(height_self.getText().toString().trim());
//                currentUser.setEyecoloe_self(eyecolor_self.getText().toString().trim());
//                currentUser.setHaircolor_self(haircolor_self.getText().toString().trim());
//                currentUser.setPhotourl(user.getPhotourl());
//                currentUser.setIsPremiumMember(user.getIsPremiumMember());
//                currentUser.setObjectId(user.getObjectId());
//                currentUser.setDateofBirth(user.getDateofBirth());
//
//                Backendless.Data.save(currentUser, new AsyncCallback<User>() {
//                    @Override
//                    public void handleResponse(User response) {
//                        dialog.dismiss();
//                        Toast.makeText(getContext(),"Profile successfully modified",Toast.LENGTH_LONG).show();
//                    }
//
//                    @Override
//                    public void handleFault(BackendlessFault fault) {
//                        dialog.dismiss();
//                        error.setTitleText("Error");
//                        error.setContentText("The following error has occured while modifying profile \n"+
//                                fault.getMessage()+"\n Please try again");
//                        error.show();
//
//                    }
//                });
//
//            }
//        });
//
//    }



    @Override
    public void onDestroy() {
        super.onDestroy();
        SugarContext.terminate();
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
        int check = ContextCompat.checkSelfPermission(getContext(), android.Manifest.permission.ACCESS_FINE_LOCATION);
        if (check == -1){
            Log.v("FinelocationPermission","Not present");
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                requestPermissions(new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},ACCESS_FINE_LOCATION);
            }
        } else {
            Log.v("FinelocationPermission","present");
            getGPS();
            // checkForHardWarePermission();
        }
    }

    private void checkForHardWarePermission() {
        int check = ContextCompat.checkSelfPermission(getContext(), android.Manifest.permission.LOCATION_HARDWARE);
        if (check == -1){
            Log.v("hardwarePermission","Not present");
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                Log.v("trying permission","hardware");
                requestPermissions(new String[]{android.Manifest.permission.LOCATION_HARDWARE},LOCATION_HARDWARE_1);
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
                    Toast.makeText(getContext(),"You need to give permission to access your location for others to find you",Toast.LENGTH_LONG).show();
                    //  checkForFineLocationPermision();
                    Log.v("FinelocationPermission","Not granted");
                }
                break;
            case LOCATION_HARDWARE_1:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED){
                    Log.v("Fhardware permission","granted");
                    getGPS();
                } else {
                    Toast.makeText(getContext(),"You need to give permission to access your location for others to find you",Toast.LENGTH_LONG).show();
                    //  checkForHardWarePermission();
                    Log.v("Fhardware permission","not granted");
                }

        }
    }

    @SuppressLint("MissingPermission")
    private  void getGPS() {
        LocationManager lm = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);
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
        final User currentUser = User.find(User.class,"username = ?",user).get(0);
       // final User currentUser = new User();
      //  currentUser.setUsername(users.get(0).getUsername());
       // currentUser.setPassword(users.get(0).getPassword());
       // currentUser.setEmail(users.get(0).getEmail());
        //  currentUser.setGender_others(binding.genderEt.getText().toString().trim());
        currentUser.setGender_others(binding.genderSpnner.getSelectedItem().toString());
        currentUser.setAboutme(binding.abtMeEt.getText().toString().trim());
        currentUser.setAge_self(binding.ageEt.getText().toString().trim());
        //  currentUser.setCity_self(util.getCity(binding.residenceEt.getText().toString().trim()));
      //  currentUser.setCity_self(binding.citiesSpinner.getSelectedItem().toString());
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
       // currentUser.setPhotourl("https://api.backendless.com/648D896E-EDD8-49C8-FF74-2F1C32DB7A00/934C0B5C-A231-E928-FF37-655A05A3AB00/files/"+user+"/1.png");
       // currentUser.setIsPremiumMember("no");
        // currentUser.setObjectId(users.get(0).getObjectId());
       // currentUser.setDateofBirth(users.get(0).getDateofBirth());
      //  currentUser.setWho_view_photos("All");
      //  currentUser.setFriend_requests("All");
      //  currentUser.setWho_view_friends("All");
      //  currentUser.setIncognito_mode("No");
      //  currentUser.setPackages("None");
        if (location[0] != 0) {
            currentUser.setLatitude(String.valueOf(location[0]));
        }
        if (location[1] != 0) {
            currentUser.setLongitude(String.valueOf(location[1]));
        }
       // currentUser.setVideoUrl("None");
      //  currentUser.setIsOnline("Yes");
       // currentUser.setHasPicture(users.get(0).getHasPicture());




        Backendless.Data.save(currentUser, new AsyncCallback<User>() {
            @Override
            public void handleResponse(User response) {
               // User.deleteAll(User.class);
                currentUser.save();
                //registerForPushNotifications();
                dialog.dismiss();
                Toast.makeText(getContext(),"Details saved successfully",Toast.LENGTH_SHORT).show();

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
}
