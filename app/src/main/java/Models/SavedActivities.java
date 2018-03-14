package Models;

import com.orm.SugarRecord;

/**
 * Created by sagar on 14/03/18.
 */

public class SavedActivities extends SugarRecord {

    private String subject,dateActivity,postedTime,city,country,description,user,hasPicture,pictureUrl,objectId;
    private Long time;

    public SavedActivities(){

    }

    public SavedActivities(Activity activity){
        this.subject = activity.getSubject();
        this.dateActivity = activity.getDateActivity();
        this.postedTime = activity.getPostedTime();
        this.city = activity.getCity();
        this.country = activity.getCountry();
        this.description = activity.getDescription();
        this.user = activity.getUser();
        this.hasPicture = activity.getHasPicture();
        this.pictureUrl = activity.getPictureUrl();
        this.objectId = activity.getObjectId();
        this.time = activity.getTime();
    }

    public String getSubject() {
        return subject;
    }

    public String getDateActivity() {
        return dateActivity;
    }

    public String getPostedTime() {
        return postedTime;
    }

    public String getCity() {
        return city;
    }

    public String getCountry() {
        return country;
    }

    public String getDescription() {
        return description;
    }

    public String getUser() {
        return user;
    }

    public String getHasPicture() {
        return hasPicture;
    }

    public String getPictureUrl() {
        return pictureUrl;
    }

    public String getObjectId() {
        return objectId;
    }

    public Long getTime() {
        return time;
    }
}
