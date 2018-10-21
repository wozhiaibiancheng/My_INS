package au.edu.unimelb.student.group55.my_ins.Firebase;


// The likes event record in Feed activity
public class ActivityLikes {

    private String dateLiked;
    private String likerID;
    private String posterID;
    private String imageUrl;

    public ActivityLikes() {
    }

    public ActivityLikes(String dateLiked, String likerID, String posterID, String imageUrl) {
        this.dateLiked = dateLiked;
        this.likerID = likerID;
        this.posterID = posterID;
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

    public String getPosterID() {
        return posterID;
    }

    public void setPosterID(String posterID) {
        this.posterID = posterID;
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
                ", posterID=" + posterID + '\'' +
                ", imageUrl=" + imageUrl + '\'' +
                '}';
    }
}
