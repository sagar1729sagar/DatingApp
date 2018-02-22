package ssapps.com.datingapp;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import Util.Util;

import com.backendless.Backendless;
import com.backendless.DeviceRegistration;
import com.backendless.async.callback.AsyncCallback;
import com.backendless.exceptions.BackendlessFault;

import Models.User;
import Util.Prefs;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private Toolbar toolbar;
    private Util util;
    private Prefs prefs;
    private static final String GCM_SENDER_ID = "57050948456";
    private static final String appKey = "7EEB2727-4E8D-944C-FFDD-3D802BC37800";
    private static final String appId = "648D896E-EDD8-49C8-FF74-2F1C32DB7A00";

    @SuppressLint("RestrictedApi")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        Backendless.initApp(this,appId,appKey);

        util = new Util();
        util.updateOnlineStatus(this,true);

        prefs = new Prefs(this);

        if (!prefs.getname().equals("None")){
            checkForPushNotificationsRegistration();
        }
       // updateOnlineStatus();



       // toolbar.setSystemUiVisibility(View.INVISIBLE);

      //  getSupportActionBar().setDefaultDisplayHomeAsUpEnabled(false);

       // FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
//        fab.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
//                        .setAction("Action", null).show();
//            }
//        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);


        drawer.post(new Runnable() {
            @Override
            public void run() {
                if (getIntent().hasExtra("redirectProfile")){
                    if (getIntent().getBooleanExtra("redirectProfile",false)){
                        makeScreenTransition(new ProfileFragment());
                    }
                } else if (getIntent().hasExtra("SettingsRedirect")){
                    if (getIntent().getBooleanExtra("PackagesRedirect",false)){
                        makeScreenTransition(new UpgradePackages());
                    }
                } else if (getIntent().hasExtra("chatRedirect")){
                    if (getIntent().getBooleanExtra("chatRedirect",false)){
                        makeScreenTransition(new ChatListingFragment());
                    }

            }
        });





    }




    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();


        //noinspection SimplifiableIfStatement
//        if (id == R.id.action_settings) {
//            return true;
//        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();


        switch (id){
            case R.id.nav_search:
                makeScreenTransition(new SearchActivity());
                break;
            case R.id.nav_save_search:
                makeScreenTransition(new SavedSearchActivity());
                break;
            case R.id.nav_around_me:
                makeScreenTransition(new AroundMeActivity());
                break;
            case R.id.nav_chat_messages:
                makeScreenTransition(new ChatListingFragment());
                break;
            case R.id.nav_online:
                makeScreenTransition(new OnlineActivity());
                break;
            case R.id.nav_whos_new:
                makeScreenTransition(new WhosNewActivity());
                break;
            case R.id.nav_in_depth:
                break;
            case R.id.nav_activity_board:
                break;
            case R.id.nav_friends:
                break;
            case R.id.favourites:
                break;
            case R.id.nav_liked_me:
                break;
            case R.id.nav_my_profile:
                break;
            case R.id.nav_upgrade_packages:
                makeScreenTransition(new UpgradePackages());
                break;
            case R.id.nav_terms_of_use:
                break;
            case R.id.nav_settings:
                makeScreenTransition(new SettingsFragment());
                break;
            case R.id.nav_logout:
              //  toolbar.setTitle("Choose one");
              //  makeScreenTransition(new LoginRegisterChooser());
                startActivity(new Intent(MainActivity.this,SignInChooserActivity.class));
                break;
            default:
                break;
        }

//        if (id == R.id.nav_camera) {
//            // Handle the camera action
//        } else if (id == R.id.nav_gallery) {
//
//        } else if (id == R.id.nav_slideshow) {
//
//        } else if (id == R.id.nav_manage) {
//
//        } else if (id == R.id.nav_share) {
//
//        } else if (id == R.id.nav_send) {
//
//        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }



    private void makeScreenTransition(Fragment fragment) {
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.fragment_placeholder,fragment);
        ft.commit();
    }

    @Override
    protected void onStop() {
        super.onStop();
        util.updateOnlineStatus(this, false);
    }


    private void checkForPushNotificationsRegistration() {
        Backendless.Messaging.getRegistrations(new AsyncCallback<DeviceRegistration>() {
            @Override
            public void handleResponse(DeviceRegistration response) {
                if (!response.getChannels().contains(prefs.getname())){
                    registerForPushNotification();
                }
            }

            @Override
            public void handleFault(BackendlessFault fault) {
                //do nothing
            }
        });
    }

    private void registerForPushNotification() {
        Backendless.Messaging.registerDevice(GCM_SENDER_ID, prefs.getname(), new AsyncCallback<Void>() {
            @Override
            public void handleResponse(Void response) {
                //do Nothing
            }

            @Override
            public void handleFault(BackendlessFault fault) {
                //do Nothing
            }
        });
    }


}
