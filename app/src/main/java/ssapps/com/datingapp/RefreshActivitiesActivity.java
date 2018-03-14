package ssapps.com.datingapp;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Toast;

import com.backendless.Backendless;
import com.backendless.async.callback.AsyncCallback;
import com.backendless.exceptions.BackendlessFault;
import com.backendless.persistence.DataQueryBuilder;

import java.util.ArrayList;
import java.util.List;

import Models.Activity;
import Util.Prefs;
import cn.pedant.SweetAlert.SweetAlertDialog;

public class RefreshActivitiesActivity extends AppCompatActivity {

    private SweetAlertDialog dialog;
    private Prefs prefs;
    private List<Activity> activities = new ArrayList<>();
    private boolean isFIrstIteration;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_refresh_activities);


        dialog = new SweetAlertDialog(this,SweetAlertDialog.PROGRESS_TYPE);
        dialog.setTitle("Refreshing...");
        dialog.setCancelable(false);
      //  error = new SweetAlertDialog(this,SweetAlertDialog.ERROR_TYPE);
        prefs = new Prefs(this);

        getData();

    }

    private void getData() {

        dialog.show();

        isFIrstIteration = true;

        DataQueryBuilder queryBuilder = DataQueryBuilder.create();
        queryBuilder.setPageSize(100);
        queryBuilder.setWhereClause("user = '"+prefs.getname()+"'");

        pullData(queryBuilder);

    }

    private void pullData(final DataQueryBuilder queryBuilder) {

        Backendless.Data.find(Activity.class, queryBuilder, new AsyncCallback<List<Activity>>() {
            @Override
            public void handleResponse(List<Activity> response) {
                dialog.dismiss();
                if (isFIrstIteration){
                    activities = Activity.listAll(Activity.class);
                    Activity.deleteAll(Activity.class);
                    isFIrstIteration = false;
                }
                if (response.size() != 0){
                    Activity.saveInTx(response);
                    queryBuilder.prepareNextPage();
                    pullData(queryBuilder);
                } else {
                    finish();
                }
            }

            @Override
            public void handleFault(BackendlessFault fault) {
                dialog.dismiss();
                if (!isFIrstIteration){
                    Activity.deleteAll(Activity.class);
                    Activity.saveInTx(activities);
                }
                Toast.makeText(getApplicationContext(),"Error : "+fault.getMessage(),Toast.LENGTH_LONG).show();
                finish();
//                error.setTitle("Error refreshing data");
//                error.setContentText("The following error occured while refreshing data\n"+fault.getMessage()+"\n Please try again");
//                error.but
//                error.show();
            }
        });
    }
}
