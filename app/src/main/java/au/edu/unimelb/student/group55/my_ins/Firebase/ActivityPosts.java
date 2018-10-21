package au.edu.unimelb.student.group55.my_ins.Firebase;


// The post event record in Feed activity
public class ActivityPosts {

    private String userID;
    private String imageUrl;
    private String dateCreated;

    public ActivityPosts() {
    }

    public ActivityPosts(String userID, String imageUrl, String dateCreated) {
        this.userID = userID;
        this.imageUrl = imageUrl;
        this.dateCreated = dateCreated;
    }

    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getDateCreated() {
        return dateCreated;
    }

    public void setDateCreated(String dateCreated) {
        this.dateCreated = dateCreated;
    }

    @Override
    public String toString() {
        return "ActivityPosts{" +
                "userID='" + userID + '\'' +
                ", imageUrl='" + imageUrl + '\'' +
                ", dateCreated=" + dateCreated + '\'' +
                '}';
    }
}
