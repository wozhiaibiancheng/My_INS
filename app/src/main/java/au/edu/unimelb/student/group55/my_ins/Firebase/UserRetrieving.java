package au.edu.unimelb.student.group55.my_ins.Firebase;

public class UserRetrieving {
    private User user;
    private UserAccountSetting settings;

    public UserRetrieving(User user, UserAccountSetting settings) {
        this.user = user;
        this.settings = settings;
    }

    public UserRetrieving() {

    }

    public User getUser() {
        return user;
    }


    public void setUser(User user) {
        this.user = user;
    }

    public UserAccountSetting getSettings() {
        return settings;
    }



    public void setSettings(UserAccountSetting settings) {
        this.settings = settings;
    }


    @Override
    public String toString() {
        return "UserSettings{" +
                "user=" + user +
                ", settings=" + settings +
                '}';

    }
}
