package ssapps.com.datingapp;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;

import com.orm.SugarContext;

import java.util.ArrayList;
import java.util.List;

import Adapters.SearchResultsAdapter;
import Models.SearchResults;
import Models.User;
import Util.Prefs;
import ssapps.com.datingapp.databinding.ActivitySearchResultsDisplayBinding;

public class SearchResultsDisplayActivity extends AppCompatActivity {

    private List<SearchResults> results = new ArrayList<>();
    private SearchResultsAdapter adapter;
    private ActivitySearchResultsDisplayBinding binding;
    private Prefs prefs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.activity_search_results_display);
        binding = DataBindingUtil.setContentView(this,R.layout.activity_search_results_display);
        SugarContext.init(this);
        prefs = new Prefs(this);

        ActionBar actionBar = getSupportActionBar();

        actionBar.setHomeButtonEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayShowHomeEnabled(true);





        RecyclerViewClickListener listener = new RecyclerViewClickListener() {
            @Override
            public void onClick(View view, int position) {
                Log.v("click",results.get(position).getUsername());
                SearchResults clickedUser = results.get(position);
                Log.v("packages",clickedUser.getPackages());
                if (clickedUser.getIsPremiumMember().equals("Yes")){
                    if (clickedUser.getPackages().contains("InDepth") || clickedUser.getPackages().contains("FullMembership")){
                        Log.v("premium ","indepth");
                        if (!clickedUser.getVideoUrl().equals("None")){
                            //todo take to indepth profile
                        }
                    }
                } else {
                    //todo take to normal class
                    Log.v("no premium","normal");
                    Intent i = new Intent(SearchResultsDisplayActivity.this,NormalProfileDisplay.class);
                    i.putExtra("name",clickedUser.getUsername());
                    startActivity(i);
                }
//                Intent i = new Intent(SearchResultsDisplayActivity.this,SearchItemDetailsActivity.class);
//                i.putExtra("name",results.get(position).getUsername());
//               // startActivity(new Intent(SearchResultsDisplayActivity.this,SearchItemDetailsActivity.class));
//                startActivity(i);
            }
        };

        results = SearchResults.listAll(SearchResults.class);
        adapter = new SearchResultsAdapter(this,results,listener);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        binding.searchResultsRecyclerView.setLayoutManager(layoutManager);
        binding.searchResultsRecyclerView.setItemAnimator(new DefaultItemAnimator());
        binding.searchResultsRecyclerView.setAdapter(adapter);

    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    @Override
    public void onBackPressed() {
        if (prefs.isOnlineRedirect()){
            prefs.setOnlineRedirect(true);
            startActivity(new Intent(this,MainActivity.class));
        } else {
            this.finish();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //SugarContext.terminate();
    }


}
