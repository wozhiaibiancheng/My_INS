package au.edu.unimelb.student.group55.my_ins.Firebase;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.constraint.solver.widgets.Snapshot;
import android.util.Log;
import android.widget.Toast;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import au.edu.unimelb.student.group55.my_ins.R;


public class FirebaseMethods {

    private static final String TAG = "FirebaseMethods";
    private com.google.firebase.auth.FirebaseAuth myAuth;
    private com.google.firebase.auth.FirebaseAuth.AuthStateListener myAuthListener;
    private String userID;
    private Context myContext;
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference databaseReference;

    public FirebaseMethods(Context context) {
        myAuth = com.google.firebase.auth.FirebaseAuth.getInstance();
        myContext = context;
        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference();

        if (myAuth.getCurrentUser() != null) {
            userID = myAuth.getCurrentUser().getUid();
        }
    }

    public void registerNewEmail(final String email, String password, final String username) {
        myAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        Log.d(TAG, "createUserWithEmail:onComplete:" + task.isSuccessful());
                        // If sign in fails, display a message to the user. If sign in succeeds
                        // the auth state listener will be notified and logic to handle the
                        // signed in user can be handled in the listener.
                        if (!task.isSuccessful()) {
                            Toast.makeText(myContext, task.getException().getMessage(),
                                    Toast.LENGTH_LONG).show();
                        } else if (task.isSuccessful()) {
                            userID = myAuth.getCurrentUser().getUid();
                            addUser(email,username,"","",userID);

                            Log.d(TAG, "onComplete: Authstate changed: " + userID);
                        }
                    }
                });
    }


    public void addUser(String email, String username, String description, String profile_pic,String userID) {
        User user = new User(userID, username, 1, email);

        databaseReference.child("users").child(userID).setValue(user);

        UserAccountSetting userAccountSetting = new UserAccountSetting("", username, 0, 0, 0, profile_pic, username,0);

        databaseReference.child(myContext.getString(R.string.dbname_user_account_settings)).child(userID).setValue(userAccountSetting);
    }

//    public boolean usernameExists(String username, DataSnapshot dataSnapshot) {
//
//        User user = new User();
//        System.out.println("checking username");
////        System.out.println(dataSnapshot.child("users").getChildrenCount());
////        System.out.println(dataSnapshot.child(userID).getChildrenCount());
//        for (DataSnapshot data : dataSnapshot.child("users").getChildren()) {
//            Log.d(TAG, "check if username exists: " + data);
//            user.setUsername(data.getValue(User.class).getUsername());
//
//            if (user.getUsername().equals(username)) {
//                Log.d(TAG, "username existes");
//                return true;
//            }
//        }
//        return false;
//    }

    public UserAccountSetting getUserSetting(DataSnapshot dataSnapshot) {
        UserAccountSetting setting = new UserAccountSetting();

        for (DataSnapshot userInfo : dataSnapshot.getChildren()) {

            if (userInfo.getKey().equals(myContext.getString(R.string.dbname_user_account_settings))) {

                try {
                    setting.setUsername(userInfo.child(userID).getValue(UserAccountSetting.class).getUsername());
                    setting.setDescription(userInfo.child(userID).getValue(UserAccountSetting.class).getDescription());
                    setting.setDisplay_name(userInfo.child(userID).getValue(UserAccountSetting.class).getDisplay_name());
                    setting.setPosts(userInfo.child(userID).getValue(UserAccountSetting.class).getPosts());
                    setting.setFollowing(userInfo.child(userID).getValue(UserAccountSetting.class).getFollowing());
                    setting.setFollowers(userInfo.child(userID).getValue(UserAccountSetting.class).getFollowers());
                    setting.setProfile_pic(userInfo.child(userID).getValue(UserAccountSetting.class).getProfile_pic());
                    setting.setPhone_number(userInfo.child(userID).getValue(UserAccountSetting.class).getPhone_number());

                } catch (NullPointerException e) {
                    Log.d(TAG, "user account setting null pointer error: " + e.getMessage());
                }

            }
        }
        return setting;
    }

    public void updateUsername(String username){
        databaseReference.child("users").child(userID).child("username").setValue(username);
        databaseReference.child("user_account_settings").child(userID).child("username").setValue(username);
    }

    public void updateDisplayName(String displayName){
        databaseReference.child("users").child(userID).child("display_name").setValue(displayName);
        databaseReference.child("user_account_settings").child(userID).child("display_name").setValue(displayName);
    }

    public void updateDescription(String description){
        databaseReference.child("users").child(userID).child("description").setValue(description);
        databaseReference.child("user_account_settings").child(userID).child("description").setValue(description);
    }

    public void updatePhoneNum(long phoneNum){
        databaseReference.child("users").child(userID).child("phone_number").setValue(phoneNum);
        databaseReference.child("user_account_settings").child(userID).child("phone_number").setValue(phoneNum);
    }
}

