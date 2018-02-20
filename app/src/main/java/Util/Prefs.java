package Util;

import android.content.Context;
import android.content.SharedPreferences;

public class Prefs {
    SharedPreferences prefs;
    SharedPreferences.Editor editor;
    Context context;

    int PVT_MODE = 0;
    private static final String PREF_NAME = "VeMeet";
    private static final String USERNAME = "Username";
    private static final String IS_IN_CHAT = "isInChat";
    public Prefs(Context context){
        this.context=context;
        prefs = context.getSharedPreferences(PREF_NAME,PVT_MODE);
        editor = prefs.edit();
    }


    public void setName(String name){
        editor.putString(USERNAME,name);
        editor.commit();
    }
    public String getname(){
        return prefs.getString(USERNAME,"None");
    }

    public void setIsInChat(boolean status){
        editor.putBoolean(IS_IN_CHAT,status);
        editor.commit();
    }

    public boolean isInChat(){
        return prefs.getBoolean(IS_IN_CHAT,false);
    }
  
}