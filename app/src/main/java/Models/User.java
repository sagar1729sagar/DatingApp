package Models;

import com.orm.SugarRecord;

public class User extends SugarRecord {

    
    private String username,password,email,gender_others,aboutme,age_self,city_self,country_self,age_others,gender_self,
                    lifestyle_others,relationship_others,lifestyle_self,sexual_orientation_self,status_self,children_self,
                    smoking_self,religin_self,drinking_self,height_self,units,eyecoloe_self,haircolor_self,photourl,latitude,
                    longitude,isPremiumMember;

    public User(){

    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getGender_others() {
        return gender_others;
    }

    public void setGender_others(String gender_others) {
        this.gender_others = gender_others;
    }

    public String getAboutme() {
        return aboutme;
    }

    public void setAboutme(String aboutme) {
        this.aboutme = aboutme;
    }

    public String getAge_self() {
        return age_self;
    }

    public void setAge_self(String age_self) {
        this.age_self = age_self;
    }

    public String getCity_self() {
        return city_self;
    }

    public void setCity_self(String city_self) {
        this.city_self = city_self;
    }

    public String getCountry_self() {
        return country_self;
    }

    public void setCountry_self(String country_self) {
        this.country_self = country_self;
    }

    public String getAge_others() {
        return age_others;
    }

    public void setAge_others(String age_others) {
        this.age_others = age_others;
    }

    public String getGender_self() {
        return gender_self;
    }

    public void setGender_self(String gender_self) {
        this.gender_self = gender_self;
    }

    public String getLifestyle_others() {
        return lifestyle_others;
    }

    public void setLifestyle_others(String lifestyle_others) {
        this.lifestyle_others = lifestyle_others;
    }

    public String getRelationship_others() {
        return relationship_others;
    }

    public void setRelationship_others(String relationship_others) {
        this.relationship_others = relationship_others;
    }

    public String getLifestyle_self() {
        return lifestyle_self;
    }

    public void setLifestyle_self(String lifestyle_self) {
        this.lifestyle_self = lifestyle_self;
    }

    public String getSexual_orientation_self() {
        return sexual_orientation_self;
    }

    public void setSexual_orientation_self(String sexual_orientation_self) {
        this.sexual_orientation_self = sexual_orientation_self;
    }

    public String getStatus_self() {
        return status_self;
    }

    public void setStatus_self(String status_self) {
        this.status_self = status_self;
    }

    public String getChildren_self() {
        return children_self;
    }

    public void setChildren_self(String children_self) {
        this.children_self = children_self;
    }

    public String getSmoking_self() {
        return smoking_self;
    }

    public void setSmoking_self(String smoking_self) {
        this.smoking_self = smoking_self;
    }

    public String getReligin_self() {
        return religin_self;
    }

    public void setReligin_self(String religin_self) {
        this.religin_self = religin_self;
    }

    public String getDrinking_self() {
        return drinking_self;
    }

    public void setDrinking_self(String drinking_self) {
        this.drinking_self = drinking_self;
    }

    public String getHeight_self() {
        return height_self;
    }

    public void setHeight_self(String height_self) {
        this.height_self = height_self;
    }

    public String getUnits() {
        return units;
    }

    public void setUnits(String units) {
        this.units = units;
    }

    public String getEyecoloe_self() {
        return eyecoloe_self;
    }

    public void setEyecoloe_self(String eyecoloe_self) {
        this.eyecoloe_self = eyecoloe_self;
    }

    public String getHaircolor_self() {
        return haircolor_self;
    }

    public void setHaircolor_self(String haircolor_self) {
        this.haircolor_self = haircolor_self;
    }

    public String getPhotourl() {
        return photourl;
    }

    public void setPhotourl(String photourl) {
        this.photourl = photourl;
    }

    public String getLatitude() {
        return latitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public String getLongitude() {
        return longitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    public String getIsPremiumMember() {
        return isPremiumMember;
    }

    public void setIsPremiumMember(String isPremiumMember) {
        this.isPremiumMember = isPremiumMember;
    }
}
