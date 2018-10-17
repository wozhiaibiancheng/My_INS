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

import au.edu.unimelb.student.group55.my_ins.Firebase.FirebaseMethods;
import au.edu.unimelb.student.group55.my_ins.Firebase.PhotoInformation;
import au.edu.unimelb.student.group55.my_ins.Firebase.User;
import au.edu.unimelb.student.group55.my_ins.Firebase.UserAccountSetting;
import au.edu.unimelb.student.group55.my_ins.Firebase.UserRetrieving;
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

    private UserAccountSetting userAccountSetting;

    //widgets
    private TextView posts, followers, following, displayName, username, description, follow, unfollow, editProfile;
    private CircleImageView profile_pic;
    private GridView gridView;
    private Toolbar toolbar;
    private ImageView profileMenu;
    private BottomNavigationViewEx bottomNavigationView;

    private StorageReference storageReference;
  //  private FirebaseUser user;

    private UserAccountSetting userAccountSettings;


    private Task<Uri> downloadUri;
    private String downloadLink;

    private User mUser;
    private int followers_number = 0;
    private int following_number = 0;
    private int posts_number = 0;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView( R.layout.activity_view_profile);
        Log.d("INFO", "onCreate started!");

        displayName = (TextView) findViewById( R.id.display_name);
        username = (TextView) findViewById( R.id.username);
        description = (TextView) findViewById( R.id.description);
        profile_pic = (CircleImageView) findViewById( R.id.profile_pic);
        posts = (TextView) findViewById( R.id.posts);
        followers = (TextView) findViewById( R.id.followers);
        following = (TextView) findViewById( R.id.following);
//        progressBar = (ProgressBar) findViewById(R.id.profileProgressBar);
        follow = (TextView) findViewById( R.id.text_follow);
        unfollow = (TextView) findViewById( R.id.text_unfollow);
        editProfile = (TextView) findViewById( R.id.edit_profile);

        gridView = (GridView) findViewById( R.id.image_grid);
        toolbar = (Toolbar) findViewById( R.id.profileToolBar);
        profileMenu = (ImageView) findViewById( R.id.profile_menu);

        storageReference = FirebaseStorage.getInstance().getReference();
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        mUser = getUser();
        init();

        //This need to be changed later
        temGridSetup();

        setUpToolbar();
        setBottom();
        setupActivityWidgets();
        FirebaseAuth();

        followed();
        getFollowersNumber();
        getFollowingNumber();
        getPostsNumber();
//        setUpGrid();

        follow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FirebaseDatabase.getInstance().getReference()
                        .child(getString( R.string.dbname_following))
                        .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                        .child(mUser.getUser_id())
                        .child("user_id")
                        .setValue(mUser.getUser_id());


                FirebaseDatabase.getInstance().getReference()
                        .child(getString( R.string.dbname_followers))
                        .child(mUser.getUser_id())
                        .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                        .child("user_id")
                        .setValue(FirebaseAuth.getInstance().getCurrentUser().getUid());

                setFollow();
            }
        });

        unfollow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FirebaseDatabase.getInstance().getReference()
                        .child(getString( R.string.dbname_following))             //database following
                        .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                        .child(mUser.getUser_id())
                        .removeValue();


                FirebaseDatabase.getInstance().getReference()
                        .child(getString( R.string.dbname_followers))
                        .child(mUser.getUser_id())
                        .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                        .removeValue();

                setUnFollow();

            }
        });

        editProfile.setOnClickListener(new View.OnClickListener(){@Override
        public void onClick(View view) {

        }

        });
    }

    private void getFollowersNumber(){
        followers_number = 0;
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
        Query query = reference.child(getString( R.string.dbname_followers))
                .child(mUser.getUser_id());
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for(DataSnapshot singleSnapshot :  dataSnapshot.getChildren()){
                    Log.d(TAG, "onDataChange: followers number:" + singleSnapshot.getValue(User.class).toString());
                    followers_number = followers_number +1;
                }
                followers.setText(String.valueOf(followers_number));
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

    private void getFollowingNumber(){
        following_number = 0;
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
        Query query = reference.child(getString( R.string.dbname_following))
                .child(mUser.getUser_id());
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for(DataSnapshot singleSnapshot :  dataSnapshot.getChildren()){
                    Log.d(TAG, "onDataChange: following number:" + singleSnapshot.getValue(User.class).toString());
                    following_number = following_number +1;
                }
                following.setText(String.valueOf(following_number));
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

    private void getPostsNumber(){
        posts_number = 0;
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
        Query query = reference.child(getString( R.string.dbname_user_photos))
                .child(mUser.getUser_id());
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for(DataSnapshot singleSnapshot :  dataSnapshot.getChildren()){
                    Log.d(TAG, "onDataChange: posts number:" + singleSnapshot.getValue(User.class).toString());
                    posts_number = posts_number +1;
                }
                posts.setText(String.valueOf(posts_number));
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

    private void setFollow() {
        follow.setVisibility(View.GONE);
        unfollow.setVisibility(View.VISIBLE);
        editProfile.setVisibility(View.GONE);
    }

    private void setUnFollow() {
        follow.setVisibility(View.VISIBLE);
        unfollow.setVisibility(View.GONE);
        editProfile.setVisibility(View.GONE);
    }

    private void followed() {
        setUnFollow();
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
        Query query = reference.child(getString( R.string.dbname_following))
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .orderByChild("user_id").equalTo(mUser.getUser_id());
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for(DataSnapshot singleSnapshot :  dataSnapshot.getChildren()){
                    Log.d(TAG, "onDataChange: found user:" + singleSnapshot.getValue(User.class).toString());
                    setFollow();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }


    private void init() {
        DatabaseReference _reference = FirebaseDatabase.getInstance().getReference();
        Query _query = _reference.child("users")
                .orderByChild("username").equalTo(mUser.getUsername());
        _query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for(DataSnapshot singleSnapshot :  dataSnapshot.getChildren()){
                    Log.d(TAG, "onDataChange: found user:" + singleSnapshot.getValue(User.class).toString());

                    UserRetrieving set = new UserRetrieving();
                    set.setUser(mUser);
                    set.setSettings(singleSnapshot.getValue(UserAccountSetting.class));
                    setProfile(set.getSettings());
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

//        DatabaseReference reference_ = FirebaseDatabase.getInstance().getReference();
//        Query query_ = reference_
//                .child("user_photos")
//                .child(mUser.getUser_id());
//        query_.addListenerForSingleValueEvent(new ValueEventListener() {
//            @Override
//            public void onDataChange(DataSnapshot dataSnapshot) {
//
//                ArrayList<Photo> photos = new ArrayList<Photo>();
//                for ( DataSnapshot singleSnapshot :  dataSnapshot.getChildren()){
//
//                    Photo photo = new Photo();
//                    Map<String, Object> objectMap = (HashMap<String, Object>) singleSnapshot.getValue();
//
//                    photo.setCaption(objectMap.get(getString(R.string.field_caption)).toString());
//                    photo.setTags(objectMap.get(getString(R.string.field_tags)).toString());
//                    photo.setPhoto_id(objectMap.get(getString(R.string.field_photo_id)).toString());
//                    photo.setUser_id(objectMap.get(getString(R.string.field_user_id)).toString());
//                    photo.setDate_created(objectMap.get(getString(R.string.field_date_created)).toString());
//                    photo.setImage_path(objectMap.get(getString(R.string.field_image_path)).toString());
//
//                    ArrayList<Comment> comments = new ArrayList<Comment>();
//                    for (DataSnapshot dSnapshot : singleSnapshot
//                            .child(getString(R.string.field_comments)).getChildren()){
//                        Comment comment = new Comment();
//                        comment.setUser_id(dSnapshot.getValue(Comment.class).getUser_id());
//                        comment.setComment(dSnapshot.getValue(Comment.class).getComment());
//                        comment.setDate_created(dSnapshot.getValue(Comment.class).getDate_created());
//                        comments.add(comment);
//                    }
//
//                    photo.setComments(comments);
//
//                    List<Like> likesList = new ArrayList<Like>();
//                    for (DataSnapshot dSnapshot : singleSnapshot
//                            .child(getString(R.string.field_likes)).getChildren()){
//                        Like like = new Like();
//                        like.setUser_id(dSnapshot.getValue(Like.class).getUser_id());
//                        likesList.add(like);
//                    }
//                    photo.setLikes(likesList);
//                    photos.add(photo);
//                }
//                setupImageGrid(photos);
//            }
//
//            @Override
//            public void onCancelled(DatabaseError databaseError) {
//                Log.d(TAG, "onCancelled: query cancelled.");
//            }
//        });

        //////////////////////////////////////////////////////////////////
    }


    //    set up toolbar
    private void setUpToolbar() {
        Toolbar toolbar = (Toolbar) findViewById( R.id.profile_toolbar);
        setSupportActionBar(toolbar);
        FirebaseAuth();
        toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                Log.d(TAG, "clicked menu icon" + item);
                switch (item.getItemId()) {
                    case R.id.profile_menu:
                        Log.d(TAG, "on menuItem click");
                        auth.signOut();
                        finish();
                }
                return false;
            }
        });
    }


    //    set up bottom view
    private void setBottom() {
        Log.d(TAG, "bottom view setting");
        BottomNavigationViewEx bottomNavigationViewEx = (BottomNavigationViewEx) findViewById( R.id.bottom);
        BottomNavTool.setBottomNav(bottomNavigationViewEx);
        BottomNavTool.enableNav(ViewProfileActivity.this, this, bottomNavigationViewEx);
        Menu menu = bottomNavigationViewEx.getMenu();
        MenuItem menuItem = menu.getItem(4);
        menuItem.setChecked(true);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate( R.menu.profile_menu, menu);
        return true;
    }


    private void setProfile(UserAccountSetting userAccountSetting) {
        UniversalImageLoader.setImage(userAccountSetting.getProfile_pic(), profile_pic, null, "");
        displayName.setText(userAccountSetting.getDisplay_name());
        username.setText(userAccountSetting.getUsername());
        description.setText(userAccountSetting.getDescription());
        posts.setText(String.valueOf(userAccountSetting.getPosts()));
        following.setText(String.valueOf(userAccountSetting.getFollowing()));
        followers.setText(String.valueOf(userAccountSetting.getFollowers()));

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
                FirebaseUser user = firebaseAuth.getCurrentUser();

                if (user != null) {
                    Log.d(TAG, "onAuthStateChanged:signed_in:" + user.getUid());
                } else {
                    Log.d(TAG, "onAuthStateChanged:signed_out");
                    Intent intent = new Intent(ViewProfileActivity.this, LoginActivity.class);
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
        profile_pic = (CircleImageView) findViewById( R.id.profile_pic);
    }

    private void setImageGrid(ArrayList<String> imgURLs) {
        GridView imgGrid = (GridView) findViewById( R.id.image_grid);

        int screenWidth = getResources().getDisplayMetrics().widthPixels;
        int imgWidth = screenWidth / numColumns;
        imgGrid.setColumnWidth(imgWidth);

        ImageAdapter imageAdapter = new ImageAdapter(context, R.layout.image_grid, "", imgURLs);
        imgGrid.setAdapter(imageAdapter);
    }

//    private void setUpGrid(){
//        Log.d(TAG, "setupGridView: Setting up image grid.");
//        final ArrayList<String> imgURLs = new ArrayList<>();
//        final ArrayList<PhotoInformation> photos = new ArrayList<>();
//        DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
//        Query query = reference
//                .child("posts")
//                .child(mUser.getUser_id());
//
//        query.addListenerForSingleValueEvent(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                for(DataSnapshot ds:dataSnapshot.getChildren()){
//                    photos.add(ds.getValue(PhotoInformation.class));
//                }
//
//                for(int i = 0; i < photos.size();i++){
//                    imgURLs.add(photos.get(i).getImageUrl());
//                }
//                setImageGrid(imgURLs);
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError databaseError) {
//                Log.d(TAG,"onCancelled");
//            }
//        });
//    }

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
            return intent.getExtras().getParcelable(  "intent_user");
        }
        else
        {
            return null;
        }
    }
    }







