package ssapps.com.datingapp;

import android.databinding.DataBindingUtil;
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

import com.backendless.Backendless;
import com.orm.SugarContext;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Locale;

import Util.Util;
import ssapps.com.datingapp.databinding.ActivitySearchBinding;

/**
 * Created by sagar on 08/03/18.
 */

public class SearchActivityNew extends Fragment {

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

        util = new Util();

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

        setSpinner(binding.genderSearchMaterialSpinner,getResources().getStringArray(R.array.genderIndentifyArray));
        setSpinner(binding.whoAreMaterialSpinner,getResources().getStringArray(R.array.sexualOrientationArray));
        setSpinner(binding.lifestyleMaterialSpinner,getResources().getStringArray(R.array.statusArray));
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
        SugarContext.terminate();
    }
}
