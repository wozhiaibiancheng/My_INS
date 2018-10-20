package au.edu.unimelb.student.group55.my_ins.Firebase;

public class ActivityFollowing {

    private String followerID;
    private String followID;
    private String dateToFollow;

    public ActivityFollowing(){

    }

    public ActivityFollowing(String followerID, String followID, String dateToFollow) {
        this.followerID = followerID;
        this.followID = followID;
        this.dateToFollow = dateToFollow;
    }

    public String getFollowerID() {
        return followerID;
    }

    public void setFollowerID(String followerID) {
        this.followerID = followerID;
    }

    public String getFollowID() {
        return followID;
    }

    public void setFollowID(String followID) {
        this.followID = followID;
    }

    public String getDateToFollow() {
        return dateToFollow;
    }

    public void setDateToFollow(String dateToFollow) {
        this.dateToFollow = dateToFollow;
    }

    @Override
    public String toString() {
        return "ActivityFollowing{" +
                "dateToFollow='" + dateToFollow + '\'' +
                ", followerID='" + followerID + '\'' +
                ", followID=" + followID + '\'' +
                '}';
    }


}
