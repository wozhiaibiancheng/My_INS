package au.edu.unimelb.student.group55.my_ins.Firebase;

public class UserAccountSetting {
    private String description;
    private String display_name;
    private long following;
    private long followers;
    private long posts;
    private String profile_pic;
    private String username;
    private long phone_number;

    public UserAccountSetting(String description, String display_name, long following, long followers, long posts, String profile_pic, String username,long phone_number) {
        this.description = description;
        this.display_name = display_name;
        this.following = following;
        this.followers = followers;
        this.posts = posts;
        this.profile_pic = profile_pic;
        this.username = username;
        this.phone_number = phone_number;

    }

    public UserAccountSetting(){
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getDisplay_name() {
        return display_name;
    }

    public void setDisplay_name(String display_name) {
        this.display_name = display_name;
    }

    public long getFollowing() {
        return following;
    }

    public void setFollowing(long following) {
        this.following = following;
    }

    public long getFollowers() {
        return followers;
    }

    public void setFollowers(long followers) {
        this.followers = followers;
    }

    public long getPosts() {
        return posts;
    }

    public void setPosts(long posts) {
        this.posts = posts;
    }

    public String getProfile_pic() {
        return profile_pic;
    }

    public void setProfile_pic(String profile_pic) {
        this.profile_pic = profile_pic;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public long getPhone_number() {
        return phone_number;
    }

    public void setPhone_number(long phone_number) {
        this.phone_number = phone_number;
    }
}
