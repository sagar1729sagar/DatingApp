package Models;

import com.orm.SugarRecord;

public class SavedSearch extends SugarRecord {

    private String gender,who_are,lifestyle,Status,country,city,looking_for,children,min_age,max_age,miles,drinking,religion,height_min,height_max,eryecolor,haircolor,smoking;
    private boolean onlyOnline,onlyWithPic,WhosNew,incognitoSearch;
    private long saved_time;

    public SavedSearch(){

    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getWho_are() {
        return who_are;
    }

    public void setWho_are(String who_are) {
        this.who_are = who_are;
    }

    public String getLifestyle() {
        return lifestyle;
    }

    public void setLifestyle(String lifestyle) {
        this.lifestyle = lifestyle;
    }

    public String getStatus() {
        return Status;
    }

    public void setStatus(String status) {
        Status = status;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getLooking_for() {
        return looking_for;
    }

    public void setLooking_for(String looking_for) {
        this.looking_for = looking_for;
    }

    public String getChildren() {
        return children;
    }

    public void setChildren(String children) {
        this.children = children;
    }

    public String getMin_age() {
        return min_age;
    }

    public void setMin_age(String min_age) {
        this.min_age = min_age;
    }

    public String getMax_age() {
        return max_age;
    }

    public void setMax_age(String max_age) {
        this.max_age = max_age;
    }

    public String getMiles() {
        return miles;
    }

    public void setMiles(String miles) {
        this.miles = miles;
    }

    public String getDrinking() {
        return drinking;
    }

    public void setDrinking(String drinking) {
        this.drinking = drinking;
    }

    public String getReligion() {
        return religion;
    }

    public void setReligion(String religion) {
        this.religion = religion;
    }

//    public String getHeigh() {
//        return heigh;
//    }
//
//    public void setHeigh(String heigh) {
//        this.heigh = heigh;
//    }


    public String getHeight_min() {
        return height_min;
    }

    public void setHeight_min(String height_min) {
        this.height_min = height_min;
    }

    public String getHeight_max() {
        return height_max;
    }

    public void setHeight_max(String height_max) {
        this.height_max = height_max;
    }

    public String getEryecolor() {
        return eryecolor;
    }

    public void setEryecolor(String eryecolor) {
        this.eryecolor = eryecolor;
    }

    public String getHaircolor() {
        return haircolor;
    }

    public void setHaircolor(String haircolor) {
        this.haircolor = haircolor;
    }

    public boolean isOnlyOnline() {
        return onlyOnline;
    }

    public void setOnlyOnline(boolean onlyOnline) {
        this.onlyOnline = onlyOnline;
    }

    public boolean isOnlyWithPic() {
        return onlyWithPic;
    }

    public void setOnlyWithPic(boolean onlyWithPic) {
        this.onlyWithPic = onlyWithPic;
    }

    public boolean isWhosNew() {
        return WhosNew;
    }

    public void setWhosNew(boolean whosNew) {
        WhosNew = whosNew;
    }

    public boolean isIncognitoSearch() {
        return incognitoSearch;
    }

    public void setIncognitoSearch(boolean incognitoSearch) {
        this.incognitoSearch = incognitoSearch;
    }

    public String getSmoking() {
        return smoking;
    }

    public void setSmoking(String smoking) {
        this.smoking = smoking;
    }

    public long getSaved_time() {
        return saved_time;
    }

    public void setSaved_time(long saved_time) {
        this.saved_time = saved_time;
    }
}