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
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import Util.Util;

import com.backendless.Backendless;
import com.backendless.DeviceRegistration;
import com.backendless.async.callback.AsyncCallback;
import com.backendless.exceptions.BackendlessFault;
import com.github.siyamed.shapeimageview.RoundedImageView;
import com.orm.SugarContext;
import com.squareup.picasso.Picasso;

import Models.User;
import Util.Prefs;
import cn.pedant.SweetAlert.SweetAlertDialog;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private Toolbar toolbar;
    private Util util;
    private Prefs prefs;
    private static final String GCM_SENDER_ID = "57050948456";
    private static final String appKey = "7EEB2727-4E8D-944C-FFDD-3D802BC37800";
    private static final String appId = "648D896E-EDD8-49C8-FF74-2F1C32DB7A00";
    private SweetAlertDialog dialog,error;
    private ImageView fillImage;
    private RoundedImageView halfImage;
    private TextView userName,email;


    @SuppressLint("RestrictedApi")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        dialog = new SweetAlertDialog(this,SweetAlertDialog.PROGRESS_TYPE);
        dialog.setCancelable(false);
        error = new SweetAlertDialog(this,SweetAlertDialog.ERROR_TYPE);

        Backendless.initApp(this, appId, appKey);
        SugarContext.init(this);

        util = new Util();
        util.updateOnlineStatus(this, true);

        prefs = new Prefs(this);



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

        final DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        final NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);



        View view = navigationView.getHeaderView(0);


        fillImage = (ImageView)view.findViewById(R.id.nav_draw_full_image);
        halfImage = (RoundedImageView)view.findViewById(R.id.nav_draw_small_Image);
        userName = (TextView)view.findViewById(R.id.nav_draw_user_name);
        email = (TextView)view.findViewById(R.id.nav_draw_email);


        if (!prefs.getname().equals("None")) {
            checkForPushNotificationsRegistration();
            User loggedUser = new User();
            loggedUser = User.find(User.class,"username = ?",prefs.getname()).get(0);
            if (loggedUser.getHasPicture() == null){
                halfImage.setVisibility(View.GONE);
                userName.setText(loggedUser.getUsername());
                email.setText(loggedUser.getMailId());
            } else if (loggedUser.getHasPicture().equals("No")){
                halfImage.setVisibility(View.GONE);
                userName.setText(loggedUser.getUsername());
                email.setText(loggedUser.getMailId());
            } else {
                Picasso.with(getApplicationContext()).load(loggedUser.getPhotourl()).into(fillImage);

           //     Picasso.with(this).load(loggedUser.getPhotourl()).into(halfImage);
//                Picasso.with(this).load(loggedUser.getPhotourl()).into(halfImage);
         //       userName.setText(loggedUser.getUsername());
           //     email.setText(loggedUser.getMailId());

                halfImage.setVisibility(View.GONE);
                userName.setVisibility(View.GONE);
                email.setVisibility(View.GONE);
            }
        } else {
            navigationView.getMenu().findItem(R.id.nav_save_search).setVisible(false);
            navigationView.getMenu().findItem(R.id.nav_whos_new).setVisible(false);
            navigationView.getMenu().findItem(R.id.nav_in_depth).setVisible(false);
            navigationView.getMenu().findItem(R.id.nav_activity_board).setVisible(false);
            navigationView.getMenu().findItem(R.id.nav_settings).setVisible(false);
            halfImage.setVisibility(View.GONE);
            userName.setVisibility(View.GONE);
            email.setVisibility(View.GONE);
        }







        drawer.post(new Runnable() {
            @Override
            public void run() {

//                if (item.getItemId() == R.id.nav_activity_board){
//                    prefs.setActivity_Redirect(true);
//                    supportInvalidateOptionsMenu();
//                } else {
//                    prefs.setActivity_Redirect(false);
//                    supportInvalidateOptionsMenu();
//                }

                prefs.setActivity_Redirect(false);
                supportInvalidateOptionsMenu();
                if (prefs.getname().equals("None")){
                    startActivity(new Intent(MainActivity.this, SignInChooserActivity.class));
                } else {
                    //   makeScreenTransition(new SearchActivity());
                    //makeScreenTransition(new InDepthActivity());
                    Log.v("Indepth redirecrt", String.valueOf(prefs.isIndeothRedirect()));
                    if (getIntent().hasExtra("redirectProfile")) {
                        if (getIntent().getBooleanExtra("redirectProfile", false)) {
                            getSupportActionBar().setTitle("My Profile");
                            makeScreenTransition(new ProfileFragment());
                        }
                    } else if (getIntent().hasExtra("SettingsRedirect")) {
                        if (getIntent().getBooleanExtra("PackagesRedirect", false)) {
                            getSupportActionBar().setTitle("Upgrades");
                            makeScreenTransition(new UpgradePackages());
                        }
                    } else if (getIntent().hasExtra("chatRedirect")) {
                        if (getIntent().getBooleanExtra("chatRedirect", false)) {
                            if (prefs.getname().equals("None")) {
                                Toast.makeText(getApplicationContext(), "Please login or regiter to continue", Toast.LENGTH_LONG).show();
                            } else {
                                getSupportActionBar().setTitle("Chat");
                                makeScreenTransition(new ChatListingFragment());
                            }
                        }
                    } else if (prefs.isIndeothRedirect()) {
                        prefs.setIndepthRedirect(false);
                        getSupportActionBar().setTitle("In depth");
                        makeScreenTransition(new InDepthActivity());
                    } else {
                        drawer.openDrawer(GravityCompat.START);
                       // getSupportActionBar().setTitle("Search");
                        //makeScreenTransition(new SearchActivityNew());
                    }

                }

//                }
            }
        });


        if (!prefs.getname().equals("None")){
            navigationView.getMenu().getItem(navigationView.getMenu().size()-1).setTitle("Logout");
        } else {
            navigationView.getMenu().getItem(navigationView.getMenu().size()-1).setTitle("Login");
        }


    }




    @Override
    public void onBackPressed() {
        DrawerLayout mdrawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (mdrawer.isDrawerOpen(GravityCompat.START)) {
            mdrawer.closeDrawer(GravityCompat.START);
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

        switch (id){
            case R.id.add_new:

                startActivity(new Intent(MainActivity.this,AddActivity.class));
                break;
            case R.id.refresh_activities:

                startActivity(new Intent(MainActivity.this,RefreshActivitiesActivity.class));
                break;
            case R.id.saved_activities:
                startActivity(new Intent(MainActivity.this,SaveForLaterActivities.class));


                break;
            case R.id.search_activities:

                startActivity(new Intent(MainActivity.this,ActivitySearch.class));
                break;
        }

//        if (id == R.id.add_new){
//            Log.v("add new","clicked");
//        } else
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

        if (item.getItemId() == R.id.nav_activity_board){
            prefs.setActivity_Redirect(true);
            supportInvalidateOptionsMenu();
        } else {
            prefs.setActivity_Redirect(false);
            supportInvalidateOptionsMenu();
        }

        switch (id){
            case R.id.nav_search:
                getSupportActionBar().setTitle("Search");
                makeScreenTransition(new SearchActivityNew());
               // makeScreenTransition(new SearchActivity());
                break;
            case R.id.nav_save_search:
                getSupportActionBar().setTitle("Saved search");
                makeScreenTransition(new SavedSearchActivity());
                break;
            case R.id.nav_around_me:
                getSupportActionBar().setTitle("Around me");
                makeScreenTransition(new AroundMeActivity());
                break;
            case R.id.nav_chat_messages:
                if (prefs.getname().equals("None")){
                    Toast.makeText(getApplicationContext(),"Please login or register to continue",Toast.LENGTH_LONG).show();
                } else {
                    getSupportActionBar().setTitle("Chat");
                    makeScreenTransition(new ChatListingFragment());
                }
                break;
            case R.id.nav_online:
                getSupportActionBar().setTitle("Online");
                makeScreenTransition(new OnlineActivity());
                break;
            case R.id.nav_whos_new:
                getSupportActionBar().setTitle("Who's new");
                makeScreenTransition(new WhosNewActivity());
                break;
            case R.id.nav_in_depth:
                getSupportActionBar().setTitle("Indepth");
                makeScreenTransition(new InDepthActivity());
                break;
            case R.id.nav_activity_board:
                getSupportActionBar().setTitle("Activity Board");
                makeScreenTransition(new ActivityBaord());
                break;
            case R.id.nav_friends:
                if (prefs.getname().equals("None")){
                    Toast.makeText(getApplicationContext(),"Please login or register to continue",Toast.LENGTH_LONG).show();
                } else {
                    getSupportActionBar().setTitle("Friends");
                    makeScreenTransition(new FriendsFragment());
                }
                break;
            case R.id.favourites:
                if (prefs.getname().equals("None")){
                    Toast.makeText(getApplicationContext(),"Please login or register to continue",Toast.LENGTH_LONG).show();
                } else {
                    getSupportActionBar().setTitle("Favourites");
                    makeScreenTransition(new FavouritesList());
                }
                break;
            case R.id.nav_liked_me:
                if (prefs.getname().equals("None")){
                    Toast.makeText(getApplicationContext(),"Please login or register to continue",Toast.LENGTH_LONG).show();
                } else {
                    getSupportActionBar().setTitle("I liked");
                    makeScreenTransition(new LikedList());
                }
                break;
            case R.id.nav_my_profile:
                if (prefs.getname().equals("None")){
                    Toast.makeText(getApplicationContext(),"Please login or register to continue",Toast.LENGTH_LONG).show();
                } else {
                    getSupportActionBar().setTitle("My Profile");
                    makeScreenTransition(new ProfileFragment());
                }
                break;
            case R.id.nav_upgrade_packages:
                getSupportActionBar().setTitle("Upgrades");
                makeScreenTransition(new UpgradePackages());
                break;
            case R.id.nav_terms_of_use:
                break;
            case R.id.nav_settings:
                getSupportActionBar().setTitle("Settings");
                makeScreenTransition(new SettingsFragment());
                break;
            case R.id.nav_logout:
              //  toolbar.setTitle("Choose one");
              //  makeScreenTransition(new LoginRegisterChooser());
                if (prefs.getname().equals("None")) {
                    startActivity(new Intent(MainActivity.this, SignInChooserActivity.class));
                } else {
                  performLogout();
                }
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

    private void performLogout() {

        dialog.setTitleText("Logging out");
        dialog.show();

        Backendless.UserService.logout(new AsyncCallback<Void>() {
            @Override
            public void handleResponse(Void response) {
                dialog.dismiss();
                prefs.setName("None");
                startActivity(new Intent(MainActivity.this,MainActivity.class));
            }

            @Override
            public void handleFault(BackendlessFault fault) {
                dialog.dismiss();
                error.setTitleText("Cannot logout");
                error.setContentText("Please check your internet connection an try again");
                error.show();
            }
        });

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
     //   SugarContext.terminate();
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

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {

        if (prefs.isActivityRedirect()){
            menu.findItem(R.id.add_new).setVisible(true);
            menu.findItem(R.id.refresh_activities).setVisible(true);
            menu.findItem(R.id.saved_activities).setVisible(true);
            menu.findItem(R.id.search_activities).setVisible(true);
        } else {
            menu.findItem(R.id.add_new).setVisible(false);
            menu.findItem(R.id.refresh_activities).setVisible(false);
            menu.findItem(R.id.saved_activities).setVisible(false);
            menu.findItem(R.id.search_activities).setVisible(false);
        }

        return super.onPrepareOptionsMenu(menu);
    }
}
