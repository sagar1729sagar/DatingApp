package ssapps.com.datingapp;

import android.databinding.DataBindingUtil;
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
import java.util.List;

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
            getData();
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
                if (isFirstTime){
                    isFirstTime = false;
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
                        sortData();

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

    private void sortData() {
        //todo
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
        isFirstTime = true;
        dialog.setTitleText("Searching....");
        getData();
    }
}
