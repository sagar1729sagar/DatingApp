package ssapps.com.datingapp;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.location.Location;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

import Adapters.SavedSearchAdapter;
import Models.SavedSearch;
import Models.SearchResults;
import Models.User;
import Util.Prefs;
import cn.pedant.SweetAlert.SweetAlertDialog;
import ssapps.com.datingapp.databinding.ActivitySavedSearchBinding;

public class SavedSearchActivity extends Fragment {

    private ActivitySavedSearchBinding binding;
    private List<SavedSearch> searches = new ArrayList<>();
    private SavedSearchAdapter adapter;
    private SavedSearch searchParams;
    private SweetAlertDialog dialog;
    private Prefs prefs;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater,R.layout.activity_saved_search,container,false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {

        prefs = new Prefs(getContext());

        dialog = new SweetAlertDialog(getContext(),SweetAlertDialog.PROGRESS_TYPE);
        dialog.setTitleText("Finding users...");
        dialog.setCancelable(false);

        RecyclerViewClickListener listener = new RecyclerViewClickListener() {
            @Override
            public void onClick(View view, int position) {

                searchParams = new SavedSearch();
                searchParams = searches.get(position);
                List<User> results = User.listAll(User.class);
                dialog.show();
                sortForIncognitoSetting(searchParams,results);
            }
        };

        searches = SavedSearch.listAll(SavedSearch.class);
        adapter = new SavedSearchAdapter(searches,listener);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getContext());
        binding.savedSearchList.setLayoutManager(layoutManager);
        binding.savedSearchList.setItemAnimator(new DefaultItemAnimator());
        binding.savedSearchList.setAdapter(adapter);


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

        sortForHaircolor(searchParams,results)
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
