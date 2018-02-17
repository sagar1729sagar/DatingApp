package Models;

import com.orm.SugarRecord;

/**
 * Created by sagar on 17/02/18.
 */

public class SearchResults extends SugarRecord {

    private String username,password,email,gender_others,aboutme,age_self,city_self,country_self,age_others,gender_self,
            lifestyle_others,relationship_others,lifestyle_self,sexual_orientation_self,status_self,children_self,
            smoking_self,religin_self,drinking_self,height_self,units,eyecoloe_self,haircolor_self,photourl,latitude,
            longitude,isPremiumMember,objectId,dateofBirth,who_view_photos,friend_requests,who_view_friends,incognito_mode,
            packages,hasPicture,isOnline,videoUrl;

    public SearchResults(){

    }

    public SearchResults(User user){
        this.username = user.getUsername();
        this.email = user.getEmail();
        this.gender_others = user.getGender_others();
        this.aboutme = user.getAboutme();
        this.age_self = user.getAge_self();
        this.city_self = user.getCity_self();
        this.country_self = user.getCountry_self();
        this.age_others = user.getAge_others();
        this.gender_self = user.getGender_self();
        this.lifestyle_others = user.getLifestyle_others();
        this.relationship_others = user.getRelationship_others();
        this.lifestyle_self = user.getLifestyle_self();
        this.sexual_orientation_self = user.getSexual_orientation_self();
        this.status_self = user.getStatus_self();
        this.children_self = user.getChildren_self();
        this.smoking_self = user.getSmoking_self();
        this.religin_self = user.getReligin_self();
        this.drinking_self = user.getDrinking_self();
        this.height_self = user.getHeight_self();
        this.units = user.getUnits();
        this.eyecoloe_self = user.getEyecoloe_self();
        this.haircolor_self = user.getHaircolor_self();
        this.photourl = user.getPhotourl();
        this.latitude = user.getLatitude();
        this.longitude = user.getLongitude();
        this.isPremiumMember = user.getIsPremiumMember();
        this.objectId = user.getObjectId();
        this.dateofBirth = user.getDateofBirth();
        this.who_view_photos = user.getWho_view_photos();
        this.friend_requests = user.getFriend_requests();
        this.who_view_friends = user.getWho_view_friends();
        this.incognito_mode = user.getIncognito_mode();
        this.packages = user.getPackages();
        this.hasPicture = user.getHasPicture();
        this.isOnline = user.getIsOnline();
        this.videoUrl = user.getVideoUrl();
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public String getEmail() {
        return email;
    }

    public String getGender_others() {
        return gender_others;
    }

    public String getAboutme() {
        return aboutme;
    }

    public String getAge_self() {
        return age_self;
    }

    public String getCity_self() {
        return city_self;
    }

    public String getCountry_self() {
        return country_self;
    }

    public String getAge_others() {
        return age_others;
    }

    public String getGender_self() {
        return gender_self;
    }

    public String getLifestyle_others() {
        return lifestyle_others;
    }

    public String getRelationship_others() {
        return relationship_others;
    }

    public String getLifestyle_self() {
        return lifestyle_self;
    }

    public String getSexual_orientation_self() {
        return sexual_orientation_self;
    }

    public String getStatus_self() {
        return status_self;
    }

    public String getChildren_self() {
        return children_self;
    }

    public String getSmoking_self() {
        return smoking_self;
    }

    public String getReligin_self() {
        return religin_self;
    }

    public String getDrinking_self() {
        return drinking_self;
    }

    public String getHeight_self() {
        return height_self;
    }

    public String getUnits() {
        return units;
    }

    public String getEyecoloe_self() {
        return eyecoloe_self;
    }

    public String getHaircolor_self() {
        return haircolor_self;
    }

    public String getPhotourl() {
        return photourl;
    }

    public String getLatitude() {
        return latitude;
    }

    public String getLongitude() {
        return longitude;
    }

    public String getIsPremiumMember() {
        return isPremiumMember;
    }

    public String getObjectId() {
        return objectId;
    }

    public String getDateofBirth() {
        return dateofBirth;
    }

    public String getWho_view_photos() {
        return who_view_photos;
    }

    public String getFriend_requests() {
        return friend_requests;
    }

    public String getWho_view_friends() {
        return who_view_friends;
    }

    public String getIncognito_mode() {
        return incognito_mode;
    }

    public String getPackages() {
        return packages;
    }

    public String getHasPicture() {
        return hasPicture;
    }

    public String getIsOnline() {
        return isOnline;
    }

    public String getVideoUrl() {
        return videoUrl;
    }
}
