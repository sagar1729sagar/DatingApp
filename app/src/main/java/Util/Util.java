package Util;

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

}