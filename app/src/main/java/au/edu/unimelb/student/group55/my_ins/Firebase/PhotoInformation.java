package au.edu.unimelb.student.group55.my_ins.Firebase;

import org.w3c.dom.Comment;

import java.util.List;

public class PhotoInformation {

    private String postMessage;
    private String dateCreated;
    private String imageUrl;
    private String photoID;
    private String userID;
    private String longitude;
    private String latitude;
    private List<Like> likes;
    private List<Comment> comments;


    public PhotoInformation(){}

    public PhotoInformation(String postMessage, String dateCreated, String imageUrl, String photoID, String userID, String longitude, String latitude) {
        this.postMessage = postMessage;
        this.dateCreated = dateCreated;
        this.imageUrl = imageUrl;
        this.photoID = photoID;
        this.userID = userID;
        this.longitude = longitude;
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

    public String getLongitude() {
        return longitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
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
                "longitude = '" + longitude + '\'' +
                "latitude = '" + latitude + '\'' +
                '}';
    }
}
