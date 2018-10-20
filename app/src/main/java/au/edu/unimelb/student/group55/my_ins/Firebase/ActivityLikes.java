package au.edu.unimelb.student.group55.my_ins.Firebase;

public class ActivityLikes {

    private String dateLiked;
    private String likerID;
    private String posterID;
    private String postID;

    public ActivityLikes() {
    }

    public ActivityLikes(String dateLiked, String likerID, String posterID, String postID) {
        this.dateLiked = dateLiked;
        this.likerID = likerID;
        this.posterID = posterID;
        this.postID = postID;
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

    public String getPosterID() {
        return posterID;
    }

    public void setPosterID(String posterID) {
        this.posterID = posterID;
    }

    public String getPostID() {
        return postID;
    }

    public void setPostID(String postID) {
        this.postID = postID;
    }

    @Override
    public String toString() {
        return "ActivityLikes{" +
                "dateLiked='" + dateLiked + '\'' +
                ", likerID='" + likerID + '\'' +
                ", posterID=" + posterID + '\'' +
                ", postID=" + postID + '\'' +
                '}';
    }
}
