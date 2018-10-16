package au.edu.unimelb.student.group55.my_ins.Firebase;

public class PhotoInformation {

    private String postMessage;
    private String dateCreated;
    private String imageUrl;
    private String photoID;
    private String userID;
    private String longitute;
    private String latitude;


    public PhotoInformation(){}

    public PhotoInformation(String postMessage, String dateCreated, String imageUrl, String photoID, String userID, String longitute, String latitude) {
        this.postMessage = postMessage;
        this.dateCreated = dateCreated;
        this.imageUrl = imageUrl;
        this.photoID = photoID;
        this.userID = userID;
        this.longitute = longitute;
        this.latitude = latitude;
    }

    public String getPostMessage() {
        return postMessage;
    }

    public void setPostMessage(String postMessage) {
        this.postMessage = postMessage;
    }

    public String getDateCreated() {
        return dateCreated;
    }

    public void setDateCreated(String dateCreated) {
        this.dateCreated = dateCreated;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getPhotoID() {
        return photoID;
    }

    public void setPhotoID(String photoID) {
        this.photoID = photoID;
    }

    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    public String getLongitute() {
        return longitute;
    }

    public void setLongitute(String longitute) {
        this.longitute = longitute;
    }

    public String getLatitude() {
        return latitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    @Override
    public String toString(){
        return "Photo{" +
                "postMessage = '" + postMessage + '\'' +
                ", dateCreated = '" + dateCreated + '\'' +
                ", imageUrl = '" + imageUrl + '\'' +
                ", photoID = '" + photoID + '\'' +
                ", userID = '" + userID + '\'' +
                "longitude = '" + longitute + '\'' +
                "latitude = '" + latitude + '\'' +
                '}';
    }
}
