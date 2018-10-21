package au.edu.unimelb.student.group55.my_ins.Firebase;

import android.os.Parcel;
import android.os.Parcelable;

public class UserAccountSetting implements Parcelable {
    private String description;
    private String display_name;
    private long following;
    private long followers;
    private long posts;
    private String profile_pic;
    private String username;
    private long phone_number;
    private String user_id;

    public UserAccountSetting(String description, String display_name, long following, long followers, long posts, String profile_pic, String username,long phone_number, String user_id) {
        this.description = description;
        this.display_name = display_name;
        this.following = following;
        this.followers = followers;
        this.posts = posts;
        this.profile_pic = profile_pic;
        this.username = username;
        this.phone_number = phone_number;
        this.user_id = user_id;

    }

    public UserAccountSetting(){
    }

    protected UserAccountSetting(Parcel in) {
        description = in.readString();
        display_name = in.readString();
        following = in.readLong();
        followers = in.readLong();
        posts = in.readLong();
        profile_pic = in.readString();
        username = in.readString();
        phone_number = in.readLong();
        user_id = in.readString();
    }

    public static final Creator<UserAccountSetting> CREATOR = new Creator<UserAccountSetting>() {
        @Override
        public UserAccountSetting createFromParcel(Parcel in) {
            return new UserAccountSetting( in );
        }

        @Override
        public UserAccountSetting[] newArray(int size) {
            return new UserAccountSetting[size];
        }
    };

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

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    @Override
    public String toString() {
        return "UserAccountSetting{" +
                "description='" + description + '\'' +
                ", display_name='" + display_name + '\'' +
                ", followers=" + followers +
                ", following=" + following +
                ", posts=" + posts +
                ", profile_picture='" + profile_pic + '\'' +
                ", username='" + username + '\'' +
                ", phone_number='" + phone_number + '\'' +
                '}';
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString( description );
        dest.writeString( display_name );
        dest.writeLong( following );
        dest.writeLong( followers );
        dest.writeLong( posts );
        dest.writeString( profile_pic );
        dest.writeString( username );
        dest.writeLong( phone_number );
        dest.writeString( user_id );
    }
}
