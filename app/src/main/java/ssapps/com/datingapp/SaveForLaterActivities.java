package ssapps.com.datingapp;

import android.databinding.DataBindingUtil;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;

import com.orm.SugarContext;

import java.util.ArrayList;
import java.util.List;

import Adapters.SaveForLaterAdapter;
import Models.SavedActivities;
import Models.SearchedActivities;
import ssapps.com.datingapp.databinding.ActivitySaveForLaterActivitiesBinding;

public class SaveForLaterActivities extends AppCompatActivity {

    private  ActivitySaveForLaterActivitiesBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this,R.layout.activity_save_for_later_activities);
        SugarContext.init(this);

        Log.v("count", String.valueOf(SavedActivities.count(SavedActivities.class)));
        if (SavedActivities.count(SavedActivities.class) > 0){
            List<SavedActivities> acivities = new ArrayList<>();
            acivities = SavedActivities.listAll(SavedActivities.class);
            SaveForLaterAdapter adapter = new SaveForLaterAdapter(this,acivities);

            RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
            binding.saveForLaterList.setLayoutManager(layoutManager);
            binding.saveForLaterList.setItemAnimator(new DefaultItemAnimator());
            binding.saveForLaterList.setAdapter(adapter);
        }
    }
}
