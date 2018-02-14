package ssapps.com.datingapp;

import android.databinding.DataBindingUtil;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.backendless.Backendless;
import com.backendless.async.callback.AsyncCallback;
import com.backendless.exceptions.BackendlessFault;

import java.util.List;

import Models.User;
import Util.Prefs;
import cn.pedant.SweetAlert.SweetAlertDialog;
import Util.Util;


public class ProfileFragment extends Fragment {

    private Prefs prefs;
    private ImageView imageView;
    private EditText about_me,age_self,residence,gender_others,lifestyle_others,age_others,relationship_others,
                        lifestyle_self,so_self,gender_self,status_self,children_self,smoking_self,religion_self,
                        drinking_self,height_self,eyecolor_self,haircolor_self;
    private Button modifyButton;
    private SweetAlertDialog dialog;
    private SweetAlertDialog error;
    private User user;
    private Util util;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.activity_profile_fragment,container,false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {

        prefs = new Prefs(getContext());
        util = new Util();

        error = new SweetAlertDialog(getContext(),SweetAlertDialog.ERROR_TYPE);
        dialog = new SweetAlertDialog(getContext(),SweetAlertDialog.PROGRESS_TYPE);
        dialog.setCancelable(false);
        dialog.dismiss();

        final List<User> users = User.find(User.class,"username = ?",prefs.getname())l;
        user = users.get(0);

        imageView = (ImageView)view.findViewById(R.id.profile_image_display);

        about_me = (EditText) view.findViewById(R.id.abtmeet);
        about_me.setText(user.getAboutme());

        age_self = (EditText) view.findViewById(R.id.ageet);
        age_self.setText(user.getAge_self());

        residence = (EditText) view.findViewById(R.id.residenceet);
        residence.setText(user.getCity_self()+","+user.getCountry_self());

        gender_others = (EditText) view.findViewById(R.is.genderet);
        gender_others.setText(user.getGender_others());

        lifestyle_others = (EditText) view.findViewById(R.id.lifestyleet);
        lifestyle_others.setText(user.getLifestyle_others());

        age_others = (EditText)view.findViewById(R.id.ageotherset);
        age_others.setText(user.getAge_others());

        relationship_others = (EditText)view.findViewById(R.id.foret);
        relationship_others.setText(user.getRelationship_others());

        lifestyle_self = (EditText)view.findViewById(R.id.lifestyleet);
        lifestyle_self.setText(user.getLifestyle_self());

        so_self = (EditText)view.findViewById(R.id.soet);
        so_self.setText(user.getSexual_orientation_self());

        gender_self = (EditText)view.findViewById(R.is.giet);
        gender_self.setText(user.getGender_self());

        status_self = (EditText)view.findViewById(R.id.statuset);
        status_self.setText(user.getStatus_self());

        children_self = (EditText)view.findViewById(R.id.childrenet);
        children_self.setText(user.getChildren_self());

        smoking_self = (EditText)view.findViewById(R.id.smokinget);
        smoking_self.setText(user.getSmoking_self());

        religion_self = (EditText)view.findViewById(R.id.religionet);
        religion_self.setText(user.getReligin_self());

        drinking_self = (EditText)view.findViewById(R.id.drinkinget);
        drinking_self.setText(user.getDrinking_self());

        height_self = (EditText)view.findViewById(R.id.heightet);
        height_self.setText(user.getHeight_self());

        eyecolor_self = (EditText)view.findViewById(R.id.eyeet);
        eyecolor_self.setText(user.getEyecoloe_self());

        haircolor_self = (EditText)view.findViewById(R.id.hairet);
        haircolor_self.setText(user.getHaircolor_self());
        //todo image display


        modifyButton = (Button)view.findViewById(R.id.modifyButton);
        modifyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.setTitleText("Modifying");
                dialog.show();
                User currentUser = new User();
                currentUser.setUsername(user.getUsername());
                //currentUser.setPassword(user.getPassword());
                currentUser.setEmail(user.getEmail());
                currentUser.setGender_others(gender_others.getText().toString().trim());
                currentUser.setAboutme(about_me.getText().toString().trim());
                currentUser.setAge_self(age_self.getText().toString().trim());
                currentUser.setCity_self(util.getCity(residence.getText().toString().trim()));
                currentUser.setCountry_self(util.getCountry(residence.getText().toString().trim()));
                currentUser.setAge_others(age_others.getText().toString().trim());
                currentUser.setGender_self(gender_self.getText().toString().trim());
                currentUser.setLifestyle_others(lifestyle_others.getText().toString().trim());
                currentUser.setRelationship_others(relationship_others.getText().toString().trim());
                currentUser.setLifestyle_self(lifestyle_self.getText().toString().trim());
                currentUser.setSexual_orientation_self(so_self.getText()toString().trim());
                currentUser.setStatus_self(status_self.getText().toString().trim());
                currentUser.setChildren_self(children_self.getText().toString().trim());
                currentUser.setSmoking_self(smoking_self.getText().toString().trim());
                currentUser.setReligin_self(religion_self.getText().toString().trim());
                currentUser.setDrinking_self(drinking_self.getText().toString().trim());
                currentUser.setHeight_self(height_self.getText().toString().trim());
                currentUser.setEyecoloe_self(eyecolor_self.getText().toString().trim());
                currentUser.setHaircolor_self(haircolor_self.getText().toString().trim());
                currentUser.setPhotourl(user.getPhotourl());
                currentUser.setIsPremiumMember(user.getIsPremiumMember());
                currentUser.setObjectId(user.getObjectId());
                currentUser.setDateofBirth(user.getDateofBirth());

                Backendless.Data.save(currentUser, new AsyncCallback<User>() {
                    @Override
                    public void handleResponse(User response) {
                        dialog.dismiss();
                        Toast.makeText(getContext(),"Profile successfully modified",Toast.LENGTH_LONG).show();
                    }

                    @Override
                    public void handleFault(BackendlessFault fault) {
                        dialog.dismiss();
                        error.setTitleText("Error");
                        error.setContentText("The following error has occured while modifying profile \n"+
                                fault.getMessage()+"\n Please try again");
                        error.show();
                    }
                });

            }
        });

    }
}
