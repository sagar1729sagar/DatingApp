package ssapps.com.datingapp;

import android.databinding.DataBindingUtil;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.backendless.Backendless;
import com.backendless.async.callback.AsyncCallback;
import com.backendless.exceptions.BackendlessFault;
import com.backendless.persistence.DataQueryBuilder;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.orm.SugarContext;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

import Adapters.FriendsListAdapter;
import Models.User;
import Util.Prefs;
import cn.pedant.SweetAlert.SweetAlertDialog;
import ssapps.com.datingapp.databinding.ActivityFavouritesListBinding;

public class LikedList extends Fragment {

    private ActivityFavouritesListBinding binding;
    private SweetAlertDialog dialog,error;
    private static final String appKey = "7EEB2727-4E8D-944C-FFDD-3D802BC37800";
    private static final String appId = "648D896E-EDD8-49C8-FF74-2F1C32DB7A00";
    private static final String GCM_SENDER_ID = "57050948456";
    private DatabaseReference mDatabase;
    private Prefs prefs;
    private ArrayList<String> likes = new ArrayList<>();
    private ArrayList<String> allLikes = new ArrayList<>();

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater,R.layout.activity_favourites_list,container,false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Backendless.initApp(getContext(),appId,appKey);
        SugarContext.init(getContext());

        dialog = new SweetAlertDialog(getContext(),SweetAlertDialog.PROGRESS_TYPE);
        error = new SweetAlertDialog(getContext(),SweetAlertDialog.ERROR_TYPE);

        prefs = new Prefs(getContext());

        dialog.setTitle("Accessing list");
        dialog.show();
        mDatabase = FirebaseDatabase.getInstance().getReference();
        getList();

    }

    private void getList() {


        mDatabase.child(prefs.getname()).child("Liked").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // dialog.dismiss();
                Log.v("response", String.valueOf(dataSnapshot.getValue()));
                HashMap<String,Long> map = (HashMap<String, Long>) dataSnapshot.getValue();
                Set<String> set = map.keySet();
                likes.clear();
                likes.addAll(set);
                allLikes.clear();
                allLikes.addAll(set);
                for (String like:likes){
                //    Log.v("friends",favourite);
                    // getUsers(friends);
                }
                getUsers(likes);

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                dialog.dismiss();
                error.setTitle("Error fetching friend list");
                error.setContentText(databaseError.getMessage()+"\n Please try again later");
                error.show();
            }
        });

    }


    private void getUsers(ArrayList<String> friendsInList) {
        String where = "";
        ArrayList<String> finalUSers = new ArrayList<>();
        // finalUSers = friends;
        for (String friend : friendsInList) {
            if (User.count(User.class, "username = ?", new String[]{friend}) > 0) {
                finalUSers.add(friend);
                Log.v("final users called for", friend);
            }
        }
        Log.v("friends", friendsInList.toString());
        friendsInList.removeAll(finalUSers);
        Log.v("friends", friendsInList.toString());

        if (friendsInList.size() != 0) {

            if (friendsInList.size() == 1) {
                where = "username = '" + friendsInList.get(0) + "'";
            } else {
                for (int i = 0; i < friendsInList.size(); i++) {
                    if (i == 0) {
                        where = "username in (" + friendsInList.get(i);
                    } else if (i == friendsInList.size() - 1) {
                        where = where + "," + friendsInList.get(i) + ")";
                    } else {
                        where = where + "," + friendsInList.get(i);
                    }
                }
            }

            Log.v("where caluse", where);

            DataQueryBuilder query = DataQueryBuilder.create();
            query.setPageSize(100);
            query.setWhereClause(where);

            pullData(query, true);
        } else {
            dialog.dismiss();
            generateList();
        }

    }


    private void pullData(final DataQueryBuilder query, final boolean isFirstIteration) {
        Backendless.Data.find(User.class, query, new AsyncCallback<List<User>>() {
            @Override
            public void handleResponse(List<User> response) {
                Log.v("response size", String.valueOf(response.size()));

                if (response.size() != 0){
                    for (User person:response){
                        person.setId(User.count(User.class)+1);
                        person.save();
                        query.prepareNextPage();
                        pullData(query,false);
                    }
                }  else {
                    dialog.dismiss();;
                    generateList();

                }
            }

            @Override
            public void handleFault(BackendlessFault fault) {
                dialog.dismiss();
                error.setTitle("Error");
                error.setContentText("The following error has occuredwhile getting user data\n"
                        +fault.getMessage()+"\n Please try again later");
                error.show();
            }
        });
    }



    private void generateList() {
//        String[] us;
//        for (String friend:friends){
//        us = friends.toArray(new String[0])
//        if (User.count(User.class) > 1) {
//            List<User> users = new ArrayList<>();
//            users.addAll(User.find(User.class,"username = ?",));
//            for (String friend:friends){
//               // users.addAll(User.find(User.class),"uusername = ?",friends);
//            }
        Log.v("friends size", String.valueOf(allLikes.size()));
        List<User> users = new ArrayList<>();
        for (String like:allLikes){
           // Log.v("friend",friend);
            users.add(User.find(User.class,"username = ?",like).get(0));
        }

        FriendsListAdapter adapter = new FriendsListAdapter(getContext(), users);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getContext());
        binding.favouritesList.setLayoutManager(layoutManager);
        binding.favouritesList.setItemAnimator(new DefaultItemAnimator());
        binding.favouritesList.setAdapter(adapter);
    }

}
