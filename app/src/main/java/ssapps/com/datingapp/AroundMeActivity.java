package ssapps.com.datingapp;


import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.databinding.DataBindingUtil;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.os.CountDownTimer;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SeekBar;
import android.widget.Toast;

import com.backendless.Backendless;
import com.backendless.async.callback.AsyncCallback;
import com.backendless.exceptions.BackendlessFault;
import com.backendless.persistence.DataQueryBuilder;
import com.squareup.picasso.Picasso;
import com.warkiz.widget.IndicatorSeekBarType;
import com.warkiz.widget.IndicatorType;
import com.warkiz.widget.TickType;

import java.util.ArrayList;
import java.util.List;

import Models.SearchResults;
import Models.User;
import Util.Prefs;
import cn.pedant.SweetAlert.SweetAlertDialog;
import ssapps.com.datingapp.databinding.ActivityAroundMeBinding;

public class AroundMeActivity extends Fragment implements View.OnClickListener, SeekBar.OnSeekBarChangeListener{

    private static final int ACCESS_FINE_LOCATION = 1;
    private static final int LOCATION_HARDWARE = 2;
    private ActivityAroundMeBinding binding;

    private static final String appKey = "7EEB2727-4E8D-944C-FFDD-3D802BC37800";
    private static final String appId = "648D896E-EDD8-49C8-FF74-2F1C32DB7A00";


    private Prefs prefs;
    private User currentUser;
    private CountDownTimer timer;
    private boolean isTimerRunning = false;
    private SweetAlertDialog confirm_dialog,progress_dialog,error;
    private boolean isFirst;
    private double[] location;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater,R.layout.activity_around_me,container,false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        Backendless.initApp(getContext(),appId,appKey);
            prefs = new Prefs(getContext());

            confirm_dialog = new SweetAlertDialog(getContext(),SweetAlertDialog.WARNING_TYPE);
            confirm_dialog.setTitleText("Confirm search?");
        confirm_dialog.setConfirmText("Yes");
        confirm_dialog.setCancelText("No");
            confirm_dialog.setCancelable(true);

            progress_dialog = new SweetAlertDialog(getContext(),SweetAlertDialog.PROGRESS_TYPE);
            progress_dialog.setTitleText("Searching...");
            progress_dialog.setCancelable(false);

            error = new SweetAlertDialog(getContext(),SweetAlertDialog.ERROR_TYPE);


        if (!prefs.getname().equals("None")){
            currentUser = User.find(User.class,"username = ?",prefs.getname()).get(0);

            Picasso.with(getContext()).load(currentUser.getPhotourl()).into(binding.profileImageRounded);

            binding.distanceSeekBar.getBuilder().setMax(100)
                    .setMin(0)
                    .setProgress(10)
                    .setSeekBarType(IndicatorSeekBarType.CONTINUOUS_TEXTS_ENDS)
                    .setLeftEndText("0")
                    .setRightEndText("100")
                    .setTickType(TickType.OVAL)
                    .setBackgroundTrackColor(getContext().getResources().getColor(R.color.leaf_green))
                    .setIndicatorType(IndicatorType.RECTANGLE_ROUNDED_CORNER)
                    .setIndicatorColor(getContext().getResources().getColor(R.color.leaf_green))
                    .apply();

            binding.addImage.setOnClickListener(this);
            binding.subImage.setOnClickListener(this);



        }

        setCountDownTimer();


        confirm_dialog.setCancelClickListener(new SweetAlertDialog.OnSweetClickListener() {
            @Override
            public void onClick(SweetAlertDialog sweetAlertDialog) {
                confirm_dialog.dismiss();
            }
        });

        confirm_dialog.setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
            @Override
            public void onClick(SweetAlertDialog sweetAlertDialog) {
                confirm_dialog.dismiss();
                progress_dialog.show();
                isFirst = true;
                if (checkForGeoPoint(currentUser)) {
                    initiateSearch(binding.distanceSeekBar.getProgress());
                } else {
                    AskForLocation();
                }
            }
        });



    }

    private void initiateSearch(final int progress) {

        final DataQueryBuilder queryBuilder = DataQueryBuilder.create();
        queryBuilder.setPageSize(100);

        pullData(queryBuilder);



    }

    private void pullData(final DataQueryBuilder queryBuilder) {
        Backendless.Data.find(User.class, queryBuilder, new AsyncCallback<List<User>>() {
            @Override
            public void handleResponse(List<User> response) {
                if (isFirst){
                    if (prefs.getname().equals("None")){
                        User.deleteAll(User.class);
                    } else {
                        User user = User.find(User.class,"username = ?",prefs.getname()).get(0);
                        User.deleteAll(User.class);
                        user.save();
                    }
                }
                if (response.size() != 0){
                    User.saveInTx(response);
                    queryBuilder.prepareNextPage();
                    pullData(queryBuilder);
                } else if (response.size() == 0){
                    sortData();
                }
            }

            @Override
            public void handleFault(BackendlessFault fault) {

                if (isFirst) {
                    progress_dialog.dismiss();
                    isFirst = false;
                    error.setTitleText("Error fetching profiles");
                } else {
                    sortData();
                }
            }
        });
    }

    private void sortData() {
        List<User> results = User.listAll(User.class);
        sortForIncognitoSetting(binding.distanceSeekBar.getProgress(),results);
    }

    private void sortForIncognitoSetting(int progress, List<User> results) {
        for (User result:results){
            if (result.getIncognito_mode().equals("Yes")){
                results.remove(result);
            }
        }

        sortForDistance(progress,results);
    }

    private void sortForDistance(int progress, List<User> results) {
        if (!prefs.getname().equals("None")){
            User user = User.find(User.class,"username = ?",prefs.getname()).get(0);
            if (checkForGeoPoint(user)){
                for (User result:results){
                    if (checkForGeoPoint(result)){
                        Float distance = calculateDistace(Double.parseDouble(user.getLatitude()),
                                Double.parseDouble(user.getLongitude()),Double.parseDouble(result.getLatitude()),
                                Double.parseDouble(result.getLongitude()));
                        if (distance > Float.parseFloat(String.valueOf(progress))){
                            results.remove(result);
                        }
                    } else {
                        results.remove(result);
                    }


                }
                saveResults(results);
            } else {
                progress_dialog.dismiss();
                AskForLocation();
            }
        }


    }

    private void saveResults(List<User> results) {
        if (SearchResults.count(SearchResults.class) != 0){
            SearchResults.deleteAll(SearchResults.class);
        }
        List<SearchResults> searchResults = new ArrayList<>();
        for (User result: results){
            searchResults.add(new SearchResults(result));
        }

        SearchResults.saveInTx(searchResults);

        startActivity(new Intent(getContext(),SearchResultsDisplayActivity.class));

    }

    private void AskForLocation() {

        final SweetAlertDialog ask = new SweetAlertDialog(getContext(),SweetAlertDialog.NORMAL_TYPE);
        ask.setTitleText("No location found for user "+prefs.getname());
        ask.setContentText("Shall we try and get your lcoation now?\nYou may need to give necessary permissions to locate you.\nYou cannot proceed without a valid location.");
        ask.setConfirmText("Locate me");
        ask.setCancelText("No");
        ask.setCancelable(false);
        ask.show();

        ask.setCancelClickListener(new SweetAlertDialog.OnSweetClickListener() {
            @Override
            public void onClick(SweetAlertDialog sweetAlertDialog) {
                ask.dismiss();
            }
        });

        ask.setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
            @Override
            public void onClick(SweetAlertDialog sweetAlertDialog) {
                    checkForFineLocationPermision();
            }
        });


    }

    private void checkForFineLocationPermision() {
        int check = ContextCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION);
        if (check == -1){
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION},ACCESS_FINE_LOCATION);
            }
        } else {
            checkForHardWarePermission();
        }
    }

    private void checkForHardWarePermission() {
        int check = ContextCompat.checkSelfPermission(getContext(), Manifest.permission.LOCATION_HARDWARE);
        if (check == -1){
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                requestPermissions(new String[]{Manifest.permission.LOCATION_HARDWARE},LOCATION_HARDWARE);
            }
        } else {
            getGPS();
        }

    }


    @SuppressLint("MissingPermission")
    private  void getGPS() {
        LocationManager lm = (LocationManager) getContext().getSystemService(Context.LOCATION_SERVICE);
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
        saveDetails();
    }

    private void saveDetails() {
        progress_dialog.show();
        final User user = User.find(User.class,"username = ?",prefs.getname()).get(0);
        user.setLatitude(String.valueOf(location[0]));
        user.setLongitude(String.valueOf(location[1]));

        Backendless.Data.save(user, new AsyncCallback<User>() {
            @Override
            public void handleResponse(User response) {
                user.delete();
                response.save();
                sortData();
            }

            @Override
            public void handleFault(BackendlessFault fault) {
                progress_dialog.dismiss();
                error.setTitleText("Cannot connect to VeMeet");
                error.setContentText("Error occured while fetching your location. Please try again");
                error.show();
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode){
            case ACCESS_FINE_LOCATION:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    checkForHardWarePermission();
                } else {
                    Toast.makeText(getContext(),"You need to give permission to access your location for others to find you",Toast.LENGTH_LONG).show();
                    checkForFineLocationPermision();
                }
                break;
            case LOCATION_HARDWARE:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED){
                    getGPS();
                } else {
                    Toast.makeText(getContext(),"You need to give permission to access your location for others to find you",Toast.LENGTH_LONG).show();
                    checkForHardWarePermission();
                }

        }
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

    private void setCountDownTimer() {
        timer = new CountDownTimer(3000,500) {
            @Override
            public void onTick(long l) {
                isTimerRunning = true;
            }

            @Override
            public void onFinish() {
                isTimerRunning = false;
                AskForSearch();
            }
        };

    }

    private void AskForSearch() {
        confirm_dialog.setContentText("Shall we initiate search for profiles within "+binding.distanceSeekBar.getProgress()+"miles?");
        confirm_dialog.show();

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.add_image:
                if (!(binding.distanceSeekBar.getProgress() >= 96)){
                    binding.distanceSeekBar.setProgress(binding.distanceSeekBar.getProgress() + 5);
                } else {
                    binding.distanceSeekBar.setProgress(binding.distanceSeekBar.getMax());
                }
//                if (isTimerRunning){
//                    timer.cancel();
//                    isTimerRunning = false;
//                }
//                setCountDownTimer();
//                timer.start();
                break;
            case R.id.sub_image:
                if (!(binding.distanceSeekBar.getProgress() <= 4)){
                    binding.distanceSeekBar.setProgress(binding.distanceSeekBar.getProgress() - 5);
//                    if (isTimerRunning){
//                        timer.cancel();
//                        isTimerRunning = false;
//                    }
//                    setCountDownTimer();
//                    timer.start();
                } else {
                    binding.distanceSeekBar.setProgress(binding.distanceSeekBar.getMin());
                }
                break;
        }
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
        if (isTimerRunning){
            timer.cancel();
            isTimerRunning = false;
        }
        if (seekBar.getProgress() != 0) {
            setCountDownTimer();
            timer.start();
        }
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {

    }
}
