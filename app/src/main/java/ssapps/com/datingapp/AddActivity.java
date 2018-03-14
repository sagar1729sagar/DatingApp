package ssapps.com.datingapp;

import android.*;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.databinding.DataBindingUtil;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.ParcelFileDescriptor;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import com.backendless.Backendless;
import com.backendless.async.callback.AsyncCallback;
import com.backendless.exceptions.BackendlessFault;
import com.backendless.files.BackendlessFile;
import com.orm.SugarContext;
import com.tsongkha.spinnerdatepicker.DatePicker;
import com.tsongkha.spinnerdatepicker.DatePickerDialog;
import com.tsongkha.spinnerdatepicker.SpinnerDatePickerDialogBuilder;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.FileDescriptor;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.Locale;

import Models.Activity;
import Models.User;
import Util.Util;
import Util.Prefs;
import cn.pedant.SweetAlert.SweetAlertDialog;
import ssapps.com.datingapp.databinding.ActivityAddBinding;

public class AddActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener,View.OnClickListener, DatePickerDialog.OnDateSetListener {

    private ActivityAddBinding binding;
    private static final String appKey = "7EEB2727-4E8D-944C-FFDD-3D802BC37800";
    private static final String appId = "648D896E-EDD8-49C8-FF74-2F1C32DB7A00";
    private int day;
    private int month;
    private int year;
    private Util util;
    private Prefs prefs;
    private SweetAlertDialog dialog, error;
    private JSONObject obj;
    private JSONArray array;
    private ArrayList<String> cities = new ArrayList<>();
    private ArrayAdapter citiesAdapter;
    private User loggedUser;
    private String country;
    private ArrayList<String> countries = new ArrayList<>();
    private long time;
    private static final int SELECT_PICTURE = 1;
    private static final int READ_EXT_STORAGE = 2;
    private Bitmap bitmap;
    private boolean isDateSelected = false,firstPress = true;
    private String picName;



    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_add);
        Backendless.initApp(this, appId, appKey);
        SugarContext.init(this);
        util = new Util();
        prefs = new Prefs(this);

        dialog = new SweetAlertDialog(this, SweetAlertDialog.PROGRESS_TYPE);
        dialog.setCancelable(false);
        dialog.setTitle("Saving...");
        error = new SweetAlertDialog(this, SweetAlertDialog.ERROR_TYPE);

        Calendar c = Calendar.getInstance();
        day = c.get(Calendar.DAY_OF_MONTH);
        month = c.get(Calendar.MONTH);
        year = c.get(Calendar.YEAR);

        loggedUser = User.find(User.class, "username = ?", prefs.getname()).get(0);

        setCountrySpinner();
        setCitiesSpinner();

        binding.countrySpinner.setOnItemSelectedListener(this);
        binding.selectDateButton.setOnClickListener(this);
        binding.selctPictureButton.setOnClickListener(this);
        binding.submitButton.setOnClickListener(this);

    }


    private void setCitiesSpinner() {
        try {
            InputStream is = getAssets().open("countriesToCities.json");
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            String json = new String(buffer, "UTF-8");
            obj = new JSONObject(json);
            array = obj.getJSONArray(binding.countrySpinner.getSelectedItem().toString());
            cities = util.convertToList(array);
            Collections.sort(cities, String.CASE_INSENSITIVE_ORDER);
            citiesAdapter = new ArrayAdapter(this, R.layout.tv_bg, cities);
            binding.citySpinner.setAdapter(citiesAdapter);
            if (prefs.getname().equals("None")) {
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
        for (Locale loc : locale) {
            country = loc.getDisplayCountry();
            if (country.length() > 0 && !countries.contains(country)) {
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
    public void onClick(View view) {

        switch (view.getId()){
            case R.id.select_date_Button:
                selectDate();
                break;
            case R.id.selct_picture_button:
                selectImage();
                break;
            case R.id.submit_button:
                if (!util.checkEditTextField(binding.subjectEt)){
                    binding.subjectLayout.setError("Enter the type of actvity like picnic,datout,trek etc...");
                } else if (!util.checkEditTextField(binding.descriptionEt)){
                    binding.descriptionLayout.setError("Please enter a descriotn of the activity you are planning");
                } else if (!isDateSelected){
                    Toast.makeText(getApplicationContext(),"Please select a date",Toast.LENGTH_LONG).show();
                } else if (bitmap == null && firstPress){
                    firstPress = false;
                    Toast.makeText(getApplicationContext(),"You didnot select a picture.If you wish to continue without picture, please submit again",Toast.LENGTH_LONG).show();
                } else {
                    if (bitmap == null){
                        saveActivity(false);
                    } else {
                        savePicture();
                    }
                }
                break;
        }

    }

    private void savePicture() {
        dialog.setTitle("Uploading picture...");
        dialog.show();
        picName = "activities"+prefs.getname()+String.valueOf(day)+String .valueOf(month)+String.valueOf(year)+binding.subjectEt.getText().toString();
        Backendless.Files.Android.upload(bitmap, Bitmap.CompressFormat.PNG, 25, picName + ".png", "activities", true, new AsyncCallback<BackendlessFile>() {
            @Override
            public void handleResponse(BackendlessFile response) {
                Log.v("picture","saved");
                saveActivity(true);
            }

            @Override
            public void handleFault(BackendlessFault fault) {
                dialog.dismiss();
                error.setTitle("Error uploading picture");
                error.setContentText("The following error occured while uploading picture\n"+fault.getMessage()+"Please try again");
                error.show();
            }
        });
    }

    private void saveActivity(boolean hasPicture) {

        final Activity activity = new Activity();
        activity.setCity(binding.citySpinner.getSelectedItem().toString());
        activity.setCountry(binding.countrySpinner.getSelectedItem().toString());
        activity.setDateActivity(binding.dateDisplayTv.getText().toString());
        activity.setDescription(binding.descriptionEt.getText().toString());
        if (hasPicture){
            activity.setHasPicture("Yes");
            activity.setPictureUrl("https://api.backendless.com/"+appId+"/v1/files/activities/"+picName+".png");
        } else {
            activity.setHasPicture("No");
            activity.setPictureUrl("None");
        }
        activity.setSubject(binding.subjectEt.getText().toString());
        activity.setTime(time);
        activity.setUser(prefs.getname());

        dialog.setTitle("Saving...");
        Backendless.Data.save(activity, new AsyncCallback<Activity>() {
            @Override
            public void handleResponse(Activity response) {
                dialog.dismiss();
                activity.save();
                Toast.makeText(getApplicationContext(),"Activity saved successfully",Toast.LENGTH_LONG).show();
                finish();
            }

            @Override
            public void handleFault(BackendlessFault fault) {
                dialog.dismiss();
                error.setTitle("Error saving activity");
                error.setContentText("The following error has occured while saving the object\n"+fault.getMessage()+"\n Please try again");
                error.show();
            }
        });

    }

    private void selectImage() {
        int check = ContextCompat.checkSelfPermission(this, android.Manifest.permission.READ_EXTERNAL_STORAGE);
        if (check == -1){

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                requestPermissions(new String[]{android.Manifest.permission.READ_EXTERNAL_STORAGE},READ_EXT_STORAGE);
            }

        }else {

            Intent i = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            startActivityForResult(i, SELECT_PICTURE);
        }
    }

    private void selectDate() {
        new SpinnerDatePickerDialogBuilder()
                .context(this)
                .callback(this)
                .spinnerTheme(R.style.NumberPickerStyle)
                .maxDate(year+5,month,day)
                .minDate(year,month,day)
                .build()
                .show();
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
    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.DAY_OF_MONTH,dayOfMonth);
        calendar.set(Calendar.MONTH,monthOfYear);
        calendar.set(Calendar.YEAR,year);
        calendar.set(Calendar.HOUR_OF_DAY,0);
        calendar.set(Calendar.MINUTE,0);
        calendar.set(Calendar.SECOND,0);

        time = calendar.getTimeInMillis();

        binding.dateDisplayTv.setText(String.valueOf(dayOfMonth)+"/"+String.valueOf(monthOfYear+1)+"/"+String.valueOf(year));

        isDateSelected = true;

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
                    Toast.makeText(getApplicationContext(),"You need to give permission to access gallery for uploading picture",Toast.LENGTH_LONG).show();
                  //  setToast("You need to give permission to access gallery for uploading picture");
                    // Toast.makeText(getContext(),"You need to give permission to access gallery",Toast.LENGTH_LONG).show();

                }
                break;

        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case SELECT_PICTURE:
                if (resultCode == this.RESULT_OK && data != null && data.getData() != null) {
                    Uri image = data.getData();
                    String[] filePath = {MediaStore.Images.Media.DATA};
                    Cursor cursor = getContentResolver().query(image, filePath, null, null, null);
                    cursor.moveToFirst();
                    int columnIndex = cursor.getColumnIndex(filePath[0]);
                    cursor.close();
                    bitmap = getBitmapFromUri(image);
                    binding.selectedPicture.setImageURI(image);
                }
                break;
            }
        }

    private Bitmap getBitmapFromUri(Uri uri) {
        Bitmap image = null;
        try {
            ParcelFileDescriptor parcelFileDescriptor = getContentResolver().openFileDescriptor(uri,"r");
            FileDescriptor fileDescriptor = parcelFileDescriptor.getFileDescriptor();
             image = BitmapFactory.decodeFileDescriptor(fileDescriptor);
            parcelFileDescriptor.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return image;
    }
}

