package ssapps.com.datingapp;

import android.databinding.DataBindingUtil;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import java.util.List;

import Adapters.SearchResultsAdapter;
import Models.SearchResults;
import ssapps.com.datingapp.databinding.ActivitySearchResultsDisplayBinding;

public class SearchResultsDisplayActivity extends AppCompatActivity {

    private List<SearchResults> results;
    private SearchResultsAdapter adapter;
    private ActivitySearchResultsDisplayBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.activity_search_results_display);
        binding = DataBindingUtil.setContentView(this,R.layout.activity_search_results_display);

        ActionBar actionBar = getSupportActionBar();

        actionBar.setHomeButtonEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayShowHomeEnabled(true);

        results = SearchResults.listAll(SearchResults.class);
        adapter = new SearchResultsAdapter(this,results);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        binding.searchResultsRecyclerView.setLayoutManager(layoutManager);
        binding.searchResultsRecyclerView.setItemAnimator(new DefaultItemAnimator());
        binding.searchResultsRecyclerView.setAdapter(adapter);

        //todo row click to be done

    }
}
