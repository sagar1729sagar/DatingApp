package ssapps.com.datingapp;

import android.annotation.SuppressLint;
import android.content.Context;
import android.databinding.DataBindingUtil;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.backendless.Backendless;
import com.backendless.async.callback.AsyncCallback;
import com.backendless.exceptions.BackendlessFault;
import com.jaredrummler.materialspinner.MaterialSpinner;
import com.orm.SugarContext;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import Models.User;
import Util.Prefs;
import Util.Util;
import cn.pedant.SweetAlert.SweetAlertDialog;
import ssapps.com.datingapp.databinding.ActivitySettingsFragmentBinding;

@SuppressLint("NewApi")
public class SettingsFragment extends Fragment implements EditText.OnEditorActionListener {

    private static final String appKey = "7EEB2727-4E8D-944C-FFDD-3D802BC37800";
    private static final String appId = "648D896E-EDD8-49C8-FF74-2F1C32DB7A00";
  //  private ActivitySettingsFragmentBinding binding;
    private ActivitySettingsFragmentBinding binding;
    private Prefs prefs;
    private Util util;
    private List<String> photo_spinner_items = new ArrayList<>(),friend_spinner_items = new ArrayList<>(),
                    friend_view_spinner_items = new ArrayList<>(),incognito_spinner_items = new ArrayList<>();
    private SweetAlertDialog dialog,error;
//    private EditText userName,password,confirmPassword,email;
//    private MaterialSpinner view_photos,friend_request,view_friends,incognito;
//    private Button submit;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        //  return inflater.inflate(R.layout.activity_settings_fragment,container,false);
       // binding = DataBindingUtil.inflate(inflater, R.layout.activity_settings_fragment, container, false);
       // return binding.getRoot();
        binding = DataBindingUtil.inflate(inflater,R.layout.activity_settings_fragment,container,false);
        return binding.getRoot();
    }

        @Override
        public void onViewCreated (View view, @Nullable Bundle savedInstanceState){

            Backendless.initApp(getContext(), appId, appKey);
            SugarContext.init(getContext());
            final Util util = new Util();
            photo_spinner_items = Arrays.asList("Only me", "Only friends", "All");
            friend_spinner_items = Arrays.asList("All", "None");
            friend_view_spinner_items = Arrays.asList("All", "Friends", "None");
            incognito_spinner_items = Arrays.asList("Yes", "No");

            error = new SweetAlertDialog(getContext(),SweetAlertDialog.ERROR_TYPE);
            dialog = new SweetAlertDialog(getContext(),SweetAlertDialog.PROGRESS_TYPE);
            dialog.setCancelable(false);
            dialog.setTitleText("Saving...");
            dialog.dismiss();


//        userName = (TextView)view.findViewById(R.id.user_name_et);
//        password = (TextView)view.findViewById(R.id.password_et);
            prefs = new Prefs(getContext());
            final User currentUser = User.find(User.class, "username = ?", prefs.getname()).get(0);
            Log.v("user",currentUser.getMailId());
            binding.userNameEt.setText(currentUser.getUsername());

            binding.emailEt.setText(currentUser.getMailId());


            ArrayAdapter<String> photosAdapter = new ArrayAdapter<String>(getContext(),R.layout.tv_bg,photo_spinner_items);
            binding.photosSpinner.setAdapter(photosAdapter);
            binding.photosSpinner.setSelection(photosAdapter.getPosition(currentUser.getWho_view_photos()));
//            binding.photosSpinner.setItems(photo_spinner_items);
//            switch (currentUser.getWho_view_photos()) {
//
//                case "Only me":
//                    binding.photosSpinner.setSelectedIndex(0);
//                    break;
//                case "Only friends":
//                    binding.photosSpinner.setSelectedIndex(1);
//                    break;
//                case "All":
//                    binding.photosSpinner.setSelectedIndex(2);
//                    break;
//                default:
//                    binding.photosSpinner.setSelectedIndex(2);
//                    break;
//            }


            ArrayAdapter<String> friendsAdapter = new ArrayAdapter<String>(getContext(),R.layout.tv_bg,friend_spinner_items);
            binding.friendSpinner.setAdapter(friendsAdapter);
            binding.friendSpinner.setSelection(friendsAdapter.getPosition(currentUser.getFriend_requests()));
//            binding.friendSpinner.setItems(friend_spinner_items);
//            switch (currentUser.getFriend_requests()) {
//                case "All":
//                    binding.friendSpinner.setSelectedIndex(0);
//                    break;
//                case "None":
//                    binding.friendSpinner.setSelectedIndex(1);
//                    break;
//                default:
//                    binding.friendSpinner.setSelectedIndex(0);
//                    break;
//            }


            ArrayAdapter<String> firendViewAdapter = new ArrayAdapter<String>(getContext(),R.layout.tv_bg,friend_view_spinner_items);
            binding.friendViewSpinner.setAdapter(firendViewAdapter);
            binding.friendViewSpinner.setSelection(firendViewAdapter.getPosition(currentUser.getWho_view_friends()));
//            binding.friendViewSpinner.setItems(friend_view_spinner_items);
//            switch (currentUser.getWho_view_friends()) {
//                case "All":
//                    binding.friendViewSpinner.setSelectedIndex(0);
//                    break;
//                case "Friends":
//                    binding.friendViewSpinner.setSelectedIndex(1);
//                    break;
//                case "None":
//                    binding.friendViewSpinner.setSelectedIndex(2);
//                    break;
//                default:
//                    binding.friendViewSpinner.setSelectedIndex(0);
//                    break;
//            }


            ArrayAdapter<String> incognitoAdapter = new ArrayAdapter<String>(getContext(),R.layout.tv_bg,incognito_spinner_items);
            binding.incognitoSpinner.setAdapter(incognitoAdapter);
            binding.incognitoSpinner.setSelection(incognitoAdapter.getPosition(currentUser.getIncognito_mode()));
//            binding.incognitoSpinner.setItems(incognito_spinner_items);
//            switch (currentUser.getIncognito_mode()) {
//                case "Yes":
//                    binding.incognitoSpinner.setSelectedIndex(0);
//                    break;
//                case "No":
//                    binding.incognitoSpinner.setSelectedIndex(1);
//                    break;
//                default:
//                    binding.incognitoSpinner.setSelectedIndex(1);
//                    break;
//            }


            binding.submitButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (util.checkEditTextField(binding.passwordEt) && util.checkEditTextField(binding.confirmPasswordEt)
                            && binding.passwordEt.getText().toString().equals(binding.confirmPasswordEt.getText().toString())
                            && isValidPassword(binding.passwordEt.getText().toString())) {

                        currentUser.setPassword(binding.passwordEt.getText().toString());

                    }

                      //  currentUser.setWho_view_photos(photo_spinner_items.get(binding.photosSpinner.getSelectedIndex()));
                        currentUser.setWho_view_photos(binding.photosSpinner.getSelectedItem().toString());
                      //  currentUser.setFriend_requests(friend_spinner_items.get(binding.friendSpinner.getSelectedIndex()));
                        currentUser.setFriend_requests(binding.friendSpinner.getSelectedItem().toString());
                       // currentUser.setWho_view_friends(friend_view_spinner_items.get(binding.friendViewSpinner.getSelectedIndex()));
                        currentUser.setWho_view_friends(binding.friendViewSpinner.getSelectedItem().toString());
                       // currentUser.setIncognito_mode(incognito_spinner_items.get(binding.incognitoSpinner.getSelectedIndex()));
                        currentUser.setIncognito_mode(binding.incognitoSpinner.getSelectedItem().toString());

                        dialog.show();
                        Backendless.Data.save(currentUser, new AsyncCallback<User>() {
                            @Override
                            public void handleResponse(User response) {
                            dialog.dismiss();
                            currentUser.delete();
                            response.save();
                                Toast.makeText(getContext(),"Settings saced successfully",Toast.LENGTH_LONG).show();
                            }

                            @Override
                            public void handleFault(BackendlessFault fault) {
                                dialog.dismiss();
                                error.setTitleText("Error saving your settings");
                                error.setContentText("The following error has occured while connecting to VeMeet"
                                +"\n"+fault.getMessage()+"\n Please try again");
                                error.show();
                                                            }
                        });

                }
            });


        }



    public  boolean isValidPassword(final String password) {

        Pattern pattern;
        Matcher matcher;
        final String PASSWORD_PATTERN = "^(?=.*[0-9])(?=.*[A-Z])(?=.*[@#$%^&+=!])(?=\\S+$).{4,}$";
        pattern = Pattern.compile(PASSWORD_PATTERN);
        matcher = pattern.matcher(password);

        return matcher.matches();

    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        SugarContext.terminate();
    }

    @Override
    public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
//        InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
//        if (imm != null) {
//            imm.hideSoftInputFromWindow(binding.countiresEtAuto.getWindowToken(), 0);
//        }
        InputMethodManager imm = (InputMethodManager)getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm != null){
            imm.hideSoftInputFromWindow(binding.userNameEt.getWindowToken(),0);
        }
        return true;
    }
}

