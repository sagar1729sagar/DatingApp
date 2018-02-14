package Util;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.widget.EditText;

public class Util {


    public Util(){

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


}