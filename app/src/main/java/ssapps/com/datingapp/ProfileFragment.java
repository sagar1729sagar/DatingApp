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

import java.util.List;

import Models.User;
import Util.Prefs;


public class ProfileFragment extends Fragment {

    private Prefs prefs;
    private ImageView imageView;
    private EditText about_me,age_self,residence,gender_others,lifestyle_others,age_others,relationship_others,
                        lifestyle_self,so_self,gender_self,status_self,children_self,smoking_self,religion_self,
                        drinking_self,height_self,eyecolor_self,haircolor_self;
    private Button modifyButton;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.activity_profile_fragment,container,false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {

        prefs = new Prefs(getContext());
        List<User> users = User.find(User.class,"username = ?",prefs.getname())l;
        User user = users.get(0);

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
                //todo
            }
        });

    }
}
