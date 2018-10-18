package au.edu.unimelb.student.group55.my_ins.Profile;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.ittianyu.bottomnavigationviewex.BottomNavigationViewEx;

import java.util.ArrayList;

import au.edu.unimelb.student.group55.my_ins.Firebase.FirebaseMethods;
import au.edu.unimelb.student.group55.my_ins.Firebase.User;
import au.edu.unimelb.student.group55.my_ins.Firebase.UserAccountSetting;
import au.edu.unimelb.student.group55.my_ins.LoginNRegister.LoginActivity;
import au.edu.unimelb.student.group55.my_ins.R;
import au.edu.unimelb.student.group55.my_ins.SupportFunctions.BottomNavTool;
import au.edu.unimelb.student.group55.my_ins.SupportFunctions.ImageAdapter;
import au.edu.unimelb.student.group55.my_ins.SupportFunctions.UniversalImageLoader;
import de.hdodenhof.circleimageview.CircleImageView;

public class ViewProfileActivity extends AppCompatActivity {
    private static final String TAG = "viewProfile Activity";

    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference databaseReference;
    private FirebaseMethods firebaseMethods;
    private FirebaseAuth auth;
    private FirebaseAuth.AuthStateListener authListener;

    private Context context = ViewProfileActivity.this;
    private static final int numColumns = 3;

    //widgets
    private TextView posts, followers, following, displayName, username, description, follow, unfollow;
    private CircleImageView profile_pic;
    private GridView gridView;
    private Toolbar toolbar;
    private ImageView profileMenu;
    private BottomNavigationViewEx bottomNavigationViewEx;

    private StorageReference storageReference;
    //  private FirebaseUser user;

    private Task<Uri> downloadUri;
    private String downloadLink;

    private ImageView left_icon;
    private GridView imgGrid;

//target user
    private UserAccountSetting tUserAccountSettings;
    private User tUser;
    private long tFollowerNum;
    private long tPostNum = 0;

//    current user
    private FirebaseUser cUser;
    private String cUID;
    private long cFollowingNum;
    private UserAccountSetting cUserAccountSettings;



    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView( R.layout.activity_view_profile);

        Log.d("INFO", "onCreate started!");


        displayName = (TextView) findViewById( R.id.display_name);
        username = (TextView) findViewById( R.id.username);
        description = (TextView) findViewById( R.id.description);
        profile_pic = (CircleImageView) findViewById( R.id.profile_pic);
        posts = (TextView)findViewById( R.id.posts);
        followers = (TextView) findViewById( R.id.followers);
        following = (TextView) findViewById( R.id.following);
//        progressBar = (ProgressBar) findViewById(R.id.profileProgressBar);
        follow = (TextView) findViewById( R.id.text_follow);
        unfollow = (TextView) findViewById( R.id.text_unfollow);

        gridView = (GridView) findViewById( R.id.image_grid);
        toolbar = (Toolbar) findViewById( R.id.profileToolBar);


        left_icon = (ImageView) findViewById(R.id.left_icon);

        profile_pic = (CircleImageView)findViewById( R.id.profile_pic);

        imgGrid = (GridView)findViewById( R.id.image_grid);
        bottomNavigationViewEx = (BottomNavigationViewEx) findViewById( R.id.bottom);

        storageReference = FirebaseStorage.getInstance().getReference();

        FirebaseAuth();
        tUser = getUser();
        checkFollowing();


        //This need to be changed later
        temGridSetup();

//        setUpToolbar();
        setBottom();





//        setUpGrid();

        follow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                tFollowerNum  = getTfollowerNum();
                System.out.println("before click follow, followers: " + tFollowerNum);

//                cFollowingNum = getCfollowingNum();

                System.out.println("following before er/ing: " + tFollowerNum + "; " + cFollowingNum);

                cFollowingNum += 1;

                tFollowerNum  += 1;

                followers.setText(Long.toString(tFollowerNum));

                System.out.println("following mid er/ing: " + tFollowerNum + "; " + cFollowingNum);

                //             update followers and following table
                FirebaseDatabase.getInstance().getReference()
                        .child("following")
                        .child(cUID)
                        .child(tUser.getUser_id())
                        .child("user_id")
                        .setValue(tUser.getUser_id());


                FirebaseDatabase.getInstance().getReference()
                        .child("followers")
                        .child(tUser.getUser_id())
                        .child(cUID)
                        .child("user_id")
                        .setValue(FirebaseAuth.getInstance().getCurrentUser().getUid());



                //update follower and following number in user_account_settings
                FirebaseDatabase.getInstance().getReference()
                        .child("user_account_settings")
                        .child(tUser.getUser_id())
                        .child("followers")
                        .setValue(tFollowerNum);

                FirebaseDatabase.getInstance().getReference()
                        .child("user_account_settings")
                        .child(cUID)
                        .child("following")
                        .setValue(cFollowingNum);



                System.out.println("following after er/ing: " + tFollowerNum + "; " + cFollowingNum);

                setFollow();
                tFollowerNum  = getTfollowerNum();
                cFollowingNum = getCfollowingNum();
            }
        });

        unfollow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                tFollowerNum  = getTfollowerNum();
                System.out.println("before click Unfollow, followers: " + tFollowerNum);

//                cFollowingNum = getCfollowingNum();

                System.out.println("unfolow before er/ing: " + tFollowerNum + "; " + cFollowingNum);
                cFollowingNum -= 1;


                tFollowerNum -= 1;
                System.out.println("unfollow mid er/ing: " + tFollowerNum + "; " + cFollowingNum);
                followers.setText(Long.toString(tFollowerNum));

//                update followers and following table
                FirebaseDatabase.getInstance().getReference()
                        .child("following")             //database following
                        .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                        .child(tUser.getUser_id())
                        .removeValue();


                FirebaseDatabase.getInstance().getReference()
                        .child("followers")
                        .child(tUser.getUser_id())
                        .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                        .removeValue();



//update follower and following number in user_account_settings
                FirebaseDatabase.getInstance().getReference()
                        .child("user_account_settings")
                        .child(tUser.getUser_id())
                        .child("followers")
                        .setValue(tFollowerNum);

                FirebaseDatabase.getInstance().getReference()
                        .child("user_account_settings")
                        .child(cUID)
                        .child("following")
                        .setValue(cFollowingNum);
                System.out.println("unfollow after er/ing: " + tFollowerNum + "; " + cFollowingNum);
                setUnFollow();
                tFollowerNum  = getTfollowerNum();
                cFollowingNum = getCfollowingNum();

            }
        });
        init();


    }


    private void setFollow() {
        follow.setVisibility(View.GONE);
        unfollow.setVisibility(View.VISIBLE);
    }

    private void setUnFollow() {
        follow.setVisibility(View.VISIBLE);
        unfollow.setVisibility(View.GONE);
    }

    private void checkFollowing() {
        setUnFollow();
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
//        check current user is following if target user
        Query query = reference.child(getString( R.string.dbname_following))
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .orderByChild("user_id").equalTo(tUser.getUser_id());
// if following
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for(DataSnapshot singleSnapshot :  dataSnapshot.getChildren()){
//                    Log.d(TAG, "onDataChange: found user:" + singleSnapshot.getValue(User.class).toString());
                    setFollow();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }




    private void init() {

//find target user
        DatabaseReference _reference = FirebaseDatabase.getInstance().getReference();
        Query _query = _reference.child("user_account_settings")
                .orderByChild("user_id").equalTo(tUser.getUser_id());
        _query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for(DataSnapshot ds :  dataSnapshot.getChildren()){

                    Log.d(TAG, "onDataChange: found user:" + ds.getValue(UserAccountSetting.class).toString());
                    tUserAccountSettings = ds.getValue(UserAccountSetting.class);
                    setProfile(tUserAccountSettings);
                    tFollowerNum  = getTfollowerNum();

                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        Query query = _reference.child("user_account_settings")
                .orderByChild("user_id").equalTo(FirebaseAuth.getInstance().getCurrentUser().getUid());
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for(DataSnapshot ds :  dataSnapshot.getChildren()){

                    Log.d(TAG, "onDataChange: found user:" + ds.getValue(UserAccountSetting.class).toString());
                    cUserAccountSettings = ds.getValue(UserAccountSetting.class);
                    cFollowingNum  = cUserAccountSettings.getFollowing();

                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });



    }




    private long getTfollowerNum() {

//find target user
        DatabaseReference _reference = FirebaseDatabase.getInstance().getReference();
        Query _query = _reference.child("user_account_settings")
                .orderByChild("user_id").equalTo(tUser.getUser_id());
        _query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for(DataSnapshot ds :  dataSnapshot.getChildren()){

                    Log.d(TAG, "onDataChange: found user:" + ds.getValue(UserAccountSetting.class).toString());
                    tUserAccountSettings= ds.getValue(UserAccountSetting.class);
                    tFollowerNum  = tUserAccountSettings.getFollowers();
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {

            }

        });
    return tFollowerNum;
    }





//    get following number of the current user
    private long getCfollowingNum() {

        DatabaseReference _reference = FirebaseDatabase.getInstance().getReference();
        Query query = _reference.child("user_account_settings")
                .orderByChild("user_id").equalTo(cUID);
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for(DataSnapshot ds :  dataSnapshot.getChildren()){

                    Log.d(TAG, "onDataChange: found user:" + ds.getValue(UserAccountSetting.class).toString());
                    cUserAccountSettings = ds.getValue(UserAccountSetting.class);
                    cFollowingNum  = cUserAccountSettings.getFollowing();
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        return cFollowingNum;
    }



    //    set up bottom view
    private void setBottom() {
        Log.d(TAG, "bottom view setting");

        BottomNavTool.setBottomNav(bottomNavigationViewEx);
        BottomNavTool.enableNav(context, this, bottomNavigationViewEx);
        Menu menu = bottomNavigationViewEx.getMenu();
        MenuItem menuItem = menu.getItem(4);
        menuItem.setChecked(true);
    }


    private void setProfile(UserAccountSetting userAccountSetting) {

        UniversalImageLoader.setImage(userAccountSetting.getProfile_pic(), profile_pic, null, "");
        displayName.setText(userAccountSetting.getDisplay_name());
        username.setText(userAccountSetting.getUsername());
        description.setText(userAccountSetting.getDescription());
        posts.setText(String.valueOf(userAccountSetting.getPosts()));
        following.setText(String.valueOf(userAccountSetting.getFollowing()));
        followers.setText(String.valueOf(userAccountSetting.getFollowers()));

        left_icon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick: navigating back");
                finish();
            }
        });

    }



    /**
     * Setup the firebase auth object
     */
    private void FirebaseAuth() {
        Log.d(TAG, "setupFirebaseAuth: setting up firebase auth.");

        auth = FirebaseAuth.getInstance();
        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference();

        authListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                cUser = firebaseAuth.getCurrentUser();
                cUID = cUser.getUid();

                if (cUser != null) {
                    Log.d(TAG, "onAuthStateChanged:signed_in:" + cUser.getUid());
                } else {
                    Log.d(TAG, "onAuthStateChanged:signed_out");
                    Intent intent = new Intent(context,LoginActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                }
                // ...
            }
        };

    }

    @Override
    public void onStart() {
        super.onStart();
        auth.addAuthStateListener(authListener);
    }

    @Override
    public void onStop() {
        super.onStop();
        if (authListener != null) {
            auth.removeAuthStateListener(authListener);
        }
    }


    private void setupActivityWidgets() {
        profile_pic = (CircleImageView)findViewById( R.id.profile_pic);
    }

    private void setImageGrid(ArrayList<String> imgURLs) {

        int screenWidth = getResources().getDisplayMetrics().widthPixels;
        int imgWidth = screenWidth / numColumns;
        imgGrid.setColumnWidth(imgWidth);

        ImageAdapter imageAdapter = new ImageAdapter(context, R.layout.image_grid, "", imgURLs);
        imgGrid.setAdapter(imageAdapter);
    }


    private void temGridSetup() {
        ArrayList<String> imgURLs = new ArrayList<>();

        imgURLs.add("https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcSz1WudxjK_akg8ZwryyxpzLzDNodquERTqGmPFqFNRcu5pNA-EVw");
        imgURLs.add("https://frontiersinblog.files.wordpress.com/2018/02/psychology-influence-behavior-with-images.jpg?w=940");
        imgURLs.add("https://secure.i.telegraph.co.uk/multimedia/archive/03290/kitten_potd_3290498k.jpg");
        imgURLs.add("https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcSz1WudxjK_akg8ZwryyxpzLzDNodquERTqGmPFqFNRcu5pNA-EVw");
        imgURLs.add("https://vignette.wikia.nocookie.net/parody/images/e/ef/Alice-PNG-alice-in-wonderland-33923432-585-800.png/revision/latest?cb=20141029225915");
        imgURLs.add("https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcSz1WudxjK_akg8ZwryyxpzLzDNodquERTqGmPFqFNRcu5pNA-EVw");

        setImageGrid(imgURLs);
    }


    private User getUser(){
        Intent intent = getIntent();
        if (intent!= null)
        {
            Log.d("View profile get user","found user： ");
            return intent.getExtras().getParcelable(  "intent_user");
        }
        else
        {
            return null;
        }
    }

}








