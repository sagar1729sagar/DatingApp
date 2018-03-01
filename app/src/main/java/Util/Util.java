package Util;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.widget.EditText;

import com.backendless.Backendless;
import com.backendless.async.callback.AsyncCallback;
import com.backendless.exceptions.BackendlessFault;

import org.json.JSONArray;
import org.json.JSONException;

import java.security.PrivilegedExceptionAction;
import java.util.ArrayList;

import Models.User;

public class Util {


    public Util(){

    }

    public float getScreenWidth(Context context){
        return context.getResources().getDisplayMetrics().widthPixels;
    }



    public boolean checkEditTextField(EditText editText){
        if (editText.getText().equals("")){
            return false;
        }
        if (editText.getText().toString().length() == 0){
            return false;
        }
        if (editText.getText().toString().isEmpty()){
            return false;
        }
        return true;
    }

    public boolean isNetworkAvailable(Context context) {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    public String getCity(String residence){
        int a = residence.indexOf(",");
        return residence.substring(0,a-1);
    }

    public String getCountry(String residence){
        int a = residence.indexOf(",");
        return residence.substring(a+1);
    }


    public ArrayList<String> convertToList(JSONArray array){
        ArrayList<String> list = new ArrayList<>();
        if (array != null){
            for (int i =0;i<array.length();i++){
                try {
                    list.add(array.getString(i));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
        return list;
    }

    public void updateOnlineStatus(Context context,Boolean isOnline) {
        Prefs prefs = new Prefs(context);
        if (!prefs.getname().equals("None")){
            User user = User.find(User.class,"username = ?",prefs.getname()).get(0);
            if (isOnline){
                if (user.getIsOnline().equals("No")){
                    user.setIsOnline("Yes");
                    chageUserstatus(user);
                }
            } else {
                if (!isOnline){
                    if (user.getIsOnline().equals("Yes")){
                        user.setIsOnline("No");
                        chageUserstatus(user);
                    }
                }
            }
//            if (isOnline && user.getIsOnline().equals("No")){
//                user.setIsOnline("Yes");
//                chageUserstatus(user);
//            } else if (!isOnline && user.getIsOnline().equals("Yes")){
//                user.setIsOnline("No");
//                chageUserstatus(user);
//            }
        }

    }

    private void chageUserstatus(final User user) {
        Backendless.Data.save(user, new AsyncCallback<User>() {
            @Override
            public void handleResponse(User response) {
                user.delete();
                response.save();
            }

            @Override
            public void handleFault(BackendlessFault fault) {

            }
        });

    }

}