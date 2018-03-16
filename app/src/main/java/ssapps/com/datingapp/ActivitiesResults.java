package ssapps.com.datingapp;

import android.databinding.DataBindingUtil;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import java.util.List;

import Adapters.SearchActivityAdapter;
import Models.SearchedActivities;
import ssapps.com.datingapp.databinding.ActivitiesResultsBinding;

public class ActivitiesResults extends AppCompatActivity {

    private ActivitiesResultsBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.activities_results);
        //binding = DataBindingUtil.setContentView(this,R.layout.activities_results);
        binding = DataBindingUtil.setContentView(this,R.layout.activities_results);

        ActionBar actionBar = getSupportActionBar();

        actionBar.setHomeButtonEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayShowHomeEnabled(true);

        List<SearchedActivities> activities = SearchedActivities.listAll(SearchedActivities.class);

        SearchActivityAdapter adapter = new SearchActivityAdapter(this,activities);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        binding.activitiesList.setLayoutManager(layoutManager);
        binding.activitiesList.setItemAnimator(new DefaultItemAnimator());
        binding.activitiesList.setAdapter(adapter);

    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
        this.finish();
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}
