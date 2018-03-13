package ssapps.com.datingapp;

import android.databinding.DataBindingUtil;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;

import com.backendless.Backendless;
import com.orm.SugarContext;
import com.tsongkha.spinnerdatepicker.DatePicker;
import com.tsongkha.spinnerdatepicker.DatePickerDialog;
import com.tsongkha.spinnerdatepicker.SpinnerDatePickerDialogBuilder;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Locale;
import Util.Util;

import Models.User;
import Util.Prefs;
import ssapps.com.datingapp.databinding.ActivitySearch2Binding;

public class ActivitySearch extends AppCompatActivity implements AdapterView.OnItemSelectedListener,View.OnClickListener, DatePickerDialog.OnDateSetListener {

    private ActivitySearch2Binding binding;
    private ArrayList<String> countries = new ArrayList<>();
    private static final String appKey = "7EEB2727-4E8D-944C-FFDD-3D802BC37800";
    private static final String appId = "648D896E-EDD8-49C8-FF74-2F1C32DB7A00";
    private String country;
    private User loggedUser;
    private Prefs prefs;
    private JSONArray array;
    private JSONObject obj;
    private ArrayList<String> cities = new ArrayList<>();
    private ArrayAdapter citiesAdapter;
    private Util util;
    private String Date;
    private int day;
    private int month;
    private int year;
    private Long time;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_search2);
        Backendless.initApp(this,appId,appKey);
        SugarContext.init(this);
        prefs = new Prefs(this);
        util = new Util();
        if (!prefs.getname().equals("None")){
            loggedUser = User.find(User.class,"username = ?",prefs.getname()).get(0);
        }

        setCountrySpinner();
        setCitiesSpinner();
        binding.countrySpinner.setOnItemSelectedListener(this);
        setSeekbar();


        Calendar c = Calendar.getInstance();
        day = c.get(Calendar.DAY_OF_MONTH);
        month = c.get(Calendar.MONTH);
        year = c.get(Calendar.YEAR);

    }


    private void setSeekbar() {
        binding.seekBar.getBuilder().setMin(0)
                .setMax(100)
                .setProgress(25)
                .setLeftEndText("")
                .setRightEndText("")
                .clearPadding(true)
                .apply();
    }

    private void setCitiesSpinner() {
        try {
            InputStream is = getAssets().open("countriesToCities.json");
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            String json = new String(buffer,"UTF-8");
            obj = new JSONObject(json);
            array = obj.getJSONArray(binding.countrySpinner.getSelectedItem().toString());
            cities = util.convertToList(array);
            Collections.sort(cities,String.CASE_INSENSITIVE_ORDER);
            citiesAdapter = new ArrayAdapter(this,R.layout.tv_bg,cities);
            binding.citySpinner.setAdapter(citiesAdapter);
            if (prefs.getname().equals("None")){
                binding.citySpinner.setSelection(citiesAdapter.getPosition("Vijayawada"));
            } else {
                binding.citySpinner.setSelection(citiesAdapter.getPosition(loggedUser.getCity_self()));
            }
        } catch (IOException e) {

        } catch (JSONException e) {

        }
    }

    private void setCountrySpinner() {
        Locale[] locale = Locale.getAvailableLocales();
        for (Locale loc : locale){
            country = loc.getDisplayCountry();
            if (country.length() > 0 && !countries.contains(country)){
                countries.add(country);
            }
        }

        Collections.sort(countries,String.CASE_INSENSITIVE_ORDER);

        ArrayAdapter<String> countriesAdapter = new ArrayAdapter<String>(this,R.layout.tv_bg,countries);
        binding.countrySpinner.setAdapter(countriesAdapter);
        if (prefs.getname().equals("None")){
            binding.countrySpinner.setSelection(countriesAdapter.getPosition("India"));
        } else {
            binding.countrySpinner.setSelection(countriesAdapter.getPosition(loggedUser.getCountry_self()));
        }
    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
        try {

            array = obj.getJSONArray(binding.countrySpinner.getSelectedItem().toString());
            Collections.sort(cities,String.CASE_INSENSITIVE_ORDER);
            citiesAdapter = new ArrayAdapter(this,R.layout.tv_bg,cities);
            binding.citySpinner.setAdapter(citiesAdapter);
            binding.citySpinner.setSelection(0);
        } catch (JSONException e) {

        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.add_image:
                if (!(binding.seekBar.getProgress() >= 96)){
                    binding.seekBar.setProgress(binding.seekBar.getProgress() + 5);
                } else  {
                    binding.seekBar.setProgress(binding.seekBar.getMax());
                }
                break;
            case R.is.sub_image:
                if (!(binding.seekBar.getProgress() <= 4)){
                    binding.seekBar.setProgress(binding.seekBar.getProgress() - 5);
                } else {
                    binding.seekBar.setProgress(binding.seekBar.getMin());
                }
                break;
            case R.id.select_date_button :
                displayDatePicker();
                break;
            case R.id.search_button:
                //todo
                break;
        }

    }

    private void displayDatePicker() {
        new SpinnerDatePickerDialogBuilder()
                .context(this)
                .callback(ActivitySearch.this)
                .spinnerTheme(R.style.spinnerTheme)
                .maxDate(day,month,year+5)
                .minDate(day,month,year)
                .build()
                .show();

    }

    @Override
    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {

        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.DAY_OF_MONTH,dayOfMonth);
        calendar.set(Calendar.MONTH,monthOfYear);
        calendar.set(Calendar.YEAR,year);
        calendar.set(Calendar.HOUR_OF_DAY,0);
        calendar.set(Calendar.MINUTE,0);
        calendar.set(Calendar.SECOND,0);

        time = calendar.getTimeInMillis();



    }
}
