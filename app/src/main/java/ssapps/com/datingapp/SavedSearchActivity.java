package ssapps.com.datingapp;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.location.Location;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.backendless.Backendless;
import com.backendless.async.callback.AsyncCallback;
import com.backendless.exceptions.BackendlessFault;
import com.backendless.persistence.DataQueryBuilder;
import com.orm.SugarContext;

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
    private SweetAlertDialog dialog,error;
    private Prefs prefs;
    private ArrayList<User> temp = new ArrayList<>();
    private boolean isFirstIteration;
    private static final String appKey = "7EEB2727-4E8D-944C-FFDD-3D802BC37800";
    private static final String appId = "648D896E-EDD8-49C8-FF74-2F1C32DB7A00";

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater,R.layout.activity_saved_search,container,false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {

        prefs = new Prefs(getContext());

        Backendless.initApp(getContext(),appId,appKey);
        SugarContext.init(getContext());

        dialog = new SweetAlertDialog(getContext(),SweetAlertDialog.PROGRESS_TYPE);
        dialog.setTitleText("Finding users...");
        dialog.setCancelable(false);
        error = new SweetAlertDialog(getContext(),SweetAlertDialog.ERROR_TYPE);

        RecyclerViewClickListener listener = new RecyclerViewClickListener() {
            @Override
            public void onClick(View view, int position) {
                    Log.v("position", String.valueOf(position));

                searchParams = searches.get(position);
              //  List<User> results = User.listAll(User.class);
                dialog.show();
                getData();
            }
        };

//        if(prefs.getname().equals("None")){
//            binding\
//        }

        searches = SavedSearch.listAll(SavedSearch.class);
        adapter = new SavedSearchAdapter(searches,listener);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getContext());
//        DividerItemDecoration itemDecorator = new DividerItemDecoration(getActivity(), DividerItemDecoration.VERTICAL);
//        itemDecorator.setDrawable(ContextCompat.getDrawable(getActivity(), R.drawable.divider));
        binding.savedSearchList.addItemDecoration(new DividerItemDecoration(getContext(),DividerItemDecoration.VERTICAL));
        binding.savedSearchList.setHasFixedSize(true);
        binding.savedSearchList.setLayoutManager(layoutManager);
        binding.savedSearchList.setItemAnimator(new DefaultItemAnimator());
        binding.savedSearchList.setAdapter(adapter);


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
                    User.saveInTx(response);
                    query.prepareNextPage();
                    pullData(query);
                } else {
                    Log.v("Total profiless", String.valueOf(User.count(User.class)));
                    sortForIncognitoSetting(searchParams,User.listAll(User.class));
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
                    sortForIncognitoSetting(searchParams,User.listAll(User.class));
                }
            }
        });
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
      //  Log.v("search height", String.valueOf(height));
        for (User result:results){
         //   Log.v("user height",result.getHeight_self());
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



//    private void sortForIncognitoSetting(SavedSearch searchParams, List<User> results) {
//        for (User result:results){
//            if (result.getIncognito_mode().equals("Yes")){
//                results.remove(result);
//            }
//        }
//
//        sortForGender(searchParams,results);
//    }
//
//    private void sortForGender(SavedSearch searchParams, List<User> results) {
//        String gender = searchParams.getGender();
//
//        for (int i=0;i<results.size();i++){
//            if (!results.get(i).getGender_self().equals(gender)){
//                results.remove(i);
//            }
//        }
//
//        sortForSexualOrientations(searchParams,results);
//    }
//
//    private void sortForSexualOrientations(SavedSearch searchParams, List<User> results) {
//        String sexual_Orientation = searchParams.getWho_are();
//
//        for (User result:results){
//            if (!result.getSexual_orientation_self().equals(sexual_Orientation)){
//                results.remove(result);
//            }
//        }
//
//        sortForLifeStyle(searchParams,results);
//    }
//
//    private void sortForLifeStyle(SavedSearch searchParams, List<User> results) {
//        String lifestyle = searchParams.getLifestyle();
//        for (User result:results){
//            if (!result.getLifestyle_self().equals(lifestyle)){
//                results.remove(result);
//            }
//        }
//
//        sortForStatus(searchParams,results);
//    }
//
//    private void sortForStatus(SavedSearch searchParams, List<User> results) {
//        String status = searchParams.getStatus();
//        for (User result:results){
//            if (!result.getStatus_self().equals(status)){
//                results.remove(result);
//            }
//        }
//
//        sortForMinAge(searchParams,results);
//    }
//
//    private void sortForMinAge(SavedSearch searchParams, List<User> results) {
//        int min_age = Integer.parseInt(searchParams.getMin_age());
//        for (User result:results){
//            if (Integer.parseInt(result.getAge_self()) < min_age){
//                results.remove(result);
//            }
//        }
//
//        sortForMaxage(searchParams,results);
//    }
//
//    private void sortForMaxage(SavedSearch searchParams, List<User> results) {
//        int max_age = Integer.parseInt(searchParams.getMax_age());
//        for (User reuslt:results){
//            if (Integer.parseInt(reuslt.getAge_self()) > max_age){
//                results.remove(reuslt);
//            }
//        }
//
//        sortForCountry(searchParams,results);
//    }
//
//    private void sortForCountry(SavedSearch searchParams, List<User> results) {
//        String country = searchParams.getCountry();
//        for (User result:results){
//            if (!result.getCountry_self().equals(country)){
//                results.remove(result);
//            }
//        }
//
//        sortFoCity(searchParams,results);
//    }
//
//    private void sortFoCity(SavedSearch searchParams, List<User> results) {
//        String city = searchParams.getCity();
//        for (User result:results){
//            if (!result.getCity_self().equals(city)){
//                results.remove(result);
//            }
//        }
//
//        searchForRelationship(searchParams,results);
//    }
//
//    private void searchForRelationship(SavedSearch searchParams, List<User> results) {
//        String relationship = searchParams.getLooking_for();
//        for (User result:results){
//            if (!result.getRelationship_others().equals(relationship)){
//                results.remove(result);
//            }
//        }
//
//        searchForChildren(searchParams,results);
//    }
//
//    private void searchForChildren(SavedSearch searchParams, List<User> results) {
//        String children = searchParams.getChildren();
//        for (User result:results){
//            if (!result.getChildren_self().equals(children)){
//                results.remove(result);
//            }
//        }
//
//        sortForSmoking(searchParams,results);
//    }
//
//    private void sortForSmoking(SavedSearch searchParams, List<User> results) {
//        String smoke = searchParams.getSmoking();
//        for (User result:results){
//            if (!result.getSmoking_self().equals(smoke)){
//                results.remove(result);
//            }
//        }
//
//        sortForDrinking(searchParams,results);
//    }
//
//    private void sortForDrinking(SavedSearch searchParams, List<User> results) {
//        String drink = searchParams.getDrinking();
//        for (User result:results){
//            if (!result.getDrinking_self().equals(drink)){
//                results.remove(result);
//            }
//        }
//
//        sortForReligion(searchParams,results);
//    }
//
//    private void sortForReligion(SavedSearch searchParams, List<User> results) {
//        String religion = searchParams.getReligion();
//        for (User result:results){
//            if (!result.getReligin_self().equals(religion)){
//                results.remove(result);
//            }
//        }
//
//        sortForHeight(searchParams,results);
//    }
//
//    private void sortForHeight(SavedSearch searchParams, List<User> results) {
//        String height = searchParams.getHeigh();
//        for (User result:results){
//            if (!result.getHeight_self().equals(height)){
//                results.remove(result);
//            }
//        }
//
//        sortForHaircolor(searchParams,results);
//    }
//
//    private void sortForHaircolor(SavedSearch searchParams, List<User> results) {
//        String hair = searchParams.getHaircolor();
//        for (User result:results){
//            if (!result.getHaircolor_self().equals(hair)){
//                results.remove(result);
//            }
//        }
//
//        sortForEyecolor(searchParams,results);
//    }
//
//    private void sortForEyecolor(SavedSearch searchParams, List<User> results) {
//        String eye = searchParams.getEryecolor();
//        for (User result:results){
//            if (!result.getEyecoloe_self().equals(eye)){
//                results.remove(result);
//            }
//        }
//        sorForOnline(searchParams,results);
//    }
//
//    private void sorForOnline(SavedSearch searchParams, List<User> results) {
//        String online;
//        if (searchParams.isOnlyOnline()){
//            online = "Yes";
//        } else {
//            online = "No";
//        }
//        for (User result:results){
//            if (!result.getIsOnline().equals(online)){
//                results.remove(result);
//            }
//        }
//
//        sortForPicture(searchParams,results);
//    }
//
//    private void sortForPicture(SavedSearch searchParams, List<User> results) {
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
//        }
//
//        //  saveRefinedResults(results);
//        sortForDistance(searchParams,results);
//    }
//
//    private void sortForDistance(SavedSearch searchParams, List<User> results) {
//        if (!prefs.getname().equals("None")){
//            User loggeduser = User.find(User.class,"username = ?",prefs.getname()).get(0);
//            if (checkForGeoPoint(loggeduser)){
//                for (User result:results){
//                    if (checkForGeoPoint(result)){
//                        Float distance = calculateDistace(Double.parseDouble(loggeduser.getLatitude()),
//                                Double.parseDouble(loggeduser.getLongitude()),Double.parseDouble(result.getLatitude()),
//                                Double.parseDouble(result.getLongitude()));
//                        if (distance > Float.parseFloat(searchParams.getMiles())){
//                            results.remove(result);
//                        }
//                    }
//                }
//            }
//        }
//
//        saveRefinedResults(results);
//    }
//
//    private void saveRefinedResults(List<User> results) {
//        SearchResults.deleteAll(SearchResults.class);
//        for (User result:results){
//            SearchResults searchResult = new SearchResults(result);
//            searchResult.save();
//        }
//        dialog.dismiss();
//        //  Intent intent = new Intent(SearchActivity.this)
//        startActivity(new Intent(getContext(),SearchResultsDisplayActivity.class));
//    }

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
