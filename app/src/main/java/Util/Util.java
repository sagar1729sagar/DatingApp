package Util;

import android.content.Context;
import android.content.res.Resources;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.DisplayMetrics;
import android.widget.EditText;

import com.backendless.Backendless;
import com.backendless.async.callback.AsyncCallback;
import com.backendless.exceptions.BackendlessFault;

import org.json.JSONArray;
import org.json.JSONException;

import java.security.PrivilegedExceptionAction;
import java.util.ArrayList;
import java.util.Calendar;

import Models.User;

public class Util {


    public Util(){

    }

    public float getScreenWidth(Context context){
        return context.getResources().getDisplayMetrics().widthPixels;
    }
    public float getScreenHeight(Context context){
        return context.getResources().getDisplayMetrics().heightPixels;
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

    public static String getTime(long time){

        StringBuilder timeText = new StringBuilder(200);

        Calendar calendar = Calendar.getInstance();
        Calendar now = Calendar.getInstance();

        calendar.setTimeInMillis(time);

        if(calendar.get(Calendar.DAY_OF_MONTH) == now.get(Calendar.DAY_OF_MONTH)
                && calendar.get(Calendar.MONTH) == now.get(Calendar.MONTH)
                && calendar.get(Calendar.YEAR) == now.get(Calendar.YEAR)){
            timeText.append("TODAY  ");
        } else if (calendar.get(Calendar.DAY_OF_MONTH) == now.get(Calendar.DAY_OF_MONTH)-1
                && calendar.get(Calendar.MONTH) == now.get(Calendar.MONTH)
                && calendar.get(Calendar.YEAR) == now.get(Calendar.YEAR)){
            timeText.append("YESTERDAY  ");
        }else {
            timeText.append(String.valueOf(calendar.get(Calendar.DAY_OF_MONTH))+"/"
                    + String.valueOf(calendar.get(Calendar.MONTH) + 1)+"/"
                    +String.valueOf(calendar.get(Calendar.YEAR))+"  ");
        }

        if (calendar.get(Calendar.HOUR_OF_DAY) >= 12){

            timeText.append(String.valueOf(calendar.get(Calendar.HOUR_OF_DAY)-12)+":"+String.valueOf(calendar.get(Calendar.MINUTE))+" PM");

        }else{
            timeText.append(String.valueOf(calendar.get(Calendar.HOUR_OF_DAY))+":"+String.valueOf(calendar.get(Calendar.MINUTE))+" AM");
        }

        return timeText.toString();

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


    public float convertDpToPixel(float dp, Context context){
        Resources resources = context.getResources();
        DisplayMetrics metrics = resources.getDisplayMetrics();
        float px = dp * ((float)metrics.densityDpi / DisplayMetrics.DENSITY_DEFAULT);
        return px;
    }

    public String extractYouTubeURL(String url){

        int stasrtIndex = url.indexOf("?v=");
       // return String.valueOf(stasrtIndex);
        return url.substring(stasrtIndex+3);
    }

}