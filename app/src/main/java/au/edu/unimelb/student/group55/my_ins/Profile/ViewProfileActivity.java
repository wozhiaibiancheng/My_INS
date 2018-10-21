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
import android.widget.ProgressBar;
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
import java.util.Calendar;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Map;

import au.edu.unimelb.student.group55.my_ins.Firebase.ActivityFollowing;
import au.edu.unimelb.student.group55.my_ins.Firebase.Comment;
import au.edu.unimelb.student.group55.my_ins.Firebase.FirebaseMethods;
import au.edu.unimelb.student.group55.my_ins.Firebase.PhotoInformation;
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

    private FirebaseAuth auth;
    private FirebaseAuth.AuthStateListener authListener;

    private Context context = ViewProfileActivity.this;
    private static final int numColumns = 3;

    private TextView posts, followers, following, displayName, username, description, follow, unfollow;
    private CircleImageView profile_pic;
    private BottomNavigationViewEx bottomNavigationViewEx;

    private ImageView left_icon;
    private ProgressBar progressBar;

    //target user
    private UserAccountSetting tUserAccountSettings;
    private User tUser;
    private long tFollowerNum;

    //    current user
    private FirebaseUser cUser;
    private long cFollowingNum;
    private UserAccountSetting cUserAccountSettings;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_profile);

        Log.d("INFO", "onCreate started!");


        displayName = (TextView) findViewById(R.id.display_name);
        username = (TextView) findViewById(R.id.username);
        description = (TextView) findViewById(R.id.description);
        profile_pic = (CircleImageView) findViewById(R.id.profile_pic);
        posts = (TextView) findViewById(R.id.posts);
        followers = (TextView) findViewById(R.id.followers);
        following = (TextView) findViewById(R.id.following);
        progressBar = (ProgressBar) findViewById(R.id.profileProgressBar);
        follow = (TextView) findViewById(R.id.text_follow);
        unfollow = (TextView) findViewById(R.id.text_unfollow);
        left_icon = (ImageView) findViewById(R.id.left_icon);
        profile_pic = (CircleImageView) findViewById(R.id.profile_pic);
        bottomNavigationViewEx = (BottomNavigationViewEx) findViewById(R.id.bottom);


        FirebaseAuth();
        tUser = getUser();
        checkFollowing();

        gridSetup();

        setBottom();


        follow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                cFollowingNum += 1;

                tFollowerNum += 1;

                followers.setText(Long.toString(tFollowerNum));

                System.out.println("following mid er/ing: " + tFollowerNum + "; " + cFollowingNum);

                //             update followers and following table

                FirebaseDatabase.getInstance().getReference()
                        .child("following")
                        .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                        .child(tUser.getUser_id())
                        .child("user_id")
                        .setValue(tUser.getUser_id());


                FirebaseDatabase.getInstance().getReference()
                        .child("followers")
                        .child(tUser.getUser_id())
                        .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
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
                        .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                        .child("following")
                        .setValue(cFollowingNum);

                Calendar calendar = Calendar.getInstance();
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd-HH:mm:ss");
                String currentDate = String.valueOf( simpleDateFormat.format( calendar.getTime() ) );

                ActivityFollowing activityFollowing = new ActivityFollowing(  );
                activityFollowing.setFollowID( FirebaseAuth.getInstance().getCurrentUser().getUid() );
                activityFollowing.setFollowerID( tUser.getUser_id() );
                activityFollowing.setDateToFollow( currentDate );

                FirebaseDatabase.getInstance().getReference()
                        .child( "activity" )
                        .child( "following" )
                        .child( FirebaseAuth.getInstance().getCurrentUser().getUid() )
                        .child( tUser.getUser_id() )
                        .setValue( activityFollowing );


                System.out.println("following after er/ing: " + tFollowerNum + "; " + cFollowingNum);

                setFollow();
                tFollowerNum = getTfollowerNum();
                cFollowingNum = getCfollowingNum();

//                add inner followers/following for friends suggestion
                addTuserfollowing();
                addTuserfollowers();
            }
        });

        unfollow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                cFollowingNum -= 1;
                tFollowerNum -= 1;

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
                        .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                        .child("following")
                        .setValue(cFollowingNum);

                FirebaseDatabase.getInstance().getReference()
                        .child( "activity" )
                        .child( "following" )
                        .child( FirebaseAuth.getInstance().getCurrentUser().getUid() )
                        .child( tUser.getUser_id() )
                        .removeValue();

                setUnFollow();
                tFollowerNum = getTfollowerNum();
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


        DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
//        check current user is following if target user
        Query query = reference.child("following")
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .orderByChild("user_id").equalTo(tUser.getUser_id());
// if following
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    setFollow();
                } else {
                    setUnFollow();
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
                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    tUserAccountSettings = ds.getValue(UserAccountSetting.class);
                    setProfile(tUserAccountSettings);
                    tFollowerNum = getTfollowerNum();
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
                for (DataSnapshot ds : dataSnapshot.getChildren()) {

                    Log.d(TAG, "onDataChange: found user:" + ds.getValue(UserAccountSetting.class).toString());
                    cUserAccountSettings = ds.getValue(UserAccountSetting.class);
                    cFollowingNum = cUserAccountSettings.getFollowing();

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
                for (DataSnapshot ds : dataSnapshot.getChildren()) {

                    Log.d(TAG, "onDataChange: found user:" + ds.getValue(UserAccountSetting.class).toString());
                    tUserAccountSettings = ds.getValue(UserAccountSetting.class);
                    tFollowerNum = tUserAccountSettings.getFollowers();
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
                .orderByChild("user_id").equalTo(FirebaseAuth.getInstance().getCurrentUser().getUid());
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot ds : dataSnapshot.getChildren()) {

                    Log.d(TAG, "onDataChange: found user:" + ds.getValue(UserAccountSetting.class).toString());
                    cUserAccountSettings = ds.getValue(UserAccountSetting.class);
                    cFollowingNum = cUserAccountSettings.getFollowing();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        return cFollowingNum;
    }


    //add users who are followed by the one the current user is following
//    for friend suggestion algo.
    private void addTuserfollowing() {

//find target user
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
        Query query = reference.child("following").child(tUser.getUser_id());
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                for (DataSnapshot ds : dataSnapshot.getChildren()) {
//add inner Following to suggest
                    FirebaseDatabase.getInstance().getReference()
                            .child("following")
                            .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                            .child(tUser.getUser_id())
                            .child("innerFollowing")
                            .push()
                            .setValue(ds.child("user_id").getValue());
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }

        });
    }


    //add common friends
//    for friend suggestion algo.
    private void addTuserfollowers() {

//find target user
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
        Query query = reference.child("followers").child(tUser.getUser_id());
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                for (DataSnapshot ds : dataSnapshot.getChildren()) {
//add innerFollowers to suggest
                    FirebaseDatabase.getInstance().getReference()
                            .child("following")
                            .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                            .child(tUser.getUser_id())
                            .child("innerFollowers")
                            .push()
                            .setValue(ds.child("user_id").getValue());
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }

        });
    }


    //    set up bottom view
    private void setBottom() {
        Log.d(TAG, "bottom view setting");

        BottomNavTool.setBottomNav(bottomNavigationViewEx);
        BottomNavTool.enableNav(context, this, bottomNavigationViewEx);
        Menu menu = bottomNavigationViewEx.getMenu();
        MenuItem menuItem = menu.getItem(1);
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
        progressBar.setVisibility(View.GONE);

        left_icon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick: navigating back");
                finish();
            }
        });

    }


    private void gridSetup() {
        Log.d(TAG, "setupGridView: Setting up image grid.");

        final ArrayList<String> imgURLs = new ArrayList<>();

        final ArrayList<PhotoInformation> photos = new ArrayList<>();
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
        Query query = reference
                .child("posts")
                .child(tUser.getUser_id());

        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot ds : dataSnapshot.getChildren()) {

                    PhotoInformation photoInformation = new PhotoInformation();
                    Map<String, Object> objectMap = (HashMap<String, Object>) ds.getValue();

                    photoInformation.setPostMessage(objectMap.get("postMessage").toString());
                    photoInformation.setPhotoID(objectMap.get("photoID").toString());
                    photoInformation.setUserID(objectMap.get("userID").toString());
                    photoInformation.setDateCreated(objectMap.get("dateCreated").toString());
                    photoInformation.setImageUrl(objectMap.get("imageUrl").toString());

                    ArrayList<Comment> comments = new ArrayList<Comment>();
                    for (DataSnapshot dSnapshot : ds
                            .child("comments").getChildren()){
                        Comment comment = new Comment();
                        comment.setUser_id(dSnapshot.getValue(Comment.class).getUser_id());
                        comment.setComment(dSnapshot.getValue(Comment.class).getComment());
                        comment.setDate_created(dSnapshot.getValue(Comment.class).getDate_created());
                        comments.add(comment);
                    }

                    photoInformation.setComments(comments);
                    photos.add(photoInformation);
                }

                for (int i = 0; i < photos.size(); i++) {
                    imgURLs.add(photos.get(i).getImageUrl());
                }
                setImageGrid(imgURLs);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.d(TAG, "onCancelled");
            }
        });
    }

    private void setImageGrid(ArrayList<String> imgURLs) {
        GridView imgGrid = (GridView) findViewById(R.id.image_grid);

        int screenWidth = getResources().getDisplayMetrics().widthPixels;
        int imgWidth = screenWidth / numColumns;
        imgGrid.setColumnWidth(imgWidth);


        ImageAdapter imageAdapter = new ImageAdapter(context, R.layout.image_grid, "", imgURLs);
        imgGrid.setAdapter(imageAdapter);

    }


    private User getUser() {
        Intent intent = getIntent();
        if (intent != null) {
            Log.d("View profile get user", "found user： ");
            return intent.getExtras().getParcelable("intent_user");
        } else {
            return null;
        }
    }



//    setup firebasea auth
    private void FirebaseAuth() {
        Log.d(TAG, "setupFirebaseAuth: setting up firebase auth.");

        auth = FirebaseAuth.getInstance();

        authListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                cUser = firebaseAuth.getCurrentUser();

                if (cUser != null) {
                    Log.d(TAG, "onAuthStateChanged:signed_in:" + cUser.getUid());
                } else {
                    Log.d(TAG, "onAuthStateChanged:signed_out");
                    Intent intent = new Intent(context, LoginActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                }
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



}








