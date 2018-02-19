package ssapps.com.datingapp;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.location.Location;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.backendless.Backendless;
import com.backendless.async.callback.AsyncCallback;
import com.backendless.exceptions.BackendlessFault;
import com.backendless.persistence.DataQueryBuilder;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;

import Models.SavedSearch;
import Models.SearchResults;
import Models.User;
import Util.Prefs;
import cn.pedant.SweetAlert.SweetAlertDialog;
import ssapps.com.datingapp.databinding.ActivitySearchBinding;



public class SearchActivity extends Fragment implements View.OnClickListener{
    ActivitySearchBinding binding;
   // private Boolean silence = true;
    private SweetAlertDialog error,dialog;
    private boolean isSearching = false;
    private boolean isFirstTime = true;
    private Prefs prefs;


    List<String> genders = new ArrayList<>();
    List<String> sexual_orientations = new ArrayList<>();
    List<String> lifestyles = new ArrayList<>();
    List<String> statuses = new ArrayList<>();
    List<String> min_ages = new ArrayList<>();
    List<String> max_ages  = new ArrayList<>();
    List<String> countries = new ArrayList<>();
    List<String> cities = new ArrayList<>();
    List<String> relationships = new ArrayList<>();
    List<String> children = new ArrayList<>();
    List<String> smoking = new ArrayList<>();
    List<String> religion = new ArrayList<>();
    List<String> drinking = new ArrayList<>();
    List<String> heights = new ArrayList<>();
    List<String> haircolor = new ArrayList<>();
    List<String> eyecolor = new ArrayList<>();



    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater,R.layout.activity_search,container,false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {

        dialog = new SweetAlertDialog(getContext(),SweetAlertDialog.PROGRESS_TYPE);
        dialog.setCancelable(false);
        dialog.setTitleText("Preparing the page....");
        error = new SweetAlertDialog(getContext(),SweetAlertDialog.ERROR_TYPE);

        prefs = new Prefs(getContext());

        long count = User.count(User.class);
        if (count == 0 || count == 1){
          //  silence = false;
            isSearching = false;
            getData();
        } else {
            prepareSearchPage();
        }

        binding.submitButton.setOnClickListener(this);

    }

    private void getData(){


           // if (!silence) {

                dialog.show();
           // }



        DataQueryBuilder query = DataQueryBuilder.create();
            query.setPageSize(100);

            pullData(query,true);



    }

    private void pullData(final DataQueryBuilder query, final boolean isFirst) {
        Backendless.Data.find(User.class, query, new AsyncCallback<List<User>>() {
            @Override
            public void handleResponse(List<User> response) {
                if (isFirst){
                   // isFirst = false;
                    if (prefs.getname().equals("None")){
                        User.deleteAll(User.class);
                    } else {
                        User currentUser = User.find(User.class,"username = ?",prefs.getname()).get(0);
                        User.deleteAll(User.class);
                        currentUser.save();
                    }
                }
                if (response.size() != 0){
                    User.saveInTx(response);
                    query.prepareNextPage();
                    pullData(query,false);
                } else if (response.size() == 0){
                    if (!isSearching) {
                        prepareSearchPage();
                    } else {
                        prepareData();

                    }
                }
            }

            @Override
            public void handleFault(BackendlessFault fault) {
                dialog.dismiss();
                if (isFirst){
                    error.setTitleText("Cannot prepare page");
                    error.setContentText("The following error has occured while contacting VeMeet\n"+fault.getMessage()+"\n Please try again later");
                    error.show();
                }
            }
        });
    }

    private void prepareData() {
        SavedSearch searchParams = new SavedSearch();
        searchParams.setGender(genders.get(binding.genderSearchMaterialSpinner.getSelectedIndex()));
        searchParams.setWho_are(sexual_orientations.get(binding.whoAreMaterialSpinner.getSelectedIndex()));
        searchParams.setLifestyle(lifestyles.get(binding.lifestyleMaterialSpinner.getSelectedIndex()));
        searchParams.setStatus(statuses.get(binding.statusMaterialSpinner.getSelectedIndex()));
        searchParams.setMin_age(min_ages.get(binding.ageMinMaterialSpinner.getSelectedIndex()));
        searchParams.setMax_age(max_ages.get(binding.ageMaxMaterialSpinner.getSelectedIndex()));
        searchParams.setCountry(countries.get(binding.countryMaterialSpinner.getSelectedIndex()));
        searchParams.setCity(cities.get(binding.cityMaterialSpinner.getSelectedIndex()));
        searchParams.setMiles(binding.milesEt.getText().toString());
        searchParams.setLooking_for(relationships.get(binding.forMaterialSpinner.getSelectedIndex()));
        searchParams.setChildren(children.get(binding.childrenMaterialSpinner.getSelectedIndex()));
        searchParams.setSmoking(smoking.get(binding.smokingMaterialSpinner.getSelectedIndex()));
        searchParams.setDrinking(drinking.get(binding.drinkingMaterialSpinner.getSelectedIndex()));
        searchParams.setReligion(religion.get(binding.religionMaterialSpinner.getSelectedIndex()));
        searchParams.setHeigh(heights.get(binding.heightMaterialSpinner.getSelectedIndex()));
        searchParams.setHaircolor(haircolor.get(binding.hairColorMaterialSpinner.getSelectedIndex()));
        searchParams.setEryecolor(eyecolor.get(binding.eyeColorMaterialSpinner.getSelectedIndex()));
        searchParams.setOnlyOnline(binding.onlyOnlineCheckbox.isChecked());
        searchParams.setOnlyWithPic(binding.onlyPicCheckbox.isChecked());
        searchParams.setWhosNew(binding.whoNewCheckbox.isChecked());
        searchParams.setIncognitoSearch(binding.incognitoCheckbox.isChecked());
        searchParams.setSaved_time(Calendar.getInstance().getTimeInMillis());
        if (binding.saveSearchCheckbox.isChecked()){
            searchParams.save();
        }

        List<User> results = User.listAll(User.class);
        //sortForGender(searchParams,results);
        sortForIncognitoSetting(searchParams,results);
    }

    private void sortForIncognitoSetting(SavedSearch searchParams, List<User> results) {
        for (User result:results){
            if (result.getIncognito_mode().equals("Yes")){
                results.remove(result);
            }
        }

        sortForGender(searchParams,results);
    }


    private void sortForGender(SavedSearch searchParams, List<User> results) {

        String gender = searchParams.getGender();

        for (int i=0;i<results.size();i++){
            if (!results.get(i).getGender_self().equals(gender)){
                results.remove(i);
            }
        }

        sortForSexualOrientations(searchParams,results);
    }

    private void sortForSexualOrientations(SavedSearch searchParams, List<User> results) {
        String sexual_Orientation = searchParams.getWho_are();

        for (User result:results){
            if (!result.getSexual_orientation_self().equals(sexual_Orientation)){
                results.remove(result);
            }
        }

        sortForLifeStyle(searchParams,results);
    }

    private void sortForLifeStyle(SavedSearch searchParams, List<User> results) {
        String lifestyle = searchParams.getLifestyle();
        for (User result:results){
            if (!result.getLifestyle_self().equals(lifestyle)){
                results.remove(result);
            }
        }

        sortForStatus(searchParams,results);
    }

    private void sortForStatus(SavedSearch searchParams, List<User> results) {
        String status = searchParams.getStatus();
        for (User result:results){
            if (!result.getStatus_self().equals(status)){
                results.remove(result);
            }
        }

        sortForMinAge(searchParams,results);
    }

    private void sortForMinAge(SavedSearch searchParams, List<User> results) {
        int min_age = Integer.parseInt(searchParams.getMin_age());
        for (User result:results){
            if (Integer.parseInt(result.getAge_self()) < min_age){
                results.remove(result);
            }
        }

        sortForMaxage(searchParams,results);
    }

    private void sortForMaxage(SavedSearch searchParams, List<User> results) {
        int max_age = Integer.parseInt(searchParams.getMax_age());
        for (User reuslt:results){
            if (Integer.parseInt(reuslt.getAge_self()) > max_age){
                results.remove(reuslt);
            }
        }

        sortForCountry(searchParams,results);
    }

    private void sortForCountry(SavedSearch searchParams, List<User> results) {
        String country = searchParams.getCountry();
        for (User result:results){
            if (!result.getCountry_self().equals(country)){
                results.remove(result);
            }
        }

        sortFoCity(searchParams,results);
    }

    private void sortFoCity(SavedSearch searchParams, List<User> results) {
        String city = searchParams.getCity();
        for (User result:results){
            if (!result.getCity_self().equals(city)){
                results.remove(result);
            }
        }

        searchForRelationship(searchParams,results);
    }

    private void searchForRelationship(SavedSearch searchParams, List<User> results) {
        String relationship = searchParams.getLooking_for();
        for (User result:results){
            if (!result.getRelationship_others().equals(relationship)){
                results.remove(result);
            }
        }

        searchForChildren(searchParams,results);
    }

    private void searchForChildren(SavedSearch searchParams, List<User> results) {
        String children = searchParams.getChildren();
        for (User result:results){
            if (!result.getChildren_self().equals(children)){
                results.remove(result);
            }
        }

        sortForSmoking(searchParams,results);
    }

    private void sortForSmoking(SavedSearch searchParams, List<User> results) {
        String smoke = searchParams.getSmoking();
        for (User result:results){
            if (!result.getSmoking_self().equals(smoke)){
                results.remove(result);
            }
        }

        sortForDrinking(searchParams,results);
    }

    private void sortForDrinking(SavedSearch searchParams, List<User> results) {
        String drink = searchParams.getDrinking();
        for (User result:results){
            if (!result.getDrinking_self().equals(drink)){
                results.remove(result);
            }
        }

        sortForReligion(searchParams,results);
    }

    private void sortForReligion(SavedSearch searchParams, List<User> results) {
        String religion = searchParams.getReligion();
        for (User result:results){
            if (!result.getReligin_self().equals(religion)){
                results.remove(result);
            }
        }

        sortForHeight(searchParams,results);
    }

    private void sortForHeight(SavedSearch searchParams, List<User> results) {
        String height = searchParams.getHeigh();
        for (User result:results){
            if (!result.getHeight_self().equals(height)){
                results.remove(result);
            }
        }

        sortForHaircolor(searchParams,results);
    }

    private void sortForHaircolor(SavedSearch searchParams, List<User> results) {
        String hair = searchParams.getHaircolor();
        for (User result:results){
            if (!result.getHaircolor_self().equals(hair)){
                results.remove(result);
            }
        }

        sortForEyecolor(searchParams,results);
    }

    private void sortForEyecolor(SavedSearch searchParams, List<User> results) {
        String eye = searchParams.getEryecolor();
        for (User result:results){
            if (!result.getEyecoloe_self().equals(eye)){
                results.remove(result);
            }
        }
        sorForOnline(searchParams,results);
    }

    private void sorForOnline(SavedSearch searchParams, List<User> results) {
        String online;
        if (searchParams.isOnlyOnline()){
            online = "Yes";
        } else {
            online = "No";
        }
        for (User result:results){
            if (!result.getIsOnline().equals(online)){
                results.remove(result);
            }
        }

        sortForPicture(searchParams,results);
    }

    private void sortForPicture(SavedSearch searchParams, List<User> results) {
        String pic;
        if (searchParams.isOnlyWithPic()){
            pic = "Yes";
        } else {
            pic = "No";
        }
        for (User result:results){
            if (!result.getHasPicture().equals(pic)){
                results.remove(result);
            }
        }

      //  saveRefinedResults(results);
        sortForDistance(searchParams,results);


    }


    private void sortForDistance(SavedSearch searchParams, List<User> results) {

        if (!prefs.getname().equals("None")){
            User loggeduser = User.find(User.class,"username = ?",prefs.getname()).get(0);
            if (checkForGeoPoint(loggeduser)){
                for (User result:results){
                    if (checkForGeoPoint(result)){
                        Float distance = calculateDistace(Double.parseDouble(loggeduser.getLatitude()),
                                Double.parseDouble(loggeduser.getLongitude()),Double.parseDouble(result.getLatitude()),
                                Double.parseDouble(result.getLongitude()));
                        if (distance > Float.parseFloat(searchParams.getMiles())){
                            results.remove(result);
                        }
                    }
                }
            }
        }

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

    private void prepareSearchPage() {


        List<User> allUsers = User.listAll(User.class);





        for (int i=0;i<allUsers.size();i++){
            if (!genders.contains(allUsers.get(i).getGender_self())){
                genders.add(allUsers.get(i).getGender_self());
            }
            if (!sexual_orientations.contains(allUsers.get(i).getSexual_orientation_self())){
                sexual_orientations.add(allUsers.get(i).getSexual_orientation_self());
            }
            if (!lifestyles.contains(allUsers.get(i).getLifestyle_self())){
                lifestyles.add(allUsers.get(i).getLifestyle_self());
            }
            if (!statuses.contains(allUsers.get(i).getStatus_self())){
                statuses.add(allUsers.get(i).getStatus_self());
            }
            if (!min_ages.contains(allUsers.get(i).getAge_self())){
                min_ages.add(allUsers.get(i).getAge_self());
                max_ages.add(allUsers.get(i).getAge_self());
            }
            if (!countries.contains(allUsers.get(i).getCountry_self())){
                countries.add(allUsers.get(i).getCountry_self());
            }
            if (!cities.contains(allUsers.get(i).getCity_self())){
                cities.add(allUsers.get(i).getCity_self());
            }
            if (!relationships.contains(allUsers.get(i).getRelationship_others())){
                relationships.add(allUsers.get(i).getRelationship_others());
            }
            if (!children.contains(allUsers.get(i).getChildren_self())){
                children.add(allUsers.get(i).getChildren_self());
            }
            if (!smoking.contains(allUsers.get(i).getSmoking_self())){
                smoking.add(allUsers.get(i).getSmoking_self());
            }
            if (!religion.contains(allUsers.get(i).getReligin_self())){
                religion.add(allUsers.get(i).getReligin_self());
            }
            if (!drinking.contains(allUsers.get(i).getDrinking_self())){
                drinking.add(allUsers.get(i).getDrinking_self());
            }
            if (!heights.contains(allUsers.get(i).getHeight_self())){
                heights.add(allUsers.get(i).getHeight_self());
            }
            if (!haircolor.contains(allUsers.get(i).getHaircolor_self())){
                haircolor.add(allUsers.get(i).getHaircolor_self());
            }
            if (eyecolor.contains(allUsers.get(i).getEyecoloe_self())){
                eyecolor.add(allUsers.get(i).getEyecoloe_self());
            }

        }

        binding.genderSearchMaterialSpinner.setItems(genders);
        binding.whoAreMaterialSpinner.setItems(sexual_orientations);
        binding.lifestyleMaterialSpinner.setItems(lifestyles);
        binding.statusMaterialSpinner.setItems(statuses);
        binding.ageMinMaterialSpinner.setItems(min_ages);
        binding.ageMaxMaterialSpinner.setItems(max_ages);
        binding.countryMaterialSpinner.setItems(countries);
        binding.cityMaterialSpinner.setItems(cities);
        binding.forMaterialSpinner.setItems(relationships);
        binding.childrenMaterialSpinner.setItems(children);
        binding.smokingMaterialSpinner.setItems(smoking);
        binding.religionMaterialSpinner.setItems(religion);
        binding.drinkingMaterialSpinner.setItems(drinking);
        binding.heightMaterialSpinner.setItems(heights);
        binding.hairColorMaterialSpinner.setItems(haircolor);
        binding.eyeColorMaterialSpinner.setItems(eyecolor);

        dialog.dismiss();
    }


    @Override
    public void onClick(View view) {
        isSearching = true;
        dialog.setTitleText("Searching....");
        getData();
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


}
