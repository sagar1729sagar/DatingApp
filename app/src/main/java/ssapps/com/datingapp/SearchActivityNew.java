package ssapps.com.datingapp;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import com.backendless.Backendless;
import com.backendless.async.callback.AsyncCallback;
import com.backendless.exceptions.BackendlessFault;
import com.backendless.persistence.DataQueryBuilder;
import com.orm.SugarContext;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

import Models.SavedSearch;
import Models.SearchResults;
import Models.User;
import Util.Util;
import Util.Prefs;
import cn.pedant.SweetAlert.SweetAlertDialog;
import ssapps.com.datingapp.databinding.ActivitySearchBinding;

/**
 * Created by sagar on 08/03/18.
 */

public class SearchActivityNew extends Fragment implements View.OnClickListener {

    private  ActivitySearchBinding binding;
    private static final String appKey = "7EEB2727-4E8D-944C-FFDD-3D802BC37800";
    private static final String appId = "648D896E-EDD8-49C8-FF74-2F1C32DB7A00";
    private Util util;
    private JSONObject obj;
    private JSONArray array;
    ArrayList<String> countries = new ArrayList<String>();
    ArrayList<String> cities = new ArrayList<>();
    ArrayList<String> genders = new ArrayList<>();
    String country;
    private ArrayAdapter<String> citiesAdapter;
    private SweetAlertDialog dialog,error;
    private boolean isFirstIteration;
    private Prefs prefs;
    private ArrayList<User> temp = new ArrayList<>();

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater,R.layout.activity_search,container,false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Backendless.initApp(getContext(),appId,appKey);
        SugarContext.init(getContext());

        prefs = new Prefs(getContext());
        dialog = new SweetAlertDialog(getContext(),SweetAlertDialog.PROGRESS_TYPE);
        dialog.setCancelable(false);
        dialog.setTitleText("Searching");
        error = new SweetAlertDialog(getContext(),SweetAlertDialog.ERROR_TYPE);

        util = new Util();

        if (prefs.getname().equals("None")){
            binding.saveSearchCheckbox.setVisibility(View.GONE);
            binding.incognitoCheckbox.setVisibility(View.GONE);
        }

        //        binding.whoAreMaterialSpinner.setItems(sexual_orientations);
//        binding.lifestyleMaterialSpinner.setItems(lifestyles);
//        binding.statusMaterialSpinner.setItems(statuses);
//        binding.ageMinMaterialSpinner.setItems(min_ages);
//        binding.ageMaxMaterialSpinner.setItems(max_ages);
//        binding.countryMaterialSpinner.setItems(countries);
//        binding.cityMaterialSpinner.setItems(cities);
//        binding.forMaterialSpinner.setItems(relationships);
//        binding.childrenMaterialSpinner.setItems(children);
//        binding.smokingMaterialSpinner.setItems(smoking);
//        binding.religionMaterialSpinner.setItems(religion);
//        binding.drinkingMaterialSpinner.setItems(drinking);
//        binding.heightMaterialSpinner.setItems(heights);
//        binding.hairColorMaterialSpinner.setItems(haircolor);
//        binding.eyeColorMaterialSpinner.setItems(eyecolor);
        setSpinner(binding.statusMaterialSpinner,getResources().getStringArray(R.array.statusArray));
        setSpinner(binding.genderSearchMaterialSpinner,getResources().getStringArray(R.array.genderIndentifyArray));
        setSpinner(binding.whoAreMaterialSpinner,getResources().getStringArray(R.array.sexualOrientationArray));
        setSpinner(binding.lifestyleMaterialSpinner,getResources().getStringArray(R.array.lifestyleArray));
        setSpinner(binding.forMaterialSpinner,getResources().getStringArray(R.array.forArray));
        setSpinner(binding.childrenMaterialSpinner,getResources().getStringArray(R.array.chldrenArray));
        setSpinner(binding.smokingMaterialSpinner,getResources().getStringArray(R.array.smokingArray));
        setSpinner(binding.religionMaterialSpinner,getResources().getStringArray(R.array.religionArray));
        setSpinner(binding.drinkingMaterialSpinner,getResources().getStringArray(R.array.smokingArray));
        setSpinner(binding.hairColorMaterialSpinner,getResources().getStringArray(R.array.hairColorArray));
        setSpinner(binding.eyeColorMaterialSpinner,getResources().getStringArray(R.array.eyeColorArray));




        Locale[] locale = Locale.getAvailableLocales();
        for( Locale loc : locale ){
            country = loc.getDisplayCountry();
            if( country.length() > 0 && !countries.contains(country) ){
                countries.add( country );
            }
        }
        Collections.sort(countries, String.CASE_INSENSITIVE_ORDER);

        ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<String>(getContext(),R.layout.tv_bg,countries);
        binding.countryMaterialSpinner.setAdapter(spinnerAdapter);
        binding.countryMaterialSpinner.setSelection(spinnerAdapter.getPosition("India"));
        initialiseLocationSpinners();


        binding.countryMaterialSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                try {
                array = obj.getJSONArray(String.valueOf(binding.countryMaterialSpinner.getSelectedItem()));
                // Log.v("json array", String.valueOf(array));
                Collections.sort(cities,String.CASE_INSENSITIVE_ORDER);
                citiesAdapter = new ArrayAdapter<String>(getContext(),R.layout.tv_bg,cities);
                binding.cityMaterialSpinner.setAdapter(citiesAdapter);
                binding.cityMaterialSpinner.setSelection(0);
                //  Log.v("cities spinner","set");
            } catch (JSONException e) {
                e.printStackTrace();
                // Log.v("json array exception", String.valueOf(e));
            }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });


        binding.submitButton.setOnClickListener(this);

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
            array = obj.getJSONArray(String.valueOf(binding.countryMaterialSpinner.getSelectedItem()));
            // Log.v("json","array");
            cities = util.convertToList(array);
            Collections.sort(cities,String.CASE_INSENSITIVE_ORDER);
            citiesAdapter = new ArrayAdapter<String>(getContext(),R.layout.tv_bg,cities);
            binding.cityMaterialSpinner.setAdapter(citiesAdapter);
            binding.cityMaterialSpinner.setSelection(citiesAdapter.getPosition("Vijayawada"));
            // Log.v("cities spinner","set");
        } catch (IOException e) {
            e.printStackTrace();
            // Log.v("json","error "+e.getMessage());
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }


    private void setSpinner(Spinner spinner, String[] list){
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getContext(),R.layout.tv_bg,list);
        // spinner.setPadding();
      //  spinner.setPadding((int) util.convertDpToPixel(10,getContext()),0,0,(int) util.convertDpToPixel(10,getContext()));
        spinner.setAdapter(adapter);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
       // SugarContext.terminate();
    }

    @Override
    public void onClick(View view) {

        switch (view.getId()){
            case R.id.submit_button :
                if (checkFields()) {
                    dialog.show();
                    getData();
                }
                break;
        }

    }

    private void getData() {

        isFirstIteration = true;

        DataQueryBuilder query = DataQueryBuilder.create();
        query.setPageSize(100);
        pullData(query);

    }

    private void pullData(final DataQueryBuilder query) {
        Backendless.Data.find(User.class, query, new AsyncCallback<List<User>>() {
            @Override
            public void handleResponse(List<User> response) {
                Log.v("response size", String.valueOf(response.size()));
                if (isFirstIteration){
                    isFirstIteration = false;
                    if (prefs.getname().equals("None")){
                        User.deleteAll(User.class);
                    } else {
                        User tempUser = User.find(User.class,"username = ?",prefs.getname()).get(0);
                        User.deleteAll(User.class);
                        tempUser.save();
                    }
                }

                if (response.size() != 0){
                    for (User person:response){
                        person.setId(User.count(User.class) + 1);
                        person.save();
                    }
                   // User.saveInTx(response);
                    query.prepareNextPage();
                    pullData(query);
                } else {
                    Log.v("Total profiless", String.valueOf(User.count(User.class)));
                    prepareData();
                }
            }

            @Override
            public void handleFault(BackendlessFault fault) {
                dialog.dismiss();
                if (isFirstIteration){
                    error.setTitleText("Cannot Search for users");
                    error.setContentText("The following error has occured while contacting VeMeet\n"+fault.getMessage()+"\n Please try again later");
                    error.show();
                } else {
                    prepareData();
                }
            }
        });
    }


    private boolean checkFields(){
        if (!util.checkEditTextField(binding.ageMinEt)){
            Toast.makeText(getContext(),"Minimun age should be entered",Toast.LENGTH_LONG).show();
            return false;
        }
        if (Integer.parseInt(binding.ageMinEt.getText().toString().trim()) < 18 ||
                Integer.parseInt(binding.ageMinEt.getText().toString().trim()) > 120){
            Toast.makeText(getContext(),"Minimun age should be between 18 and 120",Toast.LENGTH_LONG).show();
            return false;
        }
        if (!util.checkEditTextField(binding.ageMaxEt)){
            Toast.makeText(getContext(),"Maximum age should be entered",Toast.LENGTH_LONG).show();
            return false;
        }
        if (Integer.parseInt(binding.ageMaxEt.getText().toString().trim()) < 18 ||
                Integer.parseInt(binding.ageMaxEt.getText().toString().trim()) > 120){
            Toast.makeText(getContext(),"Maximum age should be between 18 and 120",Toast.LENGTH_LONG).show();
            return false;
        }
        if (Integer.parseInt(binding.ageMaxEt.getText().toString().trim()) < Integer.parseInt(binding.ageMinEt.getText().toString().trim())){
            Toast.makeText(getContext(),"Maximum age should be more than minimum age",Toast.LENGTH_LONG).show();
            return false;
        }
        if (!util.checkEditTextField(binding.milesEt)){
            Toast.makeText(getContext(),"Search distance is to be entered",Toast.LENGTH_LONG).show();
            return false;
        }
        if (!util.checkEditTextField(binding.heightMaxEt)){
            Toast.makeText(getContext(),"Maximum height information is required",Toast.LENGTH_LONG).show();
            return false;
        }
        if (!util.checkEditTextField(binding.heightMinEt)){
            Toast.makeText(getContext(),"Minimum height information is required",Toast.LENGTH_LONG).show();
            return false;
        }
        return true;
    }

    private void prepareData() {
        Log.v("prepare data","called");
        if (checkFields()) {
            Log.v("all fields","ok");
            SavedSearch searchParams = new SavedSearch();
            searchParams.setGender(binding.genderSearchMaterialSpinner.getSelectedItem().toString());
            searchParams.setWho_are(binding.whoAreMaterialSpinner.getSelectedItem().toString());
            searchParams.setLifestyle(binding.lifestyleMaterialSpinner.getSelectedItem().toString());
            searchParams.setStatus(binding.statusMaterialSpinner.getSelectedItem().toString());
            searchParams.setMin_age(binding.ageMinEt.getText().toString().trim());
            searchParams.setMax_age(binding.ageMaxEt.getText().toString().trim());
            searchParams.setCountry(binding.countryMaterialSpinner.getSelectedItem().toString());
            searchParams.setCity(binding.cityMaterialSpinner.getSelectedItem().toString());
            searchParams.setMiles(binding.milesEt.getText().toString().trim());
            searchParams.setLooking_for(binding.forMaterialSpinner.getSelectedItem().toString());
            searchParams.setChildren(binding.childrenMaterialSpinner.getSelectedItem().toString());
            searchParams.setSmoking(binding.smokingMaterialSpinner.getSelectedItem().toString());
            searchParams.setDrinking(binding.drinkingMaterialSpinner.getSelectedItem().toString());
            searchParams.setReligion(binding.religionMaterialSpinner.getSelectedItem().toString());
            searchParams.setHeight_max(binding.heightMaxEt.getText().toString());
            searchParams.setHeight_min(binding.heightMinEt.getText().toString());
           // searchParams.setHeigh(binding.heightEt.getText().toString().trim());
            searchParams.setHaircolor(binding.hairColorMaterialSpinner.getSelectedItem().toString());
            searchParams.setEryecolor(binding.eyeColorMaterialSpinner.getSelectedItem().toString());
            searchParams.setOnlyOnline(binding.onlyOnlineCheckbox.isChecked());
            searchParams.setOnlyWithPic(binding.onlyPicCheckbox.isChecked());
            searchParams.setWhosNew(binding.whoNewCheckbox.isChecked());
            searchParams.setIncognitoSearch(binding.incognitoCheckbox.isChecked());
           // searchParams.setSaved_time();
            if (binding.saveSearchCheckbox.isChecked()){
                searchParams.setSaved_time(Calendar.getInstance().getTimeInMillis());
                searchParams.save();
            }
            sortForIncognitoSetting(searchParams,User.listAll(User.class));
        }
    }

    private void sortForIncognitoSetting(SavedSearch searchParams, List<User> results) {
        Log.v("incognito ","called");
       // ArrayList<User> temp = new ArrayList<>();
        temp.clear();
        for (User result:results){
            if (result.getIncognito_mode().equals("Yes")){
                //results.remove(result);
                temp.add(result);
            }
        }
        if (temp.size() != 0){
            results.removeAll(temp);
        }
        Log.v("incognito results", String.valueOf(results.size()));
        sortForGender(searchParams,results);
    }

    private void sortForGender(SavedSearch searchParams, List<User> results) {

        String gender = searchParams.getGender();
        //ArrayList<User> temp =
        temp.clear();
        for (int i=0;i<results.size();i++){
            if (!results.get(i).getGender_self().equals(gender)){
               // results.remove(i);
                temp.add(results.get(i));
            }
        }
        if (temp.size() != 0){
            results.removeAll(temp);
        }
        Log.v("gender results", String.valueOf(results.size()));
        sortForSexualOrientations(searchParams,results);
    }

    private void sortForSexualOrientations(SavedSearch searchParams, List<User> results) {
        String sexual_Orientation = searchParams.getWho_are();
        temp.clear();
        for (User result:results){
            if (!result.getSexual_orientation_self().equals(sexual_Orientation)){
                //results.remove(result);
                temp.add(result);
            }
        }
        if (temp.size() != 0){
            results.removeAll(temp);
        }
        Log.v("so results", String.valueOf(results.size()));
        sortForLifeStyle(searchParams,results);
    }

    private void sortForLifeStyle(SavedSearch searchParams, List<User> results) {
        String lifestyle = searchParams.getLifestyle();
        temp.clear();
        for (User result:results){
            if (!result.getLifestyle_self().equals(lifestyle)){
                //results.remove(result);
                temp.add(result);
            }
        }
        if (temp.size() != 0){
            results.removeAll(temp);
        }
        Log.v("lifestyle results", String.valueOf(results.size()));
        sortForStatus(searchParams,results);
    }

    private void sortForStatus(SavedSearch searchParams, List<User> results) {
        String status = searchParams.getStatus();
        temp.clear();
        for (User result:results){
            if (!result.getStatus_self().equals(status)){
                //results.remove(result);
                temp.add(result);
            }
        }
        if (temp.size() != 0){
            results.removeAll(temp);
        }
        Log.v("status results", String.valueOf(results.size()));
        sortForMinAge(searchParams,results);
    }

    private void sortForMinAge(SavedSearch searchParams, List<User> results) {
        int min_age = Integer.parseInt(searchParams.getMin_age());
        Log.v("min age", String.valueOf(min_age));
        temp.clear();
        for (User result:results){
            Log.v("min age of result",result.getAge_self());
            if (Integer.parseInt(result.getAge_self()) < min_age){
                //results.remove(result);
                Log.v("mn age","satisfied");
                temp.add(result);
            }
        }
        if (temp.size() != 0){
            results.removeAll(temp);
        }
        Log.v("min age results", String.valueOf(results.size()));
        sortForMaxage(searchParams,results);
    }

    private void sortForMaxage(SavedSearch searchParams, List<User> results) {
        int max_age = Integer.parseInt(searchParams.getMax_age());
        temp.clear();
        for (User reuslt:results){
            if (Integer.parseInt(reuslt.getAge_self()) > max_age){
                //results.remove(reuslt);
                temp.add(reuslt);
            }
        }
        if (temp.size() != 0){
            results.removeAll(temp);
        }
        Log.v("max results", String.valueOf(results.size()));
        sortForCountry(searchParams,results);
    }

    private void sortForCountry(SavedSearch searchParams, List<User> results) {
        String country = searchParams.getCountry();
        temp.clear();
        for (User result:results){
            if (!result.getCountry_self().equals(country)){
                //results.remove(result);
                temp.add(result);
            }
        }
        if (temp.size() != 0){
            results.removeAll(temp);
        }
        Log.v("country results", String.valueOf(results.size()));
        sortFoCity(searchParams,results);
    }

    private void sortFoCity(SavedSearch searchParams, List<User> results) {
        String city = searchParams.getCity();
        temp.clear();
        for (User result:results){
            if (!result.getCity_self().equals(city)){
                //results.remove(result);
                temp.add(result);
            }
        }
        if (temp.size() != 0){
            results.removeAll(temp);
        }
        Log.v("cityresults", String.valueOf(results.size()));
        searchForRelationship(searchParams,results);
    }

    private void searchForRelationship(SavedSearch searchParams, List<User> results) {
        String relationship = searchParams.getLooking_for();
        temp.clear();
        for (User result:results){
            if (!result.getRelationship_others().equals(relationship)){
                //results.remove(result);
                temp.add(result);
            }
        }
        if (temp.size() != 0){
            results.removeAll(temp);
        }
        Log.v("relationship results", String.valueOf(results.size()));
        searchForChildren(searchParams,results);
    }

    private void searchForChildren(SavedSearch searchParams, List<User> results) {
        String children = searchParams.getChildren();
        temp.clear();
        for (User result:results){
            if (!result.getChildren_self().equals(children)){
                //results.remove(result);
                temp.add(result);
            }
        }
        if (temp.size() != 0){
            results.removeAll(temp);
        }
        Log.v("childrenresults", String.valueOf(results.size()));
        sortForSmoking(searchParams,results);
    }

    private void sortForSmoking(SavedSearch searchParams, List<User> results) {
        String smoke = searchParams.getSmoking();
        temp.clear();
        for (User result:results){
            if (!result.getSmoking_self().equals(smoke)){
                //results.remove(result);
                temp.add(result);
            }
        }
        if (temp.size() != 0){
            results.removeAll(temp);
        }
        Log.v("smoking results", String.valueOf(results.size()));
        sortForDrinking(searchParams,results);
    }

    private void sortForDrinking(SavedSearch searchParams, List<User> results) {
        String drink = searchParams.getDrinking();
        temp.clear();
        for (User result:results){
            if (!result.getDrinking_self().equals(drink)){
                //results.remove(result);
                temp.add(result);
            }
        }
        if (temp.size() != 0){
            results.removeAll(temp);
        }
        Log.v("drinkingresults", String.valueOf(results.size()));
        sortForReligion(searchParams,results);
    }

    private void sortForReligion(SavedSearch searchParams, List<User> results) {
        String religion = searchParams.getReligion();
        temp.clear();
        for (User result:results){
            if (!result.getReligin_self().equals(religion)){
                //results.remove(result);
                temp.add(result);
            }
        }
        if (temp.size() != 0){
            results.removeAll(temp);
        }
        Log.v("religion results", String.valueOf(results.size()));
        sortForHeight(searchParams,results);
    }

    private void sortForHeight(SavedSearch searchParams, List<User> results) {
        Float height_max = Float.valueOf(searchParams.getHeight_max());
        Float height_min = Float.valueOf(searchParams.getHeight_min());
        temp.clear();
        Log.v("height max",searchParams.getHeight_max());
        Log.v("Height min",searchParams.getHeight_min());
      //  Log.v("search height", String.valueOf(height));
        for (User result:results){
            Log.v("user height",result.getHeight_self());
            if (Float.valueOf(result.getHeight_self()) > height_max && Float.valueOf(result.getHeight_self()) < height_min){
                //results.remove(result);
                temp.add(result);
            }
        }
        if (temp.size() != 0){
            results.removeAll(temp);
        }
        Log.v("height results", String.valueOf(results.size()));
        sortForHaircolor(searchParams,results);
    }

    private void sortForHaircolor(SavedSearch searchParams, List<User> results) {
        String hair = searchParams.getHaircolor();
        temp.clear();
        for (User result:results){
            if (!result.getHaircolor_self().equals(hair)){
                //results.remove(result);
                temp.add(result);
            }
        }
        if (temp.size() != 0){
            results.removeAll(temp);
        }
        Log.v("hair color results", String.valueOf(results.size()));
        sortForEyecolor(searchParams,results);
    }

    private void sortForEyecolor(SavedSearch searchParams, List<User> results) {
        String eye = searchParams.getEryecolor();
        temp.clear();
        for (User result:results){
            if (!result.getEyecoloe_self().equals(eye)){
                //results.remove(result);
                temp.add(result);
            }
        }
        if (temp.size() != 0){
            results.removeAll(temp);
        }
        Log.v("eye color results", String.valueOf(results.size()));
        sorForOnline(searchParams,results);
    }

    private void sorForOnline(SavedSearch searchParams, List<User> results) {
//        String online;
//        if (searchParams.isOnlyOnline()){
//            online = "Yes";
//        } else {
//            online = "No";
//        }

//        for (int i=0;i<results.size();i++){
//            if (!results.get(i).getIsOnline().equals(online)){
//                temp.add(results.get(i));
//            }
//        }
//        if (temp.size() != 0){
//            results.removeAll(temp);
//            Collections.reverse(temp);
//            for (int i=0;i<temp.size();i++){
//                results.remove(temp.get(i));
//               // results.remo
////            }
//        }
        //List<User> temp = new ArrayList<>();
        temp.clear();
        if (searchParams.isOnlyOnline()){
            for (User result:results){
                if (result.getIsOnline().equals("No")){
                    temp.add(result);
                }
            }
        }
//        for (User result:results){
//            if (!result.getIsOnline().equals(online)){
//                temp.add(result);
//            }
      //  }
        if (temp.size() != 0){
            results.removeAll(temp);
        }
        Log.v("onlineresults", String.valueOf(results.size()));
        sortForPicture(searchParams,results);
    }

    private void sortForPicture(SavedSearch searchParams, List<User> results) {
//        String pic;
//        if (searchParams.isOnlyWithPic()){
//            pic = "Yes";
//        } else {
//            pic = "No";
//        }
//        for (User result:results){
//            if (!result.getHasPicture().equals(pic)){
//                results.remove(result);
//            }
  //      }
        temp.clear();
        if (searchParams.isOnlyWithPic()){
            for (User result:results){
                if (!result.getHasPicture().equals("Yes")){
                    //results.remove(result);
                    temp.add(result);
                }
            }
        }

        if (temp.size() != 0){
            results.removeAll(temp);
        }
        //  saveRefinedResults(results);
        Log.v("pictur results", String.valueOf(results.size()));
        sortForDistance(searchParams,results);
    }

    private void sortForDistance(SavedSearch searchParams, List<User> results) {
        temp.clear();
        if (!prefs.getname().equals("None")){
            User loggeduser = User.find(User.class,"username = ?",prefs.getname()).get(0);
            if (checkForGeoPoint(loggeduser)){
                for (User result:results){
                    if (checkForGeoPoint(result)){
                        Float distance = calculateDistace(Double.parseDouble(loggeduser.getLatitude()),
                                Double.parseDouble(loggeduser.getLongitude()),Double.parseDouble(result.getLatitude()),
                                Double.parseDouble(result.getLongitude()));
                        if (distance > Float.parseFloat(searchParams.getMiles())){
                     //       results.remove(result);
                            temp.add(result);
                        }
                    }
                }
            }
        }
        if (temp.size() != 0){
            results.removeAll(temp);
        }
        Log.v("distance results", String.valueOf(results.size()));
            saveRefinedResults(results);


    }

    private void saveRefinedResults(List<User> results) {
        SearchResults.deleteAll(SearchResults.class);
        for (User result:results){
            SearchResults searchResult = new SearchResults(result);
            searchResult.save();
        }
        dialog.dismiss();
        //  Intent intent = new Intent(SearchActivity.this)
        startActivity(new Intent(getContext(),SearchResultsDisplayActivity.class));
    }

    private boolean checkForGeoPoint(User user){
        if (user.getLatitude().isEmpty() || user.getLatitude() == null || user.getLatitude().equals("0")){
            return false;
        }
        if (user.getLongitude().isEmpty() || user.getLongitude() == null || user.getLongitude().equals("0")){
            return false;
        }
        return true;
    }


    private float calculateDistace(Double lat1,Double lon1,Double lat2,Double lon2){
        Location loc1 = new Location("");
        loc1.setLatitude(lat1);
        loc1.setLongitude(lon1);

        Location loc2 = new Location("");
        loc2.setLatitude(lat2);
        loc2.setLongitude(lon2);

        return loc1.distanceTo(loc2);
    }

    @Override
    public void onResume() {
        super.onResume();
        SugarContext.init(getContext());
    }
}
