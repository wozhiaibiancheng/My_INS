package au.edu.unimelb.student.group55.my_ins.Firebase;

public class ActivityLikes {

    private String dateLiked;
    private String likerID;
    private String postID;
    private String imageUrl;

    public ActivityLikes() {
    }

    public ActivityLikes(String dateLiked, String likerID, String postID, String imageUrl) {
        this.dateLiked = dateLiked;
        this.likerID = likerID;
        this.postID = postID;
        this.imageUrl = imageUrl;
    }

    public String getDateLiked() {
        return dateLiked;
    }

    public void setDateLiked(String dateLiked) {
        this.dateLiked = dateLiked;
    }

    public String getLikerID() {
        return likerID;
    }

    public void setLikerID(String likerID) {
        this.likerID = likerID;
    }

    public String getPostID() {
        return postID;
    }

    public void setPostID(String posterID) {
        this.postID = posterID;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    @Override
    public String toString() {
        return "ActivityLikes{" +
                "dateLiked='" + dateLiked + '\'' +
                ", likerID='" + likerID + '\'' +
                ", posterID=" + postID + '\'' +
                ", imageUrl=" + imageUrl + '\'' +
                '}';
    }
}
