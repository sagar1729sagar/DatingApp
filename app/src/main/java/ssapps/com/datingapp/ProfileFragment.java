package ssapps.com.datingapp;

import android.databinding.DataBindingUtil;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import Models.User;


public class ProfileFragment extends Fragment{

    private ImageView imageView;
    private TextView about_me,age_others,;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.activity_profile_fragment,container,false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {



        imageView = (ImageView)view.findViewById(R.id.profile_image_display);

        about_me = (TextView)view.findViewById(R.id.abtmeet);

        //todo image display

    }
}
