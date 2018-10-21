package au.edu.unimelb.student.group55.my_ins.Firebase;

import android.os.Parcel;
import android.os.Parcelable;
import java.util.List;

// All the information need for a post is in the photoInformation class
public class PhotoInformation implements Parcelable {

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

    public PhotoInformation(String postMessage, String dateCreated, String imageUrl, String photoID, String userID, String longitude, String latitude, List<Like> likes, List<Comment> comments) {
        this.postMessage = postMessage;
        this.dateCreated = dateCreated;
        this.imageUrl = imageUrl;
        this.photoID = photoID;
        this.userID = userID;
        this.longitude = longitude;
        this.latitude = latitude;
        this.likes = likes;
        this.comments = comments;
    }

    protected PhotoInformation(Parcel in) {
        postMessage = in.readString();
        dateCreated = in.readString();
        imageUrl = in.readString();
        photoID = in.readString();
        userID = in.readString();
        longitude = in.readString();
        latitude = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString( postMessage );
        dest.writeString( dateCreated );
        dest.writeString( imageUrl );
        dest.writeString( photoID );
        dest.writeString( userID );
        dest.writeString( longitude );
        dest.writeString( latitude );
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<PhotoInformation> CREATOR = new Creator<PhotoInformation>() {
        @Override
        public PhotoInformation createFromParcel(Parcel in) {
            return new PhotoInformation( in );
        }

        @Override
        public PhotoInformation[] newArray(int size) {
            return new PhotoInformation[size];
        }
    };

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

    public List<Like> getLikes() {
        return likes;
    }

    public void setLikes(List<Like> likes) {
        this.likes = likes;
    }

    public List<Comment> getComments() {
        return comments;
    }

    public void setComments(List<Comment> comments) {
        this.comments = comments;
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
